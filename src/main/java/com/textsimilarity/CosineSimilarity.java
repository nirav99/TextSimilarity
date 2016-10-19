package com.textsimilarity;
import java.util.*;

/**
 * Compare two texts and find similarity between them.
 * @author Nirav
 *
 */
public class CosineSimilarity
{
  private WordFrequency wfDoc1;
  private WordFrequency wfDoc2;
  
  private HashMap<String, Integer> vocabulary;
  
  public static boolean DEBUG_MODE = false;
  
  public CosineSimilarity(WordFrequency doc1, WordFrequency doc2)
  {
    this.wfDoc1 = doc1;
    this.wfDoc2 = doc2;
  }
  
  public double getSimilarity()
  { 
    buildVocabulary();
    double[] doc1Vector = getVector(wfDoc1);
    double[] doc2Vector = getVector(wfDoc2);
    
    double numerator = 0;
    
    for(int i = 0; i < doc1Vector.length; i++)
      numerator = numerator + ((doc1Vector[i]) * (doc2Vector[i]));
    
    double denominator = getNormOfVector(doc1Vector) * getNormOfVector(doc2Vector);
    double similarity = (denominator > 0) ? 1.0 * numerator / denominator : 0;

    return similarity;
  }
  
  /**
   * Builds a list of all the words used in the name and the title
   */
  private void buildVocabulary()
  {
    vocabulary = new HashMap<String, Integer>();
    addWordsToVocabulary(this.wfDoc1);
    addWordsToVocabulary(this.wfDoc2);
  }
  
  /**
   * Make words lowercase and add to vocabulary
   * @param words
   */
  private void addWordsToVocabulary(WordFrequency wfDoc)
  {
    Integer value;
    int index = vocabulary.size();
    
    Set<String> wordSet = wfDoc.wordSet();
    
    for(String word : wordSet)
    {      
      if(doesWordContainAlphaNumeric(word))
      {
        value = vocabulary.get(word);
      
        if(value == null)
          vocabulary.put(word, index++);
      }
    }
  }
  
  /**
   * Generates a vector from the given array of words.
   * Each element is an index of where the word occurs in the vocabulary. 
   * @param words
   * @return
   */
  private double[] getVector(WordFrequency wfDoc)
  { 
    Integer index;
    String formattedWord;
    
    double[] vector = new double[vocabulary.size()];
    double weight;
    
    Set<String> wordSet = wfDoc.wordSet();
    for(String word : wordSet)
    {
      formattedWord = word.toLowerCase();
      
      if(!doesWordContainAlphaNumeric(formattedWord))
        continue;
      
      index = vocabulary.get(formattedWord);
      
      if(DEBUG_MODE) System.out.println("Word = " + word + " formatted word = " + formattedWord + " Index = " + index);
      
      // We use normalized frequency of the word as the weight of the word.
      vector[index] = wfDoc.normalizedFrequency(word);
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
