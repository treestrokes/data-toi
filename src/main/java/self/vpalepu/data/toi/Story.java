package self.vpalepu.data.toi;

import java.util.ArrayList;

import org.joda.time.LocalDate;

public class Story {
  private final String title;
  private final String href;
  private LocalDate date;
  private ArrayList<String> tags;

  public Story(String title, String href, LocalDate date) {
    this.title = title;
    this.href = href;
    this.date = date;
  }
  
  public String title() {
    return this.title;
  }
  
  public String href() {
    return this.href;        
  }
  
  public LocalDate date() {
    return this.date;
  }
  
  public String[] tags() {
    String[] split = href.split("/");
    return split;
  }
  
  public String toString() {
    StringBuffer buffer = new StringBuffer();
    buffer.append(this.date()).append(",");
    buffer.append("\"").append(title).append("\"").append(",");
    buffer.append(href);
    return buffer.toString();
  }
  
  public static Story fromString(String line) {
    String href = line.substring(line.lastIndexOf(',')+1);
    String datestring = line.substring(0, line.indexOf(','));
    LocalDate date = LocalDate.parse(datestring);
    String title = line.substring(line.indexOf('"') + 1, line.lastIndexOf('"'));
    Story story = new Story(title, href, date);
    return story;
  }
  
  public static Story cleanTitledfromString(String line) {
    String href = line.substring(line.lastIndexOf(',')+1);
    String datestring = line.substring(0, line.indexOf(','));
    LocalDate date = LocalDate.parse(datestring);
    String title = line.substring(line.indexOf('"') + 1, line.lastIndexOf('"'));
    
    String cleanTitle = cleanTitle(title);
    
    Story story = new Story(cleanTitle, href, date);
    return story;
  }
  
    private static String cleanTitle(String title) {
        final StringBuilder builder = new StringBuilder();
        for(final char c : title.toCharArray())
            if(Character.isLetterOrDigit(c) || Character.isWhitespace(c))
                builder.append(Character.toLowerCase(c));
        return builder.toString();
    }
}
