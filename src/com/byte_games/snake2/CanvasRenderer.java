package com.byte_games.snake2;

import android.content.Context;
import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class CanvasRenderer {
	/*
	Version: 1.2
		+ edits made for Snake 2
	*/
	
	//Public and private variables:
	public CustomSurfaceView Surface;
	public Drawer myDrawer;
	public MyThread myThread;
	private int TickDelay; //in milisecs
	
	//Set up:
	public CanvasRenderer(CustomSurfaceView mySurface, Drawer theDrawer, int myTickDelay, Context myContext, GameActivity Owner) {
		Surface = mySurface;
		myDrawer = theDrawer;
		TickDelay = myTickDelay;
		myThread = new MyThread(Surface.getHolder());
		Surface.setCustomObjects(Owner);
	}
	
	//This provides the "clock"
	public class MyThread extends Thread {
		private boolean Runing = false;
		private Canvas mcanvas;
		private SurfaceHolder surfaceHolder;

		public MyThread(SurfaceHolder Controller) {
			surfaceHolder = Controller;
		}

		void setRunning(boolean Run) {
			if (!Runing && Run && getState() == Thread.State.NEW) {
				start();
			}
			Runing = Run;
		}

		@Override
		public void run() {
			super.run();
			while(Runing) {
				mcanvas = surfaceHolder.lockCanvas();
				if (mcanvas != null) {
					//Tell GameActivity's Drawer to update canvas
					myDrawer.doDraw(mcanvas);
					//Draw new canvas
					surfaceHolder.unlockCanvasAndPost(mcanvas);
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