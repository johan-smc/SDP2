package intefaces;

import protocol2pc.Transaction;
import server.Resource;
import serverCatalog.Product;
import serverUsers.User;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface ICatalog extends Remote {
    public Map<String, Product> getCatalog() throws RemoteException;
    String openTransaction(Transaction transaction) throws RemoteException;
}
