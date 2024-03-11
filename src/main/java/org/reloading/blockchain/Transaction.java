package org.reloading.blockchain;

import org.reloading.exceptions.InvalidTransactionException;
import org.reloading.persons.Account;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Transaction implements BlockDataProvider {
    private final UUID uuid;
    private final Account accountSender;
    private final Account accountReceiver;
    private final double amount;
    private String signature;

    public Transaction(final Account accountSender, final Account accountReceiver, final double amount) {
        if (amount <= 0) throw new InvalidTransactionException("Amount must be positive");

        if (accountSender.equals(accountReceiver)) throw new InvalidTransactionException("Transaction: Sender and " +
                "receiver cannot be the same person");


        /*
        // Check if the sender has enough money to send
        if (accountSender.getBalance() < amount)
            throw new IllegalArgumentException("Transaction: Not enough money to send " + amount + " from " +
                    accountSender.getPersonName() + " (" + accountSender.getBalance() + ")");
        */

        this.uuid = UUID.randomUUID();
        this.accountSender = accountSender;
        this.accountReceiver = accountReceiver;
        this.amount = amount;
    }

    @Override
    public void signTransaction() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        String transactionData = accountSender.getPersonUUID().toString() + accountReceiver.getPersonUUID().toString() + amount;
        this.signature = accountSender.signTransaction(transactionData);
    }

    @Override
    public boolean verifyTransaction() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, InvalidKeySpecException {
        if (signature == null) return false;
        String transactionData = accountSender.getPersonUUID().toString() + accountReceiver.getPersonUUID().toString() + amount;
        return accountReceiver.verifyTransaction(transactionData, signature, accountSender.getKeyPair().getPublic());
    }


    @Override
    public void perform() {
        try {
            if (verifyTransaction()) {
                accountSender.removeAmount(amount);
                accountReceiver.addAmount(amount);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void undo() {
        accountSender.addAmount(amount);
        accountReceiver.removeAmount(amount);
    }

    @Override
    public boolean isValid() {
        // TODO: change
        return validateTransitionsByUUID(List.of(this));
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    public Account getAccountSender() {
        return accountSender;
    }

    public Account getAccountReceiver() {
        return accountReceiver;
    }

    public double getAmount() {
        return amount;
    }

    public String getSignature() {
        return signature;
    }

    @Override
    public String[] getColumnNamesForTable() {
        return new String[]{"UUID", "Sender", "Receiver", "Amount"};
    }

    @Override
    public String[] getDataForTable() {
        return new String[]{
                uuid.toString(),
                accountSender.getPersonName(),
                accountReceiver.getPersonName(),
                String.valueOf(amount)
        };
    }

    public static boolean validateTransitionsByUUID(List<Transaction> transactions) {
        HashMap<UUID, Double> map = new HashMap<>(transactions.size());

        for (Transaction transaction : transactions) {
            Account sender = transaction.getAccountSender();
            Account receiver = transaction.getAccountReceiver();

            UUID senderUUID = sender.getPersonUUID();
            UUID receiverUUID = receiver.getPersonUUID();

            map.putIfAbsent(senderUUID, sender.getBalance()); // if sender not in map, add it
            map.putIfAbsent(receiverUUID, receiver.getBalance()); // if receiver not in map, add it

            double newBalanceSender = map.get(senderUUID) - transaction.getAmount();
            double newBalanceReceiver = map.get(receiverUUID) + transaction.getAmount();
            if (newBalanceSender < 0) return false;

            map.put(senderUUID, newBalanceSender);
            map.put(receiverUUID, newBalanceReceiver);
        }

        return true;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "sender=" + accountSender +
                ", receiver=" + accountReceiver +
                ", amount=" + amount +
                ", signature='" + signature + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
