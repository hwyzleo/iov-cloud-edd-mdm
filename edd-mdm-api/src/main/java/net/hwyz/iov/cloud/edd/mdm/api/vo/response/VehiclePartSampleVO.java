package net.hwyz.iov.cloud.edd.mdm.api.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * VMD VehiclePart 反查单条样本
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehiclePartSampleVO {

    /**
     * 零部件业务 code
     */
    private String partCode;

    /**
     * 关联的车辆 VIN
     */
    private String vehicleVin;

    /**
     * 引用记录创建时间
     */
    private Date createTime;
}
