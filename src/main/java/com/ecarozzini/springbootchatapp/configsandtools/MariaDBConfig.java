package com.ecarozzini.springbootchatapp.configsandtools;

import lombok.SneakyThrows;
import org.mariadb.jdbc.MariaDbDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
public class MariaDBConfig {
    @SneakyThrows(SQLException.class)
    @Bean
    public DataSource dataSource() {
        MariaDbDataSource dataSource = new MariaDbDataSource();
        dataSource.setUser("test");
        dataSource.setPassword("test123");
        dataSource.setUrl("jdbc:mariadb://localhost:3306/chat");
        return dataSource;
    }
}
