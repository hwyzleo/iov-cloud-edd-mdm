package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.controller.service;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.api.service.SupplierService;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.SupplierPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.SupplierResponse;
import net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler.SupplierAssembler;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.SupplierDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.service.SupplierAppService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 供应商服务端Controller
 *
 * @author hwyz_leo
 */
@RestController
@RequestMapping("/service/supplier/v1")
@RequiredArgsConstructor
public class ServiceSupplierController implements SupplierService {

    private final SupplierAppService supplierAppService;
    private final SupplierAssembler supplierAssembler;

    @Override
    @GetMapping("/listAll")
    public SupplierPageResponse listAll(@RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "100") Integer size,
                                        @RequestParam(required = false) Boolean includeInactive) {
        boolean includeInactiveFlag = Boolean.TRUE.equals(includeInactive);

        List<SupplierDto> suppliers = supplierAppService.listSuppliers(
                net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.SupplierQuery.builder()
                        .page(page)
                        .size(size)
                        .includeInactive(includeInactiveFlag)
                        .build()
        );

        long total = supplierAppService.countSuppliers(includeInactiveFlag);

        List<SupplierResponse> rows = suppliers.stream()
                .map(supplierAssembler::toResponse)
                .collect(Collectors.toList());

        return SupplierPageResponse.builder()
                .total(total)
                .rows(rows)
                .build();
    }

    @Override
    @GetMapping("/{code}")
    public SupplierResponse getByCode(@PathVariable String code) {
        SupplierDto supplier = supplierAppService.getSupplierByCode(code);
        return supplierAssembler.toResponse(supplier);
    }
}
