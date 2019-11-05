package com.company;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;

public class ViewSecurity {
    Bank b;

    public ViewSecurity(Bank bb){
        b = bb;
    }
    public void place(BankCustomer bc){
        JFrame frame = new JFrame("FancyBank Security Trading");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(false);

        frame.setSize(520, 400);
        JPanel panel = new JPanel();
        frame.getContentPane().removeAll();

        panel.setLayout(null);

        Collection<Account> allAccs = bc.getAllAccounts();

        String cols[] = {"Account Type", "Account Number", "Balance"};
        DefaultTableModel tabelModel = new DefaultTableModel(cols, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };

        for(Account a: allAccs){
            Object[] obj = {a.getAccountType(), a.getIndex(), a.getBalance()};
            tabelModel.addRow(obj);
        }

        JTable table = new JTable(tabelModel);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane listScroller = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        listScroller.setPreferredSize(new Dimension(330, 300));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                listScroller,
                panel);

        JButton backButton = new JButton("Back");
        backButton.setBounds(10, 10, 140, 25);
        panel.add(backButton);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                b.customerView(bc);
                frame.dispose();
            }
        });

        JButton addAccount = new JButton("Add Account");
        addAccount.setBounds(10, 40, 140, 25);
        panel.add(addAccount);

        addAccount.addActionListener(new ActionListener() {
            @Override

            public void actionPerformed(ActionEvent e) {
                JButton source = (JButton) e.getSource();
                String[] options = {"Savings", "Checking", "Loan"};
                String str = (String)JOptionPane.showInputDialog(source, "What kind of account?", "Account Type", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                String amt = JOptionPane.showInputDialog("Enter initial amount: ");
                Currency c = (Currency)JOptionPane.showInputDialog(source, "Which currency", "Currency Selection", JOptionPane.QUESTION_MESSAGE, null, b.getAllCurrencies(), b.getAllCurrencies()[0]);
                try {
                    if (amt != null && !amt.isEmpty()) {
                        Double amt1 = Double.parseDouble(amt);
                        if (c != null) {
                            boolean check = checkEnough(amt1, b.getOpenAccountFee(), c);
                            if (str == "Savings") {
                                if (check) {
                                    Savings newAcc = new Savings(amt1, c, b.getOpenAccountFee(), b.getSavingsInterestRate(), b.getSavingsMinAmount());
                                    newAcc.addTransaction(new Transaction(AccountType.SAVINGACCOUNT, newAcc.getIndex(), "Created Account", 0, newAcc.getBalance(), b.getOpenAccountFee()));
                                    b.recentTransactions.put(b.getTransactionCounter(), new Transaction(AccountType.SAVINGACCOUNT, newAcc.getIndex(), "Created Account", 0, newAcc.getBalance(), b.getOpenAccountFee()));
                                    b.setTransactionCounter(b.getTransactionCounter() + 1);
                                    bc.addAccount(newAcc.getIndex(), newAcc);
                                    place(bc);
                                    frame.dispose();
                                } else {
                                    JOptionPane.showMessageDialog(source, "Not enough initial funds for a savings account. The open fee is " + b.getOpenAccountFee());
                                }
                            } else if (str == "Checking") {
                                if (check) {
                                    Checking newAcc = new Checking(amt1, c, b.getOpenAccountFee(), 0);
                                    newAcc.addTransaction(new Transaction(AccountType.CHECHINGACCOUNT, newAcc.getIndex(), "Created Account", 0, newAcc.getBalance(), b.getOpenAccountFee()));
                                    bc.addAccount(newAcc.getIndex(), newAcc);
                                    b.recentTransactions.put(b.getTransactionCounter(), new Transaction(AccountType.CHECHINGACCOUNT, newAcc.getIndex(), "Created Account", 0, newAcc.getBalance(), b.getOpenAccountFee()));
                                    b.setTransactionCounter(b.getTransactionCounter() + 1);
                                    place(bc);
                                    frame.dispose();
                                } else {
                                    JOptionPane.showMessageDialog(source, "Not enough initial funds for a checking account. The open fee is " + b.getOpenAccountFee());
                                }
                            } else {
                                if (check) {
                                    double total = 0;
                                    Collection<Account> accs = bc.getAllAccounts();
                                    for (Account a: accs){
                                        total += a.getBalance();
                                    }
                                    if ((amt1 * c.getConversionToBaseRate()) <= (total * b.getMaxLoanPercent())) {
                                        Loan newAcc = new Loan(-amt1, c, b.getOpenAccountFee(), b.getLoanInterestRate());
                                        newAcc.addTransaction(new Transaction(AccountType.LOANACCOUNT, newAcc.getIndex(), "Created Account", 0, newAcc.getBalance(), b.getOpenAccountFee()));
                                        bc.addAccount(newAcc.getIndex(), newAcc);
                                        b.recentTransactions.put(b.getTransactionCounter(), new Transaction(AccountType.LOANACCOUNT, newAcc.getIndex(), "Created Account", 0, newAcc.getBalance(), b.getOpenAccountFee()));
                                        b.setTransactionCounter(b.getTransactionCounter() + 1);
                                        place(bc);
                                        frame.dispose();
                                    }
                                    else{
                                        JOptionPane.showMessageDialog(source, "Not enough capital for this loan. You must have at least " + amt1*c.getConversionToBaseRate() + " for a loan of size " + amt1*c.getConversionToBaseRate() + ".");
                                    }
                                } else {
                                    JOptionPane.showMessageDialog(source, "Not enough initial funds for a loan account. The open fee is " + b.getOpenAccountFee());
                                }
                            }
                        }
                        else{
                            JOptionPane.showMessageDialog(source, "Didn't select a currency");
                        }
                    }
                    else{
                        JOptionPane.showMessageDialog(source, "Didn't enter a number for initial account value!");
                    }
                }
                catch (NumberFormatException n){
                    JOptionPane.showMessageDialog(source,"Did not enter a valid amount");
                }
            }
        });

        JButton deleteAccount = new JButton("Delete Account");
        deleteAccount.setBounds(10, 70, 140, 25);
        panel.add(deleteAccount);

        deleteAccount.addActionListener(new ActionListener() {
            @Override

            public void actionPerformed(ActionEvent e) {
                JButton source = (JButton) e.getSource();
                int selected = table.getSelectedRow();
                if (selected != -1) {
                    Integer account_index = (Integer) tabelModel.getValueAt(selected, 1);
                    boolean checkBalance = checkEnough(bc.getAccount(account_index).getBalance(), b.getCloseAccountFee());
                    if (checkBalance) {
                        Transaction check = bc.removeAccount(account_index, b.getCloseAccountFee());
                        if (check != null) {
                            JOptionPane.showMessageDialog(source, "Account deleted");
                            b.recentTransactions.put(b.getTransactionCounter(), check);
                            b.setTransactionCounter(b.getTransactionCounter() + 1);
                            place(bc);
                            frame.dispose();
                        } else {
                            JOptionPane.showMessageDialog(source, "Account did not exist or did not belong to this customer");
                        }
                    } else {
                        JOptionPane.showMessageDialog(source, "Insufficient funds in account to delete. The fee is: " + b.getCloseAccountFee());
                    }
                }
                else{
                    JOptionPane.showMessageDialog(source, "Please select a row.");
                }
            }
        });

        JButton addButton = new JButton("Deposit");
        addButton.setBounds(10, 100, 140, 25);
        panel.add(addButton);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton source = (JButton) e.getSource();
                int selected = table.getSelectedRow();
                if (selected != -1) {
                    Integer account_index = (Integer) tabelModel.getValueAt(selected, 1);
                    String amtS = JOptionPane.showInputDialog("Enter amount to deposit: ");
                    Currency c = (Currency)JOptionPane.showInputDialog(source, "Which currency", "Currency Selection", JOptionPane.QUESTION_MESSAGE, null, b.getAllCurrencies(), b.getAllCurrencies()[0]);
                    try {
                        if (amtS != null && !amtS.isEmpty()) {
                            if (c != null) {
                                double amt = Double.parseDouble(amtS);
                                if (amt > 0) {
                                    if (bc.getAccount(account_index) instanceof Checking) {
                                        if (checkEnough(amt, b.getCheckingAccountTransactionFee(), c)) {
                                            double initB = bc.getAccount(account_index).getBalance();
                                            bc.getAccount(account_index).addBalance((amt * c.getConversionToBaseRate()) - b.getCheckingAccountTransactionFee());
                                            bc.getAccount(account_index).addTransaction(new Transaction(AccountType.CHECHINGACCOUNT, bc.getAccount(account_index).getIndex(), "Deposit", initB, bc.getAccount(account_index).getBalance(), b.getCheckingAccountTransactionFee()));
                                            b.recentTransactions.put(b.getTransactionCounter(), new Transaction(AccountType.CHECHINGACCOUNT, bc.getAccount(account_index).getIndex(), "Deposit", initB, bc.getAccount(account_index).getBalance(), b.getCheckingAccountTransactionFee()));
                                            b.setTransactionCounter(b.getTransactionCounter() + 1);
                                        } else {
                                            JOptionPane.showMessageDialog(source, "Deposit amount is less than the checking account transaction fee.");
                                        }
                                    } else {
                                        double initB = bc.getAccount(account_index).getBalance();
                                        bc.getAccount(account_index).addBalance((amt * c.getConversionToBaseRate()));
                                        if (bc.getAccount(account_index) instanceof Savings) {
                                            bc.getAccount(account_index).addTransaction(new Transaction(AccountType.SAVINGACCOUNT, bc.getAccount(account_index).getIndex(), "Deposit", initB, bc.getAccount(account_index).getBalance(), 0));
                                            b.recentTransactions.put(b.getTransactionCounter(), new Transaction(AccountType.SAVINGACCOUNT, bc.getAccount(account_index).getIndex(), "Deposit", initB, bc.getAccount(account_index).getBalance(), 0));
                                            b.setTransactionCounter(b.getTransactionCounter() + 1);
                                        }
                                        else{
                                            bc.getAccount(account_index).addTransaction(new Transaction(AccountType.LOANACCOUNT, bc.getAccount(account_index).getIndex(), "Deposit", initB, bc.getAccount(account_index).getBalance(), 0));
                                            b.recentTransactions.put(b.getTransactionCounter(), new Transaction(AccountType.LOANACCOUNT, bc.getAccount(account_index).getIndex(), "Deposit", initB, bc.getAccount(account_index).getBalance(), 0));
                                            b.setTransactionCounter(b.getTransactionCounter() + 1);
                                        }
                                    }
                                    place(bc);
                                    frame.dispose();
                                } else {
                                    JOptionPane.showMessageDialog(source, "Deposit amount is less than or equal to 0.");
                                }
                            }
                            else{
                                JOptionPane.showMessageDialog(source, "Did not enter a currency.");
                            }
                        }
                        else{
                            JOptionPane.showMessageDialog(source, "Did not enter an amount to deposit.");
                        }
                    }
                    catch(NumberFormatException e1){
                        JOptionPane.showMessageDialog(source, "Enter a valid amount to deposit.");
                    }
                }
                else{
                    JOptionPane.showMessageDialog(source, "Please select an account from the list.");
                }
            }
        });

        JButton withdrawButton = new JButton("Withdraw");
        withdrawButton.setBounds(10, 130, 140, 25);
        panel.add(withdrawButton);

        withdrawButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton source = (JButton) e.getSource();
                int selected = table.getSelectedRow();
                if (selected != -1) {
                    Integer account_index = (Integer) tabelModel.getValueAt(selected, 1);
                    String amtS = JOptionPane.showInputDialog("Enter amount to withdraw: ");
                    Currency c = (Currency)JOptionPane.showInputDialog(source, "Which currency", "Currency Selection", JOptionPane.QUESTION_MESSAGE, null, b.getAllCurrencies(), b.getAllCurrencies()[0]);
                    try {
                        if (amtS != null && !amtS.isEmpty()) {
                            double amt = Double.parseDouble(amtS);
                            if (c != null) {
                                if (amt > 0) {
                                    if (bc.getAccount(account_index).getBalance() >= (amt * c.getConversionToBaseRate()) + b.getWithdrawalFee()) {
                                        if (bc.getAccount(account_index) instanceof Checking) {
                                            if (bc.getAccount(account_index).getBalance() >= (amt * c.getConversionToBaseRate()) + b.getWithdrawalFee() + b.getCheckingAccountTransactionFee()) {
                                                double initB = bc.getAccount(account_index).getBalance();
                                                bc.getAccount(account_index).removeBalance((amt * c.getConversionToBaseRate()) + b.getWithdrawalFee() + b.getCheckingAccountTransactionFee());
                                                bc.getAccount(account_index).addTransaction(new Transaction(AccountType.CHECHINGACCOUNT, bc.getAccount(account_index).getIndex(), "Withdraw", initB, bc.getAccount(account_index).getBalance(), b.getWithdrawalFee()+b.getCheckingAccountTransactionFee()));
                                                b.recentTransactions.put(b.getTransactionCounter(), new Transaction(AccountType.CHECHINGACCOUNT, bc.getAccount(account_index).getIndex(), "Withdraw", initB, bc.getAccount(account_index).getBalance(), b.getWithdrawalFee()+b.getCheckingAccountTransactionFee()));
                                                b.setTransactionCounter(b.getTransactionCounter() + 1);
                                            } else {
                                                JOptionPane.showMessageDialog(source, "Insufficient funds. The withdrawal fee is " + b.getWithdrawalFee() + ", and the checking account transaction fee is " + b.getCheckingAccountTransactionFee() + ".");
                                            }
                                        } else if (bc.getAccount(account_index) instanceof Savings) {
                                            double initB = bc.getAccount(account_index).getBalance();
                                            bc.getAccount(account_index).removeBalance((amt * c.getConversionToBaseRate()) + b.getWithdrawalFee());
                                            bc.getAccount(account_index).addTransaction(new Transaction(AccountType.SAVINGACCOUNT, bc.getAccount(account_index).getIndex(), "Withdraw", initB, bc.getAccount(account_index).getBalance(), b.getWithdrawalFee()));
                                            b.recentTransactions.put(b.getTransactionCounter(), new Transaction(AccountType.SAVINGACCOUNT, bc.getAccount(account_index).getIndex(), "Withdraw", initB, bc.getAccount(account_index).getBalance(), b.getWithdrawalFee()));
                                            b.setTransactionCounter(b.getTransactionCounter() + 1);
                                        } else {
                                            JOptionPane.showMessageDialog(source, "Cannot withdraw from a Loan");
                                        }
                                        place(bc);
                                        frame.dispose();
                                    } else {
                                        JOptionPane.showMessageDialog(source, "Insufficient funds. The withdrawal fee is " + b.getWithdrawalFee() + ".");
                                    }
                                } else {
                                    JOptionPane.showMessageDialog(source, "Withdrawal amount is less than or equal to 0.");
                                }
                            }
                            else{
                                JOptionPane.showMessageDialog(source, "Did not enter a currency.");
                            }
                        }
                        else{
                            JOptionPane.showMessageDialog(source, "Did not enter a number to withdraw.");
                        }
                    }
                    catch(NumberFormatException e1){
                        JOptionPane.showMessageDialog(source, "Enter a valid amount to withdraw.");
                    }
                }
                else{
                    JOptionPane.showMessageDialog(source, "Please select an account from the list.");
                }
            }
        });

        JButton transferFromButton = new JButton("Transfer from");
        transferFromButton.setBounds(10, 160, 140, 25);
        panel.add(transferFromButton);

        //subtract from current selection
        transferFromButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton source = (JButton) e.getSource();
                int selected = table.getSelectedRow();
                if (selected != -1) {
                    Integer account_index = (Integer) tabelModel.getValueAt(selected, 1);
                    String amtS = JOptionPane.showInputDialog("Enter amount to transfer: ");
                    Currency c = (Currency)JOptionPane.showInputDialog(source, "Which currency", "Currency Selection", JOptionPane.QUESTION_MESSAGE, null, b.getAllCurrencies(), b.getAllCurrencies()[0]);
                    try {
                        if (amtS != null && !amtS.isEmpty()) {
                            double amt = Double.parseDouble(amtS);
                            if (c != null) {
                                if (amt > 0) {
                                    if (bc.getAccount(account_index) instanceof Checking) {
                                        if (bc.getAccount(account_index).getBalance() >= (amt * c.getConversionToBaseRate()) + b.getCheckingAccountTransactionFee()) {
                                            String altAccS = JOptionPane.showInputDialog("Enter account number of other account: ");
                                            Integer alt_index = Integer.valueOf(altAccS);
                                            if (alt_index != account_index) {
                                                Account a = bc.getAccount(alt_index);
                                                if (a != null) {
                                                    double initBa = a.getBalance();
                                                    double initB = bc.getAccount(account_index).getBalance();
                                                    bc.getAccount(account_index).removeBalance((amt * c.getConversionToBaseRate()) + b.getCheckingAccountTransactionFee());
                                                    bc.getAccount(alt_index).addBalance((amt * c.getConversionToBaseRate()));
                                                    bc.getAccount(account_index).addTransaction(new Transaction(AccountType.CHECHINGACCOUNT, bc.getAccount(account_index).getIndex(), "Transfer from", initB, bc.getAccount(account_index).getBalance(), b.getCheckingAccountTransactionFee()));
                                                    b.recentTransactions.put(b.getTransactionCounter(), new Transaction(AccountType.CHECHINGACCOUNT, bc.getAccount(account_index).getIndex(), "Transfer from", initB, bc.getAccount(account_index).getBalance(), b.getCheckingAccountTransactionFee()));
                                                    b.setTransactionCounter(b.getTransactionCounter() + 1);
                                                    a.addTransaction(new Transaction(a.getAccountType(), a.getIndex(), "Transfer to", initBa, a.getBalance(), 0));
                                                    b.recentTransactions.put(b.getTransactionCounter(), new Transaction(a.getAccountType(), a.getIndex(), "Transfer to", initBa, a.getBalance(), 0));
                                                    b.setTransactionCounter(b.getTransactionCounter() + 1);
                                                } else {
                                                    JOptionPane.showMessageDialog(source, "Other account does not exist or does not belong to the user.");
                                                }
                                            } else {
                                                JOptionPane.showMessageDialog(source, "Entered the same account number");
                                            }
                                        } else {
                                            JOptionPane.showMessageDialog(source, "Insufficient funds. The checking account transaction fee is " + b.getCheckingAccountTransactionFee() + ".");
                                        }
                                    } else if (bc.getAccount(account_index) instanceof Savings) {
                                        if (bc.getAccount(account_index).getBalance() >= (amt * c.getConversionToBaseRate())) {
                                            String altAccS = JOptionPane.showInputDialog("Enter account number of other account: ");
                                            Integer alt_index = Integer.valueOf(altAccS);
                                            Account a = bc.getAccount(alt_index);
                                            if (alt_index != account_index) {
                                                if (a != null) {
                                                    double initBa = a.getBalance();
                                                    double initB = bc.getAccount(account_index).getBalance();
                                                    bc.getAccount(account_index).removeBalance((amt * c.getConversionToBaseRate()));
                                                    bc.getAccount(alt_index).addBalance((amt * c.getConversionToBaseRate()));
                                                    bc.getAccount(account_index).addTransaction(new Transaction(AccountType.SAVINGACCOUNT, bc.getAccount(account_index).getIndex(), "Transfer from", initB, bc.getAccount(account_index).getBalance(), 0));
                                                    b.recentTransactions.put(b.getTransactionCounter(), new Transaction(AccountType.SAVINGACCOUNT, bc.getAccount(account_index).getIndex(), "Transfer from", initB, bc.getAccount(account_index).getBalance(), 0));
                                                    b.setTransactionCounter(b.getTransactionCounter() + 1);
                                                    a.addTransaction(new Transaction(a.getAccountType(), a.getIndex(), "Transfer to", initBa, a.getBalance(), 0));
                                                    b.recentTransactions.put(b.getTransactionCounter(), new Transaction(a.getAccountType(), a.getIndex(), "Transfer to", initBa, a.getBalance(), 0));
                                                    b.setTransactionCounter(b.getTransactionCounter() + 1);
                                                } else {
                                                    JOptionPane.showMessageDialog(source, "Other account does not exist or does not belong to the user.");
                                                }
                                            } else {
                                                JOptionPane.showMessageDialog(source, "Entered the same account number");
                                            }
                                        } else {
                                            JOptionPane.showMessageDialog(source, "Insufficient funds.");
                                        }
                                    } else {
                                        JOptionPane.showMessageDialog(source, "Cannot use this function with a loan account.");
                                    }
                                    place(bc);
                                    frame.dispose();
                                } else {
                                    JOptionPane.showMessageDialog(source, "Transfer amount is less than or equal to 0.");
                                }
                            }
                            else {
                                JOptionPane.showMessageDialog(source, "Didn't pick a currency.");
                            }
                        }
                        else{
                            JOptionPane.showMessageDialog(source, "Did not enter a valid number to transfer.");
                        }
                    }
                    catch(NumberFormatException e1){
                        JOptionPane.showMessageDialog(source, "Enter a valid number.");
                    }
                }
                else{
                    JOptionPane.showMessageDialog(source, "Please select an account from the list.");
                }
            }
        });

        JButton transferTo = new JButton("Transfer to");
        transferTo.setBounds(10, 190, 140, 25);
        panel.add(transferTo);

        //add to current selection
        transferTo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton source = (JButton) e.getSource();
                int selected = table.getSelectedRow();
                if (selected != -1) {
                    String altAccS = JOptionPane.showInputDialog("Enter account number of other account: ");
                    try {
                        Integer alt_index = Integer.valueOf(altAccS);
                        Account a = bc.getAccount(alt_index);
                        Integer account_index = (Integer) tabelModel.getValueAt(selected, 1);
                        if (a != null) {
                            if (a instanceof Loan) {
                                JOptionPane.showMessageDialog(source, "Account given is a loan.");
                            } else {
                                String amtS = JOptionPane.showInputDialog("Enter amount to transfer: ");
                                Currency c = (Currency)JOptionPane.showInputDialog(source, "Which currency", "Currency Selection", JOptionPane.QUESTION_MESSAGE, null, b.getAllCurrencies(), b.getAllCurrencies()[0]);
                                double amt = Double.parseDouble(amtS);
                                if (c != null) {
                                    if (a instanceof Checking) {
                                        if (a.getBalance() >= (amt * c.getConversionToBaseRate()) + b.getCheckingAccountTransactionFee()) {
                                            double initBa = a.getBalance();
                                            double initB = bc.getAccount(account_index).getBalance();
                                            a.removeBalance((amt * c.getConversionToBaseRate()) + b.getCheckingAccountTransactionFee());
                                            bc.getAccount(account_index).addBalance((amt * c.getConversionToBaseRate()));
                                            bc.getAccount(account_index).addTransaction(new Transaction(bc.getAccount(account_index).getAccountType(), bc.getAccount(account_index).getIndex(), "Transfer to", initB, bc.getAccount(account_index).getBalance(), 0));
                                            b.recentTransactions.put(b.getTransactionCounter(), new Transaction(bc.getAccount(account_index).getAccountType(), bc.getAccount(account_index).getIndex(), "Transfer to", initB, bc.getAccount(account_index).getBalance(), 0));
                                            b.setTransactionCounter(b.getTransactionCounter() + 1);
                                            a.addTransaction(new Transaction(a.getAccountType(), a.getIndex(), "Transfer from", initBa, a.getBalance(), b.getCheckingAccountTransactionFee()));
                                            b.recentTransactions.put(b.getTransactionCounter(), new Transaction(a.getAccountType(), a.getIndex(), "Transfer from", initBa, a.getBalance(), b.getCheckingAccountTransactionFee()));
                                            b.setTransactionCounter(b.getTransactionCounter() + 1);
                                        } else {
                                            JOptionPane.showMessageDialog(source, "Insufficient funds. The checking account transaction fee is " + b.getCheckingAccountTransactionFee() + ".");
                                        }
                                    } else {
                                        if (a.getBalance() >= (amt * c.getConversionToBaseRate())) {
                                            double initBa = a.getBalance();
                                            double initB = bc.getAccount(account_index).getBalance();
                                            a.removeBalance((amt * c.getConversionToBaseRate()));
                                            bc.getAccount(account_index).addBalance((amt * c.getConversionToBaseRate()));
                                            bc.getAccount(account_index).addTransaction(new Transaction(bc.getAccount(account_index).getAccountType(), bc.getAccount(account_index).getIndex(), "Transfer to", initB, bc.getAccount(account_index).getBalance(), 0));
                                            b.recentTransactions.put(b.getTransactionCounter(), new Transaction(bc.getAccount(account_index).getAccountType(), bc.getAccount(account_index).getIndex(), "Transfer to", initB, bc.getAccount(account_index).getBalance(), 0));
                                            b.setTransactionCounter(b.getTransactionCounter() + 1);
                                            a.addTransaction(new Transaction(a.getAccountType(), a.getIndex(), "Transfer from", initBa, a.getBalance(), 0));
                                            b.recentTransactions.put(b.getTransactionCounter(), new Transaction(a.getAccountType(), a.getIndex(), "Transfer from", initBa, a.getBalance(), 0));
                                            b.setTransactionCounter(b.getTransactionCounter() + 1);
                                        } else {
                                            JOptionPane.showMessageDialog(source, "Insufficient funds.");
                                        }
                                    }
                                    place(bc);
                                    frame.dispose();
                                }
                                else{
                                    JOptionPane.showMessageDialog(source, "Did not enter a currency.");
                                }
                            }
                        } else {
                            JOptionPane.showMessageDialog(source, "Invalid account number.");
                        }
                    }
                    catch(NumberFormatException e1){
                        JOptionPane.showMessageDialog(source, "Enter a valid number.");
                    }
                }
                else{
                    JOptionPane.showMessageDialog(source, "Please select an account from the list.");
                }
            }
        });

        JButton viewTs = new JButton("View Transactions");
        viewTs.setBounds(10, 220, 140, 25);
        panel.add(viewTs);


        viewTs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton source = (JButton) e.getSource();
                int selected = table.getSelectedRow();
                if (selected != -1) {
                    Integer account_index = (Integer) tabelModel.getValueAt(selected, 1);
                    ViewTransactions vt = new ViewTransactions(b);
                    vt.place(bc, bc.getAccount(account_index).getAllTransactions());
                    frame.dispose();
                    //viewTransactions1Acc(frame, bc, bc.getAccount(account_index).getAllTransactions());
                }
                else{
                    JOptionPane.showMessageDialog(source, "Please select an account from the list.");
                }
            }
        });

        JButton resolveLoans = new JButton("Resolve Loans");
        resolveLoans.setBounds(10, 250, 140, 25);
        panel.add(resolveLoans);


        resolveLoans.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton source = (JButton) e.getSource();
                Collection<Account> accs = bc.getAllAccounts();
                ArrayList<Integer> indexes = new ArrayList<>();
                for (Account a:accs){
                    if (a instanceof Loan){
                        if (a.getBalance() >= b.getCloseAccountFee()){
                            indexes.add(a.getIndex());
                        }
                    }
                }
                for (int i=0; i<indexes.size(); i++){
                    Transaction check = bc.removeAccount(indexes.get(i), b.getCloseAccountFee());
                    if (check != null) {
                        b.recentTransactions.put(b.getTransactionCounter(), check);
                        b.setTransactionCounter(b.getTransactionCounter() + 1);
                    }
                }
                JOptionPane.showMessageDialog(source, "All loans with balances greater than the close account fee: " + b.getCloseAccountFee() + " were closed.");
                place(bc);
                frame.dispose();
            }
        });




        frame.getContentPane().add(splitPane);
        frame.setVisible(true);
    }

    public boolean checkEnough(double amt, double comparison, Currency c){
        if ((amt * c.getConversionToBaseRate()) > comparison){
            return true;
        }
        else {
            return false;
        }
    }

    public boolean checkEnough(double amt, double comparison){
        if ((amt) > comparison){
            return true;
        }
        else {
            return false;
        }
    }
}
