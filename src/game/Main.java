package game;

import javafx.scene.paint.Color;
import java.util.Random;
import javafx.application.Application;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.stage.Popup;
import javafx.scene.control.Alert.*;

@SuppressWarnings("restriction")
public class Main extends Application{

	public static int WIDTH;
	public static int HEIGHT;
	public static int numPlayers;
	public static int turn;
	public static int rows, columns;
	public static int rowHeight, columnWidth;
	public static Scene scene;
	public static Cell[][] cells;
	public static int[][] grid;
	public Color[] turnColors;
	public static boolean isHost;
	public static String hostIP;
	public static int seed;
	public static int me;
	public static AnimationTimer looper;
	public static Stage s;
	public static Client c;
	@Override
	public void start(Stage screen) throws Exception {  //most of this should move to a seperate class
		s = screen;
		Srv h;
		if(isHost) {
			h = new Srv(12345);
			h.start();
		}
//		else {
			c = new Client();
			c.connectToHost(hostIP,12345);
			c.setBasicInfo();
			c.setupLoop();
//		}
		Random r = new Random(seed);
		turnColors = new Color[numPlayers];
		for(int i=0; i<numPlayers; i++) {
			turnColors[i] = Color.color(r.nextDouble(),r.nextDouble(),r.nextDouble());
		}
		
		columnWidth = WIDTH/columns;
		rowHeight = HEIGHT/rows;
		
		GridPane board = new GridPane();
		cells = new Cell[rows][columns];
		grid = new int[rows][columns];
		for(int i=0;i<columns;i++) {
			for(int j=0;j<rows;j++) {
				grid[i][j]=-100;
			}
		}
		for(int i=0;i<rows;i++) {			
			for(int j=0;j<columns;j++) {
				cells[i][j]= new Cell(i,j);
				cells[i][j].setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY , BorderWidths.DEFAULT)));
				cells[i][j].setMinSize(columnWidth, rowHeight);
				cells[i][j].setOnMouseClicked(e -> {
					Cell source = ((Cell)e.getSource());
					if(valid(source.x,source.y)&& turn==me) {
						source.addCircle();
						grid[source.x][source.y]=turn;
						turn = (turn+1) %numPlayers;
						c.sendTurn(source.x,source.y);
						if(checkWin()) {
							Main.looper.stop();
							displayWin();
						}
						screen.setTitle("Conect 4 - Player "+me+" - Player "+turn+"'s turn");
					}
				});
				
				board.add(cells[i][j], j, i);
			}
		}
		
		screen.setTitle("Conect 4 - Player "+me+" - Player "+turn+"'s turn");
		scene = new Scene(board,WIDTH,HEIGHT);
		
		screen.setScene(scene);
		
		screen.show();
		//gameloop 
		looper = new AnimationTimer() {
			@Override
			public void handle(long now) {
				//loop();
				try {
					if(turn != me) {
						String turnInfo=c.getTurn();
						if(turnInfo.equals("NA")) {
							return;
						}
						else if(me!= turn) {
							c.sendTurn(turnInfo);
							screen.setTitle("Conect 4 - Player "+me+" - Player "+turn+"'s turn");
						}
						else if(turn==me) {
							screen.setTitle("Conect 4 - Player "+me+" - Your turn");
						}
						if(checkWin()) {
							displayWin();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(44);
				}
			}
		};
		
		looper.start();

	}
	public void displayWin() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("WIN");
//		alert.setHeaderText("Look, an Error Dialog");
		alert.setContentText("Player "+Main.turn+" Won");
		alert.setOnCloseRequest(e->{
			looper.stop();
			turn = -1;
			c.close();
			s.close();
		});
		alert.show();
	}
	private boolean valid(int i, int j) {
		return grid[i][j]==-100;
		
	}
	public boolean checkWin() { //maybe distribute this
		boolean v,h,d,rd;
		v = checkVertical();
		h = checkHorizontal();
		d = checkDiag();
		rd = cheackRevDiag();
		System.out.println(v || h|| d || rd);
		return (v|| h || d || rd);
	}

	private boolean cheackRevDiag() {//paralellize this
		for(int i=3;i<rows;i++) {
			for(int j=0;j<columns-3;j++) {
				if(grid[i][j]==grid[i-1][j+1] && grid[i-1][j+1]==grid[i-2][j+2] && grid[i-2][j+2]==grid[i-3][j+3] && grid[i][j]!=-100) {
						return true;
				}
			}
		}
		return false;
	}
	private boolean checkDiag() {//paralellize this
		for(int i=0;i<rows-3;i++) {
			for(int j=0; j<columns-3;j++) {
				if(grid[i][j]==grid[i+1][j+1] && grid[i+1][j+1]==grid[i+2][j+2] && grid[i+2][j+2]==grid[i+3][j+3] && grid[i][j]!=-100) {
					return true;
				}
			}
		}
		return false;
	}
	private boolean checkHorizontal() { //paralellize this
		//boolean win =false;
		int[] last3;
		last3 = new int[3];
		for(int i=0;i<rows;i++) {
			for(int j=0;j<columns;j++) {
				if(j<3) { //could be unrolled for efficiency. not that it rly matters
					last3[j]=grid[i][j];
				}
				else if(j>=rows) {break;}
				else if(grid[i][j] == -100 && j+3 < columns) { //empty
					last3[0]=grid[i][j+1];
					last3[1]=grid[i][j+2];
					last3[2]=grid[i][j+3];
					j+=3;
				}
				else if(last3[2] != grid[i][j] && j+2 < columns) {
					last3[0]=grid[i][j];
					last3[1]=grid[i][j+1];
					last3[2]=grid[i][j+2];
					j+=2;
				}
				else if(last3[1] != last3[2] && j+1 <columns) {
					last3[0]=last3[2];
					last3[1]=grid[i][j];
					last3[2]=grid[i][j+1];
					j++;
				}
				else if(last3[0] != last3[1]) {
					last3[0]=last3[1];
					last3[1]=last3[2];
					last3[2]=grid[i][j];
				}
				else if(last3[0] != -100) {
					return true;
				}
				else {
					//error
				}
			}
		}
		return false;
		
	}
	private boolean checkVertical() {//paralellize this
		//boolean win =false;
		int[] last3;
		last3 = new int[3];
		for(int j=0;j<columns;j++) {
			for(int i=0;i<rows;i++) {
				if(i<3) { //could be unrolled for efficiency. not that it rly matters
					last3[i]=grid[i][j];
				}
				else if(i>=rows) {break;}
				else if(grid[i][j] == -100 && i+3 < rows) { //empty
					last3[0]=grid[i+1][j];
					last3[1]=grid[i+2][j];
					last3[2]=grid[i+3][j];
					i+=3;
				}
				else if(last3[2] != grid[i][j] && i+2 < rows) {
					last3[0]=grid[i][j];
					last3[1]=grid[i+1][j];
					last3[2]=grid[i+2][j];
					i+=2;
				}
				else if(last3[1] != last3[2] && i+1 <rows) {
					last3[0]=last3[2];
					last3[1]=grid[i][j];
					last3[2]=grid[i+1][j];
					i++;
				}
				else if(last3[0] != last3[1]) {
					last3[0]=last3[1];
					last3[1]=last3[2];
					last3[2]=grid[i][j];
				}
				else if(last3[0] != -100) {
					return true;
				}
				else {
					//error
				}
			}
		}
		return false;
	}
	protected class Cell extends StackPane{
		public int x,y;
		
		public Cell(int x, int y) {
			super();
			this.x = x;
			this.y = y;
		}
		public void addCircle() {
			getChildren().add(new Circle(15, turnColors[turn]));
		}
	}
	

	public static void main(String[] args) {
		WIDTH=400;
		HEIGHT=400; 
		seed=2;
		if(args.length < 1) {
			//error
		}
		isHost = Boolean.parseBoolean(args[0]);
		if(isHost) {
			numPlayers = Integer.parseInt(args[1]);
			rows = Integer.parseInt(args[2]);
			columns = Integer.parseInt(args[3]);
		}
		else {
			hostIP = args[1];
		}
		launch(args);

	}
}
