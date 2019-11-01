package com.company;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;

import java.util.LinkedHashMap;

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

    LinkedHashMap<Integer, Transaction> recentTransactions;

    public Bank(Currency[] c, double openFee, double closeFee, double withFee, double checkingFee, double savingsRate, double loanRate, double minAmount, double maxLoan){
        super();
        allCurrencies = c;
        recentTransactions = new LinkedHashMap<>();
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

    public boolean addCustomer(Customer c) {
        String u = c.getUsername();
        String p = c.getPassword();
        if (get(new PairCopy(u, p)) == null) {
            put(new PairCopy(u, p), new BankCustomer(u, p));
            return true;
        }
        else{
            return false;
        }
    }

    public boolean addOwner(Owner o) {
        String u = o.getUsername();
        String p = o.getPassword();
        if (get(new PairCopy(u, p)) == null) {
            put(new PairCopy(u, p), new Banker(u, p));
            return true;
        }
        else{
            return false;
        }
    }

    @Override

    //provides methods that the owner can perform, like applyInterest, and view Recent Transactions
    public void ownerView(Owner o){

        JFrame frame = new JFrame("FancyBank");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Banker bk = (Banker)o;

        frame.setVisible(false);

        frame.setSize(325, 140);
        JPanel panel = new JPanel();
        frame.getContentPane().removeAll();
        frame.getContentPane().add(panel);

        panel.setLayout(null);

        JLabel label = new JLabel("Hello " + bk.getUsername() + "!", JLabel.LEFT);
        label.setBounds(10, 10, 140, 25);
        panel.add(label);

        addBackToLoginButton(panel, frame);
        addChangePWButton(panel, frame, bk);

        JButton viewTs = new JButton("View Transactions");
        viewTs.setBounds(10, 70, 140, 25);
        panel.add(viewTs);

        //add to current selection
        viewTs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton source = (JButton) e.getSource();
                //viewTransactionsOwner(frame, bk, recentTransactions.values());
                ViewRecent vr = new ViewRecent(Bank.this);
                vr.place(bk, recentTransactions.values());
                recentTransactions.clear();
                transactionCounter = 0;
                frame.dispose();
            }
        });

        JButton applyInterest = new JButton("Apply Interest");
        applyInterest.setBounds(160, 70, 140, 25);
        panel.add(applyInterest);

        applyInterest.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton source = (JButton) e.getSource();
                Collection<Person> customers = values();
                for (Person p: customers){
                    if (p instanceof BankCustomer){
                        Collection<Account> accs = ((BankCustomer) p).getAllAccounts();
                        for (Account a: accs){
                            Transaction t = a.applyInterest();
                            if (t != null){
                                recentTransactions.put(transactionCounter, t);
                                transactionCounter+=1;
                            }
                        }
                    }
                }
                JOptionPane.showMessageDialog(source, "Applied Interest to valid accounts!");
            }

        });

    }

    //provides methods customers can perform, like viewAccounts, viewInfo,
    public void customerView(Customer c){

        JFrame frame = new JFrame("FancyBank");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        BankCustomer bc = (BankCustomer)c;

        frame.setVisible(false);

        frame.setSize(325, 140);
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
        viewAccounts.setBounds(10, 70, 140, 25);
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
        viewInfo.setBounds(160, 70, 140, 25);
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
        backButton.setBounds(10, 40, 140, 25);
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
        changePWButton.setBounds(160, 40, 140, 25);
        panel.add(changePWButton);

        frame.setVisible(true);

        changePWButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton source = (JButton) e.getSource();
                if (get(new PairCopy(p.getUsername(), p.getPassword())) != null){
                    String str = JOptionPane.showInputDialog(source, "Enter New Password");

                    if (str != null && !str.isEmpty()) {
                        remove(new PairCopy(p.getUsername(), p.getPassword()));
                        p.setPassword(str);
                        put(new PairCopy(p.getUsername(), p.getPassword()), p);
                        JOptionPane.showMessageDialog(source, "Password changed!");
                    }
                    else {
                        JOptionPane.showMessageDialog(source, "You didn't enter a password!");
                    }
                }
                else{
                    JOptionPane.showMessageDialog(source, "User did not exist!");
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

    public int getTransactionCounter(){
        return transactionCounter;
    }

    public void setTransactionCounter(int tc){
        transactionCounter = tc;
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

//    public boolean checkEnough(double amt, double comparison, Currency c){
//        if ((amt * c.getConversionToBaseRate()) > comparison){
//            return true;
//        }
//        else {
//            return false;
//        }
//    }
//
//    public boolean checkEnough(double amt, double comparison){
//        if ((amt) > comparison){
//            return true;
//        }
//        else {
//            return false;
//        }
//    }
//
//    //makes a table with all current accounts, and provides methods for adding accounts, deleting, withdrawing, depositing and so on
//    //selections are made by clicking on the table
//    //each function has multiple checks and error messages to guide the user into giving a good input
//    //we also check that functions are not applied to loans if inappropriate
//    public void viewAccounts(JFrame frame, BankCustomer bc){
//        frame.setVisible(false);
//
//        frame.setSize(520, 400);
//        JPanel panel = new JPanel();
//        frame.getContentPane().removeAll();
//
//        panel.setLayout(null);
//
//        Collection<Account> allAccs = bc.getAllAccounts();
//
//        String cols[] = {"Account Type", "Account Number", "Balance"};
//        DefaultTableModel tabelModel = new DefaultTableModel(cols, 0){
//            @Override
//            public boolean isCellEditable(int row, int column){
//                return false;
//            }
//        };
//
//        for(Account a: allAccs){
//            Object[] obj = {a.getAccountType(), a.getIndex(), a.getBalance()};
//            tabelModel.addRow(obj);
//        }
//
//        JTable table = new JTable(tabelModel);
//
//        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//
////        DefaultListModel<String> l1 = new DefaultListModel<>();
////
////        for(Account a: allAccs){
////            l1.addElement(a.toString());
////        }
////
////        JList<String> list = new JList<>(l1);
////
////        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
////        list.setLayoutOrientation(JList.VERTICAL);
//
//        JScrollPane listScroller = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
//        listScroller.setPreferredSize(new Dimension(330, 300));
//
//        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
//                listScroller,
//                panel);
//
//        JButton backButton = new JButton("Back");
//        backButton.setBounds(10, 10, 140, 25);
//        panel.add(backButton);
//
//        backButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                customerView(frame, bc);
//            }
//        });
//
//        JButton addAccount = new JButton("Add Account");
//        addAccount.setBounds(10, 40, 140, 25);
//        panel.add(addAccount);
//
//        addAccount.addActionListener(new ActionListener() {
//            @Override
//
//            public void actionPerformed(ActionEvent e) {
//                JButton source = (JButton) e.getSource();
//                String[] options = {"Savings", "Checking", "Loan"};
//                String str = (String)JOptionPane.showInputDialog(source, "What kind of account?", "Account Type", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
//                String amt = JOptionPane.showInputDialog("Enter initial amount: ");
//                Currency c = (Currency)JOptionPane.showInputDialog(source, "Which currency", "Currency Selection", JOptionPane.QUESTION_MESSAGE, null, allCurrencies, allCurrencies[0]);
//                try {
//                    if (amt != null && !amt.isEmpty()) {
//                        Double amt1 = Double.parseDouble(amt);
//                        if (c != null) {
//                            boolean check = checkEnough(amt1, openAccountFee, c);
//                            if (str == "Savings") {
//                                if (check) {
//                                    Savings newAcc = new Savings(amt1, c, openAccountFee, savingsInterestRate, savingsMinAmount);
//                                    newAcc.addTransaction(new Transaction("Savings", newAcc.getIndex(), "Created Account", 0, newAcc.getBalance(), openAccountFee));
//                                    recentTransactions.put(transactionCounter, new Transaction("Savings", newAcc.getIndex(), "Created Account", 0, newAcc.getBalance(), openAccountFee));
//                                    transactionCounter+=1;
//                                    bc.addAccount(newAcc.getIndex(), newAcc);
//                                    viewAccounts(frame, bc);
//                                } else {
//                                    JOptionPane.showMessageDialog(source, "Not enough initial funds for a savings account. The open fee is " + openAccountFee);
//                                }
//                            } else if (str == "Checking") {
//                                if (check) {
//                                    Checking newAcc = new Checking(amt1, c, openAccountFee, 0);
//                                    newAcc.addTransaction(new Transaction("Checking", newAcc.getIndex(), "Created Account", 0, newAcc.getBalance(), openAccountFee));
//                                    bc.addAccount(newAcc.getIndex(), newAcc);
//                                    recentTransactions.put(transactionCounter, new Transaction("Checking", newAcc.getIndex(), "Created Account", 0, newAcc.getBalance(), openAccountFee));
//                                    transactionCounter+=1;
//                                    viewAccounts(frame, bc);
//                                } else {
//                                    JOptionPane.showMessageDialog(source, "Not enough initial funds for a checking account. The open fee is " + openAccountFee);
//                                }
//                            } else {
//                                if (check) {
//                                    double total = 0;
//                                    Collection<Account> accs = bc.getAllAccounts();
//                                    for (Account a: accs){
//                                        total += a.getBalance();
//                                    }
//                                    if ((amt1 * c.getConversionToBaseRate()) <= (total * maxLoanPercent)) {
//                                        Loan newAcc = new Loan(-amt1, c, openAccountFee, loanInterestRate);
//                                        newAcc.addTransaction(new Transaction("Loan", newAcc.getIndex(), "Created Account", 0, newAcc.getBalance(), openAccountFee));
//                                        bc.addAccount(newAcc.getIndex(), newAcc);
//                                        recentTransactions.put(transactionCounter, new Transaction("Loan", newAcc.getIndex(), "Created Account", 0, newAcc.getBalance(), openAccountFee));
//                                        transactionCounter += 1;
//                                        viewAccounts(frame, bc);
//                                    }
//                                    else{
//                                        JOptionPane.showMessageDialog(source, "Not enough capital for this loan. You must have at least " + amt1*c.getConversionToBaseRate() + " for a loan of size " + amt1*c.getConversionToBaseRate() + ".");
//                                    }
//                                } else {
//                                    JOptionPane.showMessageDialog(source, "Not enough initial funds for a loan account. The open fee is " + openAccountFee);
//                                }
//                            }
//                        }
//                        else{
//                            JOptionPane.showMessageDialog(source, "Didn't select a currency");
//                        }
//                    }
//                    else{
//                        JOptionPane.showMessageDialog(source, "Didn't enter a number for initial account value!");
//                    }
//                }
//                catch (NumberFormatException n){
//                    JOptionPane.showMessageDialog(source,"Did not enter a valid amount");
//                }
//            }
//        });
//
//        JButton deleteAccount = new JButton("Delete Account");
//        deleteAccount.setBounds(10, 70, 140, 25);
//        panel.add(deleteAccount);
//
//        deleteAccount.addActionListener(new ActionListener() {
//            @Override
//
//            public void actionPerformed(ActionEvent e) {
//                JButton source = (JButton) e.getSource();
//                int selected = table.getSelectedRow();
//                if (selected != -1) {
//                    Integer account_index = (Integer) tabelModel.getValueAt(selected, 1);
//                    boolean checkBalance = checkEnough(bc.getAccount(account_index).getBalance(), closeAccountFee);
//                    if (checkBalance) {
//                        Transaction check = bc.removeAccount(account_index, closeAccountFee);
//                        if (check != null) {
//                            JOptionPane.showMessageDialog(source, "Account deleted");
//                            recentTransactions.put(transactionCounter, check);
//                            transactionCounter+=1;
//                            viewAccounts(frame, bc);
//                        } else {
//                            JOptionPane.showMessageDialog(source, "Account did not exist or did not belong to this customer");
//                        }
//                    } else {
//                        JOptionPane.showMessageDialog(source, "Insufficient funds in account to delete. The fee is: " + closeAccountFee);
//                    }
//                }
//                else{
//                    JOptionPane.showMessageDialog(source, "Please select a row.");
//                }
//        }
//        });
//
//        JButton addButton = new JButton("Deposit");
//        addButton.setBounds(10, 100, 140, 25);
//        panel.add(addButton);
//
//        addButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                JButton source = (JButton) e.getSource();
//                int selected = table.getSelectedRow();
//                if (selected != -1) {
//                    Integer account_index = (Integer) tabelModel.getValueAt(selected, 1);
//                    String amtS = JOptionPane.showInputDialog("Enter amount to deposit: ");
//                    Currency c = (Currency)JOptionPane.showInputDialog(source, "Which currency", "Currency Selection", JOptionPane.QUESTION_MESSAGE, null, allCurrencies, allCurrencies[0]);
//                    try {
//                        if (amtS != null && !amtS.isEmpty()) {
//                            if (c != null) {
//                                double amt = Double.parseDouble(amtS);
//                                if (amt > 0) {
//                                    if (bc.getAccount(account_index) instanceof Checking) {
//                                        if (checkEnough(amt, checkingAccountTransactionFee, c)) {
//                                            double initB = bc.getAccount(account_index).getBalance();
//                                            bc.getAccount(account_index).addBalance((amt * c.getConversionToBaseRate()) - checkingAccountTransactionFee);
//                                            bc.getAccount(account_index).addTransaction(new Transaction("Checking", bc.getAccount(account_index).getIndex(), "Deposit", initB, bc.getAccount(account_index).getBalance(), checkingAccountTransactionFee));
//                                            recentTransactions.put(transactionCounter, new Transaction("Checking", bc.getAccount(account_index).getIndex(), "Deposit", initB, bc.getAccount(account_index).getBalance(), checkingAccountTransactionFee));
//                                            transactionCounter+=1;
//                                        } else {
//                                            JOptionPane.showMessageDialog(source, "Deposit amount is less than the checking account transaction fee.");
//                                        }
//                                    } else {
//                                        double initB = bc.getAccount(account_index).getBalance();
//                                        bc.getAccount(account_index).addBalance((amt * c.getConversionToBaseRate()));
//                                        if (bc.getAccount(account_index) instanceof Savings) {
//                                            bc.getAccount(account_index).addTransaction(new Transaction("Savings", bc.getAccount(account_index).getIndex(), "Deposit", initB, bc.getAccount(account_index).getBalance(), 0));
//                                            recentTransactions.put(transactionCounter, new Transaction("Savings", bc.getAccount(account_index).getIndex(), "Deposit", initB, bc.getAccount(account_index).getBalance(), 0));
//                                            transactionCounter+=1;
//                                        }
//                                        else{
//                                            bc.getAccount(account_index).addTransaction(new Transaction("Loan", bc.getAccount(account_index).getIndex(), "Deposit", initB, bc.getAccount(account_index).getBalance(), 0));
//                                            recentTransactions.put(transactionCounter, new Transaction("Loan", bc.getAccount(account_index).getIndex(), "Deposit", initB, bc.getAccount(account_index).getBalance(), 0));
//                                            transactionCounter+=1;
//                                        }
//                                    }
//                                    viewAccounts(frame, bc);
//                                } else {
//                                    JOptionPane.showMessageDialog(source, "Deposit amount is less than or equal to 0.");
//                                }
//                            }
//                            else{
//                                JOptionPane.showMessageDialog(source, "Did not enter a currency.");
//                            }
//                        }
//                        else{
//                            JOptionPane.showMessageDialog(source, "Did not enter an amount to deposit.");
//                        }
//                    }
//                    catch(NumberFormatException e1){
//                        JOptionPane.showMessageDialog(source, "Enter a valid amount to deposit.");
//                    }
//                }
//                else{
//                    JOptionPane.showMessageDialog(source, "Please select an account from the list.");
//                }
//            }
//        });
//
//        JButton withdrawButton = new JButton("Withdraw");
//        withdrawButton.setBounds(10, 130, 140, 25);
//        panel.add(withdrawButton);
//
//        withdrawButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                JButton source = (JButton) e.getSource();
//                int selected = table.getSelectedRow();
//                if (selected != -1) {
//                    Integer account_index = (Integer) tabelModel.getValueAt(selected, 1);
//                    String amtS = JOptionPane.showInputDialog("Enter amount to withdraw: ");
//                    Currency c = (Currency)JOptionPane.showInputDialog(source, "Which currency", "Currency Selection", JOptionPane.QUESTION_MESSAGE, null, allCurrencies, allCurrencies[0]);
//                    try {
//                        if (amtS != null && !amtS.isEmpty()) {
//                            double amt = Double.parseDouble(amtS);
//                            if (c != null) {
//                                if (amt > 0) {
//                                    if (bc.getAccount(account_index).getBalance() >= (amt * c.getConversionToBaseRate()) + withdrawalFee) {
//                                        if (bc.getAccount(account_index) instanceof Checking) {
//                                            if (bc.getAccount(account_index).getBalance() >= (amt * c.getConversionToBaseRate()) + withdrawalFee + checkingAccountTransactionFee) {
//                                                double initB = bc.getAccount(account_index).getBalance();
//                                                bc.getAccount(account_index).removeBalance((amt * c.getConversionToBaseRate()) + withdrawalFee + checkingAccountTransactionFee);
//                                                bc.getAccount(account_index).addTransaction(new Transaction("Checking", bc.getAccount(account_index).getIndex(), "Withdraw", initB, bc.getAccount(account_index).getBalance(), withdrawalFee+checkingAccountTransactionFee));
//                                                recentTransactions.put(transactionCounter, new Transaction("Checking", bc.getAccount(account_index).getIndex(), "Withdraw", initB, bc.getAccount(account_index).getBalance(), withdrawalFee+checkingAccountTransactionFee));
//                                                transactionCounter+=1;
//                                            } else {
//                                                JOptionPane.showMessageDialog(source, "Insufficient funds. The withdrawal fee is " + withdrawalFee + ", and the checking account transaction fee is " + checkingAccountTransactionFee + ".");
//                                            }
//                                        } else if (bc.getAccount(account_index) instanceof Savings) {
//                                            double initB = bc.getAccount(account_index).getBalance();
//                                            bc.getAccount(account_index).removeBalance((amt * c.getConversionToBaseRate()) + withdrawalFee);
//                                            bc.getAccount(account_index).addTransaction(new Transaction("Savings", bc.getAccount(account_index).getIndex(), "Withdraw", initB, bc.getAccount(account_index).getBalance(), withdrawalFee));
//                                            recentTransactions.put(transactionCounter, new Transaction("Savings", bc.getAccount(account_index).getIndex(), "Withdraw", initB, bc.getAccount(account_index).getBalance(), withdrawalFee));
//                                            transactionCounter+=1;
//                                        } else {
//                                            JOptionPane.showMessageDialog(source, "Cannot withdraw from a Loan");
//                                        }
//                                        viewAccounts(frame, bc);
//                                    } else {
//                                        JOptionPane.showMessageDialog(source, "Insufficient funds. The withdrawal fee is " + withdrawalFee + ".");
//                                    }
//                                } else {
//                                    JOptionPane.showMessageDialog(source, "Withdrawal amount is less than or equal to 0.");
//                                }
//                            }
//                            else{
//                                JOptionPane.showMessageDialog(source, "Did not enter a currency.");
//                            }
//                        }
//                        else{
//                            JOptionPane.showMessageDialog(source, "Did not enter a number to withdraw.");
//                        }
//                    }
//                    catch(NumberFormatException e1){
//                        JOptionPane.showMessageDialog(source, "Enter a valid amount to withdraw.");
//                    }
//                }
//                else{
//                    JOptionPane.showMessageDialog(source, "Please select an account from the list.");
//                }
//            }
//        });
//
//        JButton transferFromButton = new JButton("Transfer from");
//        transferFromButton.setBounds(10, 160, 140, 25);
//        panel.add(transferFromButton);
//
//        //subtract from current selection
//        transferFromButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                JButton source = (JButton) e.getSource();
//                int selected = table.getSelectedRow();
//                if (selected != -1) {
//                    Integer account_index = (Integer) tabelModel.getValueAt(selected, 1);
//                    String amtS = JOptionPane.showInputDialog("Enter amount to transfer: ");
//                    Currency c = (Currency)JOptionPane.showInputDialog(source, "Which currency", "Currency Selection", JOptionPane.QUESTION_MESSAGE, null, allCurrencies, allCurrencies[0]);
//                    try {
//                        if (amtS != null && !amtS.isEmpty()) {
//                            double amt = Double.parseDouble(amtS);
//                            if (c != null) {
//                                if (amt > 0) {
//                                    if (bc.getAccount(account_index) instanceof Checking) {
//                                        if (bc.getAccount(account_index).getBalance() >= (amt * c.getConversionToBaseRate()) + checkingAccountTransactionFee) {
//                                            String altAccS = JOptionPane.showInputDialog("Enter account number of other account: ");
//                                            Integer alt_index = Integer.valueOf(altAccS);
//                                            if (alt_index != account_index) {
//                                                Account a = bc.getAccount(alt_index);
//                                                if (a != null) {
//                                                    double initBa = a.getBalance();
//                                                    double initB = bc.getAccount(account_index).getBalance();
//                                                    bc.getAccount(account_index).removeBalance((amt * c.getConversionToBaseRate()) + checkingAccountTransactionFee);
//                                                    bc.getAccount(alt_index).addBalance((amt * c.getConversionToBaseRate()));
//                                                    bc.getAccount(account_index).addTransaction(new Transaction("Checking", bc.getAccount(account_index).getIndex(), "Transfer from", initB, bc.getAccount(account_index).getBalance(), checkingAccountTransactionFee));
//                                                    recentTransactions.put(transactionCounter, new Transaction("Checking", bc.getAccount(account_index).getIndex(), "Transfer from", initB, bc.getAccount(account_index).getBalance(), checkingAccountTransactionFee));
//                                                    transactionCounter+=1;
//                                                    a.addTransaction(new Transaction(a.getAccountType(), a.getIndex(), "Transfer to", initBa, a.getBalance(), 0));
//                                                    recentTransactions.put(transactionCounter, new Transaction(a.getAccountType(), a.getIndex(), "Transfer to", initBa, a.getBalance(), 0));
//                                                    transactionCounter+=1;
//                                                } else {
//                                                    JOptionPane.showMessageDialog(source, "Other account does not exist or does not belong to the user.");
//                                                }
//                                            } else {
//                                                JOptionPane.showMessageDialog(source, "Entered the same account number");
//                                            }
//                                        } else {
//                                            JOptionPane.showMessageDialog(source, "Insufficient funds. The checking account transaction fee is " + checkingAccountTransactionFee + ".");
//                                        }
//                                    } else if (bc.getAccount(account_index) instanceof Savings) {
//                                        if (bc.getAccount(account_index).getBalance() >= (amt * c.getConversionToBaseRate())) {
//                                            String altAccS = JOptionPane.showInputDialog("Enter account number of other account: ");
//                                            Integer alt_index = Integer.valueOf(altAccS);
//                                            Account a = bc.getAccount(alt_index);
//                                            if (alt_index != account_index) {
//                                                if (a != null) {
//                                                    double initBa = a.getBalance();
//                                                    double initB = bc.getAccount(account_index).getBalance();
//                                                    bc.getAccount(account_index).removeBalance((amt * c.getConversionToBaseRate()));
//                                                    bc.getAccount(alt_index).addBalance((amt * c.getConversionToBaseRate()));
//                                                    bc.getAccount(account_index).addTransaction(new Transaction("Savings", bc.getAccount(account_index).getIndex(), "Transfer from", initB, bc.getAccount(account_index).getBalance(), 0));
//                                                    recentTransactions.put(transactionCounter, new Transaction("Savings", bc.getAccount(account_index).getIndex(), "Transfer from", initB, bc.getAccount(account_index).getBalance(), 0));
//                                                    transactionCounter+=1;
//                                                    a.addTransaction(new Transaction(a.getAccountType(), a.getIndex(), "Transfer to", initBa, a.getBalance(), 0));
//                                                    recentTransactions.put(transactionCounter, new Transaction(a.getAccountType(), a.getIndex(), "Transfer to", initBa, a.getBalance(), 0));
//                                                    transactionCounter+=1;
//                                                } else {
//                                                    JOptionPane.showMessageDialog(source, "Other account does not exist or does not belong to the user.");
//                                                }
//                                            } else {
//                                                JOptionPane.showMessageDialog(source, "Entered the same account number");
//                                            }
//                                        } else {
//                                            JOptionPane.showMessageDialog(source, "Insufficient funds.");
//                                        }
//                                    } else {
//                                        JOptionPane.showMessageDialog(source, "Cannot use this function with a loan account.");
//                                    }
//                                    viewAccounts(frame, bc);
//                                } else {
//                                    JOptionPane.showMessageDialog(source, "Transfer amount is less than or equal to 0.");
//                                }
//                            }
//                            else {
//                                JOptionPane.showMessageDialog(source, "Didn't pick a currency.");
//                            }
//                        }
//                        else{
//                            JOptionPane.showMessageDialog(source, "Did not enter a valid number to transfer.");
//                        }
//                    }
//                    catch(NumberFormatException e1){
//                        JOptionPane.showMessageDialog(source, "Enter a valid number.");
//                    }
//                }
//                else{
//                    JOptionPane.showMessageDialog(source, "Please select an account from the list.");
//                }
//            }
//        });
//
//        JButton transferTo = new JButton("Transfer to");
//        transferTo.setBounds(10, 190, 140, 25);
//        panel.add(transferTo);
//
//        //add to current selection
//        transferTo.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                JButton source = (JButton) e.getSource();
//                int selected = table.getSelectedRow();
//                if (selected != -1) {
//                    String altAccS = JOptionPane.showInputDialog("Enter account number of other account: ");
//                    try {
//                        Integer alt_index = Integer.valueOf(altAccS);
//                        Account a = bc.getAccount(alt_index);
//                        Integer account_index = (Integer) tabelModel.getValueAt(selected, 1);
//                        if (a != null) {
//                            if (a instanceof Loan) {
//                                JOptionPane.showMessageDialog(source, "Account given is a loan.");
//                            } else {
//                                String amtS = JOptionPane.showInputDialog("Enter amount to transfer: ");
//                                Currency c = (Currency)JOptionPane.showInputDialog(source, "Which currency", "Currency Selection", JOptionPane.QUESTION_MESSAGE, null, allCurrencies, allCurrencies[0]);
//                                double amt = Double.parseDouble(amtS);
//                                if (c != null) {
//                                    if (a instanceof Checking) {
//                                        if (a.getBalance() >= (amt * c.getConversionToBaseRate()) + checkingAccountTransactionFee) {
//                                            double initBa = a.getBalance();
//                                            double initB = bc.getAccount(account_index).getBalance();
//                                            a.removeBalance((amt * c.getConversionToBaseRate()) + checkingAccountTransactionFee);
//                                            bc.getAccount(account_index).addBalance((amt * c.getConversionToBaseRate()));
//                                            bc.getAccount(account_index).addTransaction(new Transaction(bc.getAccount(account_index).getAccountType(), bc.getAccount(account_index).getIndex(), "Transfer to", initB, bc.getAccount(account_index).getBalance(), 0));
//                                            recentTransactions.put(transactionCounter, new Transaction(bc.getAccount(account_index).getAccountType(), bc.getAccount(account_index).getIndex(), "Transfer to", initB, bc.getAccount(account_index).getBalance(), 0));
//                                            transactionCounter+=1;
//                                            a.addTransaction(new Transaction(a.getAccountType(), a.getIndex(), "Transfer from", initBa, a.getBalance(), checkingAccountTransactionFee));
//                                            recentTransactions.put(transactionCounter, new Transaction(a.getAccountType(), a.getIndex(), "Transfer from", initBa, a.getBalance(), checkingAccountTransactionFee));
//                                            transactionCounter+=1;
//                                        } else {
//                                            JOptionPane.showMessageDialog(source, "Insufficient funds. The checking account transaction fee is " + checkingAccountTransactionFee + ".");
//                                        }
//                                    } else {
//                                        if (a.getBalance() >= (amt * c.getConversionToBaseRate())) {
//                                            double initBa = a.getBalance();
//                                            double initB = bc.getAccount(account_index).getBalance();
//                                            a.removeBalance((amt * c.getConversionToBaseRate()));
//                                            bc.getAccount(account_index).addBalance((amt * c.getConversionToBaseRate()));
//                                            bc.getAccount(account_index).addTransaction(new Transaction(bc.getAccount(account_index).getAccountType(), bc.getAccount(account_index).getIndex(), "Transfer to", initB, bc.getAccount(account_index).getBalance(), 0));
//                                            recentTransactions.put(transactionCounter, new Transaction(bc.getAccount(account_index).getAccountType(), bc.getAccount(account_index).getIndex(), "Transfer to", initB, bc.getAccount(account_index).getBalance(), 0));
//                                            transactionCounter+=1;
//                                            a.addTransaction(new Transaction(a.getAccountType(), a.getIndex(), "Transfer from", initBa, a.getBalance(), 0));
//                                            recentTransactions.put(transactionCounter, new Transaction(a.getAccountType(), a.getIndex(), "Transfer from", initBa, a.getBalance(), 0));
//                                            transactionCounter+=1;
//                                        } else {
//                                            JOptionPane.showMessageDialog(source, "Insufficient funds.");
//                                        }
//                                    }
//                                    viewAccounts(frame, bc);
//                                }
//                                else{
//                                    JOptionPane.showMessageDialog(source, "Did not enter a currency.");
//                                }
//                            }
//                        } else {
//                            JOptionPane.showMessageDialog(source, "Invalid account number.");
//                        }
//                    }
//                    catch(NumberFormatException e1){
//                        JOptionPane.showMessageDialog(source, "Enter a valid number.");
//                    }
//                }
//                else{
//                    JOptionPane.showMessageDialog(source, "Please select an account from the list.");
//                }
//            }
//        });
//
//        JButton viewTs = new JButton("View Transactions");
//        viewTs.setBounds(10, 220, 140, 25);
//        panel.add(viewTs);
//
//
//        viewTs.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                JButton source = (JButton) e.getSource();
//                int selected = table.getSelectedRow();
//                if (selected != -1) {
//                    Integer account_index = (Integer) tabelModel.getValueAt(selected, 1);
//                    viewTransactions1Acc(frame, bc, bc.getAccount(account_index).getAllTransactions());
//                }
//                else{
//                    JOptionPane.showMessageDialog(source, "Please select an account from the list.");
//                }
//            }
//        });
//
//        JButton resolveLoans = new JButton("Resolve Loans");
//        resolveLoans.setBounds(10, 250, 140, 25);
//        panel.add(resolveLoans);
//
//
//        resolveLoans.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                JButton source = (JButton) e.getSource();
//                Collection<Account> accs = bc.getAllAccounts();
//                ArrayList<Integer> indexes = new ArrayList<>();
//                for (Account a:accs){
//                    if (a instanceof Loan){
//                        if (a.getBalance() >= closeAccountFee){
//                            indexes.add(a.getIndex());
//                        }
//                    }
//                }
//                for (int i=0; i<indexes.size(); i++){
//                    Transaction check = bc.removeAccount(indexes.get(i), closeAccountFee);
//                    if (check != null) {
//                        recentTransactions.put(transactionCounter, check);
//                        transactionCounter += 1;
//                    }
//                }
//                JOptionPane.showMessageDialog(source, "All loans with balances greater than the close account fee: " + closeAccountFee + " were closed.");
//                viewAccounts(frame, bc);
//            }
//        });
//
//
//
//
//        frame.getContentPane().add(splitPane);
//        frame.setVisible(true);
//    }
//
//    //displays transactions for a bc for one or multiple accounts and provides a back button
//    public void viewTransactions1Acc(JFrame frame, BankCustomer bc, Collection<Transaction> ts){
//        frame.setVisible(false);
//
//        frame.setSize(750, 400);
//        JPanel panel = new JPanel();
//        frame.getContentPane().removeAll();
//
//        panel.setLayout(null);
//
//        Collection<Transaction> allTs = ts;
//
//        String cols[] = {"Account Type", "Account Number", "Transaction Type", "Initial Balance", "Final Balance", "Fees"};
//        DefaultTableModel tabelModel = new DefaultTableModel(cols, 0){
//            @Override
//            public boolean isCellEditable(int row, int column){
//                return false;
//            }
//        };
//
//        for(Transaction a: allTs){
//            Object[] obj = {a.getAccountType(), a.getAccountNumber(), a.getTransactionType(), a.getInitBalance(), a.getFinalBalance(), a.getFee()};
//            tabelModel.addRow(obj);
//        }
//
//        JTable table = new JTable(tabelModel);
//
//        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//
//        JScrollPane listScroller = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
//        listScroller.setPreferredSize(new Dimension(600, 300));
//
//        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
//                listScroller,
//                panel);
//
//        JButton backButton = new JButton("Back");
//        backButton.setBounds(10, 10, 105, 25);
//        panel.add(backButton);
//
//        backButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                viewAccounts(frame, bc);
//            }
//        });
//
//
//        frame.getContentPane().add(splitPane);
//        frame.setVisible(true);
//    }
//
//    //displays recent transactions for the owner and provides a back button
//    public void viewTransactionsOwner(JFrame frame, Banker bk, Collection<Transaction> ts){
//        frame.setVisible(false);
//
//        frame.setSize(750, 400);
//        JPanel panel = new JPanel();
//        frame.getContentPane().removeAll();
//
//        panel.setLayout(null);
//
//        Collection<Transaction> allTs = ts;
//
//        String cols[] = {"Account Type", "Account Number", "Transaction Type", "Initial Balance", "Final Balance", "Fees"};
//        DefaultTableModel tabelModel = new DefaultTableModel(cols, 0){
//            @Override
//            public boolean isCellEditable(int row, int column){
//                return false;
//            }
//        };
//
//        for(Transaction a: allTs){
//            Object[] obj = {a.getAccountType(), a.getAccountNumber(), a.getTransactionType(), a.getInitBalance(), a.getFinalBalance(), a.getFee()};
//            tabelModel.addRow(obj);
//        }
//
//        JTable table = new JTable(tabelModel);
//
//        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//
//        JScrollPane listScroller = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
//        listScroller.setPreferredSize(new Dimension(600, 300));
//
//        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
//                listScroller,
//                panel);
//
//        JButton backButton = new JButton("Back");
//        backButton.setBounds(10, 10, 105, 25);
//        panel.add(backButton);
//
//        backButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                ownerView(frame, bk);
//            }
//        });
//
//
//        frame.getContentPane().add(splitPane);
//        frame.setVisible(true);
//    }



}
