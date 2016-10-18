package com.textsimilarity;

import java.io.*;
import java.util.*;

public class Driver
{
  public File inputDir;
  public ArrayList<String> textFiles;
  
  public Driver(File inputDir)
  {
    this.inputDir = inputDir;
    this.textFiles = new ArrayList<String>();
    searchForFiles();
  }
  
  public void findSimilarText() throws IOException
  { 
    SimilarityFinder simFinder = new SimilarityFinder();
    
    for(int i = 0; i < textFiles.size(); i++)
    {
      for(int j = i + 1; j < textFiles.size(); j++) 
      {
        simFinder.checkFilesForSimilarity(textFiles.get(i), textFiles.get(j));
      }
    }
  }
  
  private void searchForFiles()
  {
    String[] fileNames = inputDir.list();
    
    for(String fileName : fileNames)
    {
      if(fileName.endsWith(".txt"))
        textFiles.add(new String(inputDir.getAbsolutePath() + File.separator + fileName));
    }
    System.out.println("Found : " + textFiles.size() + " files");
  }
  
  public static void main(String[] args)
  {
    try
    {
      if(args == null || args.length != 1 || args[0].toLowerCase().contains("help"))
      {
        printUsage();
        return;
      }
      File inputDir = new File(args[0]);
      
      if(!inputDir.exists() || !inputDir.isDirectory())
      {
        System.err.println(inputDir.getAbsolutePath() + " does not exist or is not a valid directory");
        printUsage();
        return;
      }
      Driver driver = new Driver(inputDir);
      driver.findSimilarText();
    }
    catch(Exception e)
    {
      System.err.println(e.getMessage());
      e.printStackTrace();
    }
  }
  
  private static void printUsage()
  {
    System.err.println("Usage : " );
    System.err.println("Provide directory who text files should be analyzed. The files should end with extension .txt");
  }
}
