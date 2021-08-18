package io.qyi.e5.outlook_log.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
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
@Accessors(chain = true)
@Measurement(name = "OutlookLog")
public class OutlookLog {

    private static final long serialVersionUID = 1L;
    /**
     * github_id
     */
    @Column(tag = true)
    private String githubId;

    /**
     * outlook_id
     */
    @Column(tag = true)
    private String outlookId;

    /**
     * 调用时间
     */
    @Column(timestamp = true)
    private Instant callTime;

    /**
     * 调用结果
     */
    @Column
    private Number resultc;

    /**
     * 如果有错误原因则记录
     */
    @Column
    private String msg;

    /**
     * 原始错误消息
     */
    @Column
    private String originalMsg;


}
