package Version1_2_0;

import java.util.ArrayList;
import java.util.Random;

public class Player_Constants {

    Double KEEP_EMPTY_TILES_TOL  = 45.0;
    Double KEEP_MONEY_TOL = 2500.0;
    Double TRADE_MONEY_TOL = 1500.0;
    Double TRADE_MIN_RATIO = 1.5;
    Double MONEY_TO_BUY_HIGH_CORP = 2000.0;
    Double MONEY_TO_BUY_RAND_CORP = 4000.0;
    Double VALUE_OF_CREATING_CORP = 2.0;
    Double VALUE_OF_ADDING_TO_CORP = 3.0;
    Double MIN_SHARE_TO_MERGE = 0.4;
    Double VAL_OF_MAJORITY_MERGE = 20.0;
    Double VAL_OF_MINORITY_MERGE = 20.0;
    Double VAL_OF_NONE_MERGE = 20.0;
    Double DANGER_INFLUENCE = 1.0;
    Double OFFENSE_INFLUENCE = 1.0;
    Double REDUCTION_FROM_CONSERVATIVE = 0.5;
    Double MONEY_TO_CONSERVATIVE = 2000.0;

    public ArrayList<Double> playerVals = new ArrayList<>();

    Random rand = new Random();

    public Player_Constants() {
        playerVals.add(KEEP_EMPTY_TILES_TOL);
        playerVals.add(KEEP_MONEY_TOL);
        playerVals.add(TRADE_MONEY_TOL);
        playerVals.add(TRADE_MIN_RATIO);
        playerVals.add(MONEY_TO_BUY_HIGH_CORP);
        playerVals.add(MONEY_TO_BUY_RAND_CORP);
        playerVals.add(VALUE_OF_CREATING_CORP);
        playerVals.add(VALUE_OF_ADDING_TO_CORP);
        playerVals.add(MIN_SHARE_TO_MERGE);
        playerVals.add(VAL_OF_MAJORITY_MERGE);
        playerVals.add(VAL_OF_MINORITY_MERGE);
        playerVals.add(VAL_OF_NONE_MERGE);
        playerVals.add(DANGER_INFLUENCE);
        playerVals.add(OFFENSE_INFLUENCE);
        playerVals.add(REDUCTION_FROM_CONSERVATIVE);
        playerVals.add(MONEY_TO_CONSERVATIVE);
    }

    public void set(int index, Double value) {
        playerVals.set(index, value);
    }

    public void set(double[] newVals) {
        KEEP_EMPTY_TILES_TOL = newVals[0];
        KEEP_MONEY_TOL = newVals[1];
        TRADE_MONEY_TOL = newVals[2];
        TRADE_MIN_RATIO = newVals[3];
        MONEY_TO_BUY_HIGH_CORP = newVals[4];
        MONEY_TO_BUY_RAND_CORP = newVals[5];
        VALUE_OF_CREATING_CORP = newVals[6];
        VALUE_OF_ADDING_TO_CORP = newVals[7];
        MIN_SHARE_TO_MERGE = newVals[8];
        VAL_OF_MAJORITY_MERGE = newVals[9];
        VAL_OF_MINORITY_MERGE = newVals[10];
        VAL_OF_NONE_MERGE = newVals[11];
        DANGER_INFLUENCE = newVals[12];
        OFFENSE_INFLUENCE = newVals[13];
        REDUCTION_FROM_CONSERVATIVE = newVals[14];
        MONEY_TO_CONSERVATIVE = newVals[15];
    }

    public void randomize() {
        KEEP_EMPTY_TILES_TOL  = rand.nextDouble() * 100;
        KEEP_MONEY_TOL = rand.nextDouble() * 10000;
        TRADE_MONEY_TOL = rand.nextDouble() * 10000;
        TRADE_MIN_RATIO = 1 + (rand.nextDouble() * 4);
        MONEY_TO_BUY_HIGH_CORP = rand.nextDouble() * 10000;
        MONEY_TO_BUY_RAND_CORP = rand.nextDouble() * 10000;
        VALUE_OF_CREATING_CORP = rand.nextDouble() * 5;
        VALUE_OF_ADDING_TO_CORP = rand.nextDouble() * 5;
        MIN_SHARE_TO_MERGE = rand.nextDouble() * 0.6;
        VAL_OF_MAJORITY_MERGE = rand.nextDouble() * 5;
        VAL_OF_MINORITY_MERGE = (rand.nextDouble() * 10) - 5;
        VAL_OF_NONE_MERGE = -rand.nextDouble() * 5;
        DANGER_INFLUENCE = rand.nextDouble() * 5;
        OFFENSE_INFLUENCE = rand.nextDouble() * 5;
        REDUCTION_FROM_CONSERVATIVE = rand.nextDouble() * 1;
        MONEY_TO_CONSERVATIVE = rand.nextDouble() * 5000;
    }

    public void refresh() {
        playerVals.clear();
        playerVals.add(KEEP_EMPTY_TILES_TOL);
        playerVals.add(KEEP_MONEY_TOL);
        playerVals.add(TRADE_MONEY_TOL);
        playerVals.add(TRADE_MIN_RATIO);
        playerVals.add(MONEY_TO_BUY_HIGH_CORP);
        playerVals.add(MONEY_TO_BUY_RAND_CORP);
        playerVals.add(VALUE_OF_CREATING_CORP);
        playerVals.add(VALUE_OF_ADDING_TO_CORP);
        playerVals.add(MIN_SHARE_TO_MERGE);
        playerVals.add(VAL_OF_MAJORITY_MERGE);
        playerVals.add(VAL_OF_MINORITY_MERGE);
        playerVals.add(VAL_OF_NONE_MERGE);
        playerVals.add(DANGER_INFLUENCE);
        playerVals.add(OFFENSE_INFLUENCE);
        playerVals.add(REDUCTION_FROM_CONSERVATIVE);
        playerVals.add(MONEY_TO_CONSERVATIVE);
    }
}
