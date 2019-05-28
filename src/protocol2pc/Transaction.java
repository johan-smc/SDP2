package protocol2pc;

import server.Resource;
import server.ServerReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Transaction implements Serializable {

    public Transaction() {
        operations = new ArrayList<>();
        this.desition = DESISION.IN_PROGRESS;
        this.type = Operation.TYPE.READ;
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

    public enum DESISION{
        COMMIT,
        ABORT,
        IN_PROGRESS,
        NOT_EXIST
    }
    private String id;
    private List<Operation> operations;
    private DESISION desition;
    private Operation.TYPE type;
    public Transaction(String transactionId){
        this();
        id = transactionId;
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

    public DESISION getDesition() {
        return desition;
    }

    public void setDesition(DESISION desition) {
        this.desition = desition;
    }

}
