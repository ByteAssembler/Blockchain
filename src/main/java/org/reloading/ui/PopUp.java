package org.reloading.ui;

import org.reloading.blockchain.Block;
import org.reloading.blockchain.BlockDataProvider;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PopUp<T extends BlockDataProvider> extends JDialog {

    private final JTable table;

    public PopUp(BlockchainWindow mainWindow, Block<T> block) {
        super(mainWindow, "Transactions of block " + block.getUuid(), true);

        // Must contain at least one transaction
        var blockDataList = block.getData().getUnmodifiableList();

        String[] columnNames = blockDataList.get(0).getColumnNamesForTable();
        String[][] data = blockDataList.stream().map(BlockDataProvider::getDataForTable).toArray(String[][]::new);

        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        table = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(table);

        JButton closeButton = new JButton("Schließen");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                mainWindow.setEnabled(true);
            }
        });

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(closeButton, BorderLayout.SOUTH);

        setContentPane(panel);
        pack();
        setLocationRelativeTo(mainWindow);
    }

    public static <T extends BlockDataProvider> void open(Block<T> block) {
        var data = block.getData();
        if (data == null || data.getUnmodifiableList().isEmpty()) return;


        BlockchainWindow mainWindow = null;
        for (Window window : Window.getWindows()) {
            if (window instanceof BlockchainWindow) {
                mainWindow = (BlockchainWindow) window;
                break;
            }
        }

        if (mainWindow != null) {
            mainWindow.setEnabled(false);

            PopUp<T> popUp = new PopUp<>(mainWindow, block);
            popUp.setVisible(true);
        } else {
            System.out.println("Hauptfenster nicht gefunden!");
        }
    }
}
