package intefaces;

import protocol2pc.Transaction;
import server.Resource;
import serverUsers.User;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface IUsers extends Remote {
    public Map<String, Resource> getUsers() throws RemoteException;
    public User login(String id, String password) throws RemoteException;
    public User changePassword(String id, String password) throws RemoteException;
    String openTransaction(Transaction transaction) throws RemoteException;
}
