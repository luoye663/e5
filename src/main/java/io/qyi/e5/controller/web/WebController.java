package io.qyi.e5.controller.web;

import io.qyi.e5.bean.result.Result;
import io.qyi.e5.outlook.service.IOutlookService;
import io.qyi.e5.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: e5
 * @description:
 * @author: 落叶随风
 * @create: 2020-02-27 16:03
 **/
@RestController
public class WebController {

    @Autowired
    IOutlookService outlookService;

    @RequestMapping("/")
    public Result index() {
        return ResultUtil.error(-1, "This is api server!");
    }

}
