package com.marklogic.mluser;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JTextField;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.AbstractListModel;
import javax.swing.JRadioButton;
import javax.swing.JCheckBox;
import javax.swing.ListModel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Vector;

import com.marklogic.xcc.ContentSource;
import com.marklogic.xcc.ContentSourceFactory;
import com.marklogic.xcc.Session;
import com.marklogic.xcc.Request;
import com.marklogic.xcc.ResultSequence;
import com.marklogic.xcc.exceptions.RequestException;
import com.marklogic.xcc.exceptions.XccConfigException;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.JScrollPane;

public class MLUser {

	private JFrame frame;
	private JTextField txtUsername;
	private JTextField txtPassword;
	private JList lstKnownServers;
	private JList lstApplyServers;
	private JTextField txtServerAddr;
	private JRadioButton rdbtnCreateUser;
	private JRadioButton rdbtnDeleteUser;
	private JRadioButton rdbtnResetPassword;
	private JTextField txtAdminUser;
	private JTextField txtAdminPassword;
	private JTextField txtServerPort;
	private JTextPane txtStatus;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MLUser window = new MLUser();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MLUser() {
		initialize();
	}
	
	private void addToStatus(final String msg) {
		String current = txtStatus.getText();
		if (!current.equals(""))
			current = current + "\n";
		txtStatus.setText(current + msg);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 827, 415);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblMlUser = new JLabel("ML User");
		lblMlUser.setBounds(6, 6, 152, 27);
		frame.getContentPane().add(lblMlUser);
		
		JLabel lblNewUser = new JLabel("New User");
		lblNewUser.setBounds(6, 28, 76, 41);
		frame.getContentPane().add(lblNewUser);
		
		txtUsername = new JTextField();
		txtUsername.setBounds(6, 58, 134, 28);
		frame.getContentPane().add(txtUsername);
		txtUsername.setColumns(10);
		
		lstKnownServers = new JList();
		lstKnownServers.setModel(new AbstractListModel() {
			String[] values = new String[] {};
			public int getSize() {
				return values.length;
			}
			public Object getElementAt(int index) {
				return values[index];
			}
		});
		lstKnownServers.setBounds(6, 165, 350, 100);
		frame.getContentPane().add(lstKnownServers);
		
		JLabel lblServers = new JLabel("Known Servers");
		lblServers.setBounds(6, 145, 146, 16);
		frame.getContentPane().add(lblServers);
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setBounds(6, 92, 61, 16);
		frame.getContentPane().add(lblPassword);
		
		txtPassword = new JTextField();
		txtPassword.setBounds(6, 108, 134, 28);
		frame.getContentPane().add(txtPassword);
		txtPassword.setColumns(10);
		
		JLabel lblApplyTo = new JLabel("Apply To");
		lblApplyTo.setBounds(407, 145, 61, 16);
		frame.getContentPane().add(lblApplyTo);
		
		lstApplyServers = new JList();
		lstApplyServers.setBounds(450, 165, 350, 100);
		frame.getContentPane().add(lstApplyServers);
		
		JButton btnAdd = new JButton("Add");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Vector<Object> servers = new Vector<Object>();
				int i;
				for (i = 0; i < lstApplyServers.getModel().getSize(); i++)
					servers.add(lstApplyServers.getModel().getElementAt(i));
				for (i = 0; i < lstKnownServers.getSelectedValues().length; i++)
					servers.add(lstKnownServers.getSelectedValues()[i]);
				lstApplyServers.setListData(servers);
			}
		});
		btnAdd.setBounds(360, 176, 87, 29);
		frame.getContentPane().add(btnAdd);
		
		JButton btnClear = new JButton("Clear");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lstApplyServers.setListData(new Object[0]);
			}
		});
		btnClear.setBounds(360, 211, 87, 29);
		frame.getContentPane().add(btnClear);
		
		ButtonGroup actionGroup = new ButtonGroup();
		
		rdbtnCreateUser = new JRadioButton("Create User");
		rdbtnCreateUser.setBounds(177, 60, 141, 23);
		rdbtnCreateUser.setSelected(true);
		frame.getContentPane().add(rdbtnCreateUser);
		actionGroup.add(rdbtnCreateUser);
		
		rdbtnDeleteUser = new JRadioButton("Delete User");
		rdbtnDeleteUser.setBounds(177, 84, 141, 23);
		frame.getContentPane().add(rdbtnDeleteUser);
		actionGroup.add(rdbtnDeleteUser);
		
		rdbtnResetPassword = new JRadioButton("Reset Password");
		rdbtnResetPassword.setBounds(177, 110, 141, 23);
		frame.getContentPane().add(rdbtnResetPassword);
		actionGroup.add(rdbtnResetPassword);
		
		final JCheckBox chckbxAdminRole = new JCheckBox("Admin role");
		chckbxAdminRole.setBounds(291, 60, 128, 23);
		frame.getContentPane().add(chckbxAdminRole);
		chckbxAdminRole.setSelected(true);
		
		JButton btnGo = new JButton("Go");
		btnGo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (rdbtnCreateUser.isSelected())
					createUser(txtUsername.getText(), txtPassword.getText(), chckbxAdminRole.isSelected(), lstApplyServers.getModel());
				else if (rdbtnDeleteUser.isSelected())
					deleteUser(txtUsername.getText(), lstApplyServers.getModel());
				else if (rdbtnResetPassword.isSelected())
					resetPassword(txtUsername.getText(), txtPassword.getText(), lstApplyServers.getModel());
			}

			private void resetPassword(String username, String password, ListModel servers) {
				URI uri = null;
				Server server = null;
				String query = "";
				for (int i = 0; i < servers.getSize(); i++) {
					try {
						server = (Server) servers.getElementAt(i);
						uri = new URI("xcc://" + server + "/Security");
						ContentSource contentSource = 
								ContentSourceFactory.newContentSource (uri);
						Session session = contentSource.newSession();

						query = "xquery version \"1.0-ml\";" +
								"import module namespace sec=\"http://marklogic.com/xdmp/security\" at \"/MarkLogic/security.xqy\";" +
								"sec:user-set-password('" + username + "', '" + password + "')";
						Request request = session.newAdhocQuery (query);

						ResultSequence rs = session.submitRequest (request);

						addToStatus("Set " + username + "'s password on " + server.getAddress());

						session.close();
					} catch (URISyntaxException e) {
						txtStatus.setText(txtStatus.getText() + e.getLocalizedMessage());
					} catch (XccConfigException e) {
						txtStatus.setText(txtStatus.getText() + e.getLocalizedMessage());
					} catch (RequestException e) {
						if (e.getMessage().equals("User does not exist"))
							addToStatus("User " + username + " does not exist on server " + server.getAddress());
						else
							addToStatus(e.getLocalizedMessage());
					}
				}
			}

			private void deleteUser(String username, ListModel servers) {
				URI uri = null;
				Server server = null;
				String query = "";
				for (int i = 0; i < servers.getSize(); i++) {
					try {
						server = (Server) servers.getElementAt(i);
						uri = new URI("xcc://" + server + "/Security");
						ContentSource contentSource = 
								ContentSourceFactory.newContentSource (uri);
						Session session = contentSource.newSession();

						query = "xquery version \"1.0-ml\";" +
								"import module namespace sec=\"http://marklogic.com/xdmp/security\" at \"/MarkLogic/security.xqy\";" +
								"sec:remove-user('" + username + "')";
						Request request = session.newAdhocQuery (query);

						ResultSequence rs = session.submitRequest (request);

						addToStatus(rs.asString() + "\nRemoved " + username + " on " + server.getAddress());

						session.close();
					} catch (URISyntaxException e) {
						txtStatus.setText(txtStatus.getText() + e.getLocalizedMessage());
					} catch (XccConfigException e) {
						txtStatus.setText(txtStatus.getText() + e.getLocalizedMessage());
					} catch (RequestException e) {
						if (e.getMessage().equals("User does not exist"))
							addToStatus("User " + username + " does not exist on server " + server.getAddress());
						else
							addToStatus(e.getLocalizedMessage());
					}
				}
			}

			private void createUser(String username, String password, boolean adminRole, ListModel servers) {
				URI uri = null;
				Server server = null;
				String adminRoleStr;
				if (adminRole)
					adminRoleStr = "admin";
				else
					adminRoleStr = "()";
				String query = "";
				for (int i = 0; i < servers.getSize(); i++) {
					try {
						server = (Server) servers.getElementAt(i);
						uri = new URI("xcc://" + server + "/Security");
						ContentSource contentSource = 
								ContentSourceFactory.newContentSource (uri);
						Session session = contentSource.newSession();

						query = "xquery version \"1.0-ml\";" +
								"import module namespace sec=\"http://marklogic.com/xdmp/security\" at \"/MarkLogic/security.xqy\";" +
								"sec:create-user('" + username + "', '" + username + "', '" + password + "' ,'" + adminRoleStr + "', (), ())";
						Request request = session.newAdhocQuery (query);

						ResultSequence rs = session.submitRequest (request);

						addToStatus(rs.asString());
						addToStatus("Created " + username + " on " + server);

						session.close();
					} catch (URISyntaxException e) {
						txtStatus.setText(txtStatus.getText() + e.getLocalizedMessage());
					} catch (XccConfigException e) {
						txtStatus.setText(txtStatus.getText() + e.getLocalizedMessage());
					} catch (RequestException e) {
						if (e.getMessage().equals("User already exists"))
							addToStatus("User " + username + " already exists on server " + server.getAddress());
						else
							addToStatus(e.getLocalizedMessage());
					}
				}
			}
		});
		btnGo.setBounds(441, 59, 117, 29);
		frame.getContentPane().add(btnGo);
		
		JLabel lblNewServer = new JLabel("New Server");
		lblNewServer.setBounds(6, 277, 95, 16);
		frame.getContentPane().add(lblNewServer);
		
		txtServerAddr = new JTextField();
		txtServerAddr.setBounds(6, 322, 288, 28);
		frame.getContentPane().add(txtServerAddr);
		txtServerAddr.setColumns(10);
		
		JButton btnAddServer = new JButton("Add Server");
		btnAddServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Vector<Server> servers = new Vector<Server>();
				int i;
				for (i = 0; i < lstKnownServers.getModel().getSize(); i++)
					servers.add((Server)lstKnownServers.getModel().getElementAt(i));
				servers.add(
						new Server(
								txtServerAddr.getText(), 
								Integer.parseInt(txtServerPort.getText(), 10), 
								txtAdminUser.getText(), 
								txtAdminPassword.getText()));
				lstKnownServers.setListData(servers);
				storeServers(servers);
			}
		});
		btnAddServer.setBounds(6, 350, 117, 29);
		frame.getContentPane().add(btnAddServer);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(6, 135, 815, 12);
		frame.getContentPane().add(separator);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(6, 270, 815, 12);
		frame.getContentPane().add(separator_1);
		
		JLabel lblAddress = new JLabel("Address");
		lblAddress.setBounds(6, 305, 61, 16);
		frame.getContentPane().add(lblAddress);
		
		JLabel lblAdminUser = new JLabel("Admin User");
		lblAdminUser.setBounds(397, 305, 78, 16);
		frame.getContentPane().add(lblAdminUser);
		
		JLabel lblAdminPassword = new JLabel("Admin Password");
		lblAdminPassword.setBounds(504, 305, 109, 16);
		frame.getContentPane().add(lblAdminPassword);
		
		txtAdminUser = new JTextField();
		txtAdminUser.setText("admin");
		txtAdminUser.setBounds(396, 322, 95, 28);
		frame.getContentPane().add(txtAdminUser);
		txtAdminUser.setColumns(10);
		
		txtAdminPassword = new JTextField();
		txtAdminPassword.setBounds(504, 322, 134, 28);
		frame.getContentPane().add(txtAdminPassword);
		txtAdminPassword.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Port");
		lblNewLabel.setBounds(298, 305, 61, 16);
		frame.getContentPane().add(lblNewLabel);
		
		txtServerPort = new JTextField();
		txtServerPort.setBounds(297, 322, 87, 28);
		frame.getContentPane().add(txtServerPort);
		txtServerPort.setColumns(10);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(450, 92, 354, 41);
		frame.getContentPane().add(scrollPane_1);
		
		txtStatus = new JTextPane();
		txtStatus.setEditable(false);
		scrollPane_1.setViewportView(txtStatus);
		
		Vector<Server> servers = loadKnownServers();
		lstKnownServers.setListData(servers);
	}

	private Vector<Server> loadKnownServers() {
		Vector<Server> servers = new Vector<Server>();
		try {
			FileInputStream fileIn =
					new FileInputStream("servers.ser");
	        ObjectInputStream in = new ObjectInputStream(fileIn);
	        int count = in.readInt();
	        for (int i = 0; i < count; i++) {
	        	servers.add((Server) in.readObject());
	        }
	        in.close();
	        fileIn.close();
	      }
		catch(FileNotFoundException e) {
			// This is okay. Just means nothing has been saved yet. 
		}
		catch(IOException i) {
	         i.printStackTrace();
	    }
		catch(ClassNotFoundException c) {
	         System.out.println("Server class not found");
	         c.printStackTrace();
	    }
		return servers;
	}

	private void storeServers(Vector<Server> servers) {
		try {
	        FileOutputStream fileOut =
	        		new FileOutputStream("servers.ser");
	        ObjectOutputStream out =
	                new ObjectOutputStream(fileOut);
	        out.writeInt(servers.size());
	        for (Server s : servers)
	        	out.writeObject(s);
	        out.close();
	        fileOut.close();
	      }
		catch(IOException i) {
	          i.printStackTrace();
	      }
	}
}
