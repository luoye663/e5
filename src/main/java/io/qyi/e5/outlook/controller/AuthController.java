package io.qyi.e5.outlook.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.qyi.e5.config.security.UsernamePasswordAuthenticationToken;
import io.qyi.e5.outlook.entity.Outlook;
import io.qyi.e5.outlook.service.IOutlookService;
import io.qyi.e5.util.EncryptUtil;
import io.qyi.e5.util.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @program: e5
 * @description:
 * @author: 落叶随风
 * @create: 2020-02-24 16:02
 **/
@Controller
@RequestMapping("/outlook/auth2")
public class AuthController {
    @Autowired
    RedisUtil redisUtil;

    @Autowired
    IOutlookService outlookService;

    @Value("${redis.auth2.outlook}")
    String states;

    @Value("${outlook.authorize.url}")
    String authorizeUrl;

    @RequestMapping("/receive")
    public String Receive(Model model, String code, String state, String session_state, HttpServletResponse response) throws Exception {
        model.addAttribute("result", false);
        if (!redisUtil.hasKey(states + state)) {
            model.addAttribute("msg", "state已过期，重新点击授权!");
            return "/user/authorization_outlook";
        }
//        这里不应该查询，在进行授权时因该把基础数据丢到redis
        QueryWrapper<Outlook> outlookQueryWrapper = new QueryWrapper<>();
        outlookQueryWrapper.eq("github_id", redisUtil.get(states + state));
        Outlook outlook = outlookService.getOne(outlookQueryWrapper);
//      删除redis中的此键
        redisUtil.del(states + state);
        if (outlook == null) {
            model.addAttribute("msg", "没有查询到此用户，请检查是否在系统中注册!");
            return "/user/authorization_outlook";
        }
        System.out.println(outlook);
        boolean authorization_code = outlookService.getTokenAndSave(code, outlook.getClientId(), outlook.getClientSecret(), "https://e5.qyi.io/outlook/auth2/receive"
                , "authorization_code");
        if (authorization_code) {
            model.addAttribute("result", true);
        } else {
            model.addAttribute("msg", "未知错误，请联系管理员~");
        }
        return "/user/authorization_outlook";
    }

    @ResponseBody
    @RequestMapping("/getAuthorizeUrl")
    public void getAuthorizeUrl(HttpServletResponse response) {
//       查询此用户的github_id与
        QueryWrapper<Outlook> outlookQueryWrapper = new QueryWrapper<>();
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        outlookQueryWrapper.eq("github_id", authentication.getGithub_id());
        Outlook outlook = outlookService.getOne(outlookQueryWrapper);

        if (outlook != null) {
            // 生成随机uuid标识用户
            String state = EncryptUtil.getInstance().SHA1Hex(UUID.randomUUID().toString());
            redisUtil.set(states + state, outlook.getGithubId(), 600);

            System.out.println(outlook);
            String url = String.format(authorizeUrl, outlook.getClientId(), "https://e5.qyi.io/outlook/auth2/receive", state);
            System.out.println(url);
            response.setStatus(302);
            response.setHeader("Location", url);
        }

    }
}
