package com.company;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Array;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;

import java.util.LinkedHashMap;
import java.util.Random;

public class Bank extends Login{

    //constants related to fees and set of currencies set on instantiation
    private Currency[] allCurrencies;
    private double openAccountFee = 5;
    private double closeAccountFee = 10;
    private double withdrawalFee = 2;
    private double checkingAccountTransactionFee = 1;
    private int transactionCounter;
    private double savingsInterestRate = .1;
    private double loanInterestRate = .2;
    private double savingsMinAmount = 500;
    private double maxLoanPercent = 1.0;


    public Bank(Currency[] c, double openFee, double closeFee, double withFee, double checkingFee, double savingsRate, double loanRate, double minAmount, double maxLoan){
        super();
        allCurrencies = c;
        transactionCounter = 0;
        openAccountFee = openFee;
        closeAccountFee = closeFee;
        withdrawalFee = withFee;
        checkingAccountTransactionFee = checkingFee;
        savingsInterestRate = savingsRate;
        loanInterestRate = loanRate;
        savingsMinAmount = minAmount;
        maxLoanPercent = maxLoan;
    }

    public boolean addUser(Person person) {
        String u = person.getUsername();
        String p = person.getPassword();
        String t = person.getType();
		boolean success = Person.addUser(u, p, t);
		return success;
    }

    @Override

    //provides methods that the owner can perform, like applyInterest, and view Recent Transactions
    public void ownerView(Owner o){

        JFrame frame = new JFrame("FancyBank");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Banker bk = (Banker)o;

        frame.setVisible(false);

        frame.setSize(365, 200);
        JPanel panel = new JPanel();
        frame.getContentPane().removeAll();
        frame.getContentPane().add(panel);

        panel.setLayout(null);

        JLabel label = new JLabel("Hello " + bk.getUsername() + "!", JLabel.LEFT);
        label.setBounds(10, 10, 160, 25);
        panel.add(label);

        addBackToLoginButton(panel, frame);
        addChangePWButton(panel, frame, bk);

        JButton viewTs = new JButton("Transactions by Date");
        viewTs.setBounds(10, 70, 160, 25);
        panel.add(viewTs);

        //add to current selection
        viewTs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton source = (JButton) e.getSource();

                String d = JOptionPane.showInputDialog("Enter date to query (Format: yyyy/MM/dd): ");
                if (d != null) {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
                    java.util.Date parsed = null;
                    try {
                        parsed = format.parse(d);
                        java.sql.Date sql = new java.sql.Date(parsed.getTime());
                        ArrayList<Transaction> ts = Transaction.queryTransactionsByDate(sql);
                        BankerViewTransactions vr = new BankerViewTransactions(Bank.this);
                        vr.place(bk, ts);

                        frame.dispose();
                    } catch (ParseException ex) {
                        JOptionPane.showMessageDialog(source, "Incorrect Format");

                    }
                }
            }
        });

        JButton applyInterest = new JButton("Apply Interest");
        applyInterest.setBounds(180, 70, 160, 25);
        panel.add(applyInterest);

        applyInterest.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton source = (JButton) e.getSource();

                Account.applySavingsInterest(savingsInterestRate, savingsMinAmount);
                Account.applyLoanInterestRate(loanInterestRate);

                JOptionPane.showMessageDialog(source, "Applied Interest to valid accounts!");
            }

        });

        JButton viewAccs = new JButton("Accounts by User");
        viewAccs.setBounds(10, 100, 160, 25);
        panel.add(viewAccs);

        viewAccs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton source = (JButton) e.getSource();

                String d = JOptionPane.showInputDialog("Enter username: ");
                if (d != null) {
                    ArrayList<Account> ts = BankCustomer.getAccounts(d);
                    BankerViewAccounts bva = new BankerViewAccounts(Bank.this);
                    bva.place(bk, ts);
                    frame.dispose();
                }
            }
        });



        JButton viewTransactions = new JButton("Transactions by Acc");
        viewTransactions.setBounds(180, 100, 160, 25);
        panel.add(viewTransactions);

        viewTransactions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton source = (JButton) e.getSource();

                String d = JOptionPane.showInputDialog("Enter account number: ");
                if (d != null) {
                    try {
                        ArrayList<Transaction> ts = Account.getTransactions(Integer.valueOf(d));
                        BankerViewTransactions vr = new BankerViewTransactions(Bank.this);
                        vr.place(bk, ts);
                        frame.dispose();
                    }
                    catch(NumberFormatException ex){
                        JOptionPane.showMessageDialog(source, "Enter a valid number!");
                    }
                }
            }
        });


        //a button to randomly update the stock price
        JButton updateStockMarket = new JButton("Stock update");
        updateStockMarket.setBounds(10, 130, 160, 25);
        panel.add(updateStockMarket);
        updateStockMarket.addActionListener(new ActionListener() {
            @Override

            public void actionPerformed(ActionEvent e) {
                JButton source = (JButton) e.getSource();

                StockMarket stockMarket = new StockMarket();
                ArrayList<Stock> market = stockMarket.getStockMarket();
                for(Stock stock : market) {
                    Random random = new Random();
                    int r = random.nextInt(20);
                    double percentage = 1.0 + (10-r) / 100.0;
                    double newPrice = stock.getPrice() * percentage;
                    StockMarket.updateMarketPrice(newPrice, stock.getCode());
                }

                String[] fees = {"The stock market price has been updated "};
                JOptionPane.showMessageDialog(source, fees);

            }
        });

        JButton queryStocks = new JButton("Query Stock by Acc");
        queryStocks.setBounds(180, 130, 160, 25);
        panel.add(queryStocks);
        queryStocks.addActionListener(new ActionListener() {
            @Override

            public void actionPerformed(ActionEvent e) {
                JButton source = (JButton) e.getSource();

                String d = JOptionPane.showInputDialog("Enter account number: ");
                if (d != null) {

                    try {
                        Account a = BankCustomer.getAccount(Integer.valueOf(d));
                        if (a instanceof Savings) {
                            ArrayList<Stock> stocks = SecurityAccount.getStocks(Integer.valueOf(d));
                            BankerViewStocks bvs = new BankerViewStocks(Bank.this);
                            bvs.place(bk, (Savings)a);
                            frame.dispose();
                        }
                    }
                    catch(NumberFormatException ex){
                        JOptionPane.showMessageDialog(source, "Enter a valid number!");
                    }
                }


            }
        });
    }

    //provides methods customers can perform, like viewAccounts, viewInfo,
    public void customerView(Customer c){

        JFrame frame = new JFrame("FancyBank");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        BankCustomer bc = (BankCustomer)c;

        frame.setVisible(false);

        frame.setSize(365, 140);
        JPanel panel = new JPanel();
        frame.getContentPane().removeAll();
        frame.getContentPane().add(panel);

        panel.setLayout(null);

        addBackToLoginButton(panel, frame);
        addChangePWButton(panel, frame, bc);


        JLabel label = new JLabel("Hello " + c.getUsername() + "!", JLabel.LEFT);
        label.setBounds(10, 10, 140, 25);
        panel.add(label);

        JButton viewAccounts = new JButton("View Accounts");
        viewAccounts.setBounds(10, 70, 160, 25);
        panel.add(viewAccounts);
        viewAccounts.addActionListener(new ActionListener() {
            @Override

            public void actionPerformed(ActionEvent e) {
                ViewAccounts v = new ViewAccounts(Bank.this);
                v.place(bc);
                frame.dispose();
            }
        });

        JButton viewInfo = new JButton("Rates and fees");
        viewInfo.setBounds(180, 70, 160, 25);
        panel.add(viewInfo);
        viewInfo.addActionListener(new ActionListener() {
            @Override

            public void actionPerformed(ActionEvent e) {
                JButton source = (JButton) e.getSource();
                String[] fees = {"The open account fee: " + openAccountFee, "The close account fee: " + closeAccountFee, "The withdrawal fee: " + withdrawalFee, "The checking account fee: " + checkingAccountTransactionFee};
                JOptionPane.showMessageDialog(source, fees);
                String[] rates = {"The savings account interest rate: " + savingsInterestRate, "The savings account minimum amount for interest: " + savingsMinAmount, "The loan interest rate: " + loanInterestRate};
                JOptionPane.showMessageDialog(source, rates);

            }
        });


    }

    //both backToLogin and changePW are used in both owner and customer view, so we put them into methods and call both

    public void addBackToLoginButton(JPanel panel, JFrame frame){

        JButton backButton = new JButton("Back");
        backButton.setBounds(10, 40, 160, 25);
        panel.add(backButton);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placeComponents();
                frame.dispose();
            }
        });
    }

    public void addChangePWButton(JPanel panel, JFrame frame, Person p){
        JButton changePWButton = new JButton("Change PW");
        changePWButton.setBounds(180, 40, 160, 25);
        panel.add(changePWButton);

        frame.setVisible(true);

        changePWButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton source = (JButton) e.getSource();
                String str = JOptionPane.showInputDialog(source, "Enter New Password");
                if (str != null && !str.isEmpty()) {
                	if (Person.changeCustomerPW(p.getUsername(), str))
                        JOptionPane.showMessageDialog(source, "Password changed!");
                	else{
                        JOptionPane.showMessageDialog(source, "Failed to change password!");
                    }
                }
                else {
                    JOptionPane.showMessageDialog(source, "You didn't enter a password!");
                }
            }
        });
    }

    public Currency[] getAllCurrencies() {
        return allCurrencies;
    }

    public double getOpenAccountFee(){
        return openAccountFee;
    }

    public double getCloseAccountFee(){
        return closeAccountFee;
    }

    public double getSavingsInterestRate(){
        return savingsInterestRate;
    }

    public double getSavingsMinAmount(){
        return savingsMinAmount;
    }


    public double getWithdrawalFee(){
        return withdrawalFee;
    }

    public double getCheckingAccountTransactionFee(){
        return checkingAccountTransactionFee;
    }

    public double getMaxLoanPercent(){
        return maxLoanPercent;
    }

    public double getLoanInterestRate(){
        return loanInterestRate;
    }


}
