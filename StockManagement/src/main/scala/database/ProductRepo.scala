package database
import entities.Product

import java.sql.{Connection, Date, PreparedStatement, ResultSet}

object ProductRepo {

  def insertProduct(connection: Connection, product: Product): Unit = {
    val insertProductSQL = "INSERT INTO product (name, price,description,expiry_date,category_id) VALUES (?,?,?,?,?)"

    val preparedStatement: PreparedStatement = connection.prepareStatement(insertProductSQL)
    preparedStatement.setString(1, product.name)
    preparedStatement.setDouble(2, product.price)
    preparedStatement.setString(3,product.description)
    preparedStatement.setDate(4,product.expiry_date)
    preparedStatement.setInt(5,product.categoryId)

    preparedStatement.executeUpdate()

    preparedStatement.close()
  }
  def updateProduct(connection: Connection, product: Product): Unit = {
    val updateProductSQL =
      "UPDATE product SET name = ?, price = ?, description = ?, expiry_date = ?, category_id = ? WHERE id = ?"
    val preparedStatement: PreparedStatement = connection.prepareStatement(updateProductSQL)
    preparedStatement.setString(1, product.name)
    preparedStatement.setDouble(2, product.price)
    preparedStatement.setString(3, product.description)
    preparedStatement.setDate(4, product.expiry_date)
    preparedStatement.setInt(5, product.categoryId)
    preparedStatement.setInt(6, product.id)
    preparedStatement.executeUpdate()
    preparedStatement.close()
  }

  def deleteProduct(connection: Connection, productId: Int): Unit = {
    val deleteProductSQL = "DELETE FROM product WHERE id = ?"
    val preparedStatement: PreparedStatement = connection.prepareStatement(deleteProductSQL)
    preparedStatement.setInt(1, productId)
    preparedStatement.executeUpdate()
    preparedStatement.close()
  }
  def getAllProducts(connection: Connection): List[Product] = {
    val selectAllSQL = "SELECT * FROM product"

    val statement = connection.createStatement()
    val resultSet: ResultSet = statement.executeQuery(selectAllSQL)

    var products: List[Product] = List()

    while (resultSet.next()) {
      val id = resultSet.getInt("id")
      val name = resultSet.getString("name")
      val price = resultSet.getDouble("price")
      val description = resultSet.getString("description")
      val expiry_date = resultSet.getDate("expiry_date")
      val category_id = resultSet.getInt("category_id")


      products = products :+ Product(id, name,description, price,expiry_date,category_id)
    }

    statement.close()
    resultSet.close()

    products
  }

  def getProduct(connection: Connection, productId: Int): Product = {
    val selectSQL = "SELECT * FROM product WHERE id = ?"

    var preparedStatement: PreparedStatement = null

    try {
      preparedStatement = connection.prepareStatement(selectSQL)
      preparedStatement.setInt(1, productId)

      val resultSet: ResultSet = preparedStatement.executeQuery()

      if (resultSet.next()) {
        val id = resultSet.getInt("id")
        val name = resultSet.getString("name")
        val price = resultSet.getDouble("price")
        val description = resultSet.getString("description")
        val expiry_date = resultSet.getDate("expiry_date")
        val category_id = resultSet.getInt("category_id")

        Product(id, name, description, price, expiry_date, category_id)
      } else {
        // Return a default product or throw an exception based on your logic
        // For now, returning an empty Product
        Product(0, "", "", 0.0, new Date(0), 0)
      }
    } finally {
      if (preparedStatement != null) {
        preparedStatement.close()
      }
    }
  }


}
