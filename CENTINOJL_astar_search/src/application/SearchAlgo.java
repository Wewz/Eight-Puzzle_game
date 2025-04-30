package application;

import java.util.ArrayList;

import javafx.scene.image.Image;

public class SearchAlgo {
	private int pathCost;
	private ArrayList<Integer> state;
	private ArrayList<String> previousMoves;
	private int f;
	private int h;
	private int g;
	
	// class constructor
	public SearchAlgo(ArrayList<Integer> state, int pathCost, ArrayList<String> prevMoves) {
		this.state = state;
		this.pathCost = pathCost;
		this.previousMoves = prevMoves;
		
		this.g = this.previousMoves.size();
	}
	
	public  void setH(int h) {
		this.h = h;
	}
	
	public int getPathCost() {
		return pathCost;
	}
	
	public ArrayList<Integer> getState() {
		return this.state;
	}
	
	public ArrayList<String> getPrevMoves() {
		return this.previousMoves;
	}
	
	public int getG() {
		return this.g;
	}
	
	public int getH() {
		return this.h;
	}
	
	public int getF() {
		return this.f;
	}
}
