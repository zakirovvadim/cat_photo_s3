package ru.vadim.cat_photo_s3.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.relational.core.dialect.AnsiDialect;
import org.springframework.data.relational.core.dialect.Dialect;

/**
 * Простой диалект для SQLite, чтобы Spring Data JDBC не падал.
 * AnsiDialect работает как "generic SQL", и для простых CRUD (insert/select/update/delete)
 * и автоинкремента id через INTEGER PRIMARY KEY AUTOINCREMENT этого обычно хватает.
 */
@Configuration
public class SqliteConfig {

    @Bean("jdbcDialect")
    @Primary
    public Dialect jdbcDialect() {
        return new SqliteDialect();
    }
}
