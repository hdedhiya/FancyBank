package com.company;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.SortedSet;

public class ViewSecurity {
    private Bank b;

    public ViewSecurity(Bank bb){
        b = bb;
    }
    public void place(BankCustomer bc, Savings savingAccount){
        JFrame frame = new JFrame("FancyBank Security Account");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(false);

        frame.setSize(520, 400);
        JPanel panel = new JPanel();
        frame.getContentPane().removeAll();

        panel.setLayout(null);

        //all of myStock list
        //get this from database

        ArrayList<Stock> allStocks = SecurityAccount.getStocks(savingAccount.getIndex());
        StockMarket market = new StockMarket();
        SecurityAccount securityAccount = new SecurityAccount(allStocks, market, savingAccount);

        String cols[] = {"Stock Number", "Company Name", "Code", "Price", "Share"};
        DefaultTableModel tabelModel = new DefaultTableModel(cols, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };

        for(Stock s: allStocks){
            Object[] obj = {s.getIndex(), s.getName(), s.getCode(), s.getPrice(), s.getShares()};
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
                vm.place(bc, savingAccount);
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
                    int index = (int) tabelModel.getValueAt(selected, 0);
                    String name = (String) tabelModel.getValueAt(selected, 1);
                    String code = (String) tabelModel.getValueAt(selected, 2);
                    int shares = (int) tabelModel.getValueAt(selected, 4);

                    try {
                        if (amtS != null && !amtS.isEmpty()) {
                            int amt = Integer.parseInt(amtS);
                            boolean canSell = checkEnough(amt, shares);
                            if(canSell) {
                                //securityAccount.sellStock(name, code);
                                //connect to db

                                int newAmt = shares - amt;
                                SecurityAccount.updateStock(index, newAmt);
                                double sellPrice = securityAccount.queryPrice(name, code, amt);

                                BankCustomer.updateAccount(savingAccount.getIndex(), savingAccount.getBalance() + sellPrice);
                                ViewSecurity vs = new ViewSecurity(b);
                                vs.place(bc, savingAccount);
                                frame.dispose();

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
//
            }
        });

        JButton addButton = new JButton("Sell All");
        addButton.setBounds(10, 100, 140, 25);
        panel.add(addButton);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton source = (JButton) e.getSource();

                double profit = securityAccount.queryPriceForAll();
                for(Stock stock : allStocks) {
                    SecurityAccount.deleteStock(stock.getIndex());
                }
                SecurityAccount.sellAllStock(savingAccount.getIndex());
                BankCustomer.updateAccount(savingAccount.getIndex(), savingAccount.getBalance() + profit);
                JOptionPane.showMessageDialog(source, "All Stocks sold successfully");
                place(bc, savingAccount);
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
                double profit = securityAccount.queryPriceForAll();
                double allPrice = 0;
                for(Stock stock : allStocks) {
                    allPrice += stock.getPrice() * stock.getShares();
                }
                JOptionPane.showMessageDialog(source, "Your profit is " + (profit-allPrice));


                //todo


            }
        });





        frame.getContentPane().add(splitPane);
        frame.setVisible(true);
    }


    public boolean checkEnough(int amt, int comparison){
        return !(amt > comparison);
    }
}
