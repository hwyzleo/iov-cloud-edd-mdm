package net.hwyz.iov.cloud.edd.mdm.service.domain.model.catalog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 物料品类标准目录条目（CR-039 §3）
 * <p>
 * 每条记录包含：code / level（声明层级，须与按 parentCode 链计算的深度一致）/
 * name（英文标准名称）/ nameLocal（中文本地化名称）/ parentCode（L2/L3 指向父节点，L1 为空）/
 * description / aliases[]（仅目录元数据，不落库）/ sortOrder。
 * 标准目录不含 _X_ 扩展项、第四层、环路、孤儿或跨 scope 挂接。
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialCategoryCatalogEntry {

    /**
     * 受控层级语义 code（如 MC_CMP、MC_CMP_BODY、MC_CMP_BODY_BIW）
     */
    private String code;

    /**
     * 声明层级：1=L1 物料大类，2=L2 工程系统/专业域，3=L3 可归类叶子
     */
    private Integer level;

    /**
     * 英文标准名称（对应数据库 name）
     */
    private String name;

    /**
     * 中文本地化名称（对应数据库 name_local）
     */
    private String nameLocal;

    /**
     * 父节点 code（L1 为空，L2 指向 L1，L3 指向 L2）
     */
    private String parentCode;

    /**
     * 描述（可选）
     */
    private String description;

    /**
     * 检索/legacy 映射同义词（仅目录元数据，不落库、不生成第二条品类）
     */
    @Builder.Default
    private List<String> aliases = new ArrayList<>();

    /**
     * 排序序号（同级内升序）
     */
    private Integer sortOrder;
}
