package org.reloading.ui;

import org.reloading.blockchain.Block;
import org.reloading.blockchain.Blockchain;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class WindowUtility {
    public static void open(Block block) {
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

    public static void open(Blockchain blockchain) {
        BlockchainMainWindow mainWindow = null;
        for (Window window : Window.getWindows()) {
            if (window instanceof BlockchainMainWindow) {
                mainWindow = (BlockchainMainWindow) window;
                break;
            }
        }

        if (mainWindow != null) {
            mainWindow.setEnabled(false);

            CreateTransactionDialog popUp = new CreateTransactionDialog(mainWindow, blockchain);
            popUp.setVisible(true);
        }
    }

    public static JButton createButton(String text, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.addActionListener(actionListener);
        return button;
    }

    public static JComponent createResizablePanel(double resizeWeight, JComponent left, JComponent right) {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        splitPane.setResizeWeight(resizeWeight);
        splitPane.setOneTouchExpandable(true);
        splitPane.setContinuousLayout(true);
        return splitPane;
    }
}
