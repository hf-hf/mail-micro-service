package top.hunfan.mail.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * 字符串工具类
 * @author hf-hf
 * @date 2019/1/9 15:31
 */
public class StringTools {

    private static final String YEAR_MONTH_DAY_HOUR_MINUTE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private static NumberFormat doubleFormat;

    private static Map<String, DateFormat> formatterMap = new HashMap<>();

    public static boolean isNull(Object obj) {
        return obj == null;
    }

    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        } else if (obj instanceof List) {
            return ((List)obj).isEmpty();
        } else if (obj instanceof Map) {
            return ((Map)obj).isEmpty();
        } else {
            return obj.toString().trim().length() == 0;
        }
    }

    public static String doubleToStr(double obj, int maxFractionDigits) {
        NumberFormat df = getDoubleFormat(false, maxFractionDigits);
        synchronized(df) {
            return df.format(obj);
        }
    }

    private static NumberFormat getDoubleFormat(boolean showGroup, int maxFractionDigits) {
        if (doubleFormat == null) {
            doubleFormat = NumberFormat.getNumberInstance();
        }

        doubleFormat.setGroupingUsed(showGroup);
        doubleFormat.setMaximumFractionDigits(maxFractionDigits);
        return doubleFormat;
    }

    public static String dateToStr(Date date) {
        return asString(date, YEAR_MONTH_DAY_HOUR_MINUTE_PATTERN);
    }

    public static String asString(Object obj, int maxLength){
        String objStr = asString(obj);
        if(null == objStr || objStr.length() <= maxLength){
            return objStr;
        }
        return objStr.substring(0, maxLength);
    }

    public static String asString(Object obj) {
        if (isNull(obj)) {
            return null;
        } else if (isEmpty(obj)) {
            return "";
        } else if (obj instanceof Date) {
            return dateToStr((Date)obj);
        } else if (obj instanceof Double) {
            return doubleToStr(((Double)obj).doubleValue(), 6);
        } else {
            try {
                String value = ReadXlobStr(obj);
                if (value != null) {
                    return value;
                }
            } catch (Exception var2) {
                return var2.getMessage();
            }

            return obj.toString();
        }
    }

    public static String asString(Date date, String format) {
        return asString(date, format, TimeZone.getDefault().getID());
    }

    public static String asString(Date date, String format, String timeZone) {
        String strDate = "";
        if (date != null) {
            DateFormat df = getDateFormat(format, timeZone);
            synchronized(df) {
                strDate = df.format(date);
            }
        }

        return strDate;
    }

    private static DateFormat getDateFormat(String format, String timeZone) {
        DateFormat df = formatterMap.get(format + "_" + timeZone);
        if (df == null) {
            df = new SimpleDateFormat(format);
            df.setTimeZone(TimeZone.getTimeZone(timeZone));
            formatterMap.put(format + "_" + timeZone, df);
        }

        return df;
    }

    public static String ReadXlobStr(Object xlob) throws SQLException, IOException {
        StringBuffer result = new StringBuffer();
        BufferedReader reader = null;
        if (xlob instanceof Blob) {
            reader = new BufferedReader(new InputStreamReader(((Blob)xlob).getBinaryStream()));
        } else {
            if (!(xlob instanceof Clob)) {
                return null;
            }

            reader = new BufferedReader(((Clob)xlob).getCharacterStream());
        }

        if (reader != null) {
            for(String line = reader.readLine(); line != null; line = reader.readLine()) {
                result.append(line).append("\n");
            }
        }

        return result.toString();
    }

}
