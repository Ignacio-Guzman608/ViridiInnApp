package auth;

import models.User;
import repositories.SettingsDAO;
import repositories.UserDAO;

import java.time.Duration;
import java.time.LocalDateTime;

public class LoginManager {

    private static LoginManager instance;

    private final UserDAO userDAO = new UserDAO();
    private final SettingsDAO settingsDAO = new SettingsDAO();

    private User currentUser;
    private LocalDateTime lastActivity;

    private boolean loginEnabled;
    private int timeoutMinutes;

    private LoginManager() {
        refreshConfig();
    }

    public static LoginManager getInstance() {
        if (instance == null) instance = new LoginManager();
        return instance;
    }

    // ---------- CONFIG ----------
    public void refreshConfig() {
        loginEnabled = settingsDAO.getBoolean("login.enabled", true);
        timeoutMinutes = settingsDAO.getInt("login.timeoutMinutes", 30);
    }

    public boolean isLoginEnabled() { return loginEnabled; }

    public void setLoginEnabled(boolean enabled) {
        this.loginEnabled = enabled;
        settingsDAO.set("login.enabled", String.valueOf(enabled));
    }

    // ---------- SESIÓN ----------
    public User getCurrentUser() { return currentUser; }

    public boolean isAuthenticated() {
        if (currentUser == null) return false;

        if (timeoutMinutes > 0 && lastActivity != null) {
            long elapsed = Duration.between(lastActivity, LocalDateTime.now()).toMinutes();
            if (elapsed >= timeoutMinutes) {
                logout();
                return false;
            }
        }
        return true;
    }

    public boolean login(String username, String password) {
        User user = userDAO.authenticate(username, password);
        if (user == null) return false;
        this.currentUser = user;
        this.lastActivity = LocalDateTime.now();
        return true;
    }

    public void logout() {
        this.currentUser = null;
        this.lastActivity = null;
    }

    public void touch() {
        if (currentUser != null) lastActivity = LocalDateTime.now();
    }

    public boolean hasRole(String role) {
        return currentUser != null && currentUser.getRole().equals(role);
    }
}
