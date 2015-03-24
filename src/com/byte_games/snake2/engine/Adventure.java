package com.byte_games.snake2.engine;

import java.util.ArrayList;
import java.util.List;
	
//This class is used for generating and maintaining Adventure objects which are used to track the progress and data for an adventure.

public class Adventure {
	//TODO: Add more level types
	public static enum LevelType {Size};
	public List<Level> Levels = new ArrayList<Level>();
	
	private int CurrentLevel = 0;
	
	//Create new adventure object
	public Adventure(int length) {
		for (int count = 1; count <= length; count++) {
			Levels.add(new Level(LevelType.Size, 5));
		}
	}
	
	public int getCurrentLevelNumber() {
		return CurrentLevel + 1;
	}
	
	public Level getCurrentLevel() {
		return Levels.get(CurrentLevel);
	}
	
	//Returns false if adventure is completed
	public boolean advanceLevel() {
		CurrentLevel++;
		
		if (CurrentLevel == Levels.size()) {
			CurrentLevel = Levels.size() - 1;
			return false;
		}
		return true;
	}
	
	//TODO: Saving? Maybe?

	//This class represents one level of the adventure and contains data needed to build the level.
	public class Level {
		private LevelType Type;
		private int Goal;
		
		Level(LevelType Type, int Goal) {
			this.Type = Type;
			this.Goal = Goal;
		}
		
		public int getGoal() {
			return Goal;
		}
		
		public LevelType getType() {
			return Type;
		}
	}
}