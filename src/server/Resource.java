package server;

import protocol2pc.Operation;
import protocol2pc.Transaction;

import java.io.Serializable;
import java.util.*;

public abstract class Resource implements Serializable {

    private Map<String, Transaction> transactions;
    private Set<String> activeTransactions;
    protected String id;

    public Resource() {
        this.transactions = new HashMap<>();
        this.activeTransactions = new HashSet<>();
    }

    public Resource(String id) {
        this();
        this.id = id;

    }

    public  synchronized  void doOperation(String transactionId, Operation operation){
        if( !this.transactions.containsKey(transactionId) )
        {
            this.transactions.put(transactionId, new Transaction(transactionId));

        }
        this.transactions.get(transactionId).putOperation(operation);
    }


    public  synchronized boolean canCommit(String transactionId)
    {
        System.out.println("Can commit? ");
        Iterator<String> it = activeTransactions.iterator();
        boolean canCom = !it.hasNext() ||
                    (this.transactions.get(it.next()).getType().equals( Operation.TYPE.READ) &&
                        this.transactions.get(transactionId).getType().equals(Operation.TYPE.READ)

                    );
        canCom &= this.validate(this.transactions.get(transactionId));

        if(canCom)
        {

            this.activeTransactions.add(transactionId);
            return true;
        }
        return false;
    }

    protected abstract boolean validate(Transaction transaction);


    public synchronized void doCommit(String transactionId) {
        System.out.println("Resource " + this.getId()+" do commit");
        if( this.transactions.containsKey(transactionId) && this.activeTransactions.contains(transactionId) ) {
            System.out.println("I'm going to do the operations");
            doOperations(this.transactions.get(transactionId));
            this.transactions.remove(transactionId);
            this.activeTransactions.remove(transactionId);

        }
    }

     public abstract void doOperations(Transaction transaction);




    public synchronized void doAbort(String transactionId) {
        System.out.println("Resource " + this.getId()+" do abort");
        if( this.transactions.containsKey(transactionId) && this.activeTransactions.contains(transactionId) ) {

            this.transactions.remove(transactionId);
            this.activeTransactions.remove(transactionId);

        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
