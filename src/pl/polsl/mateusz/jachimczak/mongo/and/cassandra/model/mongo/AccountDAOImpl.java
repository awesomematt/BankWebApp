package pl.polsl.mateusz.jachimczak.mongo.and.cassandra.model.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import pl.polsl.mateusz.jachimczak.mongo.and.cassandra.model.Account;

import java.util.ArrayList;
import java.util.List;

public class AccountDAOImpl implements AccountDAO {

    @Override
    public BasicDBObject findDocInCollection(MongoCollection<BasicDBObject> collection, String key, String value) {
        BasicDBObject query = new BasicDBObject();
        query.put(key, value);
        BasicDBObject foundDocument = collection.find(query).first();
        return foundDocument;
    }

    @Override
    public boolean insertAccount(MongoCollection<BasicDBObject> collection, Account account) {
        collection.insertOne(createDocumentFromAccountObject(account));
        return true;
    }

    @Override
    public boolean updateAccount(MongoCollection<BasicDBObject> collection, BasicDBObject accountOld, Account accountNew) {
        collection.replaceOne(accountOld, createDocumentFromAccountObject(accountNew));
        return true;
    }

    @Override
    public boolean deleteAccount(MongoCollection<BasicDBObject> collection, BasicDBObject account) {
        collection.deleteOne(account);
        return true;
    }

    public BasicDBObject createDocumentFromAccountObject(Account account) {
        BasicDBObject doc = new BasicDBObject("username", account.getUsername()).
                append("user_id", account.getUserID()).
                append("accountNumber", account.getAccountNumber()).
                append("accountPin", account.getAccountPin()).
                append("accountBalance", account.getAccountBalance());
        return doc;
    }

    public List<Account> createListOfExampleAccounts(List<String> exampleUsersIds) {
        List<Account> accounts = new ArrayList<>();
        accounts.add(new Account("zbysiu65", exampleUsersIds.get(0), "78456873298479823742", "2434", 4767.88));
        accounts.add(new Account("lukas666", exampleUsersIds.get(1), "3706413298479823742", "2432", 2567.88));
        accounts.add(new Account("robcio88", exampleUsersIds.get(2), "83725904467779823742", "5454", 567.88));
        accounts.add(new Account("ZibiMistrz", exampleUsersIds.get(3), "113678353298479823742", "4574", 6567.88));
        accounts.add(new Account("ZbigniewH", exampleUsersIds.get(4), "22729873298422378455", "4565", 3567.88));
        accounts.add(new Account("karolcia90", exampleUsersIds.get(5), "33459873298479825587", "9986", 1567.88));
        accounts.add(new Account("elzbieta99", exampleUsersIds.get(6), "82257873298479829999", "8457", 9067.88));
        accounts.add(new Account("StasiaTheBest", exampleUsersIds.get(7), "44529873298479828899", "3345", 8067.88));
        accounts.add(new Account("kamilcia93", exampleUsersIds.get(8), "12345873298479812345", "3334", 67.88));
        accounts.add(new Account("Januszysko98", exampleUsersIds.get(9), "78909873298479827890", "7754", 4567.88));
        return accounts;
    }
}
