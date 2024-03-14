package org.reloading.ui;

import org.reloading.blockchain.Block;
import org.reloading.blockchain.Blockchain;
import org.reloading.blockchain.Transaction;
import org.reloading.exceptions.BlockInvalidException;
import org.reloading.exceptions.NegativeAmountException;
import org.reloading.exceptions.NotEnoughMoneyException;
import org.reloading.exceptions.PreviousBlockInvalidException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.reloading.ui.BlockchainMainWindow.closeForPopups;

public class CreateTransactionDialog extends JDialog {
    private final List<Transaction> transactions = new ArrayList<>();
    private final JTable table;

    public CreateTransactionDialog(BlockchainMainWindow mainWindow, Blockchain blockchain) {
        super(mainWindow, "Create Transaction", true);
        setModal(true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                closeForPopups(mainWindow, CreateTransactionDialog.this);
            }
        });

        table = new JTable(new ReadOnlyTableModel());
        addRightClickDeletion(table);
        updateTable();

        JScrollPane scrollPane = new JScrollPane(table);

        JButton closeButton = WindowUtility.createButton("Close",
                e -> closeForPopups(mainWindow, CreateTransactionDialog.this));

        JButton signButton = WindowUtility.createButton("Sign", e -> {
            if (transactions.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "No transactions to sign.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            for (Transaction transaction : transactions) {
                try {
                    transaction.sign();
                } catch (NoSuchAlgorithmException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(),
                            "Error: The algorithm for the signature is not given", JOptionPane.ERROR_MESSAGE);
                } catch (SignatureException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(),
                            "Error: The signature is not valid", JOptionPane.ERROR_MESSAGE);
                } catch (InvalidKeyException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(),
                            "Error: The signature key ist not valid", JOptionPane.ERROR_MESSAGE);
                }
            }

            JOptionPane.showMessageDialog(null, "Transactions signed.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        });


        JButton addButtonToBlockchain = WindowUtility.createButton("Add to Blockchain", e -> {
            if (transactions.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No transactions to add.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Block block = new Block(transactions);

            try {
                blockchain.addBlock(block);

                JOptionPane.showMessageDialog(null,
                        "The block with the transactions has been added.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                mainWindow.updateContent(blockchain);
                closeForPopups(mainWindow, CreateTransactionDialog.this);
            } catch (BlockInvalidException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(),
                        "Block is invalid", JOptionPane.ERROR_MESSAGE);
            } catch (NoSuchAlgorithmException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(),
                        "The algorithm for the signature is not given", JOptionPane.ERROR_MESSAGE);
            } catch (SignatureException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(),
                        "The signature is not valid", JOptionPane.ERROR_MESSAGE);
            } catch (InvalidKeyException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(),
                        "The signature key ist not valid", JOptionPane.ERROR_MESSAGE);
            } catch (InvalidKeySpecException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(),
                        "The signature key specifications are not valid", JOptionPane.ERROR_MESSAGE);
            } catch (NegativeAmountException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(),
                        "The amount can not be negative", JOptionPane.ERROR_MESSAGE);
            } catch (NotEnoughMoneyException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(),
                        "An account does not have enough money", JOptionPane.ERROR_MESSAGE);
            } catch (PreviousBlockInvalidException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(),
                        "The previous block is invalid", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton addButton = WindowUtility.createButton("Add Transaction", e -> {
            Optional<Transaction> transactionOption = InputUtility.createTransactionDialog();
            if (transactionOption.isPresent()) {
                Transaction transaction = transactionOption.get();
                transactions.add(transaction);
                updateTable();
            }
        });


        JPanel panel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4));
        buttonPanel.add(addButton);
        buttonPanel.add(signButton);
        buttonPanel.add(addButtonToBlockchain);
        buttonPanel.add(closeButton);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(panel);
        pack();
        setLocationRelativeTo(mainWindow);
    }


    private void updateTable() {
        String[] columnNames = Transaction.getColumnNamesForTable();
        String[][] data = transactions.stream().map(Transaction::getDataForTable).toArray(String[][]::new);
        table.setModel(new ReadOnlyTableModel(data, columnNames));
    }

    private void addRightClickDeletion(JTable table) {
        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem deleteItem = new JMenuItem("Delete Transaction");
        deleteItem.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                ((DefaultTableModel) table.getModel()).removeRow(selectedRow);
                transactions.remove(selectedRow);
                updateTable();
            }
        });
        popupMenu.add(deleteItem);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int r = table.rowAtPoint(e.getPoint());
                    if (r >= 0 && r < table.getRowCount()) {
                        table.setRowSelectionInterval(r, r);
                        popupMenu.show(table, e.getX(), e.getY());
                    }
                }
            }
        });
    }
}
