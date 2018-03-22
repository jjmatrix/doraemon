package org.jmatrix.core.jdbc.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
public class DataSourceConfig {

    @Bean(initMethod = "init", destroyMethod = "close")
    @Qualifier("dataSourceM1")
    public DataSource dataSourceM1() throws SQLException {
        DruidDataSource dataSource = new DruidDataSource();

        dataSource.setName("Druid DataSource M1");
        dataSource.setUrl("jdbc:mysql://127.0.0.1:3306/core?useUnicode=true&characterEncoding=UTF8&autoReconnect=true");
        dataSource.setUsername("root");
        dataSource.setPassword("123456");
        dataSource.setInitialSize(1);
        dataSource.setMinIdle(1);
        dataSource.setMaxActive(10);
        dataSource.setMaxWait(5000);
        dataSource.setTimeBetweenEvictionRunsMillis(60000);
        dataSource.setMinEvictableIdleTimeMillis(300000);
        dataSource.setTimeBetweenLogStatsMillis(300000);
        dataSource.setValidationQuery("/* ping */ SELECT 1");
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);

        /*
        配置监控统计拦截的filters
         */
        dataSource.setFilters("stat");
        dataSource.setConnectionProperties("druid.stat.logSlowSql=true;druid.stat.slowSqlMillis=5000");
        return dataSource;
    }

    @Bean(initMethod = "init", destroyMethod = "close")
    @Qualifier("dataSourceM2")
    public DataSource dataSourceM2() throws SQLException {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setName("Druid DataSource M2");
        dataSource.setUrl("jdbc:mysql://127.0.0.1:33066/core?useUnicode=true&characterEncoding=UTF8&autoReconnect=true");
        dataSource.setUsername("root");
        dataSource.setPassword("123456");
        dataSource.setInitialSize(1);
        dataSource.setMinIdle(1);
        dataSource.setMaxActive(10);
        dataSource.setMaxWait(5000);
        dataSource.setTimeBetweenEvictionRunsMillis(60000);
        dataSource.setMinEvictableIdleTimeMillis(300000);
        dataSource.setTimeBetweenLogStatsMillis(300000);
        dataSource.setValidationQuery("/* ping */ SELECT 1");
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);

        /*
        配置监控统计拦截的filters
         */
        dataSource.setFilters("stat");
        dataSource.setConnectionProperties("druid.stat.logSlowSql=true;druid.stat.slowSqlMillis=5000");
        return dataSource;
    }

}
