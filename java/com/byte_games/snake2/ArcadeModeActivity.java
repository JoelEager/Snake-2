package com.byte_games.snake2;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

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
import com.byte_games.snake2.engine.SettingsManager;
import com.byte_games.snake2.engine.SnakeEngine;
import com.byte_games.snake2.engine.TerrainGen;
import com.byte_games.snake2.engine.Ticker;

public class ArcadeModeActivity extends GameActivity {
	private boolean DoneSetup = false;
	private Mode OldMode = Mode.Paused;
	private double Speed = 0.30;
	private List<Location> Snake = new ArrayList<Location>();
	private List<AnnotatedLocation> Walls = new ArrayList<AnnotatedLocation>();
	private int WallSpawnTicker = 1;
	private Location Food;
	private AlertDialog Boxy = null;
	private TextView ScoreText;
	private TextView HighscoreText;
	private int Highscore;
    SettingsManager myHighscore;
	
	protected ArcadeModeActivity myGameReference = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		myContext = getBaseContext();
		ScoreText = (TextView) findViewById(R.id.textScore);
		HighscoreText = (TextView) findViewById(R.id.textHighscore);
        ((TextView) findViewById(R.id.textLevel)).setText("");
		
		//Setup game variables
		Snake.add(new Location(5, 5));
		Snake.add(new Location(4, 5));
		Snake.add(new Location(3, 5));
		Snake.add(new Location(2, 5));
		Snake.add(new Location(1, 5));
		Food = RanLoc();

		//Setup renderer and start draw thread
		myEngine = new SnakeEngine((SESurfaceView) findViewById(R.id.surfaceView), new myDrawer(), EngineTickRate, myContext, this);
		myEngine.myThread.setRunning(true);
        
        myEngine.Surface.setOnTouchListener(gestureListener);
        
        //Get highscore
        myHighscore = new SettingsManager(this, "Highscores", "Arcade", 0);
        Highscore = myHighscore.getInt();
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
	public void finishSetup() {
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

            showTutorial();
			
			DoneSetup = true;
		}
	}

    @Override
    protected void continueTutorial() {
        if (true) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Arcade mode");
            builder.setMessage("Goal: Eat mice (the white pixels) to grow longer.\n\n" +
                    "Your current score is displayed as the snake's current length (top left) and your highscore is in the top right.\n\n" +
                    "Additional walls will spawn as you get longer.");
            builder.setCancelable(false);
            builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    CurrentMode = Mode.Right;
                }
            });
            builder.create();
            builder.show();
        } else {
            CurrentMode = Mode.Right;
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
	
	protected AnnotatedLocation RanWallLoc() {
		Location Min = new Location(2, 2);
		Location Max = new Location(GraphicsHelper.SizeOfGame.X - 2, GraphicsHelper.SizeOfGame.Y - 2);
		boolean Good = false;
		AnnotatedLocation New = new AnnotatedLocation(0, 0);
		
		while (!Good) {
			New.X = Min.X + (int)(Math.random() * ((Max.X - Min.X) + 1));
			New.Y = Min.Y + (int)(Math.random() * ((Max.Y - Min.Y) + 1));
			Good = true;
			
			for (Location Point : Snake) {
				if (Point.X >= New.X - 1 && Point.X <= New.X + 1) {
					if (Point.Y >= New.Y - 1 && Point.Y <= New.Y + 1) {
						Good = false;
					}
				}
			}
			//(1 pixel margin between new wall and all other walls to avoid enclosing the mouse.)
			for (Location Point : Walls) {
				if (Point.X >= New.X - 2 && Point.X <= New.X + 2) {
					if (Point.Y >= New.Y - 2 && Point.Y <= New.Y + 2) {
						Good = false;
					}
				}
			}
			if (Food.X >= New.X - 1 && Food.X <= New.X + 1) {
				if (Food.Y >= New.Y - 1 && Food.Y <= New.Y + 1) {
					Good = false;
				}
			}
			
			//Check 10 spaces in front of the snake
			Location SnakeTest = new Location(0, 0);
			Snake.get(0).CopyTo(SnakeTest);
			for (int TestCount = 1; TestCount <= 10; TestCount++) {
				if (CurrentMode == Mode.Left) {
					SnakeTest.X -= 1;
				} else if (CurrentMode == Mode.Right) {
					SnakeTest.X += 1;
				} else if (CurrentMode == Mode.Up) {
					SnakeTest.Y -= 1;
				} else if (CurrentMode == Mode.Down) {
					SnakeTest.Y += 1;
				}
				
				if (SnakeTest.X >= New.X - 1 && SnakeTest.X <= New.X + 1) {
					if (SnakeTest.Y >= New.Y - 1 && SnakeTest.Y <= New.Y + 1) {
						Good = false;
						break;
					}
				}
			}
		}
		return New;
	}

	private final static int TH_ShowDeathDialog = 1;
	private final static int TH_UpdateBar = 2;
	
	private static final class ThreadHelper extends Handler {
		private ArcadeModeActivity myGame;
		
		ThreadHelper(ArcadeModeActivity myGameReferance) {
			myGame = new WeakReference<ArcadeModeActivity>(myGameReferance).get();
		}
		
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == TH_ShowDeathDialog) {
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

					myGame.myHighscore.putInt(myGame.Highscore);
				}
				
				myGame.ScoreText.setText(lengthToString(myGame.Snake.size()));
			}
		}
	};
	
	public void PauseGame() {
		if (CurrentMode != Mode.Paused) {
			OldMode = CurrentMode;
			CurrentMode = Mode.Paused;

			AlertDialog.Builder builder = new AlertDialog.Builder(myGameReference);
			builder.setMessage("Game paused");
			builder.setCancelable(false);
			builder.setPositiveButton("Resume", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
                    Boxy = null;
                    HideNavBar();
                    UnpauseGame(OldMode);
                }
            });
			builder.setNegativeButton("Return to menu", new DialogInterface.OnClickListener() {
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
		Paint[] colors_Wall;
		Bitmap bmpBackground = null;
		Bitmap bmpBottomWall = null;
		double SpeedCount = 0;
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
						
						//Every 6 inches increase speed
						if (Snake.size() % 3 == 0) {
							if (Speed >= .5 && Speed < .65) {
								Speed += .01;
							} else if (Speed < .5) {
								Speed += .02;
							}
						}
						
						Food = RanLoc();
						myThreadHelper.obtainMessage(TH_UpdateBar).sendToTarget();

						//Wall management code
						int MaxWallBlocks = 15 * 9;
						WallSpawnTicker--;
						
						if (WallSpawnTicker == 0) {
							WallSpawnTicker = 3;
							
							//Add 1-3 walls based on current wall count
							int wallsToAdd = 1;
							if (Walls.size() == 0) { wallsToAdd = 3; }
							if (Walls.size() == MaxWallBlocks) { wallsToAdd = 3; }
							
							for (int count = 0; count != wallsToAdd; count++) {
								//Remove a wall if there's too many
								if (Walls.size() >= MaxWallBlocks) {
									for (int RemoveCount = 1; RemoveCount != 10; RemoveCount++) {
										Walls.remove(0);
									}
								}
								
								//Spawn a wall
								AnnotatedLocation Wall = RanWallLoc();
								
								//Add 3x3 block of walls
								for (int countX = -1; countX <= 1; countX++) {
									for (int countY = -1; countY <= 1; countY++) {
										AnnotatedLocation WallPart = new AnnotatedLocation(0, 0);
										Wall.CopyTo(WallPart);
										WallPart.NumValue = (int)(Math.random() * ((2) + 1));
										
										WallPart.X += countX;
										WallPart.Y += countY;
								
										Walls.add(WallPart);
									}
								}
							}
						}
					} else if (Snake.get(0).X <= -1 || Snake.get(0).X >= GraphicsHelper.SizeOfGame.X + 1 || Snake.get(0).Y <= -1 || Snake.get(0).Y >= GraphicsHelper.SizeOfGame.Y + 1) {
						//Edge of game wall hit
						CurrentMode = Mode.Paused;
						myThreadHelper.obtainMessage(TH_ShowDeathDialog, "You ran into a wall").sendToTarget();
					} else {
                        for (Location Block : Walls) {
                            if (Block.equals(Snake.get(0))) {
                                //Wall hit
                                CurrentMode = Mode.Paused;
                                myThreadHelper.obtainMessage(TH_ShowDeathDialog, "You ran into a wall").sendToTarget();
                                break;
                            }
                        }
                        for (int count = 1; count < Snake.size(); count++) {
                            if (Snake.get(count).equals(Snake.get(0))) {
                                //Snake hit
                                CurrentMode = Mode.Paused;
                                myThreadHelper.obtainMessage(TH_ShowDeathDialog, "You ran into yourself").sendToTarget();
                                break;
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
			
			//Draw and update walls
			for (AnnotatedLocation Block : Walls) {
				GraphicsHelper.addPixel(CanvasIn, Block, colors_Wall[Block.NumValue], Unit);
			}
			
			CanvasIn.drawBitmap(bmpBottomWall, 0, (GraphicsHelper.SizeOfGame.Y + 1) * Unit, new Paint());
			return CanvasIn;
		}
	}
}