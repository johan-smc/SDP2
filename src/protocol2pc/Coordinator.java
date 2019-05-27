package protocol2pc;

import intefaces.ICoordinator;
import intefaces.IParticipant;
import server.Server;
import server.ServerReference;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Coordinator implements ICoordinator {

    public static final String NAME_SERVICE = "coordinator";

    private static int consecutive = 0;
    private Map<String, Transaction> transactions;
    private Map<String, Map<ServerReference, Boolean> > transactionParticipants;
    private Map<ServerReference, IParticipant> participants;
    private ServerReference myServer;


    public Coordinator(ServerReference myServer) {
        this.myServer = myServer;
    }

    @Override
    public void haveCommited(Transaction transaction, ServerReference participant) throws RemoteException {
        this.transactionParticipants.get(transaction.getId()).remove(participant);
    }

    @Override
    public Transaction.DESITION getDecision(Transaction transaction) throws RemoteException {
        if( !this.transactions.containsKey(transaction.getId()) )
        {
            return Transaction.DESITION.NOT_EXIST;
        }
        return this.transactions.get(transaction.getId()).getDesition();
    }

    @Override
    public String openTransaction(Transaction transaction) throws RemoteException {
        transaction.setId(this.myServer.getIp()+getConsecutive());
        joinAllParticipants(transaction);
        new Thread(() -> {
            sendCanCommitAllParticipants(transaction);
        }).start();

        return transaction.getId();
    }

    private String getConsecutive() {
        String answer = String.valueOf(consecutive);
        consecutive++;
        return answer;
    }



    private void sendCanCommitAllParticipants(Transaction transaction) {

        boolean commit = true;
        Map<ServerReference, Boolean> server = this.transactionParticipants.get(transaction.getId());
        for (Map.Entry<ServerReference, Boolean> entry : server.entrySet()) {
            boolean answer  = false;
            try {
                answer = this.participants.get(entry.getKey()).canCommit(transaction);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            if( !answer )
            {
                this.doAbort(transaction);
                commit = false;
                break;
            }
            entry.setValue(answer);
        }
        if( commit)
        {
            this.doCommit(transaction);
        }
        // TODO inform client
    }

    private void doAbort(Transaction transaction) {


        Map<ServerReference, Boolean> server = this.transactionParticipants.get(transaction.getId());
        for (Map.Entry<ServerReference, Boolean> entry : server.entrySet()) {
            try {
                this.participants.get(entry.getKey()).doAbort(transaction);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
    private void doCommit(Transaction transaction) {
        // TODO maybe has error the code


        Map<ServerReference, Boolean> server = this.transactionParticipants.get(transaction.getId());
        for (Map.Entry<ServerReference, Boolean> entry : server.entrySet()) {
            try {
                this.participants.get(entry.getKey()).doCommit(transaction);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void joinAllParticipants(Transaction transaction) {
        this.transactionParticipants.put(transaction.getId(), new HashMap<>());
        Map<ServerReference, Boolean> server = this.transactionParticipants.get(transaction.getId());
        for (Operation operation: transaction.getOperations())
        {
            if( !server.containsKey(operation.getServer()) )
            {
                server.put(operation.getServer(), null);
                addServer(operation.getServer());
                try {
                    this.participants.get(operation.getServer()).join(transaction, this.myServer);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void addServer(ServerReference server) {
        if( !this.participants.containsKey(server) )
        {
            try {
                Registry registry = LocateRegistry.getRegistry(server.getIp(), server.getPort());
                IParticipant look_up = (IParticipant) registry.lookup(server.getServerNameRMI());
                this.participants.put(server, look_up);
            } catch (RemoteException | NotBoundException e) {
                e.printStackTrace();
            }
        }
    }
}
