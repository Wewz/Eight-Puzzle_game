package application;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class EightPuzzleGameStage {
	
	public static final int WINDOW_HEIGHT = 600;
	public static final int WINDOW_WIDTH = 600;

	private GameTimer gametimer;
	private GraphicsContext textGC;
	private Canvas textCanvas;

	private Scene scene;
	private Canvas canvas;
	private GraphicsContext gc;
	private StackPane root;
	
	private ImageView background;
	private Image bg;
	
	// class constructor
	public EightPuzzleGameStage() {
		this.root = new StackPane();
		this.bg = new Image("Background.png", EightPuzzleGameStage.WINDOW_WIDTH, EightPuzzleGameStage.WINDOW_HEIGHT, false, false);
		this.createBackgroundImage();
	}

	public void setStage(Stage stage) {
		
		stage.setTitle("Eight Puzzle Game");	
		stage.show();

		// set the scene
		this.scene = new Scene(this.root, EightPuzzleGameStage.WINDOW_WIDTH, EightPuzzleGameStage.WINDOW_HEIGHT);
		this.scene.setFill(Color.web("#181734"));
		
		// create the canvas for the display text in the Game
		this.textCanvas = new Canvas(EightPuzzleGameStage.WINDOW_WIDTH, EightPuzzleGameStage.WINDOW_HEIGHT);
		this.textGC = textCanvas.getGraphicsContext2D();
		
		// Text font, size, and color
		Font theFont = Font.font("Century Gothic", FontWeight.MEDIUM, 20);
		this.textGC.setFont(theFont);
		this.textGC.setFill(Color.WHITE);
		
		// create/set the background image for the Game Stage
		this.background = this.createBackgroundImage();
		// add the background to the StackPane root
		this.root.getChildren().addAll(this.background);
		
		// create a new StackPane to add the in-game elements 
		StackPane stackpane = new StackPane();
		stackpane.setAlignment(Pos.CENTER);
		
		// set the needed elements for the Game Stage
		this.canvas = new Canvas(EightPuzzleGameStage.WINDOW_WIDTH, EightPuzzleGameStage.WINDOW_HEIGHT);	
		this.gc = canvas.getGraphicsContext2D();
		stackpane.getChildren().addAll(this.canvas);
		this.root.getChildren().addAll(stackpane);
		
		stage.setScene(this.scene); 
		
		// add the canvas for text to the root
		this.root.getChildren().addAll(this.textCanvas);
		
		//instantiate an animation timer
		this.gametimer = new GameTimer(this.gc,this.scene, this.textGC, this.root, stage);
				
		// start the animation timer
		this.gametimer.start();
	}
	
	private ImageView createBackgroundImage() {
		ImageView background = new ImageView();
		background.setImage(this.bg);
		return background;
	}
}
