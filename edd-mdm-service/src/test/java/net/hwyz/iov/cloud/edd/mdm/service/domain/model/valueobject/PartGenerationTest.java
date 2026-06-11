package net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PartGenerationTest {

    @Test
    void testCreateValid() {
        PartGeneration gen = new PartGeneration("AA");
        assertEquals("AA", gen.value());
    }

    @Test
    void testCreateInvalid() {
        assertThrows(IllegalArgumentException.class, () -> new PartGeneration(null));
        assertThrows(IllegalArgumentException.class, () -> new PartGeneration("A"));
        assertThrows(IllegalArgumentException.class, () -> new PartGeneration("ABC"));
        assertThrows(IllegalArgumentException.class, () -> new PartGeneration("12"));
    }

    @Test
    void testNext() {
        assertEquals("AB", new PartGeneration("AA").next().value());
        assertEquals("AC", new PartGeneration("AB").next().value());
        assertEquals("BA", new PartGeneration("AZ").next().value());
        assertEquals("BB", new PartGeneration("BA").next().value());
        assertNull(new PartGeneration("ZZ").next());
    }

    @Test
    void testIsOverflow() {
        assertFalse(new PartGeneration("AA").isOverflow());
        assertFalse(new PartGeneration("AB").isOverflow());
        assertTrue(new PartGeneration("ZZ").isOverflow());
    }

    @Test
    void testParse() {
        assertEquals("AA", PartGeneration.parse("18100020AA").value());
        assertEquals("AB", PartGeneration.parse("S1810021AB").value());
        assertEquals("ZZ", PartGeneration.parse("00000001ZZ").value());
        assertNull(PartGeneration.parse("12345678"));
        assertNull(PartGeneration.parse(null));
    }

    @Test
    void testToString() {
        assertEquals("AA", new PartGeneration("AA").toString());
    }
}
