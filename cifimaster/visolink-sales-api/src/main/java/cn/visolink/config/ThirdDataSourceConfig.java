//package cn.visolink.config;
//
//import com.alibaba.druid.pool.DruidDataSource;
//import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
//import org.apache.ibatis.session.SqlSessionFactory;
//import org.mybatis.spring.SqlSessionFactoryBean;
//import org.mybatis.spring.SqlSessionTemplate;
//import org.mybatis.spring.annotation.MapperScan;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.jdbc.DataSourceBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
//import org.springframework.jdbc.datasource.DataSourceTransactionManager;
//
//import javax.sql.DataSource;
//
///**
// * 多数据源配置
// * @author wcl
// */
//@Configuration
//@MapperScan(basePackages = "cn.visolink.common.mapper",sqlSessionFactoryRef = "thirdSqlSessionFactory")
//public class ThirdDataSourceConfig {
//    @Bean(name = "thirdDataSource")
//    @Qualifier("thirdDataSource")
//    @ConfigurationProperties(prefix = "spring.datasource.slave")
//    public DataSource thirdDataSource() {
//        return DruidDataSourceBuilder.create().build();
//    }
//
//    @Bean(name = "thirdSqlSessionFactory")
//    public SqlSessionFactory thirdSqlSessionFactory(@Qualifier("thirdDataSource") DataSource dataSource) throws Exception {
//        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
//        factoryBean.setDataSource(dataSource);
//        factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml"));
//        return factoryBean.getObject();
//    }
//    @Bean(name="thirdTransactionManager")
//    public DataSourceTransactionManager thirdTransactionManager(@Qualifier("thirdDataSource") DataSource dataSource){
//        return  new DataSourceTransactionManager(dataSource);
//    }
//
//    @Bean(name = "thirdSqlSessionTemplate")
//    public SqlSessionTemplate thirdSqlSessionTemplate(@Qualifier("thirdSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
//        return new SqlSessionTemplate(sqlSessionFactory);
//    }
//
//
//
//}
