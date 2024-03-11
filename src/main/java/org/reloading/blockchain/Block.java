package org.reloading.blockchain;

import org.reloading.Print;
import org.reloading.secure.Encrypt;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Block implements Print {
    private boolean changeable = true;
    private final UUID uuid;
    private final Date creationDateTime;
    private final ListWrapper data; // List<Transaction> transactions;
    private String previousHash;
    private String hash = null;
    private long nonce = 0;


    public Block(UUID uuid, String previousHash, ListWrapper data, final Date creationDateTime) {
        this.uuid = uuid;
        this.previousHash = previousHash;
        this.data = data;
        this.creationDateTime = creationDateTime;
    }

    public Block(UUID uuid, ListWrapper data, Date creationDateTime) {
        this(uuid, null, data, creationDateTime);
    }

    public Block(UUID uuid, String previousHash, ListWrapper data) {
        this(uuid, previousHash, data, new Date());
    }

    public Block(String previousHash, ListWrapper data) {
        this(UUID.randomUUID(), previousHash, data);
    }

    public Block(ListWrapper data) {
        this(null, data);
    }

    public Block(List<Transaction> data) {
        this(null, new ListWrapper(data));
    }


    public UUID getUuid() {
        return uuid;
    }

    public Date getCreationDateTime() {
        return creationDateTime;
    }

    @Deprecated
    public ListWrapper getData() {
        return data;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    @Deprecated
    public String getPreviousHash() {
        return previousHash;
    }

    public void calculateHash() {
        hash = Encrypt.sha256(this.toString());
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

        changeable = false;
    }

    public boolean isMined() {
        return changeable;
    }

    public boolean isGenesisBlock() {
        return data == null;
    }

    public boolean isValid() {
        return this.hash.startsWith(Blockchain.difficultyPrefix)
                && this.previousHash.startsWith(Blockchain.difficultyPrefix);
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

    @Override
    public String toString() {
        return "Block{" +
                "\n\tuuid=" + uuid +
                ", \n\tcreationDateTime=" + creationDateTime +
                ", \n\tdata=" + data +
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
