package net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SupplierCode 值对象单元测试
 * CR-036
 */
class SupplierCodeTest {

    @Test
    void testGeneratePadding() {
        assertEquals("SUP00000001", SupplierCode.generate(1).code());
    }

    @Test
    void testGenerateSingleDigit() {
        assertEquals("SUP00000009", SupplierCode.generate(9).code());
    }

    @Test
    void testGenerateDoubleDigit() {
        assertEquals("SUP00000010", SupplierCode.generate(10).code());
    }

    @Test
    void testGenerateLargeSeq() {
        assertEquals("SUP99999999", SupplierCode.generate(99_999_999L).code());
    }

    @Test
    void testGenerateOverflow() {
        assertThrows(IllegalArgumentException.class, () -> SupplierCode.generate(100_000_000L));
    }

    @Test
    void testGenerateZeroInvalid() {
        assertThrows(IllegalArgumentException.class, () -> SupplierCode.generate(0L));
    }

    @Test
    void testMatches() {
        assertTrue(SupplierCode.matches("SUP00000001"));
        assertTrue(SupplierCode.matches("SUP99999999"));
        assertFalse(SupplierCode.matches("SUP0000000"));   // 7 位
        assertFalse(SupplierCode.matches("SUP000000010")); // 9 位
        assertFalse(SupplierCode.matches("SUP0000000A"));  // 非数字
        assertFalse(SupplierCode.matches("LEGACY001"));    // 存量非 SUP 格式
        assertFalse(SupplierCode.matches(null));
    }

    @Test
    void testMaxSeq() {
        assertEquals(5L, SupplierCode.maxSeq(Arrays.asList("SUP00000003", "SUP00000005", "LEGACY001", "SUP00000004")));
        assertEquals(0L, SupplierCode.maxSeq(Arrays.asList("LEGACY001", "ABC")));
        assertEquals(0L, SupplierCode.maxSeq(Collections.emptyList()));
        assertEquals(0L, SupplierCode.maxSeq(null));
    }

    @Test
    void testSeqMaxConstant() {
        assertEquals(99_999_999L, SupplierCode.getSeqMax());
    }
}
