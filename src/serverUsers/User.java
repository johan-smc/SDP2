package serverUsers;

import protocol2pc.Operation;
import protocol2pc.Transaction;
import server.Resource;

public class User extends Resource {
    private String password;
    private double balance;
    private boolean isAdmin;

    public boolean isAdmin() {
        return isAdmin;
    }

    public String getPassword() {
        return password;
    }

    public double getBalance() {
        return balance;
    }

    @Override
    public void doOperations(Transaction transaction) {
        // TODO
        for (Operation o:
             transaction.getOperations()) {
            if( o.getResource().equals(this.id) && o.getType().equals(Operation.TYPE.WRITE)){
                balance += (Double)o.getValue();
            }
        }
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

    @Override
    public String toString() {
        return "id: "+ this.id+ " password: " + this.password + " balance: " +this.balance + " isAdmin: " + this.isAdmin ;
    }

    public String getStringToSave() {
        String tempPass = password;
        if(tempPass == "") tempPass = "-";
        return (id + " " + tempPass + " " + balance + " " + isAdmin);
    }

    @Override
    protected boolean validate(Transaction transaction) {
        double temporalBalance = balance;
        for (Operation o:
                transaction.getOperations()) {
            if( o.getResource().equals(this.id) && o.getType().equals(Operation.TYPE.WRITE)){
                temporalBalance+= (double)o.getValue();
            }
        }
        return temporalBalance >= 0.0;
    }
}
