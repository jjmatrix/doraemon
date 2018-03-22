package org.jmatrix.core.jdbc.config;

import com.dangdang.ddframe.rdb.sharding.config.ShardingPropertiesConstant;
import com.dangdang.ddframe.rdb.sharding.config.common.api.config.ShardingRuleConfig;
import com.dangdang.ddframe.rdb.sharding.config.common.api.config.StrategyConfig;
import com.dangdang.ddframe.rdb.sharding.spring.datasource.SpringShardingDataSource;
import org.jmatrix.core.jdbc.shard.DefaultTableShardingAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by matrix on 2017/9/19.
 */
@Configuration
@DependsOn(value = {"dataSourceM1", "dataSourceM2"})
public class ShardingConfig implements ApplicationContextAware {

    private final Logger logger = LoggerFactory.getLogger(ShardingConfig.class);

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Bean
    public StrategyConfig tableStrategyConfig() {
        StrategyConfig tableStrategyConfig = new StrategyConfig();
        tableStrategyConfig.setShardingColumns("order_no");
        tableStrategyConfig.setAlgorithmClassName(DefaultTableShardingAlgorithm.class.getName());
        return tableStrategyConfig;
    }

    @Bean
    @Autowired
    @Qualifier("shardingDataSource")
    @Primary
    public SpringShardingDataSource shardingDataSource(@Qualifier("tableStrategyConfig") StrategyConfig tableStrategyConfig) {
        SpringShardingDataSource springShardingDataSource = null;
        ShardingRuleConfig shardingRuleConfig = new ShardingRuleConfig();

        //default db
        Map<String, DataSource> dataSourceMap = new HashMap<>();
        dataSourceMap.put("dataSourceM1", (DataSource) applicationContext.getBean("dataSourceM1"));
        dataSourceMap.put("dataSourceM2", (DataSource) applicationContext.getBean("dataSourceM2"));
        shardingRuleConfig.setDataSource(dataSourceMap);

        //db strategy
        StrategyConfig dbStrategyConfig = new StrategyConfig();
        dbStrategyConfig.setShardingColumns("none");
        dbStrategyConfig.setAlgorithmClassName("com.dangdang.ddframe.rdb.sharding.api.strategy.database.NoneDatabaseShardingAlgorithm");
        shardingRuleConfig.setDefaultDatabaseStrategy(dbStrategyConfig);

        //table strategy
//        Map<String, TableRuleConfig> tables = new HashMap<>();
//        shardingRuleConfig.setTables(tables);
//        TableRuleConfig tableRuleConfig = new TableRuleConfig();
//        tableRuleConfig.setActualTables("order_${1..3}");
//        tableRuleConfig.setTableStrategy(tableStrategyConfig);
//        tables.put("order", tableRuleConfig);

        try {
            Properties properties = new Properties();
            properties.put(ShardingPropertiesConstant.SQL_SHOW.getKey(), "true");
            springShardingDataSource = new SpringShardingDataSource(shardingRuleConfig, properties);
        } catch (Exception e) {
            logger.error("create spring data source failed.", e);
        }
        return springShardingDataSource;
    }

    public static void main(String[] args) {
        System.out.println(DefaultTableShardingAlgorithm.class.getName());
        System.out.println(DefaultTableShardingAlgorithm.class.getCanonicalName());
        System.out.println(DefaultTableShardingAlgorithm.class.getSimpleName());
    }
}
