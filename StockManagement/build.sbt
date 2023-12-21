ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

lazy val root = (project in file("."))
  .settings(
    name := "ScalaProject"
  )


libraryDependencies ++= Seq(
  "org.tpolecat" %% "doobie-core" % "0.12.1",
  "org.tpolecat" %% "doobie-hikari" % "0.12.1",
  "com.zaxxer" % "HikariCP" % "3.4.5",
  "mysql" % "mysql-connector-java" % "8.0.23",
  "com.typesafe.akka" %% "akka-persistence-typed" % "2.8.0"
)

