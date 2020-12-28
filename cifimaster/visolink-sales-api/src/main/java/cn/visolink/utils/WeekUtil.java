package cn.visolink.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author sjl
 * @Created date 2020/3/9 1:34 上午
 */
public class WeekUtil {
    public static void getIntervalTimeByWeek(Long startTime, Long endTime, List<Long> beginDateList, List<Long> endDateList) {
        Date startDate = new Date(startTime);
        Date endDate = new Date(endTime);
        SimpleDateFormat sdw = new SimpleDateFormat("E");
        Calendar calendar = Calendar.getInstance();
        String begin = sdw.format(startDate);
        calendar.setTime(startDate);
        beginDateList.add(calendar.getTimeInMillis());
        if ("星期一".equals(begin)||"Mon".equals(begin)||"星期八".equals(begin) ) {
            addTimeStamp(beginDateList, endDateList, startDate, endDate, sdw, calendar);
        } else {
            if ("星期日".equals(sdw.format(startDate))||"Sun".equals(sdw.format(startDate))||"星期七".equals(sdw.format(startDate))) {
                Calendar special = Calendar.getInstance();
                special.setTime(startDate);
                special.set(Calendar.HOUR_OF_DAY, 23);
                special.set(Calendar.MINUTE, 59);
                special.set(Calendar.SECOND, 59);
                endDateList.add(special.getTime().getTime());
            }
            addTimeStamp(beginDateList, endDateList, startDate, endDate, sdw, calendar);
        }
    }

    private static void addTimeStamp(List<Long> beginDateList, List<Long> endDateList, Date startDate, Date endDate, SimpleDateFormat sdw, Calendar calendar) {
        while (startDate.getTime() < endDate.getTime()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            startDate = calendar.getTime();
            if ("星期一".equals(sdw.format(startDate))||"Mon".equals(sdw.format(startDate))) {
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                beginDateList.add(calendar.getTimeInMillis());
            } else if ("星期日".equals(sdw.format(startDate))||"Sun".equals(sdw.format(startDate)) || startDate.getTime() >= endDate.getTime()||"星期七".equals(sdw.format(startDate))) {
                if (startDate.getTime() <= endDate.getTime()) {
                    calendar.set(Calendar.HOUR_OF_DAY, 23);
                    calendar.set(Calendar.MINUTE, 59);
                    calendar.set(Calendar.SECOND, 59);
                    endDateList.add(calendar.getTimeInMillis());
                } else {
                    endDateList.add(endDate.getTime());
                }
            }
        }
    }
}
