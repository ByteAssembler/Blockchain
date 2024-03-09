package org.reloading.blockchain;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Collections;
import java.util.List;

public class ListWrapper<T> {
    private final List<T> list;

    public ListWrapper(List<T> list) {
        this.list = list;
    }

    public List<T> getUnmodifiableList() {
        return Collections.unmodifiableList(list);
    }

    @Deprecated // It can panic
    public boolean isValid() {
        return Transaction.validateTransitionsByUUID((List<Transaction>) getUnmodifiableList());
    }

    public void perform() {
        ((List<Transaction>) list).forEach(BlockDataProvider::perform);
    }

    public void signTransaction() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        for (T transaction : list) ((Transaction) transaction).signTransaction();
    }

    @Override
    public String toString() {
        return list.toString();
    }
}
