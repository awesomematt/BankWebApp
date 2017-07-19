package pl.polsl.mateusz.jachimczak.mongo.and.cassandra.model.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

import java.util.ArrayList;
import java.util.List;

public class CassandraConnector {
    private static CassandraConnector instance = null;
    private static Session session;
    private static Cluster cluster;

    protected CassandraConnector() {

    }

    public static CassandraConnector getInstance() {
        if (instance == null) {
            instance = new CassandraConnector();
        }
        return instance;
    }

    public void startConnection(String ipAdress) {
        this.cluster = Cluster.builder().addContactPoint(ipAdress).build();
        session = cluster.connect();
    }

    public void closeConnection() {
        cluster.close();
    }

    public List<String> getCassandraDatabaseInfo() {
        List<String> cassInfo = new ArrayList<>();
        final Metadata metadata = cluster.getMetadata();
        Row row = getSession().execute("select release_version, cql_version from system.local").one();
        String cassandraVersion = row.getString("release_version");
        String cqlVersion = row.getString("cql_version");
        row = session.execute("SELECT COUNT(*) FROM user.users").one();
        Long userTableRowsCount = row.getLong("count");
        row = session.execute("SELECT COUNT(*) FROM account.accounts").one();
        Long accountTableRowsCount = row.getLong("count");
        String userInfo = "Total rows in USERS table in USER keyspace: " + userTableRowsCount;
        String accountInfo = "Total rows in ACCOUNTS table in ACCOUNT keyspace: " + accountTableRowsCount;
        cassInfo.add(cassandraVersion);
        cassInfo.add(cqlVersion);
        cassInfo.add(metadata.getClusterName());
        cassInfo.add(userInfo);
        cassInfo.add(accountInfo);
        return cassInfo;
    }

    public static Session getSession() {
        return session;
    }
}
