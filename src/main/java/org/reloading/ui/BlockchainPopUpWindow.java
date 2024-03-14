package org.reloading.ui;

import org.reloading.blockchain.Block;
import org.reloading.blockchain.Transaction;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static org.reloading.ui.BlockchainMainWindow.closeForPopups;

public class BlockchainPopUpWindow extends JDialog {

    private final JTable table;

    public BlockchainPopUpWindow(BlockchainMainWindow mainWindow, Block block) {
        super(mainWindow, "Transactions of block " + block.getUuid(), true);
        setModal(true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeForPopups(mainWindow, BlockchainPopUpWindow.this);
            }
        });


        // Must contain at least one transaction
        var blockDataList = block.getUnmodifiableTransactions();

        String[] columnNames = blockDataList.get(0).getColumnNamesForTable();
        String[][] data = blockDataList.stream().map(Transaction::getDataForTable).toArray(String[][]::new);

        DefaultTableModel model = new ReadOnlyTableModel(data, columnNames);
        table = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(table);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeForPopups(mainWindow, BlockchainPopUpWindow.this);
            }
        });

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(closeButton, BorderLayout.SOUTH);

        setContentPane(panel);
        pack();
        setLocationRelativeTo(mainWindow);
    }


}
