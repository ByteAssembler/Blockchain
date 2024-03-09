package org.reloading.persons;

import java.util.UUID;

public class Person {
    private final UUID uuid;
    private final String name;

    public Person(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public Person(String name) {
        this(UUID.randomUUID(), name);
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
