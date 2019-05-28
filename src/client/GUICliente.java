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
import javax.swing.JTextPane;


public class GUICliente extends JFrame {

	private static final long serialVersionUID = 1L;
	private JFrame frame;
	private JPanel contentPane;
	private JTable tableProducts;
	private JButton btnComprar;
	private JLabel lblBalance;
	
	private ClienteController controller;
	private JTable tableUser;
	private JButton btnCommit;
	private JButton btnActualizarInventario;
	private JTextPane txtLogs;
	private JButton btnActualizarLogs;

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
		setBounds(100, 100, 656, 521);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		
		frame = new JFrame();
		frame.setSize(270, 160);
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		catalogView();
	}
	private void catalogView() {
		
		JPanel principalPanel = new JPanel();
		principalPanel.setBounds(0, 0, 450, 278);
		// frame.getContentPane().add(principalPanel);
		contentPane.add(principalPanel);
		principalPanel.setLayout(null);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 615, 446);
		principalPanel.add(tabbedPane);
		
		JPanel userTab = new JPanel();
		tabbedPane.addTab("User", null, userTab, null);
		userTab.setLayout(null);
		
		JScrollPane scrollPanePrducts = new JScrollPane();
		scrollPanePrducts.setBounds(6, 6, 567, 246);
		userTab.add(scrollPanePrducts);
		
		tableProducts = new JTable();
		scrollPanePrducts.setViewportView(tableProducts);
		this.controller.populateTable();
		
		btnComprar = new JButton("Comprar");
		btnComprar.setBounds(156, 289, 117, 29);
		userTab.add(btnComprar);
		btnComprar.addActionListener(controller);
		
		JLabel lblBalancetitle = new JLabel("Balance:");
		lblBalancetitle.setBounds(35, 339, 61, 16);
		userTab.add(lblBalancetitle);
		
		lblBalance = new JLabel("");
		lblBalance.setBounds(93, 339, 61, 16);
		this.controller.setAccountBalance();
		
		userTab.add(lblBalance);
		
		JPanel andminTab = new JPanel();
		if(this.controller.getUser().isAdmin()) {
			tabbedPane.addTab("Admin", null, andminTab, null);
		}
		userTab.setLayout(null);
		
		btnActualizarInventario = new JButton("Actualizar Inventario");
		btnActualizarInventario.setBounds(298, 289, 189, 29);
		if(this.controller.getUser().isAdmin()) {
			userTab.add(btnActualizarInventario);
		}
		btnActualizarInventario.addActionListener(controller);
		
		JScrollPane scrollPaneUsers = new JScrollPane();
		scrollPaneUsers.setBounds(6, 6, 254, 220);
		andminTab.add(scrollPaneUsers);
		
		tableUser = new JTable();
		scrollPaneUsers.setViewportView(tableUser);	
		this.controller.populateUserTable();
		
		btnCommit = new JButton("Commit");
		andminTab.add(btnCommit);
		btnCommit.addActionListener(controller);
		
		JPanel logsTab = new JPanel();
		tabbedPane.addTab("Logs", null, logsTab, null);
		logsTab.setLayout(null);
		
		btnActualizarLogs = new JButton("Actualizar");
		btnActualizarLogs.setBounds(242, 334, 117, 29);
		logsTab.add(btnActualizarLogs);
		
		txtLogs = new JTextPane();
		txtLogs.setBounds(0, 0, 594, 305);
		logsTab.add(txtLogs);
		btnActualizarLogs.addActionListener(controller);
		
		
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

	public JButton getBtnCommit() {
		return btnCommit;
	}

	public JTable getTableUser() {
		return tableUser;
	}

	public JButton getBtnActualizarInventario() {
		return btnActualizarInventario;
	}

	public JTextPane getTxtLogs() {
		return txtLogs;
	}

	public void setTxtLogs(JTextPane txtLogs) {
		this.txtLogs = txtLogs;
	}

	public JButton getBtnActualizarLogs() {
		return btnActualizarLogs;
	}

	public void setBtnActualizarLogs(JButton btnActualizarLogs) {
		this.btnActualizarLogs = btnActualizarLogs;
	}
	
	
	
}
