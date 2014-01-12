package com.byte_games.snake2;

import android.graphics.Canvas;

/*	Class used to provide custom drawing function needed for the 
 	CanvasRender class and future Renderers. */

public abstract class Drawer {
	//Must be over-ridden to work.
	public Canvas doDraw(Canvas CanvasIn) {
		//Do drawing
		return CanvasIn;
	}
}