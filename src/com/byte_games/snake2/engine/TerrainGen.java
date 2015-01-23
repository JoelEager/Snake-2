package com.byte_games.snake2.engine;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.byte_games.snake2.R;
import com.byte_games.snake2.engine.GraphicsHelper.Location;
import com.byte_games.snake2.engine.GraphicsHelper.Size;

//Uses data in terrainData.xml to generate randomized terrain data.

public class TerrainGen {
	private Paint[] colors_Grass = {new Paint(), new Paint(), new Paint(), new Paint(), new Paint()};
	private Paint[] colors_Rock = {new Paint(), new Paint(), new Paint()};
	private Paint[] colors_Lake = {new Paint(), new Paint(), new Paint()};
	private float Unit;
	private Size SizeOfGame;
	private Size BackgroundBiomeSize;
	private	Context myContext;
	
	public TerrainGen(float Unit, Size SizeOfGame, Size BackgroundBiomeSize, Context myContext) {
		this.Unit = Unit;
		this.SizeOfGame = SizeOfGame;
		this.BackgroundBiomeSize = BackgroundBiomeSize;
		this.myContext = myContext;
		
		//Initialize color arrays
		colors_Grass[0].setColor(Color.parseColor("#00A000"));
		colors_Grass[1].setColor(Color.parseColor("#00A900"));
		colors_Grass[2].setColor(Color.parseColor("#00B300"));
		colors_Grass[3].setColor(Color.parseColor("#00B900"));
		colors_Grass[4].setColor(Color.parseColor("#00C000"));
		
		colors_Rock[0].setColor(Color.parseColor("#606060"));
		colors_Rock[1].setColor(Color.parseColor("#696969"));
		colors_Rock[2].setColor(Color.parseColor("#6D6D6D"));
		
		colors_Lake[0].setColor(Color.parseColor("#0069BD"));
		colors_Lake[1].setColor(Color.parseColor("#0060BE"));
		colors_Lake[2].setColor(Color.parseColor("#0059BD"));
	}
	
	public Canvas makeGameBackground(Canvas canvasBackground) {
		final String[] BiomeData = myContext.getResources().getStringArray(R.array.BiomeDefs);
		final String[] BiomeTypes = myContext.getResources().getStringArray(R.array.BiomeTypes);
		final int GrassWeight = (int) (BiomeTypes.length * 0.5); //Relative likelihood of biome being grass
		
		//Loop in both directions one biome at a time
		for (int countX = 0; countX <= SizeOfGame.X; countX += BackgroundBiomeSize.X) {
			for (int countY = 0; countY <= SizeOfGame.Y; countY += BackgroundBiomeSize.Y) {
				//Limits for this biome
				Location myUpperLeft = new Location(countX, countY);
				Location myLowerRight = new Location(countX + (BackgroundBiomeSize.X - 1), countY + (BackgroundBiomeSize.Y - 1));
				
				//Chose biome
				int OptionsIndex = GrassWeight + BiomeTypes.length - 1;
				int BiomeTypeIndex = (int) (Math.random() * OptionsIndex);
				
				//Render it
				if (BiomeTypeIndex <= BiomeTypes.length - 1) {
					//It's a biome
					for (int countInnerX = myUpperLeft.X; countInnerX <= myLowerRight.X; countInnerX++) {
						for (int countInnerY = myUpperLeft.Y; countInnerY <= myLowerRight.Y; countInnerY++) {
							String ThisPoint = "~" + (countInnerY - myUpperLeft.Y) + "," + (countInnerX - myUpperLeft.X) + "~";
							if (BiomeData[BiomeTypeIndex].contains(ThisPoint)) {
								GraphicsHelper.addPixel(canvasBackground, new Location(countInnerX, countInnerY), choseColor(BiomeTypes[BiomeTypeIndex]), Unit);
							} else {
								GraphicsHelper.addPixel(canvasBackground, new Location(countInnerX, countInnerY), choseColor("Grass"), Unit);
							}
						}
					}
				} else {
					//It's grass
					for (int countInnerX = myUpperLeft.X; countInnerX <= myLowerRight.X; countInnerX++) {
						for (int countInnerY = myUpperLeft.Y; countInnerY <= myLowerRight.Y; countInnerY++) {
							GraphicsHelper.addPixel(canvasBackground, new Location(countInnerX, countInnerY), choseColor("Grass"), Unit);
						}
					}
				}
			}
		}
		return canvasBackground;
	}
	
	private Paint choseColor(String objType) {
		//Chose a random color from the correct array
		if (objType.equals("Grass")) {
			return colors_Grass[(int) (Math.random() * ((4) + 1))];
		} else if (objType.equals("Rock")) {
			return colors_Rock[(int) (Math.random() * ((2) + 1))];
		} else if (objType.equals("Lake")) {
			return colors_Lake[(int) (Math.random() * ((2) + 1))];
		}
		return null;
	}
}