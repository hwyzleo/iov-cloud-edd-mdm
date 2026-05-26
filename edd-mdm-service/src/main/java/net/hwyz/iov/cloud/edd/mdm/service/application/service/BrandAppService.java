package net.hwyz.iov.cloud.edd.mdm.service.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.BrandCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.BrandUpdateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.BrandQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.BrandDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.port.service.OutboxService;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Brand;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.ProductDomainService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 品牌应用服务
 *
 * @author hwyz_leo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BrandAppService {

    private final ProductDomainService productDomainService;
    private final OutboxService outboxService;

    /**
     * 创建品牌
     *
     * @param cmd 创建命令
     * @return 品牌DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public BrandDto createBrand(BrandCreateCmd cmd) {
        log.info("创建品牌: {}", cmd.getCode());

        // 调用领域服务创建品牌
        Brand brand = productDomainService.createBrand(
                cmd.getCode(),
                cmd.getName(),
                cmd.getNameLocal(),
                cmd.getDescription(),
                cmd.getLogo(),
                cmd.getCountry(),
                cmd.getFoundedYear(),
                cmd.getEffectiveFrom(),
                cmd.getEffectiveTo(),
                cmd.getCreateBy()
        );

        // 发布品牌创建事件到Outbox
        outboxService.publishBrandCreatedEvent(brand);

        return convertToDto(brand);
    }

    /**
     * 更新品牌
     *
     * @param cmd 更新命令
     * @return 品牌DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public BrandDto updateBrand(BrandUpdateCmd cmd) {
        log.info("更新品牌: {}", cmd.getCode());

        // 调用领域服务更新品牌
        Brand brand = productDomainService.updateBrand(
                cmd.getCode(),
                cmd.getName(),
                cmd.getNameLocal(),
                cmd.getDescription(),
                cmd.getLogo(),
                cmd.getCountry(),
                cmd.getFoundedYear(),
                cmd.getEffectiveFrom(),
                cmd.getEffectiveTo(),
                cmd.getModifyBy()
        );

        // 发布品牌更新事件到Outbox
        outboxService.publishBrandUpdatedEvent(brand);

        return convertToDto(brand);
    }

    /**
     * 失效品牌
     *
     * @param code     品牌code
     * @param modifyBy 修改人
     * @return 品牌DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public BrandDto deactivateBrand(String code, String modifyBy) {
        log.info("失效品牌: {}", code);

        // 调用领域服务失效品牌
        Brand brand = productDomainService.deactivateBrand(code, modifyBy);

        // 发布品牌失效事件到Outbox
        outboxService.publishBrandDeactivatedEvent(brand);

        return convertToDto(brand);
    }

    /**
     * 删除品牌
     *
     * @param code     品牌code
     * @param modifyBy 修改人
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteBrand(String code, String modifyBy) {
        log.info("删除品牌: {}", code);
        productDomainService.deleteBrand(code, modifyBy);
    }

    /**
     * 根据code获取品牌
     *
     * @param code 品牌code
     * @return 品牌DTO
     */
    public BrandDto getBrandByCode(String code) {
        Brand brand = productDomainService.getBrandByCode(code);
        return convertToDto(brand);
    }

    /**
     * 分页查询品牌列表
     *
     * @param query 查询条件
     * @return 品牌DTO列表
     */
    public List<BrandDto> listBrands(BrandQuery query) {
        List<Brand> brands = productDomainService.listBrands(
                query.getPage(),
                query.getSize(),
                query.isIncludeInactive()
        );
        return brands.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 查询品牌总数
     *
     * @param includeInactive 是否包含失效记录
     * @return 总数
     */
    public long countBrands(boolean includeInactive) {
        return productDomainService.countBrands(includeInactive);
    }

    /**
     * 转换为DTO
     *
     * @param brand 品牌聚合根
     * @return 品牌DTO
     */
    private BrandDto convertToDto(Brand brand) {
        return BrandDto.builder()
                .id(brand.getId())
                .code(brand.getCode())
                .name(brand.getName())
                .nameLocal(brand.getNameLocal())
                .description(brand.getDescription())
                .logo(brand.getLogo())
                .country(brand.getCountry())
                .foundedYear(brand.getFoundedYear())
                .version(brand.getVersion())
                .effectiveFrom(brand.getEffectiveFrom())
                .effectiveTo(brand.getEffectiveTo())
                .status(brand.getStatus().name())
                .createBy(brand.getCreateBy())
                .createTime(brand.getCreateTime())
                .modifyBy(brand.getModifyBy())
                .modifyTime(brand.getModifyTime())
                .build();
    }
}
