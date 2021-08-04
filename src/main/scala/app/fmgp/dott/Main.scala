package app.fmgp.dott

import scala.util.CommandLineParser.FromString
import scala.collection.parallel.CollectionConverters._
import scala.util.chaining._

import java.time.Instant
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.ZonedDateTime
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object Constants {
  val datetimePattern = "yyyy-MM-dd HH:mm:ss"
  val formatter = java.time.format.DateTimeFormatter.ofPattern(datetimePattern)
  val timeZone = java.time.ZoneOffset.UTC

  val defaultGroups = Seq(
      Grouping(1, Some(3), "1-3"),
      Grouping(4, Some(6), "4-6"),
      Grouping(7, Some(12), "7-12"),
      Grouping(12, None, ">12"),
    )

}




//run "2018-01-01 00:00:00" "2018-01-01 00:00:00" "1-3" "4-6" "7-12" ">12" "0-25"
@main def tool(start: Instant, end: Instant, others: Grouping*): Unit = {
  val groups =
    if(! others.isEmpty) others
    else Constants.defaultGroups

  for {
    //orders <- Future(Data.generateOrders(3))
    orders <- Future{Data.orders}
    count = orders
      .par //all parallel computation comes from here
      .filter(o => o.purchaseDate.isAfter(start) && o.purchaseDate.isBefore(end))
      .map{ o => 
        //debug o.tap(println)
        groups.map(i => if (i.check(o)) 1 else 0).toArray
      }
      .fold(Array.ofDim[Int](groups.size)){(a,b)  => a.zip(b).map(u => u._1 + u._2)}

    outputText = groups.zip(count).map((g,c) => s"${g.text} months: $c orders")
   } yield outputText.map(println)

}





given FromString[Instant] = new FromString[Instant] {
  def fromString(s: String): Instant = 
    try { LocalDateTime.parse(s, Constants.formatter).toInstant(Constants.timeZone) } catch {
      case ex:java.time.format.DateTimeParseException => 
        //throw new CommandLineParser.ParseError(0,s"Argument must be in following format: '$datetimePattern'")
        throw IllegalArgumentException(s"Argument must be in following format: '${Constants.datetimePattern}'")
    }
}

given FromString[Grouping] = new FromString[Grouping] {
  def fromString(str: String): Grouping = 
    try { 
      if (str.contains('>')) Grouping(str.replace(">", "").toInt, None, str)
      else str.split("-") match {
        case Array(s,e) => Grouping(s.toInt, Some(e.toInt), str)
      }
    } catch {
      case ex: (scala.MatchError | java.lang.NumberFormatException) => 
        throw IllegalArgumentException(s"Arguments for grouping must be in following formats: '1-2 3-6 >6' (the numbers represents months)")
    }
}

case class Grouping(minMonth: Int, maxMonth: Option[Int], text: String) {
  val mustBeBefore = YearMonth.now.minusMonths(minMonth).atDay(1).atStartOfDay().toInstant(Constants.timeZone)
  val mustBeAfterOrEqual = maxMonth.map(e => YearMonth.now.minusMonths(e).atDay(1).atStartOfDay().toInstant(Constants.timeZone))

  def check(o:Order): scala.Boolean = o.items.exists(i => 
    i.product.creationDate.isBefore(mustBeBefore) && mustBeAfterOrEqual.map(ts => !i.product.creationDate.isBefore(ts)).getOrElse(true)
  )
}