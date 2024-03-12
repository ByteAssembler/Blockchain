package org.reloading.blockchain;

import org.reloading.Print;
import org.reloading.exceptions.BlockInvalidException;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.List;

public class Blockchain implements Print {
    public static int hashPrefixDifficulty = 4;
    public static String difficultyPrefix = "0".repeat(hashPrefixDifficulty);
    private final List<Block> blocks;

    public Blockchain() {
        this(new ArrayList<>());
    }

    public Blockchain(List<Block> blocks) {
        if (blocks == null) throw new IllegalArgumentException("List<Block> blocks cannot be null!");
        if (blocks.isEmpty()) blocks.add(Block.createGenesisBlock());
        this.blocks = blocks;
    }

    // Only for the CLI
    @Deprecated
    public void removeBlockByIndex(int index) {
        if (index < 0 || index >= blocks.size()) throw new IllegalArgumentException("Index out of bounds");
        blocks.remove(index);
    }

    public void addBlock(Block block) throws BlockInvalidException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        if (block == null) throw new IllegalArgumentException("Block cannot be null");
        // throw new IllegalArgumentException("A non genesis block must contain at least one transaction");

        if (blocks.isEmpty()) {
            // This is the genesis block
            if (!block.isGenesisBlock())
                throw new BlockInvalidException("First Block in Blockchain has to be a Genesis Block!");
            if (!block.isValid()) throw new BlockInvalidException("Block is invalid!");
            blocks.add(block);
            return;
        }

        // Current block set the previous hash to the hash of the previous block
        Block previousBlock = getPreviousBlock();
        block.setPreviousHash(previousBlock.getHash());

        // Check if sender and receiver are not the same person (in Transaction Constructor)

        // Check if the multiple sender have enough money to send
        if (!block.areTransactionsValid()) // if (!block.validateTransitionsByUUID())
            throw new BlockInvalidException("Block: Transaction/s are not valid");

        block.signTransaction();

        // Mine the block
        block.mine();

        // Check if the block is valid
        if (!block.isValid()) throw new IllegalArgumentException("Block: Block is not valid. This should not happen!");


        // Update the balance of the sender
        // Update the balance of the receiver
        // TODO: block.getTransactions().forEach(Transaction::performTransaction);
        block.perform(); // TODO: check

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
