package com.byte_games.snake2.engine;

import android.app.Activity;

//Converts a int length to a string in the correct units

public class LengthStringGen {
    private boolean Units;

    public LengthStringGen(Activity Context) {
        //Units setting
        //(true for English, false for Metric)
        Units = (new SettingsManager(Context, "Setings", "Units", false)).getBoolean();
    }

    public String lengthToString(int Length) {
        String Text = "";

        if (Units) {
            int Inches = Length * 2; //1 length = 2 inches
            int Feet = Inches / 12;
            Inches -= Feet * 12;

            Text = Feet + " ft, " + Inches + " in";
        } else {
            int Meters = Length / 18; //18 length = 1 meter
            int CM = (int) ((Length - (Meters * 18)) * ((double) 100 / 18));

            Text = Meters + " m, " + CM + " cm";
        }
        return Text;
    }
}
