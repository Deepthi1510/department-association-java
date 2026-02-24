package com.deptassoc.swingui;

/**
 * Result object returned from authentication.
 * Contains role, user ID, username, and display name.
 */
public class AuthResult {
    private boolean success;
    private String role;        // STUDENT, FACULTY, ASSOCIATION_MEMBER
    private int userId;
    private String username;
    private String displayName;
    private int assocId;        // For ASSOCIATION_MEMBER only
    private String memberRole;  // For ASSOCIATION_MEMBER: President, Treasurer, Sports Secretary, Cultural Secretary

    public AuthResult(boolean success, String role, int userId, String username, String displayName) {
        this.success = success;
        this.role = role;
        this.userId = userId;
        this.username = username;
        this.displayName = displayName;
        this.assocId = -1;
        this.memberRole = null;
    }

    public AuthResult(boolean success, String role, int userId, String username, String displayName, int assocId) {
        this.success = success;
        this.role = role;
        this.userId = userId;
        this.username = username;
        this.displayName = displayName;
        this.assocId = assocId;
        this.memberRole = null;
    }

    public AuthResult(boolean success, String role, int userId, String username, String displayName, String memberRole) {
        this.success = success;
        this.role = role;
        this.userId = userId;
        this.username = username;
        this.displayName = displayName;
        this.assocId = -1;
        this.memberRole = memberRole;
    }

    public boolean isSuccess() { return success; }
    public String getRole() { return role; }
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getDisplayName() { return displayName; }
    public int getAssocId() { return assocId; }
    public String getMemberRole() { return memberRole; }

    @Override
    public String toString() {
        return String.format("AuthResult{role='%s', userId=%d, username='%s'}", role, userId, username);
    }
}
