package upgrad;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.sql.*;

public class Driver {

    /**
     * Driver class main method
     * @param args
     * @throws SQLException
     */
    public static void main(String[] args) {
        // MySql credentials
        String url = "jdbc:mysql://pgc-sd-bigdata.cyaielc9bmnf.us-east-1.rds.amazonaws.com:3306/pgcdata";
        String user = "student";
        String password = "STUDENT123";

        // MongoDB Configurations
        String noSqlDBName = "upgrad";
        String mongoConnectionString = "mongodb://ec2-52-21-86-115.compute-1.amazonaws.com:27017";
        String noSqlCollectionName = "products";

        // Connection Default Value Initialization
        Connection sqlConnection = null;
        MongoClient mongoClient = null;

        try {
            // Creating database connections
            sqlConnection = DriverManager.getConnection(url, user, password);
            mongoClient = MongoClients.create(mongoConnectionString);
            MongoDatabase db = mongoClient.getDatabase(noSqlDBName);
            MongoCollection<Document> collection = db.getCollection(noSqlCollectionName);

            // Import data into MongoDb
            CRUDHelper.importDataToMongoDB(sqlConnection, "mobiles", "Mobile", collection);
            CRUDHelper.importDataToMongoDB(sqlConnection, "cameras", "Camera", collection);
            CRUDHelper.importDataToMongoDB(sqlConnection, "headphones", "Headphone", collection);

            // List all products in the inventory
            CRUDHelper.displayAllProducts(collection);

            // Display top 5 Mobiles
            CRUDHelper.displayTop5Mobiles(collection);

            // Display products ordered by their categories in Descending Order Without autogenerated Id
            CRUDHelper.displayCategoryOrderedProductsDescending(collection);

            // Display product count in each category
            CRUDHelper.displayProductCountByCategory(collection);

            // Display wired headphones
            CRUDHelper.displayWiredHeadphones(collection);
        }
        catch(SQLException ex) {
            System.out.println("Got Exception in SQL Query Execution.");
            ex.printStackTrace();
        }
        catch(Exception ex) {
            System.out.println("Got other Exception.");
            ex.printStackTrace();
        }
        finally {
            if(null != mongoClient) {
                mongoClient.close();
            }
            if(null != sqlConnection){
                try {
                    sqlConnection.close();
                }
                catch (SQLException ex) {
                    System.out.println("Got Exception in SQL Connection Termination.");
                    ex.printStackTrace();
                }
            }
        }
    }
}