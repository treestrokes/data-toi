package self.vpalepu.data.toi;

import java.util.ArrayList;

import org.joda.time.Days;
import org.joda.time.LocalDate;

public class DatedURL {
  
  public static final String TOI_URL = "http://timesofindia.indiatimes.com";
  private static final LocalDate REF_DATE = LocalDate.parse("2013-01-01");
  private static final int REF_STARTTIME = 41_275; 
  private final static String format1 = "/yyyy/M/d/";
  private final static String format2 = "'/year-'yyyy',month-'M";
  
  
  private final LocalDate date;
  
  public DatedURL(LocalDate date) {
    this.date = date;
  }
  
  public String getDateString() {
    return date.toString("yyyy-MM-dd");
  }
  
  public String generateURL() {
    int diff;
    diff = Days.daysBetween(REF_DATE, date).getDays();
       
    int starttime = REF_STARTTIME + diff;
    StringBuffer buffer = new StringBuffer();
    buffer.append(TOI_URL)
          .append(date.toString(format1))
          .append("archivelist")
          .append(date.toString(format2))
          .append(",starttime-")
          .append(starttime)
          .append(".cms");
    return buffer.toString();
  }
}
