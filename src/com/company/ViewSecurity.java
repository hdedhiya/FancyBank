package com.company;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

public class ViewSecurity {
    private Bank b;

    public ViewSecurity(Bank bb){
        b = bb;
    }
    public void place(BankCustomer bc){
        JFrame frame = new JFrame("FancyBank Security Account");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(false);

        frame.setSize(520, 400);
        JPanel panel = new JPanel();
        frame.getContentPane().removeAll();

        panel.setLayout(null);

        //all of myStock list
        //get this from database

        SQLConnection sc = new SQLConnection();
        sc.TheSqlConnection();
        ArrayList<Stock> allStocks = sc.getStocks(bc.getUsername());

        String cols[] = {"Company Name", "Code", "Price", "Share"};
        DefaultTableModel tabelModel = new DefaultTableModel(cols, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };

        for(Stock s: allStocks){
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
                ViewAccounts v = new ViewAccounts(b);
                v.place(bc);
                frame.dispose();
            }
        });

        JButton viewMarket = new JButton("View Market");
        viewMarket.setBounds(10, 40, 140, 25);
        panel.add(viewMarket);

        viewMarket.addActionListener(new ActionListener() {
            @Override

            public void actionPerformed(ActionEvent e) {
                JButton source = (JButton) e.getSource();
                ViewMarket vm = new ViewMarket(b);
                vm.place(bc);
                frame.dispose();
            }
        });

        JButton sellStock = new JButton("Sell Stock");
        sellStock.setBounds(10, 70, 140, 25);
        panel.add(sellStock);

        sellStock.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton source = (JButton) e.getSource();
                int selected = table.getSelectedRow();
                if (selected != -1) {
                    String amtS = JOptionPane.showInputDialog("Enter number of shares to sell: ");
                    int shares = (int) tabelModel.getValueAt(selected, 4); //stockNum, name, code, price, share. share at col 3
                    int stockNum = (int) tabelModel.getValueAt(selected, 0); //stockNum at 0
                    try {
                        if (amtS != null && !amtS.isEmpty()) {
                            int amt = Integer.parseInt(amtS);
                            boolean canSell = checkEnough(amt, shares);
                            if(canSell) {
                                //connect to db
                                SQLConnection sc = new SQLConnection();
                                sc.TheSqlConnection();
                                int newAmt = shares - amt;
                                sc.updateStock(stockNum, newAmt);


                                //query price on the choosing stock
                                //update the shares, or remove that stock if all sold
                                //update the database
                                //todo
                                JOptionPane.showMessageDialog(source, "Your stock is successfully sold");



                            } else {
                                JOptionPane.showMessageDialog(source, "You don't have enough shares to sell");
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
//                int selected = table.getSelectedRow();
//                if (selected != -1) {
//                    Integer stock_index = (Integer) tabelModel.getValueAt(selected, 1);
//                    boolean checkBalance = checkEnough(bc.getAccount(stock_index).getBalance(), b.getCloseAccountFee());
//                    if (checkBalance) {
//                        Transaction check = bc.removeAccount(stock_index, b.getCloseAccountFee());
//                        if (check != null) {
//                            JOptionPane.showMessageDialog(source, "Account deleted");
//                            b.recentTransactions.put(b.getTransactionCounter(), check);
//                            b.setTransactionCounter(b.getTransactionCounter() + 1);
//                            place(bc);
//                            frame.dispose();
//                        } else {
//                            JOptionPane.showMessageDialog(source, "Account did not exist or did not belong to this customer");
//                        }
//                    } else {
//                        JOptionPane.showMessageDialog(source, "Insufficient funds in account to delete. The fee is: " + b.getCloseAccountFee());
//                    }
//                }
//                else{
//                    JOptionPane.showMessageDialog(source, "Please select a row.");
//                }
            }
        });

        JButton addButton = new JButton("Sell All");
        addButton.setBounds(10, 100, 140, 25);
        panel.add(addButton);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton source = (JButton) e.getSource();
                bc.sellAllStocks();
                String username = bc.getUsername();
                SQLConnection sc = new SQLConnection();
                sc.TheSqlConnection();
                sc.sellAllStock(username);
                JOptionPane.showMessageDialog(source, "All Stocks sold successfully");
                place(bc);
                frame.dispose();
            }
        });

        JButton withdrawButton = new JButton("View Profit");
        withdrawButton.setBounds(10, 130, 140, 25);
        panel.add(withdrawButton);

        withdrawButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton source = (JButton) e.getSource();
                bc.viewStockProfit();
                JOptionPane.showMessageDialog(source, "Stock profit shown");

                //todo
                //show profit in massagedialog or label

            }
        });





        frame.getContentPane().add(splitPane);
        frame.setVisible(true);
    }


    public boolean checkEnough(int amt, int comparison){
        return !(amt > comparison);
    }
}
