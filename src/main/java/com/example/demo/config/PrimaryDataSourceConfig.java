package com.example.demo.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Created by sam on 2017/7/30.
 */

@Configuration
@MapperScan(value = "com.example.demo.mapper.primary",sqlSessionFactoryRef = "primarySqlSessionFactory")
public class PrimaryDataSourceConfig {

    @Bean(name = "primaryDataSource")
    @Primary
    public DataSource primaryDataSource(){
        System.out.println("-------primary dataSource-------init");
        AtomikosDataSourceBean dataSourceBean = new AtomikosDataSourceBean();
        dataSourceBean.setXaDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlXADataSource");
        Properties pts = new Properties();
        pts.setProperty("url","jdbc:mysql://localhost:3306/studentdb?useUnicode=true&characterEncoding=utf-8");
        pts.setProperty("user","root");
        pts.setProperty("password","huguiqi");
        dataSourceBean.setXaProperties(pts);
        dataSourceBean.setPoolSize(1);
        dataSourceBean.setMaxPoolSize(3);
        return dataSourceBean;
    }


//第一种方式
//    @Bean(name = "primaryDataSource")
//    @Primary
//    @ConfigurationProperties(prefix = "primary.datasource")
//    public DataSource primaryDataSource(){
//        System.out.println("-------primary dataSource-------init");
//        return DataSourceBuilder.create().build();
//    }

    //第二种方式
//    @Bean(name = "primaryDataSource")
//    @Primary
//    public DataSource primaryDataSource(){
//        System.out.println("-------primary dataSource-------init");
//        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
//        EmbeddedDatabase db = builder
//                .setType(EmbeddedDatabaseType.H2) //等价于设置url=jdbc:h2:mem:testdb
//                .addScript("primarySchema.sql")
//                .build();
//        return db;
//    }





    @Bean
    public DataSourceInitializer primaryInitSql(@Qualifier("primaryDataSource") DataSource dataSource){

        return init(dataSource,"primarySchema");
    }



    private DataSourceInitializer init(DataSource dataSource,String schameName){
        DataSourceInitializer dsi = new DataSourceInitializer();
        dsi.setDataSource(dataSource);
        dsi.setDatabasePopulator(new ResourceDatabasePopulator(new ClassPathResource(schameName+".sql")));
        return dsi;
    }



    @Bean(name = "primarySqlSessionFactory")
    @Primary
    public SqlSessionFactory sqlSessionFactory(@Qualifier("primaryDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        return sessionFactory.getObject();
    }


}
