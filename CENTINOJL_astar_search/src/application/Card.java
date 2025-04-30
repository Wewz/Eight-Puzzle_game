package application;

import javafx.geometry.Insets;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

public class Card {
	public final static int CARD_HEIGHT = 100;
	
	private int value;
	private Image cardImg;
	private int x, y;
	private boolean isClickable = false;
	private boolean clicked = false;
	
	// class constructor
	public Card(int value, int x, int y) {
		this.value = value;
		this.x = x;
		this.y = y;
		this.cardImage();
	}
	
	private void cardImage() {
		
		if (this.value == 1)
			this.cardImg = new Image("1.png", Card.CARD_HEIGHT, Card.CARD_HEIGHT, false, false);
		else if (this.value == 2) 
			this.cardImg = new Image("2.png", Card.CARD_HEIGHT, Card.CARD_HEIGHT, false, false);
		else if (this.value == 3) 
			this.cardImg = new Image("3.png", Card.CARD_HEIGHT, Card.CARD_HEIGHT, false, false);
		else if (this.value == 4)
			this.cardImg = new Image("4.png", Card.CARD_HEIGHT, Card.CARD_HEIGHT, false, false);
		else if (this.value == 5)
			this.cardImg = new Image("5.png", Card.CARD_HEIGHT, Card.CARD_HEIGHT, false, false);
		else if (this.value == 6)
			this.cardImg = new Image("6.png", Card.CARD_HEIGHT, Card.CARD_HEIGHT, false, false);
		else if (this.value == 7)
			this.cardImg = new Image("7.png", Card.CARD_HEIGHT, Card.CARD_HEIGHT, false, false);
		else if (this.value == 8)
			this.cardImg = new Image("8.png", Card.CARD_HEIGHT, Card.CARD_HEIGHT, false, false);
		
		
	}
	
	void render(GraphicsContext gc){
		gc.drawImage(this.cardImg, this.x, this.y);
    }
	
	public void setClicked(boolean val) {
		this.clicked = val;
	}
	
	public boolean getClicked() {
		return this.clicked;
	}
	
	public void setIsClickable(boolean val) {
		this.isClickable = val;
	}
	
	public boolean getIsClickable() {
		return this.isClickable;
	}
	
	public Image getImage() {
		return this.cardImg;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
}
