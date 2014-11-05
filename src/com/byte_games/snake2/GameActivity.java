package com.byte_games.snake2;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.byte_games.snake2.engine.SnakeEngine;
import com.byte_games.snake2.engine.GraphicsHelper.*;

abstract public class GameActivity extends Activity {
	protected Context myContext;
	protected SnakeEngine myEngine;
	protected float Unit;
	protected final int EngineTickRate = 20; //in milisecs - 50 FPS
	
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
	
	@Override
	public void onBackPressed(){
	     PauseGame();
	}

	@Override
	protected void onPause() {
		super.onPause();
		PauseGame();
	}
	
	public void PauseGame() {
		//Empty sub to be over-ridden by sub-classes
	}
	
	protected void HideNavBar() {
		//Hides the nav bar
		View decorView = this.getWindow().getDecorView();
		int uiOptions = View.SYSTEM_UI_FLAG_LOW_PROFILE;
		decorView.setSystemUiVisibility(uiOptions);
	}
}