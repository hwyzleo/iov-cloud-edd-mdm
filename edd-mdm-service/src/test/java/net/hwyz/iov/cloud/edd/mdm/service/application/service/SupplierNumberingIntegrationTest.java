package net.hwyz.iov.cloud.edd.mdm.service.application.service;

import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.SupplierCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.SupplierUpdateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.SupplierDto;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Supplier;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.SupplierRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Supplier code 系统发号集成测试
 * CR-036
 */
@SpringBootTest
@Transactional
class SupplierNumberingIntegrationTest {

    @Autowired
    private SupplierAppService supplierAppService;
    @Autowired
    private SupplierRepository supplierRepository;

    private SupplierCreateCmd baseCreateCmd() {
        return SupplierCreateCmd.builder()
                .name("测试供应商")
                .nameLocal("测试供应商（本地）")
                .shortName("测试")
                .supplierType("MATERIAL")
                .country("CN")
                .contactName("张三")
                .createBy("test")
                .build();
    }

    @Test
    void testCreateSupplierWithSystemNumbering() {
        SupplierDto dto = supplierAppService.createSupplier(baseCreateCmd());

        assertNotNull(dto.getId());
        assertNotNull(dto.getCode());
        assertTrue(dto.getCode().matches("^SUP[0-9]{8}$"), "系统发号 code 应匹配 SUP+8 位格式: " + dto.getCode());
        assertEquals(dto.getCode(), dto.getSourceId());
    }

    @Test
    void testCreateSupplierIgnoresInputCode() {
        SupplierCreateCmd cmd = baseCreateCmd();
        cmd.setCode("LEGACY001"); // 调用方传入 code 应被忽略

        SupplierDto first = supplierAppService.createSupplier(cmd);
        SupplierDto second = supplierAppService.createSupplier(cmd);

        assertTrue(first.getCode().matches("^SUP[0-9]{8}$"));
        assertTrue(second.getCode().matches("^SUP[0-9]{8}$"));
        assertNotEquals("LEGACY001", first.getCode());
        assertNotEquals(first.getCode(), second.getCode());
    }

    @Test
    void testCreateMultipleSuppliersUniqueCode() {
        SupplierDto a = supplierAppService.createSupplier(baseCreateCmd());
        SupplierDto b = supplierAppService.createSupplier(baseCreateCmd());
        SupplierDto c = supplierAppService.createSupplier(baseCreateCmd());

        assertTrue(a.getCode().matches("^SUP[0-9]{8}$"));
        assertTrue(b.getCode().matches("^SUP[0-9]{8}$"));
        assertTrue(c.getCode().matches("^SUP[0-9]{8}$"));
        assertNotEquals(a.getCode(), b.getCode());
        assertNotEquals(b.getCode(), c.getCode());
        assertNotEquals(a.getCode(), c.getCode());
    }

    @Test
    void testUpdateSupplierKeepsCode() {
        SupplierDto created = supplierAppService.createSupplier(baseCreateCmd());

        SupplierUpdateCmd updateCmd = SupplierUpdateCmd.builder()
                .code(created.getCode())
                .name("更新后的供应商")
                .shortName("更新")
                .supplierType("SERVICE")
                .modifyBy("test")
                .build();
        SupplierDto updated = supplierAppService.updateSupplier(updateCmd);

        assertEquals(created.getCode(), updated.getCode());
        assertEquals("更新后的供应商", updated.getName());
        assertEquals(created.getVersion() + 1, updated.getVersion());
    }

    @Test
    void testDeleteDraftDoesNotReuseCode() {
        SupplierDto first = supplierAppService.createSupplier(baseCreateCmd());
        supplierAppService.deleteSupplier(first.getCode(), "test");

        SupplierDto second = supplierAppService.createSupplier(baseCreateCmd());

        // 已删除 code 不复用：新 code 流水大于已删除 code
        assertNotEquals(first.getCode(), second.getCode());
        long firstSeq = Long.parseLong(first.getCode().substring(3));
        long secondSeq = Long.parseLong(second.getCode().substring(3));
        assertTrue(secondSeq > firstSeq, "DRAFT 物理删除后 next_seq 不回退，新 code 流水应更大");
    }

    @Test
    void testLegacyNonSupCodeCompatible() {
        // 存量非 SUP 格式 code 不受影响：可正常创建、查询、更新
        Supplier legacy = Supplier.create(
                "LEGACY001", "存量供应商", null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, "system");
        Supplier saved = supplierRepository.save(legacy, "CREATE");

        SupplierDto queried = supplierAppService.getSupplierByCode("LEGACY001");
        assertEquals("LEGACY001", queried.getCode());
        assertNotNull(saved.getId());

        SupplierUpdateCmd updateCmd = SupplierUpdateCmd.builder()
                .code("LEGACY001")
                .name("存量供应商（更新）")
                .modifyBy("test")
                .build();
        SupplierDto updated = supplierAppService.updateSupplier(updateCmd);
        assertEquals("LEGACY001", updated.getCode());

        // 系统新发号不与存量 code 冲突
        SupplierDto fresh = supplierAppService.createSupplier(baseCreateCmd());
        assertTrue(fresh.getCode().matches("^SUP[0-9]{8}$"));
    }
}
