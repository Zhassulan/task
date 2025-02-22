package com.home.task.config;

import com.home.task.entity.TaskEntity;
import com.home.task.repository.TasksJpaRepository;
import com.opentable.db.postgres.embedded.FlywayPreparer;
import com.opentable.db.postgres.embedded.PreparedDbProvider;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
@EnableJpaRepositories(basePackageClasses = TasksJpaRepository.class)
@EntityScan(basePackageClasses = TaskEntity.class)
public class EmbeddedPostgresWithFlywayConfiguration {

    @Bean
    public DataSource dataSource() throws SQLException {
        return PreparedDbProvider
                .forPreparer(FlywayPreparer.forClasspathLocation("db/migration"))
                .createDataSource();
    }
}
