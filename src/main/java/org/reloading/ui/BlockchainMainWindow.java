package org.reloading.ui;

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
    private final DefaultTableModel blockTableModel;
    private final JTextField accountSearchField;
    private final JTextField blockSearchField;

    public BlockchainMainWindow(Blockchain blockchain) {
        super("Blockchain");

        accountTableModel = new AccountTable();
        blockTableModel = new ReadOnlyTableModel();

        accountTableModel.addColumn("UUID");
        accountTableModel.addColumn("Name");
        accountTableModel.addColumn("Balance");

        blockTableModel.addColumn("UUID");
        blockTableModel.addColumn("DateTime");
        blockTableModel.addColumn("Data");
        blockTableModel.addColumn("PreviousHash");
        blockTableModel.addColumn("Hash");

        JTable accountTable = new JTable(accountTableModel);
        JTable blockTable = new JTable(blockTableModel);

        accountSearchField = new JTextField();
        blockSearchField = new JTextField();

        accountSearchField.setVisible(false);
        blockSearchField.setVisible(false);

        JButton leftAddButton = new JButton("Add Account");
        JButton rightAddButton = new JButton("Is valid?");
        rightAddButton.setVisible(false);

        leftAddButton.addActionListener(e -> {
            Account account = CreateAccountDialog.createAccount();
            update(blockchain);
        });

        rightAddButton.addActionListener(e -> {
            boolean isValid = blockchain.validateBlockchain();
            JOptionPane.showMessageDialog(null, "The blockchain is " + (isValid ? "" : "not ") + "valid.", "Error", isValid ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
        });

        JButton addBlock = new JButton("Add Block");
        addBlock.addActionListener(e -> {
            WindowUtility.open(blockchain);
            update(blockchain);
        });

        JPanel leftPanel = createTablePanel(accountTable, accountSearchField, leftAddButton);
        JPanel rightPanel = createTablePanel(blockTable, addBlock, rightAddButton);

        addRightClickDeletion(blockTable, blockchain);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.weightx = 0.25;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(leftPanel, gbc);

        gbc.gridx = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0.75;
        mainPanel.add(rightPanel, gbc);

        add(mainPanel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 300);
        setLocationRelativeTo(null);

        update(blockchain);

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

    private JPanel createTablePanel(JTable table, JTextField searchField, JButton addButton) {
        JPanel panel = new JPanel(new BorderLayout());

        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new BorderLayout());

        controlPanel.add(searchField, BorderLayout.CENTER);
        controlPanel.add(addButton, BorderLayout.EAST);

        panel.add(controlPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createTablePanel(JTable table, JButton button1, JButton button2) {
        JPanel panel = new JPanel(new BorderLayout());

        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new BorderLayout());

        controlPanel.add(button1, BorderLayout.CENTER);
        controlPanel.add(button2, BorderLayout.EAST);

        panel.add(controlPanel, BorderLayout.SOUTH);

        return panel;
    }

    public void update(Blockchain blockchain) {
        accountTableModel.setRowCount(0);
        blockTableModel.setRowCount(0);


        Accounts.getAccount().forEach(account -> {
            accountTableModel.addRow(new Object[]{account.getPersonUUID(), account.getPersonName(), account.getBalance()});
        });

        blockchain.getBlocks().forEach(block -> {
            var data = block.getUnmodifiableTransactions();
            var prevHash = block.getPreviousHash().orElse("null");
            if (data != null)
                blockTableModel.addRow(new String[]{block.getUuid().toString(), block.getCreationDateTime().toString(), data.toString(), prevHash, block.getHash()});
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
                update(blockchain);
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
