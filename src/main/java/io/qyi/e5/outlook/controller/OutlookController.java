package io.qyi.e5.outlook.controller;


import io.qyi.e5.bean.result.Result;
import io.qyi.e5.bean.result.ResultEnum;
import io.qyi.e5.config.security.UsernamePasswordAuthenticationToken;
import io.qyi.e5.outlook.service.IOutlookService;
import io.qyi.e5.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 落叶
 * @since 2020-02-24
 */
@RestController
@RequestMapping("/outlook/outlook")
public class OutlookController {

    @Autowired
    IOutlookService outlookService;

    @PostMapping("/save")
    public Result save(@RequestParam String client_id, @RequestParam String client_secret) {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        if (outlookService.save(client_id, client_secret,authentication.getGithub_id())) {
            return ResultUtil.success();
        }
        return ResultUtil.error(ResultEnum.UNKNOWN_ERROR);
    }

    @PostMapping("/saveRandomTime")
    public Result saveRandomTime(@RequestParam int cronTime,@RequestParam int crondomTime) {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        if (outlookService.saveRandomTime(cronTime,0,0)) {
            return ResultUtil.success();
        }
        return ResultUtil.error(ResultEnum.UNKNOWN_ERROR);
    }
}
