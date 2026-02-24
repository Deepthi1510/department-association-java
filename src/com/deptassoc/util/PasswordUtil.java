package com.deptassoc.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Password utility for hashing and verifying passwords.
 * Uses SHA-256 with salt. Can be replaced with BCrypt if jbcrypt is available.
 * 
 * If BCrypt is available, add dependency:
 *   org.mindrot:jbcrypt:0.4
 * And uncomment the BCrypt section below.
 */
public class PasswordUtil {
    
    private static final int SALT_LENGTH = 16;
    private static final String ALGORITHM = "SHA-256";
    
    /**
     * Hashes a password using SHA-256 with a random salt.
     * Format: salt:hash (both base64 encoded)
     */
    public static String hash(String password) {
        try {
            // Generate random salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);
            
            // Hash password with salt
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] hash = md.digest(password.getBytes());
            
            // Return salt:hash in base64
            String saltB64 = Base64.getEncoder().encodeToString(salt);
            String hashB64 = Base64.getEncoder().encodeToString(hash);
            return saltB64 + ":" + hashB64;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
    
    /**
     * Verifies a password against its hash.
     * Hash format: salt:hash (both base64 encoded)
     */
    public static boolean verify(String password, String hash) {
        try {
            // Extract salt and hash from combined string
            String[] parts = hash.split(":");
            if (parts.length != 2) {
                return false;
            }
            
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] storedHash = Base64.getDecoder().decode(parts[1]);
            
            // Hash provided password with same salt
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] computedHash = md.digest(password.getBytes());
            
            // Compare hashes
            return MessageDigest.isEqual(storedHash, computedHash);
        } catch (Exception e) {
            return false;
        }
    }
    
    // ===== OPTIONAL: BCrypt Support (uncomment if jbcrypt is added) =====
    // Uncomment below if org.mindrot:jbcrypt is available in classpath
    /*
    private static final int BCRYPT_LOG_ROUNDS = 12;
    
    public static String hashBCrypt(String password) {
        return org.mindrot.bc4j.BCrypt.hashpw(password, org.mindrot.bc4j.BCrypt.gensalt(BCRYPT_LOG_ROUNDS));
    }
    
    public static boolean verifyBCrypt(String password, String hash) {
        return org.mindrot.bc4j.BCrypt.checkpw(password, hash);
    }
    */
}
