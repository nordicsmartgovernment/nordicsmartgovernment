package no.nsg.utils;

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import no.nsg.Application;
import no.nsg.repository.ConnectionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {Application.class})
@WebAppConfiguration
@ContextConfiguration(initializers = {EmbeddedPostgresSetup.Initializer.class})
public abstract class EmbeddedPostgresSetup {
    private final static Logger LOGGER = LoggerFactory.getLogger(EmbeddedPostgresSetup.class);

    private static final String POSTGRES_DB_NAME = "postgres";
    private static final String POSTGRES_USER = "postgres";
    private static final String POSTGRES_PASSWORD = "postgres";

    @Autowired
    private ConnectionManager connectionManager;

    private static EmbeddedPostgres embeddedPostgres;

    @BeforeEach
    public void updatePostgresDbUrl() {
        connectionManager.updateDbUrl(embeddedPostgres.getJdbcUrl(POSTGRES_USER, POSTGRES_DB_NAME));
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            try {
                embeddedPostgres = EmbeddedPostgres.start();
            } catch (IOException e) {
                LOGGER.error("Failed starting embedded postgres database");
                e.printStackTrace();
            }

            TestPropertyValues.of(
                    "spring.datasource.url=" + embeddedPostgres.getJdbcUrl(POSTGRES_USER, POSTGRES_DB_NAME),
                    "spring.datasource.username=" + POSTGRES_USER,
                    "spring.datasource.password=" + POSTGRES_PASSWORD,
                    "postgres.nsg.db_url=" + embeddedPostgres.getJdbcUrl(POSTGRES_USER, POSTGRES_DB_NAME),
                    "postgres.nsg.dbo_user=" + POSTGRES_USER,
                    "postgres.nsg.dbo_password=" + POSTGRES_PASSWORD,
                    "postgres.nsg.user=" + POSTGRES_USER,
                    "postgres.nsg.password=" + POSTGRES_PASSWORD
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
