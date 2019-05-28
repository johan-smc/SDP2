package intefaces;

import protocol2pc.Transaction;
import server.ServerReference;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IParticipant extends Remote {

    boolean canCommit(Transaction transaction) throws RemoteException;
    void doCommit(Transaction transaction) throws RemoteException;
    void doAbort(Transaction transaction) throws RemoteException;
    boolean join(Transaction transaction, ServerReference server) throws RemoteException;
    double getBalance(Transaction transaction) throws RemoteException;

}
