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
    private boolean mined = false;
    private String previousHash;
    private String hash = null;
    private long nonce = 0;


    private Block(UUID uuid, String previousHash, List<Transaction> transactions, final Date creationDateTime) {
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

    @Deprecated
    public Optional<String> getPreviousHash() {
        // if (previousHash == null) throw new NullPointerException("The previous hash is null");
        if (previousHash.isEmpty()) return Optional.empty();
        return Optional.of(previousHash);
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    private void calculateHash() {
        hash = Encrypt.sha256(this.toString());
    }

    @Deprecated // (forRemoval = true)
    public String getHash() {
        return hash;
    }

    public String getHashOrMine() throws BlockInvalidException {
        mine();
        return hash;
    }

    public void mine() throws BlockInvalidException {
        if (previousHash == null) throw new BlockInvalidException("The previous hash was not given." +
                "Previous hash cannot be null");

        calculateHash();
        while (!hash.startsWith(Blockchain.difficultyPrefix)) {
            nonce++;
            calculateHash();
        }

        mined = true;
    }

    public boolean isMined() {
        return mined;
    }

    public boolean isGenesisBlock() {
        // Optional: Check that the UUID bytes from the Genesis block are zero.
        // Optional: Check that the hash contains only zeros
        return transactions == null;
    }

    public boolean isValid() {
        if (hash == null || previousHash == null) return false;
        return this.hash.startsWith(Blockchain.difficultyPrefix)
                && this.previousHash.startsWith(Blockchain.difficultyPrefix)
                && areTransactionsValidAndSigned();
    }

    public boolean isGenesisBlockAndIsValid() {
        return isGenesisBlock() && isValid();
    }

    public boolean areTransactionsValidAndSigned() {
        return Transaction.validateTransitionsByUUID(getUnmodifiableTransactions()) && areTransactionsSignaturesValid();
    }

    private boolean areTransactionsSignaturesValid() {
        return Transaction.areTransactionsSignaturesValid(getUnmodifiableTransactions());
    }

    public void perform() throws NegativeAmountException, NotEnoughMoneyException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, InvalidKeyException {
        for (Transaction transaction : transactions) transaction.perform();
    }

    public void signTransactions() throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        for (Transaction transaction : transactions) transaction.sign();
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
