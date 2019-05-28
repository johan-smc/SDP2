package serverUsers;

import intefaces.IUsers;
import protocol2pc.Coordinator;
import protocol2pc.Participant;
import protocol2pc.Transaction;
import server.Resource;
import server.Server;
import server.ServerReference;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;
import java.util.Scanner;

public class ServerUser extends Server implements IUsers {



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
    private  void saveUsers() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(this.getFileName()));
            for (Map.Entry<String, Resource> entry: this.resources.entrySet()) {
                User u = (User) entry.getValue();
                writer.write(u.getStringToSave() + '\n');
            }
            writer.close();

        } catch (FileNotFoundException e) {
            System.out.println("[ERROR] Archivo: "+ e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public Map<String, Resource> getUsers() throws RemoteException {
        return this.resources;
    }

    @Override
    public User login(String id, String password) throws RemoteException {
        System.out.println("Login " + id);
        System.out.println("Password: "+  password);
        System.out.println(this.resources.containsKey(id));
        User user = (User) this.resources.get(id);
        if( !this.resources.containsKey(id) || !user.login(password) )
        {
            return null;
        }
        return (User) this.resources.get(id);
    }

    @Override
    public User changePassword(String id, String password) throws RemoteException {
        System.out.println("Change password: " + id);

        User user = (User) this.resources.get(id);
        if( !this.resources.containsKey(id) )
        {
            return null;
        }
        user.setPassword(password);
        this.saveUsers();
        return user;
    }

    @Override
    public String openTransaction(Transaction transaction) throws RemoteException {
        return this.coordinator.openTransaction(transaction);
    }

    @Override
    public ServerReference getReference() {
        return this.participant.getMyServer();
    }

    @Override
    public Transaction.DECISION getDecision(String transaction) throws RemoteException {
        return this.coordinator.getDecision(transaction);
    }
}
