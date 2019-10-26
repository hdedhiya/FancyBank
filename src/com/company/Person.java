package com.company;

//Generic Person with username and password
public class Person {
    private String username;
    private String password;

    public Person (String u, String p){
        username = u;
        password = p;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String p){
        password = p;
    }
}
