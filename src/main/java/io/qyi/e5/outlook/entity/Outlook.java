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



}
