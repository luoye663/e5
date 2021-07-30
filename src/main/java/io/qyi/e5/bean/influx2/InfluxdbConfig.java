package io.qyi.e5.bean.influx2;

import com.influxdb.LogLevel;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfluxdbConfig {
    @Value("${spring.influx.url:''}")
    private String influxDBUrl;
    @Value("${spring.influx.token:''}")
    private String token;

    @Bean
    public InfluxDBClient influxDBClient() {
        InfluxDBClient influxDBClient = InfluxDBClientFactory.create(influxDBUrl, token.toCharArray());
        influxDBClient.setLogLevel(LogLevel.BASIC);
        return influxDBClient;
    }

}
