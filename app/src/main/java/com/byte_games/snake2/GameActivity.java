package com.byte_games.snake2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;

import com.byte_games.snake2.engine.SnakeEngine;

@SuppressLint("ClickableViewAccessibility")
abstract public class GameActivity extends Activity {
	protected Context myContext;
	protected SnakeEngine myEngine;
	protected float Unit;
	protected final int EngineTickRate = 20; //in milisecs - 50 FPS
	protected Mode CurrentMode = Mode.Right;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Gesture detection
		gestureDetector = new GestureDetector(this, new MyGestureDetector());
		gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		};
	}
	
	public void finishSetup() {
		//Empty sub to be over-ridden by sub-classes
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
	
	public void changeMode(View sourceView) {
		updateMode(sourceView.getId());
	}
	
	protected void updateMode(int DirectionId) {
		if (CurrentMode != Mode.Paused) {
			if (DirectionId == R.id.imgLeft) {
				CurrentMode = Mode.Left;
			} else if (DirectionId == R.id.imgRight) {
				CurrentMode = Mode.Right;
			} else if (DirectionId == R.id.imgUp) {
				CurrentMode = Mode.Up;
			} else if (DirectionId == R.id.imgDown) {
				CurrentMode = Mode.Down;
			} else if (DirectionId == R.id.imgPause) {
				PauseGame();
			}
		}

		HideNavBar();
	}
	
	protected void HideNavBar() {
		//Hides the nav bar
		View decorView = this.getWindow().getDecorView();
		int uiOptions = View.SYSTEM_UI_FLAG_LOW_PROFILE;
		decorView.setSystemUiVisibility(uiOptions);
	}
	
	protected static String lengthToString(int Length) {
		boolean Units = true; //true for English, false for Metric
		String Text = "";
		
		if (Units) {
			int Inches = Length * 2; //1 length = 2 inches
			int Feet = Inches / 12;
			Inches -= Feet * 12;

			Text = Feet + " ft, " + Inches + " in";
		} else {
			int Meters = Length / 18; //18 length = 1 meter
			int CM = (int) ((Length - (Meters * 18)) * ((double) 100 / 18));

			Text = Meters + " m, " + CM + " cm";
		}
		return Text;
	}
	
	private static final int SWIPE_MIN_DISTANCE = 50;
	private static final int SWIPE_THRESHOLD_VELOCITY = 100;
	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;
	
	class MyGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			try {
				if (Math.abs(e1.getX() - e2.getX()) > Math.abs(e1.getY() - e2.getY())) {
					//X component of swipe is larger
					if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
						//It's a right to left swipe
						updateMode(R.id.imgLeft);
					} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
						//It's a left to right swipe
						updateMode(R.id.imgRight);
					}
				} else {
					//Y component of swipe is larger
					if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
						//It's a bottom to top swipe
						updateMode(R.id.imgUp);
					} else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
						//It's a top to bottom swipe
						updateMode(R.id.imgDown);
					}
				}
			} catch (Exception e) {
				//Do nothing
			}
			return false;
		}

		@Override
		public boolean onDown(MotionEvent e) {
			  return true;
		}
	}
}