package actors
import akka.actor.{Actor, ActorLogging}
import entities.Product
import database.ProductRepo
import database.DatabaseConnector

// Define messages
case class UpdateProduct(product: Product)
case class DeleteProduct(productId: Int)
case class AddProduct(product: Product)
case class GetProduct(productId:Int)
case class GetAllProducts()

// Product actor
class ProductActor extends Actor with ActorLogging {
  var products: Map[Int, Product] = Map.empty

  override def receive: Receive = {
    case UpdateProduct(product) =>
      updateProduct(product)

    case DeleteProduct(productId) =>
      deleteProduct(productId)


    case AddProduct(product)=>
      addProduct(product)



    case GetProduct(productId)=>
       getProduct(productId)


    case GetAllProducts()=>
      getAllProducts()
  }

  private def updateProduct(product: Product): Unit = {
    ProductRepo.updateProduct(DatabaseConnector.connection,product)
    products += (product.id -> product)
  }

  private def deleteProduct(productId: Int): Unit = {
    ProductRepo.deleteProduct(DatabaseConnector.connection, productId)

    // Update the products map by excluding the product with the specified productId
    products = products.filterNot { case (_, product) => product.id == productId }
  }

  private def addProduct(product: Product): Unit = {
    ProductRepo.insertProduct(DatabaseConnector.connection,product)
    products += (product.id -> product)
  }

  private def getProduct(productId: Int): Unit = {
    val product = ProductRepo.getProduct(DatabaseConnector.connection, productId)
    sender() ! product // Send the product back to the sender
  }

  private def getAllProducts(): Unit = {
    val products = ProductRepo.getAllProducts(DatabaseConnector.connection)
    sender() ! products // Send the list of products back to the sender
  }


}
