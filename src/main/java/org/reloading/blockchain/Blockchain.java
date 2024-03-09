package org.reloading.blockchain;

import org.reloading.Print;
import org.reloading.exceptions.BlockInvalidException;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.List;

public class Blockchain<T extends BlockDataProvider> implements Print {
    private final List<Block<T>> blocks;

    public static int hashPrefixDifficulty = 4;
    public static String difficultyPrefix = "0".repeat(hashPrefixDifficulty);

    public Blockchain() {
        this(new ArrayList<>());
    }

    public Blockchain(List<Block<T>> blocks) {
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

    public void addBlock(Block<T> block) throws BlockInvalidException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        if (block == null)
            throw new IllegalArgumentException("Block cannot be null");
        // throw new IllegalArgumentException("A non genesis block must contain at least one transaction");

        if (blocks.isEmpty()) {
            // This is the genesis block
            if (block.isGenesisBlockAndIsValid())
                throw new BlockInvalidException("Genesis block is already set or genesis block is not valid!");
            blocks.add(block);
            return;
        }

        // Current block set the previous hash to the hash of the previous block
        Block<T> previousBlock = getPreviousBlock();
        block.setPreviousHash(previousBlock.getHash());

        ListWrapper<T> data = block.getData();

        // Check if sender and receiver are not the same person (in Transaction Constructor)

        // Check if the multiple sender have enough money to send
        if (!data.isValid()) // if (!block.validateTransitionsByUUID())
            throw new BlockInvalidException("Data is not valid"); // "Block: Transaction/s are not valid"

        data.signTransaction();

        // Mine the block
        block.mine();

        // Check if the block is valid
        if (!block.isValid()) throw new IllegalArgumentException("Block: Block is not valid. This should not happen!");


        // Update the balance of the sender
        // Update the balance of the receiver
        // TODO: block.getTransactions().forEach(Transaction::performTransaction);
        data.perform(); // TODO: check

        // Add block to the blockchain
        blocks.add(block);

        // Print
        System.out.print("Block added: ");
        block.print();
    }

    public Block<T> getPreviousBlock() {
        return blocks.get(blocks.size() - 1);
    }

    public boolean validateBlockchain() {
        if (blocks.isEmpty()) return false;
        if (blocks.size() == 1) return blocks.get(0).isGenesisBlockAndIsValid();

        for (int i = 1; i < blocks.size(); i++) {
            Block<T> currentBlock = blocks.get(i);
            Block<T> previousBlock = blocks.get(i - 1);

            String currentBlockPreviousHash = currentBlock.getPreviousHash();
            String previousBlockHash = previousBlock.getHash();

            if (!currentBlockPreviousHash.startsWith(Blockchain.difficultyPrefix)) return false;
            if (!previousBlockHash.startsWith(Blockchain.difficultyPrefix)) return false;

            if (!currentBlockPreviousHash.equals(previousBlockHash)) return false;
        }

        return true;
    }

    public List<Block<T>> getBlocks() {
        return blocks;
    }

    @Override
    public void print() {
        System.out.println("Blockchain:");
        for (Block<T> block : blocks) {
            System.out.print("- ");
            block.print();
        }
    }
}
