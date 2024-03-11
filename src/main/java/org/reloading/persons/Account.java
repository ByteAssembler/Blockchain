package org.reloading.persons;

import org.reloading.Print;
import org.reloading.secure.KeyPairGenerator;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.UUID;

import static org.reloading.persons.Accounts.account;

public class Account implements Print {
    private final Person person;
    private double balance;
    private final KeyPair keyPair;

    public Account(Person person, double balance) {
        this.person = person;
        this.balance = balance;

        keyPair = KeyPairGenerator.generateKeyPair();

        account.add(this);
    }

    public Account(String personName, double balance) {
        this(new Person(personName), balance);
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

    public double getBalance() {
        return this.balance;
    }

    // Only for the CLI
    @Deprecated
    public void setBalance(double balance) {
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

    public void addAmount(double amount) {
        this.balance += amount;
    }

    public void removeAmount(double amount) {
        double tmp = this.balance - amount;

        if (tmp < 0.0D)
            throw new IllegalArgumentException("Account: Not enough money to remove " + amount + " from " + getPersonName() + " (" + this.balance + ")");
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
