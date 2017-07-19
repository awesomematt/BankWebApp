package pl.polsl.mateusz.jachimczak.mongo.and.cassandra.controller;

import pl.polsl.mateusz.jachimczak.mongo.and.cassandra.model.cassandra.CassandraConnector;
import pl.polsl.mateusz.jachimczak.mongo.and.cassandra.model.mongo.MongoDBJDBC;
import pl.polsl.mateusz.jachimczak.mongo.and.cassandra.view.DBLogs;

import java.util.ArrayList;
import java.util.List;

public class MessageHandler {
    private static MessageHandler instance = null;
    private static DBLogs databaseLogs;

    protected MessageHandler() {
        databaseLogs = DBLogs.getInstance();
    }

    public static MessageHandler getInstance() {
        if (instance == null)
            instance = new MessageHandler();
        return instance;
    }

    public static void printAllDatabaseLogs(MongoDBJDBC dbInstance) {
        String dbName = dbInstance.getDBName();
        if (dbName != "") {
            List<String> collections = dbInstance.getDatabase().listCollectionNames().into(new ArrayList<String>());
            databaseLogs.connectionMessage(dbName);
            databaseLogs.showCollections(collections);
            databaseLogs.showDocumentsCount(collections, dbInstance.getDatabase());
        } else
            databaseLogs.connectionErrorMessage();
    }

    public List<String> getAllDatabaseLogsForAdmin(MongoDBJDBC dbInstance) {
        List<String> dbLogs = new ArrayList<>();
        List<String> collections = dbInstance.getDatabase().listCollectionNames().into(new ArrayList<String>());
        dbLogs.add("--Database Information--");
        dbLogs.add("MongoDB server version: 3.4.2");
        dbLogs.add(databaseLogs.getNamesOfCollections(collections));
        dbLogs.add(databaseLogs.getCountOfDocumentsInCollections(collections, dbInstance.getDatabase()));
        return dbLogs;
    }

    public List<String> getAllDatabaseLogsForAdminFromCassandra(CassandraConnector client) {
        List<String> dbLogs = client.getCassandraDatabaseInfo();
        dbLogs.add(0, "--Database Information--");
        return dbLogs;
    }
}
