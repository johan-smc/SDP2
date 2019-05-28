package server;

import java.io.Serializable;

public class ServerReference implements Serializable, Comparable {
    private String ip;
    private int port;
    private String serverNameRMI;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getServerNameRMI() {
        return serverNameRMI;
    }

    public void setServerNameRMI(String serverNameRMI) {
        this.serverNameRMI = serverNameRMI;
    }

    public ServerReference(String ip, int port, String serverNameRMI) {
        this.ip = ip;
        this.port = port;
        this.serverNameRMI = serverNameRMI;
    }

    private String getUnion(){
        return this.ip+this.port+this.serverNameRMI;
    }

    @Override
    public int compareTo(Object o) {

        ServerReference other = (ServerReference)o;
        return this.getUnion().compareTo(other.getUnion());
    }

    @Override
    public boolean equals(Object obj) {
        ServerReference other = (ServerReference)obj;
        return this.ip.equals(other.ip) && this.port == other.port && this.serverNameRMI.equals(other.serverNameRMI);
    }
}
