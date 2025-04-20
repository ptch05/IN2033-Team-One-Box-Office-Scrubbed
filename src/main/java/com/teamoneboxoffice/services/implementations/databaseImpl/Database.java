package com.teamoneboxoffice.services.implementations.databaseImpl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class Database {
    private static final String PROPERTIES_FILE = "config.properties";

    private String jdbcUrl;
    private String username;
    private String password;
    private String driverClass;
    private int connectionTimeout;
    private boolean useSSL;
    private boolean allowPublicKeyRetrieval;

    private Connection connection;

    public Database() {
        loadProperties();

        try {
            // Load the JDBC driver
            Class.forName(driverClass);
            System.out.println("JDBC driver loaded successfully: " + driverClass);

            // Establish connection
            connect();
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
        }
    }


    private void loadProperties() {
        Properties props = new Properties();

        try (InputStream input = new FileInputStream(PROPERTIES_FILE)) {
            props.load(input);

            jdbcUrl = props.getProperty("jdbc.url");
            username = props.getProperty("jdbc.username");
            password = props.getProperty("jdbc.password");
            driverClass = props.getProperty("jdbc.driver", "com.mysql.cj.jdbc.Driver");
            connectionTimeout = Integer.parseInt(props.getProperty("jdbc.connectionTimeout", "30000"));
            useSSL = Boolean.parseBoolean(props.getProperty("jdbc.useSSL", "false"));
            allowPublicKeyRetrieval = Boolean.parseBoolean(props.getProperty("jdbc.allowPublicKeyRetrieval", "true"));

            System.out.println("Database properties loaded successfully");
            System.out.println("JDBC URL: " + jdbcUrl);
            System.out.println("Username: " + username);
            System.out.println("Driver: " + driverClass);
        } catch (IOException e) {
            System.err.println("Error loading properties file: " + e.getMessage());

            // Set default values

            //There were default values here in the original code, but I removed them for here
            jdbcUrl = "";
            username = "";
            password = "";
            driverClass = "com.mysql.cj.jdbc.Driver";
            connectionTimeout = 30000;
            useSSL = false;
            allowPublicKeyRetrieval = true;

            System.out.println("Using default properties instead");
        }
    }


    public Connection connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            Properties connectionProps = new Properties();
            connectionProps.setProperty("user", username);
            connectionProps.setProperty("password", password);
            connectionProps.setProperty("connectTimeout", String.valueOf(connectionTimeout));
            connectionProps.setProperty("useSSL", String.valueOf(useSSL));
            connectionProps.setProperty("allowPublicKeyRetrieval", String.valueOf(allowPublicKeyRetrieval));

            connection = DriverManager.getConnection(jdbcUrl, username, password);
            System.out.println("Database connection established successfully.");
        }
        return connection;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connect();
        }
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }


    public static void main(String[] args) {
        Database db = new Database();
        try {
            Connection conn = db.getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("Connected to the database successfully!");
                System.out.println("Database product: " + conn.getMetaData().getDatabaseProductName());
                System.out.println("Database version: " + conn.getMetaData().getDatabaseProductVersion());
                System.out.println("JDBC driver: " + conn.getMetaData().getDriverName());
                System.out.println("JDBC driver version: " + conn.getMetaData().getDriverVersion());
            }
        } catch (SQLException e) {
            System.err.println("Failed to connect: " + e.getMessage());
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
    }
}