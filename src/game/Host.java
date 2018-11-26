package game;

import java.io.*;
import java.net.*;

public class Host extends Thread{
	
	public static int port;
	public static ServerSocket hostSocket;
	public static Socket[] sockets;
	 BufferedReader[] from_client;   
	 PrintWriter[] to_client; 
	
	public Host(int port) {
		this.port=port;

		try {
			hostSocket = new ServerSocket(port,10);
			from_client = new BufferedReader[Main.numPlayers];
			to_client = new PrintWriter[Main.numPlayers];
			sockets = new Socket[Main.numPlayers];
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println(hostSocket.getInetAddress());
	}
	
	public void run() {
		initConnections();
		setupLoop();
	}
	public void initConnections() {
		for(int i=0;i<Main.numPlayers;i++) {
			System.out.println("waiting for players");
			try {
				sockets[i] = hostSocket.accept();
				 from_client[i] = new BufferedReader(new InputStreamReader(sockets[i].getInputStream()));   
				 to_client[i] = new PrintWriter(sockets[i].getOutputStream()); 
				System.out.println("player " + i + " connected");
				to_client[i].println(Main.numPlayers+" "+i+" "+Main.columns+" "+Main.rows+" "+Main.seed);
				to_client[i].flush();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(3);
			}
		}
		try {
			hostSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(5);
		}
	}
	public void setupLoop() {
		try {
			for(int i=0;i<Main.numPlayers;i++) {
				to_client[i].println("true " + (port+i+1) +"  ");
				to_client[i].flush();
				to_client[(i+1)%Main.numPlayers].println("false " + (port+i+1) +" " +((InetSocketAddress)sockets[i].getLocalSocketAddress()).getAddress().toString().replace("/",""));
				to_client[(i+1)%Main.numPlayers].flush();				
			}
		}catch(Exception e) {
			e.printStackTrace();
			System.exit(7);
		}
	}
	public void close() {
		//TODO
	}
}
