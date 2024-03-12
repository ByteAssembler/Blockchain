package org.reloading;

import org.reloading.blockchain.Block;
import org.reloading.blockchain.Blockchain;
import org.reloading.blockchain.Transaction;
import org.reloading.exceptions.BlockInvalidException;
import org.reloading.persons.Account;
import org.reloading.persons.Accounts;
import org.reloading.ui.BlockchainMainWindow;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Account tom = new Account("Tom", 1400);
        Account max = new Account("Max", 3500);
        Account eva = new Account("Eva", 1400);

        Blockchain blockchain = new Blockchain();

        Block block1 = new Block(Arrays.asList(new Transaction(tom, max, 300), new Transaction(tom, eva, 100)));

        Block block2 = new Block(List.of(new Transaction(max, eva, 1000)));

        Block block3 = new Block(Arrays.asList(new Transaction(tom, eva, 900), new Transaction(tom, max, 100), new Transaction(eva, max, 30)));

        Block block4 = new Block(Arrays.asList(new Transaction(tom, eva, 900), new Transaction(tom, max, 100), new Transaction(eva, max, 30)));

        try {
            blockchain.addBlock(block1);
            blockchain.addBlock(block2);
            blockchain.addBlock(block3);
            // blockchain.addBlock(block4);
        } catch (BlockInvalidException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        Accounts.print();

        BlockchainMainWindow.open(blockchain);
    }
}
