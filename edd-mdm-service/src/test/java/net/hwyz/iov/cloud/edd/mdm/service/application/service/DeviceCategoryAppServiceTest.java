package net.hwyz.iov.cloud.edd.mdm.service.application.service;

import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.DeviceCategoryCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.DeviceCategoryUpdateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.DeviceCategoryDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.port.service.OutboxService;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.DeviceCategoryDuplicateCodeException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.DeviceCategoryHasReferenceException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.DeviceCategoryNotExistException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.DeviceCategory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.DeviceCategoryStatus;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.DeviceCategoryRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.VehicleNodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 设备类别应用服务单元测试
 *
 * @author hwyz_leo
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DeviceCategoryAppService 测试")
class DeviceCategoryAppServiceTest {

    @Mock
    private DeviceCategoryRepository deviceCategoryRepository;
    @Mock
    private VehicleNodeRepository vehicleNodeRepository;
    @Mock
    private OutboxService outboxService;

    private DeviceCategoryAppService deviceCategoryAppService;

    @BeforeEach
    void setUp() {
        deviceCategoryAppService = new DeviceCategoryAppService(
                deviceCategoryRepository,
                vehicleNodeRepository,
                outboxService
        );
    }

    @Nested
    @DisplayName("createDeviceCategory 测试")
    class CreateDeviceCategoryTests {

        @Test
        @DisplayName("创建成功 - 验证 repository.save 和 outbox.publish 被调用")
        void create_success() {
            DeviceCategoryCreateCmd cmd = DeviceCategoryCreateCmd.builder()
                    .code("DC001").name("摄像头").nameLocal("Camera")
                    .description("摄像头设备类别").sortOrder(1)
                    .createBy("admin").build();

            DeviceCategory savedCategory = DeviceCategory.create(
                    "DC001", "摄像头", "Camera", "摄像头设备类别", 1,
                    null, null, "admin"
            );

            when(deviceCategoryRepository.existsByCode("DC001")).thenReturn(false);
            when(deviceCategoryRepository.save(any(DeviceCategory.class), eq("CREATE"))).thenReturn(savedCategory);

            DeviceCategoryDto result = deviceCategoryAppService.createDeviceCategory(cmd);

            assertNotNull(result);
            assertEquals("DC001", result.getCode());
            verify(deviceCategoryRepository).existsByCode("DC001");
            verify(deviceCategoryRepository).save(any(DeviceCategory.class), eq("CREATE"));
            verify(outboxService).publishDeviceCategoryCreatedEvent(any(DeviceCategory.class));
        }

        @Test
        @DisplayName("创建失败 - code 重复抛 DeviceCategoryDuplicateCodeException")
        void create_duplicateCode_throwsException() {
            DeviceCategoryCreateCmd cmd = DeviceCategoryCreateCmd.builder()
                    .code("DC001").name("摄像头").createBy("admin").build();

            when(deviceCategoryRepository.existsByCode("DC001")).thenReturn(true);

            assertThrows(DeviceCategoryDuplicateCodeException.class, () ->
                    deviceCategoryAppService.createDeviceCategory(cmd)
            );

            verify(deviceCategoryRepository).existsByCode("DC001");
            verify(deviceCategoryRepository, never()).save(any(), anyString());
        }
    }

    @Nested
    @DisplayName("updateDeviceCategory 测试")
    class UpdateDeviceCategoryTests {

        @Test
        @DisplayName("更新成功")
        void update_success() {
            DeviceCategoryUpdateCmd cmd = DeviceCategoryUpdateCmd.builder()
                    .code("DC001").name("新名称").nameLocal("New Name")
                    .description("新描述").modifyBy("modifier").build();

            DeviceCategory existingCategory = createTestCategory();
            DeviceCategory updatedCategory = DeviceCategory.create(
                    "DC001", "新名称", "New Name", "新描述", 1,
                    null, null, "admin"
            );

            when(deviceCategoryRepository.findByCode("DC001")).thenReturn(Optional.of(existingCategory));
            when(deviceCategoryRepository.save(any(DeviceCategory.class), eq("UPDATE"))).thenReturn(updatedCategory);

            DeviceCategoryDto result = deviceCategoryAppService.updateDeviceCategory(cmd);

            assertNotNull(result);
            assertEquals("DC001", result.getCode());
            verify(deviceCategoryRepository).findByCode("DC001");
            verify(deviceCategoryRepository).save(any(DeviceCategory.class), eq("UPDATE"));
            verify(outboxService).publishDeviceCategoryUpdatedEvent(any(DeviceCategory.class));
        }

        @Test
        @DisplayName("更新失败 - 不存在抛 DeviceCategoryNotExistException")
        void update_notExist_throwsException() {
            DeviceCategoryUpdateCmd cmd = DeviceCategoryUpdateCmd.builder()
                    .code("DC999").name("新名称").modifyBy("modifier").build();

            when(deviceCategoryRepository.findByCode("DC999")).thenReturn(Optional.empty());

            assertThrows(DeviceCategoryNotExistException.class, () ->
                    deviceCategoryAppService.updateDeviceCategory(cmd)
            );

            verify(deviceCategoryRepository).findByCode("DC999");
            verify(deviceCategoryRepository, never()).save(any(), anyString());
        }
    }

    @Nested
    @DisplayName("deleteDeviceCategory 测试")
    class DeleteDeviceCategoryTests {

        @Test
        @DisplayName("删除成功 - 无引用")
        void delete_success_noReference() {
            DeviceCategory existingCategory = createTestCategory();

            when(deviceCategoryRepository.findByCode("DC001")).thenReturn(Optional.of(existingCategory));
            when(vehicleNodeRepository.countByDeviceCategory("DC001")).thenReturn(0L);

            deviceCategoryAppService.deleteDeviceCategory("DC001", "admin");

            verify(deviceCategoryRepository).findByCode("DC001");
            verify(vehicleNodeRepository).countByDeviceCategory("DC001");
            verify(deviceCategoryRepository).delete(any(DeviceCategory.class), eq("admin"));
            verify(outboxService).publishDeviceCategoryDeletedEvent(any(DeviceCategory.class), eq(false));
        }

        @Test
        @DisplayName("删除失败 - 有引用抛 DeviceCategoryHasReferenceException")
        void delete_hasReference_throwsException() {
            DeviceCategory existingCategory = createTestCategory();

            when(deviceCategoryRepository.findByCode("DC001")).thenReturn(Optional.of(existingCategory));
            when(vehicleNodeRepository.countByDeviceCategory("DC001")).thenReturn(3L);

            assertThrows(DeviceCategoryHasReferenceException.class, () ->
                    deviceCategoryAppService.deleteDeviceCategory("DC001", "admin")
            );

            verify(deviceCategoryRepository).findByCode("DC001");
            verify(vehicleNodeRepository).countByDeviceCategory("DC001");
            verify(deviceCategoryRepository, never()).delete(any(), anyString());
        }
    }

    @Nested
    @DisplayName("deactivateDeviceCategory 测试")
    class DeactivateDeviceCategoryTests {

        @Test
        @DisplayName("失效成功")
        void deactivate_success() {
            DeviceCategory existingCategory = createTestCategory();
            DeviceCategory deactivatedCategory = createTestCategory();
            deactivatedCategory.deactivate("admin");

            when(deviceCategoryRepository.findByCode("DC001")).thenReturn(Optional.of(existingCategory));
            when(deviceCategoryRepository.save(any(DeviceCategory.class), eq("DEACTIVATE"))).thenReturn(deactivatedCategory);

            DeviceCategoryDto result = deviceCategoryAppService.deactivateDeviceCategory("DC001", "admin");

            assertNotNull(result);
            verify(deviceCategoryRepository).findByCode("DC001");
            verify(deviceCategoryRepository).save(any(DeviceCategory.class), eq("DEACTIVATE"));
            verify(outboxService).publishDeviceCategoryUpdatedEvent(any(DeviceCategory.class));
        }
    }

    private DeviceCategory createTestCategory() {
        return DeviceCategory.create(
                "DC001", "摄像头", "Camera", "摄像头设备类别", 1,
                null, null, "admin"
        );
    }
}
