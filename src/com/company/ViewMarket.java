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
    public void place(BankCustomer bc){
        JFrame frame = new JFrame("Stock Market");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(false);

        frame.setSize(520, 400);
        JPanel panel = new JPanel();
        frame.getContentPane().removeAll();

        panel.setLayout(null);


        // pull down the stockmarket from database
        //Collection<Stock> allStocks = new StockMarket().stockMarket;
        Collection<Stock> allStocks = new ArrayList<>();
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
                ViewSecurity vs = new ViewSecurity(b);
                vs.place(bc);
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

            }
        });

        //select one stock and show some more info on the stock
        JButton deleteAccount = new JButton("Show Detail");
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




        frame.getContentPane().add(splitPane);
        frame.setVisible(true);
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


