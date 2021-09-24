package Version1_2_0;

import NeuralNet.FourLayerNet;
import Version1_2_0.Actions.Action;
import Version1_2_0.Actions.BuyStock;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

public class Framework {
    Constants c = new Constants();
    Utility util = new Utility();
    Text t = new Text();
    Random rand = new Random();
    Scanner in = new Scanner(System.in);
    Action lastAction;

    PrintWriter log;

    int turn = 0;

    int merges = 0;

    boolean pileOut = false;

    FourLayerNet stockNet;
    FourLayerNet keepNet;
    FourLayerNet tradeNet;
    FourLayerNet sellNet;
    ArrayList<double[]> keepPositions;
    ArrayList<Integer> keepTracker;
    ArrayList<double[]> tradePositions;
    ArrayList<Integer> tradeTracker;
    ArrayList<double[]> sellPositions;
    ArrayList<Integer> sellTracker;
    ArrayList<double[]> stockPositions;
    ArrayList<Integer> stockTracker;

    Corporation Hydra = new Corporation(2, c.HYDRA, "Hydra");
    Corporation America = new Corporation(2, c.AMERICA, "America");
    Corporation Quantum = new Corporation(3, c.QUANTUM, "Quantum");
    Corporation Phoenix = new Corporation(3, c.PHOENIX, "Phoenix");
    Corporation Fusion = new Corporation(2, c.FUSION, "Fusion");
    Corporation Sackson = new Corporation(1, c.SACKSON, "Sackson");
    Corporation Zeta = new Corporation(1, c.ZETA, "Zeta");

    Corporation[] Corps = {Zeta, Sackson, America, Fusion, Hydra, Phoenix, Quantum};
    ArrayList<Integer> Pile = new ArrayList<>();
    ArrayList<Integer> CorpOverlay = new ArrayList<>();

    public Player[] players;

    public Framework(){}

    public void init_nn_stuff(FourLayerNet stockNet) {
        this.stockNet = stockNet;
    }

    public void init_nn3_stuff(FourLayerNet keepNet, FourLayerNet tradeNet, FourLayerNet sellNet) {
        this.keepNet = keepNet;
        this.tradeNet = tradeNet;
        this.sellNet = sellNet;
    }

    public double[] newGameState(Player player, int stockCorpNum) {
        ArrayList<Integer> arrayList = new ArrayList<>();

        if (stockCorpNum != -1) {
            Corporation corp = Corps[stockCorpNum];
            player.stocks[corp.num] += 1;
            corp.stocks_left--;
            corp.stocks_bought++;
            player.Money -= corp.stock_value();
        }

        for (Corporation corp : Corps) {
            for (int i : corp.corpArray()) {
                arrayList.add(i);
            }
        }

        int startI = -1;
        for (int i = 0; i < 3; i++) {
            if (players[i] == player) {
                startI = i;
            }
        }

        for (int i = startI; i < startI + 3; i++) {
            Player currPlayer =  players[i % 3];

            arrayList.add(currPlayer.Money);

            for (double stock : currPlayer.stocks) {
                arrayList.add((int) stock);
            }
        }

        double[] intArray = new double[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            intArray[i] = arrayList.get(i);
        }

        if (stockCorpNum != -1) {
            Corporation corp = Corps[stockCorpNum];
            player.stocks[corp.num] -= 1;
            corp.stocks_left++;
            corp.stocks_bought--;
            player.Money += corp.stock_value();
        }

        return intArray;
    }

    public double[] gameStateArray(Player player) {
        ArrayList<Integer> arrayList = new ArrayList<>();

        for (Corporation corp : Corps) {
            for (int tile : CorpOverlay) {
                if (tile != corp.num) arrayList.add(0);
                else arrayList.add(1);
            }
        }

        int startI = -1;
        for (int i = 0; i < 3; i++) {
            if (players[i] == player) {
                startI = i;
            }
        }

        for (int i = startI; i < startI + 3; i++) {
            Player currPlayer =  players[i % 3];

            arrayList.add(currPlayer.Money);

            for (double stock : currPlayer.stocks) {
                arrayList.add((int) stock);
            }
        }


        double[] intArray = new double[arrayList.size()];

        for (int i = 0; i < arrayList.size(); i++)
            intArray[i] = arrayList.get(i);

        return intArray;
    }

    private double[] gameStateArray(Player player, int corpStock) {
        //System.out.println();

        ArrayList<Integer> arrayList = new ArrayList<>();

        player.stocks[corpStock] += 1;
        player.Money -= Corps[corpStock].stock_value();

        for (Corporation corp : Corps) {
            for (int tile : CorpOverlay) {
                if (tile != corp.num) arrayList.add(0);
                else arrayList.add(1);
            }
        }

        int startI = -1;
        for (int i = 0; i < 3; i++) {
            if (players[i] == player) {
                startI = i;
            }
        }

        for (int i = startI; i < startI + 3; i++) {
            Player currPlayer =  players[i % 3];

            arrayList.add(currPlayer.Money);

            for (double stock : currPlayer.stocks) {
                arrayList.add((int) stock);
            }
        }


        double[] intArray = new double[arrayList.size()];

        for (int i = 0; i < arrayList.size(); i++)
            intArray[i] = arrayList.get(i);

        player.stocks[corpStock] -= 1;
        player.Money += Corps[corpStock].stock_value();

        //for (double doub : intArray) {
        //    System.out.print(((int) doub) + " ");
        //}

        return intArray;
    }

    private double[] gameStateArray(Player player, ArrayList<Integer> newOverlay) {
        ArrayList<Integer> arrayList = new ArrayList<>();

        for (Corporation corp : Corps) {
            for (int tile : newOverlay) {
                if (tile != corp.num) arrayList.add(0);
                else arrayList.add(1);
            }
        }

        int startI = -1;
        for (int i = 0; i < 3; i++) {
            if (players[i] == player) {
                startI = i;
            }
        }

        for (int i = startI; i < startI + 3; i++) {
            Player currPlayer =  players[i % 3];

            arrayList.add(currPlayer.Money);

            for (double stock : currPlayer.stocks) {
                arrayList.add((int) stock);
            }
        }


        double[] intArray = new double[arrayList.size()];

        for (int i = 0; i < arrayList.size(); i++)
            intArray[i] = arrayList.get(i);

        return intArray;
    }

    public void discard(Player player) {
        ArrayList<Integer> hand = player.Hand;
        for (int i = 0; i < 5; i++) {
            if (i < hand.size()) {
                util.MergesCorps(hand.get(i), player);

                if (util.MergesCorps(hand.get(i), player) && util.smallCorp.safe()) {
                    System.out.println("Tile " + Text.tile_name(player.Hand.get(i)) + " was discarded.");
                    hand.remove(i);
                }
            }
        }
    }

    public void draw_tile(ArrayList<Integer> Hand) {
        Hand.add(Pile.get(0));
        Pile.remove(0);
    }

    public void manual_draw_tile(ArrayList<Integer> Hand) {

        System.out.println("What tile did the cpu draw?");
        String nextTile = in.next();
        if (!nextTile.equals("ro")) {
            try {
                int tile = util.stringToTile(nextTile);
                System.out.println(Text.tile_name(tile));
                Hand.add(tile);
            } catch (NumberFormatException e) {
                System.out.println("That tile was not found. Try again.");
                nextTile = in.next();
                int tile = util.stringToTile(nextTile);
                System.out.println(Text.tile_name(tile));
                Hand.add(tile);
            }
        } else {
            pileOut = true;
        }
    }

    public void setup(Player Player1, Player Player2, Player Player3) {

        log = Main.log;

        for (int i = 1; i < 109; i++) {
            Pile.add(i);
        }
        Collections.shuffle(Pile);

        for (int i = 0; i < 109; i++) {
            CorpOverlay.add(c.UNPLAYED);
        }

        players = new Player[]{Player1, Player2, Player3};
    }

    public boolean illegal_play(int Tile) {
        boolean invalid_corp = util.createsCorp(Tile) && !corp_left();
        util.MergesCorps(Tile, players[0]);
        boolean merges_safes = util.MergesCorps(Tile, players[0]) && util.smallCorp.safe();

        return invalid_corp || merges_safes;
    }

    public ArrayList<double[]> stock_inventory() {
        ArrayList<double[]> array = new ArrayList<>();
        array.add(players[0].stocks);
        array.add(players[1].stocks);
        array.add(players[2].stocks);
        return array;
    }

    public void kill_corp(Corporation corp) {
        if (corp.exists && corp.stocks_left < 25) {
            Merge(false, corp, null, 0);
            System.out.println(" ");
        } else {}
    }

    public void Merge(boolean cont, Corporation sCorp, Corporation bCorp, int inst) {

        ArrayList<Integer> maxes = new ArrayList<>();
        ArrayList<Integer> nums = new ArrayList<>();

        for (int n = 0; n < 3; n++) {
            Player player = players[n];

            boolean done = false;
            for (int i = 0; i < maxes.size(); i++) {
                if (player.stocks[sCorp.num] > maxes.get(i)) {
                    maxes.add(i, (int) player.stocks[sCorp.num]);
                    nums.add(i, n);

                    done = true;
                    i = maxes.size();
                }
            }

            if (!done) {
                maxes.add((int) player.stocks[sCorp.num]);
                nums.add(n);
            }
        }

        log.println("sCorp size " + sCorp.size());
        if (bCorp != null) {
            log.println("bCorp size " + bCorp.size());
        }

        Text.stocks_of_corp(sCorp);
        System.out.println(sCorp.name + "'s stock value is $" + sCorp.stock_value());

        double max = maxes.get(0);
        double max2 = maxes.get(1);
        double max3 = maxes.get(2);
        int num = nums.get(0);
        int num2 = nums.get(1);
        int num3 = nums.get(2);

        if (max == max2 && max2 == max3) {
            int oneThird = (sCorp.majority_value() + sCorp.minority_value()) / 3;
            players[num].Money += oneThird;
            players[num2].Money += oneThird;
            players[num3].Money += oneThird;
            System.out.println("$" + ((sCorp.majority_value() + sCorp.minority_value()) / 3) + " was earned by "+ players[num].name + " and " + players[num2].name + " and " + players[num3].name + ".");
        } else if (max == max2) {
            int half = (sCorp.majority_value() + sCorp.minority_value()) / 2;
            players[num].Money += half;
            players[num2].Money += half;
            System.out.println("$" + ((sCorp.majority_value() + sCorp.minority_value()) / 2) + " was earned by "+ players[num].name + " and " + players[num2].name + ".");
        } else if (max2 == 0) {
            players[num].Money += (sCorp.majority_value() + sCorp.minority_value());
            System.out.println("The majority and minority bonuses of $" + (sCorp.majority_value() + sCorp.minority_value()) + " were earned by " + players[num].name);
        } else if (max2 == max3) {
            players[num].Money += sCorp.majority_value();
            players[num2].Money += sCorp.minority_value() / 2;
            players[num3].Money += sCorp.minority_value() / 2;
            System.out.println("The majority bonus of $" + sCorp.majority_value() + " was earned by " + players[num].name);
            System.out.println("The half minority bonus of $" + (sCorp.minority_value() / 2) + " was earned by " + players[num2].name);
            System.out.println("The half minority bonus of $" + (sCorp.minority_value() / 2) + " was earned by " + players[num3].name);
        } else {
            players[num].Money += sCorp.majority_value();
            players[num2].Money += sCorp.minority_value();
            System.out.println("The majority bonus of $" + sCorp.majority_value() + " was earned by " + players[num].name);
            System.out.println("The minority bonus of $" + sCorp.minority_value() + " was earned by " + players[num2].name);
        }



        for (Player player : players) {
            int moneyLeft = player.Money % 100;
            if (moneyLeft > 0) {
                player.Money += 100 - moneyLeft;
            }
        }



        if (cont) {
            resolve_merge(bCorp, sCorp, inst);
        } else {
            for (int i = 0; i < players.length; i++) {
                int playerStockNum = (int) players[i].stocks[sCorp.num];

                players[i].Money += playerStockNum * sCorp.stock_value();

                Corps[sCorp.num].stocks_left += playerStockNum;
                Corps[sCorp.num].stocks_bought -= playerStockNum;

                players[i].stocks[sCorp.num] = 0;
            }
        }

    }

    private boolean keep_stock(Player player, Corporation sCorp) {
        return util.EmptyTilesLeft() > player.c.KEEP_EMPTY_TILES_TOL && player.Money > player.c.KEEP_MONEY_TOL &&
                player.stocks[sCorp.num] * sCorp.stock_value() < 2000 + (player.stocks[sCorp.num] * 200);
    }

    public boolean trade_stock(Player player, Corporation bCorp, Corporation sCorp) {
        int newPrice;

        if (util.miniCorp == null) {
            newPrice = util.largeCorp.post_merge_value(util.smallCorp.size(), 0);
        } else {
            newPrice = util.largeCorp.post_merge_value(util.smallCorp.size(), util.miniCorp.size());
        }

        return !player.owner(bCorp.num) && newPrice > ((double) sCorp.stock_value()) * player.c.TRADE_MIN_RATIO && bCorp.stocks_left > 0
                && player.Money > player.c.TRADE_MONEY_TOL;
    }

    private int trade(Corporation bigCorp, Corporation smallCorp, Player player) {
        int tot_stock = (int) player.stocks[smallCorp.num];

        if (tot_stock % 2 == 1) {
            tot_stock -= 1;
        }

        int num_traded;

        if (tot_stock / 2 < bigCorp.stocks_left) {
            num_traded = tot_stock;
        } else {
            num_traded = (int) bigCorp.stocks_left * 2;
        }

        player.stocks[bigCorp.num] += num_traded / 2;
        player.stocks[smallCorp.num] -= num_traded;

        bigCorp.stocks_left -= num_traded / 2;
        bigCorp.stocks_bought += num_traded / 2;

        smallCorp.stocks_left += num_traded;
        smallCorp.stocks_bought -= num_traded;

        return num_traded;
    }

    private void undo_trade(Corporation bigCorp, Corporation smallCorp, Player player, int num_traded) {
        player.stocks[bigCorp.num] -= num_traded / 2;
        player.stocks[smallCorp.num] += num_traded;

        bigCorp.stocks_left += num_traded / 2;
        bigCorp.stocks_bought -= num_traded / 2;

        smallCorp.stocks_left -= num_traded;
        smallCorp.stocks_bought += num_traded;
    }

    private int sell(Corporation smallCorp, Player player) {
        int stock_sold = (int) player.stocks[smallCorp.num];

        smallCorp.stocks_left += stock_sold;
        smallCorp.stocks_bought -= stock_sold;

        player.Money += stock_sold * smallCorp.stock_value();
        player.stocks[smallCorp.num] = 0;

        return stock_sold;
    }

    private void undo_sell(Corporation smallCorp, Player player, int num_sold) {
        smallCorp.stocks_left -= num_sold;
        smallCorp.stocks_bought += num_sold;

        player.Money -= num_sold * smallCorp.stock_value();
        player.stocks[smallCorp.num] = num_sold;
    }

    private void resolve_merge_nn(Corporation bCorp, Corporation sCorp, Player currPlayer) {

        ArrayList<Integer> tempOverlay = new ArrayList<>();

        for (int i : CorpOverlay) {
            tempOverlay.add(i);
        }

        for (int i = 0; i < 109; i++) {
            if (tempOverlay.get(i) == sCorp.num) {
                tempOverlay.set(i, bCorp.num);
            }
        }

        // Baseline value for keeping stocks
        keepNet.think(new double[][] {gameStateArray(currPlayer, tempOverlay)});
        double highVal = keepNet.getOutput()[0][0];
        String highOption = "Keep";

        keepPositions.add(gameStateArray(currPlayer, tempOverlay));


        // Valuing trading in the stocks
        int num_traded = trade(bCorp, sCorp, currPlayer);

        tradePositions.add(gameStateArray(currPlayer, tempOverlay));

        tradeNet.think(new double[][] {gameStateArray(currPlayer, tempOverlay)});
        double tradeVal = tradeNet.getOutput()[0][0];
        undo_trade(bCorp, sCorp, currPlayer, num_traded);

        if (tradeVal > highVal) {
            highVal = tradeVal;
            highOption = "Trade";
        }


        // Valuing selling the stocks
        int num_sold = sell(sCorp, currPlayer);

        sellPositions.add(gameStateArray(currPlayer, tempOverlay));

        sellNet.think(new double[][] {gameStateArray(currPlayer, tempOverlay)});
        double sellVal = sellNet.getOutput()[0][0];
        undo_sell(sCorp, currPlayer, num_sold);

        if (sellVal > highVal) {
            highOption = "Sell";
        }


        switch (highOption) {
            case "Keep":
                System.out.println(currPlayer.name + " kept their stock in " + sCorp.name);
                keepTracker.add(1);tradeTracker.add(0);sellTracker.add(0);
                break;
            case "Trade":
                trade(bCorp, sCorp, currPlayer);
                System.out.println(currPlayer.name + " traded for " + (num_traded / 2) + " " + bCorp.name + " stocks");
                keepTracker.add(0);tradeTracker.add(1);sellTracker.add(0);
                break;
            case "Sell":
                sell(sCorp, currPlayer);
                System.out.println(currPlayer.name + " gained $" + (num_sold * sCorp.stock_value()) + " from selling");
                keepTracker.add(0);tradeTracker.add(0);sellTracker.add(1);
                break;
        }

        System.out.println();
    }

    private void resolve_merge(Corporation bCorp, Corporation sCorp, int inst) {

        merges++;

        System.out.println("Small corporation stocks left: " + sCorp.stocks_left);

        for (int i = inst; i < inst + players.length; i++) {
            Player currPlayer = players[i % players.length];

            //if (currPlayer == Main.HAL && currPlayer.stocks[sCorp.num] != 0) {
            //    resolve_merge_nn(bCorp, sCorp, currPlayer);
            //    continue;
            //}

            int playerSmallStocks = (int) currPlayer.stocks[sCorp.num];


            if (currPlayer.human && currPlayer.stocks[sCorp.num] != 0) {
                System.out.println("What would " + players[i % players.length].name + " like to do with their stock?");
                System.out.println("0 - Sell");
                System.out.println("1 - Keep");
                System.out.println("2 - Trade");

                int selection = in.nextInt();

                switch (selection) {
                    case 0:
                        System.out.println(players[i % players.length].name + " gained $" + players[i % players.length].stocks[sCorp.num] * sCorp.stock_value() + " from selling.");

                        currPlayer.Money += playerSmallStocks * sCorp.stock_value();

                        sCorp.stocks_left += playerSmallStocks;
                        sCorp.stocks_bought -= playerSmallStocks;

                        currPlayer.stocks[sCorp.num] = 0;
                        break;
                    case 1:
                        System.out.println(players[i % players.length].name + " kept their stock in " + sCorp.name);
                        break;
                    case 2:

                        int subSelect;

                        if (playerSmallStocks % 2 == 1) {
                            System.out.println("What would " + players[i % players.length].name + " like to do with the extra stock?");
                            System.out.println("0 - Sell");
                            System.out.println("1 - Keep");
                            subSelect = in.nextInt();

                            if (subSelect == 0) {
                                currPlayer.stocks[sCorp.num] -= 1;
                                sCorp.stocks_bought -= 1;
                                sCorp.stocks_left += 1;
                                currPlayer.Money += sCorp.stock_value();

                                System.out.println(players[i % players.length].name + " sold 1 stock.");
                            } else {
                                System.out.println(players[i % players.length].name + " kept 1 stock.");
                            }
                        } else {
                            subSelect = 0;
                        }

                        double num_traded = 0;

                        if (currPlayer.stocks[sCorp.num] / 2 < bCorp.stocks_left) {
                            if (subSelect != 0) {
                                num_traded = -1;
                            }
                            num_traded += currPlayer.stocks[sCorp.num];
                            currPlayer.stocks[bCorp.num] += num_traded / 2;
                            currPlayer.stocks[sCorp.num] -= num_traded;

                            bCorp.stocks_left -= num_traded / 2;
                            bCorp.stocks_bought += num_traded / 2;

                            sCorp.stocks_left += num_traded;
                            sCorp.stocks_bought -= num_traded;

                        } else {
                            num_traded += bCorp.stocks_left * 2;
                            currPlayer.stocks[bCorp.num] += num_traded / 2;
                            currPlayer.stocks[sCorp.num] -= num_traded;

                            bCorp.stocks_left -= num_traded / 2;
                            bCorp.stocks_bought += num_traded / 2;

                            sCorp.stocks_left += num_traded;
                            sCorp.stocks_bought -= num_traded;

                            if (subSelect == 1) {
                                System.out.println(currPlayer.name + " kept " + currPlayer.stocks[sCorp.num] + " stocks.");
                            } else {
                                currPlayer.Money += currPlayer.stocks[sCorp.num] * util.smallCorp.stock_value();
                                sCorp.stocks_bought -= currPlayer.stocks[sCorp.num];
                                sCorp.stocks_left += currPlayer.stocks[sCorp.num];
                                System.out.println(currPlayer.name + " sold " + currPlayer.stocks[sCorp.num] + " stocks.");
                                currPlayer.stocks[sCorp.num] = 0;
                            }
                        }

                        System.out.println(players[i % players.length].name + " gained " + (num_traded / 2) + " stocks from trading.");
                        break;
                }
            } else {
                if (keep_stock(currPlayer, sCorp)) {
                    System.out.println(currPlayer.name + " kept their stock in " + sCorp.name);
                } else if (trade_stock(currPlayer, bCorp, sCorp)) {

                    if (playerSmallStocks % 2 == 1) {
                        System.out.println(currPlayer.name + " sold one stock as part of trading");

                        currPlayer.stocks[sCorp.num] -= 1;
                        sCorp.stocks_bought -= 1;
                        sCorp.stocks_left += 1;
                        currPlayer.Money += sCorp.stock_value();
                    }

                    double num_traded;

                    if (currPlayer.stocks[sCorp.num] / 2 < bCorp.stocks_left) {
                        num_traded = currPlayer.stocks[sCorp.num];
                        currPlayer.stocks[bCorp.num] += num_traded / 2;
                        currPlayer.stocks[sCorp.num] = 0;

                        bCorp.stocks_left -= num_traded / 2;
                        bCorp.stocks_bought += num_traded / 2;

                        sCorp.stocks_left += num_traded;
                        sCorp.stocks_bought -= num_traded;

                    } else {
                        num_traded = bCorp.stocks_left * 2;
                        currPlayer.stocks[bCorp.num] += num_traded / 2;
                        currPlayer.stocks[sCorp.num] -= num_traded;

                        bCorp.stocks_left -= num_traded / 2;
                        bCorp.stocks_bought += num_traded / 2;

                        sCorp.stocks_left += num_traded;
                        sCorp.stocks_bought -= num_traded;

                        if (util.EmptyTilesLeft() > currPlayer.c.KEEP_EMPTY_TILES_TOL
                                && currPlayer.Money > currPlayer.c.KEEP_MONEY_TOL) {

                            System.out.println(currPlayer.name + " kept the rest of their stock");

                        } else {
                            System.out.println(currPlayer.name + " sold the rest of their stock");
                            int num_sold = (int) currPlayer.stocks[sCorp.num];

                            currPlayer.Money += num_sold * util.smallCorp.stock_value();
                            currPlayer.stocks[sCorp.num] = 0;

                            sCorp.stocks_left += num_sold;
                            sCorp.stocks_bought -= num_sold;
                        }
                    }

                    System.out.println(currPlayer.name + " gained " + (num_traded / 2) + " stocks from trading.");

                } else {
                    System.out.println(currPlayer.name + " gained $" + currPlayer.stocks[sCorp.num] * sCorp.stock_value() + " from selling.");
                    currPlayer.Money += playerSmallStocks * sCorp.stock_value();

                    sCorp.stocks_left += playerSmallStocks;
                    sCorp.stocks_bought -= playerSmallStocks;

                    currPlayer.stocks[sCorp.num] = 0;
                }
            }
        }


        for (int i = 0; i < 109; i++) {
            if (CorpOverlay.get(i) == sCorp.num) {
                CorpOverlay.set(i, bCorp.num);
                Corps[bCorp.num].Tiles.add(i);
            }
        }

        System.out.println("Small corporation stocks left: " + sCorp.stocks_left);
        Text.stocks_of_corp(sCorp);

        Corps[sCorp.num].exists = false;

        System.out.println(" ");
    }

    public boolean corp_left() {
        for (int i = 0; i < 7; i++) {
            if (!Corps[i].exists) {return true;}
        }
        return false;
    }

    public void select_nn_stock(Player player, FourLayerNet net) {
        //net.think(new double[][] {gameStateArray(player)});
        //double original = net.getOutput()[0][0];
        stockPositions.add(newGameState(player, -1));

        for (int i = 0; i < 3; i++) {
            Corporation highCorp = null;
            double highVal = 0;
            for (int j = 0; j < 7; j++) {
                Corporation corp = Corps[j];

                if (player.Money >= corp.stock_value() && corp.stocks_left > 0 && (corp.exists || turn < 4)) {
                    net.think(new double[][] {newGameState(player, j)});
                    //stockPositions.add(newGameState(player, j));

                    double val = net.getOutput()[0][0];

                    if (val > highVal) {
                        highVal = val;
                        highCorp = corp;

                    }
                }
            }

            if (highCorp != null) {
                takeAction(new BuyStock(player, highCorp));
                //if (highVal > original) {
                    //System.out.println("BETTER");
                //}
            }
        }
    }

    public void select_stock(Player player) {
        for (int i = 0; i < 3; i++) {

            int highcorp = 8;
            double highrate = 0;

            for (int n = 0; n < 7; n++) {

                Corporation corp = Corps[n];

                log.println(Corps[n].name + "'s danger rating is " + Corps[n].dangerRating());
                log.println(Corps[n].name + "'s offense rating is " + Corps[n].offenseRating());

                double totRate = (corp.dangerRating() * player.c.DANGER_INFLUENCE) + (corp.offenseRating() * player.c.OFFENSE_INFLUENCE);
                if (corp.soleOwner() != null) {
                    if (corp.soleOwner() != player) {
                        log.println("Somebody else is the sole owner of " + Corps[n].name);
                        totRate += corp.dangerRating() * 9;
                    } else {
                        log.println(player.name + " is the sole owner of " + Corps[n].name);
                    }
                }
                ArrayList<Integer> hand = player.Hand;
                for (int x = 0; x < hand.size(); x++) {
                    util.MergesCorps(hand.get(x), player);

                    if (util.MergesCorps(hand.get(x), player) && !illegal_play(hand.get(x))
                            && player.find_share(util.smallCorp) <= 0.5 && util.smallCorp.num == n) {
                        totRate *= 6;
                    }
                }

                if (player.Money <= player.c.MONEY_TO_CONSERVATIVE) {
                    totRate -= player.c.REDUCTION_FROM_CONSERVATIVE;
                }

                totRate = totRate / (Corps[n].size() * 0.5);

                if (totRate > highrate && !player.owner(n) && !player.futile(n)
                    && !player.safe(n) && corp.stocks_left > 0 && corp.stock_value() <= player.Money) {
                    highrate = totRate;
                    highcorp = n;
                }
            }

            int highsize = 0;
            if (highcorp == 8 && player.Money > player.c.MONEY_TO_BUY_HIGH_CORP) {
                for (int n = 0; n < 7; n++) {
                    Corporation corp = Corps[n];
                    if (corp.size() > highsize && !corp.owned() && corp.stocks_left > 0 && corp.size() > 6) {
                        log.println(player.name + " is buying a high corp");
                        highsize = corp.size();
                        highcorp = n;
                    }
                }
            }

            if (highcorp != 8) {
                takeAction(new BuyStock(player, Corps[highcorp]));
            } else if (player.Money > player.c.MONEY_TO_BUY_RAND_CORP && i == 2 && turn < 5) {
                takeAction(new BuyStock(player, Corps[rand.nextInt(7)]));
                log.println(player.name + " is buying a random corp");
            }
        }
    }

    public double find_benefit(int Tile, Player player) {
        //Creating Corporation
        double value = 0;
        if (util.createsCorp(Tile) && corp_left()) {
            boolean done = false;
            for (int i = 0; i < 7; i++) {
                if (!done && !Corps[i].exists) {
                    value += player.c.VALUE_OF_CREATING_CORP;
                    done = true;
                }
            }
        }

        //OLD adding to Corporation
        if (util.AddsToCorp(Tile) && !util.MergesCorps(Tile, player)) {
            util.AddsToCorp(Tile);

            log.println("tile " + Text.tile_name(Tile) + " adds to " + util.targetCorp.name);
            log.println(player.name + "'s share of " + util.targetCorp.name + " is " + player.find_share(util.targetCorp));

            value += player.c.VALUE_OF_ADDING_TO_CORP * player.find_share(util.targetCorp);
        }

        //Merging
        if (util.MergesCorps(Tile, player)) {
            if (util.majority(player, util.smallCorp.num)) {
                value += player.c.VAL_OF_MAJORITY_MERGE;
            } else if (util.minority(player, util.smallCorp.num)) {
                int MIN_MONEY = 151;

                if (player.Money > MIN_MONEY) {
                    value += player.c.VAL_OF_MINORITY_MERGE;
                } else {
                    value += 1;
                }
            } else {
                value += player.c.VAL_OF_NONE_MERGE;
            }
        }

        if (illegal_play(Tile)) {
            value = -1000;
        }

        return value;
    }

    public void play_tile(int Tile, Player player) {

        System.out.println(player.name + " played tile " + t.tile_name(Tile));
        log.println(player.name + " played tile " + t.tile_name(Tile));


        if (illegal_play(Tile)) {
            System.out.println("THIS IS ILLEGAL!!!!");
        }

        if (turn == 0) {
            CorpOverlay.set(Tile, c.UNINCORPORATED);
        } else if (util.AddsToCorp(Tile)) {
            util.targetCorp.Tiles.add(Tile);
            CorpOverlay.set(Tile, util.targetCorp.num);
            for (int i = 0; i < 109; i++) {
                if (util.find_dist(Tile, i) == 1 && CorpOverlay.get(i) == c.UNINCORPORATED) {
                    CorpOverlay.set(i, util.targetCorp.num);
                    Corps[util.targetCorp.num].Tiles.add(i);
                }
            }

            log.println("Added tile to " + util.targetCorp.name);
            log.println(util.targetCorp.name + " is now size " + util.targetCorp.size());
        } else if (util.createsCorp(Tile) && corp_left()) {
            System.out.println("What corporation would you like to create?");
            if (!player.human) {
                int createdCorp = CorpOverlay.set(Tile, player.preferred_corp());
            } else {
                for (int i = 0; i < 7; i++) {
                    if (!Corps[i].exists) {
                        System.out.println(i + " - " + Corps[i].name);
                    }
                }
                player.lastCreated = in.nextInt();
                int createdCorp = CorpOverlay.set(Tile, player.lastCreated);
            }

            System.out.println(player.name + " created " + Corps[player.lastCreated].name);

            Corps[player.lastCreated].Tiles.add(Tile);
            Corps[player.lastCreated].exists = true;
            for (int i = 0; i < 109; i++) {
                if (util.find_dist(Tile, i) == 1 && CorpOverlay.get(i) == c.UNINCORPORATED) {
                    CorpOverlay.set(i, player.lastCreated);
                    Corps[player.lastCreated].Tiles.add(i);
                }
            }
            for (int i = 0; i < 109; i++) {
                boolean add = false;
                for (int currTile : Corps[player.lastCreated].Tiles) {
                    if (util.find_dist(currTile, i) == 1 && CorpOverlay.get(i) == c.UNINCORPORATED) {
                        add = true;
                    }
                }

                if (add) {
                    CorpOverlay.set(i, player.lastCreated);
                    Corps[player.lastCreated].Tiles.add(i);
                }
            }
            player.stocks[player.lastCreated] += 1;
            Corps[player.lastCreated].stocks_left -= 1;
            Corps[player.lastCreated].stocks_bought++;
        } else if (util.MergesCorps(Tile, player)) {
            System.out.println(player.name + " merged " + util.smallCorp.name + " with " + util.largeCorp.name + ".");
            System.out.println(player.name + "'s share of " + util.smallCorp.name + ": " + player.find_share(util.smallCorp));
            Merge(true, util.smallCorp, util.largeCorp, player.ID);
            if (util.miniCorp != null) {
                Merge(true, util.miniCorp, util.largeCorp, player.ID);
            }
            util.largeCorp.Tiles.add(Tile);
            CorpOverlay.set(Tile, util.largeCorp.num);
            for (int i = 0; i < 109; i++) {
                if (util.find_dist(Tile, i) == 1 && CorpOverlay.get(i) == c.UNINCORPORATED) {
                    CorpOverlay.set(i, util.largeCorp.num);
                    Corps[util.largeCorp.num].Tiles.add(i);
                }
            }
        } else {
            CorpOverlay.set(Tile, c.UNINCORPORATED);
        }
    }

    public void takeAction(Action action) {
        action.takeAction();
        lastAction = action;
    }
}