package net.hwyz.iov.cloud.edd.mdm.service.domain.service;

import net.hwyz.iov.cloud.edd.mdm.service.domain.exception.BrandNotFoundException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.exception.DuplicateCodeException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.exception.InvalidEffectiveDateException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Brand;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.CarLine;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Platform;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.BrandHistory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.CarLineHistory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.PlatformHistory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.BrandRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.CarLineRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.PlatformRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 产品领域服务
 *
 * @author hwyz_leo
 */
@Service
@RequiredArgsConstructor
public class ProductDomainService {

    private final BrandRepository brandRepository;
    private final CarLineRepository carLineRepository;
    private final PlatformRepository platformRepository;

    /**
     * 创建品牌
     *
     * @param code        业务主键
     * @param name        官方名称
     * @param nameLocal   本地化名称
     * @param description 品牌描述
     * @param logo        Logo URL
     * @param country     国家
     * @param foundedYear 创立年份
     * @param effectiveFrom 生效开始时间
     * @param effectiveTo   生效结束时间
     * @param createBy    创建人
     * @return 品牌聚合根
     * @throws DuplicateCodeException      code已存在
     * @throws InvalidEffectiveDateException 生效期无效
     */
    public Brand createBrand(String code, String name, String nameLocal, String description,
                             String logo, String country, Integer foundedYear,
                             Date effectiveFrom, Date effectiveTo, String createBy) {
        // 检查code唯一性
        if (brandRepository.existsByCode(code)) {
            throw new DuplicateCodeException("品牌code已存在: " + code);
        }

        // 创建品牌
        Brand brand = Brand.create(code, name, nameLocal, description, logo, country,
                foundedYear, effectiveFrom, effectiveTo, createBy);

        // 保存品牌
        return brandRepository.save(brand);
    }

    /**
     * 更新品牌
     *
     * @param code        业务主键
     * @param name        官方名称
     * @param nameLocal   本地化名称
     * @param description 品牌描述
     * @param logo        Logo URL
     * @param country     国家
     * @param foundedYear 创立年份
     * @param effectiveFrom 生效开始时间
     * @param effectiveTo   生效结束时间
     * @param modifyBy    修改人
     * @return 品牌聚合根
     * @throws BrandNotFoundException        品牌不存在
     * @throws InvalidEffectiveDateException 生效期无效
     */
    public Brand updateBrand(String code, String name, String nameLocal, String description,
                             String logo, String country, Integer foundedYear,
                             Date effectiveFrom, Date effectiveTo, String modifyBy) {
        // 查找品牌
        Brand brand = brandRepository.findByCode(code)
                .orElseThrow(() -> new BrandNotFoundException("品牌不存在: " + code));

        // 更新品牌
        brand.update(name, nameLocal, description, logo, country, foundedYear,
                effectiveFrom, effectiveTo, modifyBy);

        // 保存品牌
        return brandRepository.save(brand);
    }

    /**
     * 失效品牌
     *
     * @param code     业务主键
     * @param modifyBy 修改人
     * @return 品牌聚合根
     * @throws BrandNotFoundException 品牌不存在
     */
    public Brand deactivateBrand(String code, String modifyBy) {
        // 查找品牌
        Brand brand = brandRepository.findByCode(code)
                .orElseThrow(() -> new BrandNotFoundException("品牌不存在: " + code));

        // 失效品牌
        brand.deactivate(modifyBy);

        // 保存品牌
        return brandRepository.save(brand);
    }

    /**
     * 删除品牌
     *
     * @param code     业务主键
     * @param modifyBy 修改人
     * @throws BrandNotFoundException 品牌不存在
     */
    public void deleteBrand(String code, String modifyBy) {
        // 查找品牌
        Brand brand = brandRepository.findByCode(code)
                .orElseThrow(() -> new BrandNotFoundException("品牌不存在: " + code));

        // 删除品牌
        brand.delete(modifyBy);

        // 保存品牌
        brandRepository.save(brand);
    }

    /**
     * 根据code获取品牌
     *
     * @param code 业务主键
     * @return 品牌聚合根
     * @throws BrandNotFoundException 品牌不存在
     */
    public Brand getBrandByCode(String code) {
        return brandRepository.findByCode(code)
                .orElseThrow(() -> new BrandNotFoundException("品牌不存在: " + code));
    }

    /**
     * 分页查询品牌列表
     *
     * @param page            页码
     * @param size            每页大小
     * @param includeInactive 是否包含失效记录
     * @return 品牌列表
     */
    public List<Brand> listBrands(int page, int size, boolean includeInactive) {
        return brandRepository.findAll(page, size, includeInactive);
    }

    /**
     * 查询品牌总数
     *
     * @param includeInactive 是否包含失效记录
     * @return 总数
     */
    public long countBrands(boolean includeInactive) {
        return brandRepository.count(includeInactive);
    }

    /**
     * 创建车系
     *
     * @param code            业务主键
     * @param name            官方名称
     * @param nameLocal       本地化名称
     * @param brandCode       品牌code
     * @param carLineType      车系类型
     * @param lifecycleStatus 生命周期状态
     * @param targetMarket    目标市场
     * @param effectiveFrom   生效开始时间
     * @param effectiveTo     生效结束时间
     * @param createBy        创建人
     * @return 车系聚合根
     * @throws DuplicateCodeException      code已存在
     * @throws InvalidEffectiveDateException 生效期无效
     */
    public CarLine createCarLine(String code, String name, String nameLocal, String brandCode,
                               String carLineType, String lifecycleStatus, String targetMarket,
                               Date effectiveFrom, Date effectiveTo, String createBy) {
        // 检查code唯一性
        if (carLineRepository.existsByCode(code)) {
            throw new DuplicateCodeException("车系code已存在: " + code);
        }

        // 检查品牌是否存在且状态为ACTIVE
        Brand brand = brandRepository.findByCode(brandCode)
                .orElseThrow(() -> new IllegalArgumentException("品牌不存在: " + brandCode));
        if (brand.getStatus() != net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.BrandStatus.ACTIVE) {
            throw new IllegalArgumentException("品牌状态不是ACTIVE: " + brandCode);
        }

        // 创建车系
        CarLine carLine = CarLine.create(code, name, nameLocal, brandCode,
                net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.CarLineType.valueOf(carLineType),
                net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.LifecycleStatus.valueOf(lifecycleStatus),
                net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.TargetMarket.valueOf(targetMarket),
                effectiveFrom, effectiveTo, createBy);

        // 保存车系
        return carLineRepository.save(carLine);
    }

    /**
     * 更新车系
     *
     * @param code            业务主键
     * @param name            官方名称
     * @param nameLocal       本地化名称
     * @param carLineType      车系类型
     * @param lifecycleStatus 生命周期状态
     * @param targetMarket    目标市场
     * @param effectiveFrom   生效开始时间
     * @param effectiveTo     生效结束时间
     * @param modifyBy        修改人
     * @return 车系聚合根
     * @throws BrandNotFoundException        车系不存在
     * @throws InvalidEffectiveDateException 生效期无效
     */
    public CarLine updateCarLine(String code, String name, String nameLocal,
                               String carLineType, String lifecycleStatus, String targetMarket,
                               Date effectiveFrom, Date effectiveTo, String modifyBy) {
        // 查找车系
        CarLine carLine = carLineRepository.findByCode(code)
                .orElseThrow(() -> new BrandNotFoundException("车系不存在: " + code));

        // 更新车系
        carLine.update(name, nameLocal,
                net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.CarLineType.valueOf(carLineType),
                net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.LifecycleStatus.valueOf(lifecycleStatus),
                net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.TargetMarket.valueOf(targetMarket),
                effectiveFrom, effectiveTo, modifyBy);

        // 保存车系
        return carLineRepository.save(carLine);
    }

    /**
     * 失效车系
     *
     * @param code     业务主键
     * @param modifyBy 修改人
     * @return 车系聚合根
     * @throws BrandNotFoundException 车系不存在
     */
    public CarLine deactivateCarLine(String code, String modifyBy) {
        // 查找车系
        CarLine carLine = carLineRepository.findByCode(code)
                .orElseThrow(() -> new BrandNotFoundException("车系不存在: " + code));

        // 失效车系
        carLine.deactivate(modifyBy);

        // 保存车系
        return carLineRepository.save(carLine);
    }

    /**
     * 删除车系
     *
     * @param code     业务主键
     * @param modifyBy 修改人
     * @throws BrandNotFoundException 车系不存在
     */
    public void deleteCarLine(String code, String modifyBy) {
        // 查找车系
        CarLine carLine = carLineRepository.findByCode(code)
                .orElseThrow(() -> new BrandNotFoundException("车系不存在: " + code));

        // 删除车系
        carLine.delete(modifyBy);

        // 保存车系
        carLineRepository.save(carLine);
    }

    /**
     * 根据code获取车系
     *
     * @param code 业务主键
     * @return 车系聚合根
     * @throws BrandNotFoundException 车系不存在
     */
    public CarLine getCarLineByCode(String code) {
        return carLineRepository.findByCode(code)
                .orElseThrow(() -> new BrandNotFoundException("车系不存在: " + code));
    }

    /**
     * 分页查询车系列表
     *
     * @param page            页码
     * @param size            每页大小
     * @param brandCode       品牌code（可选）
     * @param includeInactive 是否包含失效记录
     * @return 车系列表
     */
    public List<CarLine> listCarLine(int page, int size, String brandCode, boolean includeInactive) {
        return carLineRepository.findAll(page, size, brandCode, includeInactive);
    }

    /**
     * 查询车系总数
     *
     * @param brandCode       品牌code（可选）
     * @param includeInactive 是否包含失效记录
     * @return 总数
     */
    public long countCarLine(String brandCode, boolean includeInactive) {
        return carLineRepository.count(brandCode, includeInactive);
    }

    /**
     * 创建平台
     *
     * @param code          业务主键
     * @param name          官方名称
     * @param nameLocal     本地化名称
     * @param platformType  平台类型
     * @param architecture  EE架构代号
     * @param effectiveFrom 生效开始时间
     * @param effectiveTo   生效结束时间
     * @param createBy      创建人
     * @return 平台聚合根
     * @throws DuplicateCodeException      code已存在
     * @throws InvalidEffectiveDateException 生效期无效
     */
    public Platform createPlatform(String code, String name, String nameLocal,
                                   String platformType, String architecture,
                                   Date effectiveFrom, Date effectiveTo, String createBy) {
        // 检查code唯一性
        if (platformRepository.existsByCode(code)) {
            throw new DuplicateCodeException("平台code已存在: " + code);
        }

        // 创建平台
        Platform platform = Platform.create(code, name, nameLocal,
                net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.PlatformType.valueOf(platformType),
                architecture, effectiveFrom, effectiveTo, createBy);

        // 保存平台
        return platformRepository.save(platform);
    }

    /**
     * 更新平台
     *
     * @param code          业务主键
     * @param name          官方名称
     * @param nameLocal     本地化名称
     * @param platformType  平台类型
     * @param architecture  EE架构代号
     * @param effectiveFrom 生效开始时间
     * @param effectiveTo   生效结束时间
     * @param modifyBy      修改人
     * @return 平台聚合根
     * @throws BrandNotFoundException        平台不存在
     * @throws InvalidEffectiveDateException 生效期无效
     */
    public Platform updatePlatform(String code, String name, String nameLocal,
                                   String platformType, String architecture,
                                   Date effectiveFrom, Date effectiveTo, String modifyBy) {
        // 查找平台
        Platform platform = platformRepository.findByCode(code)
                .orElseThrow(() -> new BrandNotFoundException("平台不存在: " + code));

        // 更新平台
        platform.update(name, nameLocal,
                net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.PlatformType.valueOf(platformType),
                architecture, effectiveFrom, effectiveTo, modifyBy);

        // 保存平台
        return platformRepository.save(platform);
    }

    /**
     * 失效平台
     *
     * @param code     业务主键
     * @param modifyBy 修改人
     * @return 平台聚合根
     * @throws BrandNotFoundException 平台不存在
     */
    public Platform deactivatePlatform(String code, String modifyBy) {
        // 查找平台
        Platform platform = platformRepository.findByCode(code)
                .orElseThrow(() -> new BrandNotFoundException("平台不存在: " + code));

        // 失效平台
        platform.deactivate(modifyBy);

        // 保存平台
        return platformRepository.save(platform);
    }

    /**
     * 删除平台
     *
     * @param code     业务主键
     * @param modifyBy 修改人
     * @throws BrandNotFoundException 平台不存在
     */
    public void deletePlatform(String code, String modifyBy) {
        // 查找平台
        Platform platform = platformRepository.findByCode(code)
                .orElseThrow(() -> new BrandNotFoundException("平台不存在: " + code));

        // 删除平台
        platform.delete(modifyBy);

        // 保存平台
        platformRepository.save(platform);
    }

    /**
     * 根据code获取平台
     *
     * @param code 业务主键
     * @return 平台聚合根
     * @throws BrandNotFoundException 平台不存在
     */
    public Platform getPlatformByCode(String code) {
        return platformRepository.findByCode(code)
                .orElseThrow(() -> new BrandNotFoundException("平台不存在: " + code));
    }

    /**
     * 分页查询平台列表
     *
     * @param page            页码
     * @param size            每页大小
     * @param includeInactive 是否包含失效记录
     * @return 平台列表
     */
    public List<Platform> listPlatforms(int page, int size, boolean includeInactive) {
        return platformRepository.findAll(page, size, includeInactive);
    }

    /**
     * 查询平台总数
     *
     * @param includeInactive 是否包含失效记录
     * @return 总数
     */
    public long countPlatforms(boolean includeInactive) {
        return platformRepository.count(includeInactive);
    }

    /**
     * 查询品牌历史版本列表
     *
     * @param code 品牌code
     * @return 历史版本列表
     * @throws BrandNotFoundException 品牌不存在
     */
    public List<BrandHistory> listBrandHistory(String code) {
        // 检查品牌是否存在
        if (!brandRepository.existsByCode(code)) {
            throw new BrandNotFoundException("品牌不存在: " + code);
        }
        return brandRepository.findHistoryByCode(code);
    }

    /**
     * 查询车系历史版本列表
     *
     * @param code 车系code
     * @return 历史版本列表
     * @throws BrandNotFoundException 车系不存在
     */
    public List<CarLineHistory> listCarLineHistory(String code) {
        // 检查车系是否存在
        if (!carLineRepository.existsByCode(code)) {
            throw new BrandNotFoundException("车系不存在: " + code);
        }
        return carLineRepository.findHistoryByCode(code);
    }

    /**
     * 查询平台历史版本列表
     *
     * @param code 平台code
     * @return 历史版本列表
     * @throws BrandNotFoundException 平台不存在
     */
    public List<PlatformHistory> listPlatformHistory(String code) {
        // 检查平台是否存在
        if (!platformRepository.existsByCode(code)) {
            throw new BrandNotFoundException("平台不存在: " + code);
        }
        return platformRepository.findHistoryByCode(code);
    }
}
