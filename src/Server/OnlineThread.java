package Server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Enumeration;

import Server.Constant.Comand;


public class OnlineThread implements Runnable {
    Server main;
    
    public OnlineThread(Server main){
        this.main = main;
    }

    @Override
    public void run() {
        try {
            while(!Thread.interrupted()){
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
                Thread.sleep(1900);
            }
        } catch(InterruptedException e){
        } catch (IOException e) {
        }
    }
    
}
