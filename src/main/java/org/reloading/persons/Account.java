package org.reloading.persons;

import org.reloading.Print;
import org.reloading.exceptions.NegativeAmountException;
import org.reloading.exceptions.NotEnoughMoneyException;
import org.reloading.secure.KeyPairGenerator;

import java.math.BigDecimal;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.UUID;

import static org.reloading.persons.Accounts.account;

public class Account implements Print {
    private final Person person;
    private BigDecimal balance;
    private final KeyPair keyPair;

    public Account(Person person, BigDecimal balance) {
        this.person = person;
        this.balance = balance;

        keyPair = KeyPairGenerator.generateKeyPair();

        // Check if the account already exists - only for the CLI
        if (Accounts.checkIfAccountWithPersonNameExists(person.getName()))
            throw new IllegalArgumentException("Account: Account with name (similar) " + person.getName() + " already exists.");

        account.add(this);
    }

    public Account(String personName, BigDecimal balance) {
        this(new Person(personName), balance);
    }

    public Account(String personName, double balance) {
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

    public boolean verifyTransaction(String data, String signature, PublicKey publicKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, InvalidKeySpecException {
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initVerify(publicKey);
        sig.update(data.getBytes());
        byte[] signatureBytes = Base64.getDecoder().decode(signature);
        return sig.verify(signatureBytes);
    }

    public void addAmount(BigDecimal amount) {
        this.balance = this.balance.add(amount);
        // this.balance += amount;
    }

    public void removeAmount(BigDecimal amount) throws NotEnoughMoneyException {
        BigDecimal tmp = this.balance.subtract(amount);

        if (tmp.compareTo(BigDecimal.ZERO) < 0)
            throw new NotEnoughMoneyException("Account: Not enough money to remove " + amount + " from " + getPersonName() + " (" + this.balance + ")");
        else this.balance = tmp;
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
