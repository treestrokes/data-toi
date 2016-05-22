package self.vpalepu.data.toi;

import static com.google.common.base.Preconditions.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.base.Preconditions;


public class Scraper {

  private static final FilenameFilter HTML_FILE_FLITER = new FilenameFilter() {

    @Override
    public boolean accept(File dir, String name) {
      if(name.endsWith(".html")) {
        return true;
      }
      return false;
    }
  };

  public ArrayList<Story> scrape(File html) throws IOException {
    Document doc = Jsoup.parse(html, null);
    ArrayList<Story> stories = new ArrayList<>();
    Elements links = doc.getElementsByTag("a");
    for (Element link : links) {
      Attributes attributes = link.attributes();
      if(attributes.hasKey("pg")
          || attributes.hasKey("target")
          || attributes.hasKey("navid")
          || attributes.hasKey("class"))
        continue;

      if(attributes.size() != 1 
          || !attributes.hasKey("href")
          || !attributes.get("href").startsWith(DatedURL.TOI_URL + "//")) {
        continue;
      }

      String linkHref = link.attr("href");
      String linkText = link.text();
      String dateString = html.getName().replace(".html", "");
      Story story = new Story(linkText, linkHref, new LocalDate(dateString));
      stories.add(story);
    }
    return stories;
  }

  public File scrape(DatedURL datedUrl, Path storeRoot) {
    try {
      String url = datedUrl.generateURL();

      Document doc = Jsoup.connect(url).timeout(20*1000).get();
      File html = new File(storeRoot.toFile() + "/" + datedUrl.getDateString() + ".html");
      save(html, doc.toString());
      return html;
    } catch(SocketTimeoutException ex) {
      System.err.println(datedUrl.getDateString());
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }

  private void save(File file, String textToSave) {
    file.delete();
    try {
      BufferedWriter out = new BufferedWriter(new FileWriter(file));
      out.write(textToSave);
      out.close();
    } catch (IOException e) {
    }
  }

  public ArrayList<String> scrapeFrom(Path storeRoot, LocalDate date, int forDays) 
      throws InterruptedException {
    ArrayList<String> failedDates = new ArrayList<>();
    for(int i = 0; i < forDays; i += 1) {
      DatedURL datedUrl = new DatedURL(date);
      String url2 = datedUrl.generateURL();
      System.out.println(url2);
      File html = this.scrape(datedUrl, storeRoot);
      if(html == null) {
        failedDates.add(date.toString());
      }
      date = date.plusDays(1);
      Thread.sleep(5000);
    }
    return failedDates;
  }

  public ArrayList<String> scrapeFrom(Path storeRoot, String ... dateStrings) 
      throws InterruptedException {
    ArrayList<String> failedDates = new ArrayList<>();
    for(String dateString : dateStrings) {
      if(dateString == null ||dateString.isEmpty()) {
        continue;
      }
      LocalDate date = LocalDate.parse(dateString);
      DatedURL datedUrl = new DatedURL(date);
      System.out.println(date.toString());
      String url2 = datedUrl.generateURL();
      System.out.println(url2);
      File html = this.scrape(datedUrl, storeRoot);
      if(html == null) {
        failedDates.add(date.toString());
      }
      Thread.sleep(5000);
    }
    return failedDates;
  }

  public static void scrape(Path storeRoot, String from, int forDays) throws IOException, InterruptedException {
    Scraper sc = new Scraper();

    LocalDate date = LocalDate.parse(from);
    ArrayList<String> failedDates = sc.scrapeFrom(storeRoot, date, forDays);
    while(!failedDates.isEmpty()) {
      System.out.println("Starting over:");
      String[] dateStrings = new String[failedDates.size()];
      int i = 0;
      for(String failedDate : failedDates) {
        if(failedDate == null || failedDate.isEmpty()) {
          continue;
        }
        dateStrings[i] = failedDate;
        i += 1;
      }
      failedDates = sc.scrapeFrom(storeRoot, dateStrings);
    }
  }

  public static ArrayList<Story> scrapeFromStore(File htmlStore) throws IOException {
    Scraper sc = new Scraper();
    ArrayList<Story> stories = new ArrayList<>();
    checkState(htmlStore.isDirectory());
    File[] htmlFiles = htmlStore.listFiles(HTML_FILE_FLITER);
    System.out.println(htmlFiles.length);
    for(File html : htmlFiles) {
      stories.addAll(sc.scrape(html));
    }
    return stories;
  }

  public static void scrapeAndSpit(File spit, File ... htmlstores) throws IOException {
    ArrayList<Story> stories = new ArrayList<Story>();
    for(File store : htmlstores) {
      stories.addAll(Scraper.scrapeFromStore(store));
    }
    try {
      BufferedWriter out = new BufferedWriter(new FileWriter(spit));
      for(Story story :  stories) {
        
        if(!story.date().isAfter(new LocalDate(2015, 5, 27)))
          continue;
        
        String[] titleWords = story.title().split("\\s+");
        List<String> titleWordList = Arrays.asList(titleWords);
        
        if(titleWordList.contains("Lack") || titleWordList.contains("lack")) {
          out.write(story.toString() + "\n");
        }
      }
      out.close();
    } catch (IOException e) {
    }
  }

  public static void main(String[] args) throws IOException, InterruptedException {

    Path pwd = Paths.get(System.getProperty("user.dir"));
    System.out.println(pwd);

    Path dataDir = pwd.resolve("data/");
    if(Files.notExists(dataDir) || 
        (Files.exists(dataDir) && Files.isRegularFile(dataDir)) ) {
      Files.createDirectory(dataDir);
    }

    Preconditions.checkState(Files.exists(dataDir) && Files.isDirectory(dataDir));
    System.out.println(dataDir.toString());



    DateTime day1In2015 = new DateTime().withDate(2016, 2, 1);
    DateTime day31In2016 = new DateTime().withDate(2016, 5, 18);

    System.out.println(day1In2015.toString("yyyy-MM-dd"));

    int forDays = ((int) new Duration(day1In2015, day31In2016).getStandardDays()) + 1;

    System.out.println(forDays);

    Scraper.scrape(dataDir, day1In2015.toString("yyyy-MM-dd"), forDays);

    File out = new File(dataDir.resolve("stories15-16.csv").toString() );
    Scraper.scrapeAndSpit(out, dataDir.toFile());
  }

}
