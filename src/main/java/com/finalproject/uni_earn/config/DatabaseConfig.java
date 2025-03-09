package com.finalproject.uni_earn.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Configuration
public class DatabaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

    @Value("${spring.initial-datasource.url}")
    private String initialUrl;

    @Value("${spring.initial-datasource.username}")
    private String initialUsername;

    @Value("${spring.initial-datasource.password}")
    private String initialPassword;

    @Value("${spring.datasource.url}")
    private String appUrl;

    @Value("${spring.datasource.username}")
    private String appUsername;

    @Value("${spring.datasource.password}")
    private String appPassword;

    @Bean
    @Primary
    public DataSource dataSource() {
        logger.info("Starting database initialization with initial URL: {}", initialUrl);

        // Step 1: Connect to 'postgres' and create 'uniearn' if it doesn’t exist
        DriverManagerDataSource initialDataSource = new DriverManagerDataSource();
        initialDataSource.setDriverClassName("org.postgresql.Driver");
        initialDataSource.setUrl(initialUrl);
        initialDataSource.setUsername(initialUsername);
        initialDataSource.setPassword(initialPassword);

        try (Connection conn = initialDataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            logger.info("Connected to 'postgres' database successfully");
            ResultSet rs = stmt.executeQuery("SELECT 1 FROM pg_database WHERE datname = 'uniearn'");
            if (!rs.next()) {
                stmt.execute("CREATE DATABASE uniearn");
                logger.info("Database 'uniearn' created successfully");
            } else {
                logger.info("Database 'uniearn' already exists");
            }
        } catch (SQLException e) {
            logger.error("Error creating database: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize database", e);
        }

        // Step 2: Return the DataSource for 'uniearn'
        DriverManagerDataSource appDataSource = new DriverManagerDataSource();
        appDataSource.setDriverClassName("org.postgresql.Driver");
        appDataSource.setUrl(appUrl);
        appDataSource.setUsername(appUsername);
        appDataSource.setPassword(appPassword);
        logger.info("Returning DataSource for URL: {}", appUrl);
        return appDataSource;
    }
}
