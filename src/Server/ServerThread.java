package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class ServerThread implements Runnable {
    
    ServerSocket server;
    Server main;
    boolean keepGoing = true;
    private int port;
    
    public ServerThread(int port, Server main){
    	this.port = port;
        main.appendMessage("[Server]: Starting server in port "+ port);
        try {
            this.main = main;
            server = new ServerSocket(port);
            main.appendMessage("[Server]: Server started.!");
        } 
        catch (IOException e) { main.appendMessage("[IOException]: "+ e.getMessage()); } 
        catch (Exception e ){ main.appendMessage("[Exception]: "+ e.getMessage()); }
    }

    @Override
    public void run() {
        try {
            while(keepGoing){
                Socket socket = server.accept();
                SocketThread runnable = new SocketThread(socket, main);
                new Thread(runnable).start();
            }
        } catch (IOException e) {
            main.appendMessage("[ServerThreadIOException]: "+ e.getMessage());
        }
    }
    
    
    public void stop(){
        try {
        	keepGoing = false;
            main.appendMessage("[Server] Closing server in port "+port);
            server.close();
            
        } catch (IOException e) {
            System.out.println("Unable close server");
        }
    }
    
}
