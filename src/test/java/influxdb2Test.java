import io.qyi.e5.bean.influx2.InfluxdbConfig;
import io.qyi.e5.outlook_log.service.IOutlookLogService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public class influxdb2Test {

    @InjectMocks
    InfluxdbConfig influxdbConfig;

    @Mock
    IOutlookLogService outlookLogService;



    @Test
    public void test1(){
        // outlookLogService.findAllList(1000, 2000);
        influxdbConfig.influxDBClient();
    }
}
