package pl.polsl.mateusz.jachimczak.mongo.and.cassandra.model.cassandra;


import com.datastax.driver.core.Session;

public class CassTables {

    public void createUserTable(Session session) {
        final String createUserKeyspaceCql = "CREATE KEYSPACE user" +
                "  WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 3 }";
        session.execute(createUserKeyspaceCql);
        final String createUsersTableCql = "CREATE TABLE user.users (userid int, name varchar, surname varchar, pesel varchar, "
                + "username varchar, password varchar, admin boolean, PRIMARY KEY (userid))";
        session.execute(createUsersTableCql);
    }

    public void createAccountTable(Session session) {
        final String createAccountKeyspaceCql = "CREATE KEYSPACE account" +
                "  WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 3 }";
        session.execute(createAccountKeyspaceCql);
        final String createAccountsTableCql = "CREATE TABLE account.accounts (username varchar, userid int, accountnumber varchar, "
                + "accountpin varchar, accountbalance double, PRIMARY KEY (userid))";
        session.execute(createAccountsTableCql);
    }
}
