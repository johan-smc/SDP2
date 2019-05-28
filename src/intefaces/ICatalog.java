package intefaces;

import protocol2pc.Transaction;
import server.Resource;
import server.ServerReference;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface ICatalog extends Remote {
    public Map<String, Resource> getCatalog() throws RemoteException;
    String openTransaction(Transaction transaction) throws RemoteException;

    ServerReference getReference() throws RemoteException;
    Transaction.DECISION getDecision(String transaction) throws RemoteException;
}
