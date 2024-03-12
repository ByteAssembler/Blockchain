package org.reloading.persons;

import java.util.*;

/**
 * Optional and removable part
 * <p>
 * This class exits only for the CLI to be able to get the accounts
 */
public class Accounts {
    protected static final HashSet<Account> account = new HashSet<>();

    public static Set<Account> getAccount() {
        return Collections.unmodifiableSet(account);
    }

    public static void print() {
        System.out.println("Accounts:");
        account.forEach(Account::print);
    }

    public static Optional<Account> getAccountBySimilarPersonName(String personName) {
        return account.stream().filter(account -> account.getPersonName().equalsIgnoreCase(personName)).findFirst();
    }

    public static boolean checkIfAccountWithPersonNameExists(String personName) {
        return account.stream().anyMatch(account -> account.getPersonName().equalsIgnoreCase(personName));
    }

    public static Optional<Account> getAccountByPersonName(String personName) {
        return account.stream().filter(account -> account.getPersonName().equals(personName)).findFirst();
    }

    public static Optional<Account> getAccountByPersonUUID(UUID personUUID) {
        return account.stream().filter(account -> account.getPersonUUID().equals(personUUID)).findFirst();
    }

    public static Optional<Account> getAccountByPerson(Person person) {
        return account.stream().filter(account -> account.getPerson().equals(person)).findFirst();
    }

    public static Optional<Account> getAccountByPersonNameOrPersonUUID(String nameOrUUID) {
        UUID personUUID;
        try {
            personUUID = UUID.fromString(nameOrUUID);
        } catch (IllegalArgumentException e) {
            personUUID = null;
        }

        if (personUUID != null) return getAccountByPersonUUID(personUUID);
        else return getAccountByPersonName(nameOrUUID);

    }
}
