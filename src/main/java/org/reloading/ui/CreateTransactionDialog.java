package org.reloading.ui;

import org.reloading.blockchain.Block;
import org.reloading.blockchain.Blockchain;
import org.reloading.blockchain.Transaction;
import org.reloading.exceptions.BlockInvalidException;
import org.reloading.exceptions.NegativeAmountException;
import org.reloading.exceptions.NotEnoughMoneyException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.reloading.ui.BlockchainMainWindow.closeForPopups;

public class CreateTransactionDialog extends JDialog {
    private final List<Transaction> transactions = new ArrayList<Transaction>();
    private final JTable table;

    public CreateTransactionDialog(BlockchainMainWindow mainWindow, Blockchain blockchain) {
        super(mainWindow, "Create Transaction", true);
        setModal(true);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                closeForPopups(mainWindow, CreateTransactionDialog.this);
            }
        });

        table = new JTable(new ReadOnlyTableModel());
        addRightClickDeletion(table);
        updateTable();

        JScrollPane scrollPane = new JScrollPane(table);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> closeForPopups(mainWindow, CreateTransactionDialog.this));

        JButton signButton = new JButton("Sign");
        signButton.addActionListener(e -> {
            if (transactions.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No transactions to sign.", "Error", JOptionPane.ERROR_MESSAGE);
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

            JOptionPane.showMessageDialog(null, "Transactions signed.", "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton addButtonToBlockchain = new JButton("Add to Blockchain");
        addButtonToBlockchain.addActionListener(e -> {
            if (transactions.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No transactions to add.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Block block = new Block(transactions);

            try {
                blockchain.addBlock(block);

                JOptionPane.showMessageDialog(null, "Transactions added to blockchain.", "Success", JOptionPane.INFORMATION_MESSAGE);
                mainWindow.update(blockchain);
                closeForPopups(mainWindow, CreateTransactionDialog.this);
            } catch (BlockInvalidException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(),
                        "Error: Block is invalid", JOptionPane.ERROR_MESSAGE);
            } catch (NoSuchAlgorithmException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(),
                        "Error: The algorithm for the signature is not given", JOptionPane.ERROR_MESSAGE);
            } catch (SignatureException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(),
                        "Error: The signature is not valid", JOptionPane.ERROR_MESSAGE);
            } catch (InvalidKeyException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(),
                        "Error: The signature key ist not valid", JOptionPane.ERROR_MESSAGE);
            } catch (InvalidKeySpecException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(),
                        "Error: The signature key specifications are not valid", JOptionPane.ERROR_MESSAGE);
            } catch (NegativeAmountException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(),
                        "Error: The amount can not be negative", JOptionPane.ERROR_MESSAGE);
            } catch (NotEnoughMoneyException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(),
                        "Error: An account does not have enough money", JOptionPane.ERROR_MESSAGE);
            }



            /*try {
                blockchain.addBlock(block);
                JOptionPane.showMessageDialog(null, "Transactions added to blockchain.", "Success", JOptionPane.INFORMATION_MESSAGE);
                mainWindow.update(blockchain);
                closeForPopups(mainWindow, CreateTransactionDialog.this);
            } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException ex) {
                throw new RuntimeException(ex);
            } catch (BlockInvalidException ex) {
                JOptionPane.showMessageDialog(mainWindow, "Block is invalid!", "Error", JOptionPane.ERROR_MESSAGE);
                // throw new RuntimeException(ex);
            } catch (InvalidKeySpecException ex) {
                throw new RuntimeException(ex);
            }*/
        });

        JButton addButton = new JButton("Add Transaction");
        addButton.addActionListener(e -> {
            Optional<Transaction> transactionOption = InputUtility.createTransactionDialog();
            if (transactionOption.isPresent()) {
                Transaction transaction = transactionOption.get();
                transactions.add(transaction);
                updateTable();
            }
        });

        JButton validateButton = new JButton("Validate");
        validateButton.addActionListener(e -> {
            boolean isValid = Transaction.validateTransitionsByUUID(transactions);
            JOptionPane.showMessageDialog(null, "The transactions are " + (isValid ? "" : "not ") + "valid.", "Error", isValid ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
        });
        validateButton.setVisible(false);

        JPanel panel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new GridLayout(1, 5));
        buttonPanel.add(addButton);
        buttonPanel.add(signButton);
        buttonPanel.add(addButtonToBlockchain);
        buttonPanel.add(validateButton);
        buttonPanel.add(closeButton);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(panel);
        pack();
        setLocationRelativeTo(mainWindow);
    }


    private void updateTable() {
        String[] columnNames = Transaction.getColumnNamesForTableStatic();
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

        table.addMouseListener(new java.awt.event.MouseAdapter() {
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

            /*
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.rowAtPoint(evt.getPoint());
                int col = table.columnAtPoint(evt.getPoint());
                if (row >= 0 && col >= 0) {
                    if (SwingUtilities.isRightMouseButton(evt)) {
                        int dialogResult = JOptionPane.showConfirmDialog(null, "Would you like to delete this transaction?", "Warning", JOptionPane.YES_NO_OPTION);
                        if (dialogResult == JOptionPane.YES_OPTION) {
                            transactions.remove(row);
                            updateTable();
                        }
                    }
                }
            }
            */
        });
    }
}
