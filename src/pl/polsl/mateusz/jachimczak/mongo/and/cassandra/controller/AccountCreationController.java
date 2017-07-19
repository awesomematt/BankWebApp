package pl.polsl.mateusz.jachimczak.mongo.and.cassandra.controller;

import pl.polsl.mateusz.jachimczak.mongo.and.cassandra.model.cassandra.CassandraConnector;
import pl.polsl.mateusz.jachimczak.mongo.and.cassandra.model.mongo.MongoDBJDBC;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/signup")
public class AccountCreationController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    public AccountCreationController() {
        super();
    }

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher rd = getServletContext().getRequestDispatcher("/signup.jsp");
        rd.forward(request, response);
    }

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {

        String name = request.getParameter("newName");
        String surname = request.getParameter("newSurname");
        String pesel = request.getParameter("newPesel");
        String username = request.getParameter("newUsername");
        String password = request.getParameter("newPassword");
        //FOR MONGO DATABASE
        if (checkInputDataAndCreateNewUserAccount(request, name, surname, pesel, username, password)) {
        //--------------------------------------
        //FOR CASSANDRA DATABASE
        //if (checkInputDataAndCreateNewUserAccountForCassandra(request, name, surname, pesel, username, password)) {
            //--------------------------------------
            RequestDispatcher rd = getServletContext().getRequestDispatcher("/creationdone.jsp");
            rd.forward(request, response);
        } else {
            request.setAttribute("creationError", "Error: Some fields are left blank or username already exists.");
            RequestDispatcher rd = getServletContext().getRequestDispatcher("/signup.jsp");
            rd.forward(request, response);
        }

    }

    private boolean checkInputDataAndCreateNewUserAccount(HttpServletRequest request, String name, String surname, String pesel, String username, String password) {
        DatabaseOperationsHandler dbHandler = DatabaseOperationsHandler.getInstance();
        MongoDBJDBC dbInstance = dbHandler.initializeAndGetDBInstance(request);
        if (name != null && surname != null && pesel != null && username != null && password != null) {
            if (!dbHandler.checkIfUserExists(dbInstance, username)) {
                dbHandler.createNewUser(dbInstance, name, surname, pesel, username, password);
                return true;
            } else
                return false;
        } else
            return false;
    }

    private boolean checkInputDataAndCreateNewUserAccountForCassandra(HttpServletRequest request, String name, String surname, String pesel, String username, String password) {
        DatabaseOperationsHandler dbHandler = DatabaseOperationsHandler.getInstance();
        CassandraConnector client = dbHandler.initializeAndGetCassandraDBInstance(request);
        if (name != null && surname != null && pesel != null && username != null && password != null) {
            if (!dbHandler.checkIfUserExistsInCassandraDB(client, username)) {
                dbHandler.createNewUserForCassandra(client, name, surname, pesel, username, password);
                return true;
            } else
                return false;
        } else
            return false;
    }
}
