package io.qyi.e5.outlook.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.qyi.e5.outlook.entity.Outlook;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 落叶
 * @since 2020-02-24
 */
public interface OutlookMapper extends BaseMapper<Outlook> {
    Outlook selectOutlookOne(int id, int github_id);
}
