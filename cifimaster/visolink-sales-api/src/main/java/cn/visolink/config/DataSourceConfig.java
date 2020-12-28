package cn.visolink.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
public class DataSourceConfig {

    /**
     * 初始化连接数
     */
    private final int INITIAL_SIZE = 3;
    /**
     * 最小连接数
     */
    private final int MIN_IDLE = 3;
    /**
     * 最大连接数
     */
    private final int MAX_ACTIVE = 10;
    /**
     * 检测空闲连接的时间间隔，1分钟
     */
    private final int TIME_BETWEEN_EVICTION_RUNSMILLIS = 60000;
    /**
     * 连接最小生存时间，5分钟
     */
    private final int MIN_EVICTABLEIDLE_TIMEMILLIS = 300000;
    /**
     * 连接最大生存时间，25分钟
     */
    private final int MAX_EVICTABLEIDLE_TIMEMILLIS = 1500000;


    @Bean(name = "dataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.druid")
    public DataSource dataSource() {
        return DataSourceBuilder.create().type(com.alibaba.druid.pool.DruidDataSource.class).build();
    }

    @Bean(name = "jdbcTemplatewjwj")
    public JdbcTemplate jdbcTemplatewjwj(@Qualifier("dataSource") DataSource dataSourcedb4) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSourcedb4);
        return jdbcTemplate;
    }

    @Bean(name = "dataSourcedbmy")
    public DruidDataSource dataSourcedbmy(@Value("${mingyuan.driverClassName}") String driverClassName,
                                          @Value("${mingyuan.url}") String url,
                                          @Value("${mingyuan.username}") String username,
                                          @Value("${mingyuan.password}") String password) throws SQLException {
        return getDruidDataSource(driverClassName, url, username, password);
    }

    /**
     * 项目启动时，初始化数据源
     *
     * @param driverClassName driverClassName
     * @param url             url
     * @param username        username
     * @param password        password
     * @return return
     * @throws SQLException
     */
    private DruidDataSource getDruidDataSource(@Value("${mingyuan.driverClassName}") String driverClassName, @Value("${mingyuan.url}") String url, @Value("${mingyuan.username}") String username, @Value("${mingyuan.password}") String password) throws SQLException {
        DruidDataSource dataSourcedbmy = DataSourceBuilder.create().type(DruidDataSource.class)
                .driverClassName(driverClassName)
                .url(url)
                .username(username)
                .password(password)
                .build();

        dataSourcedbmy.setInitialSize(INITIAL_SIZE);
        dataSourcedbmy.setMinIdle(MIN_IDLE);
        dataSourcedbmy.setMaxActive(MAX_ACTIVE);
        dataSourcedbmy.setTimeBetweenEvictionRunsMillis(TIME_BETWEEN_EVICTION_RUNSMILLIS);
        dataSourcedbmy.setMinEvictableIdleTimeMillis(MIN_EVICTABLEIDLE_TIMEMILLIS);
        dataSourcedbmy.setMaxEvictableIdleTimeMillis(MAX_EVICTABLEIDLE_TIMEMILLIS);
        dataSourcedbmy.setValidationQuery("select 1");
        dataSourcedbmy.setTestWhileIdle(true);
        dataSourcedbmy.setTestOnBorrow(false);
        dataSourcedbmy.setTestOnReturn(false);
        dataSourcedbmy.init();
        return dataSourcedbmy;
    }

    @Bean(name = "jdbcTemplatemy")
    public JdbcTemplate jdbcTemplatemy(@Qualifier("dataSourcedbmy") DataSource dataSourcedb4) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSourcedb4);
        return jdbcTemplate;

    }

    @Bean(name = "dataSourcedbmy352")
    public DataSource dataSourcedbmy352(@Value("${mingyuan352.driverClassName}") String driverClassName,
                                        @Value("${mingyuan352.url}") String url,
                                        @Value("${mingyuan352.username}") String username,
                                        @Value("${mingyuan352.password}") String password) {
        return DataSourceBuilder.create().type(com.alibaba.druid.pool.DruidDataSource.class)
                .driverClassName(driverClassName)
                .url(url)
                .username(username)
                .password(password)
                .build();
    }

    @Bean(name = "jdbcTemplatemy352")
    public JdbcTemplate jdbcTemplatemy352(@Qualifier("dataSourcedbmy352") DataSource dataSourcedb4) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSourcedb4);
        return jdbcTemplate;

    }

    @Bean(name = "dataSourcedbgxc")
    public DruidDataSource dataSourcedbgxc(@Value("${gongxiaocun.driverClassName}") String driverClassName,
                                           @Value("${gongxiaocun.url}") String url,
                                           @Value("${gongxiaocun.username}") String username,
                                           @Value("${gongxiaocun.password}") String password) throws SQLException {
        return getDruidDataSource(driverClassName, url, username, password);
    }

    @Bean(name = "jdbcTemplategxc")
    public JdbcTemplate jdbcTemplategxc(@Qualifier("dataSourcedbgxc") DataSource dataSourcedb6) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSourcedb6);
        return jdbcTemplate;

    }

    @Bean(name = "dataSourcedbNOS")
    public DataSource dataSourcedbcaopan(@Value("${nos.driverClassName}") String driverClassName,
                                         @Value("${nos.url}") String url,
                                         @Value("${nos.username}") String username,
                                         @Value("${nos.password}") String password) {
        return DataSourceBuilder.create().type(com.alibaba.druid.pool.DruidDataSource.class)
                .driverClassName(driverClassName)
                .url(url)
                .username(username)
                .password(password)
                .build();
    }

    @Bean(name = "jdbcTemplateNOS")
    public JdbcTemplate jdbcTemplateNOS(@Qualifier("dataSourcedbNOS") DataSource dataSourcedb7) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSourcedb7);
        return jdbcTemplate;

    }


    @Bean(name = "dataSourcedbmingyuancost")
    public DataSource dataSourcedbmingyuancost(@Value("${mingyuancost.driverClassName}") String driverClassName,
                                               @Value("${mingyuancost.url}") String url,
                                               @Value("${mingyuancost.username}") String username,
                                               @Value("${mingyuancost.password}") String password) {
        return DataSourceBuilder.create().type(com.alibaba.druid.pool.DruidDataSource.class)
                .driverClassName(driverClassName)
                .url(url)
                .username(username)
                .password(password)
                .build();
    }

    @Bean(name = "jdbcTemplatemingyuancost")
    public JdbcTemplate jdbcTemplatemingyuancost(@Qualifier("dataSourcedbmingyuancost") DataSource dataSourcedb7) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSourcedb7);
        return jdbcTemplate;

    }


    @Bean(name = "dataSourcedbXUKE")
    public DataSource dataSourcedbXUKE(@Value("${xuke.driverClassName}") String driverClassName,
                                       @Value("${xuke.url}") String url,
                                       @Value("${xuke.username}") String username,
                                       @Value("${xuke.password}") String password) {
        return DataSourceBuilder.create().type(com.alibaba.druid.pool.DruidDataSource.class)
                .driverClassName(driverClassName)
                .url(url)
                .username(username)
                .password(password)
                .build();
    }

    @Bean(name = "jdbcTemplateXUKE")
    public JdbcTemplate jdbcTemplateXUKE(@Qualifier("dataSourcedbXUKE") DataSource dataSourcedb) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSourcedb);
        return jdbcTemplate;

    }
}
