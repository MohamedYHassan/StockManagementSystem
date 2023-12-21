package database
import entities.{Inventory, Product}

import java.sql.{Connection, Date, PreparedStatement, ResultSet}
object InventoryRepo {

  def insertInventoryItem(connection: Connection, inventory: Inventory): Unit = {
    val insertInventorySQL = "INSERT INTO inventory (product_id,quantity) VALUES (?,?)"
    val preparedStatement: PreparedStatement = connection.prepareStatement(insertInventorySQL)
    preparedStatement.setInt(1, inventory.productId)
    preparedStatement.setInt(2, inventory.quantity)

    preparedStatement.executeUpdate()
    preparedStatement.close()
  }

  def deleteInventoryItem(connection: Connection, inventoryId: Int): Unit = {
    val deleteInventorySQL = "DELETE FROM inventory WHERE id = ?"
    val preparedStatement: PreparedStatement = connection.prepareStatement(deleteInventorySQL)
    preparedStatement.setInt(1, inventoryId)
    preparedStatement.executeUpdate()
    preparedStatement.close()
  }

  def updateInventoryItem(connection: Connection, inventory: Inventory): Unit = {
    val updateInventorySQL = "UPDATE inventory SET quantity = ? WHERE product_id = ?"
    val preparedStatement: PreparedStatement = connection.prepareStatement(updateInventorySQL)
    preparedStatement.setInt(1, inventory.quantity)
    preparedStatement.setInt(2, inventory.productId)
    preparedStatement.executeUpdate()
    preparedStatement.close()
  }

  def getAllInventoryItemsWithProductDetails(connection: Connection): List[(Int, String, String, Double, Date,  Int,Int)] = {
    val selectAllWithProductSQL =
      """
        |SELECT p.id AS product_id, p.name, p.description, p.price, p.expiry_date, p.category_id,
        |        i.quantity
        |FROM product p
        |JOIN inventory i ON p.id = i.product_id
        |""".stripMargin

    val statement = connection.createStatement()
    val resultSet: ResultSet = statement.executeQuery(selectAllWithProductSQL)

    var inventoryWithProductDetails: List[(Int, String, String, Double, Date, Int,  Int)] = List()

    while (resultSet.next()) {
      val productId = resultSet.getInt("product_id")
      val name = resultSet.getString("name")
      val description = resultSet.getString("description")
      val price = resultSet.getDouble("price")
      val expiryDate = resultSet.getDate("expiry_date")
      val categoryId = resultSet.getInt("category_id")

      val quantity = resultSet.getInt("quantity")

      // Add the result to the list
      inventoryWithProductDetails = inventoryWithProductDetails :+ (productId, name, description, price, expiryDate, categoryId,  quantity)
    }

    statement.close()
    resultSet.close()

    inventoryWithProductDetails
  }

  def getAllInventoryItems(connection: Connection): List[Inventory] = {
    val selectAllSQL = "SELECT * FROM inventory"

    val statement = connection.createStatement()
    val resultSet: ResultSet = statement.executeQuery(selectAllSQL)
    var inventoryEntries: List[Inventory] = List()
    while (resultSet.next()) {
      val id = resultSet.getInt("id")
      val productId = resultSet.getInt("product_id")
      val quantity = resultSet.getInt("quantity")
      inventoryEntries = inventoryEntries :+ Inventory(id, productId, quantity)
    }
    statement.close()
    resultSet.close()
    inventoryEntries
  }
  def getInventoryItem(connection: Connection, productId: Int): Inventory = {
    val selectSQL = "SELECT * FROM inventory WHERE product_id = ?"

    var preparedStatement: PreparedStatement = null

    try {
      preparedStatement = connection.prepareStatement(selectSQL)
      preparedStatement.setInt(1, productId)

      val resultSet: ResultSet = preparedStatement.executeQuery()

      if (resultSet.next()) {
        val id = resultSet.getInt("id")
        val productId = resultSet.getInt("product_id")
        val quantity = resultSet.getInt("quantity")


        Inventory(id, productId, quantity)
      } else {
        // Return a default product or throw an exception based on your logic
        // For now, returning an empty Product
        Inventory(0, 0, 0)
      }
    } finally {
      if (preparedStatement != null) {
        preparedStatement.close()
      }
    }
  }


  def getProductsToRestock(connection: Connection, threshold: Int): List[(Int, String,Int)] = {
    val selectLowQuantitySQL =
      """
        |SELECT p.id,p.name, i.quantity
        |FROM product p
        |JOIN inventory i ON p.id = i.product_id
        |WHERE i.quantity < ?
        |""".stripMargin

    var preparedStatement: PreparedStatement = null

    try {
      preparedStatement = connection.prepareStatement(selectLowQuantitySQL)
      preparedStatement.setInt(1, threshold)

      val resultSet: ResultSet = preparedStatement.executeQuery()

      var productsToRestock: List[(Int, String, Int)]  = List()

      while (resultSet.next()) {
        val id = resultSet.getInt("id")
        val name = resultSet.getString("name")
        val quantity = resultSet.getInt("quantity")

        // Create a tuple with the correct types
        val product: (Int, String, Int) = (id, name, quantity)

        // Add the product to the list
        productsToRestock = productsToRestock :+ product
      }

      productsToRestock
    } finally {
      if (preparedStatement != null) {
        preparedStatement.close()
      }
    }
  }



}




