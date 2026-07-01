package net.hwyz.iov.cloud.edd.mdm.service.domain.repository;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.SwinManagedSystem;

import java.util.List;
import java.util.Optional;

/**
 * SWIN管理软件系统仓储接口
 *
 * @author hwyz_leo
 */
public interface SwinManagedSystemRepository {

    /**
     * 保存SWIN管理软件系统
     *
     * @param swinManagedSystem SWIN管理软件系统
     */
    void save(SwinManagedSystem swinManagedSystem);

    /**
     * 根据SWIN代码查找管理的软件系统列表
     *
     * @param swinCode SWIN代码
     * @return 管理的软件系统列表
     */
    List<SwinManagedSystem> findBySwinCode(String swinCode);

    /**
     * 检查SWIN代码和车载节点代码是否存在
     *
     * @param swinCode        SWIN代码
     * @param vehicleNodeCode 车载节点代码
     * @return 是否存在
     */
    boolean existsBySwinCodeAndVehicleNodeCode(String swinCode, String vehicleNodeCode);

    /**
     * 根据车载节点代码统计引用数
     *
     * @param vehicleNodeCode 车载节点代码
     * @return 引用数
     */
    long countByVehicleNodeCode(String vehicleNodeCode);

    /**
     * 删除SWIN管理软件系统
     *
     * @param swinCode        SWIN代码
     * @param vehicleNodeCode 车载节点代码
     */
    void deleteBySwinCodeAndVehicleNodeCode(String swinCode, String vehicleNodeCode);

    /**
     * 根据SWIN代码删除所有管理的软件系统
     *
     * @param swinCode SWIN代码
     */
    void deleteAllBySwinCode(String swinCode);
}
