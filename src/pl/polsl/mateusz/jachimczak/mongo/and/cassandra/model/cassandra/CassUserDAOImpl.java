package pl.polsl.mateusz.jachimczak.mongo.and.cassandra.model.cassandra;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import pl.polsl.mateusz.jachimczak.mongo.and.cassandra.model.User;

import java.util.ArrayList;
import java.util.List;

public class CassUserDAOImpl implements CassUserDAO {

    @Override
    public User findUser(Session session, String username) {
        User user = null;
        Row row = session.execute("SELECT * FROM user.users WHERE username=? ALLOW FILTERING", username).one();
        if (row != null)
            user = new User(row.getString("name"),
                    row.getString("surname"),
                    row.getString("pesel"),
                    row.getString("username"),
                    row.getString("password"));
        return user;
    }

    //For security test ONLY
//    public User findUser(Session session, String username, String password) {
//        User user = null;
//        Row row = session.execute("SELECT * FROM user.users WHERE username='" + username
//                + "' and password='" + password
//                + "' ALLOW FILTERING").one();
//        if (row != null)
//            user = new User(row.getString("name"),
//                    row.getString("surname"),
//                    row.getString("pesel"),
//                    row.getString("username"),
//                    row.getString("password"));
//        return user;
//    }

    @Override
    public boolean insertUser(Session session, User user, Integer id) {
        session.execute("INSERT INTO user.users (userid, name, surname, pesel, username, password) VALUES (?, ?, ?, ?, ?, ?)",
                id,
                user.getName(),
                user.getSurname(),
                user.getPesel(),
                user.getUsername(),
                user.getPassword());
        return true;
    }

    @Override
    public boolean updateUser(Session session, User user, Integer userID) {
        session.execute("UPDATE user.users SET name=?, surname=?, pesel=?, username=?, password=? "
                        + "WHERE userid =?",
                user.getName(),
                user.getSurname(),
                user.getPesel(),
                user.getUsername(),
                user.getPassword(),
                userID);
        return true;
    }

    //For security test ONLY
//    public boolean updateUser(Session session, User user, Integer userID) {
//        session.execute("UPDATE user.users SET name='" + user.getName()
//                + "', surname='" + user.getSurname()
//                + "', pesel='" + user.getPesel()
//                + "', username='" + user.getUsername()
//                + "', password='" + user.getPassword() + "' "
//                + "WHERE userid ='" + userID + "'");
//        return true;
//    }

    @Override
    public boolean deleteUser(Session session, User user) {
        session.execute("DELETE FROM user.users WHERE userid=?", getUserID(session, user.getUsername()));
        return true;
    }

    public List<String> getAllUsersIds(Session session) {
        List<String> userIds = new ArrayList<>();
        Row row = session.execute("SELECT COUNT(*) FROM user.users").one();
        Long count = row.getLong("count");
        Integer currentID = 1;
        for (int i = 0; i < count; i++) {
            userIds.add(currentID.toString());
            currentID++;
        }
        return userIds;
    }

    public List<String> getAllUsernames(Session session) {
        List<String> listOfUsernames = new ArrayList<>();
        ResultSet rs = session.execute("SELECT * FROM user.users");
        for (Row row : rs) {
            listOfUsernames.add(row.getString("username"));
        }
        return listOfUsernames;
    }

    public boolean insertAdminUser(Session session, User user, Integer id) {
        boolean admin = true;
        session.execute("INSERT INTO user.users (userid, name, surname, pesel, username, password, admin) VALUES (?, ?, ?, ?, ?, ?, ?)",
                id,
                user.getName(),
                user.getSurname(),
                user.getPesel(),
                user.getUsername(),
                user.getPassword(),
                admin);
        return true;
    }

    public Integer getUserID(Session session, String username) {
        Row row = session.execute("SELECT * FROM user.users WHERE username=? ALLOW FILTERING", username).one();
        Integer userID = row.getInt("userid");
        return userID;
    }

    public Boolean checkAdminFlag(Session session, String username) {
        Row row = session.execute("SELECT * FROM user.users WHERE username=? ALLOW FILTERING", username).one();
        Boolean adminFlag = row.getBool("admin");
        if (adminFlag)
            return true;
        else
            return false;
    }

    public boolean checkIfUsersTableExists(Session session) {
        Row row = session.execute("SELECT table_name" +
                " FROM system_schema.tables WHERE keyspace_name='user'").one();
        if (row != null && row.getString("table_name").equals("users"))
            row = session.execute("SELECT COUNT(*) FROM user.users").one();
        else
            return false;
        Long count;
        if (row != null) {
            count = row.getLong("count");
            if (count != null && count != 0)
                return true;
            else
                return false;
        } else
            return false;
    }

    public Row getRawUserDataFromTable(Session session, String username) {
        Row row = session.execute("SELECT * FROM user.users WHERE username=? ALLOW FILTERING", username).one();
        return row;
    }
}
