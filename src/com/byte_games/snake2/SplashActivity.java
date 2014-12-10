package com.byte_games.snake2;

import com.byte_games.snake2.engine.TerrainGen;
import com.byte_games.snake2.engine.GraphicsHelper.Size;

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

		    	TerrainGen myTerrainGen = new TerrainGen(Unit, SizeOfScreen, new Size(15, 10), getBaseContext());
		    	canvasBackground = myTerrainGen.makeGameBackground(canvasBackground);

		    	android.graphics.drawable.BitmapDrawable Background = new android.graphics.drawable.BitmapDrawable(getResources(), bmpBackground);
		    	BigBox.setBackgroundDrawable(Background);
		    } 
		});
	}
	
	public void play(View sourceView) {
		if (sourceView.getId() == R.id.buttonPlayArcade) {
			startActivity(new Intent(this, ArcadeModeActivity.class));
		} else if (sourceView.getId() == R.id.buttonPlayClassic) {
			startActivity(new Intent(this, ClassicModeActivity.class));
		}
	}
}