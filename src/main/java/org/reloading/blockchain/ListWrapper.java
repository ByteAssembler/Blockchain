package org.reloading.blockchain;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Collections;
import java.util.List;

public class ListWrapper {
    private final List<Transaction> list;

    public ListWrapper(List<Transaction> list) {
        this.list = list;
    }

    public List<Transaction> getUnmodifiableList() {
        return Collections.unmodifiableList(list);
    }

    @Deprecated
    public boolean isValid() {
        return Transaction.validateTransitionsByUUID(getUnmodifiableList());
    }

    public void perform() {
        list.forEach(BlockDataProvider::perform);
    }

    public void signTransaction() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        for (Transaction transaction : list) transaction.signTransaction();
    }

    @Override
    public String toString() {
        return list.toString();
    }
}
