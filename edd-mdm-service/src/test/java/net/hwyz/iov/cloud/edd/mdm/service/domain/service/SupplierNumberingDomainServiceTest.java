package net.hwyz.iov.cloud.edd.mdm.service.domain.service;

import net.hwyz.iov.cloud.edd.mdm.service.common.exception.MdmErrorCode;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.SupplierCodeExhaustedException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.SupplierCodeGenerationFailedException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.SupplierCode;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.SupplierRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.SupplierSeqRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * SupplierNumberingDomainService 单元测试
 * CR-036
 */
@ExtendWith(MockitoExtension.class)
class SupplierNumberingDomainServiceTest {

    @Mock
    private SupplierSeqRepository supplierSeqRepository;
    @Mock
    private SupplierRepository supplierRepository;

    private SupplierNumberingDomainService newService() {
        return new SupplierNumberingDomainService(supplierSeqRepository, supplierRepository);
    }

    @Test
    void testAllocateGeneratesFormattedCode() {
        when(supplierSeqRepository.allocateNextSeq()).thenReturn(1L);
        when(supplierRepository.existsByCode("SUP00000001")).thenReturn(false);

        SupplierCode code = newService().allocate();

        assertEquals("SUP00000001", code.code());
        assertTrue(SupplierCode.matches(code.code()));
        verify(supplierSeqRepository).allocateNextSeq();
    }

    @Test
    void testAllocateLargeSeq() {
        when(supplierSeqRepository.allocateNextSeq()).thenReturn(99_999_999L);
        when(supplierRepository.existsByCode("SUP99999999")).thenReturn(false);

        SupplierCode code = newService().allocate();

        assertEquals("SUP99999999", code.code());
    }

    @Test
    void testAllocateOverflowReturns812703() {
        when(supplierSeqRepository.allocateNextSeq()).thenReturn(100_000_000L);

        SupplierCodeExhaustedException ex = assertThrows(
                SupplierCodeExhaustedException.class, () -> newService().allocate());

        assertEquals(MdmErrorCode.SUPPLIER_CODE_EXHAUSTED.getCode(), ex.getErrorCode().getCode());
        assertEquals(100_000_000L, ex.getCurrentSeq());
    }

    @Test
    void testAllocateUkConflictRetryOnce() {
        when(supplierSeqRepository.allocateNextSeq()).thenReturn(1L, 2L);
        when(supplierRepository.existsByCode("SUP00000001")).thenReturn(true);
        when(supplierRepository.existsByCode("SUP00000002")).thenReturn(false);

        SupplierCode code = newService().allocate();

        assertEquals("SUP00000002", code.code());
        verify(supplierSeqRepository, times(2)).allocateNextSeq();
    }

    @Test
    void testAllocateUkConflictStillFailsReturns812702() {
        when(supplierSeqRepository.allocateNextSeq()).thenReturn(1L, 2L);
        when(supplierRepository.existsByCode("SUP00000001")).thenReturn(true);
        when(supplierRepository.existsByCode("SUP00000002")).thenReturn(true);

        SupplierCodeGenerationFailedException ex = assertThrows(
                SupplierCodeGenerationFailedException.class, () -> newService().allocate());

        assertEquals(MdmErrorCode.SUPPLIER_CODE_GENERATION_FAILED.getCode(), ex.getErrorCode().getCode());
        assertEquals("SUP00000002", ex.getConflictCode());
    }

    @Test
    void testAllocateRetryOverflow() {
        when(supplierSeqRepository.allocateNextSeq()).thenReturn(99_999_999L, 100_000_000L);
        when(supplierRepository.existsByCode("SUP99999999")).thenReturn(true);

        SupplierCodeExhaustedException ex = assertThrows(
                SupplierCodeExhaustedException.class, () -> newService().allocate());

        assertEquals(MdmErrorCode.SUPPLIER_CODE_EXHAUSTED.getCode(), ex.getErrorCode().getCode());
    }
}
