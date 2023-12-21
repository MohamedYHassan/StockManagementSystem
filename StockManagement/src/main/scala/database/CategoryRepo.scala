package database
import java.sql.{Connection, PreparedStatement, ResultSet}
import entities.Category

object CategoryRepo {

  def insertCategory(connection: Connection,category: Category): Unit = {
    val insertCategorySQL = "INSERT INTO  category (name,description) VALUES (?,?)"
    val preparedStatement: PreparedStatement = connection.prepareStatement(insertCategorySQL)
    preparedStatement.setString(1,category.name)
    preparedStatement.setString(2,category.description)

    preparedStatement.executeUpdate()
    preparedStatement.close()
  }


  def getAllCategories(connection: Connection): List[Category] = {
    val selectAllSQL = "SELECT * FROM  category"
    val statement = connection.createStatement()
    val resultSet: ResultSet = statement.executeQuery(selectAllSQL)
    var categoryEntries: List[Category] = List()
    while(resultSet.next()) {
      val id = resultSet.getInt("id")
      val name = resultSet.getString("name")
      val description = resultSet.getString("description")
      categoryEntries = categoryEntries :+ Category(id, name, description)
    }
    statement.close()
    resultSet.close()
    categoryEntries
  }

}
