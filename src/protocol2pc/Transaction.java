package protocol2pc;

import server.Resource;
import server.ServerReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Transaction implements Serializable {


    private String id;
    private List<Operation> operations;
    private DECISION desition;
    private Operation.TYPE type;
    private TYPE_TRANSACTION typeTransaction;
    public Transaction() {
        operations = new ArrayList<>();
        this.desition = DECISION.IN_PROGRESS;
        this.type = Operation.TYPE.READ;
        this.typeTransaction = TYPE_TRANSACTION.NORMAL;

    }
    public Transaction(String transactionId){
        this();
        id = transactionId;
    }
    public Transaction(String transactionId, TYPE_TRANSACTION typeTransaction){
        this(transactionId);
        this.typeTransaction = typeTransaction;
    }

    public void putOperation(Operation operation) {
        this.operations.add(operation);
        if( this.operations.equals(Operation.TYPE.WRITE) )
        {
            this.type = Operation.TYPE.WRITE;
        }
    }

    public Operation.TYPE getType() {
        return type;
    }

    public void setType(Operation.TYPE type) {
        this.type = type;
    }

    public TYPE_TRANSACTION getTypeTransaction() {
        return typeTransaction;
    }

    public void setTypeTransaction(TYPE_TRANSACTION typeTransaction) {
        this.typeTransaction = typeTransaction;
    }

    public enum TYPE_TRANSACTION{
        NORMAL,
        PURCHASE
    }

    public enum DECISION{
        COMMIT,
        ABORT,
        IN_PROGRESS,
        NOT_EXIST
    }

    private String generateId(){
        return null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public void setOperations(List<Operation> operations) {
        this.operations = operations;
    }

    public DECISION getDesition() {
        return desition;
    }

    public void setDesition(DECISION desition) {
        this.desition = desition;
    }

}
