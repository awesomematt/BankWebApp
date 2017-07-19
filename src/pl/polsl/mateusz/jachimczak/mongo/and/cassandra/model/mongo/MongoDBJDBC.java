package pl.polsl.mateusz.jachimczak.mongo.and.cassandra.model.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoDBJDBC {

    private static MongoDBJDBC instance = null;
    private static MongoClient mongoClient;
    private static MongoDatabase database;
    private static MongoCollection<BasicDBObject> userCollection;
    private static MongoCollection<BasicDBObject> accountCollection;


    protected MongoDBJDBC() {
    }

    public static MongoDBJDBC getInstance() {
        if (instance == null) {
            instance = new MongoDBJDBC();
        }
        return instance;
    }

    public static void initializeDB(String ipAddress, Integer port, String dbName) {
        try {
            mongoClient = new MongoClient(ipAddress, port);
            database = mongoClient.getDatabase(dbName);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public static void initializeCollections() {
        userCollection = database.getCollection("user", BasicDBObject.class);
        accountCollection = database.getCollection("account", BasicDBObject.class);
    }

    public static void setMongoClientAndInitializeDB(MongoClient mongoClient, String dbName) {
        MongoDBJDBC.mongoClient = mongoClient;
        database = mongoClient.getDatabase(dbName);
    }

    public static MongoDatabase getDatabase() {
        return database;
    }

    public static String getDBName() {
        return database.getName();
    }

    public static MongoCollection<BasicDBObject> getUserCollection() {
        return userCollection;
    }

    public static MongoCollection<BasicDBObject> getAccountCollection() {
        return accountCollection;
    }
}
