package Version1_2_0;

import java.util.ArrayList;

public class Corporation {

    private Main main = new Main();
    private Utility util = new Utility();

    public double stocks_left = 25;
    public double stocks_bought = 0;
    private int rank;
    public int num;
    boolean exists;

    public String name;

    ArrayList<Integer> Tiles = new ArrayList<>();

    public Corporation(int _rank, int _num, String name) {rank = _rank; num = _num; this.name = name;}

    public double dangerRating() {
        if (safe()) {
            return 0;
        }

        double rate = 0;
        double dangerDist = 5;

        ArrayList<Integer> corpOverlay = main.frame.CorpOverlay;

        for (int n = 0; n < 109; n++) {
            if (corpOverlay.get(n) != 8 && corpOverlay.get(n) != 7
                    && corpOverlay.get(n) != num && main.frame.Corps[corpOverlay.get(n)].size() >= size()) {

                for (int x = 0; x < 109; x++) {
                    if (util.euclid_dist(n, x) < dangerDist && corpOverlay.get(x) == num) {
                        double factor = Math.pow(util.find_dist(n, x), -1);
                        rate += factor;
                    }
                }
            }
        }

        return rate;
    }

    public ArrayList<Corporation> threats() {

        ArrayList<Integer> corpOverlay = main.frame.CorpOverlay;
        ArrayList<Corporation> temp = new ArrayList<>();

        for (int n = 0; n < 109; n++) {
            if (corpOverlay.get(n) != 8 && corpOverlay.get(n) != 7
                    && corpOverlay.get(n) != num && main.frame.Corps[corpOverlay.get(n)].size() >= size()) {

                for (int x = 0; x < 109; x++) {
                    if (util.find_dist(n, x) < 4 && corpOverlay.get(x) == num) {
                        if (!temp.contains(main.frame.Corps[corpOverlay.get(n)])) {
                            temp.add(main.frame.Corps[corpOverlay.get(n)]);
                        }
                    }
                }
            }
        }


        return temp;
    }

    public int smallestThreat() {
        int minSize = 400;

        for (Corporation corp : threats()) {
            if (corp.size() < minSize) {
                minSize = corp.size();
            }
        }

        return minSize;
    }

    public double offenseRating() {
        double rate = 0;

        for (int n = 0; n < 109; n++) {
            if (main.frame.CorpOverlay.get(n) != 8 && main.frame.CorpOverlay.get(n) != 7
                    && main.frame.CorpOverlay.get(n) != num && main.frame.Corps[main.frame.CorpOverlay.get(n)].size() <= size()) {

                for (int x = 0; x < 109; x++) {
                    if (util.find_dist(n, x) < 4 && main.frame.CorpOverlay.get(x) == num) {
                        double factor = (Math.pow(util.find_dist(n, x), -1)) / 2;
                        rate += factor;
                    }
                }
            }
        }

        if (size() > 20) {
            rate *= 2;
        }

        return rate;
    }

    public boolean owned() {
        boolean check = false;
        for (int i = 0; i < 3; i++) {
            check = main.frame.players[i].stocks[num] > 12;
        }

        return check;
    }

    public int size() {
        int size = 0;
        for (int i = 0; i < 109; i++) {
            if (main.frame.CorpOverlay.get(i) == num) {
                size++;
            }
        }
        return size;
    }

    public int stock_value() {
        int val1;
        int size = size();

        if (size > 10) {
            val1 = (size / 10) + 6;
        } else if (size <= 2) {
            val1 = 2;
        } else if (size < 6) {
            val1 = size;
        } else {
            val1 = 6;
        }

        return (val1 + rank - 1) * 100;
    }

    public int post_merge_value(int smallCorpSize, int miniCorpSize) {
        int val1;
        int newSize = size() + smallCorpSize + miniCorpSize + 1;

        if (newSize > 10) {
            val1 = (newSize / 10) + 6;
        } else if (newSize <= 2) {
            val1 = 2;
        } else if (newSize < 6) {
            val1 = newSize;
        } else {
            val1 = 6;
        }

        return (val1 + rank - 1) * 100;
    }

    public int minority_value() {return stock_value() * 5;}

    public int majority_value() {return stock_value() * 10;}

    public boolean safe() {
        return size() > 10;
    }

    public Player soleOwner() {

        int playWithStock = 0;
        Player ownPlay = null;

        for (Player player : main.frame.players) {
            if (player.stocks[num] > 0) {
                playWithStock++;
                ownPlay = player;
            }
        }

        if (playWithStock == 1) {
            return ownPlay;
        } else {
            return null;
        }
    }

    public double[] orderStocks() {
        double[] order = {0, 0, 0};

        for (int i = 0; i < 3; i++) {
            double stock = main.frame.players[i].stocks[num];
            if (stock > order[0]) {
                order[2] = order[1];
                order[1] = order[0];
                order[0] = stock;
            } else if (stock > order[1]) {
                order[2] = order[1];
                order[1] = stock;
            } else {
                order[2] = stock;
            }
        }

        return order;
    }

    public int[] corpArray() {
        return new int[] {(int) stocks_left, (int) stocks_bought, (int) dangerRating(), (int) offenseRating(), size(), stock_value(), safe() ? 1 : 0, exists ? 1 : 0};
    }
}