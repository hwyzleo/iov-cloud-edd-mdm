package net.hwyz.iov.cloud.edd.mdm.service.domain.service.policy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 物料品类受控缩写注册表单元测试（CR-039 §5.1）
 *
 * @author hwyz_leo
 */
@DisplayName("MaterialCategoryAbbreviationRegistry 测试")
class MaterialCategoryAbbreviationRegistryTest {

    private final MaterialCategoryAbbreviationRegistry registry = new MaterialCategoryAbbreviationRegistry();

    @Nested
    @DisplayName("Scope 受控缩写")
    class ScopeTests {

        @Test
        @DisplayName("4 个受控 Scope 全部合法")
        void allScopesValid() {
            assertTrue(registry.isScopeAbbreviation("CMP"));
            assertTrue(registry.isScopeAbbreviation("RAW"));
            assertTrue(registry.isScopeAbbreviation("SW"));
            assertTrue(registry.isScopeAbbreviation("IND"));
        }

        @Test
        @DisplayName("未知 Scope 拒绝")
        void unknownScopeRejected() {
            assertFalse(registry.isScopeAbbreviation("XXX"));
            assertFalse(registry.isScopeAbbreviation("MAT"));
            assertFalse(registry.isScopeAbbreviation("cmp"));
            assertFalse(registry.isScopeAbbreviation(null));
        }
    }

    @Nested
    @DisplayName("Domain 受控缩写（按 Scope 分组）")
    class DomainTests {

        @Test
        @DisplayName("Component Scope 的 10 个 Domain 合法")
        void componentDomainsValid() {
            for (String d : new String[]{"BODY", "EXT", "INT", "CHS", "PWR", "EGY", "THM", "ELEC", "ADAS", "GEN"}) {
                assertTrue(registry.isDomainAbbreviation("CMP", d), "应合法: " + d);
            }
        }

        @Test
        @DisplayName("Raw/Software/Indirect Scope 的 Domain 合法")
        void otherScopeDomainsValid() {
            assertTrue(registry.isDomainAbbreviation("RAW", "MET"));
            assertTrue(registry.isDomainAbbreviation("RAW", "POLY"));
            assertTrue(registry.isDomainAbbreviation("RAW", "CHEM"));
            assertTrue(registry.isDomainAbbreviation("SW", "EMB"));
            assertTrue(registry.isDomainAbbreviation("SW", "DATA"));
            assertTrue(registry.isDomainAbbreviation("SW", "MODEL"));
            assertTrue(registry.isDomainAbbreviation("IND", "TOOL"));
            assertTrue(registry.isDomainAbbreviation("IND", "PKG"));
            assertTrue(registry.isDomainAbbreviation("IND", "CONS"));
        }

        @Test
        @DisplayName("跨 Scope 的 Domain 拒绝（如 RAW 下不允许 BODY）")
        void crossScopeDomainRejected() {
            assertFalse(registry.isDomainAbbreviation("RAW", "BODY"));
            assertFalse(registry.isDomainAbbreviation("SW", "CHS"));
            assertFalse(registry.isDomainAbbreviation("CMP", "MET"));
            assertFalse(registry.isDomainAbbreviation("IND", "EMB"));
        }

        @Test
        @DisplayName("未知 Domain 拒绝")
        void unknownDomainRejected() {
            assertFalse(registry.isDomainAbbreviation("CMP", "XYZ"));
            assertFalse(registry.isDomainAbbreviation("CMP", null));
            assertFalse(registry.isDomainAbbreviation(null, "BODY"));
        }
    }

    @Nested
    @DisplayName("L3 Family 简短词")
    class FamilyShortNameTests {

        @Test
        @DisplayName("受控 Family 词（行业公认缩写）合法")
        void approvedFamilyShortNamesValid() {
            for (String f : new String[]{"HV_BATTERY", "LV_BATTERY", "HVAC", "LIDAR", "ECU", "HARNESS",
                    "BIW", "TRACTION_BATTERY", "SUSPENSION"}) {
                assertTrue(registry.isApprovedFamilyShortName(f), "应受控: " + f);
                assertTrue(registry.isValidFamilyShortName(f), "应合法: " + f);
            }
        }

        @Test
        @DisplayName("格式合法的合理新词通过，但不在受控集")
        void wellFormedNewWordAllowedButNotApproved() {
            assertTrue(registry.isValidFamilyShortName("NEWPART"));
            assertFalse(registry.isApprovedFamilyShortName("NEWPART"));
        }

        @Test
        @DisplayName("格式非法（小写/超长/含空格）拒绝")
        void malformedFamilyRejected() {
            assertFalse(registry.isValidFamilyShortName("bodyinwhite"));
            assertFalse(registry.isValidFamilyShortName("HIGHVOLTAGEBATTERY")); // >16
            assertFalse(registry.isValidFamilyShortName("BODY IN WHITE"));
            assertFalse(registry.isValidFamilyShortName("A")); // <2
            assertFalse(registry.isValidFamilyShortName(null));
        }

        @Test
        @DisplayName("扩展标记为 X")
        void extensionMarkerConstant() {
            assertTrue(MaterialCategoryAbbreviationRegistry.EXTENSION_MARKER.equals("X"));
        }
    }
}
