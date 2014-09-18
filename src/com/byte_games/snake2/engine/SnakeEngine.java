package com.byte_games.snake2.engine;

import com.byte_games.snake2.GameActivity;

import android.content.Context;
import android.graphics.Canvas;
import android.view.SurfaceHolder;

	/*
	 * SnakeEngine is the main class that manages all game engine objects.
	 * 		This engine contains all the back end components that are shared between game modes.
	 * 		The game activity classes modify this and the other engine classes to add custom logic.
	 * 
	 * The SnakeEngine package also contains these additional classes:
	 * 		> MyThread (subclass to SnakeEngine) - Provides the game clock and manages game engine thread.
	 * 		> Ticker - Contains the function that myThread calls every tick. That function handles rendering 
	 * 			and game logic. Game activities must override this class and fill in the function.
	 * 		> SESurfaceView - The upgraded SurfaceView that SnakeEngine renders to.
	 * 		> GraphicsHelper - Classes, values, and functions to help with drawing tasks.
	 * 		> TerrainGen - Uses data in terrainData.xml to generate randomized terrain data.
	 * 
	 * GraphicsHelper and TerrainGen are made to be statically accessible so they should be imported and directly referenced.
	 */
	
public class SnakeEngine {
	//Public and private variables:
	public SESurfaceView Surface;
	public Ticker myTicker;
	public MyThread myThread;
	private int TickDelay; //in milisecs
	
	//Set up:
	public SnakeEngine(SESurfaceView inSurface, Ticker overridenTicker, int inTickDelay, Context myContext, GameActivity Owner) {
		Surface = inSurface;
		myTicker = overridenTicker;
		TickDelay = inTickDelay;
		myThread = new MyThread(Surface.getHolder());
		Surface.setCustomObjects(Owner);
	}
	
	//This provides the game clock
	public class MyThread extends Thread {
		private boolean Runing = false;
		private Canvas myCanvas;
		private SurfaceHolder surfaceHolder;

		public MyThread(SurfaceHolder Controller) {
			surfaceHolder = Controller;
		}

		public void setRunning(boolean Run) {
			if (!Runing && Run && getState() == Thread.State.NEW) {
				start();
			}
			Runing = Run;
		}

		@Override
		public void run() {
			super.run();
			while(Runing) {
				myCanvas = surfaceHolder.lockCanvas();
				if (myCanvas != null) {
					//Fire off the game logic and rendering
					myTicker.doTick(myCanvas);
					//Draw the updated canvas
					surfaceHolder.unlockCanvasAndPost(myCanvas);
				}
				try {
					Thread.sleep(TickDelay);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}