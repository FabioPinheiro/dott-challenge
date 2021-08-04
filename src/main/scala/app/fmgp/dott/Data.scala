package app.fmgp.dott

import java.time.Instant
import scala.util.Random


//This is the orders.jar 
object Data {
  val nowInstant = Instant.now()

  val secondInADay = 60*60*24 
  val orders = generateOrders(10000)
  def generateOrders(numberOfOrder: Int) =
    (0 to numberOfOrder-1).map{ i => 
      val item = Item(
        cost =  MonetaryValue(1),
        shippingFee = MonetaryValue(0),
        tax = MonetaryValue(0), // =)
        product = Product(
            name = s"product $i",
            category = "",
            price =  MonetaryValue(1),
            weight =  1.1,
            creationDate = nowInstant.minusSeconds(  (Random.nextInt(365*2)+31) * secondInADay)
            )
        )
      Order(
        customer = Customer(s"Name $i",""),
        shipping = Address(s"Street n'$i"),
        grandTotal = MonetaryValue(1),
        purchaseDate = nowInstant.minusSeconds(  Random.nextInt(10) * secondInADay),
        items = Seq(item)
      )
    }.toSeq
}