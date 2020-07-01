package com.github.wxiaoqi.security.gate.config;

import com.codingapi.tx.datasource.relational.LCNTransactionDataSource;
import com.github.trang.druid.autoconfigure.DruidDataSourceConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

/**
 * 代理数据源
 *
 * @author ace
 * @version 2018/1/18.
 */
@AutoConfigureAfter(DruidDataSourceConfiguration.class)
@Configuration
public class TxConfiguration {

    @Bean("lcnTransactionDataSource")
    @ConfigurationProperties("spring.datasource.druid")
    @Primary
    public LCNTransactionDataSource dataSource(Environment env) {
        LCNTransactionDataSource dataSourceProxy = new LCNTransactionDataSource();
        dataSourceProxy.setDataSource(createDataSource(env));
        dataSourceProxy.setMaxCount(10);
        return dataSourceProxy;
    }

    public DataSource createDataSource(Environment env) {
        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.username(env.getProperty("spring.datasource.username"));
        dataSourceBuilder.password(env.getProperty("spring.datasource.password"));
        dataSourceBuilder.url(env.getProperty("spring.datasource.url"));
        dataSourceBuilder.driverClassName(env.getProperty("spring.datasource.driver-class-name"));
        return dataSourceBuilder.build();
    }
}
