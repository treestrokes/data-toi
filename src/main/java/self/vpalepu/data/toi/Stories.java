package self.vpalepu.data.toi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class Stories {
  public static void main(String[] args) throws IOException {
    if(args.length <= 0 || args[0].equals("")) {
      throw new RuntimeException("Need proper file path.");
    }
    
    File file = new File(args[0]);
    System.err.println("File path that has been read in: " + file.getAbsolutePath());
    
    BufferedReader fileReader = new BufferedReader(new FileReader(file));
    CSVParser lineParser = null;
    
    String line;
    int numberOfStories = 0;
    ArrayList<Story> stories = new ArrayList<>();
    
    
    long startTime = System.currentTimeMillis();
    
    while((line = fileReader.readLine()) != null) {
      Story story = Story.cleanTitledfromString(line);
      
      if(story.title().replaceAll("\\s+", "").isEmpty()) {
        continue;
      }
      
      stories.add(story);
      
      
      
      
//      System.out.println(line);
//      lineParser = CSVParser.parse(line, CSVFormat.DEFAULT);
//      Iterator<CSVRecord> records = lineParser.iterator();
//      while(records.hasNext()) {
//        System.out.println(records.next().toString());
//      }
//      System.out.println(lineParser.toString());
      numberOfStories += 1;
    }
    
    System.out.println("Number of Published Stories: " + numberOfStories);
    System.out.format("Time taken to print %s stories, is %s.\n", 
                      numberOfStories, 
                      System.currentTimeMillis() - startTime);
    
    fileReader.close();
  }
}
