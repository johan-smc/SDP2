package protocol2pc;

import intefaces.ICoordinator;
import intefaces.IParticipant;
import server.ServerReference;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class Coordinator extends UnicastRemoteObject implements ICoordinator {

    public static final String NAME_SERVICE = "coordinator";

    private static int consecutive = 0;
    private Map<String, Transaction> transactions;
    private Map<String, Map<ServerReference, Boolean> > transactionParticipants;
    private Map<ServerReference, IParticipant> participants;
    private ServerReference myServer;


    public Coordinator(ServerReference myServer) throws RemoteException {
        super();

        this.transactionParticipants = new HashMap<>();
        this.transactions = new HashMap<>();
        this.participants = new TreeMap<>();
        this.myServer = myServer;
    }

    @Override
    public synchronized  void haveCommited(Transaction transaction, ServerReference participant) throws RemoteException {

        System.out.println("INIT Delete participant");
        this.transactionParticipants.get(transaction.getId()).remove(participant);
        System.out.println("OK Delete participant");
    }

    @Override
    public Transaction.DECISION getDecision(Transaction transaction) throws RemoteException {
        if( !this.transactions.containsKey(transaction.getId()) )
        {
            return Transaction.DECISION.NOT_EXIST;
        }
        return this.transactions.get(transaction.getId()).getDesition();
    }

    @Override
    public String openTransaction(Transaction transaction) throws RemoteException {
        transaction.setId(this.myServer.getIp()+"-"+getConsecutive());
        joinAllParticipants(transaction);
        System.out.println("Join complete");
        new Thread(() -> {
            sendCanCommitAllParticipants(transaction);
        }).start();

        return transaction.getId();
    }

    @Override
    public String hi() throws RemoteException {
        return "HI";
    }

    private String getConsecutive() {
        String answer = String.valueOf(consecutive);
        consecutive++;
        return answer;
    }



    private void sendCanCommitAllParticipants(Transaction transaction) {

        boolean commit = true;
        boolean isPurchase = transaction.getTypeTransaction().equals(Transaction.TYPE_TRANSACTION.PURCHASE);
        Operation balanceTemporal = null;

        if( isPurchase ) {
            balanceTemporal = getBalance(transaction);
            if( !verifyBalance(balanceTemporal, transaction) )
            {
                this.doAbort(transaction);
                System.out.println("The decision is: " +false);
                return;
            }

            removeBalance(transaction);


        }

        Map<ServerReference, Boolean> server = this.transactionParticipants.get(transaction.getId());
        System.out.println("Size: " +server.size());
        for (Map.Entry<ServerReference, Boolean> entry : server.entrySet()) {

            boolean answer  = false;
            try {
                System.out.println("Enviando a " + this.participants.get(entry.getKey()));
                answer = this.participants.get(entry.getKey()).canCommit(transaction);
                System.out.println("Answer: "+ answer);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            if( !answer && !isPurchase )
            {
                this.doAbort(transaction);
                commit = false;
                break;
            }
            entry.setValue(answer);

        }
        if( isPurchase )
        {
            boolean answer = true;
            updateBalance(balanceTemporal, transaction);
            try {
                System.out.println("Enviando a ---- " + this.participants.get(balanceTemporal.getServer()));
                answer = this.participants.get(balanceTemporal.getServer()).canCommit(transaction);
                System.out.println("Answer: "+ answer);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            if( !answer )
            {
                this.doAbort(transaction);
                commit = false;
            }
        }
        if( commit)
        {
            this.doCommit(transaction);
        }
        System.out.println("The decision is: " +commit);


    }

    private Operation getBalance(Transaction transaction) {
        int pos = transaction.getOperations().size()-1;
        return transaction.getOperations().get(pos);
    }

    private Operation updateBalance(Operation balanceTemporal, Transaction transaction) {
        ServerReference serverCatalog = transaction.getOperations().get(0).getServer();
        try {
            balanceTemporal.setValue(this.participants.get(serverCatalog).getBalance(transaction));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        System.out.println(balanceTemporal.getValue());
        transaction.getOperations().add(balanceTemporal);
        this.transactionParticipants.get(transaction.getId()).put(balanceTemporal.getServer(), null);
        System.out.println("Update complete");
        return balanceTemporal;
    }

    private void removeBalance(Transaction transaction) {
        int pos = transaction.getOperations().size()-1;
        transaction.getOperations().remove(pos);


    }

    private boolean verifyBalance(Operation transaction, Transaction transaction1) {
        boolean ans = false;
        try {
            ans = this.participants.get(transaction.getServer()).canCommit(transaction1);
            this.participants.get(transaction.getServer()).doAbort(transaction1);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return ans;
    }


    private void doAbort(Transaction transaction) {

        System.out.println("Do Abort");
        Map<ServerReference, Boolean> server = this.transactionParticipants.get(transaction.getId());

        Set<ServerReference> copy = new TreeSet<>(server.keySet());
        for (ServerReference reference:copy ) {
            try {

                    if (this.participants.containsKey(reference)) {
                        this.participants.get(reference).doAbort(transaction);
                    }

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        transaction.setDesition(Transaction.DECISION.ABORT);
    }
    private void doCommit(Transaction transaction) {
        // TODO maybe has error the code

        System.out.println("Do Commit");
        Map<ServerReference, Boolean> server = this.transactionParticipants.get(transaction.getId());
        System.out.println("inform to " + server.size() +" servers");

        Set<ServerReference> copy = new TreeSet<>(server.keySet());
        for (ServerReference reference:copy ) {
            try {

                    System.out.println("Inform to " +reference);
                    if( this.participants.containsKey(reference)) {
                        this.participants.get(reference).doCommit(transaction);
                    }


            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        transaction.setDesition(Transaction.DECISION.COMMIT);
        System.out.println("Terminate Commit");

    }

    private void joinAllParticipants(Transaction transaction) {
        this.transactionParticipants.put(transaction.getId(), new TreeMap<>());
        Map<ServerReference, Boolean> server = this.transactionParticipants.get(transaction.getId());
        for (Operation operation: transaction.getOperations())
        {
            if( !server.containsKey(operation.getServer()) )
            {
                System.out.println("Add to server list");
                System.out.println();
                putToServer(server,operation.getServer());
                addServer(operation.getServer());
                try {
                    this.participants.get(operation.getServer()).join(transaction, this.myServer);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void putToServer(Map<ServerReference, Boolean> server,ServerReference serverReference) {
        if( !server.containsKey(serverReference))
        {
            server.put(serverReference, null);
        }

    }

    private void addServer(ServerReference server) {

        if( !this.participants.containsKey(server) )
        {
            System.out.println("Inicio agregar");
            try {
                System.out.println(server.getIp());
                System.out.println(server.getPort());
                System.out.println(server.getServerNameRMI());
                Registry registry = LocateRegistry.getRegistry(server.getIp(), server.getPort());
                IParticipant look_up = (IParticipant) registry.lookup(Participant.NAME_SERVICE);
                //System.out.println("voy a agregar el participante"+ look_up.toString());
                this.participants.put(server, look_up);
            } catch (RemoteException | NotBoundException e) {
                e.printStackTrace();
            }
        }
    }
}
