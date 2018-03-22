package org.jmatrix.core.jdbc.config;

import com.dangdang.ddframe.rdb.sharding.spring.datasource.SpringShardingDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.jmatrix.core.jdbc.common.MybatisEntity;
import org.jmatrix.core.jdbc.common.MybatisMapperInterface;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ShardingConfig.class)
public class MybatisConfig {

    private static final Logger logger = LoggerFactory.getLogger(MybatisConfig.class);

    public static final String ENV_PRODUCTION = "production";

    @Bean
    public TransactionFactory transactionFactory() {
        TransactionFactory transactionFactory = new SpringManagedTransactionFactory();
        return transactionFactory;
    }

    @Bean
    public Environment mybatisEnvironment(@Qualifier("shardingDataSource") SpringShardingDataSource shardingDataSource) {
        return new Environment(ENV_PRODUCTION, transactionFactory(), shardingDataSource);
    }

    @Bean
    @Autowired
    public org.apache.ibatis.session.Configuration configuration(Environment environment) {
        logger.info("mybaits confiuration");
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration(environment);
        configuration.getTypeAliasRegistry().registerAlias("org.jmatrix.core.jdbc.domain", MybatisEntity.class);
        configuration.setCacheEnabled(false);
        configuration.setLogPrefix("mybatis_dao.");
        return configuration;
    }

    @Bean
    @Autowired
    public SqlSessionFactory sqlSessionFactory(org.apache.ibatis.session.Configuration configuration) {
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
        return sqlSessionFactory;
    }

    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setMarkerInterface(MybatisMapperInterface.class);
        mapperScannerConfigurer.setBasePackage("org.jmatrix");
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
        return mapperScannerConfigurer;
    }
}
