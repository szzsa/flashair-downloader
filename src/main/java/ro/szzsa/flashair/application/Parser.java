package ro.szzsa.flashair.application;

import org.apache.http.util.TextUtils;
import ro.szzsa.flashair.configuration.Configuration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 *
 */
public class Parser {

    private Configuration config = Configuration.getInstance();

    public List<Picture> parseList(String list) {
        List<Picture> pictures = new ArrayList<>();
        if (!TextUtils.isEmpty(list) && list.contains("\n")) {
            list = list.replaceAll("\\r\\n", ",#");
            String[] lines = list.split("#");
            for (String line : lines) {
                if (line.contains(config.getFlashairPictureDirectory())) {
                    String[] attrs = line.split(",");
                    Picture picture = new Picture();
                    picture.setName(attrs[1]);
                    picture.setTimestamp(getTimestamp(attrs[4], attrs[5]));
                    pictures.add(picture);
                }
            }
        }
        return pictures;
    }

    private long getTimestamp(String date, String time) {
        Calendar calendar = Calendar.getInstance();
        String dateBinary = Integer.toBinaryString(Integer.parseInt(date));
        int length = dateBinary.length();
        for (int i = 0; i < 16 - length; i++) {
            dateBinary = "0" + dateBinary;
        }
        calendar.set(Calendar.YEAR, 1980 + Integer.parseInt(dateBinary.substring(0, 7), 2));
        calendar.set(Calendar.MONTH, Integer.parseInt(dateBinary.substring(7, 11), 2));
        calendar.set(Calendar.DATE, Integer.parseInt(dateBinary.substring(11, 16), 2));
        String timeBinary = Integer.toBinaryString(Integer.parseInt(time));
        length = timeBinary.length();
        for (int i = 0; i < 16 - length; i++) {
            timeBinary = "0" + timeBinary;
        }
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeBinary.substring(0, 5), 2));
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeBinary.substring(5, 11), 2));
        calendar.set(Calendar.SECOND, Integer.parseInt(timeBinary.substring(11, 16), 2));
        return calendar.getTimeInMillis();
    }
}
