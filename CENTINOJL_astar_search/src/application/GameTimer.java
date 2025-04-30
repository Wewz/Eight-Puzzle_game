package application;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/*
 * The GameTimer is a subclass of the AnimationTimer class. It must override the handle method. 
 */
 
public class GameTimer extends AnimationTimer {
	private StackPane gameRoot;
	
	private GridPane gridpane;
	
	@SuppressWarnings("unused")
	private Stage stage;
	private GraphicsContext gc;
	private GraphicsContext textGC;
	private Scene theScene;
	
	private ArrayList<Card> cardList;
	
	private SearchAlgo asnwer;
	private ArrayList<SearchAlgo> frontier;
	private ArrayList<SearchAlgo> explored;
	private ArrayList<Integer> outcome;
	
	public static final int START_NUM_ENEMIES = 10; 	
	public static final int NEW_NUM_ENEMIES = 3;   		
	private boolean solvability;	
	private boolean canClick = true;
	private boolean showSolution = false;
	private boolean buttonClickable = true;
	
	private int zeroArrPos;
	private int zeroPosX;
	private int zeroPosY;
	
	private int adjArrPos;
	private int adjPosX;
	private int adjPosY;
	
	private long startGame;
	@SuppressWarnings("unused")
	private long currentTime;
	
	private Button dfs;
	private Button bfs;
	
	@SuppressWarnings("rawtypes")
	GameTimer(GraphicsContext gc, Scene theScene, GraphicsContext textGC, StackPane gameRoot, Stage stage){
		this.gridpane = new GridPane();
		this.gc = gc;
		this.textGC = textGC;
		this.theScene = theScene;
		this.gameRoot = gameRoot;
		this.stage = stage;
		
		this.cardList = new ArrayList<Card>();
		this.frontier = new ArrayList<SearchAlgo>();
		this.explored = new ArrayList<SearchAlgo>();
		
		this.outcome = new ArrayList<Integer>();
		this.startGame = System.nanoTime();	//get current nanotime
		
		try {
			this.generateCardArrangement();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.generateAnswer();
		this.checkSolvability();
		this.addButtons();
		this.setClickable();
	}

	@Override
	public void handle(long currentNanoTime) {
		
		// clear/reset the mage, enemies, bullets, and power ups graphics context
		this.gc.clearRect(0, 0, EightPuzzleGameStage.WINDOW_WIDTH, EightPuzzleGameStage.WINDOW_HEIGHT);
		// clear/reset the displayed texts graphics context
		this.textGC.clearRect(0, 0, EightPuzzleGameStage.WINDOW_WIDTH, EightPuzzleGameStage.WINDOW_HEIGHT);
		
		// get the time current seconds
		long currentSec = TimeUnit.NANOSECONDS.toSeconds(currentNanoTime);
		// get the time the game started in seconds
		long startSec = TimeUnit.NANOSECONDS.toSeconds(this.startGame);
		// compute the current elapse time 
		this.currentTime = currentSec- startSec;
		// render the cards to appropriate positions
		this.renderCards();
		
		if(!this.solvability) this.endGame(); 
		this.displayGameInfo();
		
		this.checkClicked();
		this.checkButtonClicked();
		this.handleMouseClickEvent(this.cardList);
		this.swapPos();
		this.checkWin();
		this.showSolution();
	}
	
	private void showSolution() {
		
		if(this.showSolution) {
			String temp = "";
			
			for(int i=0; i<this.asnwer.getPrevMoves().size(); i++) {
				temp += this.asnwer.getPrevMoves().get(i);
			}
			
			this.textGC.fillText("Solution:", 50, 550);
			this.textGC.fillText(temp, 150, 550);
		}
	}
	
	private SearchAlgo AStarSearch() throws FileNotFoundException {
		
		if(this.frontier.size() != 0) {
			this.showSolution = false;
			this.frontier.clear();
		}
		if(this.explored.size() != 0) {
			this.showSolution = false;
			this.explored.clear();
		}
	
	   File file = new File("C:\\Users\\HP\\Documents\\puzzle.in");
       Scanner sc = new Scanner(file);
        
       ArrayList<Integer> temp3 = new ArrayList<Integer>();
 
       for(int i=1; i<10; i++) {
    	   
    	   int value = Integer.parseInt(sc.next());
    	   temp3.add(value);     	   
       }
       
       sc.close();
       
       ArrayList<String> temp4 = new  ArrayList<String>();
       SearchAlgo firstState = new SearchAlgo(temp3, 0, temp4);
       firstState.setH(this.setH(firstState.getState()));
       this.frontier.add(firstState);
        
       while(this.frontier.size() != 0) {
			
			SearchAlgo currentState = this.frontier.get(0);
			
			int i;
			for(i=0; i<(this.frontier.size()-1); i++) {
				this.frontier.set(i, this.frontier.get(i+1));
			}
			
			this.frontier.remove(i);
			this.explored.add(currentState);
			
			while(this.frontier.size() != 0) {
				// here ---------------------
			}
			
			
			
			if(this.GoalTest(currentState.getState())) return currentState;
			else
				for(String moves : this.Action(currentState.getState())) {
					boolean explored = false;
					
					for(SearchAlgo exploredState : this.explored) {
						int j, count = 0;
					
						for(j=0; j<exploredState.getState().size(); j++)
							if(Result(currentState.getState(), moves).get(j) == exploredState.getState().get(j))
								count++;
						
						if(count == 9) {
							explored = true;
							break;
						}
					}
					
					if(!explored) {
						ArrayList<String> temp2 = new ArrayList<String>(currentState.getPrevMoves());
						temp2.add(moves);
						SearchAlgo temp = new SearchAlgo(this.Result(currentState.getState(), moves), currentState.getPathCost() + 1, temp2);
						
						ArrayList<SearchAlgo> frontierTemp = new ArrayList<SearchAlgo>(this.frontier);
						this.frontier.clear();
						this.frontier.add(temp);
						
						for(int j=0; j<frontierTemp.size(); j++) {
							this.frontier.add(frontierTemp.get(j));
						}
						
					}
				}
		}
		return null;
	}
	
	private int setH(ArrayList<Integer> state) {
		
		int h=0;
		
		for(int i=0; i<this.outcome.size(); i++) {
			
			if(this.outcome.get(i) == 0) continue;
			
			for(int j=0; j<3; j++) {
				if(i==0) {
					if(this.outcome.get(i) == state.get(j)) {
						if(j == 1 || j == 3) h+= 1;
						if(j == 2 || j == 4 || j == 6) h+= 2;
						if(j == 5 || j == 7) h+= 3;
						if(j == 8) h+= 4;
					}
				}
				else if(i==1) {
					if(this.outcome.get(i) == state.get(j)) {
						if(j == 0 || j == 2 || j == 4) h+= 1;
						if(j == 3 || j == 5 || j == 7) h += 2;
						if(j == 6 || j == 8) h += 3;
					}
				}
				else if(i==2) {
					if(this.outcome.get(i) == state.get(j)) {
						if(j == 1 || j == 5) h+= 1;
						if(j == 0 || j == 4 || j == 8) h+= 2;
						if(j == 3 || j == 7) h+= 3;
						if(j == 6) h+= 4;
					}
				}
				else if(i==3) {
					if(this.outcome.get(i) == state.get(j)) {
						if(j == 0 || j == 4 || j == 6) h+= 1;
						if(j == 1 || j == 5 || j == 7) h+= 2;
						if(j == 2 || j == 8) h+= 3;
					}
				}
				else if(i==4) {
					if(this.outcome.get(i) == state.get(j)) {
						if(j == 1 || j == 3 || j == 5 || j == 7) h+= 1;
						if(j == 0 || j == 2 || j == 6 || j == 8) h+= 2;
					}
				} 
				else if(i==5) {
					if(this.outcome.get(i) == state.get(j)) {
						if(j == 2 || j == 4 || j == 8) h+= 1;
						if(j == 1 || j == 3 || j == 7) h+= 2;
						if(j == 0 || j == 8) h+= 3;
					}
				} 
				else if(i==6) {
					if(this.outcome.get(i) == state.get(j)) {
						if(j == 3 || j == 7) h+= 1;
						if(j == 0 || j == 4 || j == 8) h+= 2;
						if(j == 1 || j == 5) h+= 3;
						if(j == 2) h+= 4;
					}
				} 
				else if(i==7) {
					if(this.outcome.get(i) == state.get(j)) {
						if(j == 6 || j == 8 || j == 4) h+= 1;
						if(j == 1 || j == 3 || j == 5) h+= 2;
						if(j == 0 || j == 2) h+= 3;
					}
				} 
			}
		}
		
		return h;
	}
	
	private SearchAlgo DFSearch() throws FileNotFoundException {
		
			if(this.frontier.size() != 0) {
				this.showSolution = false;
				this.frontier.clear();
			}
			if(this.explored.size() != 0) {
				this.showSolution = false;
				this.explored.clear();
			}
		
		   File file = new File("C:\\Users\\HP\\Documents\\puzzle.in");
	       Scanner sc = new Scanner(file);
	        
	       ArrayList<Integer> temp3 = new ArrayList<Integer>();
	 
	       for(int i=1; i<10; i++) {
	    	   
	    	   int value = Integer.parseInt(sc.next());
	    	   temp3.add(value);     	   
	       }
	       
	       sc.close();
	       
	       ArrayList<String> temp4 = new  ArrayList<String>();
	       SearchAlgo firstState = new SearchAlgo(temp3, 0, temp4);
	       this.frontier.add(firstState);
			
	       //int jaja=0;
	        
	       while(this.frontier.size() != 0) {
				
				SearchAlgo currentState = this.frontier.get(0);
				
				int i;
				for(i=0; i<(this.frontier.size()-1); i++) {
					this.frontier.set(i, this.frontier.get(i+1));
				}
				
				this.frontier.remove(i);
				this.explored.add(currentState);
				
				if(this.GoalTest(currentState.getState())) return currentState;
				else
					for(String moves : this.Action(currentState.getState())) {
						boolean explored = false;
						
						for(SearchAlgo exploredState : this.explored) {
							int j, count = 0;
						
							for(j=0; j<exploredState.getState().size(); j++)
								if(Result(currentState.getState(), moves).get(j) == exploredState.getState().get(j))
									count++;
							
							if(count == 9) {
								explored = true;
								break;
							}
						}
						
						if(!explored) {
							ArrayList<String> temp2 = new ArrayList<String>(currentState.getPrevMoves());
							temp2.add(moves);
							SearchAlgo temp = new SearchAlgo(this.Result(currentState.getState(), moves), currentState.getPathCost() + 1, temp2);
							
							ArrayList<SearchAlgo> frontierTemp = new ArrayList<SearchAlgo>(this.frontier);
							this.frontier.clear();
							this.frontier.add(temp);
							
							for(int j=0; j<frontierTemp.size(); j++) {
								this.frontier.add(frontierTemp.get(j));
							}
							
						}
					}
			}
			return null;
		}
	
	private SearchAlgo BFSearch() throws FileNotFoundException {
		
		if(this.frontier.size() != 0) {
			this.showSolution = false;
			this.frontier.clear();
		}
		if(this.explored.size() != 0) {
			this.showSolution = false;
			this.explored.clear();
		}
		
	   File file = new File("C:\\Users\\HP\\Documents\\puzzle.in");
       Scanner sc = new Scanner(file);
        
       ArrayList<Integer> temp3 = new ArrayList<Integer>();
 
       for(int i=1; i<10; i++) {
    	   
    	   int value = Integer.parseInt(sc.next());
    	   temp3.add(value);     	   
       }
       
       sc.close();
       
       ArrayList<String> temp4 = new  ArrayList<String>();
       SearchAlgo firstState = new SearchAlgo(temp3, 0, temp4);
       this.frontier.add(firstState);
        
       while(this.frontier.size() != 0) {
			
			SearchAlgo currentState = this.frontier.get(0);
			
			int i;
			for(i=0; i<(this.frontier.size()-1); i++) {
				this.frontier.set(i, this.frontier.get(i+1));
			}
			
			this.frontier.remove(i);
			
			this.explored.add(currentState);
			
			if(this.GoalTest(currentState.getState())) return currentState;
			else
				for(String moves : this.Action(currentState.getState())) {
					boolean explored = false;
					
					for(SearchAlgo exploredState : this.explored) {
						int j, count = 0;
					
						for(j=0; j<exploredState.getState().size(); j++)
							if(Result(currentState.getState(), moves).get(j) == exploredState.getState().get(j))
								count++;
						
						if(count == 9) {
							explored = true;
							break;
						}
					}
					
					if(!explored) {
						ArrayList<String> temp2 = new ArrayList<String>(currentState.getPrevMoves());
						temp2.add(moves);
						SearchAlgo temp = new SearchAlgo(this.Result(currentState.getState(), moves), currentState.getPathCost() + 1, temp2);
						this.frontier.add(temp);
						
					}
				}
			
			/*for(int g=0; g<this.frontier.size(); g++) {
				System.out.println("-------------------------------------------------------");
				for(int k=0; k<this.frontier.get(g).getState().size(); k++)
					System.out.println(this.frontier.get(g).getState().get(k));
			}*/
		}
		
		return null;
	}
	
	private ArrayList<Integer> Result(ArrayList<Integer> state, String move) {
		
		int i;
		
		for(i=0; i<state.size(); i++) {
			if(state.get(i) == 0)
				break;
		}
		
		ArrayList<Integer> newState = new ArrayList<Integer>();
		
		for(int j=0; j<state.size(); j++) {
			newState.add(state.get(j));
		}
		
		int temp = state.get(i);
		
		if(move.equals("U")) {
			newState.set(i, state.get(i-3));
			newState.set(i-3,temp);
		}
		else if(move.equals("R")) {
			newState.set(i, state.get(i+1));
			newState.set(i+1, temp);
			
		}
		else if(move.equals("D")) {
			newState.set(i, state.get(i+3));
			newState.set(i+3, temp);
		}
		else if(move.equals("L")) {
			newState.set(i, state.get(i-1));
			newState.set(i-1, temp);
		}
		
		return newState;

	}
	
	private boolean GoalTest(ArrayList<Integer> state) {
		int count=0;
		
		for(int i=0; i<state.size(); i++) {
			if(state.get(i) == this.outcome.get(i)) 
				count++;
		}
		
		if(count == 9)
			return true;
		
		return false;
		
		
	}
	
	private ArrayList<String> Action(ArrayList<Integer> currentState) {
		 int i;
		 
		 for(i=0; i<currentState.size(); i++) {
			 if(currentState.get(i) == 0)
				 break;
		 }
		 
		 ArrayList<String> possibleMoves = new ArrayList<String>();
		 
		 if(i == 0) {
			 possibleMoves.add("R");
			 possibleMoves.add("D");
		 }
		 else if(i==1) {
			 possibleMoves.add("R");
			 possibleMoves.add("D");
			 possibleMoves.add("L");
		 }
		 else if(i==2) {
			 possibleMoves.add("D");
			 possibleMoves.add("L");
		 }
		 else if(i==3) {
			 possibleMoves.add("U");
			 possibleMoves.add("R");
			 possibleMoves.add("D");
		 }
		 else if(i==4) {
			 possibleMoves.add("U");
			 possibleMoves.add("R");
			 possibleMoves.add("D");
			 possibleMoves.add("L");
		 }
		 else if(i==5) {
			 possibleMoves.add("U");
			 possibleMoves.add("D");
			 possibleMoves.add("L");
		 }
		 else if(i==6) {
			 possibleMoves.add("U");
			 possibleMoves.add("R");
		 }
		 else if(i==7) {
			 possibleMoves.add("U");
			 possibleMoves.add("R");
			 possibleMoves.add("L");
		 }
		 else if(i==8) {
			 possibleMoves.add("U");
			 possibleMoves.add("L");
		 }
		 
		 return possibleMoves;
	}
	
	private void checkButtonClicked() {
		
		if(this.buttonClickable) {
			
			this.dfs.setOnMouseClicked(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent event) {
					
					System.out.println("Solve DFS");
					try {
						getAnswer(DFSearch());
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		
				}
			});
			
			this.bfs.setOnMouseClicked(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent event) {
					
					System.out.println("Solve BFS");
					try {
						getAnswer(BFSearch());
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		
				}
			}); 
		}
	}
	
	private void getAnswer(SearchAlgo answer) {
		this.asnwer = answer;
		System.out.println("Yes");
		
		try {
		      FileWriter myWriter = new FileWriter("/Users/HP/Documents/puzzle.out");
		      
		      for(int i=0; i<this.asnwer.getPrevMoves().size(); i++) {
		    	  myWriter.write(asnwer.getPrevMoves().get(i));
		      }
		      
		      myWriter.close();
		      System.out.println("Successfully wrote to the file.");
	    } catch (IOException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
	    }
		
		this.showSolution = true;
	}
	
	@SuppressWarnings("static-access")
	private void addButtons() {
		
		Button bfs = new Button();
		bfs.setText("Solve BFS");
		bfs.setLayoutX(150);
		bfs.setLayoutY(10);
		bfs.setMaxSize(120, 60);
		
		Button dfs = new Button();
		dfs.setText("Solves DFS");
		bfs.setLayoutX(250);
		bfs.setLayoutY(10);
		bfs.setMaxSize(120, 60);
		
		this.gridpane.getChildren().addAll(bfs, dfs);
		this.gridpane.setAlignment(Pos.BASELINE_CENTER);
		this.gridpane.setPadding(new Insets(30, 0, 0, 0));
		this.gridpane.setMaxSize(EightPuzzleGameStage.WINDOW_WIDTH, EightPuzzleGameStage.WINDOW_HEIGHT);
		
		this.gridpane.setConstraints(dfs, 0, 0);
		this.gridpane.setConstraints(bfs, 1, 0);
		
		this.gameRoot.getChildren().addAll(this.gridpane);
		
		this.bfs = bfs;
		this.dfs = dfs;
	}
	
	// method for displaying the in game info
		private void displayGameInfo() {
			
			if(this.solvability)
				this.textGC.fillText("Solvable, You Can Do This!", 180, 100);
			else 
				this.textGC.fillText("Not Solvable", 235, 100);
		}
	
	private void updateList() {
		ArrayList<Card> temp = new ArrayList<Card>();
		
		this.cardList.get(this.adjArrPos).setX(this.zeroPosX);
		this.cardList.get(this.adjArrPos).setY(this.zeroPosY);
		
		this.cardList.get(this.zeroArrPos).setX(this.adjPosX);
		this.cardList.get(this.zeroArrPos).setY(this.adjPosY);	
		
		for(int i=0; i<this.cardList.size(); i++) {
			if(i == this.adjArrPos) {
				temp.add(this.cardList.get(this.zeroArrPos));
				continue;
			}
			
			if(i == this.zeroArrPos) {
				temp.add(this.cardList.get(this.adjArrPos));
				continue;
			}
			
			temp.add(this.cardList.get(i));
		}
		

		for(int i=(temp.size()-1); i>-1; i--) {
			this.cardList.remove(i);
		}	

		for(int i=0; i<temp.size(); i++) {
			this.cardList.add(temp.get(i));
		}	
    }
	
	private void swapPos() {
		
		if(!this.canClick) {
			
			if(this.adjPosX > this.zeroPosX) {
				this.cardList.get(this.adjArrPos).setX(this.cardList.get(this.adjArrPos).getX() - 10);
				
				if (this.cardList.get(this.adjArrPos).getX() == this.zeroPosX) {	
					this.updateList();
					this.setClickable();
					this.canClick = true;
				}
			}
			else if (this.adjPosX < this.zeroPosX) {
				this.cardList.get(this.adjArrPos).setX(this.cardList.get(this.adjArrPos).getX() + 10);
				
				if (this.cardList.get(this.adjArrPos).getX() == this.zeroPosX) {
					this.updateList();
					this.setClickable();
					this.canClick = true;
				}
			}
			else if (this.adjPosY > this.zeroPosY) {
				this.cardList.get(this.adjArrPos).setY(this.cardList.get(this.adjArrPos).getY() - 10);
				
				if (this.cardList.get(this.adjArrPos).getY() == this.zeroPosY) {			
					this.updateList();
					this.setClickable();
					this.canClick = true;
				}
			}
			else if (this.adjPosY < this.zeroPosY) {
				this.cardList.get(this.adjArrPos).setY(this.cardList.get(this.adjArrPos).getY() + 10);
				
				if (this.cardList.get(this.adjArrPos).getY() == this.zeroPosY) {
					this.updateList();
					this.setClickable();
					this.canClick = true;
				}
			}
		}
	}
	
	
	private void checkClicked() {
		
		if(this.canClick) {
			int i, j;
			
			for (i=0; i<this.cardList.size(); i++) {
				
				//System.out.println(i);
				
				if(this.cardList.get(i).getValue() == 0)
					break;
			}
			
			for (j=0; j<this.cardList.size(); j++) {
				
				if(i==0 && (j==1 || j==3)) 
					if (this.cardList.get(j).getClicked()) {
						this.swapPrep(i, j);
						break;
					} 
				
				if(i==1 && (j==0 || j==2 || j==4))
					if (this.cardList.get(j).getClicked()) {
						this.swapPrep(i, j);
						break;
					}
				
				if(i==2 && (j==1 || j==5))
					if (this.cardList.get(j).getClicked()) {
						this.swapPrep(i, j);
						break;
					}
				
				if(i==3 && (j==0 || j==4 || j==6))
					if (this.cardList.get(j).getClicked()) {
						this.swapPrep(i, j);
						break;
					}
				
				if(i==4 && (j==1 || j==3 || j==5 || j==7))
					if (this.cardList.get(j).getClicked()) {
						this.swapPrep(i, j);
						break;
					}
				
				if(i==5 && (j==2 || j==8 || j==4))
					if (this.cardList.get(j).getClicked()) {
						this.swapPrep(i, j);
						break;
					}
				
				if(i==6 && (j==3 || j==7))
					if (this.cardList.get(j).getClicked()) {
						this.swapPrep(i, j);	
						break;
					}
				
				if(i==7 && (j==4 || j==6 || j==8))
					if (this.cardList.get(j).getClicked()) {
						this.swapPrep(i, j);	
						break;
					}
				
				if(i==8 && (j==5 || j==7))
					if (this.cardList.get(j).getClicked()) {
						this.swapPrep(i, j);
						break;
					}
			}
		}
	}
	
	private void swapPrep(int i, int j) {
		this.cardList.get(j).setClicked(false);
		this.canClick = false;	
		
		this.adjArrPos = j;
		this.adjPosX = this.cardList.get(j).getX();
		this.adjPosY = this.cardList.get(j).getY();
		
		this.zeroArrPos = i;
		this.zeroPosX = this.cardList.get(i).getX();
		this.zeroPosY = this.cardList.get(i).getY();
	}

	private void handleMouseClickEvent(ArrayList<Card> cardList) {
		
		if (this.canClick) {

			this.theScene.setOnMouseClicked(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent e) {
					
					System.out.println("X: " + e.getX() + " and Y: " + e.getY());
						
					// for 1st box, 1st row
					if(e.getX() >= 130 && e.getY() >= 130  && e.getX() <= 230 && e.getY() <= 230) 
						if(cardList.get(0).getIsClickable()) 
							removePuzzleClickability(0);
					
					// for 2nd box, 1st row
					if(e.getX() >= 250 && e.getY() >= 130  && e.getX() <= 350 && e.getY() <= 230) 
						if(cardList.get(1).getIsClickable()) 
							removePuzzleClickability(1);
					
					// for 3rd box, 1st row
					if(e.getX() >= 370 && e.getY() >= 130  && e.getX() <= 470 && e.getY() <= 230) 
						if(cardList.get(2).getIsClickable()) 
							removePuzzleClickability(2);
					
					// for 1st box, 2nd row
					if(e.getX() >= 130 && e.getY() >= 250  && e.getX() <= 230 && e.getY() <= 350) 
						if(cardList.get(3).getIsClickable()) 
							removePuzzleClickability(3);
					
					// for 2nd box, 2nd row
					if(e.getX() >= 250 && e.getY() >= 250  && e.getX() <= 350 && e.getY() <= 350) 
						if(cardList.get(4).getIsClickable()) 
							removePuzzleClickability(4);	
					
					// for 2nd box, 2nd row
					if(e.getX() >= 370 && e.getY() >= 250  && e.getX() <= 470 && e.getY() <= 350) 
						if(cardList.get(5).getIsClickable()) 
							removePuzzleClickability(5);
					
					// for 1st box, 3rd row
					if(e.getX() >= 130 && e.getY() >= 370  && e.getX() <= 230 && e.getY() <= 470) 
						if(cardList.get(6).getIsClickable()) 
							removePuzzleClickability(6);
					
					// for 2nd box, 3rd row
					if(e.getX() >= 250 && e.getY() >= 370  && e.getX() <= 350 && e.getY() <= 470) 
						if(cardList.get(7).getIsClickable())  
							removePuzzleClickability(7);
					
					// for 3rd box, 3rd row
					if(e.getX() >= 370 && e.getY() >= 370  && e.getX() <= 470 && e.getY() <= 470)
						if(cardList.get(8).getIsClickable()) 
							removePuzzleClickability(8);
					
				  }
			});
		}
	}
	
	private void removePuzzleClickability(int arrPos) {
		this.cardList.get(arrPos).setClicked(true);
	}
	
	
	// create a method that will swap the position, animate it, use variables to handle it
	
	private void setClickable() {
		
		int i;
		
		for (i=0; i<this.cardList.size(); i++) {
			
			if(this.cardList.get(i).getValue() == 0)
				break;
		}
		
		for (int j=0; j<this.cardList.size(); j++) {
			
			if(i==0 && (j==1 || j==3)) 
				this.cardList.get(j).setIsClickable(true);
			else if(i==1 && (j==0 || j==2 || j==4))
				this.cardList.get(j).setIsClickable(true);
			else if(i==2 && (j==1 || j==5))
				this.cardList.get(j).setIsClickable(true);
			else if(i==3 && (j==0 || j==4 || j==6))
				this.cardList.get(j).setIsClickable(true);
			else if(i==4 && (j==1 || j==3 || j==5 || j==7))
				this.cardList.get(j).setIsClickable(true);
			else if(i==5 && (j==2 || j==8 || j==4))
				this.cardList.get(j).setIsClickable(true);
			else if(i==6 && (j==3 || j==7))
				this.cardList.get(j).setIsClickable(true);
			else if(i==7 && (j==4 || j==6 || j==8))
				this.cardList.get(j).setIsClickable(true);
			else if(i==8 && (j==5 || j==7))
				this.cardList.get(j).setIsClickable(true);
			else
				this.cardList.get(j).setIsClickable(false);
		}
	}
	
	private void renderCards() {
		
		for (Card c : this.cardList){
			c.render(this.gc);
		}
	}
	
	private void generateAnswer() {
		for(int i=1; i<(this.cardList.size()); i++) {
			this.outcome.add(i);
		}
		this.outcome.add(0);
	}
	
	@SuppressWarnings("unused")
	private void generateCardArrangement() throws FileNotFoundException {
		
        File file = new File("C:\\Users\\HP\\Documents\\puzzle.in");
        Scanner sc = new Scanner(file);
        
        int initialX = 130;
        int initialY = 130;
        int tempX = initialX;
 
       for(int i=1; i<10; i++) {
    	   
    	   int value = Integer.parseInt(sc.next());
    	   
    	   Card card = new Card(value, tempX, initialY);
    	   
    	   this.cardList.add(card);
    	   
    	   if (i%3 != 0)
    		   tempX += (Card.CARD_HEIGHT + 20);
    	   else {
    		   tempX = initialX;
    		   initialY += (Card.CARD_HEIGHT + 20);
    	   }  	     	   
       }
       
       sc.close();
	}
	
	// method that check if player win the game
	private void checkWin() {
		int i;
		for(i=0; i<this.cardList.size(); i++) {
			//System.out.println(this.cardList.get(i).getValue() + " " + this.outcome.get(i));
			
			if(this.cardList.get(i).getValue() != this.outcome.get(i)) {
				break;
			}
		}
		
		if(i == this.cardList.size())
			this.endGame();
	}
	
	private void endGame() {
		this.buttonClickable = false;
		this.stop();
	}
	
	
	private void checkSolvability() {
		int count = 0;
		for(int i=0; i<this.cardList.size(); i++) {
			
			if(this.cardList.get(i).getValue() == 0)
				continue;
			
			for(int j=(i+1); j<this.cardList.size(); j++) {
				
				if(this.cardList.get(j).getValue() == 0 || j == this.cardList.size())
					continue;
				
				if(this.cardList.get(i).getValue() > this.cardList.get(j).getValue())
					count++;
			}
		}
		
		if(count%2 !=0) this.solvability = false;
		else this.solvability = true;
	}
	
}
