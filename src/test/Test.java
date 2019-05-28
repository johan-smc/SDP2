package test;

import intefaces.ICatalog;
import intefaces.IUsers;
import protocol2pc.Operation;
import protocol2pc.Transaction;
import server.Resource;
import server.Server;
import serverCatalog.Product;
import serverUsers.User;

import javax.swing.*;
import java.nio.charset.StandardCharsets;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Scanner;

public class Test {

    private static IUsers serverUsers ;
    private static ICatalog serverCatalog;
    private static User user;
    private static Scanner sc;


    private static void printMenu() {
        System.out.println("Menu: ");
        System.out.println("1. login");
        System.out.println("2. get products");
        System.out.println("3. get Users");
        System.out.println("4. comprar");
        System.out.println("5. abonar");
        System.out.println("5. abonar a producto");
    }

    public static void main(String[] args)
    {
        conectToServers();
        sc = new Scanner(System.in);
        int option = -1;
        while( option != 0){
            printMenu();
            option = sc.nextInt();
            if( option == 1 ){
                login();
            }
            else if( option == 2){
                getProducts();
            }
            else if( option == 3){
                getUsers();
            } else if( option == 4 ){
                comprar();
            } else if( option == 5 ){
                abonar();
            } else if( option == 6 ){
                abonarProducto();
            }
        }
    }

    private static void abonarProducto() {
        if( user == null){
            System.out.println("primero login");
            return ;
        }
        Transaction transaction = new Transaction();
        try {

            Map<String, Resource> ans = serverCatalog.getCatalog();
            for (Map.Entry<String, Resource> entry : ans.entrySet()) {
                Product p = (Product)entry.getValue();
                System.out.println(entry.getKey() + ": " + p.toString());
                System.out.println("Escriba la cantidad de productos deseada");
                int cant = sc.nextInt();
                if( cant != 0 )
                {
                    transaction.putOperation(new Operation(Operation.TYPE.WRITE, cant, p.getId(), serverCatalog.getReference()));


                }
            }


            String idTransaction = serverCatalog.openTransaction(transaction);
            System.out.println(idTransaction);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private static void abonar() {
        if( user == null){
            System.out.println("primero login");
            return ;
        }
        Transaction transaction = new Transaction();
        try {
            double total = 0;
            Map<String, Resource> ans = serverUsers.getUsers();
            for (Map.Entry<String, Resource> entry : ans.entrySet()) {
                User p = (User)entry.getValue();
                System.out.println(entry.getKey() + ": " + p.toString());
                System.out.println("Escriba la cantidad abono deseada");
                double cant = sc.nextDouble();
                if( cant != 0 )
                {
                    transaction.putOperation(new Operation(Operation.TYPE.WRITE, cant, p.getId(), serverUsers.getReference()));

                }
            }

            String idTransaction = serverCatalog.openTransaction(transaction);
            System.out.println(idTransaction);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private static void comprar() {
        if( user == null){
            System.out.println("primero login");
            return ;
        }
        Transaction transaction = new Transaction();
        try {
            double total = 0;
            Map<String, Resource> ans = serverCatalog.getCatalog();
            for (Map.Entry<String, Resource> entry : ans.entrySet()) {
                Product p = (Product)entry.getValue();
                System.out.println(entry.getKey() + ": " + p.toString());
                System.out.println("Escriba la cantidad de productos deseada");
                int cant = sc.nextInt();
                if( cant != 0 )
                {
                    transaction.putOperation(new Operation(Operation.TYPE.WRITE, -cant, p.getId(), serverCatalog.getReference()));
                    total += p.getCost() * cant;
                }
            }
            transaction.putOperation(new Operation(Operation.TYPE.WRITE, -total, user.getId(), serverUsers.getReference()));

            String idTransaction = serverCatalog.openTransaction(transaction);
            System.out.println(idTransaction);
        } catch (RemoteException e) {
            e.printStackTrace();
        }


    }

    private static void getUsers() {
        try {
            Map<String, Resource> ans = serverUsers.getUsers();
            for (Map.Entry<String, Resource> entry : ans.entrySet()) {
                User p = (User)entry.getValue();
                System.out.println(entry.getKey() + ": " + p.toString());
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private static void getProducts() {
        try {
            Map<String, Resource> ans = serverCatalog.getCatalog();
            for (Map.Entry<String, Resource> entry : ans.entrySet()) {
                Product p = (Product)entry.getValue();
                System.out.println(entry.getKey() + ": " + p.toString());
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }



    private static void conectToServers() {
        Registry registry = null;

        try {
            registry = LocateRegistry.getRegistry("localhost", 9999);
            serverUsers = (IUsers) registry.lookup(Server.NAME_SERVICE);
            registry = LocateRegistry.getRegistry("localhost", 8888);
            serverCatalog = (ICatalog) registry.lookup(Server.NAME_SERVICE);
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }

    private static void login()
    {
        try {
            System.out.println("Username: ");
            String username = sc.next();
            sc.nextLine();
            System.out.println("Password: ");
            String password = sc.nextLine();
            user = serverUsers.login(
                    username,
                    getHash(password)
            );
            if( user != null )
            {
                if( user.isEmptyPassword() ){
                    changePassword();
                }
            } else{
                System.out.println("Login incorrecto");
            }

        } catch (RemoteException e1) {
            e1.printStackTrace();
        }
    }

    private static void changePassword() {
        System.out.println("Ingrese nueva contraseña");
        String password = sc.next();
        password = getHash(password);
        try {
            user = serverUsers.changePassword(user.getId(), password);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    private static String getHash(String password) {
        if( password.equals("") )
        {
            return password;
        }
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(password.getBytes(StandardCharsets.UTF_8)).toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
