package com.textsimilarity;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

import java.io.*;
/**
 * Finds similarity between two files
 * @author nirav99
 *
 */
public class SimilarityFinder
{
  private static MaxentTagger posTagger;
  
  private String textFile1;
  private String textFile2;
  
  private final double SIMILARITY_THRESHOLD = 0.5;
  
  public static boolean DEBUG_MODE = false;
  
  static
  {
    if(posTagger == null)
     posTagger = new MaxentTagger("english-left3words-distsim.tagger");
  }
  
  public SimilarityFinder()
  {

  }
  
  public void checkFilesForSimilarity(String inputFileName1, String inputFileName2) throws IOException
  {
    File inputFile1 = new File(inputFileName1);
    File inputFile2 = new File(inputFileName2);
    
    System.out.println("Comparing " + inputFile1.getName() + " & " + inputFile2.getName());
    
    textFile1 = readFile(inputFile1);
    textFile2 = readFile(inputFile2);
    
    if(textFile1 != null)
      textFile1 = removeUnwantedWords(stripHTML(textFile1));
    
    if(textFile2 != null)
      textFile2 = removeUnwantedWords(stripHTML(textFile2));
    
    if(textFile1 == null || textFile2 == null)
    {
      System.out.println("One of the files is empty, skipping..");
      return;
    }
    
    if(DEBUG_MODE)
    {
      System.out.println("=================");
      System.out.println(textFile1);
      System.out.println("=================");
      System.out.println(textFile2);
    }
    
    WordFrequency wfDoc1 = new WordFrequency(textFile1);
    WordFrequency wfDoc2 = new WordFrequency(textFile2);
    
    CosineSimilarity cosineSim = new CosineSimilarity(wfDoc1, wfDoc2);
    
    long startTime = System.currentTimeMillis();
    double similarityValue = cosineSim.getSimilarity();
    long endTime = System.currentTimeMillis();
    
    System.out.format("Cosine Similarity : %.3f, Angle between the vectors : %.3f degrees\n", similarityValue, Math.toDegrees(Math.acos(similarityValue)));

    /*
    if(similarityValue >= SIMILARITY_THRESHOLD)
      System.out.format("Score : %.3f, Files are similar\n", similarityValue);
    else
      System.out.format("Score : %.3f, No similarity found\n", similarityValue);
    */
    System.out.format("Processing time : %.4f sec\n\n\n", 1.0 * (endTime - startTime) / 1000.0);
  }
  
  private String readFile(File inputFile) throws IOException
  {
    BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "UTF-8"));
    StringBuilder content = new StringBuilder();
    String line = null;
    
    while((line = reader.readLine()) != null)
      content.append(line).append("\n");
    
    reader.close();
    return content.toString();
  }
  
  /**
   * Removes HTML from the given text
   * @param givenText
   * @return
   */
  private String stripHTML(String givenText)
  {    
    String newContent = givenText.replaceAll("(?i)<script[^>]*?>[\\s\\S]*?</script>", "").replaceAll("(?i)<style[^>]*?>[\\s\\S]*?</style>", "").replaceAll("(?i)<head[^>]*?>([\\s\\S]*?)</head>", "").replaceAll("<!--[\\s\\S]*?-->", " ");
    newContent = newContent.replaceAll("(?i)<title[^>]*?>[\\s\\S]*?</title>", "").replaceAll("(?i)<meta\\s[^>]*?>", "");
    newContent = newContent.replaceAll("(?i)<option[^>]*?>[\\s\\S]*?</option>", " ");
    newContent = newContent.replaceAll("(?i)<a [^>]*?>([\\s\\S])*?</a>", " ");
    newContent = newContent.replaceAll("<[^>]*?>", " ");
    newContent = newContent.replaceAll("\u00A0", " ").replaceAll("\\&#153;", " "); // Replace troublesome spaces
    newContent = newContent.replaceAll("\\&(#\\d+|[A-Za-z]+);", " "); // Replace HTML entities like &copy;, &#149; etc.    
    return removePunctuations(newContent);
  }
  
  private String removePunctuations(String content)
  {
    // Remove http urls, urls starting with www and email addresses.
    String newContent = content.replaceAll("(?i)https?.*?\\s", " ").replaceAll("[A-Z0-9a-z\\._%+-]+@[A-Za-z0-9\\.-]+\\.[A-Za-z]{2,4}", " ");
    newContent = newContent.replaceAll("(?i)www\\d?\\.([A-Za-z0-9]+\\.){1,}[a-zA-Z]{2,6}", " ");
    newContent = newContent.replaceAll("[\\?;:<>!#\\(\\)\\[\\]\\{\\}\"^~/]+", " ");
    newContent = newContent.replaceAll("[\\.,](?!\\d)", " ");
    newContent = newContent.replaceAll("(?<!\\d)[\\.,]", " ");
    newContent = newContent.replaceAll("[`'](?!\\w)", " ");
    newContent = newContent.replaceAll("(?<!\\w)[`']", " ");
    newContent = newContent.replaceAll("\\s+", " ");
    return newContent;
  }
  
  private String removeUnwantedWords(String text)
  {
    String tagged = posTagger.tagString(text);
    String[] words = tagged.split("\\s+");
    
    StringBuilder result = new StringBuilder();
    
    int lastIndexOfUnderscore;
    String tag;
    
    for(String word : words)
    {
      lastIndexOfUnderscore = word.lastIndexOf("_");
      
      if(lastIndexOfUnderscore >= 0)
      {
        tag = word.substring(lastIndexOfUnderscore + 1);
      
        if(shouldAddWord(word, tag))
          result.append(word.substring(0, lastIndexOfUnderscore)).append(" ");
      }
      else
        result.append(word).append(" ");
    }
    return result.toString();
  }
  
  /**
   * Discard words that are conjunctions, prepositions etc.
   * Discard email addresses etc
   * @param tagName
   * @return
   */
  private boolean shouldAddWord(String word, String tagName)
  {
    if(tagName.equals("CC") || tagName.equals("DT") || tagName.equals("EX") || tagName.equals("IN") ||
       tagName.equals("POS") || tagName.equals("TO") || tagName.equals("SYM") || tagName.equals("UH") ||
       tagName.equals("WP")  || tagName.equals("CD") || tagName.equals("NNP"))
    return false;
    
    return true;
  }
}
