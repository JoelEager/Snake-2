package com.byte_games.snake2;

import com.byte_games.snake2.GraphicsHelper.*;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class ClassicGameActivity extends GameActivity {
	private Context myContext;
	private CanvasRenderer myRenderer;
	private boolean DoneSetup = false;
	
	private Mode CurrentMode = Mode.Right;
	private Mode OldMode = Mode.Paused;
	private double Speed = 0.17;
	private List<Location> Snake = new ArrayList<Location>();
	private Location Food;
	private AlertDialog Boxy = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		myContext = getBaseContext();
		
		//Setup game variables
		Snake.add(new Location(10, 10));
		Snake.add(new Location(9, 10));
		Snake.add(new Location(8, 10));
		Food = RanLoc(Snake, new Location(0, 0), new Location(SizeOfGame.X, SizeOfGame.Y));

		//Setup renderer and start draw thread
		myRenderer = new CanvasRenderer((CustomSurfaceView) findViewById(R.id.surfaceView), new myDrawer(), 10, myContext, this);
		myRenderer.myThread.setRunning(true);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		PauseGame();
	}
	
	@Override
	protected void onDestroy() {
		//Kill thread
		myRenderer.myThread.setRunning(false);
		boolean retry = true;
		while(retry) {
			try	{
				myRenderer.myThread.join();
				retry = false;
			} catch(Exception e) {
				Log.v("Exception while ending drawer thread.", e.getMessage());
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
	void finshSetup() {
		if (!DoneSetup) {
			//Setup game GUI
			RelativeLayout BigBox = (RelativeLayout) findViewById(R.id.bigBox);
			RelativeLayout PanelL = (RelativeLayout) findViewById(R.id.panelL);
			RelativeLayout PanelR = (RelativeLayout) findViewById(R.id.panelR);

			Unit = BigBox.getHeight() / (SizeOfGame.Y + 1);
			float GameX = Unit * (SizeOfGame.X + 1);
			float PanelX = (BigBox.getWidth() - GameX) / 2;

			android.widget.RelativeLayout.LayoutParams LayoutDim = (android.widget.RelativeLayout.LayoutParams) PanelL.getLayoutParams();
			LayoutDim.width = (int) Math.floor(PanelX);
			PanelL.setLayoutParams(LayoutDim);
			LayoutDim = (android.widget.RelativeLayout.LayoutParams) PanelR.getLayoutParams();
			LayoutDim.width = (int) Math.floor(PanelX); 
			PanelR.setLayoutParams(LayoutDim);
			
			//Make Panel's Background
			Bitmap bmpBackground = Bitmap.createBitmap((int) (25 * Unit), (int) (60 * Unit), Bitmap.Config.ARGB_8888);
			Canvas canvasBackground = new Canvas(bmpBackground);
			canvasBackground.drawColor(Color.parseColor("#505050"));
			
			Paint[] Rocks = {new Paint(), new Paint(), new Paint()};
			Rocks[0].setColor(Color.parseColor("#393939"));
			Rocks[1].setColor(Color.parseColor("#353535"));
			Rocks[2].setColor(Color.parseColor("#303030"));
			
			int EndPixX = (int) ((PanelX / Unit) + 1);
			int EndPixY = (int) ((BigBox.getHeight() / Unit) + 1);
			
			for (int countX = 0; countX <= EndPixX; countX++) {
				for (int countY = 0; countY <= EndPixY; countY++) {
					addPixel(canvasBackground, new Location(countX, countY), Rocks[(int) (Math.random() * ((2) + 1))]);
				}
			}

			android.graphics.drawable.BitmapDrawable Background = new android.graphics.drawable.BitmapDrawable(getResources(), bmpBackground);
			Background.setTileModeXY(android.graphics.Shader.TileMode.CLAMP, android.graphics.Shader.TileMode.CLAMP);
			Background.setBounds(0, 0, (int) PanelX, BigBox.getHeight());
			Background.setGravity(Gravity.CLIP_HORIZONTAL|Gravity.RIGHT);
			PanelL.setBackgroundDrawable(Background);
			Background.setGravity(Gravity.LEFT);
			PanelR.setBackgroundDrawable(Background);
			
			DoneSetup = true;
		}
	}
	
	private final static int TH_ShowDeathDialog = 1;
	private final static int TH_UpdateActionBar = 2;
	private ClassicGameActivity ThisGame = this;
	
	@SuppressLint("HandlerLeak")
	private final Handler ThreadHelper = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == TH_ShowDeathDialog){
				CurrentMode = Mode.Paused;
				AlertDialog.Builder builder = new AlertDialog.Builder(ThisGame);
				builder.setTitle("You died.");
				builder.setMessage((String) msg.obj);
				builder.setCancelable(false);
				builder.setPositiveButton("Return to menu", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						NavUtils.navigateUpFromSameTask(ThisGame);
					}
				});
				builder.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = getIntent();
						finish();
						startActivity(intent);
					}
				});
				builder.create();
				builder.show();
			} else if(msg.what == TH_UpdateActionBar) {
				int Inches = Snake.size() * 2;
				int Feet = Inches / 12;
				Inches -= Feet * 12;
				String Text = "You're ";
				if (Feet == 1) {
					Text += Feet + " foot and ";
				} else {
					Text += Feet + " feet and ";
				}
				if (Inches == 1) {
					Text += Inches + " inch long";
				} else {
					Text += Inches + " inches long";
				}
				setTitle(Text);
			}
		}
	};
	
	public void changeMode(View sourceView) {
		if (CurrentMode != Mode.Paused) {
			if (sourceView.getId() == R.id.imgLeft) {
				CurrentMode = Mode.Left;
			} else if (sourceView.getId() == R.id.imgRight) {
				CurrentMode = Mode.Right;
			} else if (sourceView.getId() == R.id.imgUp) {
				CurrentMode = Mode.Up;
			} else if (sourceView.getId() == R.id.imgDown) {
				CurrentMode = Mode.Down;
			} else if (sourceView.getId() == R.id.imgPause) {
				PauseGame();
			}
		}
	}
	
	private void PauseGame() {
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
				}
			});
			builder.setNegativeButton("Quit", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					NavUtils.navigateUpFromSameTask(ThisGame);
					Boxy = null;
				}
			});
			builder.create();
			Boxy = builder.show();
		}
	}

	public class myDrawer extends Drawer {
		Paint SnakeHead;
		Paint SnakeBody1;
		Paint SnakeBody2;
		Paint Mouse;
		Paint TextPaint;
		Bitmap bmpBackground = null;
		Bitmap bmpWall = null;
		double SpeedCount = 0;

		public myDrawer() {
			SnakeHead = new Paint();
			SnakeHead.setColor(Color.rgb(190, 14, 14));
			SnakeBody1 = new Paint();
			SnakeBody1.setColor(Color.rgb(214, 60, 0));
			SnakeBody2 = new Paint();
			SnakeBody2.setColor(Color.rgb(214, 80, 0));
			Mouse = new Paint();
			Mouse.setColor(Color.WHITE);
			TextPaint = new Paint();
			TextPaint.setColor(Color.BLACK);
			TextPaint.setTextSize(20);
			ThreadHelper.obtainMessage(TH_UpdateActionBar).sendToTarget();
		}

		@Override
		public Canvas doDraw(Canvas CanvasIn) {
			//Draw Background
			if (bmpBackground == null) {
				//Make Game Background
				bmpBackground = Bitmap.createBitmap(CanvasIn.getWidth(), CanvasIn.getHeight(), Bitmap.Config.ARGB_8888);
				Canvas canvasBackground = new Canvas(bmpBackground);
				canvasBackground.drawColor(Color.parseColor("#005000"));
				canvasBackground = com.byte_games.snake2.GraphicsHelper.makeGameBackground(canvasBackground, Unit, SizeOfGame, BackgroundBiomeSize);
				
				//Make Rocky Background
				bmpWall = Bitmap.createBitmap(CanvasIn.getWidth(), (int) (CanvasIn.getHeight() - ((SizeOfGame.Y + 1) * Unit)), Bitmap.Config.ARGB_8888);
				Canvas canvasWallBackground = new Canvas(bmpWall);
				canvasWallBackground.drawColor(Color.parseColor("#505050"));
				
				Paint[] Rocks = {new Paint(), new Paint(), new Paint()};
				Rocks[0].setColor(Color.parseColor("#393939"));
				Rocks[1].setColor(Color.parseColor("#353535"));
				Rocks[2].setColor(Color.parseColor("#303030"));
				
				int EndY = (int) (canvasWallBackground.getHeight() / Unit) + 1; 
				for (int countX = 0; countX <= SizeOfGame.X; countX++) {
					for (int countY = 0; countY <= EndY; countY++) {
						addPixel(canvasWallBackground, new Location(countX, countY), Rocks[(int) (Math.random() * ((2) + 1))]);
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
						Food = RanLoc(Snake, new Location(0, 0), new Location(SizeOfGame.X, SizeOfGame.Y));
						ThreadHelper.obtainMessage(TH_UpdateActionBar).sendToTarget();
					} else if (Snake.get(0).X <= -1 || Snake.get(0).X >= SizeOfGame.X + 1 || Snake.get(0).Y <= -1 || Snake.get(0).Y >= SizeOfGame.Y + 1) {
						//Wall hit!
						CurrentMode = Mode.Paused;
						ThreadHelper.obtainMessage(TH_ShowDeathDialog, "Ouch, that wall must've hurt!").sendToTarget();
					}
				}
			}

			//Draw Mouse
			addPixel(CanvasIn, Food, Mouse);

			//Draw Snake
			for (int SnakeDrawCount = Snake.size() - 1; SnakeDrawCount >= 0; SnakeDrawCount--) {
				Location SnakePart = Snake.get(SnakeDrawCount);
				if (SnakeDrawCount == 0) {
					addPixel(CanvasIn, SnakePart, SnakeHead);
				} else if (SnakeDrawCount % 2 == 1) { //it's an odd number
					addPixel(CanvasIn, SnakePart, SnakeBody1);
				} else { //it's an even number
					addPixel(CanvasIn, SnakePart, SnakeBody2);
				}
			}
			
			CanvasIn.drawBitmap(bmpWall, 0, (SizeOfGame.Y + 1) * Unit, new Paint());
			return CanvasIn;
		}
		
		
	}
}