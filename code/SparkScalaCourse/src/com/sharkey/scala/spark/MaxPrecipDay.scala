package com.sharkey.spark.scala

import org.apache.spark._
import org.apache.spark.SparkContext._
import org.apache.log4j._
import scala.math.max

/** Find the minimum temperature by weather station */
object MaxPrecipDay {
  
  def parseLine(line:String)= {
    val fields = line.split(",")
    val day = fields(1)
    val entryType = fields(2)
    val precip = fields(3)
    (day, entryType, precip)
  }
    /** Our main function where the action happens */
  def main(args: Array[String]) {
   
    // Set the log level to only print errors
    Logger.getLogger("org").setLevel(Level.ERROR)
    
    // Create a SparkContext using every core of the local machine
    val sc = new SparkContext("local[*]", "MaxPrecipDay")
    
    // Read each line of input data
    val lines = sc.textFile("../../data/1800.csv")
    
    // Convert to (stationID, entryType, temperature) tuples
    val parsedLines = lines.map(parseLine)

    // Filter out all but TMIN entries
    val maxPrecip = parsedLines.filter(x => x._2 == "PRCP")
    
    // Convert to (stationID, temperature)
    val dayPrecip = maxPrecip.map(x => (x._1, x._3.toFloat))
    
    // Reduce by stationID retaining the minimum temperature found
    val maxPrecipByDay = dayPrecip.reduceByKey( (x,y) => max(x,y))
    
    // Collect, format, and print the results
    val results = maxPrecipByDay.max()
    
    println(results)
    //for (result <- results.sorted) {
    //   val day = result._1
    //   val precip = result._2
    //   println(s"$day maximum precip: $precip") 
    //}
      
  }
}