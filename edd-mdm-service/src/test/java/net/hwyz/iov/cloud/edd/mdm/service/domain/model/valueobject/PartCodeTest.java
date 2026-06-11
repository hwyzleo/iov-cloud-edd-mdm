package net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PartCodeTest {

    @Test
    void testGenerateHardware() {
        PartCode pc = PartCode.generate(false, 1);
        assertEquals("00000001AA", pc.code());
        assertEquals("00000001", pc.baseNo());
        assertEquals("AA", pc.generation().value());
    }

    @Test
    void testGenerateSoftware() {
        PartCode pc = PartCode.generate(true, 1);
        assertEquals("S00000001AA", pc.code());
        assertEquals("S00000001", pc.baseNo());
        assertEquals("AA", pc.generation().value());
    }

    @Test
    void testGenerateLargeSeq() {
        PartCode pc = PartCode.generate(false, 99999999);
        assertEquals("99999999AA", pc.code());
        assertEquals("99999999", pc.baseNo());
    }

    @Test
    void testGenerateOverflow() {
        assertThrows(IllegalArgumentException.class, () -> PartCode.generate(false, 100000000));
    }

    @Test
    void testParse() {
        PartCode pc = PartCode.parse("18100020AA");
        assertEquals("18100020AA", pc.code());
        assertEquals("18100020", pc.baseNo());
        assertEquals("AA", pc.generation().value());
    }

    @Test
    void testParseSoftware() {
        PartCode pc = PartCode.parse("S1810021AB");
        assertEquals("S1810021AB", pc.code());
        assertEquals("S1810021", pc.baseNo());
        assertEquals("AB", pc.generation().value());
    }

    @Test
    void testParseInvalid() {
        assertThrows(IllegalArgumentException.class, () -> PartCode.parse(null));
        assertThrows(IllegalArgumentException.class, () -> PartCode.parse("1234567"));
        assertThrows(IllegalArgumentException.class, () -> PartCode.parse("123456789"));
    }

    @Test
    void testNextGeneration() {
        PartCode pc = PartCode.parse("18100020AA");
        PartCode next = pc.nextGeneration();
        assertEquals("18100020AB", next.code());
        assertEquals("18100020", next.baseNo());
        assertEquals("AB", next.generation().value());
    }

    @Test
    void testNextGenerationSoftware() {
        PartCode pc = PartCode.parse("S1810021AA");
        PartCode next = pc.nextGeneration();
        assertEquals("S1810021AB", next.code());
        assertEquals("S1810021", next.baseNo());
    }

    @Test
    void testNextGenerationOverflow() {
        PartCode pc = PartCode.parse("18100020ZZ");
        PartCode next = pc.nextGeneration();
        assertNull(next);
    }
}
