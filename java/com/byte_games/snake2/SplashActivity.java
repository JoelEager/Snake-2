package com.byte_games.snake2;

import com.byte_games.snake2.engine.LengthStringGen;
import com.byte_games.snake2.engine.SettingsManager;
import com.byte_games.snake2.engine.TerrainGen;
import com.byte_games.snake2.engine.GraphicsHelper.Size;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //Wait until layout is drawn
        RelativeLayout BigBox = (RelativeLayout) findViewById(R.id.SplashLayout);
        ViewTreeObserver vto = BigBox.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //Make background
                RelativeLayout BigBox = (RelativeLayout) findViewById(R.id.SplashLayout);
                Bitmap bmpBackground = Bitmap.createBitmap(BigBox.getWidth(), BigBox.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvasBackground = new Canvas(bmpBackground);

                float Unit = BigBox.getWidth() / 90;
                Size SizeOfScreen = new Size(90, (int) (BigBox.getHeight() / Unit));

                TerrainGen myTerrainGen = new TerrainGen(Unit, SizeOfScreen, new Size(15, 10), getBaseContext());
                canvasBackground = myTerrainGen.makeGameBackground(canvasBackground);

                BitmapDrawable Background = new BitmapDrawable(getResources(), bmpBackground);
                BigBox.setBackground(Background);
            }
        });
    }

    public void play(View sourceView) {
        if (sourceView.getId() == R.id.buttonPlayArcade) {
            startActivity(new Intent(this, ArcadeModeActivity.class));
        } else if (sourceView.getId() == R.id.buttonPlayClassic) {
            startActivity(new Intent(this, ClassicModeActivity.class));
        } else if (sourceView.getId() == R.id.buttonPlayChallenge) {
            challengeTutorial();
        }
    }

    private void challengeTutorial() {
        //Display tutorial for challenge mode
        final SettingsManager tutorialSettings = new SettingsManager(this, "Tutorial", "Challenge", false);

        if (!tutorialSettings.getBoolean()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Challenge mode");
            builder.setMessage("In challenge mode you get to choose a number of levels and difficulty. You then try to complete that challenge with as few deaths as possible.\n\n" +
                    "In each level completion instructions are displayed in the top right.");
            builder.setCancelable(false);
            builder.setPositiveButton("Make a challenge", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    tutorialSettings.putBoolean(true);
                    launchChallenge();
                }
            });
            builder.create();
            builder.show();
        } else {
            launchChallenge();
        }
    }

    private void launchChallenge() {
        //Make and show start challenge dialog
        final SplashActivity ActivityPointer = this;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View DialogLayout = inflater.inflate(R.layout.start_challenge_dialog, null);

        final Spinner spinnerChallengeLength = (Spinner) DialogLayout.findViewById(R.id.spinnerChallengeLength);
        final Spinner spinnerDifficulty = (Spinner) DialogLayout.findViewById(R.id.spinnerDifficulty);

        builder.setView(DialogLayout);
        builder.setTitle("Choose your challenge");
        builder.setPositiveButton("Play", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                int NumOfLevels = Integer.parseInt(spinnerChallengeLength.getSelectedItem().toString().substring(0, 1));
                int Difficulty = spinnerDifficulty.getSelectedItemPosition();

                Intent myIntent = new Intent(ActivityPointer, ChallengeModeActivity.class);
                myIntent.putExtra("com.byte_games.snake2.Challenge_NumOfLevels", NumOfLevels);
                myIntent.putExtra("com.byte_games.snake2.Challenge_Difficulty", Difficulty);
                startActivity(myIntent);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //Nothing interesting here...
            }
        });
        final AlertDialog Boxy = builder.create();
        Boxy.show();
    }

    public void viewHighscores(View sourceView) {
        final SplashActivity ActivityPointer = this;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View DialogLayout = inflater.inflate(R.layout.highscores_dialog, null);

        final LengthStringGen lengthGen = new LengthStringGen(this);

        final TextView textViewClassic = (TextView) DialogLayout.findViewById(R.id.textViewClassic);
        final SettingsManager ClassicHighscore = new SettingsManager(this, "Highscores", "Classic", 0);
        textViewClassic.setText("Classic mode: " + lengthGen.lengthToString(ClassicHighscore.getInt()));

        final TextView textViewArcade = (TextView) DialogLayout.findViewById(R.id.textViewArcade);
        final SettingsManager ArcadeHighscore = new SettingsManager(this, "Highscores", "Arcade", 0);
        textViewArcade.setText("Arcade mode: " + lengthGen.lengthToString(ArcadeHighscore.getInt()));

        builder.setView(DialogLayout);
        builder.setTitle("Your highscores");
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //Nothing interesting here...
            }
        });
        builder.setNegativeButton("Reset", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityPointer);
                builder.setMessage("Are you sure you want to reset your highscores?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ClassicHighscore.putInt(0);
                        textViewClassic.setText("Classic mode: " + lengthGen.lengthToString(ClassicHighscore.getInt()));
                        ArcadeHighscore.putInt(0);
                        textViewArcade.setText("Arcade mode: " + lengthGen.lengthToString(ArcadeHighscore.getInt()));

                        viewHighscores(null);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        viewHighscores(null);
                    }
                });
                builder.create();
                builder.show();
            }
        });
        final AlertDialog Boxy = builder.create();
        Boxy.show();
    }

    public void viewSettings(View sourceView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View DialogLayout = inflater.inflate(R.layout.settings_dialog, null);

        final Spinner spinnerUnits = (Spinner) DialogLayout.findViewById(R.id.spinnerUnits);
        final SettingsManager UnitsSetting = new SettingsManager(this, "Setings", "Units", false);
        if (UnitsSetting.getBoolean()) {
            spinnerUnits.setSelection(1);
        }

        builder.setView(DialogLayout);
        builder.setTitle("Settings");
        builder.setPositiveButton("Save and close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (spinnerUnits.getSelectedItemPosition() == 0) {
                    UnitsSetting.putBoolean(false);
                } else {
                    UnitsSetting.putBoolean(true);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //Nothing interesting here...
            }
        });
        final AlertDialog Boxy = builder.create();
        Boxy.show();
    }

    public void resetTutorial(View sourceView) {
        SettingsManager tutorialSettings = new SettingsManager(this, "Tutorial", "Main", false);
        tutorialSettings.putBoolean(false);

        tutorialSettings = new SettingsManager(this, "Tutorial", "Classic", false);
        tutorialSettings.putBoolean(false);

        tutorialSettings = new SettingsManager(this, "Tutorial", "Arcade", false);
        tutorialSettings.putBoolean(false);

        tutorialSettings = new SettingsManager(this, "Tutorial", "Challenge", false);
        tutorialSettings.putBoolean(false);

        Toast.makeText(getApplicationContext(), "Tutorial dialogs reset", Toast.LENGTH_SHORT).show();
    }
}