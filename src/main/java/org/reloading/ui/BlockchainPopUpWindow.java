package org.reloading.ui;

import org.reloading.blockchain.Block;
import org.reloading.blockchain.Transaction;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static org.reloading.ui.BlockchainMainWindow.closeForPopups;

public class BlockchainPopUpWindow extends JDialog {

    public BlockchainPopUpWindow(BlockchainMainWindow mainWindow, Block block) {
        super(mainWindow, "Transactions of block " + block.getUuid(), true);
        setModal(true);

        if (block.getUnmodifiableTransactions().isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "No transactions to display.", "Error", JOptionPane.ERROR_MESSAGE);
            closeForPopups(mainWindow, this);
        }

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeForPopups(mainWindow, BlockchainPopUpWindow.this);
            }
        });


        // Must contain at least one transaction
        var transactions = block.getUnmodifiableTransactions();

        String[] columnNames = Transaction.getColumnNamesForTable();
        String[][] data = transactions.stream().map(Transaction::getDataForTable).toArray(String[][]::new);

        DefaultTableModel model = new ReadOnlyTableModel(data, columnNames);
        JTable table = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(table);

        JButton closeButton = WindowUtility.createButton("Close",
                e -> closeForPopups(mainWindow, BlockchainPopUpWindow.this));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(closeButton, BorderLayout.SOUTH);

        setContentPane(panel);
        pack();
        setLocationRelativeTo(mainWindow);
    }
}
