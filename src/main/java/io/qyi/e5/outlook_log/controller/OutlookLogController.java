package io.qyi.e5.outlook_log.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.qyi.e5.config.security.UsernamePasswordAuthenticationToken;
import io.qyi.e5.outlook.entity.Outlook;
import io.qyi.e5.outlook.service.IOutlookService;
import io.qyi.e5.outlook_log.entity.OutlookLog;
import io.qyi.e5.outlook_log.service.IOutlookLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 落叶
 * @since 2020-03-03
 */
@Controller
@RequestMapping("/outlookLog")
public class OutlookLogController {

//    @Autowired
//    private OutlookLogMapper outlookLogMapper;

    @Autowired
    private IOutlookLogService outlookLogService;
    @Autowired
    IOutlookService outlookService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${page.size}")
    private int pageSize;

    @GetMapping("/findLog")
    public String findLog(Model model){
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        int github_id = authentication.getGithub_id();
        /*QueryWrapper<OutlookLog> wrapper = new QueryWrapper();
        wrapper.eq("github_id", github_id);
        Page<OutlookLog> page = new Page<>(8,pageSize);

        IPage<Map<String, Object>> mapIPage = outlookLogMapper.selectMapsPage(page, wrapper);
        System.out.println("总页数"+mapIPage.getPages());
        System.out.println("总记录数"+mapIPage.getTotal());
        List<Map<String, Object>> records = mapIPage.getRecords();
        records.forEach(System.out::println);*/

        QueryWrapper<OutlookLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("github_id", github_id);
        List<OutlookLog> list = outlookLogService.list(queryWrapper);
        model.addAttribute("list_log", list);
        return "/outlookLog/findLog";
    }

    @GetMapping("/exec111111")
    public void s(){
        List<Outlook> list = outlookService.findAll();
        logger.info(String.valueOf(list.size()));
        for (Outlook outlook :list) {
            logger.info(outlook.toString());
            outlookService.getMailList(outlook);
        }

    }

}
