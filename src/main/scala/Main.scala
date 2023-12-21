import actors.{AddCategory, AddInventoryItem, AddProduct, CategoryActor,GetLowStockInventoryItems, ReduceInventoryItem, DeleteProduct, GetAllProducts, GetCategories, GetInventoryItem, GetAllInventoryItems, GetProduct, InventoryActor, ProductActor, UpdateInventory, UpdateProduct}
import akka.actor.{ActorSystem, Props}
import entities.{Category, Inventory, Product}
import akka.pattern.ask

import scala.concurrent.duration._
import scala.concurrent.{Await, Future, Promise}
import akka.util.Timeout

import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import java.sql.Date
import scala.io.StdIn

object Main extends App {
  // Initialize Akka Actor System
  val system = ActorSystem("StockManagementSystem")

  // Create actor instances
  val productActor = system.actorOf(Props[ProductActor], "productActor")
  val inventoryActor = system.actorOf(Props[InventoryActor],"inventoryActor")
  val categoryActor = system.actorOf(Props[CategoryActor],"categoryActor")

  implicit val timeout: Timeout = Timeout(20.seconds) // Set an appropriate timeout value
  val threshold : Int = 10


  // Console-based menu loop
  var continueMenuLoop = true
  while (continueMenuLoop) {
    println("=== Stock Management System Menu ===")
    println("1. Order New Product") //Order New Product
    println("2. Restock Product") //Restock Product
    println("3. Sell Product") //Sell Product
    println("4. List All Stock")
    println("5. Get Product by ID")
    println("6. List Available Products") //List Available Products
    println("7. Add Product")
    println("8. Update Product")
    println("9. Delete Product")
    println("10. Add Category")
    println("11. List Categories")
    println("12. List Low Stock Products")//Add category    List Categories    List Low Stock Products
    println("0. Exit")

    val choice = StdIn.readLine("Enter your choice: ")

    choice match {
      case "1" =>


          val newInventoryItem = readInventoryItemFromConsole()
          inventoryActor ! AddInventoryItem(newInventoryItem)

      case "2" =>
        // Update Product
//        val updatedProduct = readProductFromConsole()
//        productActor ! UpdateProduct(updatedProduct)

          val updatedInventoryItem = readInventoryItemFromConsole()
          inventoryActor ! UpdateInventory(updatedInventoryItem)

      case "3" =>

          val inventoryId = StdIn.readLine("Enter Product's ID: ").toInt
          inventoryActor ! ReduceInventoryItem(inventoryId)


      case "4" =>
        // Get All Inventory
        val responseFuture = (inventoryActor ? GetAllInventoryItems()).mapTo[List[(Int, String, String, Double, Date,  Int,Int)]]

        // Block and wait for the response (with a timeout)
        try {
          val response = Await.result(responseFuture, timeout.duration)
          println(s"\nInventory details: ")
          response.foreach(product => println(s"Product ID: ${product._1} - Product Name: ${product._2} - Product description: ${product._3} - Product Price: ${product._4} - Product Expiry Date: ${product._5} - Product Category ID: ${product._6} - Product Stock Quantity: ${product._7}\n"))
        } catch {
          case ex: Throwable =>
            println(s"Failed to get Inventory: ${ex.getMessage}")
        }


      case "5" =>
        // Get Product by ID
        val productIdToGet = StdIn.readLine("Enter Product ID to view: ").toInt
        val responseFuture = (productActor ? GetProduct(productIdToGet)).mapTo[Product]
        try {
          val response = Await.result(responseFuture, timeout.duration)
          println(s"\nProduct details: ")
          println(s"Product ID: ${response.id} - Product Name: ${response.name} -  Product  Description: ${response.description} - Product Price: ${response.price} - Product Expiry Date: ${response.expiry_date} - Product Category: ${response.categoryId}\n")

        } catch {
          case ex: Throwable =>
            println(s"Failed to get product: ${ex.getMessage}")
        }



      case "6" =>
        // Get All Products
        val responseFuture = (productActor ? GetAllProducts()).mapTo[List[Product]]

        // Block and wait for the response (with a timeout)
        try {
          val response = Await.result(responseFuture, timeout.duration)
          println(s"\nProducts details: ")
          response.foreach(product => println(s"Product ID: ${product.id} - Product Name: ${product.name} -  Product Description: ${product.description} - Product Price: ${product.price} - Product Expiry Date: ${product.expiry_date} - Product Category: ${product.categoryId}\n"))

        } catch {
          case ex: Throwable =>
            println(s"Failed to get product: ${ex.getMessage}")
        }


      case "7" =>
        val newProduct = readNewProductFromConsole()
        productActor ! AddProduct(newProduct)



      case "8" =>
        val updatedProduct = readUpdatedProductFromConsole()
        productActor ! UpdateProduct(updatedProduct)


      case "9" =>
        val productIdToDelete = StdIn.readLine("Enter Product ID to delete: ").toInt
        productActor ! DeleteProduct(productIdToDelete)


      case "10" =>
        val newCategory = readCategoryFromConsole()
        categoryActor ! AddCategory(newCategory)


      case "11" =>
        val responseFuture = (categoryActor ? GetCategories()).mapTo[List[Category]]

        // Block and wait for the response (with a timeout)
        try {
          val response = Await.result(responseFuture, timeout.duration)
          println(s"\nCategories details: ")
          response.foreach(category => println(s"Category ID: ${category.id} - Category Name: ${category.name} -  Category Description: ${category.description}\n"))

        } catch {
          case ex: Throwable =>
            println(s"Failed to get category: ${ex.getMessage}")
        }


      case "12" =>
        val responseFuture = (inventoryActor ? GetLowStockInventoryItems(threshold)).mapTo[List[(Int, String,Int)]]

        try {
          val response = Await.result(responseFuture,timeout.duration)
          println("\nProducts that you need to restock on (Less than 10): ")
          response.foreach(product => println(s"Product ID: ${product._1} - Product Name: ${product._2} -  Product Stock Quantity: ${product._3}\n"))
        } catch {
          case ex: Throwable =>
            println(s"Failed to get Products: ${ex.getMessage}")
        }


      case "0" =>
        // Exit
        continueMenuLoop = false

      case _ =>
        println("Invalid choice. Please enter a valid option.")
    }
  }

  // Shut down the actor system when done
  system.terminate()

  private def readNewProductFromConsole(): Product = {
    val id = 0
    val name = StdIn.readLine("Enter Product Name: ")
    val description = StdIn.readLine("Enter Product Description: ")
    val price = StdIn.readLine("Enter Product Price: ").toDouble
    val expiryDate = Date.valueOf(StdIn.readLine("Enter Product Expiry Date (YYYY-MM-DD): "))
    val categoryId = StdIn.readLine("Enter Product Category ID: ").toInt

    Product(id, name, description, price, expiryDate, categoryId)
  }

  private def readUpdatedProductFromConsole(): Product = {
    val id = StdIn.readLine("Enter Product ID: ").toInt
    val name = StdIn.readLine("Enter Product Name: ")
    val description = StdIn.readLine("Enter Product Description: ")
    val price = StdIn.readLine("Enter Product Price: ").toDouble
    val expiryDate = Date.valueOf(StdIn.readLine("Enter Product Expiry Date (YYYY-MM-DD): "))
    val categoryId = StdIn.readLine("Enter Product Category ID: ").toInt

    Product(id, name, description, price, expiryDate, categoryId)
  }



  private def readInventoryItemFromConsole() : Inventory = {
    val id = 0
    val productId = StdIn.readLine("Enter Product ID: ").toInt
    val quantity = StdIn.readLine("Enter Quantity: ").toInt

    Inventory(id, productId, quantity)
  }


  private def readCategoryFromConsole(): Category = {
    val id = 0
    val name = StdIn.readLine("Enter Category Name: ")
    val description = StdIn.readLine("Enter Category Description: ")

    Category(id,name, description)
  }




}
