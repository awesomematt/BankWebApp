package pl.polsl.mateusz.jachimczak.mongo.and.cassandra.model.cassandra;

import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import pl.polsl.mateusz.jachimczak.mongo.and.cassandra.model.Account;

public class CassAccountDAOImpl implements CassAccountDAO {

    @Override
    public Account findAccount(Session session, String username) {
        Account account = null;
        Row row = session.execute("SELECT * FROM account.accounts WHERE username=? ALLOW FILTERING", username).one();
        Integer id;
        if (row != null) {
            id = row.getInt("userid");
            account = new Account(row.getString("username"),
                    id.toString(),
                    row.getString("accountnumber"),
                    row.getString("accountpin"),
                    row.getDouble("accountbalance"));
        }
        return account;
    }

    @Override
    public boolean insertAccount(Session session, Account account) {
        session.execute("INSERT INTO account.accounts (username, userid, accountnumber, accountpin, accountbalance) VALUES (?, ?, ?, ?, ?)",
                account.getUsername(),
                Integer.parseInt(account.getUserID()),
                account.getAccountNumber(),
                account.getAccountPin(),
                account.getAccountBalance());
        return true;
    }

    @Override
    public boolean updateAccount(Session session, Account account) {
        session.execute("UPDATE account.accounts SET username=?, accountnumber=?, accountpin=?, accountbalance=? "
                        + "WHERE userid=?",
                account.getUsername(),
                account.getAccountNumber(),
                account.getAccountPin(),
                account.getAccountBalance(),
                Integer.parseInt(account.getUserID()));
        return true;
    }

    @Override
    public boolean deleteAccount(Session session, Account account) {
        session.execute("DELETE FROM account.accounts WHERE userid=?", Integer.parseInt(account.getUserID()));
        return true;
    }

    public Row getRawAccountDataFromTable(Session session, String username) {
        Row row = session.execute("SELECT * FROM account.accounts WHERE username=? ALLOW FILTERING", username).one();
        return row;
    }
}
