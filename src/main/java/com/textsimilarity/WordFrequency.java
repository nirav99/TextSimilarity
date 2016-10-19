package com.textsimilarity;

import java.util.*;

/**
 * Encapsulates words and their frequencies.
 * @author nirav99
 *
 */
public class WordFrequency
{
  private HashMap<String, Integer> wordFreq;
  private int totalWords;
  
  public WordFrequency(String text)
  {
    wordFreq = new HashMap<String, Integer>();
    buildFrequencyMap(text);
    countTotalWords();
  }
  
  public int totalWords()
  {
    return totalWords;
  }
  
  /**
   * Returns the set of words
   * @return
   */
  public Set<String> wordSet()
  {
    return this.wordFreq.keySet();
  }
  
  /**
   * Return the normalized frequency where the word count is divided by the total number of words in the document.
   * @param word
   * @return
   */
  public double normalizedFrequency(String word)
  {
    if(word == null)
      return 0;
    
    Integer value = wordFreq.get(word.toLowerCase());
    if(value == null)
      return 0;
    
    return 1.0 * value / totalWords;
  }
  
  /**
   * Returns the count of the number of times the specified word occurred in the document.
   * @param word
   * @return
   */
  public int rawFrequency(String word)
  {
    if(word == null)
      return 0;
	      
    Integer value = wordFreq.get(word.toLowerCase());
    return (value != null) ? value : 0;
  }
  
  private void buildFrequencyMap(String text)
  {
    String[] words = text.split("\\s+");
    
    Integer value = null;
    
    for(String word : words)
    {
      value = wordFreq.get(word.toLowerCase());
      
      if(value == null) value = 0;
      value = value + 1;
      
      wordFreq.put(word.toLowerCase(), value);
    }
  }
  
  private void countTotalWords()
  {
    Collection<Integer> valueColl = wordFreq.values();
    
    for(Integer val : valueColl)
      totalWords += val;
  }
}
