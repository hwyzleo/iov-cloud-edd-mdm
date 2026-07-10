package net.hwyz.iov.cloud.edd.mdm.service.application.dto.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * RXSWIN登记查询对象
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RxswinRegistryQuery {

    private Integer page;
    private Integer size;
    private String manifestCode;
    private String rxswinValue;
    private String swinCode;
    private String softwareBaselineCode;
    private Date registeredAtStart;
    private Date registeredAtEnd;
}
