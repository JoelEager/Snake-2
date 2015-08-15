package com.byte_games.snake2.engine;

import java.util.ArrayList;
import java.util.List;

import com.byte_games.snake2.engine.GraphicsHelper.Location;
import com.byte_games.snake2.engine.GraphicsHelper.Size;

//This class is used for generating and maintaining Adventure objects which are used to track the progress and data for an adventure.

public class Adventure {
	//TODO: Add more level types
	public static enum LevelType {Size, Movement};
	public List<Level> Levels = new ArrayList<Level>();
	public final int Difficulty;

	private int CurrentLevel = 0;
	private int LastLevelLayoutIndex = -1; //Used in wall generation to prevent repeats
    private int DeathCount = 0;

	//Create new adventure object
	public Adventure(int length, int Difficulty) {
		this.Difficulty = Difficulty; //0 to 1

		for (int count = 1; count <= length; count++) {
			LevelType myType = null;
			int Goal = 0;

			//Set a random type
			myType = LevelType.values()[(int)(Math.random() * LevelType.values().length)];

			//Turn off layout check if this level does not have the same type as the last level
			if (count == 1) {
				LastLevelLayoutIndex = -1;
			} else if (Levels.get(count - 2).Type != myType) {
				//It's not a repeat
				LastLevelLayoutIndex = -1;
			}

			//Configure goal
			//TODO: Balance difficulty and goals
			if (myType == LevelType.Size) {
				Goal = 5 + (Difficulty * 2);
			} else if (myType == LevelType.Movement) {
				Goal = 1;
			}

			Levels.add(new Level(myType, Goal, Difficulty));
		}
	}

	public int getCurrentLevelNumber() {
		return CurrentLevel + 1;
	}

	public Level getCurrentLevel() {
		return Levels.get(CurrentLevel);
	}

    public int getDeathCount() {
        return DeathCount;
    }

    public void advanceDeathCount() {
        DeathCount++;
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
		private Location GoalPoint = null;
		public List<Location> Walls = new ArrayList<Location>();

		Level(LevelType Type, int Goal, int Difficulty) {
			this.Type = Type;
			this.Goal = Goal;

			//Init level based on type
			if (Type == LevelType.Size) {
				ConfigSizeLevel(Difficulty);
			} else if (Type == LevelType.Movement) {
				ConfigMovementLevel(Difficulty);
			}

			//For debugging: 
			/*Type = LevelType.Movement;
			Goal = 1;
			Walls.clear();
			<wall code> */
			//----------------
		}

		public int getGoal() {
			return Goal;
		}

		public LevelType getType() {
			return Type;
		}

		public Location getGoalPoint() {
			return GoalPoint;
		}

		private void ConfigSizeLevel(int Difficulty) {
            final int Layouts = 9; //Total num of layouts
            int Layout = 1 + (int)(Math.random() * ((Layouts - 1) + 1));

			//Check for repeated wall layouts
			boolean Good = false;
			while (!Good) {
				if (Layout == LastLevelLayoutIndex) {
                    Layout = 1 + (int)(Math.random() * ((Layouts - 1) + 1));
				} else {
					Good = true;
				}
			}
			LastLevelLayoutIndex = Layout;

			//Generate walls
            if (Difficulty == 0) {
                if (Layout == 1) {
                    // O    O
                    //
                    // O    O
                    GraphicsHelper.AddRect(Walls, new Location(10, 10), new Size(3, 4));
                    GraphicsHelper.AddRect(Walls, new Location(48, 10), new Size(3, 4));
                    GraphicsHelper.AddRect(Walls, new Location(10, 27), new Size(3, 4));
                    GraphicsHelper.AddRect(Walls, new Location(48, 27), new Size(3, 4));
                } else if (Layout == 2) {
                    //   |
                    //   |
                    //   |
                    GraphicsHelper.AddRect(Walls, new Location(29, 10), new Size(3, 21));
                } else if (Layout == 3) {
                    //
                    // ---------
                    //
                    GraphicsHelper.AddRect(Walls, new Location(10, 19), new Size(41, 3));
                } else if (Layout == 4) {
                    // |     |
                    // |     |
                    // |     |
                    GraphicsHelper.AddRect(Walls, new Location(10, 10), new Size(3, 21));
                    GraphicsHelper.AddRect(Walls, new Location(48, 10), new Size(3, 21));
                } else if (Layout == 5) {
                    // [=]   [=]
                    //
                    // [=]   [=]
                    GraphicsHelper.AddRect(Walls, new Location(10, 10), new Size(7, 4));
                    GraphicsHelper.AddRect(Walls, new Location(44, 10), new Size(7, 4));
                    GraphicsHelper.AddRect(Walls, new Location(10, 27), new Size(7, 4));
                    GraphicsHelper.AddRect(Walls, new Location(44, 27), new Size(7, 4));
                } else if (Layout == 6) {
                    // ---------
                    //
                    // ---------
                    GraphicsHelper.AddRect(Walls, new Location(10, 10), new Size(41, 3));
                    GraphicsHelper.AddRect(Walls, new Location(10, 28), new Size(41, 3));
                } else if (Layout == 7) {
                    //
                    //  |||
                    //
                    GraphicsHelper.AddRect(Walls, new Location(25, 15), new Size(11, 11));
                } else if (Layout == 8) {
                    // ||     ||
                    // ||     ||
                    // ||     ||
                    GraphicsHelper.AddRect(Walls, new Location(10, 13), new Size(8, 15));
                    GraphicsHelper.AddRect(Walls, new Location(43, 13), new Size(8, 15));
                } else if (Layout == 9) {
                    //     |
                    // ----|----
                    //     |
                    GraphicsHelper.AddRect(Walls, new Location(10, 19), new Size(41, 3));
                    GraphicsHelper.AddRect(Walls, new Location(29, 10), new Size(3, 21));
                }
            } else if (Difficulty == 1) {
                if (Layout == 1) {
                    // [=]   [=]
                    //
                    // [=]   [=]
                    GraphicsHelper.AddRect(Walls, new Location(10, 10), new Size(7, 4));
                    GraphicsHelper.AddRect(Walls, new Location(44, 10), new Size(7, 4));
                    GraphicsHelper.AddRect(Walls, new Location(10, 27), new Size(7, 4));
                    GraphicsHelper.AddRect(Walls, new Location(44, 27), new Size(7, 4));
                } else if (Layout == 2) {
                    // ---------
                    //
                    // ---------
                    GraphicsHelper.AddRect(Walls, new Location(10, 10), new Size(41, 3));
                    GraphicsHelper.AddRect(Walls, new Location(10, 28), new Size(41, 3));
                } else if (Layout == 3) {
                    // |     |
                    // |=====|
                    // |     |
                    GraphicsHelper.AddRect(Walls, new Location(10, 10), new Size(3, 21));
                    GraphicsHelper.AddRect(Walls, new Location(48, 10), new Size(3, 21));
                    GraphicsHelper.AddRect(Walls, new Location(13, 19), new Size(35, 3));
                } else if (Layout == 4) {
                    // ---------
                    //  O     O
                    // ---------
                    GraphicsHelper.AddRect(Walls, new Location(10, 10), new Size(41, 3));
                    GraphicsHelper.AddRect(Walls, new Location(10, 28), new Size(41, 3));
                    GraphicsHelper.AddRect(Walls, new Location(15, 19), new Size(3, 3));
                    GraphicsHelper.AddRect(Walls, new Location(43, 19), new Size(3, 3));
                } else if (Layout == 5) {
                    // [=] | [=]
                    //     |
                    // [=] | [=]
                    GraphicsHelper.AddRect(Walls, new Location(10, 10), new Size(7, 4));
                    GraphicsHelper.AddRect(Walls, new Location(44, 10), new Size(7, 4));
                    GraphicsHelper.AddRect(Walls, new Location(10, 27), new Size(7, 4));
                    GraphicsHelper.AddRect(Walls, new Location(44, 27), new Size(7, 4));
                    GraphicsHelper.AddRect(Walls, new Location(29, 10), new Size(3, 21));
                } else if (Layout == 6) {
                    // |  |  |
                    // |  |  |
                    // |  |  |
                    GraphicsHelper.AddRect(Walls, new Location(10, 10), new Size(3, 21));
                    GraphicsHelper.AddRect(Walls, new Location(29, 10), new Size(3, 21));
                    GraphicsHelper.AddRect(Walls, new Location(48, 10), new Size(3, 21));
                } else if (Layout == 7) {
                    //
                    // || || ||
                    //
                    GraphicsHelper.AddRect(Walls, new Location(10, 15), new Size(11, 11));
                    GraphicsHelper.AddRect(Walls, new Location(25, 15), new Size(11, 11));
                    GraphicsHelper.AddRect(Walls, new Location(40, 15), new Size(11, 11));
                } else if (Layout == 8) {
                    // ||      ||
                    // ||  ||  ||
                    // ||      ||
                    GraphicsHelper.AddRect(Walls, new Location(10, 13), new Size(8, 15));
                    GraphicsHelper.AddRect(Walls, new Location(25, 15), new Size(11, 11));
                    GraphicsHelper.AddRect(Walls, new Location(43, 13), new Size(8, 15));
                } else if (Layout == 9) {
                    // O   |   O
                    // ----|----
                    // O   |   O
                    GraphicsHelper.AddRect(Walls, new Location(10, 19), new Size(41, 3));
                    GraphicsHelper.AddRect(Walls, new Location(29, 10), new Size(3, 21));
                    GraphicsHelper.AddRect(Walls, new Location(8, 8), new Size(3, 4));
                    GraphicsHelper.AddRect(Walls, new Location(50, 8), new Size(3, 4));
                    GraphicsHelper.AddRect(Walls, new Location(8, 29), new Size(3, 4));
                    GraphicsHelper.AddRect(Walls, new Location(50, 29), new Size(3, 4));
                }
            }
		}

		private void ConfigMovementLevel(int Difficulty) {
			final int Layouts = 3; //Total num of layouts
			int Layout = 1 + (int)(Math.random() * ((Layouts - 1) + 1));

			//Check for repeated wall layouts
			boolean Good = false;
			while (!Good) {
				if (Layout == LastLevelLayoutIndex) {
					Layout = 1 + (int)(Math.random() * ((Layouts - 1) + 1));
				} else {
					Good = true;
				}
			}
			LastLevelLayoutIndex = Layout;

			//Generate walls
			if (Difficulty == 0) {
				if (Layout == 1) {
					// -------
					// | X o  |
					// ---- --
					GoalPoint = new Location(20, 20);
					GraphicsHelper.AddRect(Walls, new Location(10, 10), new Size(42, 4));
					GraphicsHelper.AddRect(Walls, new Location(10, 14), new Size(4, 13));
					GraphicsHelper.AddRect(Walls, new Location(48, 14), new Size(4, 13));
					GraphicsHelper.AddRect(Walls, new Location(10, 27), new Size(29, 4));
					GraphicsHelper.AddRect(Walls, new Location(42, 27), new Size(10, 4));
					GraphicsHelper.AddRect(Walls, new Location(38, 20), new Size(5, 4));
				} else if (Layout == 2) {
					//     |  
					//  |  |  | X
					//  |     |
					GoalPoint = new Location(55, 20);
					GraphicsHelper.AddRect(Walls, new Location(15, 6), new Size(4, 35));
					GraphicsHelper.AddRect(Walls, new Location(30, 0), new Size(4, 35));
					GraphicsHelper.AddRect(Walls, new Location(45, 6), new Size(4, 35));
				} else if (Layout == 3) {
                    //    |
                    //--- |
                    //-------
                    //  X
                    GoalPoint = new Location(10, 34);
                    GraphicsHelper.AddRect(Walls, new Location(0, 11), new Size(25, 4));
                    GraphicsHelper.AddRect(Walls, new Location(30, 0), new Size(4, 15));
                    GraphicsHelper.AddRect(Walls, new Location(0, 25), new Size(45, 3));
                }
			} else if (Difficulty == 1) {
				if (Layout == 1) {
					// ---------
					// | X | = |
					// ------ --
					GoalPoint = new Location(20, 20);
					GraphicsHelper.AddRect(Walls, new Location(10, 10), new Size(42, 4));
					GraphicsHelper.AddRect(Walls, new Location(10, 14), new Size(4, 13));
					GraphicsHelper.AddRect(Walls, new Location(48, 14), new Size(4, 13));
					GraphicsHelper.AddRect(Walls, new Location(10, 27), new Size(31, 4));
					GraphicsHelper.AddRect(Walls, new Location(42, 27), new Size(10, 4));
					GraphicsHelper.AddRect(Walls, new Location(37, 21), new Size(7, 4));
					GraphicsHelper.AddRect(Walls, new Location(30, 15), new Size(4, 12));
				} else if (Layout == 2) {
					// |   |
					// | | | | X
					//   |   |
					GoalPoint = new Location(55, 20);
					GraphicsHelper.AddRect(Walls, new Location(30, 0), new Size(3, 35));
					GraphicsHelper.AddRect(Walls, new Location(35, 6), new Size(3, 35));
					GraphicsHelper.AddRect(Walls, new Location(40, 0), new Size(3, 35));
					GraphicsHelper.AddRect(Walls, new Location(45, 6), new Size(3, 35));
				} else if (Layout == 3) {
                    //    |
                    //--- |  |
                    //-------|
                    //  X  o
                    GoalPoint = new Location(10, 32);
                    GraphicsHelper.AddRect(Walls, new Location(0, 11), new Size(20, 4));
                    GraphicsHelper.AddRect(Walls, new Location(25, 0), new Size(4, 15));
                    GraphicsHelper.AddRect(Walls, new Location(0, 20), new Size(45, 4));
                    GraphicsHelper.AddRect(Walls, new Location(45, 5), new Size(4, 19));
                    GraphicsHelper.AddRect(Walls, new Location(25, 28), new Size(9, 9));
                }
			}
		}
	}
}