package cn.visolink.utils;

import cn.visolink.common.bean.VisolinkResultBody;
import cn.visolink.exception.ResultBody;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author sjl 根据时间段拆分周数
 * @Created date 2020/7/16 11:13 上午
 */
@Component
@Data
public class WeekSplitUtil {

    public static ResultBody getWeekSplitList(String start_time, String end_time){
        VisolinkResultBody<Object> response = new VisolinkResultBody<>();
        // 1.开始时间 2019-06-09 13:16:04
        try {
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");//要转换的日期格式，根据实际调整""里面内容
            if ("".equals(start_time) || "null".equals(start_time)) {
                return ResultBody.error(-1006,"开始时间不能为空!");
            }
            long startTime = sdf2.parse(start_time).getTime();
            long endTime = sdf2.parse(end_time).getTime();

            if (startTime>=endTime) {
                return ResultBody.error(-1007,"抢开日期不能小于或等于顶设2开始时间!");
            }
            // 3.开始时间段区间集合
            List<Long> beginDateList = new ArrayList<Long>();
            // 4.结束时间段区间集合
            List<Long> endDateList = new ArrayList<Long>();
            // 5.调用工具类
            WeekUtil.getIntervalTimeByWeek(startTime, endTime, beginDateList, endDateList);
            // 6.打印输出
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
            String startFormat = sdf.format(sdf2.parse(start_time));
            String endFormat = sdf.format(sdf2.parse(end_time));
            List<Map> weekList = new ArrayList<>();
            Map<Object, Object> maps = new HashMap<>();
            maps.put("week", "完整波段");
            maps.put("start_time",startFormat);
            maps.put("end_time",endFormat);
            maps.put("day_date",startFormat+"-"+endFormat);
            weekList.add(maps);
            for (int i = 0; i < endDateList.size(); i++) {
                Long beginStr = beginDateList.get(i);
                Long endStr = endDateList.get(i);
                String begin1 = sdf.format(new Date(beginStr));
                String end1 = sdf.format(new Date(endStr));
                Map<Object, Object> weekMap = new HashMap<>();
                weekMap.put("week", "第" +int2chineseNum((i + 1)) + "周");
                weekMap.put("start_time", begin1);
                weekMap.put("end_time", end1);
                weekMap.put("day_date", begin1 + "-" + end1);
                weekList.add(weekMap);
            }
            return ResultBody.success(weekList);
        }catch (Exception e){
            e.printStackTrace();
            return ResultBody.error(-1085,"周拆分数据获取失败，请联系管理员");
        }

    }
    public static String int2chineseNum(int src) {
        final String num[] = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
        final String unit[] = {"", "十", "百", "千", "万", "十", "百", "千", "亿", "十", "百", "千"};
        String dst = "";
        int count = 0;
        while (src > 0) {
            dst = (num[src % 10] + unit[count]) + dst;
            src = src / 10;
            count++;
        }
        return dst.replaceAll("零[千百十]", "零").replaceAll("一十", "十").replaceAll("零+万", "万")
                .replaceAll("零+亿", "亿").replaceAll("亿万", "亿零")
                .replaceAll("零+", "零").replaceAll("零$", "");

    }
}
