package services

import java.sql.{Connection, DriverManager}
import java.util.UUID

import com.typesafe.config.ConfigFactory

import scala.collection.mutable.ListBuffer

/**
 * Created by mayank on 30/3/16.
 */


trait DBService {
  val conf = ConfigFactory.load

  val driver = conf.getString("mysql.driver")
  val url = conf.getString("mysql.url")
  val username = conf.getString("mysql.username")
  val password = conf.getString("mysql.password")

  Class.forName(driver)

  def connection = DriverManager.getConnection(url, username, password)


  def execute[R](f: Connection => Option[R]): Option[R] = {
    try {
      f(connection)
    } catch {
      case ex: Exception => {
        println("Exception: " + ex.getMessage)
        None
      }
    } finally {
      connection.close
    }
  }
}

case class Book(id: String, name: String, author: String)

class BookService extends DBService {
  def save(book: Book):Boolean = {
    val process: Connection => Some[Boolean] = {
      connection =>
        val uuid = UUID.randomUUID().toString
        val query = s"INSERT INTO book(id,name,author) VALUES('$uuid','${book.name}','${book.author}')"
        val statement = connection.createStatement()
        Some(statement.execute(query))
    }
    if(execute(process).isDefined)
      true
    else
      false
  }

  def fetch(id: String): Option[Book] = {
    val process: Connection => Option[Book] = {
      connection =>
        val query = s"SELECT * FROM book WHERE id = '$id'"
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery(query)

        if (resultSet.first)
          Some(Book(resultSet.getString("id"), resultSet.getString("name"), resultSet.getString("author")))
        else
          None
    }
    execute(process)
  }

  def list: List[Book] = {
    val process: Connection => Option[List[Book]] = {
      connection =>
        val query = s"SELECT * FROM book"
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery(query)
        val listBuffer = ListBuffer.empty[Book]
        while (resultSet.next()) {
          listBuffer.append(Book(resultSet.getString("id"), resultSet.getString("name"), resultSet.getString("author")))
        }
        val books = listBuffer.toList
        if (books.isEmpty) None
        else
          Some(books)
    }
    execute(process) match{
      case Some(books)=> books
      case None => Nil
    }
  }
}

