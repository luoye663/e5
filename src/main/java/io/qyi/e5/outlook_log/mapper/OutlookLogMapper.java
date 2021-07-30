package io.qyi.e5.outlook_log.mapper;

import io.qyi.e5.outlook_log.entity.OutlookLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 落叶
 * @since 2020-03-03
 */
public interface OutlookLogMapper extends BaseMapper<OutlookLog> {

    @Select("select * from e5.d_#{githubId}_#{outlookId}")
    List<OutlookLog> findAllList(@Param("githubId") int githubId,@Param("outlookId") int outlookId);


}
