package com.sharkey.scala.spark

import org.apache.spark._
import org.apache.spark.SparkContext._
import org.apache.log4j._

object WordCountBetterSorted {
  
    /** Our main function where the action happens */
  def main(args: Array[String]) {
    
    val stopList = List("if", "are", "on", "for", "is", "in",
        "it", "that", "and", "of", "a", "the", "your", "to")
   
    // Set the log level to only print errors
    Logger.getLogger("org").setLevel(Level.ERROR)
    
     // Create a SparkContext using the local machine
    val sc = new SparkContext("local", "WordCountBetterSorted")   
    
    // Load each line of my book into an RDD
    val input = sc.textFile("../../data/book.txt")
    
    // Split using a regular expression that extracts words
    val words = input.flatMap(x => x.split("\\W+"))
    
    // Normalize everything to lowercase
    val lowercaseWords = words.map(x => x.toLowerCase())
    
    val filteredWords = words.filter( x => !stopList.contains(x))
    
    // Count of the occurrences of each word
    val wordCounts = filteredWords.map(x => (x, 1)).reduceByKey( (x,y) => x + y )
    
    // Flip (word, count) tuples to (count, word) and then sort by key (the counts)
    val wordCountsSorted = wordCounts.map( x => (x._2, x._1) ).sortByKey()
    
    // Print the results, flipping the (count, word) results to word: count as we go.
    for (result <- wordCountsSorted) {
      val count = result._1
      val word = result._2
      println(s"$word: $count")
    }
    
  }
  
}