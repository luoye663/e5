package io.qyi.e5.outlook.bean.bo;

import lombok.Data;

/**
 * @program: e5
 * @description:
 * @author: 落叶随风
 * @create: 2020-12-10 22:36
 **/
@Data
public class UpdateBo {
    private String client_id;
    private String client_secret;
    private int outlook_id;
}
