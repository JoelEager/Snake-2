package com.byte_games.snake2.engine;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.byte_games.snake2.R;
import com.byte_games.snake2.engine.GraphicsHelper.Location;
import com.byte_games.snake2.engine.GraphicsHelper.Size;

//Uses data in terrainData.xml to generate randomized terrain data.
//This class is made to be statically accessible so no objects of it are needed.

public class TerrainGen {
	public static Canvas makeGameBackground(Canvas canvasBackground, float UnitIn, Size SizeOfGame, Size BackgroundBiomeSize, Context myContext) {
		
		//Color arrays - used for filling in biomes
		Paint[] colors_Grass = {new Paint(), new Paint(), new Paint(), new Paint(), new Paint()};
		colors_Grass[0].setColor(Color.parseColor("#00A000"));
		colors_Grass[1].setColor(Color.parseColor("#00A900"));
		colors_Grass[2].setColor(Color.parseColor("#00B300"));
		colors_Grass[3].setColor(Color.parseColor("#00B900"));
		colors_Grass[4].setColor(Color.parseColor("#00C000"));
		
		Paint[] colors_Rock = {new Paint(), new Paint(), new Paint()};
		colors_Rock[0].setColor(Color.parseColor("#606060"));
		colors_Rock[1].setColor(Color.parseColor("#696969"));
		colors_Rock[2].setColor(Color.parseColor("#6D6D6D"));
		
		Paint[] colors_Water = {new Paint(), new Paint(), new Paint()};
		colors_Water[0].setColor(Color.parseColor("#0069BD"));
		colors_Water[1].setColor(Color.parseColor("#0060BE"));
		colors_Water[2].setColor(Color.parseColor("#0059BD"));
		
		//Loop in both directions one biome at a time
		for (int countX = 0; countX <= SizeOfGame.X; countX += BackgroundBiomeSize.X) {
			for (int countY = 0; countY <= SizeOfGame.Y; countY += BackgroundBiomeSize.Y) {
				//Limits for this biome
				Location myUpperLeft = new Location(countX, countY);
				Location myLowerRight = new Location(countX + (BackgroundBiomeSize.X - 1), countY + (BackgroundBiomeSize.Y - 1));
				
				int BiomeType = (int) (Math.random() * ((13) + 1));
				
				if (BiomeType == 0 || BiomeType == 1 || BiomeType == 2 || BiomeType == 3) {
					//Grass patch
					for (int countInnerX = myUpperLeft.X; countInnerX <= myLowerRight.X; countInnerX++) {
						for (int countInnerY = myUpperLeft.Y; countInnerY <= myLowerRight.Y; countInnerY++) {
							GraphicsHelper.addPixel(canvasBackground, new Location(countInnerX, countInnerY), colors_Grass[(int) (Math.random() * ((4) + 1))], UnitIn);
						}
					}
				} else if (BiomeType == 4) {
					final String Terrain = myContext.getString(R.string.Lake_1);
					
					for (int countInnerX = myUpperLeft.X; countInnerX <= myLowerRight.X; countInnerX++) {
						for (int countInnerY = myUpperLeft.Y; countInnerY <= myLowerRight.Y; countInnerY++) {
							String ThisPoint = "~" + (countInnerY - myUpperLeft.Y) + "," + (countInnerX - myUpperLeft.X) + "~";
							if (Terrain.contains(ThisPoint)) {
								GraphicsHelper.addPixel(canvasBackground, new Location(countInnerX, countInnerY), colors_Water[(int) (Math.random() * ((2) + 1))], UnitIn);
							} else {
								GraphicsHelper.addPixel(canvasBackground, new Location(countInnerX, countInnerY), colors_Grass[(int) (Math.random() * ((4) + 1))], UnitIn);
							}
						}
					}
				} else if (BiomeType == 5) {
					final String Terrain = myContext.getString(R.string.Lake_2);

					for (int countInnerX = myUpperLeft.X; countInnerX <= myLowerRight.X; countInnerX++) {
						for (int countInnerY = myUpperLeft.Y; countInnerY <= myLowerRight.Y; countInnerY++) {
							String ThisPoint = "~" + (countInnerY - myUpperLeft.Y) + "," + (countInnerX - myUpperLeft.X) + "~";
							if (Terrain.contains(ThisPoint)) {
								GraphicsHelper.addPixel(canvasBackground, new Location(countInnerX, countInnerY), colors_Water[(int) (Math.random() * ((2) + 1))], UnitIn);
							} else {
								GraphicsHelper.addPixel(canvasBackground, new Location(countInnerX, countInnerY), colors_Grass[(int) (Math.random() * ((4) + 1))], UnitIn);
							}
						}
					}
				} else if (BiomeType == 6) {
					final String Terrain = myContext.getString(R.string.Lake_3);

					for (int countInnerX = myUpperLeft.X; countInnerX <= myLowerRight.X; countInnerX++) {
						for (int countInnerY = myUpperLeft.Y; countInnerY <= myLowerRight.Y; countInnerY++) {
							String ThisPoint = "~" + (countInnerY - myUpperLeft.Y) + "," + (countInnerX - myUpperLeft.X) + "~";
							if (Terrain.contains(ThisPoint)) {
								GraphicsHelper.addPixel(canvasBackground, new Location(countInnerX, countInnerY), colors_Water[(int) (Math.random() * ((2) + 1))], UnitIn);
							} else {
								GraphicsHelper.addPixel(canvasBackground, new Location(countInnerX, countInnerY), colors_Grass[(int) (Math.random() * ((4) + 1))], UnitIn);
							}
						}
					}
				} else if (BiomeType == 7) {
					final String Terrain = myContext.getString(R.string.Lake_4);

					for (int countInnerX = myUpperLeft.X; countInnerX <= myLowerRight.X; countInnerX++) {
						for (int countInnerY = myUpperLeft.Y; countInnerY <= myLowerRight.Y; countInnerY++) {
							String ThisPoint = "~" + (countInnerY - myUpperLeft.Y) + "," + (countInnerX - myUpperLeft.X) + "~";
							if (Terrain.contains(ThisPoint)) {
								GraphicsHelper.addPixel(canvasBackground, new Location(countInnerX, countInnerY), colors_Water[(int) (Math.random() * ((2) + 1))], UnitIn);
							} else {
								GraphicsHelper.addPixel(canvasBackground, new Location(countInnerX, countInnerY), colors_Grass[(int) (Math.random() * ((4) + 1))], UnitIn);
							}
						}
					}
				} else if (BiomeType == 8) {
					final String Terrain = myContext.getString(R.string.Lake_5);

					for (int countInnerX = myUpperLeft.X; countInnerX <= myLowerRight.X; countInnerX++) {
						for (int countInnerY = myUpperLeft.Y; countInnerY <= myLowerRight.Y; countInnerY++) {
							String ThisPoint = "~" + (countInnerY - myUpperLeft.Y) + "," + (countInnerX - myUpperLeft.X) + "~";
							if (Terrain.contains(ThisPoint)) {
								GraphicsHelper.addPixel(canvasBackground, new Location(countInnerX, countInnerY), colors_Water[(int) (Math.random() * ((2) + 1))], UnitIn);
							} else {
								GraphicsHelper.addPixel(canvasBackground, new Location(countInnerX, countInnerY), colors_Grass[(int) (Math.random() * ((4) + 1))], UnitIn);
							}
						}
					}
				} else if (BiomeType == 9) {
					final String Terrain = myContext.getString(R.string.Rock_1);

					for (int countInnerX = myUpperLeft.X; countInnerX <= myLowerRight.X; countInnerX++) {
						for (int countInnerY = myUpperLeft.Y; countInnerY <= myLowerRight.Y; countInnerY++) {
							String ThisPoint = "~" + (countInnerY - myUpperLeft.Y) + "," + (countInnerX - myUpperLeft.X) + "~";
							if (Terrain.contains(ThisPoint)) {
								GraphicsHelper.addPixel(canvasBackground, new Location(countInnerX, countInnerY), colors_Rock[(int) (Math.random() * ((2) + 1))], UnitIn);
							} else {
								GraphicsHelper.addPixel(canvasBackground, new Location(countInnerX, countInnerY), colors_Grass[(int) (Math.random() * ((4) + 1))], UnitIn);
							}
						}
					}
				} else if (BiomeType == 10) {
					final String Terrain = myContext.getString(R.string.Rock_2);

					for (int countInnerX = myUpperLeft.X; countInnerX <= myLowerRight.X; countInnerX++) {
						for (int countInnerY = myUpperLeft.Y; countInnerY <= myLowerRight.Y; countInnerY++) {
							String ThisPoint = "~" + (countInnerY - myUpperLeft.Y) + "," + (countInnerX - myUpperLeft.X) + "~";
							if (Terrain.contains(ThisPoint)) {
								GraphicsHelper.addPixel(canvasBackground, new Location(countInnerX, countInnerY), colors_Rock[(int) (Math.random() * ((2) + 1))], UnitIn);
							} else {
								GraphicsHelper.addPixel(canvasBackground, new Location(countInnerX, countInnerY), colors_Grass[(int) (Math.random() * ((4) + 1))], UnitIn);
							}
						}
					}
				} else if (BiomeType == 11) {
					final String Terrain = myContext.getString(R.string.Rock_3);

					for (int countInnerX = myUpperLeft.X; countInnerX <= myLowerRight.X; countInnerX++) {
						for (int countInnerY = myUpperLeft.Y; countInnerY <= myLowerRight.Y; countInnerY++) {
							String ThisPoint = "~" + (countInnerY - myUpperLeft.Y) + "," + (countInnerX - myUpperLeft.X) + "~";
							if (Terrain.contains(ThisPoint)) {
								GraphicsHelper.addPixel(canvasBackground, new Location(countInnerX, countInnerY), colors_Rock[(int) (Math.random() * ((2) + 1))], UnitIn);
							} else {
								GraphicsHelper.addPixel(canvasBackground, new Location(countInnerX, countInnerY), colors_Grass[(int) (Math.random() * ((4) + 1))], UnitIn);
							}
						}
					}
				} else if (BiomeType == 12) {
					final String Terrain = myContext.getString(R.string.Rock_4);

					for (int countInnerX = myUpperLeft.X; countInnerX <= myLowerRight.X; countInnerX++) {
						for (int countInnerY = myUpperLeft.Y; countInnerY <= myLowerRight.Y; countInnerY++) {
							String ThisPoint = "~" + (countInnerY - myUpperLeft.Y) + "," + (countInnerX - myUpperLeft.X) + "~";
							if (Terrain.contains(ThisPoint)) {
								GraphicsHelper.addPixel(canvasBackground, new Location(countInnerX, countInnerY), colors_Rock[(int) (Math.random() * ((2) + 1))], UnitIn);
							} else {
								GraphicsHelper.addPixel(canvasBackground, new Location(countInnerX, countInnerY), colors_Grass[(int) (Math.random() * ((4) + 1))], UnitIn);
							}
						}
					}
				} else if (BiomeType == 13) {
					final String Terrain = myContext.getString(R.string.Rock_5);

					for (int countInnerX = myUpperLeft.X; countInnerX <= myLowerRight.X; countInnerX++) {
						for (int countInnerY = myUpperLeft.Y; countInnerY <= myLowerRight.Y; countInnerY++) {
							String ThisPoint = "~" + (countInnerY - myUpperLeft.Y) + "," + (countInnerX - myUpperLeft.X) + "~";
							if (Terrain.contains(ThisPoint)) {
								GraphicsHelper.addPixel(canvasBackground, new Location(countInnerX, countInnerY), colors_Rock[(int) (Math.random() * ((2) + 1))], UnitIn);
							} else {
								GraphicsHelper.addPixel(canvasBackground, new Location(countInnerX, countInnerY), colors_Grass[(int) (Math.random() * ((4) + 1))], UnitIn);
							}
						}
					}
				}
			}
		}
		return canvasBackground;
	}
}