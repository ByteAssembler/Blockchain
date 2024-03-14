package org.reloading.blockchain;

import org.reloading.exceptions.InvalidTransactionException;
import org.reloading.exceptions.NegativeAmountException;
import org.reloading.exceptions.NotEnoughMoneyException;
import org.reloading.persons.Account;

import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Transaction {
    private final UUID uuid;
    private final Account accountSender;
    private final Account accountReceiver;
    private final BigDecimal amount;
    private String signature;

    public Transaction(final Account accountSender, final Account accountReceiver, final BigDecimal amount)
            throws NegativeAmountException, InvalidTransactionException {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) throw new NegativeAmountException("Amount must be positive");

        if (accountSender.equals(accountReceiver))
            throw new InvalidTransactionException("Sender and recipient cannot be the same person");

        // Check if the sender has enough money to send (in Blockchain)

        this.uuid = UUID.randomUUID();
        this.accountSender = accountSender;
        this.accountReceiver = accountReceiver;
        this.amount = amount;
    }

    public Transaction(final Account accountSender, final Account accountReceiver, final double amount)
            throws NegativeAmountException, InvalidTransactionException {
        this(accountSender, accountReceiver, BigDecimal.valueOf(amount));
    }

    public static boolean validateTransitionsByUUID(final List<Transaction> transactions) {
        HashMap<UUID, BigDecimal> map = new HashMap<>(transactions.size());

        for (Transaction transaction : transactions) {
            Account sender = transaction.getAccountSender();
            Account receiver = transaction.getAccountReceiver();

            UUID senderUUID = sender.getPersonUUID();
            UUID receiverUUID = receiver.getPersonUUID();

            map.putIfAbsent(senderUUID, sender.getBalance()); // if sender not in map, add it
            map.putIfAbsent(receiverUUID, receiver.getBalance()); // if receiver not in map, add it

            BigDecimal newBalanceSender = map.get(senderUUID).subtract(transaction.getAmount());
            BigDecimal newBalanceReceiver = map.get(receiverUUID).add(transaction.getAmount());
            if (newBalanceReceiver.compareTo(BigDecimal.ZERO) < 0) return false;

            map.put(senderUUID, newBalanceSender);
            map.put(receiverUUID, newBalanceReceiver);
        }

        return true;
    }

    public static boolean areTransactionSignaturesValid(List<Transaction> transactions) {
        for (Transaction transaction : transactions) {
            try {
                if (!transaction.verify()) return false;
            } catch (NoSuchAlgorithmException | InvalidKeySpecException | SignatureException | InvalidKeyException e) {
                return false;
            }
        }

        return true;
    }

    public static String[] getColumnNamesForTable() {
        return new String[]{"UUID", "Sender", "Receiver", "Amount"};
    }

    public void sign() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        String transactionData = generateTransactionSignatureData();
        this.signature = accountSender.signTransaction(transactionData);
    }

    public boolean verify() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, InvalidKeySpecException {
        if (signature == null) return false;
        String transactionData = generateTransactionSignatureData();
        return accountReceiver.verifyTransaction(transactionData, signature, accountSender.getKeyPair().getPublic());
    }

    public void perform() throws NegativeAmountException, NotEnoughMoneyException, NoSuchAlgorithmException,
            SignatureException, InvalidKeySpecException, InvalidKeyException {
        if (verify()) {
            accountSender.removeAmount(amount);
            accountReceiver.addAmount(amount);
        }
    }

    public void undo() throws NegativeAmountException, NotEnoughMoneyException {
        accountSender.addAmount(amount);
        accountReceiver.removeAmount(amount);
    }

    public String generateTransactionSignatureData() {
        return accountSender.getPersonUUID().toString() + accountReceiver.getPersonUUID().toString() + amount;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Account getAccountSender() {
        return accountSender;
    }

    public Account getAccountReceiver() {
        return accountReceiver;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getSignature() {
        return signature;
    }

    public String[] getDataForTable() {
        return new String[]{
                uuid.toString(),
                accountSender.getPersonName(),
                accountReceiver.getPersonName(),
                String.valueOf(amount)
        };
    }

    @Override
    public String toString() {
        return "Transaction{"
                + "sender=" + accountSender
                + ", receiver=" + accountReceiver
                + ", amount=" + amount
                + ", signature='" + signature + '\''
                + '}';
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
