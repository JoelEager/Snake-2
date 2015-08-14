package com.byte_games.snake2;

import com.byte_games.snake2.engine.Adventure;
import com.byte_games.snake2.engine.Adventure.LevelType;
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
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class AdventureModeActivity extends GameActivity {
	private boolean DoneSetup = false;
	private Mode OldMode = Mode.Paused;
	private double Speed = 0.30;
	private List<Location> Snake = new ArrayList<Location>();
	private Location Food;
	private AlertDialog Boxy = null;
	private TextView ScoreText;
	private TextView ProgressText;

	private Adventure myAdventure;
	private int InitialSize;
	private boolean ChangingLevel = false;

	protected AdventureModeActivity myGameReference = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_adventure);
		myContext = getBaseContext();
		ScoreText = (TextView) findViewById(R.id.textScore);
		ProgressText = (TextView) findViewById(R.id.textProgress);

		//Setup adventure object
		Intent receivedIntent = getIntent();
		int NumOfLevels = receivedIntent.getIntExtra("com.byte_games.snake2.Adventure_NumOfLevels", 3);
		int Difficulty = receivedIntent.getIntExtra("com.byte_games.snake2.Adventure_Difficulty", 0);
		myAdventure = new Adventure(NumOfLevels, Difficulty);

		//Setup game variables
		Snake.add(new Location(5, 5));
		Snake.add(new Location(4, 5));
		Snake.add(new Location(3, 5));
		Snake.add(new Location(2, 5));
		Snake.add(new Location(1, 5));
		Food = RanLoc();

		//Configure initial level
		InitialSize = Snake.size();

		//Setup renderer and start draw thread
		myEngine = new SnakeEngine((SESurfaceView) findViewById(R.id.surfaceView), new myDrawer(), EngineTickRate, myContext, this);
		myEngine.myThread.setRunning(true);

		myEngine.Surface.setOnTouchListener(gestureListener);
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
				Log.v("Error killing engine", e.getMessage());
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
			for (Location Point : myAdventure.getCurrentLevel().Walls) {
				if (New.equals(Point)) {
					Good = false;
				}
			}
		}
		return New;
	}

	private final static int TH_ShowDeathDialog = 1;
	private final static int TH_UpdateBar = 2;
	private final static int TH_ShowWinDialog = 3;

	private static final class ThreadHelper extends Handler {
		private AdventureModeActivity myGame;

		ThreadHelper(AdventureModeActivity myGameReferance) {
			myGame = new WeakReference<AdventureModeActivity>(myGameReferance).get();
		}

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == TH_ShowDeathDialog) {				
				AlertDialog.Builder builder = new AlertDialog.Builder(myGame);
				builder.setMessage((String) msg.obj);
				builder.setCancelable(false);
				builder.setPositiveButton("Retry current level", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						//Return snake to original size
						while (myGame.InitialSize != myGame.Snake.size()) {
							myGame.Snake.remove(myGame.Snake.size() - 1);
						}
						
						//Reset to dig end state
						myGame.Snake.get(0).CopyTo(myGame.Snake.get(myGame.Snake.size() - 1));
						myGame.ChangingLevel = true;
						myGame.CurrentMode = Mode.Right;
					}
				});
				builder.setNegativeButton("Quit adventure", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						NavUtils.navigateUpFromSameTask(myGame);
					}
				});
				builder.create();
				builder.show();
			} else if (msg.what == TH_UpdateBar) {
				myGame.ProgressText.setText("Level " + myGame.myAdventure.getCurrentLevelNumber() +  "/" + myGame.myAdventure.Levels.size());

				if (myGame.ChangingLevel) {
					myGame.ScoreText.setText("Digging for level " + myGame.myAdventure.getCurrentLevelNumber() + "...");
					myGame.ProgressText.setText("");
				} else if (myGame.myAdventure.getCurrentLevel().getType() == LevelType.Size) {
					int MiceToGo = myGame.myAdventure.getCurrentLevel().getGoal() - (myGame.Snake.size() - myGame.InitialSize);
					if (MiceToGo == 1) {
						myGame.ScoreText.setText("Eat " + MiceToGo + " mouse");
					} else {
						myGame.ScoreText.setText("Eat " + MiceToGo + " mice");
					}
				} else if (myGame.myAdventure.getCurrentLevel().getType() == LevelType.Movement) {
					myGame.ScoreText.setText("Reach the exit hole");
				} //TODO: Implement the goal display for new level types
			} else if (msg.what == TH_ShowWinDialog) {
				//TODO: Display win info
				AlertDialog.Builder builder = new AlertDialog.Builder(myGame);
				builder.setMessage("You won!");
				builder.setCancelable(false);
				builder.setNegativeButton("Return to menu", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						NavUtils.navigateUpFromSameTask(myGame);
					}
				});
				builder.create();
				builder.show();
			}
		}
	};

	@Override
	public void PauseGame() {
		if (CurrentMode != Mode.Paused) {
			OldMode = CurrentMode;
			CurrentMode = Mode.Paused;

			AlertDialog.Builder builder = new AlertDialog.Builder(myGameReference);
			builder.setMessage("Game Paused");
			builder.setCancelable(false);
			builder.setPositiveButton("Resume", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					CurrentMode = OldMode;
					Boxy = null;
					HideNavBar();
				}
			});
			builder.setNegativeButton("Quit adventure", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					NavUtils.navigateUpFromSameTask(myGameReference);
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
		Paint color_Hole3;
		Paint[] colors_Wall;
		Bitmap bmpBackground = null;
		Bitmap bmpBottomWall = null;
		Bitmap bmpWall = null;
		double SpeedCount = 0;
		boolean DrawExitHole = false;
		ThreadHelper myThreadHelper;

		public myDrawer() {
			myThreadHelper = new ThreadHelper(myGameReference);

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
			color_Hole3 = new Paint();
			color_Hole3.setColor(Color.parseColor("#402912"));

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

				//Draw walls to bmp
				bmpWall = Bitmap.createBitmap(CanvasIn.getWidth(), CanvasIn.getHeight(), Bitmap.Config.ARGB_8888);
				Canvas canvasWall = new Canvas(bmpWall);
				for (Location Block : myAdventure.getCurrentLevel().Walls) {
					GraphicsHelper.addPixel(canvasWall, Block, colors_Wall[(int) (Math.random() * ((2) + 1))], Unit);
				}
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
						if (Snake.get(0).equals(Food) && myAdventure.getCurrentLevel().getType() != LevelType.Movement) {
							//Yum!
							Location NewTail = new Location(0, 0);
							Snake.get(Snake.size() - 1).CopyTo(NewTail);
							Snake.add(NewTail);
							Food = RanLoc();

							//Every 6 inches increase speed
							if (Snake.size() % 3 == 0) {
								if (Speed >= .5 && Speed < .65) {
									Speed += .01;
								} else if (Speed < .5) {
									Speed += .02;
								}
							}

							myThreadHelper.obtainMessage(TH_UpdateBar).sendToTarget();
						} else if (Snake.get(0).X <= -1 || Snake.get(0).X >= GraphicsHelper.SizeOfGame.X + 1 || Snake.get(0).Y <= -1 || Snake.get(0).Y >= GraphicsHelper.SizeOfGame.Y + 1) {
							//Wall hit!
							CurrentMode = Mode.Paused;
							myThreadHelper.obtainMessage(TH_ShowDeathDialog, "You ran into a wall").sendToTarget();
						} else {
							for (Location Block : myAdventure.getCurrentLevel().Walls) {
								if (Snake.get(0).equals(Block)) {
									//Wall hit!
									CurrentMode = Mode.Paused;
									myThreadHelper.obtainMessage(TH_ShowDeathDialog, "You ran into a wall").sendToTarget();
								}
							}
						}

						//TODO: Implement the new level types
						//Check for level completion
						if (myAdventure.getCurrentLevel().getType() == LevelType.Size) {
							//Check current size against initial size and goal size
							if (Snake.size() - InitialSize == myAdventure.getCurrentLevel().getGoal()) {
								AdvanceAdventure();
							}
						} else if (myAdventure.getCurrentLevel().getType() == LevelType.Movement) {
							//Check location
							if (Snake.get(0).equals(myAdventure.getCurrentLevel().getGoalPoint())) {
								AdvanceAdventure();
							}
						}
					}

				//Level switch code
				} else {
					//Check for dig completion
					if (Snake.get(0).equals(Snake.get(Snake.size() - 1))) {
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

						//Draw new walls to bmp
						bmpWall = Bitmap.createBitmap(CanvasIn.getWidth(), CanvasIn.getHeight(), Bitmap.Config.ARGB_8888);
						Canvas canvasWall = new Canvas(bmpWall);
						for (Location Block : myAdventure.getCurrentLevel().Walls) {
							GraphicsHelper.addPixel(canvasWall, Block, colors_Wall[(int) (Math.random() * ((2) + 1))], Unit);
						}

						//Update UI
						myThreadHelper.obtainMessage(TH_UpdateBar).sendToTarget();
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
			if (myAdventure.getCurrentLevel().getType() != LevelType.Movement) {
				GraphicsHelper.addPixel(CanvasIn, Food, color_Mouse, Unit);
			}

			//Draw hole
			if (myAdventure.getCurrentLevel().getType() == LevelType.Movement || ChangingLevel) {
				Location Hole;
				if (ChangingLevel) {
					Hole = Snake.get(0);
				} else {
					Hole = myAdventure.getCurrentLevel().getGoalPoint();
				}
				
				GraphicsHelper.addPixel(CanvasIn, new Location(Hole.X + 1, Hole.Y), color_Hole1, Unit);
				GraphicsHelper.addPixel(CanvasIn, new Location(Hole.X - 1, Hole.Y), color_Hole1, Unit);
				GraphicsHelper.addPixel(CanvasIn, new Location(Hole.X, Hole.Y + 1), color_Hole1, Unit);
				GraphicsHelper.addPixel(CanvasIn, new Location(Hole.X, Hole.Y - 1), color_Hole1, Unit);
				GraphicsHelper.addPixel(CanvasIn, new Location(Hole.X + 1, Hole.Y + 1), color_Hole2, Unit);
				GraphicsHelper.addPixel(CanvasIn, new Location(Hole.X + 1, Hole.Y - 1), color_Hole2, Unit);
				GraphicsHelper.addPixel(CanvasIn, new Location(Hole.X - 1, Hole.Y + 1), color_Hole2, Unit);
				GraphicsHelper.addPixel(CanvasIn, new Location(Hole.X - 1, Hole.Y - 1), color_Hole2, Unit);
				GraphicsHelper.addPixel(CanvasIn, new Location(Hole.X, Hole.Y), color_Hole3, Unit);
			}

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

		private void AdvanceAdventure() {
			boolean DoContinue = myAdventure.advanceLevel();
			Food = new Location(-1, -1);

			if (!DoContinue) {
				//Winner!
				CurrentMode = Mode.Paused;
				myThreadHelper.obtainMessage(TH_ShowWinDialog).sendToTarget();
			} else {
				InitialSize = Snake.size();
				ChangingLevel = true;
			}
			myThreadHelper.obtainMessage(TH_UpdateBar).sendToTarget();
		}
	}
}