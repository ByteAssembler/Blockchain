package org.reloading.blockchain;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.UUID;

public interface BlockDataProvider {
    UUID getUuid();
    void perform();
    void undo();
    boolean isValid();

    void signTransaction() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException;
    boolean verifyTransaction() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, InvalidKeySpecException;

    String[] getColumnNamesForTable();
    String[] getDataForTable();
}
