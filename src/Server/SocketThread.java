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
    
    private Socket socket;
    private Server main;
    private DataInputStream dis;
    private StringTokenizer st;
    private String client;
    
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
                String username = "";
                String from = "";
                String to = "";
                String msg = "";
                String file_name = "";
                String CMD = st.nextToken();
                String length = "";
                
                Socket skfrom = null;
                Socket skto = null;
                System.out.println(data);
                
                /** Check CMD **/
                switch(CMD){
                    case Comand.CMD_LOG_IN:
                    	/*
                    	 * Format: CMD_LOG_IN username
                    	 */
                       
                        username = st.nextToken();
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
                    	/*
                    	 * Format: CMD_LOG_OUT username
                    	 */
                        username = st.nextToken();
                        client = username;
                        main.removeClient(username);
                        main.appendMessage("[Client]: "+ username +" left from chatroom.!");
                        updateOnline();
                        break;
                        
                    case Comand.CMD_CHAT_ONE:
                      
                    	/*
                    	 * Format: CMD_CHAT_ONE from to message
                    	 */
                    	
                        from = st.nextToken();
                        to = st.nextToken();
                        System.out.println(from+ "  "+to);
                        msg = "";
                        while(st.hasMoreTokens()){
                            msg = msg +" "+ st.nextToken();
                        }
                        Socket tsoc = main.getClient(to);
                        if(tsoc == null) break;
                        String content = from +" "+ msg;
                        sendMessage(tsoc, Comand.CMD_MESSAGE_CHAT, content);
                        main.appendMessage("[Message]: From "+ from +" To "+ to +" "+ msg);
                        break;
                    
                    case Comand.CMD_CHAT_ALL:
                    	/*
                    	 * Format: CMD_CHAT_ALL from message
                    	 */
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
                        
                    case Comand.CMD_REQUEST_SEND_FILE:
                    	/*
                    	 * Format: CMD_REQUEST_SEND_FILE filename from to length
                    	 */
                        file_name = st.nextToken();
                        from = st.nextToken();
                        to = st.nextToken();
                        length = st.nextToken();
                        Socket sto = main.getClient(to);
                        if(sto != null){
                        	main.addSendFileClient(from, socket);
                        }else{
                        	sendMessage(socket, Comand.CMD_SEND_ERROR,to);
                        	break;
                        }
                        sendMessage(sto, Comand.CMD_REQUEST_SEND_FILE, file_name + " "+ from+" "+to+ " "+length);
                 
                        break;
                    case Comand.CMD_ACCEPT_RECEIVE_FILE:
                    	/*
                    	 * Format: CMD_REQUEST_RECEIVE_FILE filename from to length
                    	 */
                        file_name = st.nextToken();
                        from = st.nextToken();
                        to = st.nextToken();
                        length = st.nextToken();
                        skfrom = main.getSendFileClient(from);
                        main.addSendFileClient(to, socket);
                        sendMessage(skfrom, Comand.CMD_SEND_FILE ,file_name+" "+from+" "+to+" "+length);
                        break;

                    case Comand.CMD_DENY_RECEIVE_FILE:
                    	/*
                    	 * Format: DENY filename from to
                    	 */
                    	file_name = st.nextToken();
                        from = st.nextToken();
                        to = st.nextToken();
                        skfrom = main.getSendFileClient(from);
                        sendMessage(skfrom, Comand.CMD_DENY_RECEIVE_FILE ,file_name+" "+from+" "+to+" "+length);
                        break;
                        
                    case Comand.CMD_SEND_FILE:
                    	/*
                    	 * Format: CMD_SEND_FILE filename from to
                    	 */
                        main.appendMessage("CMD_SENDFILE : Client sending a file...");
                        file_name = st.nextToken();
                        from = st.nextToken();
                        to = st.nextToken();
                        length = st.nextToken();
                        /**  Get the client Socket **/
                        main.appendMessage("CMD_SENDFILE : preparing connections..");
                        Socket socketTo = main.getSendFileClient(to);
                        Socket socketFrom = main.getSendFileClient(from);
                        InputStream input;
                        OutputStream sendFile;
                        if(socketTo != null && socketTo != null){ 
                            try {
                                main.appendMessage("[CMD_SEND_FILE]: Connected..!");
                                
                                main.appendMessage("[CMD_SEND_FILE]: Sending file to client...");
                                sendMessage(socketTo, Comand.CMD_SEND_FILE, file_name+" "+from+" "+to+" "+length);
                                
                                input = socketFrom.getInputStream();
                                sendFile = socketTo.getOutputStream();
                                byte[] buffer = new byte[1024];
                                int cnt;
                                while((cnt = input.read(buffer)) > 0){
                                    sendFile.write(buffer, 0, cnt);
                                    System.out.println("sending "+cnt);
                                }
                                main.removeSendFileClient(to);
                                main.removeSendFileClient(from);
                                System.out.println("sent");
                                input.close();
                                sendFile.flush();
                                sendFile.close();
                                main.appendMessage("[CMD_SEND_FILE]: File was send to client...");
                            } catch (IOException e) {
                            	main.removeSendFileClient(to);
                                main.removeSendFileClient(from);
                                System.out.println("sent");
                                main.appendMessage("[CMD_SEND_FILE]: "+ e.getMessage());
                            }
                        }else{
                          
                            main.appendMessage("[CMD_SENDFILE] : Client '"+to+"' was not found.!");
                         }                        
                        break;
                    case Comand.CMD_REQUEST_AUDIO_CALL:
                    	/*
                    	 * Format: CMD_REQUEST_AUDIO_CALL from to
                    	 */
                    	
                    	System.out.println("CMD_REQUEST_AUDIO_CALL "+from +" "+to);
                        from = st.nextToken();
                        to = st.nextToken();
                        skto = main.getClient(to);
                        if(skto != null){
                        	System.out.println("CMD_REQUEST_AUDIO_CALL add client "+from +" "+to);
                        	main.addAudioCallClient(from, socket);
                        }else{
                        	sendMessage(socket, Comand.CMD_SEND_ERROR,to);
                        	System.out.println("CMD_REQUEST_AUDIO_CALL error "+from +" "+to);
                        	break;
                        }
                        sendMessage(skto, Comand.CMD_REQUEST_AUDIO_CALL,from+" "+to);
                        break;
                        
                    case Comand.CMD_ACCEPT_AUDIO_CALL:
                    	/*
                    	 * Format: CMD_ACCEPT_AUDIO_CALL from to
                    	 */
                    	main.appendMessage("CMD_ACCEPT_AUDIO_CALL");
                        from = st.nextToken();
                        to = st.nextToken();
                        skfrom = main.getAudioCallClient(from);
                        main.addAudioCallClient(to, socket);
                        sendMessage(skfrom, Comand.CMD_SEND_AUDIO_CALL ,from+" "+to);
                        break;
                    case Comand.CMD_DENY_AUDIO_CALL:
                    	/*
                    	 * Format: CMD_DENY_AUDIO_CALL from to
                    	 */
                        from = st.nextToken();
                        to = st.nextToken();
                        skfrom = main.getAudioCallClient(from);
                        sendMessage(skfrom, Comand.CMD_DENY_AUDIO_CALL ,from+" "+to);
                        break;
                    case Comand.CMD_END_AUDIO_CALL:
                    	/*
                    	 * Format: CMD_END_AUDIO_CALL from to
                    	 */
                        from = st.nextToken();
                        to = st.nextToken();
                        skfrom = main.getClient(from);
                        skto = main.getClient(to);
                        if(skfrom!=null)
                        	sendMessage(skfrom, Comand.CMD_END_AUDIO_CALL ,from+" "+to);
                        if(skto!=null)
                        	sendMessage(skto, Comand.CMD_END_AUDIO_CALL ,from+" "+to);
                        break;
                        
                    case Comand.CMD_SEND_AUDIO_CALL:
                    	/*
                    	 * Format: CMD_SEND_AUDIO_CALL from to
                    	 */
                    	 from = st.nextToken();
                         to = st.nextToken();
                         skto = main.getAudioCallClient(to);
                         skfrom = main.getAudioCallClient(from);
                         InputStream soundIn;
                         OutputStream soundOut;
                         if(skto != null && skfrom != null){ 
                             try {
                                 main.appendMessage("[CMD_SEND_AUDIO_CALL]: Connected..!");
                                 
                                 main.appendMessage("[CMD_SEND_AUDIO_CALL]: Calling to client...");
                                 sendMessage(skto, Comand.CMD_SEND_AUDIO_CALL, from+" "+to);
                                 
                                 soundIn = skfrom.getInputStream();
                                 soundOut = skto.getOutputStream();
                                 byte[] buffer = new byte[1];
                                 int cnt;
                                 while((cnt = soundIn.read(buffer)) > 0){
                                	 soundOut.write(buffer, 0, cnt);
                                     System.out.println("sending "+cnt);
                                 }
                                 main.removeAudioCallClient(to);
                                 main.removeAudioCallClient(from);
                                 System.out.println("Ended call");
                                 soundIn.close();
                                 soundOut.flush();
                                 soundOut.close();
                                 main.appendMessage("[CMD_SEND_AUDIO_CALL]: Ended call");
                             } catch (IOException e) {
                            	  skfrom.close();
                            	  skto.close();
                            	  main.removeAudioCallClient(to);
                                  main.removeAudioCallClient(from);
                                  System.out.println("Ended call");
                                  main.appendMessage("[CMD_SEND_AUDIO_CALL]: Ended call");
                             }
                         }else{
                           
                             main.appendMessage("[CMD_SEND_AUDIO_CALL] : Client '"+to+"' was not found.!");
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

