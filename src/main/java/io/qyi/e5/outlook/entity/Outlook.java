package io.qyi.e5.outlook.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author 落叶
 * @since 2020-03-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Outlook implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户id
     */
    private Integer githubId;

    private String idToken;

    private String clientId;

    private String clientSecret;

    /**
     * 刷新令牌
     */
    private String refreshToken;

    private String accessToken;

    /**
     * 刷新时间间隔
     */
    private Integer cronTime;

    /**
     * 随机时间开始
     */
    private Integer cronTimeRandomStart;

    /**
     * 随机时间结束
     */
    private Integer cronTimeRandomEnd;

    /*名称*/
    private String name;

    /*描述*/
    private String describes;
    /*步骤*/
    private Integer step;
    /**
     * 随机时间结束
     * 状态: 1、等待配置 2、暂停 3、运行中 4、封禁 5、已停止(由于调用错误导致的停止)
     */
    private Integer status;

    /*下次调用时间*/
    private Integer nextTime;

}
