package no.nsg.spring;

import java.security.Principal;


public class TestPrincipal implements Principal {

    private String name;


    public TestPrincipal(final String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

}
