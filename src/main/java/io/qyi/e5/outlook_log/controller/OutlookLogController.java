package io.qyi.e5.outlook_log.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApi;
import com.influxdb.client.WriteOptions;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import io.qyi.e5.bean.result.Result;
import io.qyi.e5.config.security.UsernamePasswordAuthenticationToken;
import io.qyi.e5.outlook.service.IOutlookService;
import io.qyi.e5.outlook_log.bena.LogVo;
import io.qyi.e5.outlook_log.entity.OutlookLog;
import io.qyi.e5.outlook_log.service.IOutlookLogService;
import io.qyi.e5.util.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
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

    @Autowired
    InfluxDBClient influxDBClient;
    @Value("${spring.influx.org:''}")
    private String org;

    @Value("${spring.influx.bucket:''}")
    private String bucket;


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${page.size}")
    private int pageSize;

    @GetMapping("/findLog")
    @ResponseBody
    public Result findLog(@RequestParam int outlookId){
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        int github_id = authentication.getGithub_id();

        QueryWrapper<OutlookLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("github_id", github_id).eq("outlook_id", outlookId).orderByAsc("call_time");

        List<OutlookLog> list = outlookLogService.findAllList(github_id, outlookId);
        Iterator<OutlookLog> iterator = list.iterator();
        List<LogVo> logVo = new LinkedList<>();
        while (iterator.hasNext()) {
            OutlookLog next = iterator.next();
            LogVo vo = new LogVo();
            BeanUtils.copyProperties(next,vo);
            logVo.add(vo);
        }
        return ResultUtil.success(logVo);
    }


    @GetMapping("/save")
    public String save(){
        ArrayList<Point> arrayList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            Point point = Point.measurement("e5s")
                    .addTag("githubud", "22121")
                    .addField("aaaaaa1", i)
                    .addField("aaaaaa2", i)
                    .addField("aaaaaa3", i)
                    .time(Instant.now().toEpochMilli(), WritePrecision.MS);
            arrayList.add(point);
        }



        try (WriteApi writeApi = influxDBClient.getWriteApi()) {
            // writeApi.writePoint(bucket, org, point);
            writeApi.writePoints(bucket,org, arrayList);
        }
        return "ok";
    }

}
