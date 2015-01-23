package com.byte_games.snake2;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.byte_games.snake2.engine.GraphicsHelper;
import com.byte_games.snake2.engine.GraphicsHelper.AnnotatedLocation;
import com.byte_games.snake2.engine.GraphicsHelper.Location;
import com.byte_games.snake2.engine.SESurfaceView;
import com.byte_games.snake2.engine.SnakeEngine;
import com.byte_games.snake2.engine.TerrainGen;
import com.byte_games.snake2.engine.Ticker;

public class ArcadeModeActivity extends GameActivity {
	private boolean DoneSetup = false;
	private Mode OldMode = Mode.Paused;
	private double Speed = 0.30;
	private List<Location> Snake = new ArrayList<Location>();
	private List<AnnotatedLocation> Hazards = new ArrayList<AnnotatedLocation>();
	private Location Food;
	private AlertDialog Boxy = null;
	private TextView ScoreText;
	private TextView HighscoreText;
	private int Highscore;
	SharedPreferences.Editor HighscoreEditor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_arcade);
		myContext = getBaseContext();
		ScoreText = (TextView) findViewById(R.id.textScore);
		HighscoreText = (TextView) findViewById(R.id.textHighscore);
		
		//Setup game variables
		Snake.add(new Location(10, 10));
		Snake.add(new Location(9, 10));
		Snake.add(new Location(8, 10));
		Snake.add(new Location(7, 10));
		Snake.add(new Location(6, 10));
		Food = RanLoc();

		//Setup renderer and start draw thread
		myEngine = new SnakeEngine((SESurfaceView) findViewById(R.id.surfaceView), new myDrawer(), EngineTickRate, myContext, this);
		myEngine.myThread.setRunning(true);
        
        myEngine.Surface.setOnTouchListener(gestureListener);
        
        //Get highscore
        SharedPreferences mySettings = getSharedPreferences("Highscores", 0);
        Highscore = mySettings.getInt("Arcade", 0);
        HighscoreEditor = getSharedPreferences("Highscores", 0).edit();
		HighscoreText.setText(lengthToString(Highscore));
	}
	
	@Override
	protected void onDestroy() {
		//Kill thread
		myEngine.myThread.setRunning(false);
		boolean retry = true;
		while(retry) {
			try	{
				myEngine.myThread.join();
				retry = false;
			} catch(Exception e) {
				Log.v("Exception while ending engine thread.", e.getMessage());
			}
		}
		//Take care of any dialogs that need to be cleaned up
		if (Boxy != null) {
			Boxy.cancel();
		}
		super.onDestroy();
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void finshSetup() {
		if (!DoneSetup) {
			//Setup game GUI
			RelativeLayout BigBox = (RelativeLayout) findViewById(R.id.bigBox);
			RelativeLayout PanelL = (RelativeLayout) findViewById(R.id.panelL);
			RelativeLayout PanelR = (RelativeLayout) findViewById(R.id.panelR);
			RelativeLayout PanelT = (RelativeLayout) findViewById(R.id.panelT);

			Unit = (BigBox.getHeight() - PanelT.getHeight()) / (GraphicsHelper.SizeOfGame.Y + 1);
			float GameX = Unit * (GraphicsHelper.SizeOfGame.X + 1);
			float PanelX = (BigBox.getWidth() - GameX) / 2;

			android.widget.RelativeLayout.LayoutParams LayoutDim = (android.widget.RelativeLayout.LayoutParams) PanelL.getLayoutParams();
			LayoutDim.width = (int) Math.floor(PanelX);
			PanelL.setLayoutParams(LayoutDim);
			LayoutDim = (android.widget.RelativeLayout.LayoutParams) PanelR.getLayoutParams();
			LayoutDim.width = (int) Math.floor(PanelX); 
			PanelR.setLayoutParams(LayoutDim);
			
			//Make background for all the panels 
			Bitmap bmpBackground = Bitmap.createBitmap(BigBox.getWidth(), BigBox.getHeight(), Bitmap.Config.ARGB_8888);
			Canvas canvasBackground = new Canvas(bmpBackground);
			canvasBackground.drawColor(Color.parseColor("#505050"));
			
			Paint[] Rocks = {new Paint(), new Paint(), new Paint()};
			Rocks[0].setColor(Color.parseColor("#393939"));
			Rocks[1].setColor(Color.parseColor("#353535"));
			Rocks[2].setColor(Color.parseColor("#303030"));
			
			int EndPixX = (int) ((BigBox.getWidth() / Unit) + 1);
			int EndPixY = (int) ((BigBox.getHeight() / Unit) + 1);
			
			for (int countX = 0; countX <= EndPixX; countX++) {
				for (int countY = 0; countY <= EndPixY; countY++) {
					GraphicsHelper.addPixel(canvasBackground, new Location(countX, countY), Rocks[(int) (Math.random() * ((2) + 1))], Unit);
				}
			}
			
			android.graphics.drawable.BitmapDrawable Background = new android.graphics.drawable.BitmapDrawable(getResources(), bmpBackground);
			PanelT.setBackgroundDrawable(Background);
			Background.setGravity(Gravity.RIGHT);
			PanelL.setBackgroundDrawable(Background);
			Background.setGravity(Gravity.LEFT);
			PanelR.setBackgroundDrawable(Background);
			
			HideNavBar();
			
			DoneSetup = true;
		}
	}
	
	protected Location RanLoc() {
		Location Min = new Location(0, 0);
		Location Max = new Location(GraphicsHelper.SizeOfGame.X, GraphicsHelper.SizeOfGame.Y);
		boolean Good = false;
		Location New = new Location(0, 0);
		while (!Good) {
			New.X = Min.X + (int)(Math.random() * ((Max.X - Min.X) + 1));
			New.Y = Min.Y + (int)(Math.random() * ((Max.Y - Min.Y) + 1));
			Good = true;
			for (Location Point : Snake) {
				if (New.equals(Point)) {
					Good = false;
				}
			}
			for (Location Point : Hazards) {
				if (New.equals(Point)) {
					Good = false;
				}
			}
		}
		return New;
	}
	
	protected AnnotatedLocation RanHazardLoc() {
		Location Min = new Location(0, 0);
		Location Max = new Location(GraphicsHelper.SizeOfGame.X, GraphicsHelper.SizeOfGame.Y);
		boolean Good = false;
		AnnotatedLocation New = new AnnotatedLocation(0, 0);
		
		while (!Good) {
			New.X = Min.X + (int)(Math.random() * ((Max.X - Min.X) + 1));
			New.Y = Min.Y + (int)(Math.random() * ((Max.Y - Min.Y) + 1));
			Good = true;
			
			for (Location Point : Snake) {
				if (New.equals(Point)) {
					Good = false;
				}
			}
			for (Location Point : Hazards) {
				if (New.equals(Point)) {
					Good = false;
				}
			}
			if (New.equals(Food)) {
				Good = false;
			}
			//Check five spaces in front of the snake
			Location SnakeTest = new Location(0, 0);
			Snake.get(0).CopyTo(SnakeTest);
			for (int TestCount = 1; TestCount <= 5; TestCount++) {
				if (CurrentMode == Mode.Left) {
					SnakeTest.X -= 1;
				} else if (CurrentMode == Mode.Right) {
					SnakeTest.X += 1;
				} else if (CurrentMode == Mode.Up) {
					SnakeTest.Y -= 1;
				} else if (CurrentMode == Mode.Down) {
					SnakeTest.Y += 1;
				}
				
				if (New.equals(SnakeTest)) {
					Good = false;
					break;
				}
			}
		}
		return New;
	}

	private final static int TH_ShowDeathDialog = 1;
	private final static int TH_UpdateActionBar = 2;
	private ArcadeModeActivity ThisGame = this;
	
	@SuppressLint("HandlerLeak")
	private final Handler ThreadHelper = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == TH_ShowDeathDialog) {
				CurrentMode = Mode.Paused;
				AlertDialog.Builder builder = new AlertDialog.Builder(ThisGame);
				builder.setMessage((String) msg.obj);
				builder.setCancelable(false);
				builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = getIntent();
						finish();
						startActivity(intent);
					}
				});
				builder.setNegativeButton("Return to menu", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						NavUtils.navigateUpFromSameTask(ThisGame);
					}
				});
				builder.create();
				builder.show();
			} else if (msg.what == TH_UpdateActionBar) {
				if (Snake.size() > Highscore) {
					Highscore = Snake.size();
					HighscoreText.setText(lengthToString(Highscore));

					HighscoreEditor.putInt("Arcade", Highscore);
					HighscoreEditor.commit();
				}
				
				ScoreText.setText(lengthToString(Snake.size()));
			}
		}
	};
	
	public void PauseGame() {
		if (CurrentMode != Mode.Paused) {
			OldMode = CurrentMode;
			CurrentMode = Mode.Paused;

			AlertDialog.Builder builder = new AlertDialog.Builder(ThisGame);
			builder.setMessage("Game Paused");
			builder.setCancelable(false);
			builder.setPositiveButton("Resume", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					CurrentMode = OldMode;
					Boxy = null;
					HideNavBar();
				}
			});
			builder.setNegativeButton("Return to menu", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					NavUtils.navigateUpFromSameTask(ThisGame);
					Boxy = null;
				}
			});
			builder.create();
			Boxy = builder.show();
		}
	}

	public class myDrawer extends Ticker {
		Paint color_SnakeHead;
		Paint color_SnakeBody1;
		Paint color_SnakeBody2;
		Paint color_Mouse;
		Paint color_Bomb1;
		Bitmap bmpBackground = null;
		Bitmap bmpBottomWall = null;
		double SpeedCount = 0;

		public myDrawer() {
			color_SnakeHead = new Paint();
			color_SnakeHead.setColor(Color.rgb(190, 14, 14));
			color_SnakeBody1 = new Paint();
			color_SnakeBody1.setColor(Color.rgb(214, 60, 0));
			color_SnakeBody2 = new Paint();
			color_SnakeBody2.setColor(Color.rgb(214, 80, 0));
			color_Mouse = new Paint();
			color_Mouse.setColor(Color.WHITE);
			color_Bomb1 = new Paint();
			color_Bomb1.setColor(Color.BLACK);
			ThreadHelper.obtainMessage(TH_UpdateActionBar).sendToTarget();
		}

		@Override
		public Canvas doTick(Canvas CanvasIn) {
			//Draw Background
			if (bmpBackground == null) {
				//Make Game Background
				bmpBackground = Bitmap.createBitmap(CanvasIn.getWidth(), CanvasIn.getHeight(), Bitmap.Config.ARGB_8888);
				Canvas canvasBackground = new Canvas(bmpBackground);
				canvasBackground.drawColor(Color.parseColor("#005000"));
				TerrainGen myTerrainGen = new TerrainGen(Unit, GraphicsHelper.SizeOfGame, GraphicsHelper.BackgroundBiomeSize, getBaseContext());
				canvasBackground = myTerrainGen.makeGameBackground(canvasBackground);
				
				//Make Rocky Background
				bmpBottomWall = Bitmap.createBitmap(CanvasIn.getWidth(), (int) (CanvasIn.getHeight() - ((GraphicsHelper.SizeOfGame.Y + 1) * Unit)), Bitmap.Config.ARGB_8888);
				Canvas canvasWallBackground = new Canvas(bmpBottomWall);
				canvasWallBackground.drawColor(Color.parseColor("#505050"));
				
				Paint[] Rocks = {new Paint(), new Paint(), new Paint()};
				Rocks[0].setColor(Color.parseColor("#393939"));
				Rocks[1].setColor(Color.parseColor("#353535"));
				Rocks[2].setColor(Color.parseColor("#303030"));
				
				int EndY = (int) (canvasWallBackground.getHeight() / Unit) + 1; 
				for (int countX = 0; countX <= GraphicsHelper.SizeOfGame.X; countX++) {
					for (int countY = 0; countY <= EndY; countY++) {
						GraphicsHelper.addPixel(canvasWallBackground, new Location(countX, countY), Rocks[(int) (Math.random() * ((2) + 1))], Unit);
					}
				}
			}
			CanvasIn.drawBitmap(bmpBackground, 0, 0, new Paint());

			//Code that's stopped on pause
			if (CurrentMode != Mode.Paused) {
				SpeedCount += Speed;
				if (SpeedCount >= 1) {
					SpeedCount--;
					
					//Move snake's body parts to catch up with the parts in front
					for (int count = Snake.size() - 1; count > 0; count--) {
						Location SnakePart = Snake.get(count);
						Snake.get(count - 1).CopyTo(SnakePart);
						Snake.set(count, SnakePart);
					}

					//Move snake's head in requested direction
					if (CurrentMode == Mode.Left) {
						Location SnakePart = Snake.get(0);
						SnakePart.X -= 1;
						Snake.set(0, SnakePart);
					} else if (CurrentMode == Mode.Right) {
						Location SnakePart = Snake.get(0);
						SnakePart.X += 1;
						Snake.set(0, SnakePart);
					} else if (CurrentMode == Mode.Up) {
						Location SnakePart = Snake.get(0);
						SnakePart.Y -= 1;
						Snake.set(0, SnakePart);
					} else if (CurrentMode == Mode.Down) {
						Location SnakePart = Snake.get(0);
						SnakePart.Y += 1;
						Snake.set(0, SnakePart);
					}

					//Check for hits
					if (Snake.get(0).equals(Food)) {
						//Yum!
						Location NewTail = new Location(0, 0);
						Snake.get(Snake.size() - 1).CopyTo(NewTail);
						Snake.add(NewTail);
						if (Snake.size() % 3 == 0) {
							if (Speed >= .3 && Speed < .5) {
								Speed += .01;
							} else if (Speed < .3) {
								Speed += .05;
							}
						}
						Food = RanLoc();
						ThreadHelper.obtainMessage(TH_UpdateActionBar).sendToTarget();
						
						//Activate a hazard?
						if (Snake.size() >= 6) {
							int NumOfHazards = 3;
							
							//Starts at a .2 chance and goes up to .7 at a rate of .1 every 1' 8" (10 snake parts)
							double Chance = 0.2 + ((Snake.size() - 6) * 0.01);
							if (Chance > 0.7) { Chance = 0.7; }
							
							//Between 1 and (1/Chance) * NumOfHazards
							int Ran = 1 + (int)(Math.random() * ((1 / Chance) * NumOfHazards));
							if (Ran == 1) {
								//Lava hazard
								AnnotatedLocation LavaSource = RanHazardLoc();
								LavaSource.Type = "Lava";
								LavaSource.NumValue = 3;
								LavaSource.TextValue = "You took a dip in a river of lava";
								Hazards.add(LavaSource);
							} else if (Ran == 2) {
								//Fire bomb hazard
								AnnotatedLocation Bomb = RanHazardLoc();
								Bomb.Type = "FireBomb";
								Bomb.NumValue = 5;
								Bomb.TextValue = "You ran into some burning napalm";
								Hazards.add(Bomb);
							} else if (Ran == 3) {
								//Laser hazard
								AnnotatedLocation LaserEmiter = RanHazardLoc();
								LaserEmiter.Type = "Laser";
								LaserEmiter.NumValue = 5;
								LaserEmiter.TextValue = "You ran into a laser";
								Hazards.add(LaserEmiter);
							}
						}
					} else if (Snake.get(0).X <= -1 || Snake.get(0).X >= GraphicsHelper.SizeOfGame.X + 1 || Snake.get(0).Y <= -1 || Snake.get(0).Y >= GraphicsHelper.SizeOfGame.Y + 1) {
						//Wall hit!
						CurrentMode = Mode.Paused;
						ThreadHelper.obtainMessage(TH_ShowDeathDialog, "You ran into a wall").sendToTarget();
					} else {
						for (AnnotatedLocation Hazard : Hazards) {
							if (Hazard.equals(Snake.get(0))) {
								//Hazard hit!
								CurrentMode = Mode.Paused;
								ThreadHelper.obtainMessage(TH_ShowDeathDialog, Hazard.TextValue).sendToTarget();
								//TODO: What if tail is hit?
							}
						}
					}
				}
			}

			//Draw mouse
			GraphicsHelper.addPixel(CanvasIn, Food, color_Mouse, Unit);
			
			//Draw snake
			for (int SnakeDrawCount = Snake.size() - 1; SnakeDrawCount >= 0; SnakeDrawCount--) {
				Location SnakePart = Snake.get(SnakeDrawCount);
				if (SnakeDrawCount == 0) {
					GraphicsHelper.addPixel(CanvasIn, SnakePart, color_SnakeHead, Unit);
				} else if (SnakeDrawCount % 2 == 1) { //it's an odd number
					GraphicsHelper.addPixel(CanvasIn, SnakePart, color_SnakeBody1, Unit);
				} else { //it's an even number
					GraphicsHelper.addPixel(CanvasIn, SnakePart, color_SnakeBody2, Unit);
				}
			}
			
			//Draw and update hazards
			for (AnnotatedLocation Hazard : Hazards) {
				if (Hazard.Type.equals("Lava")) {
					GraphicsHelper.addPixel(CanvasIn, Hazard, color_Bomb1, Unit);
				} else if (Hazard.Type.equals("FireBomb")) {
					GraphicsHelper.addPixel(CanvasIn, Hazard, color_Bomb1, Unit);
				} else if (Hazard.Type.equals("Laser")) {
					GraphicsHelper.addPixel(CanvasIn, Hazard, color_Bomb1, Unit);
				}
				//TODO: Write animations
			}
			
			CanvasIn.drawBitmap(bmpBottomWall, 0, (GraphicsHelper.SizeOfGame.Y + 1) * Unit, new Paint());
			return CanvasIn;
		}
	}
}