package com.company;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class BankerViewStocks {
    private Bank b;

    public BankerViewStocks(Bank bb) {
        b = bb;
    }

    public void place(Banker bk, Savings savingAccount) {
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
        //SecurityAccount securityAccount = new SecurityAccount(allStocks, market, savingAccount);

        String cols[] = {"Stock Number", "Company Name", "Code", "Price", "Share"};
        DefaultTableModel tabelModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Stock s : allStocks) {
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
                b.ownerView(bk);
                frame.dispose();
            }
        });

        frame.getContentPane().add(splitPane);
        frame.setVisible(true);
    }
}
