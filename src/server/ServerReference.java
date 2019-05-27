package server;

public class ServerReference {
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
}
