package net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * HsmCapability 枚举边界测试
 *
 * @author hwyz_leo
 */
@DisplayName("HsmCapability 枚举测试")
class HsmCapabilityTest {

    @Test
    @DisplayName("所有枚举值存在")
    void allEnumValues_exist() {
        assertEquals(4, HsmCapability.values().length);
        assertNotNull(HsmCapability.NONE);
        assertNotNull(HsmCapability.SHE);
        assertNotNull(HsmCapability.HSM_LIGHT);
        assertNotNull(HsmCapability.HSM_FULL);
    }

    @Test
    @DisplayName("valueOf 正确解析")
    void valueOf_correctParsing() {
        assertEquals(HsmCapability.NONE, HsmCapability.valueOf("NONE"));
        assertEquals(HsmCapability.HSM_FULL, HsmCapability.valueOf("HSM_FULL"));
    }

    @Test
    @DisplayName("valueOf 非法值抛出异常")
    void valueOf_invalidValue_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> HsmCapability.valueOf("INVALID"));
    }
}
