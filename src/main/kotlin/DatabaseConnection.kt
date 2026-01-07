package warehouse.app

import java.sql.*

object DatabaseConnection {
    private const val URL = "jdbc:mysql://localhost:3306/warehouse_db"
    private const val USER = "root"
    private const val PASSWORD = ""

    fun getConnection(): Connection {
        return DriverManager.getConnection(URL, USER, PASSWORD)
    }

    fun closeConnection(conn: Connection?, stmt: Statement?, rs: ResultSet?) {
        try {
            rs?.close()
            stmt?.close()
            conn?.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun testConnection(): Boolean {
        return try {
            getConnection().use {
                println("Database connected successfully!")
                true
            }
        } catch (e: Exception) {
            println("Connection failed: ${e.message}")
            false
        }
    }
}