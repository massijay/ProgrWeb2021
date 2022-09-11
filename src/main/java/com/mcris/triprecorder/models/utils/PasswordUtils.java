package com.mcris.triprecorder.models.utils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Objects;

public class PasswordUtils {
    private static final int ITERATIONS = 1000;
    private static final int KEY_LENGTH = 64 * 8;

    public static String hashAndSaltPassword(String plaintextPassword) {
        byte[] salt = getSalt();
        byte[] hash = hashPassword(plaintextPassword, salt, ITERATIONS);
        Base64.Encoder encoder = Base64.getEncoder();
        return ITERATIONS + ":"
                + encoder.encodeToString(salt) + ":"
                + encoder.encodeToString(hash);
    }

    public static boolean validatePassword(String plaintextToValidate, String hashedSaltedPassword) {
        String[] parts = hashedSaltedPassword.split(":");
        int iterations = Integer.parseInt(parts[0]);

        Base64.Decoder decoder = Base64.getDecoder();
        byte[] salt = decoder.decode(parts[1]);
        byte[] hash = decoder.decode(parts[2]);

        byte[] newHash = hashPassword(plaintextToValidate, salt, iterations);
        String newHSPass = parts[0] + ":" + parts[1] + ":" + Base64.getEncoder().encodeToString(newHash);
        return Objects.equals(hashedSaltedPassword, newHSPass);
    }

    private static byte[] hashPassword(String password, byte[] salt, int iterations) {
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, KEY_LENGTH);
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return factory.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] getSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }
}
