package controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import client.GUICliente;
import intefaces.ICatalog;
import intefaces.IUsers;
import protocol2pc.Operation;
import protocol2pc.Transaction;
import server.Resource;
import server.Server;
import serverCatalog.Product;
import serverUsers.User;

public class ClienteController implements ActionListener{

	private GUICliente clientGui;
	private ICatalog serverCatalog;
	private IUsers serverUser;
	private User user;
	private Set<String> transactionsIds;
	
	public ClienteController(GUICliente clientGui, User user) {
		this.conectToServers();
		this.user = user;
		this.clientGui = clientGui;
		this.transactionsIds = new HashSet<>();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == this.clientGui.getBtnComprar()) {
			this.enviarCompra();
		}
		if(e.getSource() == this.clientGui.getBtnActualizarInventario()) {
			this.actualizarInventario();
		}
		
		if(e.getSource() == this.clientGui.getBtnActualizarLogs()) {
			this.actualizarLogs();
		}
		if( e.getSource() == this.clientGui.getBtnCommit()){
			this.agegarACuenta();
		}
		
	}

	private void agegarACuenta() {
		Transaction transaction = new Transaction();
		TableModel model = this.clientGui.getTableUser().getModel();
		User[] newUsers = new User[model.getRowCount()];
		for(int i = 0; i < model.getRowCount(); ++i) {
			String username =(String) model.getValueAt(i, 0);
			String pass = (String) model.getValueAt(i, 1);
			Boolean isAdmin = Boolean.parseBoolean((String) model.getValueAt(i, 3));
			Double acutal = Double.parseDouble((String) model.getValueAt(i, 2));
			Double toAdd = Double.parseDouble((String) model.getValueAt(i, 4));
			User temp = new User(username, pass, acutal + toAdd, isAdmin);
			newUsers[i] = temp;
			if( toAdd != 0.0 ){
				try {
					transaction.putOperation(new Operation(Operation.TYPE.WRITE, toAdd, newUsers[i].getId(), serverUser.getReference()));
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
		try {
			this.transactionsIds.add(
					this.serverCatalog.openTransaction(transaction)
			);

		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	private void actualizarLogs() {
		this.populateTable();
		this.populateUserTable();
		this.setAccountBalance();
		String newStr = "";
		for (String id:
			 this.transactionsIds) {
			newStr += id + " ";
			try {
				newStr += this.serverCatalog.getDecision(id);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			newStr += "\n";
		}
		String oldLogs = this.clientGui.getTxtLogs().getText();
		this.clientGui.getTxtLogs().setText(newStr);

	}
	
	private void actualizarInventario() {
		Transaction transaction = new Transaction();
		Product[] prods = this.getShoppingCarContent(this.clientGui.getTableProducts().getModel());
		for(int i = 0; i < this.clientGui.getTableProducts().getModel().getRowCount(); ++i) {
			int toAdd = Integer.parseInt((String) this.clientGui.getTableProducts().getModel().getValueAt(i, 3));
			prods[i].setTotal(toAdd);
			try {
				if( toAdd != 0) {
					transaction.putOperation(new Operation(Operation.TYPE.WRITE, toAdd, prods[i].getId(), serverCatalog.getReference()));
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		try {
			this.transactionsIds.add(
					this.serverCatalog.openTransaction(transaction)
			);

		} catch (RemoteException e) {
			e.printStackTrace();
		}
		//this.populateTable(new ArrayList<Product>(Arrays.asList(prods)));
	}
	
	private void enviarCompra() {		
		Product[] prods = this.getShoppingCarContent(this.clientGui.getTableProducts().getModel());
		//for(int i = 0; i < prods.length; ++i) System.out.println(prods[i]);
		Transaction transaction = new Transaction(Transaction.TYPE_TRANSACTION.PURCHASE);
		double total = 0;
		for(int i = 0; i < this.clientGui.getTableProducts().getModel().getRowCount(); ++i) {
			int toAdd = Integer.parseInt((String) this.clientGui.getTableProducts().getModel().getValueAt(i, 3));
			prods[i].setTotal(toAdd);
			try {

				if( toAdd != 0) {

					transaction.putOperation(new Operation(Operation.TYPE.WRITE, -toAdd, prods[i].getId(), serverCatalog.getReference()));
					total += prods[i].getCost() * toAdd;
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		try {
			transaction.putOperation(new Operation(Operation.TYPE.WRITE, -total, user.getId(), serverUser.getReference()));
			this.transactionsIds.add(
					this.serverCatalog.openTransaction(transaction)
			);

		} catch (RemoteException e) {
			e.printStackTrace();
		}
		//this.populateTable(new ArrayList<Product>(Arrays.asList(prods)));
	}
	
	public void setAccountBalance() {
		String balance = "";
		User tmpUser = null;
		try {
			tmpUser = (User) this.serverUser.login(this.user.getId(), this.user.getPassword());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		balance = Double.toString(tmpUser.getBalance());
		this.clientGui.getLblBalance().setText(balance);
	}
	
	public void populateTable() {
		ArrayList<Product> catalog = this.getProductsFromServer();
		String columnNames[] = { "Nombre Producto", "Costo", "Inventario", "Cantidad" };
		DefaultTableModel model = new DefaultTableModel(columnNames, 0);
		for(Product product: catalog) {
			String[] rowValues = {product.getId(), Double.toString(product.getCost()), Integer.toString(product.getTotal()),  "0" };
			model.addRow(rowValues);
		}
		this.clientGui.getTableProducts().setModel(model);
		
	}
	
	private void populateTable(ArrayList<Product> catalog) {
		String columnNames[] = { "Nombre Producto", "Costo", "Inventario", "Cantidad" };
		DefaultTableModel model = new DefaultTableModel(columnNames, 0);
		for(Product product: catalog) {
			String[] rowValues = {product.getId(), Double.toString(product.getCost()), Integer.toString(product.getTotal()),  "0" };
			model.addRow(rowValues);
		}
		this.clientGui.getTableProducts().setModel(model);
		
	}
	
	private ArrayList<Product> getProductsFromServer() {
		ArrayList<Product> products = new ArrayList<Product>();
		Map<String, Resource> catalog = null;
		try {
			catalog = this.serverCatalog.getCatalog();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		Set<String> mapKeys = catalog.keySet();
		for(String key: mapKeys) {
			Product prod = (Product) catalog.get(key);
			products.add(prod);
		}
		return products;
	}
	
	private  void conectToServers() {
		Registry registry = null;

		try {
			registry = LocateRegistry.getRegistry(LoginController.ipProducts, LoginController.portProducts);
			this.serverCatalog = (ICatalog) registry.lookup(Server.NAME_SERVICE);
			registry = LocateRegistry.getRegistry(LoginController.ipUsers, LoginController.portUsers);
			this.serverUser = (IUsers) registry.lookup(Server.NAME_SERVICE);
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
	} 
	
	private Product[] getShoppingCarContent(TableModel productModel) {
		Product[] products = new Product[productModel.getRowCount()];
		for(int i = 0; i < productModel.getRowCount(); ++i) {
			String name = (String) productModel.getValueAt(i, 0);
			double value = Double.parseDouble((String) productModel.getValueAt(i, 1));
			int count = Integer.parseInt((String) productModel.getValueAt(i, 2));
			Product newProd = new Product(count, value, name);
			products[i] = newProd;
		}
		return products;
	}
	
	public void populateUserTable() {
		if(this.user.isAdmin()) {
			ArrayList<User> users = this.getUsersFromServer();
			String columnNames[] = { "Usuario", "Contraseña", "Balance", "¿Es Admin?", "Abono" };
			DefaultTableModel model = new DefaultTableModel(columnNames, 0);
			for(User user: users) {
				String[] row = { user.getId(), user.getPassword(), Double.toString(user.getBalance()), Boolean.toString(user.isAdmin()), "0" };
				model.addRow(row);
			}
			this.clientGui.getTableUser().setModel(model);
		}
	}
	
	private ArrayList<User> getUsersFromServer() {
		ArrayList<User> users = new ArrayList<User>();
		Map<String, Resource> usersList = null;
		try {
			usersList = this.serverUser.getUsers();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		for(Map.Entry<String, Resource> entry: usersList.entrySet()) {
			users.add((User) entry.getValue());
		}
		return users;
	}

	public User getUser() {
		return user;
	}

}
