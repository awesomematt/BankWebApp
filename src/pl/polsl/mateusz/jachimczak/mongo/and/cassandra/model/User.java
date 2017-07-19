package pl.polsl.mateusz.jachimczak.mongo.and.cassandra.model;

public class User {

    private String name;
    private String surname;
    private String pesel;
    private String username;
    private String password;

    public User(String name, String surname, String pesel, String username, String password) {
        this.name = name;
        this.surname = surname;
        this.pesel = pesel;
        this.username = username;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getPesel() {
        return pesel;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setPesel(String pesel) {
        this.pesel = pesel;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
