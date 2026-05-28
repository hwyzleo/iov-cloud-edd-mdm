package net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * OtaSupportType 枚举边界测试
 *
 * @author hwyz_leo
 */
@DisplayName("OtaSupportType 枚举测试")
class OtaSupportTypeTest {

    @Test
    @DisplayName("所有枚举值存在")
    void allEnumValues_exist() {
        assertEquals(4, OtaSupportType.values().length);
        assertNotNull(OtaSupportType.FOTA);
        assertNotNull(OtaSupportType.SOTA);
        assertNotNull(OtaSupportType.BOTH);
        assertNotNull(OtaSupportType.NOT_SUPPORTED);
    }

    @Test
    @DisplayName("valueOf 正确解析")
    void valueOf_correctParsing() {
        assertEquals(OtaSupportType.FOTA, OtaSupportType.valueOf("FOTA"));
        assertEquals(OtaSupportType.NOT_SUPPORTED, OtaSupportType.valueOf("NOT_SUPPORTED"));
    }

    @Test
    @DisplayName("valueOf 非法值抛出异常")
    void valueOf_invalidValue_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> OtaSupportType.valueOf("INVALID"));
    }
}
