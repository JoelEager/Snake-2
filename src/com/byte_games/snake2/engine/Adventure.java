package com.byte_games.snake2.engine;

import java.util.ArrayList;
import java.util.List;
	
//This class is used for generating and maintaining Adventure objects which are used to track the progress and data for an adventure.
//TODO: Flesh out skeleton adventure class

public class Adventure {
	public static enum LevelType {Size, Movement};
	public List<Level> Levels = new ArrayList<Level>();
	
	//Create new adventure object
	public Adventure(int length) {
		for (int count = 1; count <= length; count++) {
			Levels.add(new Level(LevelType.Size, 1));
		}
	}
	
	//TODO: Saving? Maybe?

	//This class represents one level of the adventure and contains data needed to build the level.
	public class Level {
		private LevelType Type;
		private int Length;
		
		Level(LevelType Type, int Length) {
			this.Type = Type;
			this.Length = Length;
		}
		
		public int getLength() {
			return Length;
		}
		
		public LevelType getType() {
			return Type;
		}
	}
}