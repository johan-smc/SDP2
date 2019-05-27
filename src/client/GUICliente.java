package client;

import intefaces.IUsers;
import server.Server;
import serverUsers.User;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.StandardCharsets;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.List;
import java.awt.ScrollPane;

public class GUICliente {

	private JFrame frame;
	private JTextField txtUsername;
	private JTextField txtPassword;
	private IUsers serverUsers;
	private User user;
	private JTable tableProducts;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args){
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUICliente window = new GUICliente();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private  void conectToServers() {
		Registry registry = null;

		try {
			registry = LocateRegistry.getRegistry("localhost", 9999);
			this.serverUsers = (IUsers) registry.lookup(Server.NAME_SERVICE);
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the application.
	 */
	public GUICliente() {
		conectToServers();
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		login();
		principalFrame();
	}
	private void login() {
		JPanel principalPanel = new JPanel();
		principalPanel.setBounds(0, 0, 450, 278);
		frame.getContentPane().add(principalPanel);
		principalPanel.setLayout(null);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 450, 278);
		principalPanel.add(tabbedPane);
		
		JPanel userTab = new JPanel();
		tabbedPane.addTab("User", null, userTab, null);
		userTab.setLayout(null);
		
		JScrollPane scrollPanePrducts = new JScrollPane();
		scrollPanePrducts.setBounds(6, 6, 254, 220);
		userTab.add(scrollPanePrducts);
		
		tableProducts = new JTable();
		scrollPanePrducts.setViewportView(tableProducts);
		Object columnNames[] = { "Column One", "Column Two", "Column Three" };
		DefaultTableModel model = new DefaultTableModel(columnNames, 0);
		Object rowData[] = { "Row1-Column1", "Row1-Column2", "Row1-Column3" };
		model.addRow(rowData);
		tableProducts.setModel(model);
		
		JButton btnComprar = new JButton("Comprar");
		btnComprar.setBounds(294, 97, 117, 29);
		userTab.add(btnComprar);
		
		JPanel loginPanel = new JPanel();
		loginPanel.setBounds(0, 0, 450, 278);
		frame.getContentPane().add(loginPanel);
		loginPanel.setLayout(null);
		
		txtUsername = new JTextField();
		txtUsername.setBounds(146, 92, 130, 26);
		loginPanel.add(txtUsername);
		txtUsername.setColumns(10);
		
		txtPassword = new JTextField();
		txtPassword.setBounds(146, 140, 130, 26);
		loginPanel.add(txtPassword);
		txtPassword.setColumns(10);
		
		JLabel lblUsername = new JLabel("Username");
		lblUsername.setBounds(146, 75, 101, 16);
		loginPanel.add(lblUsername);
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setBounds(146, 122, 85, 16);
		loginPanel.add(lblPassword);
		
		JButton btnLogin = new JButton("Login");
		btnLogin.setBounds(146, 178, 117, 29);
		loginPanel.add(btnLogin);
		

		btnLogin.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {

				try {
					user = serverUsers.login(
							txtUsername.getText(),
							getHash(txtPassword.getText())
					);
					if( user != null )
					{
						if( user.isEmptyPassword() ){
							changePassword();
						}
						loginPanel.setVisible(false);
						//principalFrame();
					} else{
						JOptionPane.showMessageDialog(null,  "Login Incorrecto", "Error", JOptionPane.WARNING_MESSAGE);
					}

				} catch (RemoteException e1) {
					e1.printStackTrace();
				}
			}
		});
	}

	private void principalFrame() {
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
