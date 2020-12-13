package io.qyi.e5.outlook_log.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author 落叶
 * @since 2020-03-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class OutlookLog implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * github_id
     */
    private Integer githubId;

    /**
     * outlook_id
     */
    private Integer outlookId;

    /**
     * 调用时间
     */
    private Integer callTime;

    /**
     * 调用结果
     */
    private Integer result;

    /**
     * 如果有错误原因则记录
     */
    private String msg;

    /**
     * 原始错误消息
     */
    private String originalMsg;


}
