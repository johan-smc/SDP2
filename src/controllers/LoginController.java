package controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.StandardCharsets;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.swing.JOptionPane;

import client.GUICliente;
import client.LoginView;
import intefaces.IUsers;
import server.Server;
import serverUsers.User;

public class LoginController implements ActionListener{

	public static final String ipUsers = "localhost";
	public static final int portUsers = 9999;

	public static final String ipProducts = "localhost";
	public  static final int portProducts = 8888;
	private LoginView loginView;
	private User user;
	private IUsers serverUsers;
	
	public LoginController(LoginView view) {
		this.loginView = view;
		this.conectToServers();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String username = this.loginView.getTxtUsername().getText();
		String password = this.loginView.getTxtPassword().getText();
		this.validateCredentials(username, password);
		try {
			user = serverUsers.login(username,getHash(password));
			if( user != null ) {
				if( user.isEmptyPassword() ){
					changePassword();
				}
				new GUICliente(user).setVisible(true);
				this.loginView.setVisible(false);
			} else{
				JOptionPane.showMessageDialog(null,  "Login Incorrecto", "Error", JOptionPane.WARNING_MESSAGE);
			}

		} catch (RemoteException e1) {
			e1.printStackTrace();
		}
	}
	
	private void validateCredentials(String user, String password) {
		if(user.equals("")) {
			JOptionPane.showMessageDialog(null,  "Usuario no pueden estar vacios", "Error", JOptionPane.WARNING_MESSAGE);
		}
	}
	
	private  void conectToServers() {
		Registry registry = null;
		try {
			registry = LocateRegistry.getRegistry(this.ipUsers, this.portUsers);
			this.serverUsers = (IUsers) registry.lookup(Server.NAME_SERVICE);
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
	}
	
	private void changePassword() {
		String password = JOptionPane.showInputDialog("Escribe la nueva contraseña");
		password = getHash(password);
		try {
			this.user = this.serverUsers.changePassword(this.user.getId(), password);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}
	
	private String getHash(String password) {

		password = password.trim();
		if( password.equals("") )
		{
			return password;
		}
		//return Integer.toString(password.hashCode());

		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			return Base64.getEncoder().encodeToString(digest.digest(password.getBytes(StandardCharsets.UTF_8)));

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

}
