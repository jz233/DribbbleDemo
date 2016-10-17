package zjj.com.dribbbledemoapp.utils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtils {

    public static String parseDateTime(String dateTime) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            Date date = formatter.parse(dateTime);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            return format.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
