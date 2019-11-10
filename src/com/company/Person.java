package com.company;

//Generic Person with username and password
public class Person {
    private String username;
    private String password;
    private String type;

    public Person (String u, String p){
        username = u;
        password = p;
        type = "";
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

	public String getType() {
		return type;
	}

	public void setType(String t) {
		type = t;
	}
    
    
}
