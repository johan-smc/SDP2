package client;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import controllers.LoginController;

public class LoginView extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtUsername;
	private JTextField txtPassword;
	private LoginController controller;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginView frame = new LoginView();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public LoginView() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		this.initialize();
	}
	
	private void initialize() {
		this.controller = new LoginController(this);
		
		JPanel loginPanel = new JPanel();
		loginPanel.setBounds(0, 0, 450, 278);
		contentPane.add(loginPanel);
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
		

		btnLogin.addActionListener(this.controller);
	}

	public JTextField getTxtUsername() {
		return txtUsername;
	}

	public JTextField getTxtPassword() {
		return txtPassword;
	}

}
