package pl.polsl.mateusz.jachimczak.mongo.and.cassandra.controller;

import pl.polsl.mateusz.jachimczak.mongo.and.cassandra.model.cassandra.CassandraConnector;
import pl.polsl.mateusz.jachimczak.mongo.and.cassandra.model.mongo.MongoDBJDBC;
import pl.polsl.mateusz.jachimczak.mongo.and.cassandra.view.DBLogs;


/**
 * Author: inż. Mateusz Jachimczak
 * Politechnika Śląska (Silesian University of Technology)
 * AEiI INF SSM
 *
 * INFORMATION: All methods which contains "Cass" or "Cassandra" in their names are dedicated for Cassandra
 * Database purpose only. There are many comments in the source code which will guide you, if you'll want to
 * switch DBs for the web application. Comments with statement "For security test ONLY" shows the possibilities
 * of hacking your way through the code, into the database records. Both databases has been tested for backdoors
 * and security holes. The whole research data has been added into the final documentation. This application is a
 * simulation of a bank account logging system. Dedicated to check the possibilities of hacking into the tested databases,
 * connected with the web application. (Tested databases: MongoDB and Cassandra).
 */

public class Main {
    public static void main(String args[]) {
        testConnectionForMongoDB();
        //testConnectionForCassandra();
    }

    private static void testConnectionForMongoDB() {
        MongoDBJDBC dbInstance = MongoDBJDBC.getInstance();
        dbInstance.initializeDB("localhost", 27017, "clients");
        dbInstance.initializeCollections();
        DatabaseOperationsHandler dbHandler = DatabaseOperationsHandler.getInstance();
        MessageHandler msgHandler = MessageHandler.getInstance();
        dbHandler.initializeExampleDocuments(dbInstance);

        //For security test ONLY
        //dbHandler.testHack(dbInstance);
        //dbHandler.adminHackFirst(dbInstance);
        //dbHandler.adminHackSecond(dbInstance);

        msgHandler.printAllDatabaseLogs(dbInstance);
    }

    private static void testConnectionForCassandra() {
        CassandraConnector client = CassandraConnector.getInstance();
        client.startConnection("127.0.0.1");
        DBLogs logs = DBLogs.getInstance();
        DatabaseOperationsHandler dbHandler = DatabaseOperationsHandler.getInstance();
        dbHandler.initializeExampleDataForCassandra(client);
        logs.printCassandraDBLogs(client.getCassandraDatabaseInfo());

        //For security test ONLY
        //dbHandler.acquireAdminFlagInCassandra(client);
        //dbHandler.acquireExtraData(client);

        client.closeConnection();

    }
}
