package com.company;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

public class ViewRecent {
    Bank b;
    public ViewRecent(Bank bb){
        b = bb;
    }

    public void place(Banker bk, Collection<Transaction> ts){
        JFrame frame = new JFrame("FancyBank");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(false);

        frame.setSize(750, 400);
        JPanel panel = new JPanel();
        frame.getContentPane().removeAll();

        panel.setLayout(null);

        Collection<Transaction> allTs = ts;

        String cols[] = {"Account Type", "Account Number", "Transaction Type", "Initial Balance", "Final Balance", "Fees"};
        DefaultTableModel tabelModel = new DefaultTableModel(cols, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };

        for(Transaction a: allTs){
            Object[] obj = {a.getAccountType(), a.getAccountNumber(), a.getTransactionType(), a.getInitBalance(), a.getFinalBalance(), a.getFee()};
            tabelModel.addRow(obj);
        }

        JTable table = new JTable(tabelModel);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane listScroller = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        listScroller.setPreferredSize(new Dimension(600, 300));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                listScroller,
                panel);

        JButton backButton = new JButton("Back");
        backButton.setBounds(10, 10, 105, 25);
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
//    }
    }
}
