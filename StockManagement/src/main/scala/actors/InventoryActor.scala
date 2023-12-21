package actors
import akka.actor.{Actor, ActorLogging}
import entities.Inventory
import database.{DatabaseConnector, InventoryRepo}

import java.sql.Date

// Define messages
case class UpdateInventory(inventory: Inventory)
case class AddInventoryItem(inventory: Inventory)
case class ReduceInventoryItem(inventoryId: Int)
case class GetAllInventoryItems()
case class GetInventoryItem(inventoryId: Int)
case class GetLowStockInventoryItems(threshold: Int)

// Inventory actor
class InventoryActor extends Actor with ActorLogging {
  var inventories: Map[Int, Inventory] = Map.empty

  def receive: Receive = {
    case UpdateInventory(inventory) =>
      updateInventory(inventory)
      log.info(s"Inventory updated: $inventory")


    case AddInventoryItem(inventory) =>
      val potentialInventory = getInventoryItem(inventory.productId)
      if (potentialInventory.id == 0 && potentialInventory.productId == 0 && potentialInventory.quantity == 0 ) {


        addInventoryItem(inventory)


      }
      else {
        val otherInventory = Inventory(inventory.id, inventory.productId, potentialInventory.quantity + inventory.quantity)
        updateInventory(otherInventory)




      }
      log.info(s"Inventory Item added: $inventory")


    case ReduceInventoryItem(inventoryId) =>
      reduceInventoryItem(inventoryId)

    case GetAllInventoryItems()=>
      getAllInventoryItems()


    case GetInventoryItem(inventoryId)=>
      getInventoryItem(inventoryId)



    case GetLowStockInventoryItems(threshold) =>
      getLowStockInventoryItems(threshold)



  }

  private def updateInventory(inventory: Inventory): Unit = {
    val tempInventory = getInventoryItem(inventory.productId)
    val updateInventory = Inventory(inventory.id,inventory.productId,inventory.quantity + tempInventory.quantity)
    InventoryRepo.updateInventoryItem(DatabaseConnector.connection,updateInventory)
  }

  private def reduceInventoryItem(inventoryId : Int):Unit = {
    val tempInventory = getInventoryItem(inventoryId)
    if(tempInventory.quantity - 5 <= 0 ) {
      val newInventory = Inventory(tempInventory.id,tempInventory.productId,0)
      updateInventory(newInventory)
    }
    else {
      val newInventory = Inventory(tempInventory.id,tempInventory.productId,tempInventory.quantity - 5)
      updateInventory(newInventory)
    }
  }

  private def addInventoryItem(inventory:Inventory): Unit = {
    InventoryRepo.insertInventoryItem(DatabaseConnector.connection,inventory)
  }

//  private def deleteInventoryItem(inventoryId: Int): Unit = {
//    InventoryRepo.deleteInventoryItem(DatabaseConnector.connection,inventoryId)
//  }

  private def getAllInventoryItems(): Unit = {
    val inventoryItems = InventoryRepo.getAllInventoryItemsWithProductDetails(DatabaseConnector.connection)
    sender() ! inventoryItems
  }

  private def getInventoryItem(inventoryId: Int): Inventory = {
    InventoryRepo.getInventoryItem(DatabaseConnector.connection,inventoryId)
  }


  private def getLowStockInventoryItems(threshold: Int): Unit = {
    val lowStockItems = InventoryRepo.getProductsToRestock(DatabaseConnector.connection,threshold)
    sender() ! lowStockItems
  }
}
