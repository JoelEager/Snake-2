package com.byte_games.snake2.engine;

import android.graphics.Canvas;
import android.graphics.Paint;

//Classes, values, and functions to help with drawing tasks.
//This class is made to be statically accessible so no objects of it are needed.

public class GraphicsHelper {
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
	

	public static final Size SizeOfGame = new Size(60, 40); //Zero-based
	public static final Size BackgroundBiomeSize = new Size(15, 10);
	
	public static Canvas addPixel(Canvas CanvasIn, Location Where, Paint Color, float Unit) {
		CanvasIn.drawRect(Where.X * Unit, Where.Y * Unit, (Where.X * Unit) + Unit, (Where.Y * Unit) + Unit, Color);
		return CanvasIn;
	}
}