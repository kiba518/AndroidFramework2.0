package com.kiba.framework.utils;


import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class DateUtils {

    /**
     * 获取YYYY格式
     *
     * @return
     */
    public static String getYear() {
        return formatDate(new Date(), "yyyy");
    }

    /**
     * 获取当前月份
     *
     * @return
     */
    public static String getMonth() {
        return formatDate(new Date(), "MM");
    }


    /**
     * 获取YYYY格式
     *
     * @return
     */
    public static String getYear(Date date) {
        return formatDate(date, "yyyy");
    }

    /**
     * 获取YYYY-MM-DD格式
     *
     * @return
     */
    public static String getDay() {
        return formatDate(new Date(), "yyyy-MM-dd");
    }

    /**
     * 获取YYYY-MM-DD格式
     *
     * @return
     */
    public static String getDay(Date date) {
        return formatDate(date, "yyyy-MM-dd");
    }

    /**
     * 获取YYYYMMDD格式
     *
     * @return
     */
    public static String getDays() {
        return formatDate(new Date(), "yyyyMMdd");
    }

    /**
     * 获取YYYYMMDD格式
     *
     * @return
     */
    public static String getDays(Date date) {
        return formatDate(date, "yyyyMMdd");
    }

    /**
     * 获取YYYY-MM-DD HH:mm:ss格式
     *
     * @return
     */
    public static String getTime() {
        return formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 获取YYYY-MM-DD HH:mm:ss.SSS格式
     *
     * @return
     */
    public static String getMsTime() {
        return formatDate(new Date(), "yyyy-MM-dd HH:mm:ss.SSS");
    }

    /**
     * 获取YYYYMMDDHHmmss格式
     *
     * @return
     */
    public static String getAllTime() {
        return formatDate(new Date(), "yyyyMMddHHmmss");
    }


    /**
     * 获取YYYYMMDDHHmmssSSS格式
     *
     * @return
     */
    public static String getAllMSTime() {
        return formatDate(new Date(), "yyyyMMddHHmmssSSS");
    }

    /**
     * 获取YYYY-MM-DD HH:mm:ss格式
     *
     * @return
     */
    public static String getTime(Date date) {
        return formatDate(date, "yyyy-MM-dd HH:mm:ss");
    }
    public static String formatDate(Date date) {
        return formatDate(date, "yyyy-MM-dd HH:mm:ss");
    }
    public static String formatDate(Date date, String pattern) {

        String formatDate = null;
        if (StringUtils.isNotEmpty(pattern)) {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            formatDate = df.format(date);
        } else {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            formatDate = df.format(date);
        }
        return formatDate;
    }

    /**
     * compareDate
     * (日期比较，如果s>=e 返回true 否则返回false)
     *
     * @param s
     * @param e
     * @return boolean
     * @throws
     * @author luguosui
     */
    public static boolean compareDate(String s, String e) {
        if (parseDate(s) == null || parseDate(e) == null) {
            return false;
        }
        return parseDate(s).getTime() >= parseDate(e).getTime();
    }

    /**
     * 格式化日期
     *
     * @return
     */
    public static Date parseDate(String date) {
        return parse(date, "yyyy-MM-dd");
    }

    /**
     * 格式化日期
     *
     * @return
     */
    public static Date parseTime(String date) {
        return parse(date, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 格式化日期
     *
     * @return
     */
    public static Date parse(String date, String pattern) {
        try {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            return  df.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 格式化日期
     *
     * @return
     */
    public static String format(Date date, String pattern) {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        return df.format(date);
    }

    /**
     * 把日期转换为Timestamp
     *
     * @param date
     * @return
     */
    public static Timestamp format(Date date) {
        return new Timestamp(date.getTime());
    }

    /**
     * 校验日期是否合法
     *
     * @return
     */
    public static boolean isValidDate(String s) {
        return parse(s, "yyyy-MM-dd HH:mm:ss") != null;
    }

    /**
     * 校验日期是否合法
     *
     * @return
     */
    public static boolean isValidDate(String s, String pattern) {
        return parse(s, pattern) != null;
    }

    public static int getDiffYear(String startTime, String endTime) {
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        try {
            int years = (int) (((fmt.parse(endTime).getTime() - fmt.parse(
                    startTime).getTime()) / (1000 * 60 * 60 * 24)) / 365);
            return years;
        } catch (Exception e) {
            // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            return 0;
        }
    }

    /**
     * <li>功能描述：时间相减得到天数
     *
     * @param beginDateStr
     * @param endDateStr
     * @return long
     * @author Administrator
     */
    public static long getDaySub(String beginDateStr, String endDateStr) {
        long day = 0;
        SimpleDateFormat format = new SimpleDateFormat(
                "yyyy-MM-dd");
        Date beginDate = null;
        Date endDate = null;

        try {
            beginDate = format.parse(beginDateStr);
            endDate = format.parse(endDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        day = (endDate.getTime() - beginDate.getTime()) / (24 * 60 * 60 * 1000);
        // System.out.println("相隔的天数="+day);

        return day;
    }

    /**
     * 两个时间之间的天数
     *
     * @param beginTime
     * @param endTime
     * @return
     */
    public static Long getDaySub(Date beginTime, Date endTime) {
        return (endTime.getTime() - beginTime.getTime()) / (24 * 60 * 60 * 1000);
    }

    /**
     * 得到n天之后的日期
     *
     * @param days
     * @return
     */
    public static String getAfterDayDate(String days) {
        int daysInt = Integer.parseInt(days);

        Calendar calendar = Calendar.getInstance(); // java.util包
        calendar.add(Calendar.DATE, daysInt); // 日期减 如果不够减会将月变动
        Date date = calendar.getTime();

        SimpleDateFormat sdfd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = sdfd.format(date);

        return dateStr;
    }

    /**
     * 得到n天之后是周几
     *
     * @param days
     * @return
     */
    public static String getAfterDayWeek(String days) {
        int daysInt = Integer.parseInt(days);

        Calendar calendar = Calendar.getInstance();
        // 日期减 如果不够减会将月变动
        calendar.add(Calendar.DATE, daysInt);
        Date date = calendar.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("E");
        String dateStr = sdf.format(date);

        return dateStr;
    }

    /**
     * 格式化Oracle Date
     * @param value
     * @return
     */
//	public static String buildDateValue(Object value){
//		if(Func.isOracle()){
//			return "to_date('"+ value +"','yyyy-mm-dd HH24:MI:SS')";
//		}else{
//			return Func.toStr(value);
//		}
//	}

    /**
     * 时间戳转换成日期格式字符串
     *
     * @param seconds 精确到秒的字符串
     * @param format
     * @return
     */
    public static String time2String(String seconds, String format) {
        if (seconds == null || seconds.isEmpty() || "null".equals(seconds)) {
            return "";
        }
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.parseLong(seconds + "000")));
    }


    /**
     * 日期格式字符串转换成时间戳
     *
     * @param date_str 字符串日期
     * @param format   如：yyyy-MM-dd HH:mm:ss
     * @return long
     */
    public static Long str2TimeStamp(String date_str, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return Long.valueOf(sdf.parse(date_str).getTime() / 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 日期格式字符串转换成时间戳
     *
     * @param date_str 字符串日期
     * @param format   如：yyyy-MM-dd HH:mm:ss
     * @return long
     */
    public static Long str2TimeStampMillions(String date_str, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.parse(date_str).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获得该月第一天
     *
     * @param year
     * @param month
     * @return
     */
    public static String getFirstDayOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        //设置年份
        cal.set(Calendar.YEAR, year);
        //设置月份
        cal.set(Calendar.MONTH, month - 1);
        //获取某月最小天数
        int firstDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
        //设置日历中月份的最小天数
        cal.set(Calendar.DAY_OF_MONTH, firstDay);
        //格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String firstDayOfMonth = sdf.format(cal.getTime());
        return firstDayOfMonth;
    }

    /**
     * 获得该月最后一天,实际获得下月第一天
     *
     * @param year
     * @param month
     * @return
     */
    public static String getLastDayOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        //设置年份
        cal.set(Calendar.YEAR, year);
        //设置月份
        cal.set(Calendar.MONTH, month);
        //获取某月最小天数
        int firstDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
        //设置日历中月份的最小天数
        cal.set(Calendar.DAY_OF_MONTH, firstDay);
        //格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String firstDayOfMonth = sdf.format(cal.getTime());
        return firstDayOfMonth;
    }
//	public static String getLastDayOfMonth(int year,int month){
//		Calendar cal = Calendar.getInstance();
//		//设置年份
//		cal.set(Calendar.YEAR,year);
//		//设置月份
//		cal.set(Calendar.MONTH, month-1);
//		//获取某月最大天数
//		int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
//		//设置日历中月份的最大天数
//		cal.set(Calendar.DAY_OF_MONTH, lastDay);
//		//格式化日期
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		String lastDayOfMonth = sdf.format(cal.getTime());
//		return lastDayOfMonth;
//	}

    /**
     * 当前月第一天
     *
     * @param pattern
     * @return
     */
    public static String getCurrentLastDayOfMonth(String pattern) {
        Calendar cale = Calendar.getInstance();
        cale.add(Calendar.MONTH, 1);
        cale.set(Calendar.DAY_OF_MONTH, 0);

        if (pattern == null || pattern.isEmpty()) {
            pattern = "yyyy-MM-dd";
        }
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        String firstday = format.format(cale.getTime());
        return firstday;
    }

    /**
     * 获取当前月第一天
     *
     * @param pattern
     * @return
     */
    public static String getCurrentFirstDayOfMonth(String pattern) {
        Calendar cale = Calendar.getInstance();
        cale.add(Calendar.MONTH, 0);
        cale.set(Calendar.DAY_OF_MONTH, 1);

        if (pattern == null || pattern.isEmpty()) {
            pattern = "yyyy-MM-dd";
        }
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        String firstday = format.format(cale.getTime());
        return firstday;
    }

    public static String getNextMonthFirstDay(String pattern) {
        Calendar cale = Calendar.getInstance();
        cale.add(Calendar.MONTH, 1);
        cale.set(Calendar.DAY_OF_MONTH, 1);

        if (pattern == null || pattern.isEmpty()) {
            pattern = "yyyy-MM-dd";
        }
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        String firstday = format.format(cale.getTime());
        return firstday;
    }

    /**
     * 每周的第一天和最后一天
     *
     * @param dataStr
     * @param dateFormat
     * @param resultDateFormat
     * @return
     * @throws ParseException
     */
    public static String getFirstOfWeek(String dataStr, String dateFormat, String resultDateFormat) {
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(new SimpleDateFormat(dateFormat).parse(dataStr));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int d = 0;
        if (cal.get(Calendar.DAY_OF_WEEK) == 1) {
            d = -6;
        } else {
            d = 2 - cal.get(Calendar.DAY_OF_WEEK);
        }
        cal.add(Calendar.DAY_OF_WEEK, d);
        // 所在周开始日期
        String data1 = new SimpleDateFormat(resultDateFormat).format(cal.getTime());
        return data1;

    }

    /**
     * 每周的最后一天
     *
     * @param dataStr
     * @param dateFormat
     * @param resultDateFormat
     * @return
     * @throws ParseException
     */
    public static String getLastOfWeek(String dataStr, String dateFormat, String resultDateFormat) {
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(new SimpleDateFormat(dateFormat).parse(dataStr));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int d = 0;
        if (cal.get(Calendar.DAY_OF_WEEK) == 1) {
            d = -6;
        } else {
            d = 2 - cal.get(Calendar.DAY_OF_WEEK);
        }
        cal.add(Calendar.DAY_OF_WEEK, d);
//		// 所在周开始日期
//		String data1 = new SimpleDateFormat(resultDateFormat).format(cal.getTime());
        cal.add(Calendar.DAY_OF_WEEK, 6);
        // 所在周结束日期
        String data2 = new SimpleDateFormat(resultDateFormat).format(cal.getTime());
        return data2;

    }

    //获取当天的开始时间
    public static Date getDayBegin() {
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    //获取当天的结束时间
    public static Date getDayEnd() {
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }


    /**
     * 获取指定时间year年后的第二天凌晨
     *
     * @param currentDate 指定date
     * @param year        年
     * @return
     */
    public static Date getDateAfterYear(Date currentDate, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.YEAR, +year);
        calendar.add(Calendar.DATE, +1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获得指定时间后的day天后的第二天凌晨
     *
     * @param currentDate 当前时间
     * @param day         天
     * @return
     */
    public static Date getDateAfterDays(Date currentDate, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DATE, day+1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获得指定时间后的day天后的时间
     *
     * @param currentDate 当前时间
     * @param day         天
     * @return
     */
    public static Date getDateAfterDay(Date currentDate, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DATE, day);
        return calendar.getTime();
    }

    /**
     * 获取指定月所在的季度
     *
     * @param month
     * @return
     */
    public static String getQuarter(String month) {
        String quarter = "";
        int m = Integer.parseInt(month);
        if (m >= 1 && m <= 3) {
            quarter = "1";
        }
        if (m >= 4 && m <= 6) {
            quarter = "2";
        }
        if (m >= 7 && m <= 9) {
            quarter = "3";
        }
        if (m >= 10 && m <= 12) {
            quarter = "4";
        }
        return quarter;
    }

    /**
     * 获取每隔季度的时间限制
     *
     * @param year
     * @param quarter
     * @return
     */
    public static List<String> getSeasonTimeLimit(String year, String quarter) {
        List<String> timeLimit = new ArrayList<>();
        if ("1".equals(quarter)) {
            timeLimit.add(year.concat("-01-01 00:00:00"));
            timeLimit.add(year.concat("-03-31 23:59:59"));
        } else if ("2".equals(quarter)) {
            timeLimit.add(year.concat("-04-01 00:00:00"));
            timeLimit.add(year.concat("-06-30 23:59:59"));
        } else if ("3".equals(quarter)) {
            timeLimit.add(year.concat("-07-01 00:00:00"));
            timeLimit.add(year.concat("-09-30 23:59:59"));
        } else {
            timeLimit.add(year.concat("-10-01 00:00:00"));
            timeLimit.add(year.concat("-12-31 23:59:59"));
        }

        return timeLimit;
    }


}
