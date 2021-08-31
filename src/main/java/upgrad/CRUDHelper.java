package upgrad;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import org.bson.Document;

import java.sql.*;
import java.util.Arrays;

import static com.mongodb.client.model.Projections.excludeId;
import static com.mongodb.client.model.Projections.fields;

public class CRUDHelper {

    /**
     * Import data from MySql to MongoDB
     * @param connection
     * @param tableName
     * @param category
     * @param collection
     * @throws SQLException
     */
    public static void importDataToMongoDB(Connection connection, String tableName, String category, MongoCollection<Document> collection) throws SQLException {
        Statement statement = connection.createStatement();
        String sql = "select * from " + tableName;
        ResultSet resultSet = statement.executeQuery(sql);
        ResultSetMetaData rsMetaData = resultSet.getMetaData();
        int columnCount = rsMetaData.getColumnCount();

        while (resultSet.next()) {
            Document doc = new Document();
            for(int i = 1; i<=columnCount; i++) {
                doc.append(rsMetaData.getColumnName(i), resultSet.getString(rsMetaData.getColumnName(i)));
            }
            doc.append("Category", category);
            collection.insertOne(doc);
        }

        statement.close();
    }

    /**
     * Display ALl products
     * @param collection
     */
    public static void displayAllProducts(MongoCollection<Document> collection) {
        System.out.println("------ Displaying All Products ------");
        // Call printSingleCommonAttributes to display the attributes on the Screen
        MongoCursor<Document> cursor = collection.find().cursor();

        while(cursor.hasNext()){
            PrintHelper.printSingleCommonAttributes(cursor.next());
        }
    }

    /**
     * Display top 5 Mobiles
     * @param collection
     */
    public static void displayTop5Mobiles(MongoCollection<Document> collection) {
        System.out.println("------ Displaying Top 5 Mobiles ------");
        // Call printAllAttributes to display the attributes on the Screen
        MongoCursor<Document> cursor = collection
                .find(new Document("Category", "Mobile"))
                .limit(5)
                .cursor();

        while(cursor.hasNext()){
            PrintHelper.printAllAttributes(cursor.next());
        }
    }

    /**
     * Display products ordered by their categories in Descending order without auto generated Id
     * @param collection
     */
    public static void displayCategoryOrderedProductsDescending(MongoCollection<Document> collection) {
        System.out.println("------ Displaying Products ordered by categories ------");
        // Call printAllAttributes to display the attributes on the Screen
        MongoCursor<Document> cursor = collection
                .find()
                .projection(fields(excludeId()))
                .sort(new Document("Category", -1))
                .limit(5)
                .cursor();

        while(cursor.hasNext()){
            PrintHelper.printAllAttributes(cursor.next());
        }
    }


    /**
     * Display number of products in each group
     * @param collection
     */
    public static void displayProductCountByCategory(MongoCollection<Document> collection) {
        System.out.println("------ Displaying Product Count by categories ------");
        // Call printProductCountInCategory to display the attributes on the Screen

        MongoCursor<Document> cursor = collection.aggregate(
                Arrays.asList(
                        Aggregates.group("$Category", Accumulators.sum("Count", 1))
                )).cursor();

        while(cursor.hasNext()){
            PrintHelper.printProductCountInCategory(cursor.next());
        }
    }

    /**
     * Display Wired Headphones
     * @param collection
     */
    public static void displayWiredHeadphones(MongoCollection<Document> collection) {
        System.out.println("------ Displaying Wired headphones ------");
        // Call printAllAttributes to display the attributes on the Screen
        Document query = new Document()
                .append("Category", "Headphone")
                .append("ConnectorType", "Wired");
        MongoCursor<Document> cursor = collection.find(query).cursor();

        while(cursor.hasNext()){
            PrintHelper.printAllAttributes(cursor.next());
        }
    }
}