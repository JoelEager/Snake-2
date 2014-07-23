package com.byte_games.snake2;

import java.util.List;

import android.app.Activity;
import android.content.Context;

import com.byte_games.snake2.engine.SnakeEngine;
import com.byte_games.snake2.engine.GraphicsHelper.*;

public class GameActivity extends Activity {
	protected Context myContext;
	protected SnakeEngine myEngine;
	protected float Unit;
	
	public void finshSetup() {
		//Empty sub to be over-ridden by sub-classes
	}
	
	protected Location RanLoc(List<Location> NoSpots, Location Max, Location Min) {
		boolean Good = false;
		Location New = new Location(0, 0);
		while (!Good) {
			New.X = Min.X + (int)(Math.random() * ((Max.X - Min.X) + 1));
			New.Y = Min.Y + (int)(Math.random() * ((Max.Y - Min.Y) + 1));
			Good = true;
			for (Location CurrentL : NoSpots) {
				if (CurrentL.X == New.X && CurrentL.Y == New.Y) {
					Good = false;
				}
			}
		}
		return New;
	}

	public enum Mode{Left, Right, Up, Down, Paused}
}