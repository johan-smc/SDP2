package protocol2pc;

import intefaces.ICoordinator;
import intefaces.IParticipant;
import server.Resource;
import server.ServerReference;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Participant implements IParticipant {

    public static final String NAME_SERVICE = "participant";
    private Map<String, Resource> resources;
    private ServerReference myServer;
    private Map<String, ICoordinator> transactionsReferences;

    public Participant(Map<String, Resource> resources, ServerReference myServer){
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
                resourceSet.add(resource.toString());
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
        for ( Operation operation:
                transaction.getOperations()) {
            if( operation.getServer().equals(myServer) )
            {
                Resource resource =this.resources.get(operation.getResource());
                if( resource != null )
                {
                    resource.doCommit(transaction.getId());
                }

            }
        }
        this.transactionsReferences.get(transaction.getId()).haveCommited(transaction, this.myServer);

    }

    @Override
    public void doAbort(Transaction transaction) throws RemoteException {
        for ( Operation operation:
                transaction.getOperations()) {
            if (operation.getServer().equals(myServer)) {
                Resource resource = this.resources.get(operation.getResource());
                if (resource != null) {
                    resource.doAbort(transaction.getId());
                }

            }
        }
        this.transactionsReferences.get(transaction.getId()).haveCommited(transaction, this.myServer);
    }

    @Override
    public boolean join(Transaction transaction, ServerReference server) throws RemoteException {
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
