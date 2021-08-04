package app.fmgp.dott

import java.time.Instant


case class Customer(name: String, contact: String)

opaque type Address = String
object Address:
  def apply(s: String): Address = s

opaque type MonetaryValue = Double
object MonetaryValue:
  def apply(s: Double): MonetaryValue = s

/** Order: contains general information about the order (customer name and contact, shipping address, grand total, date when the order was placed, ...) */
case class Order(customer: Customer, shipping: Address, grandTotal: MonetaryValue, purchaseDate: Instant, items: Seq[Item])

/** Item: information about the purchased item (cost, shipping fee, tax amount, ...) */
case class Item(cost: MonetaryValue, shippingFee: MonetaryValue, tax: MonetaryValue, product: Product)

/** Product: information about the product (name, category, weight, price, creation date, ...) */
case class Product(name: String, category: String, price: MonetaryValue, weight: Double, creationDate: Instant)
