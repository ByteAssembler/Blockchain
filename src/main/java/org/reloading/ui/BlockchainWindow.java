package org.reloading.ui;

import org.reloading.blockchain.Blockchain;
import org.reloading.blockchain.Transaction;
import org.reloading.persons.Account;
import org.reloading.persons.Accounts;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.UUID;

public class BlockchainWindow extends JFrame {

    private final DefaultTableModel accountTableModel;
    private final DefaultTableModel blockTableModel;
    private final JTextField accountSearchField;
    private final JTextField blockSearchField;

    public BlockchainWindow(Blockchain<Transaction> blockchain) {
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

        JButton leftAddButton = new JButton("Add Account");
        JButton rightAddButton = new JButton("Add to Right");

        leftAddButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Account account = CreateAccountDialog.createAccount();
                update(blockchain);
                // addElement(accountTableModel, accountSearchField.getText());
            }
        });

        rightAddButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addElement(blockTableModel, blockSearchField.getText());
            }
        });

        JPanel leftPanel = createTablePanel(accountTable, accountSearchField, leftAddButton);
        JPanel rightPanel = createTablePanel(blockTable, blockSearchField, rightAddButton);

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

    private JPanel createTablePanel(JTable table, JTextField searchField, JButton addButton) {
        JPanel panel = new JPanel(new BorderLayout());

        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new BorderLayout());

        controlPanel.add(searchField, BorderLayout.CENTER);
        controlPanel.add(addButton, BorderLayout.EAST);

        panel.add(controlPanel, BorderLayout.SOUTH);

        return panel;
    }

    public void update(Blockchain<Transaction> blockchain) {
        accountTableModel.setRowCount(0);
        blockTableModel.setRowCount(0);


        Accounts.getAccount().forEach(account -> {
            accountTableModel.addRow(new Object[]{
                    account.getPersonUUID(),
                    account.getPersonName(),
                    account.getBalance()
            });
        });

        blockchain.getBlocks().forEach(block -> {
            var data = block.getData();
            blockTableModel.addRow(new String[]{
                    block.getUuid().toString(),
                    block.getCreationDateTime().toString(),
                    data == null ? "null" : data.toString(),
                    block.getPreviousHash(),
                    block.getHash()
            });
        });
    }

    private void addElement(DefaultTableModel model, String element) {
        if (!element.isEmpty()) {
            model.addRow(new Object[]{element});
        }
    }

    public static void open(Blockchain<Transaction> blockchain) {
        SwingUtilities.invokeLater(() -> new BlockchainWindow(blockchain));
    }

    private void addRightClickDeletion(JTable table, Blockchain<Transaction> blockchain) {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem deleteItem = new JMenuItem("Delete Block");

        deleteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    ((DefaultTableModel) table.getModel()).removeRow(selectedRow);

                    blockchain.removeBlockByIndex(selectedRow);
                    update(blockchain);
                }
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
                        PopUp.open(block);
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
