package net.hwyz.iov.cloud.edd.mdm.service.application.service;

import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.DeviceCategoryCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.DeviceCategoryCatalogBootstrapResult;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.DeviceCategoryCatalogPreviewResult;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.DeviceCategoryDto;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.catalog.DeviceCategoryCatalogEntry;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.config.DeviceCategoryCatalogLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 设备类别标准目录 Bootstrap 单元测试（CR-037 §6 幂等初始化）
 *
 * @author hwyz_leo
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DeviceCategoryCatalogBootstrap 测试")
class DeviceCategoryCatalogBootstrapTest {

    @Mock
    private DeviceCategoryCatalogLoader catalogLoader;
    @Mock
    private DeviceCategoryAppService deviceCategoryAppService;

    private DeviceCategoryCatalogBootstrap bootstrap;

    @BeforeEach
    void setUp() {
        bootstrap = new DeviceCategoryCatalogBootstrap(catalogLoader, deviceCategoryAppService);
        when(catalogLoader.loadVersion()).thenReturn(1);
    }

    private List<DeviceCategoryCatalogEntry> catalog() {
        return List.of(
                entry("TBOX", "Telematics Control Unit", "车载通信终端", "车载通信", 1),
                entry("CAM", "Camera", "摄像头", "摄像头", 2),
                entry("LIDAR", "LiDAR", "激光雷达", "激光雷达", 3)
        );
    }

    private DeviceCategoryCatalogEntry entry(String code, String name, String nameLocal, String description,
                                             int sortOrder) {
        return DeviceCategoryCatalogEntry.builder()
                .code(code).name(name).nameLocal(nameLocal).description(description).sortOrder(sortOrder)
                .build();
    }

    private DeviceCategoryDto dto(String code, String name, String nameLocal, String description, Integer sortOrder) {
        return DeviceCategoryDto.builder()
                .code(code).name(name).nameLocal(nameLocal).description(description).sortOrder(sortOrder)
                .build();
    }

    @Nested
    @DisplayName("首次初始化")
    class FirstImportTests {

        @Test
        @DisplayName("全部条目创建（无 Core/Conditional 分支），走 AppService 且逐条独立")
        void firstImportCreatesAll() {
            when(catalogLoader.load()).thenReturn(catalog());
            when(deviceCategoryAppService.existsDeviceCategory(any())).thenReturn(false);
            when(deviceCategoryAppService.createDeviceCategory(any()))
                    .thenAnswer(inv -> dto(inv.getArgument(0, DeviceCategoryCreateCmd.class).getCode(),
                            "x", "x", "x", 1));

            DeviceCategoryCatalogBootstrapResult result = bootstrap.bootstrap();

            assertEquals(3, result.getCreated());
            assertEquals(0, result.getSkipped());
            assertEquals(0, result.getConflicted());
            assertEquals(0, result.getFailed());
            assertEquals(1, result.getCatalogVersion());
            assertEquals(DeviceCategoryCatalogBootstrap.BOOTSTRAP_OPERATOR, result.getOperator());
            assertTrue(result.getStartedAt() != null && result.getFinishedAt() != null);
            ArgumentCaptor<DeviceCategoryCreateCmd> captor = ArgumentCaptor.forClass(DeviceCategoryCreateCmd.class);
            verify(deviceCategoryAppService, times(3)).createDeviceCategory(captor.capture());
            assertEquals("system", captor.getAllValues().get(0).getCreateBy());
            assertEquals("TBOX", captor.getAllValues().get(0).getCode());
        }
    }

    @Nested
    @DisplayName("幂等跳过")
    class SkipTests {

        @Test
        @DisplayName("已存在且完全一致则跳过，不重复创建")
        void existingMatchingSkipped() {
            when(catalogLoader.load()).thenReturn(catalog());
            when(deviceCategoryAppService.existsDeviceCategory(any())).thenReturn(true);
            when(deviceCategoryAppService.getDeviceCategoryByCode("TBOX"))
                    .thenReturn(dto("TBOX", "Telematics Control Unit", "车载通信终端", "车载通信", 1));
            when(deviceCategoryAppService.getDeviceCategoryByCode("CAM"))
                    .thenReturn(dto("CAM", "Camera", "摄像头", "摄像头", 2));
            when(deviceCategoryAppService.getDeviceCategoryByCode("LIDAR"))
                    .thenReturn(dto("LIDAR", "LiDAR", "激光雷达", "激光雷达", 3));

            DeviceCategoryCatalogBootstrapResult result = bootstrap.bootstrap();

            assertEquals(3, result.getSkipped());
            assertEquals(0, result.getCreated());
            verify(deviceCategoryAppService, never()).createDeviceCategory(any());
        }
    }

    @Nested
    @DisplayName("冲突不覆盖")
    class ConflictTests {

        @Test
        @DisplayName("已存在但名称/语义不一致则记录冲突并跳过，不覆盖业务数据")
        void existingConflictNotOverwritten() {
            when(catalogLoader.load()).thenReturn(catalog());
            when(deviceCategoryAppService.existsDeviceCategory(any())).thenReturn(true);
            when(deviceCategoryAppService.getDeviceCategoryByCode("TBOX"))
                    .thenReturn(dto("TBOX", "不同名称", "不同中文名", "不同描述", 9));
            when(deviceCategoryAppService.getDeviceCategoryByCode("CAM"))
                    .thenReturn(dto("CAM", "Camera", "摄像头", "摄像头", 2));
            when(deviceCategoryAppService.getDeviceCategoryByCode("LIDAR"))
                    .thenReturn(dto("LIDAR", "LiDAR", "激光雷达", "激光雷达", 3));

            DeviceCategoryCatalogBootstrapResult result = bootstrap.bootstrap();

            assertEquals(1, result.getConflicted());
            assertEquals(2, result.getSkipped());
            assertEquals(0, result.getCreated());
            verify(deviceCategoryAppService, never()).createDeviceCategory(any());
            assertTrue(result.getDetails().stream().anyMatch(d -> d.startsWith("TBOX")));
        }
    }

    @Nested
    @DisplayName("单条失败不回滚")
    class FailureTests {

        @Test
        @DisplayName("单条创建失败记录 failed，其余仍成功")
        void singleFailureDoesNotRollbackOthers() {
            when(catalogLoader.load()).thenReturn(catalog());
            when(deviceCategoryAppService.existsDeviceCategory(any())).thenReturn(false);
            when(deviceCategoryAppService.createDeviceCategory(any()))
                    .thenAnswer(inv -> dto(inv.getArgument(0, DeviceCategoryCreateCmd.class).getCode(),
                            "x", "x", "x", 1));
            doThrow(new RuntimeException("DB down"))
                    .when(deviceCategoryAppService).createDeviceCategory(argThat(cmd -> "CAM".equals(cmd.getCode())));

            DeviceCategoryCatalogBootstrapResult result = bootstrap.bootstrap();

            assertEquals(2, result.getCreated());
            assertEquals(1, result.getFailed());
            assertTrue(result.getDetails().stream().anyMatch(d -> d.startsWith("CAM")));
        }
    }

    @Nested
    @DisplayName("预检 preview")
    class PreviewTests {

        @Test
        @DisplayName("统计 missing/initialized/conflict")
        void previewCounts() {
            when(catalogLoader.load()).thenReturn(catalog());
            when(deviceCategoryAppService.existsDeviceCategory("TBOX")).thenReturn(true);
            when(deviceCategoryAppService.existsDeviceCategory("CAM")).thenReturn(true);
            when(deviceCategoryAppService.existsDeviceCategory("LIDAR")).thenReturn(false);
            when(deviceCategoryAppService.getDeviceCategoryByCode("TBOX"))
                    .thenReturn(dto("TBOX", "Telematics Control Unit", "车载通信终端", "车载通信", 1));
            when(deviceCategoryAppService.getDeviceCategoryByCode("CAM"))
                    .thenReturn(dto("CAM", "Changed", "改变", "摄像头", 2));

            DeviceCategoryCatalogPreviewResult preview = bootstrap.preview();

            assertEquals(1, preview.getCatalogVersion());
            assertEquals(3, preview.getStandardFamilyCount());
            assertEquals(1, preview.getInitialized());
            assertEquals(1, preview.getMissing());
            assertEquals(1, preview.getConflicted());
            assertTrue(preview.getConflicts().stream().anyMatch(c -> c.startsWith("CAM")));
        }
    }
}
