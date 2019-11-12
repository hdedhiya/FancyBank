package com.company;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ViewAccounts{

    private Bank b;

    public ViewAccounts(Bank bb){
        b = bb;
    }
    public void place(BankCustomer bc){
        JFrame frame = new JFrame("FancyBank");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(false);

        frame.setSize(520, 400);
        JPanel panel = new JPanel();
        frame.getContentPane().removeAll();

        panel.setLayout(null);
        
        //get accounts

        ArrayList<Account> allAccs = BankCustomer.getAccounts(bc.getUsername());

        String cols[] = {"Account Type", "Account Number", "Balance"};
        DefaultTableModel tabelModel = new DefaultTableModel(cols, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        if(allAccs.size() != 0) {
        	for(int i = 0;i < allAccs.size();i++) {
        		Object[] obj = {allAccs.get(i).getAccountType(), allAccs.get(i).getIndex(), allAccs.get(i).getBalance()};
                tabelModel.addRow(obj);
        	}
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
                String[] options = {"Saving", "Checking", "Loan"};
                String str = (String)JOptionPane.showInputDialog(source, "What kind of account?", "Account Type", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                String amt = JOptionPane.showInputDialog("Enter initial amount: ");
                Currency c = (Currency)JOptionPane.showInputDialog(source, "Which currency", "Currency Selection", JOptionPane.QUESTION_MESSAGE, null, b.getAllCurrencies(), b.getAllCurrencies()[0]);
                try {
                    if (amt != null && !amt.isEmpty()) {
                        Double amt1 = Double.parseDouble(amt);
                        if (c != null) {
                        	//connect to db

                            boolean check = checkEnough(amt1, b.getOpenAccountFee(), c);
                            if (str == "Saving") {
                                if (check) {
                                    Savings newAcc = new Savings(amt1, c, b.getOpenAccountFee(), b.getSavingsInterestRate(), b.getSavingsMinAmount());
                                    BankCustomer.addAccount(AccountType.SAVINGACCOUNT, bc.getUsername(), Double.toString(newAcc.getRate()), Double.toString(newAcc.getBalance()));
                                    Account newAccount = BankCustomer.getAccounts(bc.getUsername()).get(BankCustomer.getAccounts(bc.getUsername()).size() - 1);
                                    int accountNum = newAccount.getIndex();
                                    String balance = Double.toString(newAccount.getBalance());
                                    Account.addTransaction(accountNum, AccountType.SAVINGACCOUNT, "Created Account", "0", balance, Double.toString(b.getOpenAccountFee()));

                                    place(bc);
                                    frame.dispose();
                                } else {
                                    JOptionPane.showMessageDialog(source, "Not enough initial funds for a saving account. The open fee is " + b.getOpenAccountFee());
                                }
                            } else if (str == "Checking") {
                                if (check) {
                                    Checking newAcc = new Checking(amt1, c, b.getOpenAccountFee(), 0);
                                    BankCustomer.addAccount(AccountType.CHECKINGACCOUNT, bc.getUsername(), Double.toString(newAcc.getRate()), Double.toString(newAcc.getBalance()));
                                    Account newAccount = BankCustomer.getAccounts(bc.getUsername()).get(BankCustomer.getAccounts(bc.getUsername()).size() - 1);
                                    int accountNum = newAccount.getIndex();
                                    String balance = Double.toString(newAccount.getBalance());
                                    Account.addTransaction(accountNum, AccountType.CHECKINGACCOUNT, "Created Account", "0", balance, Double.toString(b.getOpenAccountFee()));

                                    place(bc);
                                    frame.dispose();
                                } else {
                                    JOptionPane.showMessageDialog(source, "Not enough initial funds for a checking account. The open fee is " + b.getOpenAccountFee());
                                }
                            } else {
                                if (check) {
                                    double total = 0;
                                    ArrayList<Account> accs = BankCustomer.getAccounts(bc.getUsername());

                                    for(int i = 0;i < accs.size();i++) {
                                    	total += accs.get(i).getBalance();
                                    }

                                    if ((amt1 * c.getConversionToBaseRate()) <= (total * b.getMaxLoanPercent())) {
                                        Loan newAcc = new Loan(-amt1, c, b.getOpenAccountFee(), b.getLoanInterestRate());
                                        BankCustomer.addAccount(AccountType.LOANACCOUNT, bc.getUsername(), Double.toString(newAcc.getRate()), Double.toString(newAcc.getBalance()));
                                        Account newAccount = BankCustomer.getAccounts(bc.getUsername()).get(BankCustomer.getAccounts(bc.getUsername()).size() - 1);
                                        int accountNum = newAccount.getIndex();
                                        String balance = Double.toString(newAccount.getBalance());
                                        Account.addTransaction(accountNum, AccountType.LOANACCOUNT, "Created Account", "0", balance, Double.toString(b.getOpenAccountFee()));

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
                    Account account = BankCustomer.getAccount(account_index);
                    boolean checkBalance = checkEnough(account.getBalance(), b.getCloseAccountFee());
                    if (checkBalance) {
                        BankCustomer.deleteAccount(account_index);
                        Account.addTransaction(account_index, account.getAccountType(), "Account Deletion", Double.toString(account.getBalance()), "0", Double.toString(b.getCloseAccountFee()));
                    	place(bc);
                    	frame.dispose();

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
                                	Account account = BankCustomer.getAccount(account_index);
                                	System.out.println(account.getAccountType());
                                    if (account.getAccountType().equals(AccountType.CHECKINGACCOUNT)) {
                                        if (checkEnough(amt, b.getCheckingAccountTransactionFee(), c)) {
                                        	double initB = account.getBalance();
                                        	double finalAmt = (amt * c.getConversionToBaseRate()) - b.getCheckingAccountTransactionFee();
                                        	double finalB = initB + finalAmt;
                                            BankCustomer.updateAccount(account_index, finalB);
                                            Account.addTransaction(account_index, AccountType.CHECKINGACCOUNT, "Deposit", Double.toString(initB), Double.toString(finalB), Double.toString(b.getCheckingAccountTransactionFee()));

                                        } else {
                                            JOptionPane.showMessageDialog(source, "Deposit amount is less than the checking account transaction fee.");
                                        }
                                    } else {
                                    	double initB = account.getBalance();
                                    	double finalAmt = amt * c.getConversionToBaseRate();
                                    	double finalB = initB + finalAmt;
                                        if (account.getAccountType().equals(AccountType.SAVINGACCOUNT)) {
                                            //System.out.println("1");
                                            BankCustomer.updateAccount(account_index, finalB);
                                            BankCustomer.updateAccount(account_index, finalB);
                                            Account.addTransaction(account_index, AccountType.SAVINGACCOUNT, "Deposit", Double.toString(initB), Double.toString(finalB), "0");

                                        }
                                        else{
                                            //System.out.println("2");
                                            if (account.getBalance() < 0) {
                                                BankCustomer.updateAccount(account_index, finalB);
                                                Account.addTransaction(account_index, AccountType.LOANACCOUNT, "Deposit", Double.toString(initB), Double.toString(finalB), "0");

                                            }
                                            else{
                                                JOptionPane.showMessageDialog(source, "Loan is paid off already!");
                                            }
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
                                	Account account = BankCustomer.getAccount(account_index);
                                    if (account.getBalance() >= (amt * c.getConversionToBaseRate()) + b.getWithdrawalFee()) {
                                        if (account.getAccountType().equals(AccountType.CHECKINGACCOUNT)) {
                                            if (account.getBalance() >= (amt * c.getConversionToBaseRate()) + b.getWithdrawalFee() + b.getCheckingAccountTransactionFee()) {
                                            	double initB = account.getBalance();
                                            	double finalAmt = amt * c.getConversionToBaseRate() + b.getWithdrawalFee() + b.getCheckingAccountTransactionFee();
                                            	double finalB = initB - finalAmt;
                                                BankCustomer.updateAccount(account_index, finalB);
                                                Account.addTransaction(account_index, AccountType.CHECKINGACCOUNT, "Withdraw", Double.toString(initB), Double.toString(finalB), Double.toString(b.getWithdrawalFee() + b.getCheckingAccountTransactionFee()));

                                            } else {
                                                JOptionPane.showMessageDialog(source, "Insufficient funds. The withdrawal fee is " + b.getWithdrawalFee() + ", and the checking account transaction fee is " + b.getCheckingAccountTransactionFee() + ".");
                                            }
                                        } else if (account.getAccountType().equals(AccountType.SAVINGACCOUNT)) {
                                        	double initB = account.getBalance();
                                        	double finalAmt = (amt * c.getConversionToBaseRate()) + b.getWithdrawalFee();
                                        	double finalB = initB - finalAmt;
                                            BankCustomer.updateAccount(account_index, finalB);
                                            Account.addTransaction(account_index, AccountType.SAVINGACCOUNT, "Withdraw", Double.toString(initB), Double.toString(finalB), Double.toString(b.getWithdrawalFee()));

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

                                    Account currAcc = BankCustomer.getAccount(account_index);
                                    if (currAcc instanceof Checking) {
                                        if (currAcc.getBalance() >= (amt * c.getConversionToBaseRate()) + b.getCheckingAccountTransactionFee()) {
                                            String altAccS = JOptionPane.showInputDialog("Enter account number of other account: ");
                                            Integer alt_index = Integer.valueOf(altAccS);
                                            if (alt_index != account_index) {
                                                Account altAcc = BankCustomer.getAccount(alt_index);
                                                if (altAcc != null) {
                                                    if (altAcc instanceof Loan){
                                                        if (altAcc.getBalance() < 0){
                                                            double altInitB = altAcc.getBalance();
                                                            double initB = currAcc.getBalance();
                                                            double finB = initB - ((amt * c.getConversionToBaseRate()) + b.getCheckingAccountTransactionFee());
                                                            double altFinB = altInitB + (amt * c.getConversionToBaseRate());
                                                            BankCustomer.updateAccount(account_index, finB);
                                                            Account.addTransaction(account_index, currAcc.getAccountType(), "Transfer from", String.valueOf(initB), String.valueOf(finB), String.valueOf(b.getCheckingAccountTransactionFee()));
                                                            BankCustomer.updateAccount(alt_index, altFinB);
                                                            Account.addTransaction(alt_index, altAcc.getAccountType(), "Transfer to", String.valueOf(altInitB), String.valueOf(altFinB), "0");
                                                        }
                                                        else{
                                                            JOptionPane.showMessageDialog(source, "Loan is already paid off!");
                                                        }
                                                    }
                                                    else {
                                                        double altInitB = altAcc.getBalance();
                                                        double initB = currAcc.getBalance();
                                                        double finB = initB - ((amt * c.getConversionToBaseRate()) + b.getCheckingAccountTransactionFee());
                                                        double altFinB = altInitB + (amt * c.getConversionToBaseRate());
                                                        BankCustomer.updateAccount(account_index, finB);
                                                        Account.addTransaction(account_index, currAcc.getAccountType(), "Transfer from", String.valueOf(initB), String.valueOf(finB), String.valueOf(b.getCheckingAccountTransactionFee()));
                                                        BankCustomer.updateAccount(alt_index, altFinB);
                                                        Account.addTransaction(alt_index, altAcc.getAccountType(), "Transfer to", String.valueOf(altInitB), String.valueOf(altFinB), "0");

                                                    }
                                                } else {
                                                    JOptionPane.showMessageDialog(source, "Other account does not exist!");
                                                }
                                            } else {
                                                JOptionPane.showMessageDialog(source, "Entered the same account number");
                                            }
                                        } else {
                                            JOptionPane.showMessageDialog(source, "Insufficient funds. The checking account transaction fee is " + b.getCheckingAccountTransactionFee() + ".");
                                        }
                                    } else if (currAcc instanceof Savings) {
                                        if (currAcc.getBalance() >= (amt * c.getConversionToBaseRate())) {
                                            String altAccS = JOptionPane.showInputDialog("Enter account number of other account: ");
                                            Integer alt_index = Integer.valueOf(altAccS);
                                            Account altAcc = BankCustomer.getAccount(alt_index);
                                            if (alt_index != account_index) {
                                                if (altAcc != null) {
                                                    if (altAcc instanceof Loan){
                                                        if (altAcc.getBalance() < 0){
                                                            double altInitB = altAcc.getBalance();
                                                            double initB = currAcc.getBalance();
                                                            double finB = initB - ((amt * c.getConversionToBaseRate()));
                                                            double altFinB = altInitB + (amt * c.getConversionToBaseRate());
                                                            BankCustomer.updateAccount(account_index, finB);
                                                            Account.addTransaction(account_index, currAcc.getAccountType(), "Transfer from", String.valueOf(initB), String.valueOf(finB), "0");
                                                            BankCustomer.updateAccount(alt_index, altFinB);
                                                            Account.addTransaction(alt_index, altAcc.getAccountType(), "Transfer to", String.valueOf(altInitB), String.valueOf(altFinB), "0");
                                                        }
                                                        else{
                                                            JOptionPane.showMessageDialog(source, "Loan is already paid off!");
                                                        }
                                                    }
                                                    else {
                                                        double altInitB = altAcc.getBalance();
                                                        double initB = currAcc.getBalance();
                                                        double finB = initB - ((amt * c.getConversionToBaseRate()));
                                                        double altFinB = altInitB + (amt * c.getConversionToBaseRate());
                                                        BankCustomer.updateAccount(account_index, finB);
                                                        Account.addTransaction(account_index, currAcc.getAccountType(), "Transfer from", String.valueOf(initB), String.valueOf(finB), "0");
                                                        BankCustomer.updateAccount(alt_index, altFinB);
                                                        Account.addTransaction(alt_index, altAcc.getAccountType(), "Transfer to", String.valueOf(altInitB), String.valueOf(altFinB), "0");

                                                    }
                                                } else {
                                                    JOptionPane.showMessageDialog(source, "Other account does not exist!");
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


        JButton viewTs = new JButton("View Transactions");
        viewTs.setBounds(10, 190, 140, 25);
        panel.add(viewTs);


        viewTs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton source = (JButton) e.getSource();
                int selected = table.getSelectedRow();
                if (selected != -1) {
                	Integer account_index = (Integer) tabelModel.getValueAt(selected, 1);
                    ViewTransactions vt = new ViewTransactions(b);
                    vt.place(bc, account_index);
                    frame.dispose();
                }
                else{
                    JOptionPane.showMessageDialog(source, "Please select an account from the list.");
                }
            }
        });

        JButton resolveLoan = new JButton("Resolve Loan");
        resolveLoan.setBounds(10, 220, 140, 25);
        panel.add(resolveLoan);


        resolveLoan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton source = (JButton)e.getSource();
                int selected = table.getSelectedRow();
                if(selected != -1) {
                    String accountType = tabelModel.getValueAt(selected, 0).toString();
                    if(accountType.equals("Loan")){
                        Integer account_index = (Integer) tabelModel.getValueAt(selected, 1);

                        Account account = BankCustomer.getAccount(account_index);
                        if(account.getBalance() >= b.getCloseAccountFee()) {
                            BankCustomer.deleteAccount(account_index);
                            Account.addTransaction(account_index, account.getAccountType(), "Loan Resolved", Double.toString(account.getBalance()), "0", Double.toString(b.getCloseAccountFee()));

                            JOptionPane.showMessageDialog(source, "Loan account " + account_index + " is resolved.");
                            place(bc);
                            frame.dispose();
                        }else {

                            JOptionPane.showMessageDialog(source, "Loan account " + account_index + " cannot be resolved because of not enough money in balance.");
                        }
                    }else {
                        JOptionPane.showMessageDialog(source, "the account is not a Loan account.");
                    }
                }else {
                    JOptionPane.showMessageDialog(source, "please choose an account from the list.");
                }
            }
        });

        JButton resolveLoans = new JButton("Resolve all Loans");
        resolveLoans.setBounds(10, 250, 140, 25);
        panel.add(resolveLoans);


        resolveLoans.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton source = (JButton)e.getSource();

                for(Account account : allAccs) {
                    if(account.getAccountType().toString().equals("Loan") && account.getBalance() >= b.getCloseAccountFee()) {
                        BankCustomer.deleteAccount(account.getIndex());
                        Account.addTransaction(account.getIndex(), account.getAccountType(), "Account Deletion", Double.toString(account.getBalance()), "0", Double.toString(b.getCloseAccountFee()));
                    }
                }

                place(bc);
                frame.dispose();
            }
        });

        JButton viewStock = new JButton("View My Stocks");
        viewStock.setBounds(10, 280, 140, 25);
        panel.add(viewStock);


        viewStock.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton source = (JButton) e.getSource();

                int selected = table.getSelectedRow();
                if (selected != -1) {

                    int accountNum = (int) tabelModel.getValueAt(selected, 1);
                    double balance = (double) tabelModel.getValueAt(selected, 2);
                    Savings savingAccount = new Savings(AccountType.SAVINGACCOUNT);
                    savingAccount.setIndex(accountNum);
                    savingAccount.setBalance(balance);
                    ViewSecurity vs = new ViewSecurity(b);
                    vs.place(bc, savingAccount);
                    frame.dispose();

                }else {
                    JOptionPane.showMessageDialog(source, "Please select a row.");
                }

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
