package client;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;

import controllers.ClienteController;
import serverUsers.User;


public class GUICliente extends JFrame {

	private static final long serialVersionUID = 1L;
	private JFrame frame;
	private JPanel contentPane;
	private JTable tableProducts;
	private JButton btnComprar;
	private JLabel lblBalance;
	
	private ClienteController controller;

	/**
	 * Create the application.
	 */
	public GUICliente(User user) {
		this.controller = new ClienteController(this, user);
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		
		frame = new JFrame();
		frame.setSize(270, 160);
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		login();
	}
	private void login() {
		
		JPanel principalPanel = new JPanel();
		principalPanel.setBounds(0, 0, 450, 278);
		// frame.getContentPane().add(principalPanel);
		contentPane.add(principalPanel);
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
		this.controller.populateTable();
		
		btnComprar = new JButton("Comprar");
		btnComprar.setBounds(294, 97, 117, 29);
		userTab.add(btnComprar);
		
		JLabel lblBalancetitle = new JLabel("Balance:");
		lblBalancetitle.setBounds(304, 138, 61, 16);
		userTab.add(lblBalancetitle);
		
		lblBalance = new JLabel("");
		lblBalance.setBounds(304, 166, 61, 16);
		this.controller.setAccountBalance();
		
		userTab.add(lblBalance);
		btnComprar.addActionListener(controller);
				
	}
	
	public JButton getBtnComprar() {
		return this.btnComprar;
	}
	
	public JTable getTableProducts() {
		return this.tableProducts;
	}

	public JLabel getLblBalance() {
		return lblBalance;
	}

	public void setLblBalance(JLabel lblBalance) {
		this.lblBalance = lblBalance;
	}
	
}
