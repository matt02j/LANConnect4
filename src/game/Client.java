package game;

import java.io.*;
import java.net.*;


public class Client {
	
	public static Socket socket,left,right;
	BufferedReader from_server; 
	PrintWriter to_server;
//	BufferedReader from_Left; 
//	PrintWriter to_Left;
	BufferedReader from_Right; 
	PrintWriter to_Right; 
	public Client() {
		
	}
	public void connectToHost(String host, int port) {
		try {
			socket = new Socket(host,port);
			from_server = new BufferedReader(new InputStreamReader(socket.getInputStream()));  
			to_server = new PrintWriter(socket.getOutputStream());     
			System.out.println("connected");
			
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(2);
		} 
		
	}
	public void setBasicInfo() {
		try {
			Main.numPlayers = from_server.read();
			Main.me = from_server.read();
			Main.columns = from_server.read();
			Main.rows = from_server.read();
			Main.seed = from_server.read();
		}
		catch(Exception e) {
			e.printStackTrace();
			System.exit(4);
		}
	}
	public void connectRight(boolean isHost, int port,String host) {
		if(isHost){
			
		}
		else {
			try {
				right = new Socket(host,port);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(6);
			} 
		}
	}
	public void connectLeft(boolean isHost, int port,String host) {
		if(isHost){
			
		}
		else {
			try {
				left = new Socket(host,port);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(8);
			} 
		}
	}
	
}
