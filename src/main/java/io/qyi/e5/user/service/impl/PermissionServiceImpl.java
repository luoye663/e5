package io.qyi.e5.user.service.impl;

import io.qyi.e5.user.entity.Permission;
import io.qyi.e5.user.mapper.PermissionMapper;
import io.qyi.e5.user.service.IPermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 落叶
 * @since 2020-02-24
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements IPermissionService {

}
