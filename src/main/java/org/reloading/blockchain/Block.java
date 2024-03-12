package org.reloading.blockchain;

import org.reloading.Print;
import org.reloading.secure.Encrypt;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Block implements Print {
    private boolean mined = false;
    private final UUID uuid;
    private final Date creationDateTime;
    private final List<Transaction> transactions;
    private String previousHash;
    private String hash = null;
    private long nonce = 0;


    public Block(UUID uuid, String previousHash, List<Transaction> transactions, final Date creationDateTime) {
        this.uuid = uuid;
        this.previousHash = previousHash;
        this.transactions = transactions;
        this.creationDateTime = creationDateTime;
    }

    public Block(UUID uuid, List<Transaction> transactions, Date creationDateTime) {
        this(uuid, null, transactions, creationDateTime);
    }

    public Block(UUID uuid, String previousHash, List<Transaction> transactions) {
        this(uuid, previousHash, transactions, new Date());
    }

    public Block(String previousHash, List<Transaction> transactions) {
        this(UUID.randomUUID(), previousHash, transactions);
    }

    public Block(List<Transaction> transactions) {
        this(null, transactions);
    }


    public UUID getUuid() {
        return uuid;
    }

    public Date getCreationDateTime() {
        return creationDateTime;
    }

    @Deprecated
    public List<Transaction> getTransactions() {
        if (transactions == null) return Collections.emptyList();
        return transactions;
    }

    public List<Transaction> getUnmodifiableTransactions() {
        if (transactions == null) return Collections.emptyList();
        return Collections.unmodifiableList(transactions);
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    @Deprecated
    public String getPreviousHash() {
        return previousHash;
    }

    public String calculateHash() {
        hash = Encrypt.sha256(this.toString());
        return hash;
    }

    @Deprecated
    public String getHash() {
        return hash;
    }

    public void mine() {
        if (previousHash == null) throw new IllegalArgumentException("Block: Previous hash cannot be null");

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
        return transactions == null;
    }

    public boolean isValid() {
        return this.hash.startsWith(Blockchain.difficultyPrefix)
                && this.previousHash.startsWith(Blockchain.difficultyPrefix)
                && areTransactionsValid();
    }

    public boolean isGenesisBlockAndIsValid() {
        return isGenesisBlock() && isValid();
    }

    public static Block createGenesisBlock() {
        UUID genesisUUID = new UUID(0, 0);
        String emptyHash = "0".repeat(64);

        Block genesisBlock = new Block(genesisUUID, emptyHash, null);
        genesisBlock.mine();
        return genesisBlock;
    }


    public boolean areTransactionsValid() {
        return Transaction.validateTransitionsByUUID(getUnmodifiableTransactions());
    }

    public void perform() {
        transactions.forEach(BlockDataProvider::perform);
    }

    public void signTransaction() throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
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
