package org.reloading.secure;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

public class KeyPairGenerator {
    public static KeyPair generateKeyPair() {
        try {
            java.security.KeyPairGenerator generator = java.security.KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }
}
