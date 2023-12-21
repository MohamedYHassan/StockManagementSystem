package entities

import java.sql.Date

case class Product(id:Int, name:String, description:String, price:Double, expiry_date:Date, categoryId:Int)
