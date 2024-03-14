package org.reloading.blockchain;

import org.reloading.utils.Printable;
import org.reloading.exceptions.BlockInvalidException;
import org.reloading.exceptions.NegativeAmountException;
import org.reloading.exceptions.NotEnoughMoneyException;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

public class Blockchain implements Printable {
    public static int hashDefaultPrefixDifficulty = 4;
    public static String difficultyPrefix = "0".repeat(hashDefaultPrefixDifficulty);
    // private final int difficulty;
    private final List<Block> blocks;

    public Blockchain() {
        this(new ArrayList<>());
    }

    public Blockchain(List<Block> blocks/*, int difficulty*/) {
        if (blocks == null) throw new IllegalArgumentException("List<Block> blocks cannot be null!");
        // if (difficulty < 0) throw new IllegalArgumentException("Difficulty cannot be negative");
        if (blocks.isEmpty()) blocks.add(Block.createGenesisBlock());
        this.blocks = blocks;
        // this.difficulty = difficulty;
    }

    // Only for the CLI
    @Deprecated
    public void removeBlockByIndex(int index) {
        if (index < 0 || index >= blocks.size()) throw new IllegalArgumentException("Index out of bounds");
        blocks.remove(index);
    }

    public void addBlock(Block block) throws NegativeAmountException, NotEnoughMoneyException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, InvalidKeyException, BlockInvalidException {
        if (block == null) throw new IllegalArgumentException("Block cannot be null");

        if (blocks.isEmpty()) {
            // This should be the genesis block
            if (!block.isGenesisBlock())
                throw new BlockInvalidException("The first block in the blockchain should be the Genesis block." +
                        "The given block is not a valid Genesis block.!");

            if (!block.isValid()) throw new BlockInvalidException("Block is invalid!");

            blocks.add(block);
            return;
        }

        if (block.getUnmodifiableTransactions().isEmpty())
            throw new BlockInvalidException("A non genesis block must contain at least one transaction");


        // The current block sets the previous hash to the hash of the previous block.
        Block previousBlock = getPreviousBlock();
        block.setPreviousHash(previousBlock.getHash());

        // Check if sender and receiver are not the same person (in Transaction Constructor)

        // Check if the multiple sender have enough money to send
        if (!block.areTransactionsValidAndSigned()) // if (!block.validateTransitionsByUUID())
            throw new BlockInvalidException("Transaction/s are not valid");

        // Mine the block
        block.mine();

        // Check if the block is valid
        if (!block.isValid()) throw new IllegalArgumentException("Block is not valid. This should not happen!");


        // Update the balance of the sender
        // Update the balance of the receiver
        block.perform();

        // Add block to the blockchain
        blocks.add(block);

        // Print
        // System.out.print("Block added: ");
        // block.print();
    }

    public Block getPreviousBlock() {
        return blocks.get(blocks.size() - 1);
    }

    public boolean validateBlockchain() {
        if (blocks.isEmpty()) return false;
        if (!blocks.get(0).isGenesisBlockAndIsValid()) return false;

        var finalBlock = blocks.stream().skip(1).reduce(blocks.get(0), (previousBlock, currentBlock) -> {
            if (!currentBlock.getPreviousHash().equals(previousBlock.getHash())) return null;
            if (!currentBlock.isValid()) return null;
            return currentBlock;
        });

        return finalBlock != null;

        /*
        for (int i = 1; i < blocks.size(); i++) {
            Block currentBlock = blocks.get(i);
            Block previousBlock = blocks.get(i - 1);

            String currentBlockPreviousHash = currentBlock.getPreviousHash();
            String previousBlockHash = previousBlock.getHash();

            if (!currentBlockPreviousHash.startsWith(Blockchain.difficultyPrefix)) return false;
            if (!previousBlockHash.startsWith(Blockchain.difficultyPrefix)) return false;

            if (!currentBlockPreviousHash.equals(previousBlockHash)) return false;
        }

        return true;
        */
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    @Override
    public void print() {
        System.out.println("Blockchain:");
        for (Block block : blocks) {
            System.out.print("- ");
            block.print();
        }
    }
}
