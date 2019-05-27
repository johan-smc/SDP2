package serverUsers;

import protocol2pc.Transaction;
import server.Resource;

public class User extends Resource {
    private String password;
    private double balance;
    private boolean isAdmin;

    @Override
    public void doOperations(Transaction transaction) {
        // TODO
        System.out.println("--- NOT IMPLEMENTED");
    }

    boolean login(String password){
        return this.password.equals(password);
    }

    public User(String id, String password, double balance, boolean isAdmin) {
        super(id);
        if( password.equals("-") )
        {
            password = null;
        }
        this.password = password;
        this.balance = balance;
        this.isAdmin = isAdmin;
    }
}
