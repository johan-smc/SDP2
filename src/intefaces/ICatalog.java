package intefaces;

import protocol2pc.Transaction;
import server.Resource;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface ICatalog extends Remote {
    public Map<String, Resource> getCatalog() throws RemoteException;
    String openTransaction(Transaction transaction) throws RemoteException;
}
