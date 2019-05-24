package no.nsg.repository.transaction;

import no.nsg.repository.ConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class TransactionManager {

    @Autowired
    private ConnectionManager connectionManager;

}
