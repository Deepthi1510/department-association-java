package com.deptassoc.auth;

/**
 * Result object returned from authentication.
 */
public class AuthResult {
    private boolean success;
    private String role;      // STUDENT, FACULTY, ASSOCIATION_MEMBER, ADMIN
    private int userId;
    private String username;
    private String displayName; // User's real name if available

    public AuthResult(boolean success, String role, int userId, String username, String displayName) {
        this.success = success;
        this.role = role;
        this.userId = userId;
        this.username = username;
        this.displayName = displayName;
    }

    public boolean isSuccess() { return success; }
    public String getRole() { return role; }
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getDisplayName() { return displayName; }

    @Override
    public String toString() {
        return String.format("AuthResult{role='%s', userId=%d, username='%s'}", role, userId, username);
    }
}
