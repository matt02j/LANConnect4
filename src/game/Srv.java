package game;
import java.io.*;
import java.net.*;

public class Srv extends Thread{
	
	public int port;
	public static ServerSocket hostSocket;
	public static Socket[] sockets;
	 BufferedReader[] from_client;   
	 PrintWriter[] to_client; 
	
	public Srv(int port) {
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
	}
	
	public void run() {
		initConnections();
		setupLoop();
		close();
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
			to_client[0].println("true " + (port+Main.numPlayers) +" NA right");
			to_client[0].println("true " + (port+1) +" NA left");
			to_client[0].flush();
			for(int i=1;i<Main.numPlayers-1;i++) {
				to_client[i].println("true " + (port+i+1) +" NA left");
				String addr = ((InetSocketAddress)sockets[i].getRemoteSocketAddress()).getAddress().toString().replace("/","");
				if(addr.equals("127.0.0.1")){ //localhost
					addr = InetAddress.getLocalHost().getHostName();
				}
				to_client[i].println("false " + (port+i) +" " +addr +" right");
				to_client[i].flush();				
			}
			String addr = ((InetSocketAddress)sockets[Main.numPlayers-1].getRemoteSocketAddress()).getAddress().toString().replace("/","");
			if(addr.equals("127.0.0.1")){ //localhost
				addr = InetAddress.getLocalHost().getHostName();
			}
			to_client[Main.numPlayers-1].println("false " + (port+Main.numPlayers) +" "+addr+" left");
			to_client[Main.numPlayers-1].println("false " + (port+Main.numPlayers-1) +" "+addr+" right");
			to_client[Main.numPlayers-1].flush();
		}catch(Exception e) {
			e.printStackTrace();
			System.exit(7);
		}
	}
	public void close() {
		try {
			hostSocket.close();
			for(int i=0; i<Main.numPlayers;i++) {
				sockets[i].close();
			}
		}catch(Exception e) {
			e.printStackTrace();
			System.exit(33);
		}
	}
}
