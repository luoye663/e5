package io.qyi.e5.outlook.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.qyi.e5.bean.result.Result;
import io.qyi.e5.bean.result.ResultEnum;
import io.qyi.e5.config.ResultVO;
import io.qyi.e5.config.security.UsernamePasswordAuthenticationToken;
import io.qyi.e5.outlook.bean.OutlookListVo;
import io.qyi.e5.outlook.bean.OutlookVo;
import io.qyi.e5.outlook.bean.bo.insertOneBO;
import io.qyi.e5.outlook.entity.Outlook;
import io.qyi.e5.outlook.service.IOutlookService;
import io.qyi.e5.util.ResultUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

    @PostMapping("/insertOne")
    public ResultVO insertOne(@RequestBody insertOneBO bo) {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        Outlook outlook = outlookService.insertOne(bo.getName(), bo.getDescribe(), authentication.getGithub_id());
        OutlookVo vo = new OutlookVo();
        BeanUtils.copyProperties(outlook, vo);
        return new ResultVO<>(vo);
    }

    @PostMapping("/save")
    public ResultVO save(@RequestParam String client_id, @RequestParam String client_secret,@RequestParam int outlook_id) {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        if (outlookService.save(client_id, client_secret, outlook_id, authentication.getGithub_id())) {
            return new ResultVO<>();
        }
        return new ResultVO<>();
    }


    @PostMapping("/saveRandomTime")
    public Result saveRandomTime(@RequestParam int cronTime, @RequestParam String crondomTime) {
        String[] split = crondomTime.split("-");
        if (split.length != 2) {
            return ResultUtil.error(ResultEnum.INVALID_format);
        }
        int cron_time_random_start;
        int cron_time_random_end;
        try {
            cron_time_random_start = Integer.valueOf(split[0]);
            cron_time_random_end = Integer.valueOf(split[1]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return ResultUtil.error(ResultEnum.INVALID_format);
        }
        if (cron_time_random_start > cron_time_random_end) {
            return ResultUtil.error(-1, "亲，你的开始时间大于结束时间吗?");
        }
        if (cron_time_random_start < 1 || cron_time_random_end < 1) {
            return ResultUtil.error(-1, "时间为负？？？");
        }
        if (cron_time_random_start < 60) {
            return ResultUtil.error(-1, "最小间隔时间为 60 秒");
        }
        if (cron_time_random_end > 1296000) {
            return ResultUtil.error(-1, "最大间隔时间为 6 小时");
        }
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        if (outlookService.saveRandomTime(authentication.getGithub_id(), cronTime, cron_time_random_start, cron_time_random_end)) {
            return ResultUtil.success();
        }
        return ResultUtil.error(ResultEnum.UNKNOWN_ERROR);
    }

    @GetMapping("/getOutlookInfo")
    public Result getOutlookInfo() {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        int github_id = authentication.getGithub_id();
        QueryWrapper<Outlook> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("github_id", github_id);
        Outlook one = outlookService.getOne(queryWrapper);
        OutlookVo vo = new OutlookVo();
        if (one != null) {
            BeanUtils.copyProperties(one, vo);
        }
        return ResultUtil.success(vo);
    }


    @GetMapping("/getOutlookList")
    public Result getOutlookList() {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        int github_id = authentication.getGithub_id();
        List<Outlook> outlooklist = outlookService.getOutlooklist(github_id);
        List<OutlookListVo> vo = new ArrayList<>();
        outlooklist.forEach(outlook -> {
            OutlookListVo v = new OutlookListVo();
            BeanUtils.copyProperties(outlook, v);
            vo.add(v);
        });
        return ResultUtil.success(vo);
    }


}
