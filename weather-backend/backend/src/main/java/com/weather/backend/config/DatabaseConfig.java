package com.weather.backend.config;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DatabaseConfig {

    @Primary
    @Bean
    public DataSource dataSource() {
        // Check if running in Docker by looking for Docker-specific environment variables
        boolean isDocker = System.getenv("DOCKER_CONTAINER") != null ||
                          System.getenv("SPRING_DATASOURCE_URL") != null ||
                          "mysql".equals(System.getenv("DB_HOST"));

        // Use environment variable or default based on environment
        String host = System.getenv("DB_HOST") != null ? System.getenv("DB_HOST") :
                     (isDocker ? "mysql" : "localhost");
        String url = "jdbc:mysql://" + host + ":3306/";
        String username = "root";
        String password = "root";

        // Read username and password from environment variables for consistency
        String envUsername = System.getenv("SPRING_DATASOURCE_USERNAME");
        String envPassword = System.getenv("SPRING_DATASOURCE_PASSWORD");
        if (envUsername != null && !envUsername.isEmpty()) {
            username = envUsername;
        }
        if (envPassword != null && !envPassword.isEmpty()) {
            password = envPassword;
        }

        try (Connection conn = DriverManager.getConnection(url, username, password);
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE DATABASE IF NOT EXISTS weatherdb");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create database", e);
        }

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://" + host + ":3306/weatherdb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");

        // Add connection pool settings for better reliability
        dataSource.setMaximumPoolSize(10);
        dataSource.setMinimumIdle(5);
        dataSource.setConnectionTimeout(30000); // 30 seconds
        dataSource.setIdleTimeout(600000); // 10 minutes
        dataSource.setMaxLifetime(1800000); // 30 minutes

        return dataSource;
    }
}
