package Version1_2_0;

import java.util.ArrayList;
import java.util.Random;

public class Player {
    Player_Constants c = new Player_Constants();

    int ID;

    ArrayList<Integer> Hand = new ArrayList<>();
    Main main = new Main();
    Random rand = new Random();

    private Framework frame = Main.frame;

    public double[] stocks = {0, 0, 0, 0, 0, 0, 0};
    public String name;

    public int Money;

    int totWinnings = 0;

    boolean human;
    public int lastCreated = 8;


    public Player(boolean _human, String name, int ID) {
        this.name = name;
        human = _human;
        this.ID = ID;
    }

    public void setup() {
        for (int i = 0; i < 6; i++) {
            frame.draw_tile(Hand);
        }


        Money = 6000;
    }

    public void setupIRL() {
        Money = 6000;
    }

    public boolean owner(int corpnum) {return stocks[corpnum] > 12;}

    public boolean safe(int corpnum) {
        double highID = 42, highNum = -1, midNum = -1;
        for (int i = 0; i < 3; i++) {
            double stock = frame.players[i].stocks[corpnum];
            if (stock > highNum) {
                midNum = highNum;

                highID = frame.players[i].ID;
                highNum = stock;
            } else if (stock > midNum) {
                midNum = stock;
            }
        }

        if (highID == ID) {
            if (highNum - midNum > 3 || frame.Corps[corpnum].stocks_left < highNum - midNum) {
                return true;
            }
        }
        return false;
    }

    public boolean futile(int corpnum) {
        Corporation corp = frame.Corps[corpnum];
        double[] stockOrder = corp.orderStocks();

        if (stockOrder[1] - stocks[corpnum] > corp.stocks_left) {
            return true;
        } else {
            return false;
        }
    }

    public double find_share(Corporation corp){
        if (corp.stocks_bought == 0) {
            return 0;
        }
        return stocks[corp.num] / corp.stocks_bought;
    }

    public void setName(String newName) {
        name = newName;
    }

    public int preferred_corp() {
        double highnum = 0;
        int highcorp = 8;

        for (int i = 0; i < 7; i++) {
            if (!frame.Corps[i].exists) {
                if (find_share(frame.Corps[i]) > highnum) {
                    highnum = find_share(frame.Corps[i]);
                    highcorp = frame.Corps[i].num;
                }
            }
        }

        if (highcorp == 8) {
            int next = rand.nextInt(7);
            if (!frame.Corps[next].exists) {
                lastCreated = next;
                return next;
            } else {
                return preferred_corp();
            }
        } else {
            lastCreated = highcorp;
            return highcorp;
        }
    }

    public double portfolio_value() {
        double sum = Money;

        for (int i = 0; i < 7; i++) {
            sum += stocks[i] * frame.Corps[i].stock_value();
        }

        return sum;
    }

    public String handString() {
        String handString = "";

        handString = name + "'s hand is [";
        for (int i = 0; i < Hand.size(); i++) {
            handString = handString + Text.tile_name(Hand.get(i)) + ", ";
        }
        handString = handString + "]";

        return handString;
    }

    public boolean winning() {
        for (Player player : frame.players) {
            if (player.portfolio_value() > portfolio_value()) {
                return false;
            }
        }

        return true;
    }
}
