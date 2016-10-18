package com.textsimilarity;
import java.util.*;

/**
 * Compare two texts and find similarity between them.
 * @author Nirav
 *
 */
public class CosineSimilarity
{
  private String[] text1Words;
  private String[] text2Words;
  
  private HashMap<String, Integer> vocabulary;
  
  public static boolean DEBUG_MODE = false;
  
  public CosineSimilarity(String text1, String text2)
  {
    this.text1Words = text1.split("\\s+");
    this.text2Words = text2.split("\\s+");
  }
  
  public double getSimilarity()
  { 
    buildVocabulary();
    double[] titleVector = getVector(text1Words);
    double[] nameVector = getVector(text2Words);
    
    double numerator = 0;
    
    for(int i = 0; i < titleVector.length; i++)
      numerator = numerator + ((titleVector[i]) * (nameVector[i]));
    
    double denominator = getNormOfVector(titleVector) * getNormOfVector(nameVector);
    double similarity = (denominator > 0) ? 1.0 * numerator / denominator : 0;

    return similarity;
  }
  
  /**
   * Builds a list of all the words used in the name and the title
   */
  private void buildVocabulary()
  {
    vocabulary = new HashMap<String, Integer>();
    addWordsToVocabulary(this.text1Words);
    addWordsToVocabulary(this.text2Words);
  }
  
  /**
   * Make words lowercase and add to vocabulary
   * @param words
   */
  private void addWordsToVocabulary(String[] words)
  {
    String formattedWord;
    Integer value;
    int index = vocabulary.size();
    
    for(String word : words)
    {
      formattedWord = word.toLowerCase();
      
      if(doesWordContainAlphaNumeric(formattedWord))
      {
        value = vocabulary.get(formattedWord);
      
        if(value == null)
          vocabulary.put(formattedWord, index++);
      }
    }
  }
  
  /**
   * Generates a vector from the given array of words.
   * Each element is an index of where the word occurs in the vocabulary. 
   * @param words
   * @return
   */
  private double[] getVector(String[] words)
  { 
    Integer index;
    String formattedWord;
    
    double[] vector = new double[vocabulary.size()];
    double weight = 1.0; // Assume each word has the same weight for simplicity
    
    
    for(String word : words)
    {
      formattedWord = word.toLowerCase();
      
      if(!doesWordContainAlphaNumeric(formattedWord))
        continue;
      
      index = vocabulary.get(formattedWord);
      
      if(DEBUG_MODE) System.out.println("Word = " + word + " formatted word = " + formattedWord + " Index = " + index);
      vector[index] = weight;
    }
    
    return vector;
  }
  
  private void printVector(double[] vector)
  {
    for(int i = 0; i < vector.length; i++)
      System.out.print(vector[i] + " ");
    System.out.println();
  }
  
  /**
   * Square root of the sum of the squares of all the elements of the vector
   * @param input
   * @return
   */
  private double getNormOfVector(double input[])
  {
    double norm = 0;
    
    for(int i = 0; i < input.length; i++)
      norm = norm + input[i] * input[i];
    
    return Math.sqrt(norm);
  }
  
  private boolean doesWordContainAlphaNumeric(String word)
  {
    return word.matches("^.*?[A-Za-z0-9]+.*?$");
  }
}
