package serverCatalog;

import intefaces.ICatalog;
import protocol2pc.Coordinator;
import protocol2pc.Transaction;
import server.Server;
import server.ServerReference;
import serverUsers.ServerUser;

import java.io.File;
import java.io.FileNotFoundException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ServerCatalog extends Server implements ICatalog {

    private Map<String, Product> resources;



    @Override
    public Map<String, Product> getCatalog() throws RemoteException {
        return this.resources;
    }

    @Override
    public String openTransaction(Transaction transaction) throws RemoteException {
        return this.coordinator.openTransaction(transaction);
    }

    public static void main(String[] args) throws RemoteException {
        ServerReference coordinator = new ServerReference();
        ServerReference participant= new ServerReference();
        ServerReference server_re= new ServerReference();
        ServerUser server = new ServerUser(coordinator, participant, server_re);

        Registry registry = LocateRegistry.createRegistry(server_re.getPort());
        try {
            registry.bind(Server.NAME_SERVICE,server);
            System.err.println("server ready");
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        }

        addProductsFromFile(server.getFileName());
    }

    public ServerCatalog(ServerReference coordinator, ServerReference participant, ServerReference server)
    {
        super(coordinator, participant, server);
        this.resources = new HashMap<>();
        // TODO - error in rources
    }

    private static void addProductsFromFile(String fileName) {
        try {
            Scanner sc = new Scanner(new File(fileName));
            String time;
            String name;
            while (sc.hasNext()){
                time = sc.next();
                name = sc.next();


            }

        } catch (FileNotFoundException e) {
            System.out.println("[ERROR] Archivo: "+ e.getMessage());
        }

    }
}
