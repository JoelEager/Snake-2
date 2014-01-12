package com.byte_games.snake2;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

//Class with objects to help with drawing tasks

public class GraphicsHelper {
	/*
	Version: 1.0
		+ Edits for Snake 2
	**/
	
	//Custom Objects:
	public static final class Size {
		public int X;
		public int Y;
		
		public Size(int XSize, int YSize) {
			X = XSize;
			Y = YSize;
		}
		
		@Override
		public boolean equals(Object Other) {
			Size OtherS = (Size) Other;
			return OtherS.X == X && OtherS.Y == Y;
		}
		
		@Override
		public String toString() {
			return "X = " + X + ", Y = " + Y;
		}
		
		public void CopyTo(Size Target) {
			Target.X = X;
			Target.Y = Y;
		}
	}
	
	public static final class Location {
		public int X;
		public int Y;
		
		public Location(int XLocation, int YLocation) {
			X = XLocation;
			Y = YLocation;
		}
		
		@Override
		public boolean equals(Object Other) {
			Location OtherL = (Location) Other;
			return OtherL.X == X && OtherL.Y == Y;
		}
		
		@Override
		public String toString() {
			return "X = " + X + ", Y = " + Y;
		}
		
		public void CopyTo(Location Target) {
			Target.X = X;
			Target.Y = Y;
		}
	}
	
	private static float Unit = 0;
	
	private static Canvas addPixel(Canvas CanvasIn, Location Where, Paint Color) {
		CanvasIn.drawRect(Where.X * Unit, Where.Y * Unit, (Where.X * Unit) + Unit, (Where.Y * Unit) + Unit, Color);
		return CanvasIn;
	}
	
	public static Canvas makeGameBackground(Canvas canvasBackground, float UnitIn, Size SizeOfGame, Size BackgroundBiomeSize) {
		Unit = UnitIn;
		
		Paint[] Grass = {new Paint(), new Paint(), new Paint(), new Paint(), new Paint()};
		Grass[0].setColor(Color.parseColor("#00A000"));
		Grass[1].setColor(Color.parseColor("#00A900"));
		Grass[2].setColor(Color.parseColor("#00B300"));
		Grass[3].setColor(Color.parseColor("#00B900"));
		Grass[4].setColor(Color.parseColor("#00C000"));
		
		Paint[] Pebble = {new Paint(), new Paint(), new Paint()};
		Pebble[0].setColor(Color.parseColor("#606060"));
		Pebble[1].setColor(Color.parseColor("#696969"));
		Pebble[2].setColor(Color.parseColor("#6D6D6D"));
		
		Paint[] Water = {new Paint(), new Paint(), new Paint()};
		Water[0].setColor(Color.parseColor("#0069BD"));
		Water[1].setColor(Color.parseColor("#0060BE"));
		Water[2].setColor(Color.parseColor("#0059BD"));
		
		for (int countX = 0; countX <= SizeOfGame.X; countX += BackgroundBiomeSize.X) {
			for (int countY = 0; countY <= SizeOfGame.Y; countY += BackgroundBiomeSize.Y) {
				Location myUpperLeft = new Location(countX, countY);
				Location myLowerRight = new Location(countX + (BackgroundBiomeSize.X - 1), countY + (BackgroundBiomeSize.Y - 1));
				int BiomeType = (int) (Math.random() * ((10) + 1));
				
				if (BiomeType == 0 || BiomeType == 1 || BiomeType == 2 || BiomeType == 3) {
					//Grass patch
					for (int countInnerX = myUpperLeft.X; countInnerX <= myLowerRight.X; countInnerX++) {
						for (int countInnerY = myUpperLeft.Y; countInnerY <= myLowerRight.Y; countInnerY++) {
							addPixel(canvasBackground, new Location(countInnerX, countInnerY), Grass[(int) (Math.random() * ((4) + 1))]);
						}
					}
				} else if (BiomeType == 4) {
					//Lake
					final String Lake =     "Line 0: All Grass" +
											"Line 1:                ~1,6~1,7~1,8~1,9~1,10~" +
											"Line 2:         ~2,4~2,5~2,6~2,7~2,8~2,9~2,10~2,11~" +
											"Line 3:     ~3,3~3,4~3,5~3,6~3,7~3,8~3,9~3,10~3,11~3,12~" +
											"Line 4: ~4,2~4,3~4,4~4,5~4,6~4,7~4,8~4,9~4,10~4,11~" +
											"Line 5:     ~5,3~5,4~5,5~5,6~5,7~5,8~5,9~5,10~" +
											"Line 6:         ~6,4~6,5~6,6~6,7~6,8~6,9~6,10~6,11~" +
											"Line 7:             ~7,5~7,6~7,7~7,8~7,9~7,10~" +
											"Line 8:         ~8,4~8,5~8,6~8,7~8,8~8,9~" +
											"Line 9:             ~9,5~9,6~9,7~" +
											"Line 10: All Grass";
					
					for (int countInnerX = myUpperLeft.X; countInnerX <= myLowerRight.X; countInnerX++) {
						for (int countInnerY = myUpperLeft.Y; countInnerY <= myLowerRight.Y; countInnerY++) {
							String ThisPoint = "~" + (countInnerY - myUpperLeft.Y) + "," + (countInnerX - myUpperLeft.X) + "~";
							if (Lake.contains(ThisPoint)) {
								addPixel(canvasBackground, new Location(countInnerX, countInnerY), Water[(int) (Math.random() * ((2) + 1))]);
							} else {
								addPixel(canvasBackground, new Location(countInnerX, countInnerY), Grass[(int) (Math.random() * ((4) + 1))]);
							}
						}
					}
				} else if (BiomeType == 5) {
					//Lake 2
					final String Lake =  	"Line 0:         ~0,2~" +
											"Line 1:         ~1,2~1,3~                           ~1,10~" +
											"Line 2:             ~2,3~2,4~                       ~2,10~2,11~" +
											"Line 3:                 ~3,4~3,5~" +
											"Line 4:             ~4,3~4,4~4,5~" +
											"Line 5:         ~5,2~5,3~5,4~5,5~5,6~" +
											"Line 6:                 ~6,4~6,5~" +
											"Line 7:                                                             ~7,13~" +
											"Line 8:                                                        ~8,12~8,13~" +
											"Line 9:                                                        ~9,12~" +
											"Line 10:                                                       ~10,12~";

					for (int countInnerX = myUpperLeft.X; countInnerX <= myLowerRight.X; countInnerX++) {
						for (int countInnerY = myUpperLeft.Y; countInnerY <= myLowerRight.Y; countInnerY++) {
							String ThisPoint = "~" + (countInnerY - myUpperLeft.Y) + "," + (countInnerX - myUpperLeft.X) + "~";
							if (Lake.contains(ThisPoint)) {
								addPixel(canvasBackground, new Location(countInnerX, countInnerY), Water[(int) (Math.random() * ((2) + 1))]);
							} else {
								addPixel(canvasBackground, new Location(countInnerX, countInnerY), Grass[(int) (Math.random() * ((4) + 1))]);
							}
						}
					}
				} else if (BiomeType == 6) {
					//Lake 3
					final String Lake =  	"Line 0: " +
											"Line 1:         ~1,2~1,3~" +
											"Line 2:         ~1,2~2,3~2,4~" +
											"Line 3:             ~3,3~3,4~" +
											"Line 4: " +
											"Line 5: " +
											"Line 6: " +
											"Line 7:                                                             ~7,13~" +
											"Line 8:                                                   ~8,11~8,12~8,13~" +
											"Line 9:                                                        ~9,12~9,13~" +
											"Line 10: ";

					for (int countInnerX = myUpperLeft.X; countInnerX <= myLowerRight.X; countInnerX++) {
						for (int countInnerY = myUpperLeft.Y; countInnerY <= myLowerRight.Y; countInnerY++) {
							String ThisPoint = "~" + (countInnerY - myUpperLeft.Y) + "," + (countInnerX - myUpperLeft.X) + "~";
							if (Lake.contains(ThisPoint)) {
								addPixel(canvasBackground, new Location(countInnerX, countInnerY), Water[(int) (Math.random() * ((2) + 1))]);
							} else {
								addPixel(canvasBackground, new Location(countInnerX, countInnerY), Grass[(int) (Math.random() * ((4) + 1))]);
							}
						}
					}
				} else if (BiomeType == 7) {
					//Rock
					final String Pebbles =  "Line 0:         ~0,2~" +
											"Line 1: ~1,0~1,1~1,2~                           ~1,10~" +
											"Line 2:     ~2,1~                           ~2,9~2,10~2,11~" +
											"Line 3:                                         ~3,10~3,11~3,12~" +
											"Line 4:                                 ~4,8~4,9~4,10~4,11~" +
											"Line 5:                                     ~5,9~" +
											"Line 6:                 ~6,4~6,5~" +
											"Line 7:                     ~7,5~7,6~" +
											"Line 8:                 ~8,4~8,5~                              ~8,13~8,14~" +
											"Line 9:                     ~9,5~" +
											"Line 10:        ~10,2~";

					for (int countInnerX = myUpperLeft.X; countInnerX <= myLowerRight.X; countInnerX++) {
						for (int countInnerY = myUpperLeft.Y; countInnerY <= myLowerRight.Y; countInnerY++) {
							String ThisPoint = "~" + (countInnerY - myUpperLeft.Y) + "," + (countInnerX - myUpperLeft.X) + "~";
							if (Pebbles.contains(ThisPoint)) {
								addPixel(canvasBackground, new Location(countInnerX, countInnerY), Pebble[(int) (Math.random() * ((2) + 1))]);
							} else {
								addPixel(canvasBackground, new Location(countInnerX, countInnerY), Grass[(int) (Math.random() * ((4) + 1))]);
							}
						}
					}
				} else if (BiomeType == 8) {
					//Rock 2
					final String Pebbles =  "Line 0:                                         ~0,10~" +
											"Line 1:                                     ~1,9~1,10~" +
											"Line 2: " +
											"Line 3: ~3,0~3,1~" +
											"Line 4:     ~4,1~4,2~" +
											"Line 5:                                     ~5,9~" +
											"Line 6:                 ~6,4~6,5~" +
											"Line 7:                     ~7,5~7,6~                            ~7,13~" +
											"Line 8:                                                          ~8,13~" +
											"Line 9: " +
											"Line 10:        ~10,2~";

					for (int countInnerX = myUpperLeft.X; countInnerX <= myLowerRight.X; countInnerX++) {
						for (int countInnerY = myUpperLeft.Y; countInnerY <= myLowerRight.Y; countInnerY++) {
							String ThisPoint = "~" + (countInnerY - myUpperLeft.Y) + "," + (countInnerX - myUpperLeft.X) + "~";
							if (Pebbles.contains(ThisPoint)) {
								addPixel(canvasBackground, new Location(countInnerX, countInnerY), Pebble[(int) (Math.random() * ((2) + 1))]);
							} else {
								addPixel(canvasBackground, new Location(countInnerX, countInnerY), Grass[(int) (Math.random() * ((4) + 1))]);
							}
						}
					}
				} else if (BiomeType == 9 || BiomeType == 10) {
					//Rock 3
					final String Pebbles =  "Line 0:                      ~0,5~" +
											"Line 1:                  ~1,4~1,5~" +
											"Line 2:                      ~2,5~2,6~" +
											"Line 3: " +
											"Line 4: " +
											"Line 5: " +
											"Line 6:                                           ~6,10~6,11~" +
											"Line 7:                                                ~7,11~7,12~7,13~" +
											"Line 8:                                                          ~8,13~" +
											"Line 9: " +
											"Line 10: ";

					for (int countInnerX = myUpperLeft.X; countInnerX <= myLowerRight.X; countInnerX++) {
						for (int countInnerY = myUpperLeft.Y; countInnerY <= myLowerRight.Y; countInnerY++) {
							String ThisPoint = "~" + (countInnerY - myUpperLeft.Y) + "," + (countInnerX - myUpperLeft.X) + "~";
							if (Pebbles.contains(ThisPoint)) {
								addPixel(canvasBackground, new Location(countInnerX, countInnerY), Pebble[(int) (Math.random() * ((2) + 1))]);
							} else {
								addPixel(canvasBackground, new Location(countInnerX, countInnerY), Grass[(int) (Math.random() * ((4) + 1))]);
							}
						}
					}
				}
			}
		}
		return canvasBackground;
	}
}