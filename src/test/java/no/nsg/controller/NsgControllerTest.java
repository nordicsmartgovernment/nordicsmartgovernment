package no.nsg.controller;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
public class NsgControllerTest {

    private NsgController nsgController = new NsgController();

    @Test
    public void happyDay()
    {
        Assert.assertTrue(true);
    }
}
