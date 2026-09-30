package models;

public class User {
    private int idUser;
    private String username;
    private String passwordHash;
    private String salt;
    private String fullName;
    private String role;
    private boolean active;

    public User(int idUser, String username, String passwordHash, String salt,
                String fullName, String role, boolean active) {
        this.idUser = idUser;
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.fullName = fullName;
        this.role = role;
        this.active = active;
    }

    public int getIdUser()         { return idUser; }
    public String getUsername()    { return username; }
    public String getPasswordHash(){ return passwordHash; }
    public String getSalt()        { return salt; }
    public String getFullName()    { return fullName; }
    public String getRole()        { return role; }
    public boolean isActive()      { return active; }
}
