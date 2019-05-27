package server;

import protocol2pc.Operation;
import protocol2pc.Transaction;

import java.util.*;

public abstract class Resource {

    private Map<String, Transaction> transactions;
    private Set<String> activeTransactions;
    private String id;

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
        Iterator<String> it = activeTransactions.iterator();
        if( !it.hasNext() ||
                (this.transactions.get(it.next()).getType().equals( Operation.TYPE.READ) && this.transactions.get(transactionId).getType().equals(Operation.TYPE.READ) ) )
        {
            this.activeTransactions.add(transactionId);
            return true;
        }
        return false;
    }



    public synchronized void doCommit(String transactionId) {
        if( this.transactions.containsKey(transactionId) && this.activeTransactions.contains(transactionId) ) {
            doOperations(this.transactions.get(transactionId));
            this.transactions.remove(transactionId);
            this.activeTransactions.remove(transactionId);

        }
    }

     public abstract void doOperations(Transaction transaction);




    public synchronized void doAbort(String transactionId) {
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
