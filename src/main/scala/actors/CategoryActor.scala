package actors
import akka.actor.{Actor,ActorLogging}
import entities.Category
import database.{DatabaseConnector,CategoryRepo}

// Define messages
case class AddCategory(category: Category)
case class GetCategories()

class CategoryActor extends Actor with ActorLogging {

  override def receive: Receive = {
    case AddCategory(category) =>
      addCategory(category)

    case GetCategories() =>
      getCategories()


  }


  private def addCategory(category: Category): Unit = {
    CategoryRepo.insertCategory(DatabaseConnector.connection,category)
  }

  private def getCategories(): Unit = {
    val categories = CategoryRepo.getAllCategories(DatabaseConnector.connection)
    sender() ! categories
  }

}
