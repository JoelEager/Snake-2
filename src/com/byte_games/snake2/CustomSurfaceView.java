package com.byte_games.snake2;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

//Class used to provide a customized surface view
public class CustomSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
	GameActivity myOwner;

	public CustomSurfaceView(Context context, AttributeSet attrSet) {
		super(context, attrSet);
		getHolder().addCallback(this);
	}
	
	public void setCustomObjects(GameActivity Owner) {
		myOwner = Owner;
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,	int height)	{

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		//Run setup code that requires the completed GUI
		myOwner.finshSetup();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		
	}
}