package hostel;

import java.io.Serializable;

public class UserAccount implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String email;
    private final String name;
    private final String googleId;
    private final byte[] passwordSalt;
    private final byte[] passwordHash;

    public UserAccount(String email, String name, String googleId, byte[] passwordSalt, byte[] passwordHash) {
        this.email = email;
        this.name = name;
        this.googleId = googleId;
        this.passwordSalt = passwordSalt;
        this.passwordHash = passwordHash;
    }

    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getGoogleId() { return googleId; }
    public byte[] getPasswordSalt() { return passwordSalt; }
    public byte[] getPasswordHash() { return passwordHash; }
}