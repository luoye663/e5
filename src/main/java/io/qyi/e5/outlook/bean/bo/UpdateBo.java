package io.qyi.e5.outlook.bean.bo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @program: e5
 * @description:
 * @author: 落叶随风
 * @create: 2020-12-10 22:36
 **/
@Data
public class UpdateBo {
    @NotEmpty(message = "[客户端]不能为空!")
    private String client_id;

    @NotEmpty(message = "[客户端密码]不能为空!")
    private String client_secret;

    @NotEmpty(message = "[租户ID]不能为空!")
    private String tenant_id;

    @NotNull(message = "[ID]不能为空!")
    private Integer outlook_id;
}
