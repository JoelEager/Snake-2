package com.byte_games.snake2;

import com.byte_games.snake2.GraphicsHelper.Size;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.RelativeLayout;

public class SplashActivity extends Activity {

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		//Wait until layout is drawn
		RelativeLayout BigBox = (RelativeLayout) findViewById(R.id.SplashLayout);
		ViewTreeObserver vto = BigBox.getViewTreeObserver(); 
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() { 
		    @Override 
		    public void onGlobalLayout() {
		    	//Make background
		    	RelativeLayout BigBox = (RelativeLayout) findViewById(R.id.SplashLayout);
		    	Bitmap bmpBackground = Bitmap.createBitmap(BigBox.getWidth(), BigBox.getHeight(), Bitmap.Config.ARGB_8888);
		    	Canvas canvasBackground = new Canvas(bmpBackground);

		    	float Unit = BigBox.getWidth() / 90;
		    	Size SizeOfScreen = new Size(90, (int) (BigBox.getHeight() / Unit));

		    	canvasBackground = com.byte_games.snake2.GraphicsHelper.makeGameBackground(canvasBackground, Unit, SizeOfScreen, new Size(15, 10));

		    	android.graphics.drawable.BitmapDrawable Background = new android.graphics.drawable.BitmapDrawable(getResources(), bmpBackground);
		    	BigBox.setBackgroundDrawable(Background);
		    } 
		});
	}
	
	public void play(View sourceView) {
		if (sourceView.getId() == R.id.ButtonPlayV2) {
			startActivity(new Intent(this, v2GameActivity.class));
		} else if (sourceView.getId() == R.id.buttonPlayClassic) {
			startActivity(new Intent(this, ClassicGameActivity.class));
		}
	}
}