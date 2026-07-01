package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.SwinManagedSystem;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.SwinManagedSystemRepository;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.SwinManagedSystemMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.SwinManagedSystemPo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SWIN管理软件系统仓储实现
 *
 * @author hwyz_leo
 */
@Repository
@RequiredArgsConstructor
public class SwinManagedSystemRepositoryImpl implements SwinManagedSystemRepository {

    private final SwinManagedSystemMapper swinManagedSystemMapper;

    @Override
    public void save(SwinManagedSystem swinManagedSystem) {
        SwinManagedSystemPo po = toPo(swinManagedSystem);
        if (po.getId() == null) {
            swinManagedSystemMapper.insert(po);
            swinManagedSystem.setId(po.getId());
        } else {
            swinManagedSystemMapper.updateById(po);
        }
    }

    @Override
    public List<SwinManagedSystem> findBySwinCode(String swinCode) {
        LambdaQueryWrapper<SwinManagedSystemPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SwinManagedSystemPo::getCode, swinCode);
        return swinManagedSystemMapper.selectList(wrapper).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public boolean existsBySwinCodeAndVehicleNodeCode(String swinCode, String vehicleNodeCode) {
        LambdaQueryWrapper<SwinManagedSystemPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SwinManagedSystemPo::getCode, swinCode);
        wrapper.eq(SwinManagedSystemPo::getName, vehicleNodeCode);
        return swinManagedSystemMapper.selectCount(wrapper) > 0;
    }

    @Override
    public long countByVehicleNodeCode(String vehicleNodeCode) {
        LambdaQueryWrapper<SwinManagedSystemPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SwinManagedSystemPo::getName, vehicleNodeCode);
        return swinManagedSystemMapper.selectCount(wrapper);
    }

    @Override
    public void deleteBySwinCodeAndVehicleNodeCode(String swinCode, String vehicleNodeCode) {
        LambdaQueryWrapper<SwinManagedSystemPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SwinManagedSystemPo::getCode, swinCode);
        wrapper.eq(SwinManagedSystemPo::getName, vehicleNodeCode);
        swinManagedSystemMapper.delete(wrapper);
    }

    @Override
    public void deleteAllBySwinCode(String swinCode) {
        LambdaQueryWrapper<SwinManagedSystemPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SwinManagedSystemPo::getCode, swinCode);
        swinManagedSystemMapper.delete(wrapper);
    }

    private SwinManagedSystemPo toPo(SwinManagedSystem domain) {
        return SwinManagedSystemPo.builder()
                .id(domain.getId()).code(domain.getSwinCode()).name(domain.getVehicleNodeCode())
                .sortOrder(0).source("MANUAL")
                .version(1).status("ACTIVE")
                .createBy(domain.getCreateBy()).createTime(domain.getCreateTime())
                .modifyBy(domain.getModifyBy()).modifyTime(domain.getModifyTime())
                .rowVersion(domain.getRowVersion()).rowValid(domain.getRowValid())
                .build();
    }

    private SwinManagedSystem toDomain(SwinManagedSystemPo po) {
        return SwinManagedSystem.builder()
                .id(po.getId()).swinCode(po.getCode()).vehicleNodeCode(po.getName())
                .createBy(po.getCreateBy()).createTime(po.getCreateTime())
                .modifyBy(po.getModifyBy()).modifyTime(po.getModifyTime())
                .rowVersion(po.getRowVersion()).rowValid(po.getRowValid())
                .build();
    }
}
