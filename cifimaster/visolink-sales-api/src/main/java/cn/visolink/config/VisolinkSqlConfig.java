package cn.visolink.config;

import org.apache.wicket.core.dbhelper.api.FrameServiceApiImpl;
import org.apache.wicket.core.dbhelper.sql.DBSQLServiceImpl;
import org.apache.wicket.core.dbhelper.sql.FileMonitor;
import org.apache.wicket.core.dbhelper.sql.InitSQLListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.ResourceUtils;

import javax.sql.DataSource;
import java.net.URLDecoder;

/**
 * @Author wcl
 * @Description //TODO
 * @Date 2019/7/4 11:20
 * @Version 1.0
 **/
@Configuration
public class VisolinkSqlConfig {





    @Autowired
    DataSource dataSource;
    @Bean(name = "initBizSQL")
    public FileMonitor fileMonitor() throws Exception {
        FileMonitor fileMonitor = new FileMonitor();
        String dataSqlUrl = URLDecoder.decode(ResourceUtils.getURL("classpath:").getPath(),"utf-8");
        fileMonitor.setRootDir(dataSqlUrl+"/mapper/wicket/");
        fileMonitor.setIntervalsec(2);
        return  fileMonitor;
    }


    @Bean(name = "sqlListener")
    public InitSQLListener sqlListener() throws Exception {
        InitSQLListener sqlListener = new InitSQLListener();
        sqlListener.setFm(fileMonitor());
        return sqlListener;
    }



    @Bean(name = "jdbcTemplate")
    @Primary
    public JdbcTemplate jdbcTemplate(){
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSource);
        return jdbcTemplate;
    }

    @Bean(name = "dbsqlService")
    @Lazy(value = false)
    public DBSQLServiceImpl dbsqlService(){
        DBSQLServiceImpl dbsqlService = new DBSQLServiceImpl();
        dbsqlService.setDS(dataSource);
        dbsqlService.setJdbcTemplate(jdbcTemplate());
        return dbsqlService;
    }

    @Bean(name = "frameServiceApi")
    public FrameServiceApiImpl frameServiceApi(){
        FrameServiceApiImpl frameServiceApi = new FrameServiceApiImpl();
        frameServiceApi.setDbsqlService(dbsqlService());
        return frameServiceApi;
    }




}
