package io.qyi.e5.outlook.bean;

import lombok.Data;

/**
 * @program: e5
 * @description:
 * @author: 落叶随风
 * @create: 2020-12-17 22:20
 **/
@Data
public class OutlookMq {
    private int githubId;
    private int outlookId;

    public OutlookMq(int githubId, int outlookId) {
        this.githubId = githubId;
        this.outlookId = outlookId;
    }
}
