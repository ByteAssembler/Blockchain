package org.reloading.ui;

import org.jetbrains.annotations.Nullable;
import org.reloading.blockchain.Block;
import org.reloading.blockchain.Blockchain;
import org.reloading.persons.Account;
import org.reloading.persons.Accounts;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.UUID;

public class BlockchainMainWindow extends JFrame {

    private final DefaultTableModel accountTableModel;
    private final DefaultTableModel blockchainTableModel;

    public BlockchainMainWindow(Blockchain blockchain) {
        super("Blockchain");

        accountTableModel = new AccountTable(Accounts.getColumnNamesForTable());
        blockchainTableModel = new ReadOnlyTableModel(Block.getColumnNamesForTable());

        JTable accountTable = new JTable(accountTableModel);
        JTable blockchainTable = new JTable(blockchainTableModel);

        JButton addAccountButton = WindowUtility.createButton("Add Account", e -> {
            Account account = CreateAccountDialog.createAccount();
            updateContent(blockchain);
        });

        JButton isValidButton = WindowUtility.createButton("Is valid?", e -> {
            boolean isValid = blockchain.validateBlockchain();
            JOptionPane.showMessageDialog(null, "The blockchain is " + (isValid ? "" : "not ") + "valid.", "Error", isValid ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
        });

        JButton addBlock = WindowUtility.createButton("Add Block", e -> {
            WindowUtility.open(blockchain);
            updateContent(blockchain);
        });

        JPanel accountPanel = createTablePanel(accountTable, null, addAccountButton);
        JPanel blockchainPanel = createTablePanel(blockchainTable, addBlock, isValidButton);

        addRightClickDeletion(blockchainTable, blockchain);

        add(WindowUtility.createResizablePanel(.25, accountPanel, blockchainPanel));

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 300);
        setLocationRelativeTo(null);

        updateContent(blockchain);

        setVisible(true);
    }

    public static void open(Blockchain blockchain) {
        SwingUtilities.invokeLater(() -> new BlockchainMainWindow(blockchain));
    }

    public static void closeForPopups(BlockchainMainWindow mainWindow, JDialog dialog) {
        dialog.dispose();
        mainWindow.setEnabled(true);
        mainWindow.toFront();
    }

    private JPanel createTablePanel(JTable table, @Nullable JComponent component1, JComponent component2) {
        JPanel panel = new JPanel(new BorderLayout());

        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new BorderLayout());

        if (component1 != null) controlPanel.add(component1, BorderLayout.CENTER);
        controlPanel.add(component2, BorderLayout.EAST);

        panel.add(controlPanel, BorderLayout.SOUTH);

        return panel;
    }

    public void updateContent(Blockchain blockchain) {
        accountTableModel.setRowCount(0);
        blockchainTableModel.setRowCount(0);


        Accounts.getAccount().forEach(account -> {
            accountTableModel.addRow(new String[]{
                    account.getPersonUUID().toString(),
                    account.getPersonName(),
                    account.getBalance().toString()
            });
        });

        blockchain.getBlocks().forEach(block -> {
            var data = block.getUnmodifiableTransactions();
            if (data != null)
                blockchainTableModel.addRow(new String[]{
                        block.getUuid().toString(),
                        block.getCreationDateTime().toString(),
                        String.valueOf(data.size()),
                        block.getPreviousHashUnsafe().orElse("null"),
                        block.getHashUnsafe().orElse("null")
                });
        });
    }

    private void addRightClickDeletion(JTable table, Blockchain blockchain) {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem deleteItem = new JMenuItem("Delete Block");

        deleteItem.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                ((DefaultTableModel) table.getModel()).removeRow(selectedRow);

                blockchain.removeBlockByIndex(selectedRow);
                updateContent(blockchain);
            }
        });

        popupMenu.add(deleteItem);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = table.rowAtPoint(e.getPoint());
                    table.clearSelection();
                    table.addRowSelectionInterval(row, row);
                    popupMenu.show(table, e.getX(), e.getY());
                }

                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (e.getClickCount() == 2) {
                        int row = table.rowAtPoint(e.getPoint());
                        var block = blockchain.getBlocks().get(row);
                        WindowUtility.open(block);
                    }
                }
            }
        });
    }

    private static class AccountTable extends DefaultTableModel {
        public AccountTable(String[] columnNames) {
            this(columnNames, 0);
        }

        public AccountTable(String[] columnNames, int rowCount) {
            super(columnNames, rowCount);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 2; // Check if is the balance column
        }

        @Override
        public void setValueAt(Object value, int row, int column) {
            // Get UUID of the account
            var uuidStr = getValueAt(row, 0);
            if (uuidStr == null) return;
            UUID uuid = UUID.fromString(uuidStr.toString());

            // Get the account
            var account = Accounts.getAccountByPersonUUID(uuid);
            if (account.isEmpty()) return;

            // Convert entered value
            var balanceOption = CreateAccountDialog.convertDollarAmount(value.toString());
            if (balanceOption.isEmpty()) return;
            var balance = balanceOption.get();

            // Set the balance
            account.get().setBalance(balance);
            super.setValueAt(balance.toString(), row, column);
        }
    }
}
