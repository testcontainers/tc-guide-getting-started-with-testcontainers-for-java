package com.testcontainers.demo;

import java.sql.Connection;
import java.sql.DriverManager;

class DBConnectionProvider {
    private final String url;
    private final String username;
    private final String password;

    public DBConnectionProvider(String url,
                                String username,
                                String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    Connection getConnection() {
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
