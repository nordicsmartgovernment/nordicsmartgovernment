package no.nsg.spring;

import org.springframework.security.crypto.password.PasswordEncoder;


public class NullPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence var1) {
        return null;
    }

    @Override
    public boolean matches(CharSequence var1, String var2) {
        return true;
    }

}
