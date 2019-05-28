package serverCatalog;

import intefaces.ICatalog;
import protocol2pc.Coordinator;
import protocol2pc.Participant;
import protocol2pc.Transaction;
import server.Resource;
import server.Server;
import server.ServerReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;
import java.util.Scanner;

public class ServerCatalog extends Server implements ICatalog {





    @Override
    public Map<String, Resource> getCatalog() throws RemoteException {
        return this.resources;
    }

    @Override
    public String openTransaction(Transaction transaction) throws RemoteException {
        return this.coordinator.openTransaction(transaction);
    }

    @Override
    public ServerReference getReference() throws RemoteException {
        return this.participant.getMyServer();
    }

    @Override
    public Transaction.DECISION getDecision(String transaction) throws RemoteException {
        return this.coordinator.getDecision(transaction);
    }


    public static void main(String[] args) throws RemoteException {
        int port = Integer.valueOf(args[0]);
        System.err.println("Port: " + port);
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        assert inetAddress != null;
        String ip = inetAddress.getHostAddress();
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

        ServerCatalog server = new ServerCatalog(coordinator, participant, server_re,registry );


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
        addProductsFromFile(server);

    }

    public ServerCatalog(ServerReference coordinator, ServerReference participant, ServerReference server, Registry registry) throws RemoteException {
        super(coordinator, participant, server, registry);
    }

    private static void addProductsFromFile(ServerCatalog server ) {
        try {
            Scanner sc = new Scanner(new File(server.getFileName()));
            String id;
            int total;
            double cost;
            while (sc.hasNext()){
                id = sc.next();
                total = Integer.valueOf(sc.next());
                cost = Double.valueOf(sc.next());
                server.addResource(new Product(total, cost, id));
                System.out.println("Producto agregado");
            }

        } catch (FileNotFoundException e) {
            System.out.println("[ERROR] Archivo: "+ e.getMessage());
        }

    }


}
