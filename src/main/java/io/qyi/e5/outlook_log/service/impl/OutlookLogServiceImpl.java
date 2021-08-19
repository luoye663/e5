package io.qyi.e5.outlook_log.service.impl;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import io.qyi.e5.outlook_log.entity.OutlookLog;
import io.qyi.e5.outlook_log.service.IOutlookLogService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 落叶
 * @since 2020-03-03
 */
@Service
public class OutlookLogServiceImpl implements IOutlookLogService {

    @Resource
    private InfluxDBClient influxDBClient;

    @Value("${spring.influx.org}")
    private String org;

    @Value("${spring.influx.bucket}")
    private String bucket;

    @Override
    public void addLog(int githubId, int outlookId, String msg, int result, String original_msg) {
        try (WriteApi writeApi = influxDBClient.getWriteApi()) {
            OutlookLog log = new OutlookLog();
            log.setCallTime(System.currentTimeMillis())
                    .setGithubId(String.valueOf(githubId))
                    .setOutlookId(String.valueOf(outlookId))
                    .setMsg(msg)
                    .setOriginalMsg(original_msg)
                    .setResult(result);
            writeApi.writeMeasurement(bucket, org, WritePrecision.NS, log);

        }

    }

    @Override
    public List<OutlookLog> findAllList(int githubId, int outlookId) {
        String flux = "from(bucket:\"" + bucket + "\") |> range(start: 0)" +
                "|> filter(fn: (r) => r[\"_measurement\"] == \"OutlookLog\")" +
                "|> filter(fn: (r) => r[\"githubId\"] == \"" + githubId + "\")" +
                "|> filter(fn: (r) => r[\"outlookId\"] == \"" + outlookId + "\")" +
                "|> pivot(rowKey:[\"_time\"], columnKey: [\"_field\"], valueColumn: \"_value\")";
        QueryApi queryApi = influxDBClient.getQueryApi();
        List<OutlookLog> tables = queryApi.query(flux, org, OutlookLog.class);
        return tables;
    }
}
