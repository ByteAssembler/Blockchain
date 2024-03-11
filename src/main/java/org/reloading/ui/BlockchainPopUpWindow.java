package org.reloading.ui;

import org.reloading.blockchain.Block;
import org.reloading.blockchain.BlockDataProvider;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class BlockchainPopUpWindow extends JDialog {

    private final JTable table;

    public BlockchainPopUpWindow(BlockchainMainWindow mainWindow, Block block) {
        super(mainWindow, "Transactions of block " + block.getUuid(), true);
        setModal(true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                close(mainWindow);
            }
        });


        // Must contain at least one transaction
        var blockDataList = block.getUnmodifiableTransactions();

        String[] columnNames = blockDataList.get(0).getColumnNamesForTable();
        String[][] data = blockDataList.stream().map(BlockDataProvider::getDataForTable).toArray(String[][]::new);

        DefaultTableModel model = new ReadOnlyTableModel(data, columnNames);
        table = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(table);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                close(mainWindow);
            }
        });

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(closeButton, BorderLayout.SOUTH);

        setContentPane(panel);
        pack();
        setLocationRelativeTo(mainWindow);
    }

    public static <T extends BlockDataProvider> void open(Block block) {
        var data = block.getUnmodifiableTransactions();
        if (data == null || data.isEmpty()) return;


        BlockchainMainWindow mainWindow = null;
        for (Window window : Window.getWindows()) {
            if (window instanceof BlockchainMainWindow) {
                mainWindow = (BlockchainMainWindow) window;
                break;
            }
        }

        if (mainWindow != null) {
            mainWindow.setEnabled(false);

            BlockchainPopUpWindow popUp = new BlockchainPopUpWindow(mainWindow, block);
            popUp.setVisible(true);
        }
    }

    private void close(BlockchainMainWindow mainWindow) {
        dispose();
        mainWindow.setEnabled(true);
        mainWindow.toFront();
    }
}
