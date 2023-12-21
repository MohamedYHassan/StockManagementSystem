package database

import java.sql.{Connection, DriverManager, ResultSet}

object DatabaseConnector {
  val dbUrl = "jdbc:mysql://localhost:8889/stock_management"
  val dbUser = "root"
  val dbPassword = "root"


  Class.forName("com.mysql.cj.jdbc.Driver")

  val connection: Connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)



  //  // Methods for database operations
  //  def fetchData(): Seq[String] = {
  //    val statement = connection.createStatement()
  //    val resultSet: ResultSet = statement.executeQuery("SELECT * FROM your_table")
  //
  //    var results = List[String]()
  //
  //    while (resultSet.next()) {
  //      results :+= resultSet.getString("column_name")
  //    }
  //
  //    results
  //  }



}
