package pl.polsl.mateusz.jachimczak.mongo.and.cassandra.model;

public class Account {
    private String username;
    private String userID;
    private String accountNumber;
    private String accountPin;
    private Double accountBalance;

    public Account(String username, String userID, String accountNumber, String accountPin, Double accountBalance) {
        this.username = username;
        this.userID = userID;
        this.accountNumber = accountNumber;
        this.accountPin = accountPin;
        this.accountBalance = accountBalance;
    }

    public String getUsername() {
        return username;
    }

    public String getUserID() {
        return userID;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountPin() {
        return accountPin;
    }

    public Double getAccountBalance() {
        return accountBalance;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setAccountPin(String accountPin) {
        this.accountPin = accountPin;
    }

    public void setAccountBalance(Double accountBalance) {
        this.accountBalance = accountBalance;
    }
}
