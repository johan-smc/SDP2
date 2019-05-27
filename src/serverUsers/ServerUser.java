package serverUsers;

import intefaces.IUsers;
import protocol2pc.Coordinator;
import protocol2pc.Participant;
import protocol2pc.Transaction;
import server.Server;
import server.ServerReference;
import serverCatalog.Product;

import java.io.File;
import java.io.FileNotFoundException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ServerUser extends Server implements IUsers {

    private Map<String, User> resources;

    public static void main(String[] args) throws RemoteException {
        int port = Integer.valueOf(args[0]);
        System.err.println("Port: " + port);
        String ip = "ip";
        System.err.println("ip: " + ip);
        ServerReference coordinator = new ServerReference(
            ip,port, Coordinator.NAME_SERVICE
        );
        ServerReference participant= new ServerReference(
                ip,port, Participant.NAME_SERVICE
        );
        ServerReference server_re= new ServerReference(
                ip,port, Server.NAME_SERVICE
        );
        Registry registry = LocateRegistry.createRegistry(server_re.getPort());
        ServerUser server = new ServerUser(coordinator, participant, server_re, registry);


        try {
            registry.bind(Server.NAME_SERVICE,server);
            System.err.println("server ready");
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        }

        if( args.length > 1 )
        {
            server.setFileName(args[1]);
        }
        addClientsFromFile(server);
    }

    public ServerUser(ServerReference coordinator, ServerReference participant, ServerReference server, Registry registry) throws RemoteException {
        super(coordinator, participant, server, registry);
        this.resources = new HashMap<>();
        // TODO - error in rources
    }

    private static void addClientsFromFile(ServerUser server ) {
        try {
            Scanner sc = new Scanner(new File(server.getFileName()));
            String id;
            String password;
            double balance;
            boolean isAdmin;
            while (sc.hasNext()){
                id = sc.next();
                password = sc.next();
                balance = Double.valueOf(sc.next());
                isAdmin = Boolean.valueOf(sc.next());
                server.addResource(new User(id, password, balance, isAdmin));
                System.out.println("Usuario agregado");
            }

        } catch (FileNotFoundException e) {
            System.out.println("[ERROR] Archivo: "+ e.getMessage());
        }

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
