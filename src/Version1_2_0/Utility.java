package Version1_2_0;

import java.util.ArrayList;
import java.util.Scanner;

public class Utility {

    Main main = new Main();
    Constants c = new Constants();

    Corporation targetCorp, targetCorp2, targetCorp3;
    Corporation smallCorp;
    Corporation largeCorp;
    Corporation miniCorp;

    Scanner in = new Scanner(System.in);

    private final boolean printing = true;

    public Utility() {

    }

    public boolean board_can_create_corp(Player player) {
        if (player != Main.HAL) {
            return true;
        }

        Framework frame = main.frame;
        ArrayList<Integer> CorpOverlay = frame.CorpOverlay;

        for (int i = 0; i < 109; i++) {
            if (!AddsToCorp(i) || !PartOfCorp(i) || !MergesCorps(i, player) || !frame.illegal_play(i)) {
                int[] xDiff = {0, 1, 0, -1};
                int[] yDiff = {1, 0, -1, 0};

                for (int j = 0; j < 4; j++) {
                    int newI = i + xDiff[j] - (yDiff[j] * 12);
                    if (newI > 1 && newI < 109 && (i-1) / 12 == (newI-1) / 12) {
                        if (!AddsToCorp(newI) || !PartOfCorp(newI) || !MergesCorps(newI, player) || !frame.illegal_play(newI)) {
                            return true;
                        }
                    }
                }

            }
        }

        return false;
    }

    public boolean game_over() {
        for (int i = 0; i < 7; i++) {
            if (main.frame.Corps[i].size() > 40) {
                System.out.println(main.frame.Corps[i].name + " has reached maximum size.");
                return true;
            }
        }

        for (int i = 0; i < main.frame.players.length; i++) {
            if (main.frame.players[i].Hand.size() == 0) {
                System.out.println(main.frame.players[i].name + " has run out of tiles.");
                return true;
            }
        }

        return false;
    }

    public void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {

        }
    }

    public int find_dist(int tile1, int tile2) {
        int y, x;

        tile1 -= 1;
        tile2 -= 1;

        y = Math.abs(((tile1 - (tile1 % 12)) - (tile2 - (tile2 % 12))) / 12);
        x = Math.abs((tile1 % 12) - (tile2 % 12));

        return y + x;
    }

    public double euclid_dist(int tile1, int tile2) {
        tile1 -= 1;
        tile2 -= 1;

        double x1, y1, x2, y2;
        x1 = (tile1 % 12);
        y1 = (tile1 - (tile1 % 12));

        x2 = (tile2 % 12);
        y2 = (tile2 - (tile2 % 12));

        return Math.sqrt(Math.pow(x2-x1, 2) + Math.pow((y2-y1)/12, 2));
    }

    public double findmax(ArrayList<Double> array) {
        double max = array.get(0);

        for (int i = 0; i < array.size(); i++) {
            if (array.get(i) > max) {max = array.get(i);}
        }

        return max;
    }

    public boolean createsCorp(int Tile){
        boolean yay = false;
        for (int i = 0; i < 109; i++) {
            if ((find_dist(Tile, i) == 1) && !yay) {
                yay = main.frame.CorpOverlay.get(i) == c.UNINCORPORATED;
            }
            if (find_dist(Tile, i) == 1 && main.frame.CorpOverlay.get(i) != c.UNINCORPORATED && main.frame.CorpOverlay.get(i) != c.UNPLAYED) {
                return false;
            }
        }
        return yay;
    }

    public boolean PartOfCorp(int Tile) {
        return main.frame.CorpOverlay.get(Tile) < 7;
    }

    public boolean adj_calc(int Tile, int targ) {
        int mult = 0;

        targetCorp = null;
        targetCorp2 = null;
        targetCorp3 = null;
        boolean copy = false;
        boolean copy1 = false;
        boolean copy2 = false;

        for (int i = 0; i < 109; i++) {
            if (targetCorp != null) {
                copy1 = main.frame.CorpOverlay.get(i) == targetCorp.num;
            }
            if (targetCorp2 != null) {
                copy2 = main.frame.CorpOverlay.get(i) == targetCorp2.num;
            }

            copy = copy1 || copy2;

            if (find_dist(Tile, i) == 1) {
                if ((main.frame.CorpOverlay.get(i) != c.UNINCORPORATED) && (main.frame.CorpOverlay.get(i) != c.UNPLAYED) && !copy) {
                    mult++;
                    if (targetCorp == null || main.frame.Corps[main.frame.CorpOverlay.get(i)].num == targetCorp.num) {
                        targetCorp = main.frame.Corps[main.frame.CorpOverlay.get(i)];
                    } else if (targetCorp2 == null || main.frame.Corps[main.frame.CorpOverlay.get(i)].num == targetCorp2.num) {
                        targetCorp2 = main.frame.Corps[main.frame.CorpOverlay.get(i)];
                    } else {
                        targetCorp3 = main.frame.Corps[main.frame.CorpOverlay.get(i)];
                    }
                }
            }
        }

        return mult == targ;
    }

    public int stringToTile(String string) {
        /*int tileNum = tile % 12;
        int tileLet = (tile - ((tile - 1) % 12)) / 12;
        String letter = "null";

        switch (tileLet) {
            case 0: letter = "A";
                break;
            case 1: letter = "B";
                break;
            case 2: letter = "C";
                break;
            case 3: letter = "D";
                break;
            case 4: letter = "E";
                break;
            case 5: letter = "F";
                break;
            case 6: letter = "G";
                break;
            case 7: letter = "H";
                break;
            case 8: letter = "I";
                break;
        }

        if (tileNum == 0) {tileNum = 12;}

        return tileNum + letter;*/

        String let = string.substring(string.length()-1);
        let = let.toUpperCase();
        String num = string.substring(0, string.length()-1);
        int number = Integer.parseInt(num);
        int row = 999;

        switch (let) {
            case "A": row = 0;
                break;
            case "B": row = 1;
                break;
            case "C": row = 2;
                break;
            case "D": row = 3;
                break;
            case "E": row = 4;
                break;
            case "F": row = 5;
                break;
            case "G": row = 6;
                break;
            case "H": row = 7;
                break;
            case "I": row = 8;
                break;
        }

        return (row * 12) + number;
    }

    public boolean MergesCorps(int Tile, Player player) {
        boolean yup = false;
        largeCorp = null;
        smallCorp = null;
        miniCorp = null;

        if (adj_calc(Tile, 2)) {
            yup = true;
            if (targetCorp.size() == targetCorp2.size()) {
                if (player.human) {
                    System.out.println("Which corporation should be the large corporation?");
                    System.out.println("0 - " + targetCorp.name);
                    System.out.println("1 - " + targetCorp2.name);
                    int big = in.nextInt();
                    if (big == 0) {
                        largeCorp = targetCorp;
                        smallCorp = targetCorp2;
                    } else {
                        largeCorp = targetCorp2;
                        smallCorp = targetCorp;
                    }
                } else {
                    if (player.find_share(targetCorp) > player.find_share(targetCorp2)) {
                        largeCorp = targetCorp2;
                        smallCorp = targetCorp;
                    } else {
                        largeCorp = targetCorp;
                        smallCorp = targetCorp2;
                    }
                }
            } else if (targetCorp.size() > targetCorp2.size()) {
                smallCorp = targetCorp2;
                largeCorp = targetCorp;
            } else {
                smallCorp = targetCorp;
                largeCorp = targetCorp2;
            }
        }

        if (adj_calc(Tile, 3)) {
            yup = true;
            if (targetCorp.size() == targetCorp2.size() && targetCorp2.size() == targetCorp3.size()) {
                if (player.human) {
                    System.out.println("Which corporation should absorb the others?");
                    System.out.println("0 - " + targetCorp.name);
                    System.out.println("1 - " + targetCorp2.name);
                    System.out.println("2 - " + targetCorp3.name);
                    int big = in.nextInt();
                    if (big == 0) {
                        largeCorp = targetCorp;
                        smallCorp = targetCorp2;
                        miniCorp = targetCorp3;
                    } else if (big == 1){
                        largeCorp = targetCorp2;
                        smallCorp = targetCorp;
                        miniCorp = targetCorp3;
                    } else {
                        largeCorp = targetCorp3;
                        smallCorp = targetCorp;
                        miniCorp = targetCorp2;
                    }
                } else {
                    Corporation big = null;
                    double smolShare = 2;
                    Corporation[] corps = {targetCorp, targetCorp2, targetCorp3};
                    for (Corporation corp : corps) {
                        if (player.find_share(corp) < smolShare) {
                            smolShare = player.find_share(corp);
                            big = corp;
                        }
                    }

                    if (big == targetCorp) {
                        largeCorp = targetCorp;
                        smallCorp = targetCorp2;
                        miniCorp = targetCorp3;
                    } else if (big == targetCorp2) {
                        largeCorp = targetCorp2;
                        smallCorp = targetCorp;
                        miniCorp = targetCorp3;
                    } else {
                        largeCorp = targetCorp3;
                        smallCorp = targetCorp;
                        miniCorp = targetCorp2;
                    }
                }
            } else if (targetCorp.size() >= targetCorp2.size() && targetCorp2.size() >= targetCorp3.size()) {
                miniCorp  = targetCorp3;
                smallCorp = targetCorp2;
                largeCorp = targetCorp;
            } else if (targetCorp.size() >= targetCorp3.size() && targetCorp3.size() >= targetCorp2.size()) {
                miniCorp  = targetCorp2;
                smallCorp = targetCorp3;
                largeCorp = targetCorp;
            } else if (targetCorp2.size() >= targetCorp.size() && targetCorp.size() >= targetCorp3.size()) {
                miniCorp  = targetCorp3;
                smallCorp = targetCorp;
                largeCorp = targetCorp2;
            } else if (targetCorp2.size() >= targetCorp3.size() && targetCorp3.size() >= targetCorp.size()) {
                miniCorp  = targetCorp;
                smallCorp = targetCorp3;
                largeCorp = targetCorp2;
            } else if (targetCorp3.size() >= targetCorp.size() && targetCorp.size() >= targetCorp2.size()) {
                miniCorp  = targetCorp2;
                smallCorp = targetCorp;
                largeCorp = targetCorp3;
            } else if (targetCorp3.size() >= targetCorp2.size() && targetCorp2.size() >= targetCorp.size()) {
                miniCorp  = targetCorp;
                smallCorp = targetCorp2;
                largeCorp = targetCorp3;
            } else {
            }
        }

        return yup;
    }

    public boolean AddsToCorp(int Tile) {
        return adj_calc(Tile, 1);
    }

    public int EmptyTilesLeft() {
        int count = 0;

        for (int i = 1; i < 109; i++) {
            if (main.frame.CorpOverlay.get(i) == 7 || main.frame.CorpOverlay.get(i) == 8) {
                count++;
            }
        }

        return count;
    }

    public boolean majority(Player player, int corp) {
        Framework frame = main.frame;

        if (player.stocks[corp] == 0) {
            return false;
        }

        int p1 = 0, p2 = 0, p3 = 0;
        int tick = 0;

        for (int i = 0; i < 3; i++) {
            if (frame.players[i] == player) {
                p1 = (int) player.stocks[corp];
            } else {
                if (tick == 0) {
                    p2 = (int) frame.players[i].stocks[corp];
                    tick++;
                } else {
                    p3 = (int) frame.players[i].stocks[corp];
                }
            }
        }

        if (p1 > p2 && p1 > p3) return true;
        else if (p1 > p2 && p1 == p3) return true;
        else if (p1 == p2 && p1 > p3) return true;

        return false;
    }

    public boolean minority(Player player, int corp) {
        Framework frame = main.frame;

        if (player.stocks[corp] == 0) {
            return false;
        }

        int p1 = 0, p2 = 0, p3 = 0;
        int tick = 0;

        for (int i = 0; i < 3; i++) {
            if (frame.players[i] == player) {
                p1 = (int) player.stocks[corp];
            } else {
                if (tick == 0) {
                    p2 = (int) frame.players[i].stocks[corp];
                    tick++;
                } else {
                    p3 = (int) frame.players[i].stocks[corp];
                }
            }
        }

        if (p1 < p2 && p1 > p3) return true;
        else if (p1 > p2 && p1 < p3) return true;
        else if (p1 == p2 && p1 < p3) return true;
        else if (p1 < p2 && p1 == p3) return true;
        else if (p1 == p2 && p1 == p3) return true;

        return false;
    }

    public void print(String s) {
        if (printing) {
            System.out.println(s);
        }
    }

    public String getInput() {
        String str = in.next();

        if (str.equals("term")) {
            Main.terminal.engage();
            str = getInput();
        }

        return str;
    }
}