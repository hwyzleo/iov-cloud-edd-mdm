package net.hwyz.iov.cloud.edd.mdm.service.domain.service;

import net.hwyz.iov.cloud.edd.mdm.service.domain.exception.BrandNotFoundException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.exception.DuplicateCodeException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.exception.InvalidEffectiveDateException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Brand;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.CarLine;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Configuration;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Model;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.OptionFamily;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Platform;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Variant;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.BrandHistory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.CarLineHistory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.ConfigurationHistory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.ModelHistory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.OptionFamilyHistory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.PlatformHistory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.VariantHistory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.BrandRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.CarLineRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.ConfigurationOptionCodeBindingRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.ConfigurationRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.ModelRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.OptionFamilyRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.PlatformRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.VariantRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.VariantOptionCodeBindingRepository;
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
    private final OptionFamilyRepository optionFamilyRepository;
    private final ModelRepository modelRepository;
    private final VariantRepository variantRepository;
    private final VariantOptionCodeBindingRepository variantOptionCodeBindingRepository;
    private final ConfigurationRepository configurationRepository;
    private final ConfigurationOptionCodeBindingRepository configurationOptionCodeBindingRepository;

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

    // ==================== 选项族相关方法 ====================

    /**
     * 创建选项族
     */
    public OptionFamily createOptionFamily(String code, String name, String nameLocal, String description,
                                           Date effectiveFrom, Date effectiveTo, String createBy) {
        if (optionFamilyRepository.existsByCode(code)) {
            throw new DuplicateCodeException("选项族code已存在: " + code);
        }
        OptionFamily optionFamily = OptionFamily.create(code, name, nameLocal, description,
                effectiveFrom, effectiveTo, createBy);
        return optionFamilyRepository.save(optionFamily);
    }

    /**
     * 更新选项族
     */
    public OptionFamily updateOptionFamily(String code, String name, String nameLocal, String description,
                                           Date effectiveFrom, Date effectiveTo, String modifyBy) {
        OptionFamily optionFamily = optionFamilyRepository.findByCode(code)
                .orElseThrow(() -> new BrandNotFoundException("选项族不存在: " + code));
        optionFamily.update(name, nameLocal, description, effectiveFrom, effectiveTo, modifyBy);
        return optionFamilyRepository.save(optionFamily);
    }

    /**
     * 失效选项族
     */
    public OptionFamily deactivateOptionFamily(String code, String modifyBy) {
        OptionFamily optionFamily = optionFamilyRepository.findByCode(code)
                .orElseThrow(() -> new BrandNotFoundException("选项族不存在: " + code));
        optionFamily.deactivate(modifyBy);
        return optionFamilyRepository.save(optionFamily);
    }

    /**
     * 删除选项族
     */
    public void deleteOptionFamily(String code, String modifyBy) {
        OptionFamily optionFamily = optionFamilyRepository.findByCode(code)
                .orElseThrow(() -> new BrandNotFoundException("选项族不存在: " + code));
        optionFamily.delete(modifyBy);
        optionFamilyRepository.save(optionFamily);
    }

    /**
     * 根据code获取选项族
     */
    public OptionFamily getOptionFamilyByCode(String code) {
        return optionFamilyRepository.findByCode(code)
                .orElseThrow(() -> new BrandNotFoundException("选项族不存在: " + code));
    }

    /**
     * 分页查询选项族列表
     */
    public List<OptionFamily> listOptionFamilies(int page, int size, boolean includeInactive) {
        return optionFamilyRepository.findAll(page, size, includeInactive);
    }

    /**
     * 查询选项族总数
     */
    public long countOptionFamilies(boolean includeInactive) {
        return optionFamilyRepository.count(includeInactive);
    }

    /**
     * 查询选项族历史版本列表
     */
    public List<OptionFamilyHistory> listOptionFamilyHistory(String code) {
        if (!optionFamilyRepository.existsByCode(code)) {
            throw new BrandNotFoundException("选项族不存在: " + code);
        }
        return optionFamilyRepository.findHistoryByCode(code);
    }

    // ==================== 车型相关方法 ====================

    /**
     * 创建车型
     */
    public Model createModel(String code, String name, String nameLocal, String carLineCode,
                             String platformCode, String modelYear, String description,
                             Date effectiveFrom, Date effectiveTo, String createBy) {
        if (modelRepository.existsByCode(code)) {
            throw new DuplicateCodeException("车型code已存在: " + code);
        }
        // 校验carLineCode存在且ACTIVE
        CarLine carLine = carLineRepository.findByCode(carLineCode)
                .orElseThrow(() -> new IllegalArgumentException("车系不存在: " + carLineCode));
        if (carLine.getStatus() != net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.CarLineStatus.ACTIVE) {
            throw new IllegalArgumentException("车系状态不是ACTIVE: " + carLineCode);
        }
        // 校验platformCode存在且ACTIVE
        Platform platform = platformRepository.findByCode(platformCode)
                .orElseThrow(() -> new IllegalArgumentException("平台不存在: " + platformCode));
        if (platform.getStatus() != net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.PlatformStatus.ACTIVE) {
            throw new IllegalArgumentException("平台状态不是ACTIVE: " + platformCode);
        }
        Model model = Model.create(code, name, nameLocal, carLineCode, platformCode,
                modelYear, description, effectiveFrom, effectiveTo, createBy);
        return modelRepository.save(model);
    }

    /**
     * 更新车型
     */
    public Model updateModel(String code, String name, String nameLocal, String modelYear,
                             String description, Date effectiveFrom, Date effectiveTo, String modifyBy) {
        Model model = modelRepository.findByCode(code)
                .orElseThrow(() -> new BrandNotFoundException("车型不存在: " + code));
        model.update(name, nameLocal, modelYear, description, effectiveFrom, effectiveTo, modifyBy);
        return modelRepository.save(model);
    }

    /**
     * 失效车型
     */
    public Model deactivateModel(String code, String modifyBy) {
        Model model = modelRepository.findByCode(code)
                .orElseThrow(() -> new BrandNotFoundException("车型不存在: " + code));
        model.deactivate(modifyBy);
        return modelRepository.save(model);
    }

    /**
     * 删除车型
     */
    public void deleteModel(String code, String modifyBy) {
        Model model = modelRepository.findByCode(code)
                .orElseThrow(() -> new BrandNotFoundException("车型不存在: " + code));
        model.delete(modifyBy);
        modelRepository.save(model);
    }

    /**
     * 根据code获取车型
     */
    public Model getModelByCode(String code) {
        return modelRepository.findByCode(code)
                .orElseThrow(() -> new BrandNotFoundException("车型不存在: " + code));
    }

    /**
     * 分页查询车型列表
     */
    public List<Model> listModels(int page, int size, String carLineCode, String platformCode, boolean includeInactive) {
        return modelRepository.findAll(page, size, carLineCode, platformCode, includeInactive);
    }

    /**
     * 查询车型总数
     */
    public long countModels(String carLineCode, String platformCode, boolean includeInactive) {
        return modelRepository.count(carLineCode, platformCode, includeInactive);
    }

    /**
     * 查询车型历史版本列表
     */
    public List<ModelHistory> listModelHistory(String code) {
        if (!modelRepository.existsByCode(code)) {
            throw new BrandNotFoundException("车型不存在: " + code);
        }
        return modelRepository.findHistoryByCode(code);
    }

    // ==================== 版本相关方法 ====================

    /**
     * 创建版本
     */
    public Variant createVariant(String code, String name, String nameLocal, String modelCode,
                                 String description, Date effectiveFrom, Date effectiveTo, String createBy) {
        if (variantRepository.existsByCode(code)) {
            throw new DuplicateCodeException("版本code已存在: " + code);
        }
        // 校验modelCode存在且ACTIVE
        Model model = modelRepository.findByCode(modelCode)
                .orElseThrow(() -> new IllegalArgumentException("车型不存在: " + modelCode));
        if (model.getStatus() != net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.ModelStatus.ACTIVE) {
            throw new IllegalArgumentException("车型状态不是ACTIVE: " + modelCode);
        }
        Variant variant = Variant.create(code, name, nameLocal, modelCode, description,
                effectiveFrom, effectiveTo, createBy);
        return variantRepository.save(variant);
    }

    /**
     * 更新版本
     */
    public Variant updateVariant(String code, String name, String nameLocal, String description,
                                 Date effectiveFrom, Date effectiveTo, String modifyBy) {
        Variant variant = variantRepository.findByCode(code)
                .orElseThrow(() -> new BrandNotFoundException("版本不存在: " + code));
        variant.update(name, nameLocal, description, effectiveFrom, effectiveTo, modifyBy);
        return variantRepository.save(variant);
    }

    /**
     * 失效版本
     */
    public Variant deactivateVariant(String code, String modifyBy) {
        Variant variant = variantRepository.findByCode(code)
                .orElseThrow(() -> new BrandNotFoundException("版本不存在: " + code));
        variant.deactivate(modifyBy);
        return variantRepository.save(variant);
    }

    /**
     * 删除版本
     */
    public void deleteVariant(String code, String modifyBy) {
        Variant variant = variantRepository.findByCode(code)
                .orElseThrow(() -> new BrandNotFoundException("版本不存在: " + code));
        variant.delete(modifyBy);
        variantRepository.save(variant);
    }

    /**
     * 根据code获取版本
     */
    public Variant getVariantByCode(String code) {
        return variantRepository.findByCode(code)
                .orElseThrow(() -> new BrandNotFoundException("版本不存在: " + code));
    }

    /**
     * 分页查询版本列表
     */
    public List<Variant> listVariants(int page, int size, String modelCode, String carLineCode, String platformCode, boolean includeInactive) {
        return variantRepository.findAll(page, size, modelCode, carLineCode, platformCode, includeInactive);
    }

    /**
     * 查询版本总数
     */
    public long countVariants(String modelCode, String carLineCode, String platformCode, boolean includeInactive) {
        return variantRepository.count(modelCode, carLineCode, platformCode, includeInactive);
    }

    /**
     * 查询版本历史版本列表
     */
    public List<VariantHistory> listVariantHistory(String code) {
        if (!variantRepository.existsByCode(code)) {
            throw new BrandNotFoundException("版本不存在: " + code);
        }
        return variantRepository.findHistoryByCode(code);
    }

    /**
     * 绑定版本选项码
     */
    public void bindVariantOptionCode(String variantCode, String optionCodeCode, String optionFamilyCode, String operator) {
        // 校验版本存在
        variantRepository.findByCode(variantCode)
                .orElseThrow(() -> new BrandNotFoundException("版本不存在: " + variantCode));
        // 互斥约束：同一版本下同一选项族只能绑定一个选项码
        if (variantOptionCodeBindingRepository.existsByVariantCodeAndOptionFamilyCode(variantCode, optionFamilyCode)) {
            throw new IllegalStateException("同一版本下同一选项族只能绑定一个选项码");
        }
        variantOptionCodeBindingRepository.bind(variantCode, optionCodeCode, optionFamilyCode, operator);
    }

    /**
     * 解绑版本选项码
     */
    public void unbindVariantOptionCode(String variantCode, String optionCodeCode, String operator) {
        variantRepository.findByCode(variantCode)
                .orElseThrow(() -> new BrandNotFoundException("版本不存在: " + variantCode));
        variantOptionCodeBindingRepository.unbind(variantCode, optionCodeCode, operator);
    }

    // ==================== 配置相关方法 ====================

    /**
     * 创建配置
     */
    public Configuration createConfiguration(String code, String name, String nameLocal, String variantCode,
                                             String description, Date effectiveFrom, Date effectiveTo, String createBy) {
        if (configurationRepository.existsByCode(code)) {
            throw new DuplicateCodeException("配置code已存在: " + code);
        }
        // 校验variantCode存在且ACTIVE
        Variant variant = variantRepository.findByCode(variantCode)
                .orElseThrow(() -> new IllegalArgumentException("版本不存在: " + variantCode));
        if (variant.getStatus() != net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.VariantStatus.ACTIVE) {
            throw new IllegalArgumentException("版本状态不是ACTIVE: " + variantCode);
        }
        Configuration configuration = Configuration.create(code, name, nameLocal, variantCode, description,
                effectiveFrom, effectiveTo, createBy);
        return configurationRepository.save(configuration);
    }

    /**
     * 更新配置
     */
    public Configuration updateConfiguration(String code, String name, String nameLocal, String description,
                                             Date effectiveFrom, Date effectiveTo, String modifyBy) {
        Configuration configuration = configurationRepository.findByCode(code)
                .orElseThrow(() -> new BrandNotFoundException("配置不存在: " + code));
        configuration.update(name, nameLocal, description, effectiveFrom, effectiveTo, modifyBy);
        return configurationRepository.save(configuration);
    }

    /**
     * 失效配置
     */
    public Configuration deactivateConfiguration(String code, String modifyBy) {
        Configuration configuration = configurationRepository.findByCode(code)
                .orElseThrow(() -> new BrandNotFoundException("配置不存在: " + code));
        configuration.deactivate(modifyBy);
        return configurationRepository.save(configuration);
    }

    /**
     * 删除配置
     */
    public void deleteConfiguration(String code, String modifyBy) {
        Configuration configuration = configurationRepository.findByCode(code)
                .orElseThrow(() -> new BrandNotFoundException("配置不存在: " + code));
        configuration.delete(modifyBy);
        configurationRepository.save(configuration);
    }

    /**
     * 根据code获取配置
     */
    public Configuration getConfigurationByCode(String code) {
        return configurationRepository.findByCode(code)
                .orElseThrow(() -> new BrandNotFoundException("配置不存在: " + code));
    }

    /**
     * 分页查询配置列表
     */
    public List<Configuration> listConfigurations(int page, int size, String variantCode, boolean includeInactive) {
        return configurationRepository.findAll(page, size, variantCode, includeInactive);
    }

    /**
     * 查询配置总数
     */
    public long countConfigurations(String variantCode, boolean includeInactive) {
        return configurationRepository.count(variantCode, includeInactive);
    }

    /**
     * 查询配置历史版本列表
     */
    public List<ConfigurationHistory> listConfigurationHistory(String code) {
        if (!configurationRepository.existsByCode(code)) {
            throw new BrandNotFoundException("配置不存在: " + code);
        }
        return configurationRepository.findHistoryByCode(code);
    }

    /**
     * 绑定配置选项码
     */
    public void bindConfigurationOptionCode(String configurationCode, String optionCodeCode, String optionFamilyCode, String operator) {
        configurationRepository.findByCode(configurationCode)
                .orElseThrow(() -> new BrandNotFoundException("配置不存在: " + configurationCode));
        // 互斥约束：同一配置下同一选项族只能绑定一个选项码
        if (configurationOptionCodeBindingRepository.existsByConfigurationCodeAndOptionFamilyCode(configurationCode, optionFamilyCode)) {
            throw new IllegalStateException("同一配置下同一选项族只能绑定一个选项码");
        }
        configurationOptionCodeBindingRepository.bind(configurationCode, optionCodeCode, optionFamilyCode, operator);
    }

    /**
     * 解绑配置选项码
     */
    public void unbindConfigurationOptionCode(String configurationCode, String optionCodeCode, String operator) {
        configurationRepository.findByCode(configurationCode)
                .orElseThrow(() -> new BrandNotFoundException("配置不存在: " + configurationCode));
        configurationOptionCodeBindingRepository.unbind(configurationCode, optionCodeCode, operator);
    }

    /**
     * 根据选项码组合反查配置（包含匹配，仅返回ACTIVE状态）
     */
    public List<Configuration> findConfigurationsByOptionCodes(List<String> optionCodes) {
        if (optionCodes == null || optionCodes.isEmpty()) {
            return List.of();
        }
        List<String> codes = configurationOptionCodeBindingRepository.findConfigurationCodesByOptionCodes(optionCodes, optionCodes.size());
        if (codes.isEmpty()) {
            return List.of();
        }
        return configurationRepository.findByCodes(codes, true);
    }
}
