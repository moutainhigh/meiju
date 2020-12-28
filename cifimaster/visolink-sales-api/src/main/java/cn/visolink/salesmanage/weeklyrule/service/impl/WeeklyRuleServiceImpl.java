package cn.visolink.salesmanage.weeklyrule.service.impl;

import cn.visolink.exception.ResultBody;
import cn.visolink.salesmanage.weeklyrule.dao.WeeklyRuleDao;
import cn.visolink.salesmanage.weeklyrule.service.WeeklyRuleService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class WeeklyRuleServiceImpl implements WeeklyRuleService {
    @Autowired
    WeeklyRuleDao weeklyRuleDao;

    /*
     * 规则表的查找
     * */
    @Override
    public ResultBody WeeklyRuleSelect(Map map){
        PageHelper pageHelper=new PageHelper();

        PageHelper.startPage(Integer.parseInt(map.get("pageNum").toString()), Integer.parseInt(map.get("pageSize").toString()));
        ResultBody<Object> resultBody = new ResultBody<>();
        List<Map> result= weeklyRuleDao.WeeklyRuleSelect();
        PageInfo pageInfo=new PageInfo(result);
        resultBody.setData(pageInfo);
        return resultBody;
    }
    /*
     * 规则表的更新
     * */
    @Override
    public Integer WeeklyRuleUpdate(Map map){
        try {
            String res;
                int hour=8;
            Calendar cal = Calendar.getInstance();



            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.000'Z'");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
          String startTime=map.get("start_time")+"";
            String endTime=map.get("end_time")+"";
            String thisTime=map.get("this_time")+"";

            String windowsStart=map.get("windows_start")+"";
            String windowsEnd=map.get("windows_end")+"";
            /*窗口开始时间和窗口结束时间要么都不填，要么都填*/

            if (map.get("windows_start")!=null && map.get("windows_end")==null){
                return -1;
            }
            if (map.get("windows_end")!=null && map.get("windows_start")==null){
                return -1;
            }

            if(windowsStart.indexOf("Z")>1){
                Date date = sdf.parse(windowsStart);
                cal.setTime(date);
                cal.add(Calendar.HOUR, hour);// 24小时制
                date = cal.getTime();
                String  windows_start = simpleDateFormat.format(date);
                map.put("windows_start",windows_start);
            }

            if(windowsEnd.indexOf("Z")>1){
                Date date = sdf.parse(windowsEnd);
                cal.setTime(date);
                cal.add(Calendar.HOUR, hour);// 24小时制
                date = cal.getTime();
                String  windows_end = simpleDateFormat.format(date);
                map.put("windows_end",windows_end);
            }



            if(startTime.indexOf("Z")>1){
                Date date = sdf.parse(map.get("start_time")+"");
                cal.setTime(date);
                cal.add(Calendar.HOUR, hour);// 24小时制
                date = cal.getTime();
                String  start_time = simpleDateFormat.format(date);
                map.put("start_time",start_time);
            }
            if(endTime.indexOf("Z")>1){
                Date date = sdf.parse(map.get("end_time")+"");
                cal.setTime(date);
                cal.add(Calendar.HOUR, hour);// 24小时制
                date = cal.getTime();
                String  end_time = simpleDateFormat.format(date);
                map.put("end_time",end_time);
            }
            if(thisTime.indexOf("Z")>1){
                Date date = sdf.parse(map.get("this_time")+"");

                cal.setTime(date);
                cal.add(Calendar.HOUR, hour);// 24小时制
                date = cal.getTime();
                String  this_time = simpleDateFormat.format(date);
                map.put("this_time",this_time);
            }
            String yearmonth=map.get("year_month")+"";
            Date date = sdf.parse(map.get("year_month")+"");

            cal.setTime(date);
            cal.add(Calendar.HOUR, hour);// 24小时制
            date = cal.getTime();
            yearmonth = simpleDateFormat.format(date);

            yearmonth=yearmonth.substring(0,yearmonth.indexOf("-")+3);

            map.put("year_month",yearmonth);

            System.out.println(map.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return weeklyRuleDao.WeeklyRuleUpdate(map);
    }
    /*
     * 规则表的删除
     * */
    @Override
    public Integer WeeklyRuleDelete(Map map){
        return weeklyRuleDao.WeeklyRuleDelete(map);
    }
    /*
     * 规则表的插入
     * */
    @Override
    public Integer WeeklyRuleInsert(Map map){

        try {
            String res;
            int hour=8;
            Calendar cal = Calendar.getInstance();

            if (map.get("windows_start")!=null && map.get("windows_end")==null){
                return -1;
            }
            if (map.get("windows_end")!=null && map.get("windows_start")==null){
                return -1;
            }


            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.000'Z'");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String startTime=map.get("start_time")+"";
            String endTime=map.get("end_time")+"";
            String thisTime=map.get("this_time")+"";
            String windowsStart=map.get("windows_start")+"";
            String windowsEnd=map.get("windows_end")+"";
            if(windowsStart.indexOf("Z")>1){
                Date date = sdf.parse(windowsStart);
                cal.setTime(date);
                cal.add(Calendar.HOUR, hour);// 24小时制
                date = cal.getTime();
                String  windows_start = simpleDateFormat.format(date);
                map.put("windows_start",windows_start);
            }

            if(windowsEnd.indexOf("Z")>1){
                Date date = sdf.parse(windowsEnd);
                cal.setTime(date);
                cal.add(Calendar.HOUR, hour);// 24小时制
                date = cal.getTime();
                String  windows_end = simpleDateFormat.format(date);

                map.put("windows_end",windows_end);
            }



            if(startTime.indexOf("Z")>1){
                Date date = sdf.parse(map.get("start_time")+"");
                cal.setTime(date);
                cal.add(Calendar.HOUR, hour);// 24小时制
                date = cal.getTime();
                String  start_time = simpleDateFormat.format(date);
                map.put("start_time",start_time);
            }
            if(endTime.indexOf("Z")>1){
                Date date = sdf.parse(map.get("end_time")+"");
                cal.setTime(date);
                cal.add(Calendar.HOUR, hour);// 24小时制
                date = cal.getTime();
                String  end_time = simpleDateFormat.format(date);
                map.put("end_time",end_time);
            }
            if(thisTime.indexOf("Z")>1){
                Date date = sdf.parse(map.get("this_time")+"");

                cal.setTime(date);
                cal.add(Calendar.HOUR, hour);// 24小时制
                date = cal.getTime();
                String  this_time = simpleDateFormat.format(date);
                map.put("this_time",this_time);
            }
            String yearmonth=map.get("year_month")+"";
            Date date = sdf.parse(map.get("year_month")+"");

            cal.setTime(date);
            cal.add(Calendar.HOUR, hour);// 24小时制
            date = cal.getTime();
            yearmonth = simpleDateFormat.format(date);

            yearmonth=yearmonth.substring(0,yearmonth.indexOf("-")+3);

            map.put("year_month",yearmonth);


        } catch (ParseException e) {
            e.printStackTrace();
        }



        return weeklyRuleDao.WeeklyRuleInsert(map);
    }

}
