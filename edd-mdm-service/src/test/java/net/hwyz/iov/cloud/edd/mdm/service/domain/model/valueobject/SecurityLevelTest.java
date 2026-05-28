package net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SecurityLevel 枚举边界测试
 *
 * @author hwyz_leo
 */
@DisplayName("SecurityLevel 枚举测试")
class SecurityLevelTest {

    @Test
    @DisplayName("所有枚举值存在")
    void allEnumValues_exist() {
        assertEquals(5, SecurityLevel.values().length);
        assertNotNull(SecurityLevel.QM);
        assertNotNull(SecurityLevel.CAL1);
        assertNotNull(SecurityLevel.CAL2);
        assertNotNull(SecurityLevel.CAL3);
        assertNotNull(SecurityLevel.CAL4);
    }

    @Test
    @DisplayName("valueOf 正确解析")
    void valueOf_correctParsing() {
        assertEquals(SecurityLevel.QM, SecurityLevel.valueOf("QM"));
        assertEquals(SecurityLevel.CAL4, SecurityLevel.valueOf("CAL4"));
    }

    @Test
    @DisplayName("valueOf 非法值抛出异常")
    void valueOf_invalidValue_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> SecurityLevel.valueOf("INVALID"));
    }
}
