package net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * NodeType 枚举边界测试
 *
 * @author hwyz_leo
 */
@DisplayName("NodeType 枚举测试")
class NodeTypeTest {

    @Test
    @DisplayName("所有枚举值存在")
    void allEnumValues_exist() {
        assertEquals(11, NodeType.values().length);
        assertNotNull(NodeType.DCU);
        assertNotNull(NodeType.ECU);
        assertNotNull(NodeType.MCU);
        assertNotNull(NodeType.SENSOR);
        assertNotNull(NodeType.ACTUATOR);
        assertNotNull(NodeType.GATEWAY);
        assertNotNull(NodeType.TELEMATICS);
        assertNotNull(NodeType.HMI);
        assertNotNull(NodeType.CHARGER);
        assertNotNull(NodeType.SWITCH);
        assertNotNull(NodeType.OTHER);
    }

    @Test
    @DisplayName("valueOf 正确解析")
    void valueOf_correctParsing() {
        assertEquals(NodeType.DCU, NodeType.valueOf("DCU"));
        assertEquals(NodeType.ECU, NodeType.valueOf("ECU"));
        assertEquals(NodeType.OTHER, NodeType.valueOf("OTHER"));
    }

    @Test
    @DisplayName("valueOf 非法值抛出异常")
    void valueOf_invalidValue_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> NodeType.valueOf("INVALID"));
    }

    @Test
    @DisplayName("valueOf null 抛出异常")
    void valueOf_null_throwsException() {
        assertThrows(NullPointerException.class, () -> NodeType.valueOf(null));
    }
}
