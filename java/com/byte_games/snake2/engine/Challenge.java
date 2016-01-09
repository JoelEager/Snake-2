package com.byte_games.snake2.engine;

import java.util.ArrayList;
import java.util.List;

import com.byte_games.snake2.engine.GraphicsHelper.Location;
import com.byte_games.snake2.engine.GraphicsHelper.Size;

//This class is used for generating and maintaining Challenge objects which are used to track the progress and data for an challenge.

public class Challenge {
	public static enum LevelType {Size, Movement};
	public List<Level> Levels = new ArrayList<Level>();
	public final int Difficulty;

	private int CurrentLevel = 0;
	private int LastLevelLayoutIndex = -1; //Used in wall generation to prevent repeats
    private int DeathCount = 0;

	//Create new challenge object
	public Challenge(int length, int Difficulty) {
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
			if (myType == LevelType.Size) {
				Goal = 5 + (Difficulty * 5);
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

	//Returns false if challenge is completed
	public boolean advanceLevel() {
		CurrentLevel++;

		if (CurrentLevel == Levels.size()) {
			CurrentLevel = Levels.size() - 1;
			return false;
		}
		return true;
	}

	//This class creates objects that represent one level of the challenge and contain the data needed to build that level.
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
			final int Layouts = 5; //Total num of layouts
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
                } else if (Layout == 4) {
                    //-----
                    //  ------
                    //----
                    //  X
                    GoalPoint = new Location(10, 37);
                    GraphicsHelper.AddRect(Walls, new Location(0, 11), new Size(50, 3));
                    GraphicsHelper.AddRect(Walls, new Location(10, 21), new Size(51, 3));
                    GraphicsHelper.AddRect(Walls, new Location(0, 31), new Size(30, 3));
                } else if (Layout == 5) {
                    //   |
                    // --|  |
                    //------|
                    //  X
                    GoalPoint = new Location(5, 34);
                    GraphicsHelper.AddRect(Walls, new Location(25, 0), new Size(3, 15));
                    GraphicsHelper.AddRect(Walls, new Location(5, 15), new Size(23, 3));
                    GraphicsHelper.AddRect(Walls, new Location(0, 25), new Size(50, 3));
                    GraphicsHelper.AddRect(Walls, new Location(50, 15), new Size(3, 13));
                }
			} else if (Difficulty == 1) {
				if (Layout == 1) {
					// ---------
					// | X |   |
					// ------ --
					GoalPoint = new Location(20, 20);
					GraphicsHelper.AddRect(Walls, new Location(10, 10), new Size(42, 4));
					GraphicsHelper.AddRect(Walls, new Location(10, 14), new Size(4, 13));
					GraphicsHelper.AddRect(Walls, new Location(48, 14), new Size(4, 13));
					GraphicsHelper.AddRect(Walls, new Location(10, 27), new Size(30, 4));
					GraphicsHelper.AddRect(Walls, new Location(42, 27), new Size(10, 4));
					GraphicsHelper.AddRect(Walls, new Location(30, 16), new Size(4, 11));
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
                    //  |    |
                    //-------|
                    //  X  o
                    GoalPoint = new Location(10, 32);
                    GraphicsHelper.AddRect(Walls, new Location(18, 3), new Size(4, 17));
                    GraphicsHelper.AddRect(Walls, new Location(32, 0), new Size(4, 17));
                    GraphicsHelper.AddRect(Walls, new Location(0, 20), new Size(46, 4));
                    GraphicsHelper.AddRect(Walls, new Location(46, 3), new Size(4, 21));
                    GraphicsHelper.AddRect(Walls, new Location(25, 26), new Size(9, 13));
                } else if (Layout == 4) {
                    //-----
                    //  ------
                    //------|
                    //  X |
                    GoalPoint = new Location(10, 33);
                    GraphicsHelper.AddRect(Walls, new Location(0, 8), new Size(54, 3));
                    GraphicsHelper.AddRect(Walls, new Location(6, 15), new Size(55, 3));
                    GraphicsHelper.AddRect(Walls, new Location(0, 22), new Size(54, 3));
                    GraphicsHelper.AddRect(Walls, new Location(51, 25), new Size(3, 10));
                    GraphicsHelper.AddRect(Walls, new Location(40, 30), new Size(3, 11));
                } else if (Layout == 5) {
                    // | X
                    // |------
                    //-| |
                    //   |
                    GoalPoint = new Location(20, 5);
                    GraphicsHelper.AddRect(Walls, new Location(7, 7), new Size(8, 30));
                    GraphicsHelper.AddRect(Walls, new Location(14, 0), new Size(1, 7));
                    GraphicsHelper.AddRect(Walls, new Location(2, 15), new Size(3, 3));
                    GraphicsHelper.AddRect(Walls, new Location(19, 20), new Size(3, 21));
                    GraphicsHelper.AddRect(Walls, new Location(15, 14), new Size(30, 3));
                }
			}
		}
	}
}