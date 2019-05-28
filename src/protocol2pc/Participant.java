package protocol2pc;

import intefaces.ICoordinator;
import intefaces.IParticipant;
import server.Resource;
import server.ServerReference;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Participant extends UnicastRemoteObject implements IParticipant  {

    public static final String NAME_SERVICE = "participant";
    private Map<String, Resource> resources;
    private ServerReference myServer;
    private Map<String, ICoordinator> transactionsReferences;

    public ServerReference getMyServer() {
        return myServer;
    }

    public Participant(Map<String, Resource> resources, ServerReference myServer) throws RemoteException {
        super();

        this.resources = resources;
        this.myServer = myServer;
        this.transactionsReferences = new HashMap<>();
    }

    @Override
    public boolean canCommit(Transaction transaction) throws RemoteException {
        Set<String> resourceSet = new HashSet<>();
        for ( Operation operation:
             transaction.getOperations()) {
            if( operation.getServer().equals(myServer) )
            {
                Resource resource =this.resources.get(operation.getResource());
                if( resource == null  )
                {
                    this.doAbort(transaction);
                    return false;
                }
                resource.doOperation(transaction.getId(), operation);
                resourceSet.add(resource.getId());
            }
        }
        for (String rId:
             resourceSet) {
            if( !this.resources.get(rId).canCommit(transaction.getId()) )
            {
                this.doAbort(transaction);
                return false;
            }
        }
        return true;
    }

    @Override
    public void doCommit(Transaction transaction) throws RemoteException {

        System.out.println("Participant Do commit");
        for ( Operation operation:
                transaction.getOperations()) {

            if( operation.getServer().equals(myServer) )
            {
                Resource resource =this.resources.get(operation.getResource());
                System.out.println("I'm going to call commit to: " + resource);
                if( resource != null )
                {
                    resource.doCommit(transaction.getId());
                }

            }
        }
        System.out.println("Inform to coordinator");
        this.transactionsReferences.get(transaction.getId()).haveCommited(transaction, this.myServer);
        System.out.println("End Participant Do commit");
    }

    @Override
    public void doAbort(Transaction transaction) throws RemoteException {
        System.out.println("Participant Do Abort");
        for ( Operation operation:
                transaction.getOperations()) {
            if (operation.getServer().equals(myServer)) {
                Resource resource = this.resources.get(operation.getResource());
                System.out.println("I'm going to call abort to: " + resource);
                if (resource != null) {
                    resource.doAbort(transaction.getId());
                }

            }
        }
        this.transactionsReferences.get(transaction.getId()).hi();
        System.out.println("Inform to coordinator");
        this.transactionsReferences.get(transaction.getId()).haveCommited(transaction, this.myServer);
        System.out.println("Participant Do Commit");
    }

    @Override
    public boolean join(Transaction transaction, ServerReference server) throws RemoteException {
        System.out.println("Join from " + server.getIp() + ":" +server.getPort());
        if( this.transactionsReferences.containsKey(transaction.getId()) )
        {
            return false;
        }
        Registry registry = LocateRegistry.getRegistry(server.getIp(), server.getPort());
        try {
            ICoordinator look_up = (ICoordinator) registry.lookup(server.getServerNameRMI());
            this.transactionsReferences.put(transaction.getId(), look_up );
        } catch (NotBoundException e) {
            e.printStackTrace();
        }

        return true;
    }
}
