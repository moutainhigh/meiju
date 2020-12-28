package cn.visolink.utils;


import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

/**
 * 字符串工具类
 */
public class DateUtil {
    //获取当月最后一天
    public static String getLastDayOfMonth(String yearMonth) {
        int year = Integer.parseInt(yearMonth.split("-")[0]);  //年
        int month = Integer.parseInt(yearMonth.split("-")[1]); //月
        Calendar cal = Calendar.getInstance();
        // 设置年份
        cal.set(Calendar.YEAR, year);
        // 设置月份
        cal.set(Calendar.MONTH, month - 1);
        // 获取某月最大天数
        int lastDay = cal.getActualMaximum(Calendar.DATE);
        // 设置日历中月份的最大天数
        cal.set(Calendar.DAY_OF_MONTH, lastDay);
        // 格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(cal.getTime());
    }
    //获取当月第一天
    public static String getFirstDay(String yearMonth) {
        int year = Integer.parseInt(yearMonth.split("-")[0]);  //年
        int month = Integer.parseInt(yearMonth.split("-")[1]); //月
        Calendar cal = Calendar.getInstance();
        // 设置年份
        cal.set(Calendar.YEAR, year);
        // 设置月份
        cal.set(Calendar.MONTH, month - 1);
        // 获取某月最大天数
        int lastDay = cal.getActualMinimum(Calendar.DATE);
        // 设置日历中月份的最大天数
        cal.set(Calendar.DAY_OF_MONTH, lastDay);
        // 格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(cal.getTime());
    }


    public static List getWeek(String thisTime) {
        // Java8  LocalDate
        String yearMonth=thisTime.substring(0,7);
        LocalDate date = LocalDate.parse(thisTime);
        // 该月第一天
        LocalDate firstDay = date.with(TemporalAdjusters.firstDayOfMonth());
        // 该月最后一天
        LocalDate lastDay = date.with(TemporalAdjusters.lastDayOfMonth());
        // 该月的第一个周一
        LocalDate start = date.with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));
        List<Map> list = new ArrayList<>();
        // 处理每个月的1号不是周一的情况
        int weekNum=0;
        if (!firstDay.equals(start)) {
            weekNum++;
            Map NowDate=new HashMap();
            NowDate.put("thisYearMonth",yearMonth);
            NowDate.put("startTime",firstDay.toString());
            NowDate.put("endTime",start.plusDays(-1).toString());
            NowDate.put("weekNum",weekNum);
            Period next = Period.between(firstDay,start.plusDays(-1));
            NowDate.put("day_num", next.getDays()+1);
            list.add(NowDate);
        }
        LocalDate endTimeL=null;
        while (start.isBefore(lastDay)) {
            weekNum++;
            String endTime="";
            LocalDate temp = start.plusDays(6);
            if (temp.isBefore(lastDay)) {
                endTime=temp.toString();
                endTimeL=temp;
            } else {
                endTime=lastDay.toString();
                endTimeL=lastDay;
            }
            Map NowDate=new HashMap();
            NowDate.put("thisYearMonth",yearMonth);
            NowDate.put("startTime",start.toString());
            NowDate.put("endTime",endTime);
            NowDate.put("weekNum",weekNum);
            Period next = Period.between(start,endTimeL);
            NowDate.put("day_num", next.getDays()+1);
            list.add(NowDate);
            start = start.plusWeeks(1);
            //防止死循环
            if(weekNum==10){
                return list;
            }
        }
        return list;
    }

    /**
     * 以季度分割时间段
     * 此处季度是以 12-2月   3-5月   6-8月  9-11月 划分
     * @param startTime     开始时间戳(毫秒)
     * @param endTime       结束时间戳(毫秒)
     * @param beginDateList 开始段时间戳 和 结束段时间戳 一一对应
     * @param endDateList   结束段时间戳 和 开始段时间戳 一一对应
     */
    public static void getIntervalTimeByQuarter(Long startTime, Long endTime, List<Long> beginDateList, List<Long> endDateList) {
        Date startDate = new Date(startTime);
        Date endDate = new Date(endTime);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        int month = calendar.get(Calendar.MONTH) + 1;
        switch (month) {
            case 12:
            case 3:
            case 6:
            case 9:
                addTime(beginDateList, endDateList, startDate, endDate, calendar, 3);
                break;
            case 1:
            case 4:
            case 7:
            case 10:
                addTime(beginDateList, endDateList, startDate, endDate, calendar, 2);
                break;
            case 2:
            case 5:
            case 8:
            case 11:
                addTime(beginDateList, endDateList, startDate, endDate, calendar, 1);
                break;
        }
    }

    private static void addTime(List<Long> beginDateList, List<Long> endDateList, Date startDate, Date endDate, Calendar calendar, int i) {
        beginDateList.add(startDate.getTime());
        calendar.add(Calendar.MONTH, i);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.DATE, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 13);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        if (calendar.getTimeInMillis() > endDate.getTime()) {
            endDateList.add(endDate.getTime());

        } else {
            endDateList.add(calendar.getTimeInMillis());
            while (calendar.getTimeInMillis() < endDate.getTime()) {
                calendar.add(Calendar.DATE, 1);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                beginDateList.add(calendar.getTimeInMillis());
                calendar.add(Calendar.MONTH, 3);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.add(Calendar.DATE, -1);
                calendar.set(Calendar.HOUR_OF_DAY, 13);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                if (calendar.getTimeInMillis() < endDate.getTime()) {
                    endDateList.add(calendar.getTimeInMillis());
                } else {
                    endDateList.add(endDate.getTime());
                }
            }
        }
    }

    /**
     * 以周分割时间段
     *
     * @param startTime     开始时间戳(毫秒)
     * @param endTime       结束时间戳(毫秒)
     * @param beginDateList 开始段时间戳 和 结束段时间戳 一一对应
     * @param endDateList   结束段时间戳 和 开始段时间戳 一一对应
     */
    public static void getIntervalTimeByWeek(Long startTime, Long endTime, List<Long> beginDateList, List<Long> endDateList) {
        Date startDate = new Date(startTime);
        Date endDate = new Date(endTime);
        SimpleDateFormat sdw = new SimpleDateFormat("E");
        Calendar calendar = Calendar.getInstance();
        String begin = sdw.format(startDate);
        calendar.setTime(startDate);
        beginDateList.add(calendar.getTimeInMillis());
        if ("星期一".equals(begin)|| "Mon".equals(begin)) {
            addTimeStamp(beginDateList, endDateList, startDate, endDate, sdw, calendar);
        } else {
            if ("星期日".equals(sdw.format(startDate))|| "星期七".equals(sdw.format(startDate))|| "Sun".equals(sdw.format(startDate))) {
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
            if ("星期一".equals(sdw.format(startDate))|| "Mon".equals(sdw.format(startDate))) {
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                beginDateList.add(calendar.getTimeInMillis());
            } else if ("Sun".equals(sdw.format(startDate)) ||"星期七".equals(sdw.format(startDate)) ||"星期日".equals(sdw.format(startDate)) || startDate.getTime() >= endDate.getTime()) {
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

    /**
     * 按照月份分割一段时间
     *
     * @param startTime     开始时间戳(毫秒)
     * @param endTime       结束时间戳(毫秒)
     * @param beginDateList 开始段时间戳 和 结束段时间戳 一一对应
     * @param endDateList   结束段时间戳 和 开始段时间戳 一一对应
     */
    public static void getIntervalTimeByMonth(Long startTime, Long endTime, List<Long> beginDateList, List<Long> endDateList) {
        Date startDate = new Date(startTime);
        Date endDate = new Date(endTime);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        beginDateList.add(calendar.getTimeInMillis());
        while (calendar.getTimeInMillis() < endDate.getTime()) {
            calendar.add(Calendar.MONTH, 1);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.add(Calendar.DATE, -1);
            calendar.set(Calendar.HOUR_OF_DAY, 13);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            if(calendar.getTimeInMillis() < endDate.getTime()){
                endDateList.add(calendar.getTimeInMillis());
            } else {
                endDateList.add(endDate.getTime());
                break;
            }
            calendar.add(Calendar.DATE, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            beginDateList.add(calendar.getTimeInMillis());
        }
    }

    /**
     * 按照年份分割月份
     *
     * @param startTime     开始时间戳(毫秒)
     * @param endTime       结束时间戳(毫秒)
     */
    public static List getMonthByYear(String startTime, String endTime) {
        int addYear=Integer.parseInt(startTime.substring(0,4));
        int nowYear=Integer.parseInt(endTime.substring(0,4));
        List res=new ArrayList();
        //设置时间格式
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat yearformat = new SimpleDateFormat("yyyy");
        SimpleDateFormat monthformat = new SimpleDateFormat("MM");
        Calendar ca = Calendar.getInstance();
        //当前月
        int nowMonth = Integer.parseInt(endTime.substring(5,7));
        //获得实体类
        ca.set(Calendar.YEAR, addYear);
        //设置月份
        int startMonth=Integer.parseInt(startTime.substring(5,7))-2;
        ca.set(Calendar.MONTH,startMonth);
        while (addYear <= nowYear) {
            ca.add(Calendar.MONTH, 1);
            //设置最后一天
            ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
            //最后一天格式化
            String lastDay = format.format(ca.getTime());
            //累计年度
            addYear = Integer.parseInt(yearformat.format(ca.getTime()));
            if (addYear > nowYear) {
                return null;
            }
            int year = ca.get(Calendar.YEAR);
            // 获取月，这里需要需要月份的范围为0~11，因此获取月份的时候需要+1才是当前月份值
            int month = ca.get(Calendar.MONTH) + 1;

            String monthAdd = "";
            if (month < 10) {
                monthAdd = "0" + month;
            } else {
                monthAdd = month + "";
            }
            Map paramMap = new HashMap();
            paramMap.put("monthStartTime", year + "-" + monthAdd + "-01 00:00:00");
            paramMap.put("monthSendTime", lastDay + " 23:59:59");
            paramMap.put("months", year + "-" + monthAdd);
            res.add(paramMap);
            //当前年月
            if (addYear == nowYear && month == nowMonth) {
                addYear++;
            }
        }
        Collections.reverse(res);
        return res;
    }


    static class Week {

        String weekNum;
        int year;
        String weekBegin;
        String weekEnd;

        public String getWeekNum() {
            return weekNum;
        }

        public void setWeekNum(String weekNum) {
            this.weekNum = weekNum;
        }

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public String getWeekBegin() {
            return weekBegin;
        }

        public void setWeekBegin(String weekBegin) {
            this.weekBegin = weekBegin;
        }

        public String getWeekEnd() {
            return weekEnd;
        }

        public void setWeekEnd(String weekEnd) {
            this.weekEnd = weekEnd;
        }
    }
}
