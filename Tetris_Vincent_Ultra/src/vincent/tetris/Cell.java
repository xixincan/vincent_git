package vincent.tetris;

import java.awt.image.BufferedImage;

public class Cell {
	private int row;
	private int col;
	private BufferedImage image;
	
	public Cell(int row,int col,BufferedImage image){
		this.row=row;
		this.col=col;
		this.image=image;
	}
	
	public void setRow(int row){
		this.row=row;
	}
	 public int getRow(){
		 return this.row;
	 }
	public void setCol(int col){
		this.col=col;
	}
	public int getCol(){
		return this.col;
	}
	public void setImage(BufferedImage image){
		this.image=image;
	}
	public BufferedImage getImage(){
		return this.image;
	}
	
	public void softDrop(){
		this.row++;
	}
	public void moveLeft(){
		this.col--;
	}
	public void moveRight(){
		this.col++;
	}
	
	public String toString(){
		return "("+this.row+","+this.col+")";
	}
}
