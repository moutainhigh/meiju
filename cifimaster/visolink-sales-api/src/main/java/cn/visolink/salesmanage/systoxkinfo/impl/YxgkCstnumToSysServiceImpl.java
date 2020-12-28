package cn.visolink.salesmanage.systoxkinfo.impl;


import cn.visolink.salesmanage.datainterface.dao.DatainterfaceDao;
import cn.visolink.salesmanage.systoxkinfo.YxgkCstnumToSysService;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.wicket.core.dbhelper.sql.DBSQLServiceImpl;
import org.apache.wicket.core.model.OptionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * <p>
 * Message服务实现类
 * </p>
 *
 * @author autoJob
 * @since 2019-09-03
 */
@EnableScheduling
@Service
public class YxgkCstnumToSysServiceImpl implements YxgkCstnumToSysService {

    @Autowired
    private DBSQLServiceImpl dbsqlService;
    //@Autowired
   // DatainterfaceDao datainterfaceDao;

    //@Resource(name="jdbcTemplatexh")
    //private JdbcTemplate jdbcTemplatexh;

    @Override
   // @Scheduled(cron = "0 0 1 * * ?")
    //@Scheduled(fixedRate = 5000)
    public void  selectYxgkCstnum() {
        //循环所有的数据
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE,-1);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String yestoday = sdf.format(calendar.getTime());

            String recordSql="SELECT * FROM vs_yxgk_cstnum where  TheFirstVisitDate="+"\""+yestoday+"\"";
           List<Map<String,Object>> list =null;// jdbcTemplatexh.queryForList(recordSql );
          //  datainterfaceDao.insertcstnum(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
