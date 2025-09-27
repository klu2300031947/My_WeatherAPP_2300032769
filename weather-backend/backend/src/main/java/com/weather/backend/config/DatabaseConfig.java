package com.weather.backend.config;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DatabaseConfig {

    @Primary
    @Bean
    public DataSource dataSource() {
        // Get DB credentials from environment or fallback to defaults
        String host = System.getenv("DB_HOST") != null ? System.getenv("DB_HOST") : "localhost";
        String port = System.getenv("DB_PORT") != null ? System.getenv("DB_PORT") : "3306";
        String database = System.getenv("DB_NAME") != null ? System.getenv("DB_NAME") : "weatherdb";
        String username = System.getenv("SPRING_DATASOURCE_USERNAME") != null ?
                System.getenv("SPRING_DATASOURCE_USERNAME") : "weatheruser";
        String password = System.getenv("SPRING_DATASOURCE_PASSWORD") != null ?
                System.getenv("SPRING_DATASOURCE_PASSWORD") : "weatherpass";

        String jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + database +
                         "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");

        // Connection pool tuning (optional)
        dataSource.setMaximumPoolSize(10);
        dataSource.setMinimumIdle(2);
        dataSource.setConnectionTimeout(30000); // 30 sec
        dataSource.setIdleTimeout(600000);      // 10 min
        dataSource.setMaxLifetime(1800000);     // 30 min

        return dataSource;
    }
}
