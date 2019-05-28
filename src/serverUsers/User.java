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
        System.out.println(".. " + this.password + " " + password);
        return this.password.equals(password);
    }

    public User(String id, String password, double balance, boolean isAdmin) {
        super(id);
        if( password.equals("-") )
        {
            password = "";
        }
        this.password = password;
        this.balance = balance;
        this.isAdmin = isAdmin;
    }
    public boolean isEmptyPassword()
    {
        return this.password.equals("");
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getPassword() {
    	return this.password;
    }

	public double getBalance() {
		return balance;
	}
}
