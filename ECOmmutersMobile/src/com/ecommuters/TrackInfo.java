package com.ecommuters;

import java.io.Serializable;

public class TrackInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7243063135764199919L;
	private double rating;
	private double minHeight;
	private double maxHeight;
	private String difficulty;
	private int cycling;
	private double length;
	private String title;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public double getLength() {
		return length;
	}
	public void setLength(double length) {
		this.length = length;
	}
	public int getCycling() {
		return cycling;
	}
	public void setCycling(int cycling) {
		this.cycling = cycling;
	}
	public String getDifficulty() {
		return difficulty;
	}
	public void setDifficulty(String difficulty) {
		this.difficulty = difficulty;
	}
	public double getMaxHeight() {
		return maxHeight;
	}
	public void setMaxHeight(double maxHeight) {
		this.maxHeight = maxHeight;
	}
	public double getMinHeight() {
		return minHeight;
	}
	public void setMinHeight(double minHeight) {
		this.minHeight = minHeight;
	}
	public double getRating() {
		return rating;
	}
	public void setRating(double rating) {
		this.rating = rating;
	}

	
}
