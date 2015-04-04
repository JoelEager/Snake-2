package com.byte_games.snake2.engine;

import java.util.ArrayList;
import java.util.List;

import com.byte_games.snake2.engine.GraphicsHelper.Location;
import com.byte_games.snake2.engine.GraphicsHelper.Size;
	
//This class is used for generating and maintaining Adventure objects which are used to track the progress and data for an adventure.

public class Adventure {
	//TODO: Add more level types
	public static enum LevelType {Size};//, Movement};
	public List<Level> Levels = new ArrayList<Level>();
	public final int Difficulty;
	
	private int CurrentLevel = 0;
	
	//Create new adventure object
	public Adventure(int length, int Difficulty) {
		this.Difficulty = Difficulty;
		
		for (int count = 1; count <= length; count++) {
			LevelType myType = null;
			int Goal = 0;
			boolean Good = false;
			
			//Set a random type
			while (!Good) {
				myType = LevelType.values()[(int)(Math.random() * LevelType.values().length)];
				
				if (count == 1) {
					Good = true;
				} else if (Levels.get(count - 2).Type != myType) {
					//It's not a repeat
					Good = true;
				}
				Good = true; //TODO: Remove me
			}
			
			//Configure goal
			//TODO: Balance difficulty and goals
			if (myType == LevelType.Size) {
				Goal = 3 + (Difficulty * 2);
			}
			
			Levels.add(new Level(myType, Goal, Difficulty, count));
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

	//This class creates objects that represent one level of the adventure and contain the data needed to build that level.
	public class Level {
		private LevelType Type;
		private int Goal;
		public List<Location> Walls = new ArrayList<Location>();;
		
		Level(LevelType Type, int Goal, int Difficulty, int LevelCount) {
			this.Type = Type;
			this.Goal = Goal;
			
			//Generate walls
			if (Difficulty == 0) {
				//No walls
			} else {
				final int Levels = 9; //Total num of levels
				int MaxLevel = (Levels / 3) * Difficulty;
				if (LevelCount == 1) {
					MaxLevel = 3;
				}
				int Level = 1 + (int)(Math.random() * ((MaxLevel - 1) + 1));
				
				if (Level == 1) {
					// O    O
					//
					// O    O
					GraphicsHelper.AddRect(Walls, new Location(10, 10), new Size(3, 4));
					GraphicsHelper.AddRect(Walls, new Location(48, 10), new Size(3, 4));
					GraphicsHelper.AddRect(Walls, new Location(10, 27), new Size(3, 4));
					GraphicsHelper.AddRect(Walls, new Location(48, 27), new Size(3, 4));
				} else if (Level == 2) {
					//   |
					//   |
					//   |
					GraphicsHelper.AddRect(Walls, new Location(29, 10), new Size(3, 21));
				} else if (Level == 3) {
					//
					// ---------
					//
					GraphicsHelper.AddRect(Walls, new Location(10, 19), new Size(41, 3));
				} else if (Level == 4) {
					// [=]   [=]
					// 
					// [=]   [=]
					GraphicsHelper.AddRect(Walls, new Location(10, 10), new Size(7, 4));
					GraphicsHelper.AddRect(Walls, new Location(44, 10), new Size(7, 4));
					GraphicsHelper.AddRect(Walls, new Location(10, 27), new Size(7, 4));
					GraphicsHelper.AddRect(Walls, new Location(44, 27), new Size(7, 4));
				} else if (Level == 5) {
					// |     |
					// |     |
					// |     |
					GraphicsHelper.AddRect(Walls, new Location(10, 10), new Size(3, 21));
					GraphicsHelper.AddRect(Walls, new Location(48, 10), new Size(3, 21));
				} else if (Level == 6) {
					// ---------
					// 
					// ---------
					GraphicsHelper.AddRect(Walls, new Location(10, 10), new Size(41, 3));
					GraphicsHelper.AddRect(Walls, new Location(10, 28), new Size(41, 3));
				} else if (Level == 7) {
					// |     |
					// |=====|
					// |     |
					GraphicsHelper.AddRect(Walls, new Location(10, 10), new Size(3, 21));
					GraphicsHelper.AddRect(Walls, new Location(48, 10), new Size(3, 21));
					GraphicsHelper.AddRect(Walls, new Location(13, 19), new Size(35, 3));
				} else if (Level == 8) {
					// ---------
					//  O     O
					// ---------
					GraphicsHelper.AddRect(Walls, new Location(10, 10), new Size(41, 3));
					GraphicsHelper.AddRect(Walls, new Location(10, 28), new Size(41, 3));
					GraphicsHelper.AddRect(Walls, new Location(15, 19), new Size(3, 3));
					GraphicsHelper.AddRect(Walls, new Location(43, 19), new Size(3, 3));
				} else if (Level == 9) {
					// [=] | [=]
					//     |
					// [=] | [=]
					GraphicsHelper.AddRect(Walls, new Location(10, 10), new Size(7, 4));
					GraphicsHelper.AddRect(Walls, new Location(44, 10), new Size(7, 4));
					GraphicsHelper.AddRect(Walls, new Location(10, 27), new Size(7, 4));
					GraphicsHelper.AddRect(Walls, new Location(44, 27), new Size(7, 4));
					GraphicsHelper.AddRect(Walls, new Location(29, 10), new Size(3, 21));
				}
			}
		}
		
		public int getGoal() {
			return Goal;
		}
		
		public LevelType getType() {
			return Type;
		}
	}
}