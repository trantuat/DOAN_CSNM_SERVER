package Server;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;




public class Server implements ActionListener{ 

	private JFrame frame;
	private JTextField port;
	private JButton btnConnect;
	private JButton btnDisconnect;
	private JTextArea textArea;
	private static Server window;
	private ServerThread serverThread;
	public Hashtable<String, Socket> clients = new Hashtable<>();
	public Hashtable<String, Socket> sendFileClients = new Hashtable<>();
	public Hashtable<String, Socket> audioCallClients = new Hashtable<>();
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		 try {
			 
	            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
	                if ("Nimbus".equals(info.getName())) {
	                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
	                    break;
	                }
	            }
	        }catch(Exception e){
	        	
	        }
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					window = new Server();
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
	public Server() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 315);
	//	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JLabel lbl = new JLabel("Port : ");
		
		btnConnect = new JButton("Connect");
		btnConnect.addActionListener(this);
		port = new JTextField();
		port.setText("1234");
		port.setColumns(10);
		
		JScrollPane scrollPane = new JScrollPane();
		
		btnDisconnect = new JButton("Disconnect");
		btnDisconnect.setEnabled(false);
		btnDisconnect.addActionListener(this);
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(22)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(scrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 393, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lbl)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(port, GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnConnect)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnDisconnect, GroupLayout.PREFERRED_SIZE, 92, GroupLayout.PREFERRED_SIZE)))
					.addGap(19))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lbl)
						.addComponent(port, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnDisconnect)
						.addComponent(btnConnect))
					.addGap(10)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
					.addGap(22))
		);
		
		frame.addWindowListener(
			    new WindowAdapter() 
			    {
			        @Override
			        public void windowClosing(WindowEvent e) 
			        {
			        	if(serverThread != null)
			             	 serverThread.stop();
			        }

			    });
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);
		frame.getContentPane().setLayout(groupLayout);
	}
	/*
	 * Handle clients send message
	 */
	public void removeClient(String client){
		clients.remove(client);
	}
	
	public boolean isExistsUsername(String client){
		return clients.containsKey(client);
	}
	public void addClients(String username, Socket socket){
		clients.put(username, socket);
	}
    public Socket getClient(String client){
	     return clients.get(client);
	}
	
    /*
     * Handle clients send file
     */

	public void addSendFileClient(String username, Socket socket){
		sendFileClients.put(username, socket);
	}
	
	public void removeSendFileClient(String client){
		sendFileClients.remove(client);
	}
	
	public Socket getSendFileClient(String client){
		return sendFileClients.get(client);
	}
	
	/*
	 * Handle clients call audio
	 */
	
	public void addAudioCallClient(String username, Socket socket){
		audioCallClients.put(username, socket);
	}
	
	public void removeAudioCallClient(String client){
		audioCallClients.remove(client);
	}
	
	public Socket getAudioCallClient(String client){
		return audioCallClients.get(client);
	}
	
	public void appendMessage(String str){
		textArea.append(str+"\n");
	}
		
		
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getSource() == btnConnect){
			try{
			    serverThread = new ServerThread(Integer.parseInt(port.getText()), window);
			    new Thread(serverThread).start();
			   // new Thread(new OnlineThread(this)).start();
			    btnDisconnect.setEnabled(true);
			    btnConnect.setEnabled(false);
			    port.setEnabled(false);
			}catch(Exception e){
				JOptionPane.showMessageDialog(null, "Unvalid port!", "Error port", JOptionPane.ERROR_MESSAGE);
				 
			}
		}
		if(arg0.getSource() == btnDisconnect){
		   int confirm = JOptionPane.showConfirmDialog(null, "Close Server.?");
		   if(confirm == 0){
			   clients.clear();
			   btnDisconnect.setEnabled(false);
			   btnConnect.setEnabled(true);
			   port.setEnabled(true);
			   serverThread.stop();
		   }
		}
		
	}

}
