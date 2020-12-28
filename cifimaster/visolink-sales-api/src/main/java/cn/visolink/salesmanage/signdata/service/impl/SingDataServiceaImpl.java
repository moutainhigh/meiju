package cn.visolink.salesmanage.signdata.service.impl;

import cn.visolink.salesmanage.signdata.dao.SingDataMapper;
import cn.visolink.salesmanage.signdata.service.SingDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sjl
 * @Created date 2019/11/18 7:58 下午
 */
@Service
public class SingDataServiceaImpl implements SingDataService {

    @Autowired
    private SingDataMapper singDataMapper;
    @Override
    public Map getSingMoneyData(Map map) {

        Map<Object, Object> resultMap = new HashMap<>();
        //获取周度数据
        List<Map> singWeekData = singDataMapper.getSingWeekData(map);
        List<Map> vistiCountDataWeek = singDataMapper.getVistiCountData(map);
        /*
         * 求出年和月
         * */
        String months=map.get("startTime").toString();

        Integer trimyear=Integer.parseInt(months.substring(0,months.indexOf("-")));
        Integer  trimmonth=Integer.parseInt(months.substring(months.indexOf("-")+1,months.indexOf("-")+3));
        /*
         * 算出当前月第一天
         * */
        String currentmonths=trimyear+"-"+trimmonth+"-01";
        map.put("startTime",currentmonths);
        Date date=new Date();
        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

       // map.put("endTime",sf.format(date));
        List<Map> singMonthData = singDataMapper.getSingMonthData(map);
        List<Map> vistiCountDataMonth = singDataMapper.getVistiCountData(map);

        resultMap.put("weekData",singWeekData);
        resultMap.put("monthData",singMonthData);
        resultMap.put("vistiCountDataWeek",vistiCountDataWeek);
        resultMap.put("vistiCountDataMonth",vistiCountDataMonth);
        return resultMap;
    }
}
