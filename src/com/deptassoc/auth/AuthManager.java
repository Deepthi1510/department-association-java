package com.deptassoc.auth;

import com.deptassoc.util.PasswordUtil;
import com.deptassoc.dao.StudentDao;
import com.deptassoc.dao.FacultyDao;
import com.deptassoc.dao.AssociationMemberDao;
import com.deptassoc.model.Student;
import com.deptassoc.model.Faculty;
import com.deptassoc.model.AssociationMember;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * Manages user authentication and role-based access.
 * Loads users from users.json and handles credential verification.
 */
public class AuthManager {
    
    private static final String USERS_FILE = "users.json";
    private static final List<Map<String, Object>> users = new ArrayList<>();
    private static boolean initialized = false;
    private static int loginAttempts = 0;
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    
    /**
     * Initializes the AuthManager by loading users.json.
     */
    public static void init() {
        loadUsers();
        initialized = true;
    }
    
    /**
     * Loads users from users.json into memory.
     */
    private static void loadUsers() {
        users.clear();
        File file = new File(USERS_FILE);
        
        if (!file.exists()) {
            System.out.println("\n⚠️  users.json not found. No users configured.");
            System.out.println("To create initial users, call: AuthManager.createInitialUsers()");
            return;
        }
        
        try {
            String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
            users.addAll(parseJsonArray(content));
            System.out.println("✓ Loaded " + users.size() + " users from users.json");
        } catch (Exception e) {
            System.err.println("Error loading users.json: " + e.getMessage());
        }
    }
    
    /**
     * Authenticates a user with username and password.
     */
    public static AuthResult authenticate(String username, String password) {
        if (!initialized) {
            return new AuthResult(false, null, 0, username, null);
        }
        
        if (loginAttempts >= MAX_LOGIN_ATTEMPTS) {
            System.out.println("\n❌ Maximum login attempts exceeded. Exiting.");
            System.exit(1);
        }
        
        for (Map<String, Object> user : users) {
            String storedUsername = (String) user.get("username");
            if (storedUsername.equals(username)) {
                String passwordHash = (String) user.get("passwordHash");
                if (PasswordUtil.verify(password, passwordHash)) {
                    String type = (String) user.get("type");
                    int id = ((Number) user.get("id")).intValue();
                    String displayName = getDisplayName(type, id);
                    loginAttempts = 0; // Reset on successful login
                    return new AuthResult(true, type, id, username, displayName);
                }
            }
        }
        
        loginAttempts++;
        return new AuthResult(false, null, 0, username, null);
    }
    
    /**
     * Resets login attempt counter.
     */
    public static void resetLoginAttempts() {
        loginAttempts = 0;
    }
    
    /**
     * Returns the number of failed login attempts.
     */
    public static int getLoginAttempts() {
        return loginAttempts;
    }
    
    /**
     * Creates a new user and writes to users.json.
     */
    public static void createUser(String type, int id, String username, String password) throws Exception {
        String passwordHash = PasswordUtil.hash(password);
        
        Map<String, Object> newUser = new LinkedHashMap<>();
        newUser.put("type", type);
        newUser.put("id", id);
        newUser.put("username", username);
        newUser.put("passwordHash", passwordHash);
        
        users.add(newUser);
        writeUsers();
        System.out.println("✓ User created: " + username + " (" + type + ")");
    }
    
    /**
     * Writes all users to users.json atomically.
     */
    private static void writeUsers() throws Exception {
        File file = new File(USERS_FILE);
        String json = toJsonArray(users);
        
        // Atomic write using temp file
        File tempFile = new File(USERS_FILE + ".tmp");
        Files.write(tempFile.toPath(), json.getBytes(StandardCharsets.UTF_8));
        
        // Set restrictive permissions (Unix-like)
        tempFile.setReadable(false, false);
        tempFile.setReadable(true, true);
        tempFile.setWritable(false, false);
        tempFile.setWritable(true, true);
        
        // Atomic rename
        Files.move(tempFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        System.out.println("✓ users.json updated");
    }
    
    /**
     * Creates sample initial users (for first-time setup).
     * Call this manually once to populate users.json with examples.
     */
    public static void createInitialUsers() throws Exception {
        System.out.println("\n--- Creating Initial Users ---");
        
        // Clear existing users
        users.clear();
        
        // Sample users (IDs should match actual DB records)
        createUser("STUDENT", 1, "alice", "password123");
        createUser("STUDENT", 2, "bob", "password123");
        createUser("FACULTY", 1, "drsmith", "faculty123");
        createUser("ASSOCIATION_MEMBER", 1, "ashok", "member123");
        
        System.out.println("\n✓ Initial users created successfully");
        System.out.println("⚠️  Default passwords should be changed by users on first login");
        System.out.println("ℹ️  users.json is now protected and contains hashed passwords\n");
    }
    
    /**
     * Returns display name for a user based on their role and ID.
     */
    private static String getDisplayName(String type, int id) {
        try {
            switch (type) {
                case "STUDENT":
                    Student student = new StudentDao().findById(id);
                    return student != null ? student.getSName() : "Student#" + id;
                case "FACULTY":
                    Faculty faculty = new FacultyDao().findById(id);
                    return faculty != null ? faculty.getFName() : "Faculty#" + id;
                case "ASSOCIATION_MEMBER":
                    AssociationMember member = new AssociationMemberDao().findById(id);
                    return member != null ? "Member#" + id : "Member#" + id;
                default:
                    return "User#" + id;
            }
        } catch (Exception e) {
            return "User#" + id;
        }
    }
    
    /**
     * Simple JSON array parser (lightweight, no external lib needed).
     */
    private static List<Map<String, Object>> parseJsonArray(String json) {
        List<Map<String, Object>> result = new ArrayList<>();
        json = json.trim();
        
        if (!json.startsWith("[") || !json.endsWith("]")) {
            return result;
        }
        
        int depth = 0;
        StringBuilder obj = new StringBuilder();
        
        for (int i = 1; i < json.length() - 1; i++) {
            char c = json.charAt(i);
            if (c == '{') depth++;
            if (c == '}') depth--;
            
            obj.append(c);
            
            if (depth == 0 && c == '}') {
                Map<String, Object> map = parseJsonObject(obj.toString());
                if (!map.isEmpty()) {
                    result.add(map);
                }
                obj = new StringBuilder();
            }
        }
        
        return result;
    }
    
    /**
     * Simple JSON object parser.
     */
    private static Map<String, Object> parseJsonObject(String json) {
        Map<String, Object> result = new LinkedHashMap<>();
        json = json.trim().replaceAll("[{}]", "").replaceAll(",\\s*$", "");
        
        String[] pairs = json.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        for (String pair : pairs) {
            String[] kv = pair.split(":", 2);
            if (kv.length == 2) {
                String key = kv[0].trim().replaceAll("\"", "");
                String value = kv[1].trim().replaceAll("\"", "");
                
                try {
                    if (value.matches("\\d+")) {
                        result.put(key, Integer.parseInt(value));
                    } else {
                        result.put(key, value);
                    }
                } catch (NumberFormatException e) {
                    result.put(key, value);
                }
            }
        }
        
        return result;
    }
    
    /**
     * Converts users to JSON array string.
     */
    private static String toJsonArray(List<Map<String, Object>> userList) {
        StringBuilder sb = new StringBuilder("[\n");
        
        for (int i = 0; i < userList.size(); i++) {
            Map<String, Object> user = userList.get(i);
            sb.append("  {\n");
            sb.append("    \"type\": \"").append(user.get("type")).append("\",\n");
            sb.append("    \"id\": ").append(user.get("id")).append(",\n");
            sb.append("    \"username\": \"").append(user.get("username")).append("\",\n");
            sb.append("    \"passwordHash\": \"").append(user.get("passwordHash")).append("\"\n");
            sb.append("  }");
            
            if (i < userList.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }
        
        sb.append("]\n");
        return sb.toString();
    }
    
    /**
     * Returns list of all users (for admin purposes).
     */
    public static List<Map<String, Object>> getAllUsers() {
        return new ArrayList<>(users);
    }
    
    /**
     * Checks if users.json exists.
     */
    public static boolean usersFileExists() {
        return new File(USERS_FILE).exists();
    }
}
