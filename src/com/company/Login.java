package com.company;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

//Abstract login class that can be used with any GUI program that requires two different views, one for generic Owners, and one for generic Customers
//we let descendants define how to insert Owners and Customers into the hashmap so that they can be whatever descendants of those objects that we require
// we also let descendants define the different views so they are specific to the program

public abstract class Login {

    private HashMap<PairCopy<String, String>, Person> users;
    private HashSet<String> usernames;

    public Login() {
        users = new HashMap();
        usernames = new HashSet();
    }

    public Person getUser(String u, String p){
    	SQLConnection sc = new SQLConnection();
		sc.TheSqlConnection();
		Person user = sc.getUser(u, p);
		sc.close();
        return user;
    }

    public void placeComponents() {
        JFrame frame = new JFrame("FancyBank");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setVisible(false);

        frame.setSize(285, 180);
        JPanel panel = new JPanel();
        frame.getContentPane().removeAll();
        frame.getContentPane().add(panel);

        panel.setLayout(null);

        JLabel userLabel = new JLabel("Username");
        userLabel.setBounds(10, 10, 80, 25);
        panel.add(userLabel);

        JTextField userText = new JTextField(20);
        userText.setBounds(100, 10, 160, 25);
        panel.add(userText);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setBounds(10, 40, 80, 25);
        panel.add(passwordLabel);

        JPasswordField passwordText = new JPasswordField(20);
        passwordText.setBounds(100, 40, 160, 25);
        panel.add(passwordText);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(10, 80, 80, 25);
        panel.add(loginButton);

        JButton registerButton = new JButton("Register as Banker");
        registerButton.setBounds(100, 110, 160, 25);
        panel.add(registerButton);

        JButton registerButtonC = new JButton("Register as Customer");
        registerButtonC.setBounds(100, 80, 160, 25);
        panel.add(registerButtonC);

        frame.setVisible(true);

        ActionListener loginButtonListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String t = userText.getText();
                String p = new String (passwordText.getPassword());
                JButton source = (JButton) e.getSource();

//                System.out.println("t: " + t);
//                System.out.println("p: " + p);

                if (t.isEmpty() || t == null || p.isEmpty() || p == null){
                    JOptionPane.showMessageDialog(source, "Username or password has not been entered!");
                }
                else{
                    Person user = getUser(t, p);
                    if (user != null){
                        if (user.getType().equals("banker")){
                            ownerView((Owner) user);
                            frame.dispose();
                        }
                        else if (user.getType().equals("customer")){
                        	System.out.println(user.getType());
                            customerView((Customer) user);
                            frame.dispose();
                        }
                    }
                    else{
                        JOptionPane.showMessageDialog(source, "Username or password was incorrect!");
                    }
                }
            }
        };
        loginButton.addActionListener(loginButtonListener);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String t = userText.getText();
                String p = new String (passwordText.getPassword());
                JButton source = (JButton) e.getSource();
                if (t.isEmpty() || t == null || p.isEmpty() || p == null){
                    JOptionPane.showMessageDialog(source, "Username or password has not been entered!");
                }
                else {
                    if (!usernames.contains(t)) {
                        Owner o = new Owner(t, p);
                        o.setType("banker");
                        boolean check = addUser(o);
                        if (!check) {
                            JOptionPane.showMessageDialog(source, "Username and password is already associated with an account");
                        } else {
                            JOptionPane.showMessageDialog(source, "Account Created!");
                            usernames.add(t);
                        }
                    } else {
                        JOptionPane.showMessageDialog(source, "Username taken!");
                    }
                }
            }
        });


        registerButtonC.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String t = userText.getText();
                String p = new String (passwordText.getPassword());
                JButton source = (JButton) e.getSource();

                if (t.isEmpty() || t == null || p.isEmpty() || p == null){
                    JOptionPane.showMessageDialog(source, "Username or password has not been entered!");
                }
                else {

                    if (!usernames.contains(t)) {

                        Customer c = new Customer(t, p);
                        c.setType("customer");
                        boolean check = addUser(c);
                        if (!check) {
                            JOptionPane.showMessageDialog(source, "Username is already associated with an account");
                        } else {
                            JOptionPane.showMessageDialog(source, "Account Created!");
                            usernames.add(t);
                        }
                    } else {
                        JOptionPane.showMessageDialog(source, "Username taken!");
                    }
                }
            }
        });
    }

    public void put(PairCopy p, Person pp){
        users.put(p, pp);
    }

    public Person remove(PairCopy p){
        return users.remove(p);
    }

    public Collection<Person> values(){
        return users.values();
    }

    public Person get(PairCopy p){
        return users.get(p);
    }

    public abstract void ownerView(Owner o);

    public abstract void customerView(Customer c);

    public abstract boolean addUser(Person p);

    public abstract boolean addOwner(Owner o);
}
