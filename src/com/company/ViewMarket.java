package com.company;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;

public class ViewMarket {

    Bank b;
    public ViewMarket(Bank bb){
        b = bb;
    }
    public void place(BankCustomer bc, Savings savingAccount){
        JFrame frame = new JFrame("Stock Market");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(false);

        frame.setSize(520, 400);
        JPanel panel = new JPanel();
        frame.getContentPane().removeAll();

        panel.setLayout(null);


        // pull down the stockmarket from database
        //Collection<Stock> allStocks = new StockMarket().stockMarket;
        SQLConnection sc = new SQLConnection();
        sc.TheSqlConnection();


        Collection<Stock> market = sc.getMarket();
        String cols[] = {"Company Name", "Code", "Price", "Share"};
        DefaultTableModel tabelModel = new DefaultTableModel(cols, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };

        for(Stock s: market){
            Object[] obj = {s.getName(), s.getCode(), s.getPrice(), s.getShares()};
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
                ViewSecurity vs = new ViewSecurity(b);
                vs.place(bc, savingAccount);
                frame.dispose();
            }
        });

        //select a stock and buy. ask for how many shares and check if purchasing power is enough
        JButton addAccount = new JButton("Buy Stock");
        addAccount.setBounds(10, 40, 140, 25);
        panel.add(addAccount);

        addAccount.addActionListener(new ActionListener() {
            @Override

            public void actionPerformed(ActionEvent e) {
                JButton source = (JButton) e.getSource();

                //buy chosen stock. ask for shares. check for purchasing power
                //update after buying successfully
                int selected = table.getSelectedRow();
                if (selected != -1) {
                    String amtS = JOptionPane.showInputDialog("Enter number of shares to buy: ");
                    String companyName = (String) tabelModel.getValueAt(selected, 0); //companyName, code, price, share. share at col 3
                    String code = (String) tabelModel.getValueAt(selected, 1); // code at columm 1
                    double price = (double) tabelModel.getValueAt(selected, 2); //companyName, code, price, share. share at col 3
                    int shares = (int) tabelModel.getValueAt(selected, 3); //companyName, code, price, share. share at col 3
                    String username = bc.getUsername();
                    try {
                        if (amtS != null && !amtS.isEmpty()) {
                            int amt = Integer.parseInt(amtS);
                            boolean canBuy = checkEnough(amt, shares);
                            SQLConnection sc = new SQLConnection();
                            sc.TheSqlConnection();
                            double balance = sc.getAccount(savingAccount.getIndex()).getBalance();

                            double paidPrice = price * amt;
                            int minimum = 100; //minimum amount on saving account to buy stock
                            double purchasingPower = balance - minimum;
                            //boolean powerEnoughToBuy = checkPowerEnough();
                            if(canBuy && purchasingPower >= paidPrice) {
                                int newAmt = shares - amt;
                                sc.updateStockMarket(newAmt, code);
                                sc.addStock(savingAccount.getIndex(), companyName, code, price, amt);
                                System.out.println("my current balance is " + savingAccount.getBalance());
                                System.out.println("need to pay " + paidPrice);
                                double newBalance = balance - paidPrice;
                                sc.updateAccount(savingAccount.getIndex(), newBalance);
                                sc.close();

                                ViewSecurity vs = new ViewSecurity(b);
                                vs.place(bc, savingAccount);
                                frame.dispose();

                            } else {
                                JOptionPane.showMessageDialog(source, "You don't have enough purchasing power to buy");
                            }
                        } else {
                            JOptionPane.showMessageDialog(source, "Please enter a valid amount of shares");
                        }

                    } catch (NumberFormatException e1) {
                        JOptionPane.showMessageDialog(source, "Enter a valid number.");

                    }
                }else {
                    JOptionPane.showMessageDialog(source, "Please select a row.");
                }
            }
        });

        JButton purchasingPowerButton = new JButton("Purchasing Power");
        purchasingPowerButton.setBounds(10, 180, 140, 25);
        panel.add(purchasingPowerButton);

        purchasingPowerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton source = (JButton) e.getSource();
                SQLConnection sc = new SQLConnection();
                sc.TheSqlConnection();
                double balance = sc.getAccount(savingAccount.getIndex()).getBalance();
                sc.close();
                int minimum = 100; //minimum amount on saving account to buy stock
                double purchasingPower = balance - minimum;
                JOptionPane.showMessageDialog(source, "Your remaining purchasing power is " + purchasingPower);



            }
        });

        frame.getContentPane().add(splitPane);
        frame.setVisible(true);
    }


    public boolean checkEnough(double amt, double comparison) {
        return(amt <= comparison);

    }

    }


