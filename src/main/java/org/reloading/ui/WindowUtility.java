package org.reloading.ui;

import org.reloading.blockchain.Block;
import org.reloading.blockchain.Blockchain;

import java.awt.*;

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
}
