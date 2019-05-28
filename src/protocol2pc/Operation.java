package protocol2pc;

import server.Resource;
import server.ServerReference;

import java.io.Serializable;

public class Operation implements Serializable {

    public enum TYPE{
        READ,
        WRITE;


    }
    private TYPE type;
    private Object value;
    private String resource;
    private ServerReference server;

    public Operation(TYPE type, Object value, String resource, ServerReference server) {
        this.type = type;
        this.value = value;
        this.resource = resource;
        this.server = server;
    }

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public ServerReference getServer() {
        return server;
    }

    public void setServer(ServerReference server) {
        this.server = server;
    }
}
