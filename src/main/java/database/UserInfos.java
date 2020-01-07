package database;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class UserInfos {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int id;
    private String username;
    private String hashedPassword;
    private int badgeId;
    private byte[] encryptedRetina;

    public UserInfos(String username, String hashedPassword, int badgeId, byte[] encryptedRetina) {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.badgeId = badgeId;
        this.encryptedRetina = encryptedRetina;
    }

    public UserInfos() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public int getBadgeId() {
        return badgeId;
    }

    public void setBadgeId(int badgeId) {
        this.badgeId = badgeId;
    }

    public byte[] getEncryptedRetina() {
        return encryptedRetina;
    }

    public void setEncryptedRetina(byte[] encryptedRetina) {
        this.encryptedRetina = encryptedRetina;
    }
}
