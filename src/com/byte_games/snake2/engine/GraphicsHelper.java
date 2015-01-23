package com.byte_games.snake2.engine;

import java.util.List;

import android.graphics.Canvas;
import android.graphics.Paint;

//Classes, values, and functions to help with drawing tasks.
//This class is made to be statically accessible so no objects of it are needed.

public class GraphicsHelper {
	//Hardcoded global values for Snake 2 graphics
		public static final Size SizeOfGame = new Size(60, 40); //Zero-based
		public static final Size BackgroundBiomeSize = new Size(15, 10);
	
	//Useful objects
		public static class Size {
			public int X;
			public int Y;
			
			public Size(int XSize, int YSize) {
				X = XSize;
				Y = YSize;
			}
			
			public boolean equals(Size Other) {
				return Other.X == X && Other.Y == Y;
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
		
		public static class Location {
			public int X;
			public int Y;
			
			public Location(int XLocation, int YLocation) {
				X = XLocation;
				Y = YLocation;
			}
			
			public boolean equals(Location Other) {
				return Other.X == X && Other.Y == Y;
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
		
		public static class AnnotatedLocation extends Location {
			public String Type;
			public int Value;
			
			public AnnotatedLocation(int XLocation, int YLocation) {
				super(XLocation, YLocation);
			}
			
			public AnnotatedLocation(int XLocation, int YLocation, String initType, int initValue) {
				super(XLocation, YLocation);
				Type = initType;
				Value = initValue;
			}
			
			public boolean equals(AnnotatedLocation Other) {
				return Other.X == X && Other.Y == Y && Type.equals(Other.Type) && Other.Value == Value;
			}
			
			@Override
			public String toString() {
				return "X = " + X + ", Y = " + Y + " - Annotation: " + Type + " @ " + Value;
			}
			
			public void CopyTo(AnnotatedLocation Target) {
				Target.X = X;
				Target.Y = Y;
				Target.Type = Type;
				Target.Value = Value;
			}
		}
		
	//Useful functions
		//Draws a pixel to the canvas
		public static Canvas addPixel(Canvas CanvasIn, Location Where, Paint Color, float Unit) {
			CanvasIn.drawRect(Where.X * Unit, Where.Y * Unit, (Where.X * Unit) + Unit, (Where.Y * Unit) + Unit, Color);
			return CanvasIn;
		}
		
		//Adds a filled in rectangle with the specified settings to a list of locations
		public static List<Location> AddRect(List<Location> ListIn, Location Where, Size RectSize) {
			for (int countX = 0; countX <= RectSize.X - 1; countX++) {
				for (int countY = 0; countY <= RectSize.Y - 1; countY++) {
					ListIn.add(new Location(Where.X + countX, Where.Y + countY));
				}
			}
			return ListIn;
		}
}