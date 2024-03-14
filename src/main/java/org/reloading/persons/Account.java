package org.reloading.persons;

import org.reloading.exceptions.AccountAlreadyExistsException;
import org.reloading.exceptions.NegativeAmountException;
import org.reloading.exceptions.NotEnoughMoneyException;
import org.reloading.secure.KeyPairGenerator;
import org.reloading.utils.Printable;

import java.math.BigDecimal;
import java.security.*;
import java.util.Base64;
import java.util.UUID;

import static org.reloading.persons.Accounts.account;

public class Account implements Printable {
    private final Person person;
    private final KeyPair keyPair;
    private BigDecimal balance;

    public Account(Person person, BigDecimal balance) throws AccountAlreadyExistsException {
        this.person = person;
        this.balance = balance;

        keyPair = KeyPairGenerator.generateKeyPair();

        // Check if the account already exists - only for the CLI
        if (Accounts.checkIfAccountWithPersonNameExists(person.getName()))
            throw new AccountAlreadyExistsException("Account with name (similar) " + person.getName() + " already exists.");

        account.add(this);
    }

    public Account(String personName, BigDecimal balance) throws AccountAlreadyExistsException {
        this(new Person(personName), balance);
    }

    public Account(String personName, double balance) throws AccountAlreadyExistsException {
        this(new Person(personName), BigDecimal.valueOf(balance));
    }

    public String getPersonName() {
        return person.getName();
    }

    public UUID getPersonUUID() {
        return person.getUuid();
    }

    public Person getPerson() {
        return person;
    }

    public BigDecimal getBalance() {
        return this.balance;
    }

    // Only for the CLI
    @Deprecated
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }

    public String signTransaction(String data) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(keyPair.getPrivate());
        signature.update(data.getBytes());
        byte[] signatureBytes = signature.sign();
        return Base64.getEncoder().encodeToString(signatureBytes);
    }

    public boolean verifyTransaction(String data, String signature, PublicKey publicKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initVerify(publicKey);
        sig.update(data.getBytes());
        byte[] signatureBytes = Base64.getDecoder().decode(signature);
        return sig.verify(signatureBytes);
    }

    public void addAmount(BigDecimal amount) throws NegativeAmountException {
        if (amount.compareTo(BigDecimal.ZERO) < 0)
            throw new NegativeAmountException("Cannot add negative amount to " + getPersonName() + " (" + this.balance + ")");

        this.balance = this.balance.add(amount);
    }

    public void removeAmount(BigDecimal amount) throws NegativeAmountException, NotEnoughMoneyException {
        BigDecimal tmp = this.balance.subtract(amount);

        if (amount.compareTo(BigDecimal.ZERO) < 0)
            throw new NegativeAmountException("Cannot remove negative amount from " + getPersonName() + " (" + this.balance + ")");

        if (tmp.compareTo(BigDecimal.ZERO) < 0)
            throw new NotEnoughMoneyException("Not enough money to remove " + amount + " from " + getPersonName() + " (" + this.balance + ")");

        this.balance = tmp;
    }

    @Override
    public String toString() {
        return getPersonName();
    }

    @Override
    public void print() {
        System.out.println(getPersonName() + ": " + getBalance());
    }

    @Override
    public int hashCode() {
        return person.hashCode();
    }
}
