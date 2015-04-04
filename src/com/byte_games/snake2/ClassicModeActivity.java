package com.byte_games.snake2;

import com.byte_games.snake2.engine.SnakeEngine;
import com.byte_games.snake2.engine.SESurfaceView;
import com.byte_games.snake2.engine.Ticker;
import com.byte_games.snake2.engine.GraphicsHelper;
import com.byte_games.snake2.engine.TerrainGen;
import com.byte_games.snake2.engine.GraphicsHelper.*;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class ClassicModeActivity extends GameActivity {
	private boolean DoneSetup = false;
	private boolean ChangingLevel = false;
	private Mode OldMode = Mode.Paused;
	private double Speed = 0.30;
	private List<Location> Snake = new ArrayList<Location>();
	private List<Location> Walls = new ArrayList<Location>();
	private Location Food;
	private AlertDialog Boxy = null;
	private TextView ScoreText;
	private TextView HighscoreText;
	private int Highscore;
	SharedPreferences.Editor HighscoreEditor;
	
	protected ClassicModeActivity myGameReferance = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
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
        Highscore = mySettings.getInt("Classic", 0);
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
			for (Location Point : Walls) {
				if (New.equals(Point)) {
					Good = false;
				}
			}
		}
		return New;
	}
	
	private final static int TH_ShowDeathDialog = 1;
	private final static int TH_UpdateBar = 2;
	
	private static final class ThreadHelper extends Handler {
		private ClassicModeActivity myGame;
		
		ThreadHelper(ClassicModeActivity myGameReferance) {
			myGame = new WeakReference<ClassicModeActivity>(myGameReferance).get();
		}
		
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == TH_ShowDeathDialog) {
				myGame.CurrentMode = Mode.Paused;
				
				AlertDialog.Builder builder = new AlertDialog.Builder(myGame);
				builder.setMessage((String) msg.obj);
				builder.setCancelable(false);
				builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = myGame.getIntent();
						myGame.finish();
						myGame.startActivity(intent);
					}
				});
				builder.setNegativeButton("Return to menu", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						NavUtils.navigateUpFromSameTask(myGame);
					}
				});
				builder.create();
				builder.show();
			} else if (msg.what == TH_UpdateBar) {
				if (myGame.Snake.size() > myGame.Highscore) {
					myGame.Highscore = myGame.Snake.size();
					myGame.HighscoreText.setText(lengthToString(myGame.Highscore));

					myGame.HighscoreEditor.putInt("Classic", myGame.Highscore);
					myGame.HighscoreEditor.commit();
				}
				
				myGame.ScoreText.setText(lengthToString(myGame.Snake.size()));
			}
		}
	};
	
	@Override
	public void PauseGame() {
		if (CurrentMode != Mode.Paused) {
			OldMode = CurrentMode;
			AlertDialog.Builder builder = new AlertDialog.Builder(myGameReferance);
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
					NavUtils.navigateUpFromSameTask(myGameReferance);
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
		Paint color_Hole1;
		Paint color_Hole2;
		Paint[] colors_Wall;
		Bitmap bmpBackground = null;
		Bitmap bmpBottomWall = null;
		Bitmap bmpWall = null;
		double SpeedCount = 0;
		boolean DrawExitHole = false;
		boolean FinalLevel = false;
		ThreadHelper myThreadHelper;

		public myDrawer() {
			myThreadHelper = new ThreadHelper(myGameReferance);
			
			color_SnakeHead = new Paint();
			color_SnakeHead.setColor(Color.rgb(190, 14, 14));
			color_SnakeBody1 = new Paint();
			color_SnakeBody1.setColor(Color.rgb(214, 60, 0));
			color_SnakeBody2 = new Paint();
			color_SnakeBody2.setColor(Color.rgb(214, 80, 0));
			
			color_Mouse = new Paint();
			color_Mouse.setColor(Color.WHITE);
			
			color_Hole1 = new Paint();
			color_Hole1.setColor(Color.parseColor("#5A3C1E"));
			color_Hole2 = new Paint();
			color_Hole2.setColor(Color.parseColor("#704B25"));

			colors_Wall = new Paint[3];
			colors_Wall[0] = new Paint();
			colors_Wall[0].setColor(Color.parseColor("#393939"));
			colors_Wall[1] = new Paint();
			colors_Wall[1].setColor(Color.parseColor("#353535"));
			colors_Wall[2] = new Paint();
			colors_Wall[2].setColor(Color.parseColor("#303030"));
			
			myThreadHelper.obtainMessage(TH_UpdateBar).sendToTarget();
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
				
				//Prep Wall Bmp
				bmpWall = Bitmap.createBitmap(CanvasIn.getWidth(), CanvasIn.getHeight(), Bitmap.Config.ARGB_8888);
			}
			CanvasIn.drawBitmap(bmpBackground, 0, 0, new Paint());
			
			//Draw exit hole if needed
			if (DrawExitHole) {
				Location HolePoint = new Location(5, 5);
				GraphicsHelper.addPixel(CanvasIn, new Location(HolePoint.X + 1, HolePoint.Y), color_Hole1, Unit);
				GraphicsHelper.addPixel(CanvasIn, new Location(HolePoint.X - 1, HolePoint.Y), color_Hole1, Unit);
				GraphicsHelper.addPixel(CanvasIn, new Location(HolePoint.X, HolePoint.Y + 1), color_Hole1, Unit);
				GraphicsHelper.addPixel(CanvasIn, new Location(HolePoint.X, HolePoint.Y - 1), color_Hole1, Unit);
				GraphicsHelper.addPixel(CanvasIn, new Location(HolePoint.X + 1, HolePoint.Y + 1), color_Hole2, Unit);
				GraphicsHelper.addPixel(CanvasIn, new Location(HolePoint.X + 1, HolePoint.Y - 1), color_Hole2, Unit);
				GraphicsHelper.addPixel(CanvasIn, new Location(HolePoint.X - 1, HolePoint.Y + 1), color_Hole2, Unit);
				GraphicsHelper.addPixel(CanvasIn, new Location(HolePoint.X - 1, HolePoint.Y - 1), color_Hole2, Unit);
				GraphicsHelper.addPixel(CanvasIn, new Location(HolePoint.X, HolePoint.Y), color_Hole2, Unit);
				
				if  (!HolePoint.equals(Snake.get(Snake.size() - 1))) {
					DrawExitHole = false;
				}
			}
			
			//Code that's stopped on pause
			if (CurrentMode != Mode.Paused) {
				SpeedCount += Speed;
				
				//Normal game code
				if (!ChangingLevel) {
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
							
							//Every 6 inches increase speed
							if (Snake.size() % 3 == 0) {
								if (Speed >= .5 && Speed < .65) {
									Speed += .01;
								} else if (Speed < .5) {
									Speed += .02;
								}
							}
							
							//Every 3 feet change level
							if (!FinalLevel && Snake.size() % 18 == 0) {
								ChangingLevel = true;
							} else {
								Food = RanLoc();
							}
							
							myThreadHelper.obtainMessage(TH_UpdateBar).sendToTarget();
						} else if (Snake.get(0).X <= -1 || Snake.get(0).X >= GraphicsHelper.SizeOfGame.X + 1 || Snake.get(0).Y <= -1 || Snake.get(0).Y >= GraphicsHelper.SizeOfGame.Y + 1) {
							//Wall hit!
							CurrentMode = Mode.Paused;
							myThreadHelper.obtainMessage(TH_ShowDeathDialog, "You ran into a wall").sendToTarget();
						} else {
							for (Location Block : Walls) {
								if (Snake.get(0).equals(Block)) {
									//Wall hit!
									CurrentMode = Mode.Paused;
									myThreadHelper.obtainMessage(TH_ShowDeathDialog, "You ran into a wall").sendToTarget();
								}
							}
						}
					}
				//Level switch code
				} else {
					//Check for dig completion
					if (Snake.get(0).equals(Snake.get(Snake.size() - 1))) {
						//Add walls
						int Level = Snake.size() / 18;
						Walls.clear();
						if (Level == 1) {
							// O    O
							//
							// O    O
							GraphicsHelper.AddRect(Walls, new Location(10, 10), new Size(3, 4));
							GraphicsHelper.AddRect(Walls, new Location(48, 10), new Size(3, 4));
							GraphicsHelper.AddRect(Walls, new Location(10, 27), new Size(3, 4));
							GraphicsHelper.AddRect(Walls, new Location(48, 27), new Size(3, 4));
						} else if (Level == 2) {
							//   |
							//   |
							//   |
							GraphicsHelper.AddRect(Walls, new Location(29, 10), new Size(3, 21));
						} else if (Level == 3) {
							//
							// ---------
							//
							GraphicsHelper.AddRect(Walls, new Location(10, 19), new Size(41, 3));
						} else if (Level == 4) {
							// [=]   [=]
							// 
							// [=]   [=]
							GraphicsHelper.AddRect(Walls, new Location(10, 10), new Size(7, 4));
							GraphicsHelper.AddRect(Walls, new Location(44, 10), new Size(7, 4));
							GraphicsHelper.AddRect(Walls, new Location(10, 27), new Size(7, 4));
							GraphicsHelper.AddRect(Walls, new Location(44, 27), new Size(7, 4));
						} else if (Level == 5) {
							// |     |
							// |     |
							// |     |
							GraphicsHelper.AddRect(Walls, new Location(10, 10), new Size(3, 21));
							GraphicsHelper.AddRect(Walls, new Location(48, 10), new Size(3, 21));
						} else if (Level == 6) {
							// ---------
							// 
							// ---------
							GraphicsHelper.AddRect(Walls, new Location(10, 10), new Size(41, 3));
							GraphicsHelper.AddRect(Walls, new Location(10, 28), new Size(41, 3));
						} else if (Level == 7) {
							// |     |
							// |=====|
							// |     |
							GraphicsHelper.AddRect(Walls, new Location(10, 10), new Size(3, 21));
							GraphicsHelper.AddRect(Walls, new Location(48, 10), new Size(3, 21));
							GraphicsHelper.AddRect(Walls, new Location(13, 19), new Size(35, 3));
						} else if (Level == 8) {
							// ---------
							//  O     O
							// ---------
							GraphicsHelper.AddRect(Walls, new Location(10, 10), new Size(41, 3));
							GraphicsHelper.AddRect(Walls, new Location(10, 28), new Size(41, 3));
							GraphicsHelper.AddRect(Walls, new Location(15, 19), new Size(3, 3));
							GraphicsHelper.AddRect(Walls, new Location(43, 19), new Size(3, 3));
						} else if (Level == 9) {
							// [=] | [=]
							//     |
							// [=] | [=]
							GraphicsHelper.AddRect(Walls, new Location(10, 10), new Size(7, 4));
							GraphicsHelper.AddRect(Walls, new Location(44, 10), new Size(7, 4));
							GraphicsHelper.AddRect(Walls, new Location(10, 27), new Size(7, 4));
							GraphicsHelper.AddRect(Walls, new Location(44, 27), new Size(7, 4));
							GraphicsHelper.AddRect(Walls, new Location(29, 10), new Size(3, 21));
							FinalLevel = true;
						}
						
						//Draw wall to Bmp
						bmpWall = Bitmap.createBitmap(CanvasIn.getWidth(), CanvasIn.getHeight(), Bitmap.Config.ARGB_8888);
						Canvas canvasWall = new Canvas(bmpWall);
						for (Location Block : Walls) {
							GraphicsHelper.addPixel(canvasWall, Block, colors_Wall[(int) (Math.random() * ((2) + 1))], Unit);
						}
						
						//Prep for new level
						for (int count = Snake.size() - 1; count >= 0; count--) {
							Snake.set(count, new Location(5, 5));
						}
						ChangingLevel = false;
						DrawExitHole = true;
						CurrentMode = Mode.Right;
						Food = RanLoc();
						
						//Make new background
						bmpBackground = Bitmap.createBitmap(CanvasIn.getWidth(), CanvasIn.getHeight(), Bitmap.Config.ARGB_8888);
						Canvas canvasBackground = new Canvas(bmpBackground);
						canvasBackground.drawColor(Color.parseColor("#005000"));
						TerrainGen myTerrainGen = new TerrainGen(Unit, GraphicsHelper.SizeOfGame, GraphicsHelper.BackgroundBiomeSize, getBaseContext());
						canvasBackground = myTerrainGen.makeGameBackground(canvasBackground);
					}
					
					//Dig hole
					if (SpeedCount >= 1) {
						SpeedCount--;
						
						//Move snake's body parts to catch up with the parts in front
						for (int count = Snake.size() - 1; count > 0; count--) {
							Location SnakePart = Snake.get(count);
							Snake.get(count - 1).CopyTo(SnakePart);
							Snake.set(count, SnakePart);
						}
					}
					
					//Draw hole
					GraphicsHelper.addPixel(CanvasIn, new Location(Snake.get(0).X + 1, Snake.get(0).Y), color_Hole1, Unit);
					GraphicsHelper.addPixel(CanvasIn, new Location(Snake.get(0).X - 1, Snake.get(0).Y), color_Hole1, Unit);
					GraphicsHelper.addPixel(CanvasIn, new Location(Snake.get(0).X, Snake.get(0).Y + 1), color_Hole1, Unit);
					GraphicsHelper.addPixel(CanvasIn, new Location(Snake.get(0).X, Snake.get(0).Y - 1), color_Hole1, Unit);
					GraphicsHelper.addPixel(CanvasIn, new Location(Snake.get(0).X + 1, Snake.get(0).Y + 1), color_Hole2, Unit);
					GraphicsHelper.addPixel(CanvasIn, new Location(Snake.get(0).X + 1, Snake.get(0).Y - 1), color_Hole2, Unit);
					GraphicsHelper.addPixel(CanvasIn, new Location(Snake.get(0).X - 1, Snake.get(0).Y + 1), color_Hole2, Unit);
					GraphicsHelper.addPixel(CanvasIn, new Location(Snake.get(0).X - 1, Snake.get(0).Y - 1), color_Hole2, Unit);
				}
			}

			//Draw Mouse
			GraphicsHelper.addPixel(CanvasIn, Food, color_Mouse, Unit);

			//Draw Snake
			for (int SnakeDrawCount = Snake.size() - 1; SnakeDrawCount >= 0; SnakeDrawCount--) {
				Location SnakePart = Snake.get(SnakeDrawCount);
				if (SnakeDrawCount == 0 && !ChangingLevel) { //Snake head isn't drawn while digging
					GraphicsHelper.addPixel(CanvasIn, SnakePart, color_SnakeHead, Unit);
				} else if (SnakeDrawCount % 2 == 1) { //it's an odd number
					GraphicsHelper.addPixel(CanvasIn, SnakePart, color_SnakeBody1, Unit);
				} else { //it's an even number
					GraphicsHelper.addPixel(CanvasIn, SnakePart, color_SnakeBody2, Unit);
				}
			}

			CanvasIn.drawBitmap(bmpWall, 0, 0, new Paint());
			CanvasIn.drawBitmap(bmpBottomWall, 0, (GraphicsHelper.SizeOfGame.Y + 1) * Unit, new Paint());
			return CanvasIn;
		}	
	}
}