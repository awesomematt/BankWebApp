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
import java.util.ArrayList;
import java.util.List;

@WebServlet("/myaccount")
public class AccountController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public AccountController() {
        super();
    }

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {

        String username = request.getSession().getAttribute("username").toString();
        if ((username != null) && (!username.equals(""))) {
            //FOR MONGO DATABASE
            request.setAttribute("userData", getAccountDetails(request, username));
            //-----------------------------------------------------------------------
            //FOR CASSANDRA DATABASE
            //request.setAttribute("userData", getAccountDetailsFromCassandra(request, username));
            //-----------------------------------------------------------------------
            RequestDispatcher rd = getServletContext().getRequestDispatcher("/myaccount.jsp");
            rd.forward(request, response);
        } else {
            RequestDispatcher rd = getServletContext().getRequestDispatcher("/unauthorized.jsp");
            rd.forward(request, response);
        }
    }

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {
        String editAccount = request.getParameter("editAccount");
        String paymentAmount = request.getParameter("paymentAmount");
        String transferAmount = request.getParameter("transferAmount");
        String username = request.getSession().getAttribute("username").toString();
        String dataEdition = request.getParameter("dataEdition");
        if ((username != null) && (!username.equals("")))
            //FOR MONGO DATABASE
            request.setAttribute("userData", getAccountDetails(request, username));
            //-----------------------------------------------------------------------
            //FOR CASSANDRA DATABASE
            //request.setAttribute("userData", getAccountDetailsFromCassandra(request, username));
            //------------------------------------------------------------------------------------
        else {
            RequestDispatcher rd = getServletContext().getRequestDispatcher("/unauthorized.jsp");
            rd.forward(request, response);
        }
        if ((editAccount != null) && (!editAccount.equals(""))) {
            request.setAttribute("edition", "Edition mode, please edit your desired fields");
            RequestDispatcher rd = getServletContext().getRequestDispatcher("/myaccount.jsp");
            rd.forward(request, response);
        }
        if ((dataEdition != null) && (dataEdition.equals("true"))) {
            //FOR MONGO DATABASE
            saveEditedData(request, username);
            //------------------------------------------------------------------------------------
            //FOR CASSANDRA DATABASE
            //saveEditedDataForCassandra(request, username);
            //------------------------------------------------------------------------------------
            request.setAttribute("success", "Data edited successfully");
            RequestDispatcher rd = getServletContext().getRequestDispatcher("/myaccount.jsp");
            String editedUsername = request.getSession().getAttribute("username").toString();
            //FOR MONGO DATABASE
            request.setAttribute("userData", getAccountDetails(request, editedUsername));
            //------------------------------------------------------------------------------------
            //FOR CASSANDRA DATABASE
            //request.setAttribute("userData", getAccountDetailsFromCassandra(request, editedUsername));
            //------------------------------------------------------------------------------------------
            rd.forward(request, response);
        }
        //FOR MONGO DATABASE
        String actionMade = accountBalanceManagement(request, paymentAmount, transferAmount, username);
        //-----------------------------------------------------------------------------------------------
        //FOR CASSANDRA DATABASE
        //String actionMade = accountBalanceManagementForCassandra(request, paymentAmount, transferAmount, username);
        //--------------------------------------------------------------------------------------------------------
        if (!actionMade.equals("none") && !actionMade.equals("")) {
            if (actionMade.equals("payment")) {
                request.setAttribute("paymentInfo", "Payment amount was successfully added to your account");
                RequestDispatcher rd = getServletContext().getRequestDispatcher("/myaccount.jsp");
                //FOR MONGO DATABASE
                request.setAttribute("userData", getAccountDetails(request, username));
                //----------------------------------------------------------------------------------
                //FOR CASSANDRA DATABASE
                //request.setAttribute("userData", getAccountDetailsFromCassandra(request, username));
                //------------------------------------------------------------------------------------
                rd.forward(request, response);
            } else {
                request.setAttribute("transferInfo", "Transfer done");
                RequestDispatcher rd = getServletContext().getRequestDispatcher("/myaccount.jsp");
                //FOR MONGO DATABASE
                request.setAttribute("userData", getAccountDetails(request, username));
                //----------------------------------------------------------------------------------
                //FOR CASSANDRA DATABASE
                //request.setAttribute("userData", getAccountDetailsFromCassandra(request, username));
                //------------------------------------------------------------------------------------
                rd.forward(request, response);
            }
        }
    }

    private List<String> getAccountDetails(HttpServletRequest request, String username) {
        DatabaseOperationsHandler dbHandler = DatabaseOperationsHandler.getInstance();
        return dbHandler.getUserDataWithAccountBalance(dbHandler.initializeAndGetDBInstance(request), username);
    }

    private List<String> getAccountDetailsFromCassandra(HttpServletRequest request, String username) {
        DatabaseOperationsHandler dbHandler = DatabaseOperationsHandler.getInstance();
        return dbHandler.getUserDataWithAccountBalanceFromCassandra(dbHandler.initializeAndGetCassandraDBInstance(request), username);
    }

    private String accountBalanceManagement(HttpServletRequest request, String paymentAmount, String transferAmount, String username) {
        String actionMade = "none";
        if (paymentAmount != null) {
            DatabaseOperationsHandler dbHandler = DatabaseOperationsHandler.getInstance();
            MongoDBJDBC dbInstance = dbHandler.initializeAndGetDBInstance(request);
            Double amount = Double.parseDouble(paymentAmount.replaceAll(" ", "."));
            dbHandler.addAmountToAccountBalance(dbInstance, amount, username);
            actionMade = "payment";
        }
        if (transferAmount != null) {
            DatabaseOperationsHandler dbHandler = DatabaseOperationsHandler.getInstance();
            MongoDBJDBC dbInstance = dbHandler.initializeAndGetDBInstance(request);
            Double amount = Double.parseDouble(transferAmount.replaceAll(" ", "."));
            dbHandler.subAmountFromAccountBalance(dbInstance, amount, username);
            actionMade = "transfer";
        }
        return actionMade;
    }

    private String accountBalanceManagementForCassandra(HttpServletRequest request, String paymentAmount, String transferAmount, String username) {
        String actionMade = "none";
        if (paymentAmount != null) {
            DatabaseOperationsHandler dbHandler = DatabaseOperationsHandler.getInstance();
            CassandraConnector client = dbHandler.initializeAndGetCassandraDBInstance(request);
            Double amount = Double.parseDouble(paymentAmount.replaceAll(" ", "."));
            dbHandler.addAmountToAccountBalanceForCassandra(client, amount, username);
            actionMade = "payment";
        }
        if (transferAmount != null) {
            DatabaseOperationsHandler dbHandler = DatabaseOperationsHandler.getInstance();
            CassandraConnector client = dbHandler.initializeAndGetCassandraDBInstance(request);
            Double amount = Double.parseDouble(transferAmount.replaceAll(" ", "."));
            dbHandler.subAmountFromAccountBalanceForCassandra(client, amount, username);
            actionMade = "transfer";
        }
        return actionMade;
    }

    private void saveEditedData(HttpServletRequest request, String username) {
        List<String> listOfChanges = new ArrayList<>();
        String editedUsername = request.getParameter("editedUsername");
        DatabaseOperationsHandler dbHandler = DatabaseOperationsHandler.getInstance();
        MongoDBJDBC dbInstance = dbHandler.initializeAndGetDBInstance(request);
        listOfChanges.add(username);
        listOfChanges.add(request.getParameter("editedName"));
        listOfChanges.add(request.getParameter("editedSurname"));
        listOfChanges.add(request.getParameter("editedPesel"));
        listOfChanges.add(editedUsername);
        listOfChanges.add(request.getParameter("editedPassword"));
        if ((editedUsername != null) && (!editedUsername.equals("")))
            request.getSession().setAttribute("username", editedUsername);
        dbHandler.editUserData(dbInstance, listOfChanges);
    }

    private void saveEditedDataForCassandra(HttpServletRequest request, String username) {
        List<String> listOfChanges = new ArrayList<>();
        String editedUsername = request.getParameter("editedUsername");
        DatabaseOperationsHandler dbHandler = DatabaseOperationsHandler.getInstance();
        CassandraConnector client = dbHandler.initializeAndGetCassandraDBInstance(request);
        listOfChanges.add(username);
        listOfChanges.add(request.getParameter("editedName"));
        listOfChanges.add(request.getParameter("editedSurname"));
        listOfChanges.add(request.getParameter("editedPesel"));
        listOfChanges.add(editedUsername);
        listOfChanges.add(request.getParameter("editedPassword"));
        if ((editedUsername != null) && (!editedUsername.equals("")))
            request.getSession().setAttribute("username", editedUsername);
        dbHandler.editUserDataForCassandra(client, listOfChanges);
    }
}
