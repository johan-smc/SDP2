package serverUsers;

import intefaces.IUsers;
import protocol2pc.Transaction;
import server.Server;
import server.ServerReference;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;

public class ServerUser extends Server implements IUsers {

    private Map<String, User> resources;

    public static void main(String[] args) throws RemoteException {
        ServerReference coordinator = new ServerReference();
        ServerReference participant= new ServerReference();
        ServerReference server_re= new ServerReference();
        ServerUser server = new ServerUser(coordinator, coordinator, participant);

        Registry registry = LocateRegistry.createRegistry(server_re.getPort());
        try {
            registry.bind(Server.NAME_SERVICE,server);
            System.err.println("server ready");
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        }

        addClientsFromFile();
    }

    public ServerUser(ServerReference coordinator, ServerReference participant, ServerReference server)
    {
        super(coordinator, participant, server);
        this.resources = new HashMap<>();
        // TODO - error in rources
    }

    private static void addClientsFromFile() {


    }

    @Override
    public Map<String, User> getUsers() throws RemoteException {
        return this.resources;
    }

    @Override
    public User login(String id, String password) throws RemoteException {
        if( !this.resources.containsKey(id) || !this.resources.get(id).login(password) )
        {
            return null;
        }
        return this.resources.get(id);
    }

    @Override
    public String openTransaction(Transaction transaction) throws RemoteException {
        return this.coordinator.openTransaction(transaction);
    }
}
