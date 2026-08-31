package net.hwyz.iov.cloud.edd.mdm.service.domain.model.catalog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 设备类别标准目录条目（CR-037 §5）
 * <p>
 * 每条记录包含：code / name（英文标准名称）/ nameLocal（中文本地化名称）/ description /
 * aliases[]（仅目录元数据，不落库）/ sortOrder。
 * 目录记录不包含 tier、category、parentCode、nodeType 或 recommendedNodeTypes 属性。
 * DeviceCategory 为扁平字典：不保存通信制式、安装方位、功率、分辨率、线数、硬件代次等节点级规格。
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceCategoryCatalogEntry {

    /**
     * 受控精简设备族 code（如 TBOX、DCU、CAM、LIDAR）
     */
    private String code;

    /**
     * 英文标准名称（对应数据库 name）
     */
    private String name;

    /**
     * 中文本地化名称（对应数据库 name_local）
     */
    private String nameLocal;

    /**
     * 描述（可选）
     */
    private String description;

    /**
     * 检索/legacy 映射同义词（TCU、Telematics Box 等），仅目录元数据，不落库、不生成第二条类别
     */
    @Builder.Default
    private List<String> aliases = new ArrayList<>();

    /**
     * 排序序号（升序）
     */
    private Integer sortOrder;
}
