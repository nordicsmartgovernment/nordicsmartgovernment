package no.nsg.controller;

import no.nsg.utils.EmbeddedPostgresSetup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@Tag("ServiceTest")
public class HealthControllerTest extends EmbeddedPostgresSetup {
    private static Logger LOGGER = LoggerFactory.getLogger(HealthControllerTest.class);

    @Autowired
    HealthController healthController;

    @Test
    public void happyDay()
    {
        Assertions.assertTrue(true);
    }

    @Test
    public void getReadyTest() {
        ResponseEntity<Void> response = healthController.getReady();
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void getPingTest() {
        ResponseEntity<String> response = healthController.getPing();
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("pong", response.getBody());
    }

}
