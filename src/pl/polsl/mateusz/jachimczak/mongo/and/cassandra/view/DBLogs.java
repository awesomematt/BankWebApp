package pl.polsl.mateusz.jachimczak.mongo.and.cassandra.view;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import pl.polsl.mateusz.jachimczak.mongo.and.cassandra.model.cassandra.CassandraConnector;

import java.util.List;

public class DBLogs {
    private static DBLogs instance = null;

    protected DBLogs() {
    }

    public static DBLogs getInstance() {
        if (instance == null) {
            instance = new DBLogs();
        }
        return instance;
    }

    // MongoDB logs ---->
    public static void connectionMessage(String dbName) {
        System.out.println("(--Welcome--)");
        System.out.println("Connection to the database: " + dbName + " (--Successful--)");
    }

    public static void connectionErrorMessage() {
        System.out.println("Couldn't connect to the database. (--Error--)");
    }

    public static void showCollections(List<String> collections) {
        System.out.println("Detected: " + collections.size() + " collections in database.");
        for (String s : collections) {
            System.out.println("Name: " + s);
        }
    }

    public static void showDocumentsCount(List<String> collections, MongoDatabase database) {
        MongoCollection<BasicDBObject> coll;
        for (String s : collections) {
            coll = database.getCollection(s, BasicDBObject.class);
            System.out.println("Number of documents in collection: " + s + " = " + coll.count());
        }
    }

    public static String getNamesOfCollections(List<String> collections) {
        String colNames = ("Detected: " + collections.size() + " collections in database.");
        for (String s : collections) {
            colNames = colNames + System.lineSeparator() + ("Name: " + s);
        }
        return colNames;
    }

    public static String getCountOfDocumentsInCollections(List<String> collections, MongoDatabase database) {
        MongoCollection<BasicDBObject> coll;
        String docCount = ("Number of documents in collection: ");
        for (String s : collections) {
            coll = database.getCollection(s, BasicDBObject.class);
            docCount = docCount + System.lineSeparator() + s + " = " + coll.count();
        }
        return docCount;
    }
    // <---- End of MongoDB logs

    // Cassandra logs ---->
    public static void printCassandraDBLogs(List<String> dbInfo) {
        System.out.println("Connection to Cassandra Database - Successful\n");
        System.out.println("---------------------------------------------");
        System.out.println("Cassandra Version: " + dbInfo.get(0));
        System.out.println("CQL Version: " + dbInfo.get(1));
        System.out.println("Cluster Name: " + dbInfo.get(2));
        System.out.println(dbInfo.get(3));
        System.out.println(dbInfo.get(4));
        System.out.println("----------------End of log-------------------");
    }
    // <---- End of Cassandra logs
}
