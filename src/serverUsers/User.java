package serverUsers;

import protocol2pc.Transaction;
import server.Resource;

public class User extends Resource {
    private String password;
    private double balance;

    @Override
    public void doOperations(Transaction transaction) {
        // TODO
    }

    public boolean login(String password){
        return this.password.equals(password);
    }

}
