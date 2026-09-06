package hostel;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.*;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.security.spec.InvalidKeySpecException;

public class AuthDatabase implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String DATABASE_FILE = "users.db";
    private static final int ITERATIONS = 120_000;
    private static final int KEY_LENGTH = 256;
    private final Map<String, UserAccount> accounts = new HashMap<>();
    private transient SecureRandom random = new SecureRandom();

    public AuthDatabase() { load(); }

    public UserAccount register(String name, String email, String password, String confirmation, String googleId) {
        String normalizedEmail = normalize(email);
        if (name.isBlank() || normalizedEmail.isBlank() || password.length() < 8) throw new IllegalArgumentException("Enter a name, email, and password of at least 8 characters.");
        if (!password.equals(confirmation)) throw new IllegalArgumentException("Passwords do not match.");
        if (accounts.containsKey(normalizedEmail)) throw new IllegalArgumentException("An account with this email already exists.");
        byte[] salt = new byte[16]; random().nextBytes(salt);
        UserAccount account = new UserAccount(normalizedEmail, name.trim(), googleId.trim(), salt, hash(password.toCharArray(), salt));
        accounts.put(normalizedEmail, account); save(); return account;
    }

    public UserAccount login(String email, String password) {
        UserAccount account = accounts.get(normalize(email));
        if (account == null || !MessageDigest.isEqual(account.getPasswordHash(), hash(password.toCharArray(), account.getPasswordSalt()))) throw new IllegalArgumentException("Invalid email or password.");
        return account;
    }

    public void resetPassword(String email, String newPassword, String confirmation) {
        UserAccount existing = accounts.get(normalize(email));
        if (existing == null) throw new IllegalArgumentException("No account was found for that email.");
        if (newPassword.length() < 8) throw new IllegalArgumentException("Password must be at least 8 characters.");
        if (!newPassword.equals(confirmation)) throw new IllegalArgumentException("Passwords do not match.");
        byte[] salt = new byte[16]; random().nextBytes(salt);
        accounts.put(existing.getEmail(), new UserAccount(existing.getEmail(), existing.getName(), existing.getGoogleId(), salt, hash(newPassword.toCharArray(), salt))); save();
    }

    private byte[] hash(char[] password, byte[] salt) {
        try { return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH)).getEncoded(); }
        catch (NoSuchAlgorithmException | InvalidKeySpecException exception) { throw new IllegalStateException("Password security is unavailable.", exception); }
    }
    private String normalize(String email) { return email == null ? "" : email.trim().toLowerCase(); }
    private SecureRandom random() { if (random == null) random = new SecureRandom(); return random; }

    private void load() {
        File file = new File(DATABASE_FILE); if (!file.exists()) return;
        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(file))) { accounts.putAll(((AuthDatabase) input.readObject()).accounts); }
        catch (IOException | ClassNotFoundException exception) { throw new IllegalStateException("Could not open the user database.", exception); }
    }
    private void save() {
        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(DATABASE_FILE))) { output.writeObject(this); }
        catch (IOException exception) { throw new IllegalStateException("Could not save the user database.", exception); }
    }
}