package com.byte_games.snake2;

import com.byte_games.snake2.engine.TerrainGen;
import com.byte_games.snake2.engine.GraphicsHelper.Size;
import android.net.Uri;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.LayoutInflater;
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
	
	@SuppressLint("InflateParams")
	public void play(View sourceView) {
		if (sourceView.getId() == R.id.buttonPlayArcade) {
			startActivity(new Intent(this, ArcadeModeActivity.class));
		} else if (sourceView.getId() == R.id.buttonPlayClassic) {
			startActivity(new Intent(this, ClassicModeActivity.class));
		} else if (sourceView.getId() == R.id.buttonPlayAdventure) {
			final SplashActivity ActivityPointer = this;
			
			//Make and show start adventure dialog
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			LayoutInflater inflater = getLayoutInflater();
			View DialogLayout = inflater.inflate(R.layout.start_adventure_dialog, null);
			
			final android.widget.Spinner spinnerAdventureLength = (android.widget.Spinner) DialogLayout.findViewById(R.id.spinnerAdventureLength);
			
			builder.setView(DialogLayout);
			builder.setTitle("Choose your adventure:");
			builder.setPositiveButton("Play", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					int NumOfLevels = Integer.parseInt(spinnerAdventureLength.getSelectedItem().toString().substring(0, 1));
					
					Intent myIntent = new Intent(ActivityPointer, AdventureModeActivity.class);
					myIntent.putExtra("com.byte_games.snake2.Adventure_NumOfLevels", NumOfLevels);
			    	startActivity(myIntent);
				}
			});
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					//Nothing interesting here...
				}
			});
			final AlertDialog Boxy = builder.create();
			Boxy.show();
		}
	}
	
	public void email(View sourceView) {
		Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto","snake2beta@byte-games.com", null));
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Bug report or idea for Snake 2");
		startActivity(Intent.createChooser(emailIntent, "Send email..."));
	}
}