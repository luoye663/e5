package io.qyi.e5.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program: demo
 * @description:
 * @author: 落叶随风
 * @create: 2019-10-22 04:06
 **/
@Configuration
@MapperScan(basePackages = {"io.qyi.e5.*.mapper"})
public class MybatisPlusConfig {


    /**
     * 分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }
}
