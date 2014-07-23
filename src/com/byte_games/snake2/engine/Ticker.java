package com.byte_games.snake2.engine;

import android.graphics.Canvas;

	/* 
	 * Class used to provide function called every tick.
	 * Must be over-riden and object passed to SnakeEngine.
 	 */
	
public abstract class Ticker {
	//Must be over-ridden to work.
	public Canvas doTick(Canvas CanvasIn) {
		
		return CanvasIn;
	}
}