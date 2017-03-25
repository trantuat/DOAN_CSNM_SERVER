package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Enumeration;
import java.util.StringTokenizer;

import Server.Constant.Comand;
import Server.Constant.Response;


public class SocketThread implements Runnable{
    
    Socket socket;
    Server main;
    DataInputStream dis;
    StringTokenizer st;
    String client, filesharing_username;
    
    public SocketThread(Socket socket, Server main){
        this.main = main;
        this.socket = socket;
        
        try {
            dis = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            main.appendMessage("[SocketThreadIOException]: "+ e.getMessage());
        }
    }
    
    @Override
    public void run() {
        try {
            while(true){
            	
                String data = dis.readUTF();
                st = new StringTokenizer(data);
                String username = null;
                String CMD = st.nextToken();
                System.out.println(data);
                
                /** Check CMD **/
                switch(CMD){
                    case Comand.CMD_LOG_IN:
                       
                        username = st.nextToken();
                        System.out.println(username);
                        client = username;
                        if(!main.isExistsUsername(client)){
                             main.addClients(username, socket);
                             sendMessage(socket, Response.SUCCESS, "");
                             main.appendMessage("[Client]: "+ username +" joined the chatroom.!");
                        }else{
                        	sendMessage(socket, Response.FAIL, "");
                        }
                
                        break;
                    case Comand.CMD_REQUEST_ONLINE:
                        updateOnline();
                        break;
                        
                    case Comand.CMD_LOG_OUT:
                        username = st.nextToken();
                        client = username;
                        main.removeClient(username);
                        main.appendMessage("[Client]: "+ username +" left from chatroom.!");
                        updateOnline();
                        break;
                        
                    case Comand.CMD_CHAT_ONE:
                        /** CMD_CHAT [from] [sendTo] [message] **/
                    	
                        String from = st.nextToken();
                        String sendTo = st.nextToken();
                        System.out.println(from+ "  "+sendTo);
                        String msg = "";
                        while(st.hasMoreTokens()){
                            msg = msg +" "+ st.nextToken();
                        }
                        Socket tsoc = main.getClient(sendTo);
                        if(tsoc == null) break;
                        try {
                            DataOutputStream dos = new DataOutputStream(tsoc.getOutputStream());
                            String content = from +" "+ msg;
                            sendMessage(tsoc, Comand.CMD_MESSAGE_CHAT, content);
                            main.appendMessage("[Message]: From "+ from +" To "+ sendTo +" "+ msg);
                        } catch (IOException e) {  main.appendMessage("[IOException]: Unable to send message to "+ sendTo); }
                        break;
                    
                    case Comand.CMD_CHAT_ALL:
                        String chatall_from = st.nextToken();
                        String chatall_msg = "";
                        while(st.hasMoreTokens()){
                            chatall_msg = chatall_msg +" "+st.nextToken();
                        }
                        String chatall_content = chatall_from +" "+ chatall_msg;
                        
                        Enumeration<String> e2 = main.clients.keys();
                        String name=null;
                		while(e2. hasMoreElements()){
                			name=(String) e2.nextElement();
                			if(!name.equals(chatall_from)){
                				Socket tsoc2 = (Socket) main.clients.get(name);
                                sendMessage(tsoc2, Comand.CMD_MESSAGE_GROUP ,chatall_content);
                			}
                		}

                        break;

                    default: 
                        main.appendMessage("[CMDException]: Unknown Command "+ CMD);
                    break;
                }
            }
        } catch (IOException e) {
        }
    }
    private void sendMessage(Socket socket, String cmd, String msg){
    	DataOutputStream dos;
		try {
			dos = new DataOutputStream(socket.getOutputStream());
			dos.writeUTF(cmd +" "+ msg);
		} catch (IOException e) {
			main.appendMessage("[ERROR SEND]: "+ e.getMessage());
		}
         
	}
    
    private void updateOnline() throws IOException{
    	 String msg = "";
         Enumeration e = main.clients.keys();
       //  String msg="";
 		while(e. hasMoreElements()){
 			msg = msg+" "+(String) e.nextElement();
 			
 		}
 		Enumeration e1 = main.clients.keys();
 		while(e1. hasMoreElements()){
 		   	 Socket tsoc =main.clients.get((String) e1.nextElement());
 			 DataOutputStream dos = new DataOutputStream(tsoc.getOutputStream());
 			 if(msg.length() > 0){
                  dos.writeUTF(Comand.CMD_ONLINE+" "+ msg);
              }
 		}
    }
    
    
}

