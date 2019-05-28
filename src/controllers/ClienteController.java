package controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import client.GUICliente;
import intefaces.ICatalog;
import intefaces.IUsers;
import server.Resource;
import server.Server;
import serverCatalog.Product;
import serverUsers.User;

public class ClienteController implements ActionListener{

	private GUICliente clientGui;
	private ICatalog serverCatalog;
	private IUsers serverUser;
	private User user;
	
	public ClienteController(GUICliente clientGui, User user) {
		this.conectToServers();
		this.user = user;
		this.clientGui = clientGui;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == this.clientGui.getBtnComprar()) {
			this.enviarCompra();
		}
		
	}
	
	private void enviarCompra() {		
		Product[] prods = this.getShoppingCarContent(this.clientGui.getTableProducts().getModel());
		for(int i = 0; i < prods.length; ++i) System.out.println(prods[i]);
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
		String columnNames[] = { "Nombre Producto", "Costo", "Cantidad" };
		DefaultTableModel model = new DefaultTableModel(columnNames, 0);
		for(Product product: catalog) {
			String[] rowValues = {product.getId(), Double.toString(product.getCost()), "0" };
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
			registry = LocateRegistry.getRegistry("localhost", 8888);
			this.serverCatalog = (ICatalog) registry.lookup(Server.NAME_SERVICE);
			registry = LocateRegistry.getRegistry("localhost", 9999);
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

}
