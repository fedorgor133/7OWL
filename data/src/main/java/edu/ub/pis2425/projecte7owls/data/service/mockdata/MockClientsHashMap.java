package edu.ub.pis2425.projecte7owls.data.service.mockdata;

import java.util.HashMap;

import edu.ub.pis2425.projecte7owls.domain.entities.User;

public class MockClientsHashMap extends HashMap<String, User> {
    /**
     * Contructor buit
     */
    public MockClientsHashMap() {
        super();
        mockInit();
    }

    /**
     * Inicialitza el mockdata
     */
    private void mockInit() {
        put("admin", new User("admin", "admin", "admin"));
        put("aduhax16@gmail.com", new User("aduhax16@gmail.com", "aduhax", "16aduhax16"));
    }
}
