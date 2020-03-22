package io.qyi.e5.controller.web;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.qyi.e5.config.security.UsernamePasswordAuthenticationToken;
import io.qyi.e5.outlook.entity.Outlook;
import io.qyi.e5.outlook.service.IOutlookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;

/**
 * @program: e5
 * @description:
 * @author: 落叶随风
 * @create: 2020-02-27 16:03
 **/
@Controller
public class WebController {

    @Autowired
    IOutlookService outlookService;

    @RequestMapping("/")
    public void index(HttpServletResponse httpServletResponse) {
        httpServletResponse.setStatus(302);
        httpServletResponse.setHeader("Location", "/user/home");
    }

    @RequestMapping("/user/login")
    public String login(Model model) {
        model.addAttribute("welcome", "hello fishpro");
        return "user/login";
    }

    @RequestMapping("/user/home")
    public String home(Model model) {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        int github_id = authentication.getGithub_id();
        QueryWrapper<Outlook> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("github_id", github_id);
        Outlook one = outlookService.getOne(queryWrapper);
        if (one != null) {
            model.addAttribute("client_id", one.getClientId());
            model.addAttribute("client_secret", one.getClientSecret());
            model.addAttribute("cron_time", one.getCronTime());
            model.addAttribute("cron_time_random_start", one.getCronTimeRandomStart());
            model.addAttribute("cron_time_random_end", one.getCronTimeRandomEnd());
        } else {
            model.addAttribute("client_id", "");
            model.addAttribute("client_secret", "");
            model.addAttribute("cron_time", 0);
            model.addAttribute("cron_time_random_start", 0);
            model.addAttribute("cron_time_random_end", 0);
        }

        model.addAttribute("welcome", "hello fishpro");
        return "user/home";
    }

}
