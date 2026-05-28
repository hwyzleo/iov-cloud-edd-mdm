package net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * FunctionalDomain 枚举边界测试
 *
 * @author hwyz_leo
 */
@DisplayName("FunctionalDomain 枚举测试")
class FunctionalDomainTest {

    @Test
    @DisplayName("所有枚举值存在")
    void allEnumValues_exist() {
        assertEquals(9, FunctionalDomain.values().length);
        assertNotNull(FunctionalDomain.POWERTRAIN);
        assertNotNull(FunctionalDomain.CHASSIS);
        assertNotNull(FunctionalDomain.BODY);
        assertNotNull(FunctionalDomain.ADAS);
        assertNotNull(FunctionalDomain.COCKPIT);
        assertNotNull(FunctionalDomain.CONNECTIVITY);
        assertNotNull(FunctionalDomain.ENERGY);
        assertNotNull(FunctionalDomain.CROSS_DOMAIN);
        assertNotNull(FunctionalDomain.OTHER);
    }

    @Test
    @DisplayName("valueOf 正确解析")
    void valueOf_correctParsing() {
        assertEquals(FunctionalDomain.POWERTRAIN, FunctionalDomain.valueOf("POWERTRAIN"));
        assertEquals(FunctionalDomain.OTHER, FunctionalDomain.valueOf("OTHER"));
    }

    @Test
    @DisplayName("valueOf 非法值抛出异常")
    void valueOf_invalidValue_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> FunctionalDomain.valueOf("INVALID"));
    }
}
