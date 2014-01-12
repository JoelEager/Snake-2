package com.byte_games.snake2;

import java.util.List;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.byte_games.snake2.GraphicsHelper.Location;
import com.byte_games.snake2.GraphicsHelper.Size;

public class GameActivity extends Activity {
	static float Unit = 0; //The size of one side of a pixel
	static final Size SizeOfGame = new Size(60, 40); //Zero-based
	static final Size BackgroundBiomeSize = new Size(15, 10);
	
	void finshSetup() {
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

	enum Mode{Left, Right, Up, Down, Paused} 
	
	Canvas addPixel(Canvas CanvasIn, Location Where, Paint Color) {
		CanvasIn.drawRect(Where.X * Unit, Where.Y * Unit, (Where.X * Unit) + Unit, (Where.Y * Unit) + Unit, Color);
		return CanvasIn;
	}
}