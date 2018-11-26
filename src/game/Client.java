
import java.io.*;
import java.net.*;


public class Client {
	
	public Socket socket,left,right;
	public ServerSocket myHost;
	public int port;
	BufferedReader from_server; 
	PrintWriter to_server;
	BufferedReader from_Left; 
	PrintWriter to_Left;
	BufferedReader from_Right; 
	PrintWriter to_Right; 
	public Client() {
		
	}
	public void close() {
		//TODO
	}
	public void getTurn() {
		boolean read =false;
		int i,j;
		String turnInfo="";
		try {
			while(!read) {
				if((turnInfo = from_Left.readLine())!=null) {
					read=true;
				}
				else if((turnInfo = from_Right.readLine())!=null) {
					read=true;
				}
			}
			String[] in = turnInfo.split(" ");
			Main.turn = Integer.parseInt(in[0]);
			i = Integer.parseInt(in[1]);
			j = Integer.parseInt(in[2]);
			//update grid
			Main.grid[i][j]=Main.turn;
			Main.cells[i][j].addCircle();
			//checkwin
		}
		catch(Exception e) {
			e.printStackTrace();
			System.exit(8);
		}
	}
	public void sendTurn() {
		
	}
	public void connectToHost(String host, int port) {
		try {
			this.port = port;
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
			String input = from_server.readLine();
			String[] in = input.split(" ");
			Main.numPlayers = Integer.parseInt(in[0]);
			Main.me = Integer.parseInt(in[1]);
			Main.columns = Integer.parseInt(in[2]);
			Main.rows = Integer.parseInt(in[3]);
			Main.seed = Integer.parseInt(in[4]);
		}
		catch(Exception e) {
			e.printStackTrace();
			System.exit(4);
		}
		System.out.println("got info");
	}
	public void connectRight(boolean isHost, int port,String host) {
		try {
			if(isHost){
					myHost =  new ServerSocket(port,1);
					right = myHost.accept();
			}
			else {
					right = new Socket();
					right.connect(new InetSocketAddress(host,port),10);
				
			}
	
			from_Right = new BufferedReader(new InputStreamReader(right.getInputStream()));  
			to_Right = new PrintWriter(right.getOutputStream()); 
			right.setSoTimeout(10);
		}
		catch(Exception e) {
			e.printStackTrace();
			System.exit(6);
		}
	}
	public void connectLeft(boolean isHost, int port,String host) {
		try {
			if(isHost){
					myHost =  new ServerSocket(port,1);
					left = myHost.accept();
			}
			else {
				left = new Socket();
				left.connect(new InetSocketAddress(host,port),10);
				
			}
	
			from_Left = new BufferedReader(new InputStreamReader(left.getInputStream()));  
			to_Left = new PrintWriter(left.getOutputStream()); 
			left.setSoTimeout(14);
		}
		catch(Exception e) {
			e.printStackTrace();
			System.exit(12);
		}
	}
	public void setupLoop() {
		try {
			String input = from_server.readLine();
			System.out.println("right "+input);
			String[] in = input.split(" ");
			boolean isHost = Boolean.parseBoolean(in[0]);
			int port = Integer.parseInt(in[1]);
			String host = in[2];
			connectRight(isHost,port,host);
			input = from_server.readLine();
			System.out.println("left "+input);
			in = input.split(" ");
			isHost = Boolean.parseBoolean(in[0]);
			port = Integer.parseInt(in[1]);
			host = in[2];
			connectLeft(isHost,port,host);
		} 
		catch(Exception e) {
			e.printStackTrace();
			System.exit(14);
		}
	}
	
}
