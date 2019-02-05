import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;
import java.util.Locale;

public class WorldTimeService {
    static String toReturn;

    public static String getTime(String place) throws IOException {

        Document doc = Jsoup.connect("https://www.google.com/search?q=" + place + "+" + "time").get();
        String html = "<div class=\"gsrt vk_bk dDoNo\" aria-level=\"3\" role=\"heading\">22:44</div>" +
                "<span class=\"KfQeJ\">29. januar 2019</span>";
        String toReturn = "";

        Elements content = doc.getElementsByClass("gsrt vk_bk dDoNo");
        Elements d = doc.getElementsByClass("KfQeJ");
        Elements date = doc.getElementsByTag("span");
        for (Element t : content) {
            Elements ts = t.getElementsByTag("div");
            Element tt = ts.first();
            toReturn = tt.text();
        }
        for (Element a : d) {
            Elements ds = a.getElementsByTag("span");
            Element dd = ds.first();
            toReturn += ", " + dd.text();
        }

        return toReturn;

    }
}
