package intefaces;

import protocol2pc.Participant;
import protocol2pc.Transaction;
import server.ServerReference;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ICoordinator extends Remote {


    void haveCommited(Transaction transaction, ServerReference participant) throws RemoteException;
    Transaction.DESISION getDecision(Transaction transaction) throws RemoteException;
    String openTransaction(Transaction transaction) throws RemoteException;
    String hi() throws RemoteException;

}
