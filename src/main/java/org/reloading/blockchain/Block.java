package org.reloading.blockchain;

import org.reloading.exceptions.BlockInvalidException;
import org.reloading.exceptions.NegativeAmountException;
import org.reloading.exceptions.NotEnoughMoneyException;
import org.reloading.secure.Encrypt;
import org.reloading.utils.Printable;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

public class Block implements Printable {
    private final UUID uuid;
    private final Date creationDateTime;
    private final List<Transaction> transactions;
    private String previousHash;
    private String hash = null;
    private long nonce = 0;


    private Block(UUID uuid, String previousHash, List<Transaction> transactions, Date creationDateTime) {
        this.uuid = uuid;
        this.previousHash = previousHash;
        this.transactions = transactions;
        this.creationDateTime = creationDateTime;
    }

    private Block(UUID uuid, List<Transaction> transactions, Date creationDateTime) {
        this(uuid, null, transactions, creationDateTime);
    }

    /**
     * Used to create the genesis block
     */
    private Block(UUID uuid, String previousHash, List<Transaction> transactions) {
        this(uuid, previousHash, transactions, new Date());
    }

    private Block(String previousHash, List<Transaction> transactions) {
        this(UUID.randomUUID(), previousHash, transactions);
    }

    public Block(List<Transaction> transactions) {
        this(null, transactions);
    }

    public static Block createGenesisBlock() {
        UUID genesisUUID = new UUID(0, 0);
        String emptyHash = "0".repeat(64);

        Block genesisBlock = new Block(genesisUUID, emptyHash, null);
        try {
            genesisBlock.mine();
        } catch (BlockInvalidException e) {
            throw new RuntimeException(e);
        }
        return genesisBlock;
    }

    public static boolean validateHash(String hash) {
        if (hash == null) return false;
        return hash.startsWith(Blockchain.difficultyPrefix);
    }

    public UUID getUuid() {
        return uuid;
    }

    public Date getCreationDateTime() {
        return creationDateTime;
    }

    public List<Transaction> getUnmodifiableTransactions() {
        if (transactions == null) return Collections.emptyList();
        return Collections.unmodifiableList(transactions);
    }

    public Optional<String> getPreviousHashUnsafe() {
        if (previousHash == null || previousHash.isEmpty()) return Optional.empty();
        return Optional.of(previousHash);
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public Optional<String> getHashUnsafe() {
        if (hash == null) return Optional.empty();
        return Optional.of(hash);
    }

    public String getHashOrMine() throws BlockInvalidException {
        mine();
        return hash;
    }

    private void calculateHash() {
        hash = Encrypt.sha256(this.toString());
    }

    public void mine() throws BlockInvalidException {
        if (previousHash == null) throw new BlockInvalidException("The previous hash was not given. " +
                "Previous hash cannot be null");

        calculateHash();
        while (!hash.startsWith(Blockchain.difficultyPrefix)) {
            nonce++;
            calculateHash();
        }
    }

    public boolean isGenesisBlock() {
        // Optional: Check that the UUID bytes from the Genesis block are zero.
        // Optional: Check that the hash contains only zeros
        return transactions == null;
    }

    public boolean isValid() {
        if (hash == null || previousHash == null) return false;
        return validateHash(hash) && validateHash(previousHash) && areTransactionsValidAndSigned();
    }

    public boolean isGenesisBlockAndIsValid() {
        return isGenesisBlock() && isValid();
    }

    public boolean areTransactionsValidAndSigned() {
        return Transaction.validateTransitionsByUUID(getUnmodifiableTransactions())
                && Transaction.areTransactionSignaturesValid(getUnmodifiableTransactions());
    }

    public void performTransactions() throws NegativeAmountException, NotEnoughMoneyException,
            NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, InvalidKeyException {
        for (Transaction transaction : transactions) transaction.perform();
    }

    public void signTransactions() throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        for (Transaction transaction : transactions) transaction.sign();
    }

    public static String[] getColumnNamesForTable() {
        return new String[]{"UUID", "DateTime", "Data", "PreviousHash", "Hash"};
    }

    @Override
    public String toString() {
        return "Block{" +
                "\n\tuuid=" + uuid +
                ", \n\tcreationDateTime=" + creationDateTime +
                ", \n\tdata=" + transactions +
                ", \n\tpreviousHash='" + previousHash + '\'' +
                ", \n\thash='" + hash + '\'' +
                ", \n\tnoise=" + nonce +
                "\n}";
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
