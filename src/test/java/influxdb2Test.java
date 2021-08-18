import com.influxdb.LogLevel;
import com.influxdb.client.*;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import io.qyi.e5.outlook_log.entity.OutlookLog;
import org.junit.jupiter.api.Test;
import org.springframework.cglib.beans.BeanMap;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class influxdb2Test {
    InfluxDBClient influxDBClient = InfluxDBClientFactory.create("http://127.0.0.1:8086", "o-oFFLbRCFHmGFMMYR7kLFGb4jTUXkJkTCmPBeZxn32prCKxwVpS-FM3pLyCvv0gVao-Cm6c_s2Yl-7Ud_xH_Q==".toCharArray()
    );

    private String org = "luoye";

    @Test
    public void save(){

        influxDBClient.setLogLevel(LogLevel.BASIC);
        WriteOptions writeOptions = WriteOptions.builder()
                .batchSize(5000)
                .flushInterval(1000)
                .bufferLimit(10000)
                .jitterInterval(1000)
                .retryInterval(5000)
                .build();

        List<OutlookLog> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            OutlookLog outlookLog = new OutlookLog();
            outlookLog.setResult(1).setMsg(i + "- ok").setOriginalMsg("加入成功").setCallTime(Instant.now());
            list.add(outlookLog);
        }

        BeanMap beanMap = BeanMap.create(list.get(0));
        try (WriteApi writeApi = influxDBClient.getWriteApi(writeOptions)) {
            // writeApi.writeMeasurement();
            // writeApi.writeMeasurements("e5", org ,WritePrecision.NS,list);
            List<Point> list1 = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                Point point = Point
                        .measurement("githubId_123")
                        .addTag("githubId","123465")
                        .addFields(beanMap)
                        .time(Instant.now(), WritePrecision.NS);
                list1.add(point);
            }

            writeApi.writePoints("e5",org,list1);

        }
        influxDBClient.close();
    }


    @Test
    public void find(){
        String flux = "from(bucket:\"e5\") |> range(start: 0)" +
                "|> filter(fn: (r) => r[\"_measurement\"] == \"OutlookLog\")" +
                // "|> filter(fn: (r) => r[\"_field\"] == \"aaaaaa1\")" +
                "|> limit(n: 100)";
        QueryApi queryApi = influxDBClient.getQueryApi();
        List<FluxTable> tables = queryApi.query(flux,org);
        for (FluxTable fluxTable : tables) {
            List<FluxRecord> records = fluxTable.getRecords();

            for (FluxRecord fluxRecord : records) {
                System.out.println(fluxRecord.getField());
                System.out.println(fluxRecord.getTime() + " ->" + fluxRecord.getValueByKey("_value"));
            }
        }
        influxDBClient.close();
    }

}
