package Version1_2_0;

import NeuralNet.FourLayerNet;
import NeuralNet.NeuronLayer;
import NeuralNet.ThreeLayerNet;
import Version1_2_0.Actions.BuyStock;
import com.sun.org.apache.xpath.internal.axes.FilterExprWalker;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.*;

public class Main {

    static PrintWriter log = null;

    static Scanner in = new Scanner(System.in);
    static PrintWriter file;
    static Framework frame      = new Framework();
    private static Utility util = new Utility();
    private static Text t       = new Text();

    static int index = 0;

    private static FourLayerNet keepNet;
    private static FourLayerNet tradeNet;
    private static FourLayerNet sellNet;
    private static FourLayerNet stockNet;
    static double[][] inputs;
    static double[][] outputs;

    private static double base_val;

    private static Random rand = new Random();

    private static int HAL_wins = 0;
    private static int PLR2wins = 0;
    private static int PLR3wins = 0;

    static Player HAL     = new Player(false, "HAL", 0);
    static Player Player2 = new Player(false, "P2", 1);
    static Player Player3 = new Player(false, "P3", 2);

    static Terminal terminal = new Terminal(in);

    static final int INPUT_SIZE = 80;

    static double[][] testKeepIn = new double[293][INPUT_SIZE];
    static double[][] testKeepOut = new double[293][1];
    static double[][] testTradeIn = new double[293][INPUT_SIZE];
    static double[][] testTradeOut = new double[293][1];
    static double[][] testSellIn = new double[293][INPUT_SIZE];
    static double[][] testSellOut = new double[293][1];
    static double[][] testStockIn = new double[2013][INPUT_SIZE];
    static double[][] testStockOut = new double[2013][1];

    private static void loadTests3() {
        Scanner inputStream = null;
        try {
            inputStream = new Scanner(new FileReader("/Users/Core/IdeaProjects/AcquireCPU/TestArrays"));
            System.out.println("TEST FILE FOUND");
        } catch (FileNotFoundException e) {
            System.out.println("ERROR");
        }

        for (int i = 0; i < 293; i++) {
            for (int j = 0; j < INPUT_SIZE; j++) {
                testKeepIn[i][j] = Double.parseDouble(inputStream.next());
            }
            testKeepOut[i][0] = Double.parseDouble(inputStream.next());
        }

        for (int i = 0; i < 293; i++) {
            for (int j = 0; j < INPUT_SIZE; j++) {
                testTradeIn[i][j] = Double.parseDouble(inputStream.next());
            }
            testTradeOut[i][0] = Double.parseDouble(inputStream.next());
        }

        for (int i = 0; i < 293; i++) {
            for (int j = 0; j < INPUT_SIZE; j++) {
                testSellIn[i][j] = Double.parseDouble(inputStream.next());
            }
            testSellOut[i][0] = Double.parseDouble(inputStream.next());
        }

        System.out.println("FINISHED LOADING TESTS");
    }

    private static void loadTests() {
        Scanner inputStream = null;
        try {
            inputStream = new Scanner(new FileReader("/Users/Ralph/IdeaProjects/AcquireCPU/TestStocks"));
            inputStream.next();
            System.out.println("TEST FILE FOUND");
        } catch (FileNotFoundException e) {
            System.out.println("ERROR");
        }

        for (int i = 0; i < 2013; i++) {
            System.out.println(i);
            for (int j = 0; j < INPUT_SIZE; j++) {
                testStockIn[i][j] = Double.parseDouble(inputStream.next());
            }
        }

        for (int i = 0; i < 2013; i++) {
            testStockOut[i][0] = Double.parseDouble(inputStream.next());
        }

        System.out.println("FINISHED LOADING TESTS");
    }

    private static void take_cpu_turn(Player cpu_player) {
        //Play Tile
        //Text.print_hand(cpu_player);

        double max_ben = -2000;
        int tile_pos = 0;
        for (int i = 0; i < cpu_player.Hand.size(); i++) {
            double ben = frame.find_benefit(cpu_player.Hand.get(i), cpu_player);
            boolean greater = ben >= max_ben;
            max_ben = greater ? ben : max_ben;
            tile_pos = greater ? i : tile_pos;
        }

        frame.play_tile(cpu_player.Hand.get(tile_pos), cpu_player);
        cpu_player.Hand.remove(tile_pos);

        //Buy Stocks
        //if (cpu_player != HAL) {
            frame.select_stock(cpu_player);
        //} else {
        //    frame.select_nn_stock(cpu_player, stockNet);
        //}

        //Pick Tile
        frame.discard(cpu_player);
        while (cpu_player.Hand.size() < 6 && frame.Pile.size() > 0) {
            frame.draw_tile(cpu_player.Hand);
        }
    }

    private static void cpu_turn_irl_game(Player cpu_player) {
        //Play Tile
        double max_ben = -1000;
        int tile_pos = 0;
        for (int i = 0; i < cpu_player.Hand.size(); i++) {
            double ben = frame.find_benefit(cpu_player.Hand.get(i), cpu_player);
            boolean greater = ben >= max_ben;
            max_ben = greater ? ben : max_ben;
            tile_pos = greater ? i : tile_pos;
        }

        log.println(cpu_player.handString());
        if (!frame.illegal_play(cpu_player.Hand.get(tile_pos))) {
            frame.play_tile(cpu_player.Hand.get(tile_pos), cpu_player);
        }
        cpu_player.Hand.remove(tile_pos);

        //Buy Stocks
        frame.select_stock(cpu_player);

        //Pick Tile
        frame.discard(cpu_player);
        while (cpu_player.Hand.size() < 6 && !frame.pileOut) {frame.manual_draw_tile(cpu_player.Hand);}
    }

    private static void human_turn(Player player) {
        Text.print_hand(player);

        System.out.println("Which tile did the player play?");
        int tileIndex = in.nextInt();

        if (tileIndex < 6) {
            frame.play_tile(player.Hand.get(tileIndex), player);
            player.Hand.remove(tileIndex);
        }

        System.out.println("Which three stocks to buy?");
        System.out.println("0 - Zeta $" + frame.Zeta.stock_value());
        System.out.println("1 - Sackson $" + frame.Sackson.stock_value());
        System.out.println("2 - America $" + frame.America.stock_value());
        System.out.println("3 - Fusion $" + frame.Fusion.stock_value());
        System.out.println("4 - Hydra $" + frame.Hydra.stock_value());
        System.out.println("5 - Phoenix $" + frame.Phoenix.stock_value());
        System.out.println("6 - Quantum $" + frame.Quantum.stock_value());
        System.out.println("7 - Pass");
        System.out.println();
        Text.print_stocks(Player3);
        for (int i = 0; i < 3; i++) {
            if (player.Money <=100) {
                System.out.println(player.name + " has run out of money!");
                i = 4;
                continue;
            }

            int selectedStock = in.nextInt();
            if (selectedStock != 7) {
                if (frame.Corps[selectedStock].stocks_left == 0) {
                    System.out.println("Fool! There are none of those left!");
                } else if (player.Money - frame.Corps[selectedStock].stock_value() < 0) {
                    System.out.println("Fool! You lack cash!");
                } else {
                    frame.takeAction(new BuyStock(player, frame.Corps[selectedStock]));
                }
            } else {
                i = 4;
            }
        }

        while (player.Hand.size() < 6 && frame.Pile.size() > 0) {
            frame.draw_tile(player.Hand);
            System.out.println("You picked up " + Text.tile_name(player.Hand.get(player.Hand.size()-1)));
        }
        frame.discard(player);
    }

    private static void human_IRL_turn(Player player) {
        System.out.println("It's " + player.name + "'s turn!");

        System.out.println("Which tile did " + player.name + " play?");

        String nextTile = util.getInput();

        if (!nextTile.equals("ro")) {
            try {
                int tile = util.stringToTile(nextTile);
                frame.play_tile(tile, player);
            } catch (NumberFormatException e) {
                System.out.println("That tile was not found. Try again.");
                nextTile = util.getInput();
                int tile = util.stringToTile(nextTile);
                frame.play_tile(tile, player);
            }

        } else {
            player.Hand.clear();
        }



        System.out.println("Which three stocks to buy?");
        for (int i = 0; i < 7; i++) {
            if (frame.Corps[i].stocks_left > 0) {
                System.out.println(i + " - " + frame.Corps[i].name + " $" + frame.Corps[i].stock_value());
            }
        }
        System.out.println("7 - Pass");
        System.out.println();
        //Text.print_stocks(player);
        for (int i = 0; i < 3; i++) {
            if (player.Money <=100) {
                System.out.println(player.name + " has run out of money!");
                i = 4;
                continue;
            }

            int selectedStock;
            try {
                selectedStock = in.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("My friend, that is simply not an integer!");
                in.next();
                i--;
                continue;
            }

            if (selectedStock != 7 && selectedStock > -1 && selectedStock < 8) {
                if (frame.Corps[selectedStock].stocks_left == 0) {
                    System.out.println("Fool! There are none of those left!");
                    i--;
                } else if (player.Money - frame.Corps[selectedStock].stock_value() < 0) {
                    System.out.println("Fool! You lack cash!");
                    i--;
                } else {
                    frame.takeAction(new BuyStock(player, frame.Corps[selectedStock]));
                }
            } else if (selectedStock == 7){
                i = 4;
            } else {
                System.out.println("Stock " + selectedStock + " not recognized. Try again!");
                i--;
            }
        }
    }

    private static void play_virtual_game() {
        frame.setup(HAL, Player2, Player3);
        HAL.setup();
        Player2.setup();
        Player3.setup();

        frame.turn = 0;

        double minDist = 1000;
        int minPl = -1;
        for (int i = 0; i < 3; i++) {
            frame.play_tile(frame.Pile.get(0), frame.players[i]);

            double dist = Math.sqrt(Math.pow((frame.Pile.get(0) % 12) - 1, 2) + Math.pow((frame.Pile.get(0) - ((frame.Pile.get(0) - 1) % 12)) / 12, 2));
            frame.Pile.remove(0);

            if (dist < minDist) {
                minDist = dist;
                minPl = i;
            }
        }

        while (!util.game_over()) {
            /*frame.turn++;
            System.out.println();
            System.out.println();
            System.out.println("//////Turn " + frame.turn + "//////");
            take_cpu_turn(HAL);

            t.printBoard();
            System.out.println();
            System.out.println(" ");
            take_cpu_turn(Player2);

            t.printBoard();
            System.out.println(" ");
            System.out.println(" ");
            human_turn(Player3);

            t.printBoard();
            System.out.println(" ");
            System.out.println(" ");
            util.sleep(1000);*/




            frame.turn++;
            System.out.println();
            System.out.println();
            System.out.println("//////Turn " + frame.turn + "//////");

            for (int i = minPl; i < minPl + 3; i++) {
                t.printBoard();
                System.out.println();

                Player player = frame.players[i % 3];

                if (player.human) {
                    human_turn(player);
                } else {
                    take_cpu_turn(player);
                }

                System.out.println();

                util.sleep(3000);

                if (util.game_over()) break;
            }
        }

        System.out.println("I GOT TWO WORDS FOR YA: GAME. OVER.");
        System.out.println("//////////AT END OF REGULATION//////////");

        Text.print_stocks(HAL);
        Text.print_stocks(Player2);
        Text.print_stocks(Player3);

        System.out.println("HAL Money: " + HAL.Money);
        System.out.println("Version1_2_0.Player 2 Money: " + Player2.Money);
        System.out.println("Version1_2_0.Player 3 Money: " + Player3.Money);

        System.out.println(" ");
        System.out.println(" ");
        System.out.println(" ");

        resolve_game();

        System.out.println("//////////FINAL RESULTS//////////");

        System.out.println("HAL Money: " + HAL.Money);
        System.out.println("Version1_2_0.Player 2 Money: " + Player2.Money);
        System.out.println("Version1_2_0.Player 3 Money: " + Player3.Money);
    }

    private static void play_irl_game() {
        frame.setup(HAL, Player2, Player3);
        if (HAL.human) HAL.setup(); else HAL.setupIRL();
        if (Player2.human) Player2.setup(); else Player2.setupIRL();
        if (Player3.human) Player3.setup(); else Player3.setupIRL();

        frame.turn = 0;

        double minDist = 1000;
        int minPl = -1;
        for (int i = 0; i < 3; i++) {
            System.out.println("What tile did " + frame.players[i].name + " draw for the start of the game?");
            int tile = util.stringToTile(in.next());
            frame.play_tile(tile, frame.players[i]);

            double dist = Math.sqrt(Math.pow(((tile-1) % 12), 2) + Math.pow((tile - ((tile - 1) % 12)) / 12, 2));

            if (dist < minDist) {
                minDist = dist;
                minPl = i;
            }
        }

        while (HAL.Hand.size() < 6 && frame.Pile.size() > 0) {frame.manual_draw_tile(HAL.Hand);}
        while (Player2.Hand.size() < 6 && frame.Pile.size() > 0) {frame.manual_draw_tile(Player2.Hand);}

        while (!util.game_over()) {

            frame.turn++;
            System.out.println();
            System.out.println("//////Turn " + frame.turn + "//////");
            for (Player player : frame.players) {
                System.out.println(player.name + " portfolio value: $" + player.portfolio_value());
            }

            for (int i = minPl; i < minPl + 3; i++) {
                t.printBoard();
                System.out.println();

                Player player = frame.players[i % 3];

                if (player.human) {
                    human_IRL_turn(player);
                } else {
                    cpu_turn_irl_game(player);
                }

                System.out.println();

                util.sleep(3000);

                if (util.game_over()) break;
            }
        }

        System.out.println("I GOT TWO WORDS FOR YA: GAME. OVER.");
        System.out.println("//////////AT END OF REGULATION//////////");

        Text.print_stocks(HAL);
        Text.print_stocks(Player2);
        Text.print_stocks(Player3);

        System.out.println("HAL Money: " + HAL.Money);
        System.out.println("Version1_2_0.Player 2 Money: " + Player2.Money);
        System.out.println("Version1_2_0.Player 3 Money: " + Player3.Money);

        System.out.println(" ");
        System.out.println(" ");
        System.out.println(" ");

        resolve_game();

        System.out.println("//////////FINAL RESULTS//////////");

        System.out.println(HAL.name + " Money: " + HAL.Money);
        System.out.println(Player2.name + " Money: " + Player2.Money);
        System.out.println(Player3 + " Money: " + Player3.Money);
    }

    private static void resolve_game() {
        for (int i = 0; i < 7; i++) {
            frame.kill_corp(frame.Corps[i]);
        }
    }

    private static void play_many_games(int num_of_games) {
        HAL_wins = 0;
        PLR3wins = 0;
        PLR2wins = 0;
        int game_num = 0;
        for (int i = 0; i < num_of_games; i++) {
            game_num++;
            System.out.println("Game Number: " + game_num);
            int ord = rand.nextInt(6);

            if (ord == 0) {
                frame.setup(HAL, Player2, Player3);
            } else if (ord == 1) {
                frame.setup(HAL, Player3, Player2);
            } else if (ord == 2) {
                frame.setup(Player2, HAL, Player3);
            } else if (ord == 3) {
                frame.setup(Player2, Player3, HAL);
            } else if (ord == 4) {
                frame.setup(Player3, HAL, Player2);
            } else {
                frame.setup(Player3, Player2, HAL);
            }

            HAL.setup();
            Player2.setup();
            Player3.setup();

            frame.turn = 1;


            while (!util.game_over()) {
                frame.turn++;
                for (int n = 0; n < 3; n++) {
                    take_cpu_turn(frame.players[n]);
                }
            }

            resolve_game();

            if (Player2.Money > Player3.Money && Player2.Money > HAL.Money) {
                PLR2wins++;
            } else if (Player3.Money > Player2.Money && Player3.Money > HAL.Money) {
                PLR3wins++;
            } else {
                HAL_wins++;
            }

            HAL.totWinnings += HAL.Money;
            Player2.totWinnings += Player2.Money;
            Player3.totWinnings += Player3.Money;

            for (int y = 0; y < 3; y++) {
                frame.players[y].Hand.clear();
                for (int z = 0; z < 7; z++) {
                    frame.players[y].stocks[z] = 0;
                }
            }

            for (int y = 0; y < 7; y++) {
                frame.Corps[y].exists = false;
                frame.Corps[y].stocks_left = 25;
                frame.Corps[y].stocks_bought = 0;
            }

            frame.CorpOverlay.clear();
            frame.Pile.clear();

            System.out.println(HAL.name + " wins: " + HAL_wins);
            System.out.println(Player2.name + " wins: " + PLR2wins);
            System.out.println(Player3.name + " wins: " + PLR3wins);
        }
    }

    private static double[][] copy_array_list(ArrayList<double[]> arrayList) {
        double[][] temp;
        if (arrayList.size() > 0) {
            temp = new double[arrayList.size()][arrayList.get(0).length];
        } else {
            temp = new double[0][0];
        }

        for (int x = 0; x < arrayList.size(); x++) {
            for (int y = 0; y < arrayList.get(0).length; y++) {
                temp[x][y] = arrayList.get(x)[y];
            }
        }

        return temp;
    }

    private static void apply_results(int num, boolean win, double[][] keepOut, double[][] tradeOut, double[][] sellOut) {
        for (int i = 0; i < num; i++) {
            if (frame.keepTracker.get(i) == 1) {
                keepOut[i][0] = win ? 1 : 0;
                tradeOut[i][0] = win ? 0 : 0.5;
                sellOut[i][0] = win ? 0 : 0.5;
            } else if (frame.tradeTracker.get(i) == 1) {
                keepOut[i][0] = win ? 0 : 0.5;
                tradeOut[i][0] = win ? 1 : 0;
                sellOut[i][0] = win ? 0 : 0.5;
            } else if (frame.sellTracker.get(i) == 1) {
                keepOut[i][0] = win ? 0 : 0.5;
                tradeOut[i][0] = win ? 0 : 0.5;
                sellOut[i][0] = win ? 1 : 0;
            }
        }
    }

    private static void play_many_nn3_games(int num_of_games) {
        HAL_wins = 0;
        PLR3wins = 0;
        PLR2wins = 0;
        int game_num = 0;

        ArrayList<double[]> keepPositions = new ArrayList<>();
        ArrayList<double[]> keepResults = new ArrayList<>();

        ArrayList<double[]> tradePositions = new ArrayList<>();
        ArrayList<double[]> tradeResults = new ArrayList<>();

        ArrayList<double[]> sellPositions = new ArrayList<>();
        ArrayList<double[]> sellResults = new ArrayList<>();

        frame.init_nn3_stuff(keepNet, tradeNet, sellNet);

        for (int i = 0; i < num_of_games; i++) {
            frame.keepPositions = new ArrayList<>();
            frame.keepTracker = new ArrayList<>();
            frame.tradePositions = new ArrayList<>();
            frame.tradeTracker = new ArrayList<>();
            frame.sellPositions = new ArrayList<>();
            frame.sellTracker = new ArrayList<>();


            game_num++;
            System.out.println("Game Number: " + game_num);
            int ord = rand.nextInt(6);

            if (ord == 0) {
                frame.setup(HAL, Player2, Player3);
            } else if (ord == 1) {
                frame.setup(HAL, Player3, Player2);
            } else if (ord == 2) {
                frame.setup(Player2, HAL, Player3);
            } else if (ord == 3) {
                frame.setup(Player2, Player3, HAL);
            } else if (ord == 4) {
                frame.setup(Player3, HAL, Player2);
            } else {
                frame.setup(Player3, Player2, HAL);
            }

            HAL.setup();
            Player2.setup();
            Player3.setup();

            frame.turn = 1;


            while (!util.game_over()) {
                for (int n = 0; n < 3; n++) {
                    Player player = frame.players[n];
                    take_cpu_turn(player);
                }
                frame.turn++;
            }

            resolve_game();


            double[][] keepArray = copy_array_list(frame.keepPositions);
            double[][] tradeArray = copy_array_list(frame.tradePositions);
            double[][] sellArray = copy_array_list(frame.sellPositions);
            double[][] keepOut = new double[frame.keepPositions.size()][1];
            double[][] tradeOut = new double[frame.tradePositions.size()][1];
            double[][] sellOut = new double[frame.sellPositions.size()][1];

            if (Player2.Money > Player3.Money && Player2.Money > HAL.Money) {
                PLR2wins++;

                System.out.println("P2 WINS");
                apply_results(frame.keepTracker.size(), false, keepOut, tradeOut, sellOut);
            } else if (Player3.Money > Player2.Money && Player3.Money > HAL.Money) {
                PLR3wins++;

                System.out.println("P3 WINS");
                apply_results(frame.keepTracker.size(), false, keepOut, tradeOut, sellOut);
            } else {
                HAL_wins++;

                System.out.println("HAL WINS");
                apply_results(frame.keepTracker.size(), true, keepOut, tradeOut, sellOut);
            }


            /*if (keepArray.length > 0) {
                keepNet.applyTests(keepArray, keepOut);
                keepNet.train(keepArray, keepOut, 10);

                tradeNet.applyTests(tradeArray, tradeOut);
                tradeNet.train(tradeArray, tradeOut, 10);

                sellNet.applyTests(sellArray, sellOut);
                sellNet.train(sellArray, sellOut, 10);
            }*/


            keepPositions.addAll(Arrays.asList(keepArray));
            keepResults.addAll(Arrays.asList(keepOut));
            tradePositions.addAll(Arrays.asList(tradeArray));
            tradeResults.addAll(Arrays.asList(tradeOut));
            sellPositions.addAll(Arrays.asList(sellArray));
            sellResults.addAll(Arrays.asList(sellOut));

            HAL.totWinnings += HAL.Money;
            Player2.totWinnings += Player2.Money;
            Player3.totWinnings += Player3.Money;

            for (int y = 0; y < 3; y++) {
                frame.players[y].Hand.clear();
                for (int z = 0; z < 7; z++) {
                    frame.players[y].stocks[z] = 0;
                }
            }

            for (int y = 0; y < 7; y++) {
                frame.Corps[y].exists = false;
                frame.Corps[y].stocks_left = 25;
                frame.Corps[y].stocks_bought = 0;
            }



            frame.CorpOverlay.clear();
            frame.Pile.clear();

            System.out.println(HAL.name + " wins: " + HAL_wins);
            System.out.println(Player2.name + " wins: " + PLR2wins);
            System.out.println(Player3.name + " wins: " + PLR3wins);
        }


        double[][] keepArray = copy_array_list(keepPositions);
        double[][] tradeArray = copy_array_list(tradePositions);
        double[][] sellArray = copy_array_list(sellPositions);
        double[][] keepOut = copy_array_list(keepResults);
        double[][] tradeOut = copy_array_list(tradeResults);
        double[][] sellOut = copy_array_list(sellResults);

        keepNet.applyTests(testKeepIn, testKeepOut);
        keepNet.train(keepArray, keepOut, 10);
        tradeNet.applyTests(testTradeIn, testTradeOut);
        tradeNet.train(tradeArray, tradeOut, 10);
        sellNet.applyTests(testSellIn, testSellOut);
        sellNet.train(sellArray, sellOut, 10);


        keepNet.exportNet("KeepNet");
        tradeNet.exportNet("TradeNet");
        sellNet.exportNet("SellNet");
    }

    private static void play_many_nn_games(int num_of_games) {
        HAL_wins = 0;
        PLR3wins = 0;
        PLR2wins = 0;
        int game_num = 0;

        ArrayList<double[]> stockPositions = new ArrayList<>();
        ArrayList<double[]> stockResults = new ArrayList<>();

        frame.init_nn_stuff(stockNet);

        for (int i = 0; i < num_of_games; i++) {
            frame.stockPositions = new ArrayList<>();
            frame.stockTracker = new ArrayList<>();


            game_num++;
            System.out.println("Game Number: " + game_num);
            int ord = rand.nextInt(6);

            if (ord == 0) {
                frame.setup(HAL, Player2, Player3);
            } else if (ord == 1) {
                frame.setup(HAL, Player3, Player2);
            } else if (ord == 2) {
                frame.setup(Player2, HAL, Player3);
            } else if (ord == 3) {
                frame.setup(Player2, Player3, HAL);
            } else if (ord == 4) {
                frame.setup(Player3, HAL, Player2);
            } else {
                frame.setup(Player3, Player2, HAL);
            }

            HAL.setup();
            Player2.setup();
            Player3.setup();

            frame.turn = 1;


            while (!util.game_over()) {
                for (int n = 0; n < 3; n++) {
                    Player player = frame.players[n];
                    take_cpu_turn(player);
                }
                frame.turn++;
            }

            resolve_game();

            double[][] stockArray = copy_array_list(frame.stockPositions);
            double[][] stockOut = new double[frame.stockPositions.size()][1];

            if (Player2.Money > Player3.Money && Player2.Money > HAL.Money) {
                PLR2wins++;

                System.out.println("P2 WINS");
                for (int j = 0; j < stockOut.length; j++) {
                    stockOut[j][0] = 0;
                }
            } else if (Player3.Money > Player2.Money && Player3.Money > HAL.Money) {
                PLR3wins++;

                System.out.println("P3 WINS");
                for (int j = 0; j < stockOut.length; j++) {
                    stockOut[j][0] = 0;
                }
            } else {
                HAL_wins++;

                System.out.println("HAL WINS");
                for (int j = 0; j < stockOut.length; j++) {
                    stockOut[j][0] = 1;
                }
            }

            stockPositions.addAll(Arrays.asList(stockArray));
            stockResults.addAll(Arrays.asList(stockOut));

            HAL.totWinnings += HAL.Money;
            Player2.totWinnings += Player2.Money;
            Player3.totWinnings += Player3.Money;

            for (int y = 0; y < 3; y++) {
                frame.players[y].Hand.clear();
                for (int z = 0; z < 7; z++) {
                    frame.players[y].stocks[z] = 0;
                }
            }

            for (int y = 0; y < 7; y++) {
                frame.Corps[y].exists = false;
                frame.Corps[y].stocks_left = 25;
                frame.Corps[y].stocks_bought = 0;
            }



            frame.CorpOverlay.clear();
            frame.Pile.clear();

            System.out.println(HAL.name + " wins: " + HAL_wins);
            System.out.println(Player2.name + " wins: " + PLR2wins);
            System.out.println(Player3.name + " wins: " + PLR3wins);
        }


        double[][] stockArray = copy_array_list(stockPositions);
        double[][] stockOut = copy_array_list(stockResults);

        stockNet.applyTests(testStockIn, testStockOut);
        stockNet.train(stockArray, stockOut, 200);


        stockNet.exportNet("StockNet");
    }

    private static void generate_nn3_tests() {
        HAL_wins = 0;
        PLR3wins = 0;
        PLR2wins = 0;
        int game_num = 0;

        ArrayList<double[]> keepPositions = new ArrayList<>();
        ArrayList<double[]> keepResults = new ArrayList<>();

        ArrayList<double[]> tradePositions = new ArrayList<>();
        ArrayList<double[]> tradeResults = new ArrayList<>();

        ArrayList<double[]> sellPositions = new ArrayList<>();
        ArrayList<double[]> sellResults = new ArrayList<>();

        frame.init_nn3_stuff(keepNet, tradeNet, sellNet);

        for (int i = 0; i < 100; i++) {
            frame.keepPositions = new ArrayList<>();
            frame.keepTracker = new ArrayList<>();
            frame.tradePositions = new ArrayList<>();
            frame.tradeTracker = new ArrayList<>();
            frame.sellPositions = new ArrayList<>();
            frame.sellTracker = new ArrayList<>();


            game_num++;
            System.out.println("Game Number: " + game_num);
            int ord = rand.nextInt(6);

            if (ord == 0) {
                frame.setup(HAL, Player2, Player3);
            } else if (ord == 1) {
                frame.setup(HAL, Player3, Player2);
            } else if (ord == 2) {
                frame.setup(Player2, HAL, Player3);
            } else if (ord == 3) {
                frame.setup(Player2, Player3, HAL);
            } else if (ord == 4) {
                frame.setup(Player3, HAL, Player2);
            } else {
                frame.setup(Player3, Player2, HAL);
            }

            HAL.setup();
            Player2.setup();
            Player3.setup();

            frame.turn = 1;


            while (!util.game_over()) {
                for (int n = 0; n < 3; n++) {
                    Player player = frame.players[n];
                    take_cpu_turn(player);
                }
                frame.turn++;
            }

            resolve_game();


            double[][] keepArray = copy_array_list(frame.keepPositions);
            double[][] tradeArray = copy_array_list(frame.tradePositions);
            double[][] sellArray = copy_array_list(frame.sellPositions);
            double[][] keepOut = new double[frame.keepPositions.size()][1];
            double[][] tradeOut = new double[frame.tradePositions.size()][1];
            double[][] sellOut = new double[frame.sellPositions.size()][1];

            if (Player2.Money > Player3.Money && Player2.Money > HAL.Money) {
                PLR2wins++;

                System.out.println("P2 WINS");
                i--;
            } else if (Player3.Money > Player2.Money && Player3.Money > HAL.Money) {
                PLR3wins++;

                System.out.println("P3 WINS");
                i--;
            } else {
                HAL_wins++;
                System.out.println("HAL WINS");

                apply_results(frame.keepTracker.size(), true, keepOut, tradeOut, sellOut);

                keepPositions.addAll(Arrays.asList(keepArray));
                keepResults.addAll(Arrays.asList(keepOut));
                tradePositions.addAll(Arrays.asList(tradeArray));
                tradeResults.addAll(Arrays.asList(tradeOut));
                sellPositions.addAll(Arrays.asList(sellArray));
                sellResults.addAll(Arrays.asList(sellOut));
            }

            HAL.totWinnings += HAL.Money;
            Player2.totWinnings += Player2.Money;
            Player3.totWinnings += Player3.Money;

            for (int y = 0; y < 3; y++) {
                frame.players[y].Hand.clear();
                for (int z = 0; z < 7; z++) {
                    frame.players[y].stocks[z] = 0;
                }
            }

            for (int y = 0; y < 7; y++) {
                frame.Corps[y].exists = false;
                frame.Corps[y].stocks_left = 25;
                frame.Corps[y].stocks_bought = 0;
            }



            frame.CorpOverlay.clear();
            frame.Pile.clear();

            System.out.println(HAL.name + " wins: " + HAL_wins);
            System.out.println(Player2.name + " wins: " + PLR2wins);
            System.out.println(Player3.name + " wins: " + PLR3wins);
        }

        PrintWriter out = null;

        try {
            out = new PrintWriter("TestArrays");

            out.println(keepPositions.size());

            String string = "";
            for (double[] keepPosition : keepPositions) {
                for (double doub : keepPosition) {
                    string = string + doub + " ";
                }
            }
            out.println(string);

            System.out.println("Half Keeps");

            string = "";
            for (double[] keepResult : keepResults) {
                for (double doub : keepResult) {
                    string = string + doub + " ";
                }
            }
            out.println(string);

            System.out.println("Finished Keeps");

            string = "";
            for (double[] tradePosition : tradePositions) {
                for (double doub : tradePosition) {
                    string = string + doub + " ";
                }
            }
            out.println(string);

            System.out.println("Half Trades");

            string = "";
            for (double[] tradeResult : tradeResults) {
                for (double doub : tradeResult) {
                    string = string + doub + " ";
                }
            }
            out.println(string);

            System.out.println("Finished Trades");

            string = "";
            for (double[] sellPosition : sellPositions) {
                for (double doub : sellPosition) {
                    string = string + doub + " ";
                }
            }
            out.println(string);

            System.out.println("Half Sells");

            string = "";
            for (double[] sellResult : sellResults) {
                for (double doub : sellResult) {
                    string = string + doub + " ";
                }
            }
            out.println(string);

            System.out.println("Finished Sells");

            out.close();
        } catch (FileNotFoundException e) {
            System.out.println("ERROR");
        }

    }

    private static void generate_nn_tests() {
        HAL_wins = 0;
        PLR3wins = 0;
        PLR2wins = 0;
        int game_num = 0;

        ArrayList<double[]> stockPositions = new ArrayList<>();
        ArrayList<double[]> stockResults = new ArrayList<>();

        frame.init_nn_stuff(stockNet);

        for (int i = 0; i < 100; i++) {
            frame.stockPositions = new ArrayList<>();
            frame.stockTracker = new ArrayList<>();


            game_num++;
            System.out.println("Game Number: " + game_num);
            int ord = rand.nextInt(6);

            if (ord == 0) {
                frame.setup(HAL, Player2, Player3);
            } else if (ord == 1) {
                frame.setup(HAL, Player3, Player2);
            } else if (ord == 2) {
                frame.setup(Player2, HAL, Player3);
            } else if (ord == 3) {
                frame.setup(Player2, Player3, HAL);
            } else if (ord == 4) {
                frame.setup(Player3, HAL, Player2);
            } else {
                frame.setup(Player3, Player2, HAL);
            }

            HAL.setup();
            Player2.setup();
            Player3.setup();

            frame.turn = 1;


            while (!util.game_over()) {
                for (int n = 0; n < 3; n++) {
                    Player player = frame.players[n];
                    take_cpu_turn(player);
                }
                frame.turn++;
            }

            resolve_game();

            double[][] stockArray = copy_array_list(frame.stockPositions);
            double[][] stockOut = new double[frame.stockPositions.size()][1];

            if (Player2.Money > Player3.Money && Player2.Money > HAL.Money) {
                PLR2wins++;

                System.out.println("P2 WINS");
                for (int j = 0; j < stockOut.length; j++) {
                    stockOut[j][0] = 0;
                }
            } else if (Player3.Money > Player2.Money && Player3.Money > HAL.Money) {
                PLR3wins++;

                System.out.println("P3 WINS");
                for (int j = 0; j < stockOut.length; j++) {
                    stockOut[j][0] = 0;
                }
            } else {
                HAL_wins++;

                System.out.println("HAL WINS");
                for (int j = 0; j < stockOut.length; j++) {
                    stockOut[j][0] = 1;
                }
            }

            stockPositions.addAll(Arrays.asList(stockArray));
            stockResults.addAll(Arrays.asList(stockOut));

            HAL.totWinnings += HAL.Money;
            Player2.totWinnings += Player2.Money;
            Player3.totWinnings += Player3.Money;

            for (int y = 0; y < 3; y++) {
                frame.players[y].Hand.clear();
                for (int z = 0; z < 7; z++) {
                    frame.players[y].stocks[z] = 0;
                }
            }

            for (int y = 0; y < 7; y++) {
                frame.Corps[y].exists = false;
                frame.Corps[y].stocks_left = 25;
                frame.Corps[y].stocks_bought = 0;
            }



            frame.CorpOverlay.clear();
            frame.Pile.clear();

            System.out.println(HAL.name + " wins: " + HAL_wins);
            System.out.println(Player2.name + " wins: " + PLR2wins);
            System.out.println(Player3.name + " wins: " + PLR3wins);
        }


        PrintWriter out = null;

        try {
            out = new PrintWriter("TestStocks");

            out.println(stockPositions.size());

            String string = "";
            for (double[] keepPosition : stockPositions) {
                for (double doub : keepPosition) {
                    string = string + doub + " ";
                }
                out.println(string);
                string = "";
            }

            System.out.println("Half Uploaded");

            string = "";
            for (double[] keepResult : stockResults) {
                for (double doub : keepResult) {
                    string = string + doub + " ";
                }
            }
            out.println(string);

            System.out.println("Finished Uploading");
        } catch (FileNotFoundException e) {

        }

        out.close();
    }

    public static void play_one_game() {
        frame.setup(HAL, Player2, Player3);
        HAL.setup();
        Player2.setup();
        Player3.setup();

        frame.turn = 0;


        while (!util.game_over()) {
            frame.turn++;
            System.out.println("//////Turn " + frame.turn + "//////");
            for (int n = 0; n < 3; n++) {
                take_cpu_turn(frame.players[n]);
                System.out.println("$" + frame.players[n].Money);
                System.out.println(" ");
            }
            t.printBoard();
            System.out.println(" ");
            System.out.println(" ");
            System.out.println(" ");
            util.sleep(1000);
        }


        System.out.println("I GOT TWO WORDS FOR YA: GAME. OVER.");
        System.out.println("//////////AT END OF REGULATION//////////");

        Text.print_stocks(HAL);
        Text.print_stocks(Player2);
        Text.print_stocks(Player3);

        System.out.println("HAL Money: " + HAL.Money);
        System.out.println("Version1_2_0.Player 2 Money: " + Player2.Money);
        System.out.println("Version1_2_0.Player 3 Money: " + Player3.Money);

        System.out.println(" ");
        System.out.println(" ");
        System.out.println(" ");

        resolve_game();

        System.out.println("//////////FINAL RESULTS//////////");

        System.out.println("HAL Money: " + HAL.Money);
        System.out.println("Version1_2_0.Player 2 Money: " + Player2.Money);
        System.out.println("Version1_2_0.Player 3 Money: " + Player3.Money);

    }

    private static void init_three_neural_net() {
        init_three_neural_net(null);
    }

    private static void init_three_neural_net(String fileName) {
        int NEURONS = 1000;
        NeuronLayer layer01 = new NeuronLayer(NEURONS, INPUT_SIZE);
        NeuronLayer layer02 = new NeuronLayer(NEURONS, NEURONS);
        NeuronLayer layer03 = new NeuronLayer(NEURONS, NEURONS);
        NeuronLayer layer04 = new NeuronLayer(1, NEURONS);
        NeuronLayer layer11 = new NeuronLayer(NEURONS, INPUT_SIZE);
        NeuronLayer layer12 = new NeuronLayer(NEURONS, NEURONS);
        NeuronLayer layer13 = new NeuronLayer(NEURONS, NEURONS);
        NeuronLayer layer14 = new NeuronLayer(1, NEURONS);
        NeuronLayer layer21 = new NeuronLayer(NEURONS, INPUT_SIZE);
        NeuronLayer layer22 = new NeuronLayer(NEURONS, NEURONS);
        NeuronLayer layer23 = new NeuronLayer(NEURONS, NEURONS);
        NeuronLayer layer24 = new NeuronLayer(1, NEURONS);


        if (fileName != null) {
            Scanner inputStream = null;
            try {
                inputStream = new Scanner(new FileReader("/Users/Ralph/IdeaProjects/AcquireCPU/KeepNet"));
                System.out.println("FOUND THE FILE");
            } catch (FileNotFoundException e) {
                System.out.println("FILE NOT FOUND");
            }

            if (inputStream != null) {
                for (int i = 0; i < layer01.weights.length; ++i) {
                    for (int j = 0; j < layer01.weights[0].length; ++j) {
                        layer01.weights[i][j] = Double.parseDouble(inputStream.next());
                    }
                }

                for (int i = 0; i < layer02.weights.length; ++i) {
                    for (int j = 0; j < layer02.weights[0].length; ++j) {
                        layer02.weights[i][j] = Double.parseDouble(inputStream.next());
                    }
                }

                for (int i = 0; i < layer03.weights.length; ++i) {
                    for (int j = 0; j < layer03.weights[0].length; ++j) {
                        layer03.weights[i][j] = Double.parseDouble(inputStream.next());
                    }
                }

                for (int i = 0; i < layer04.weights.length; ++i) {
                    for (int j = 0; j < layer04.weights[0].length; ++j) {
                        layer04.weights[i][j] = Double.parseDouble(inputStream.next());
                    }
                }
            }
        }

        if (fileName != null) {
            Scanner inputStream = null;
            try {
                inputStream = new Scanner(new FileReader("/Users/Ralph/IdeaProjects/AcquireCPU/TradeNet"));
                System.out.println("FOUND THE FILE");
            } catch (FileNotFoundException e) {
                System.out.println("FILE NOT FOUND");
            }

            if (inputStream != null) {
                for (int i = 0; i < layer11.weights.length; ++i) {
                    for (int j = 0; j < layer11.weights[0].length; ++j) {
                        layer11.weights[i][j] = Double.parseDouble(inputStream.next());
                    }
                }

                for (int i = 0; i < layer12.weights.length; ++i) {
                    for (int j = 0; j < layer12.weights[0].length; ++j) {
                        layer12.weights[i][j] = Double.parseDouble(inputStream.next());
                    }
                }

                for (int i = 0; i < layer13.weights.length; ++i) {
                    for (int j = 0; j < layer13.weights[0].length; ++j) {
                        layer13.weights[i][j] = Double.parseDouble(inputStream.next());
                    }
                }

                for (int i = 0; i < layer14.weights.length; ++i) {
                    for (int j = 0; j < layer14.weights[0].length; ++j) {
                        layer14.weights[i][j] = Double.parseDouble(inputStream.next());
                    }
                }
            }
        }

        if (fileName != null) {
            Scanner inputStream = null;
            try {
                inputStream = new Scanner(new FileReader("/Users/Ralph/IdeaProjects/AcquireCPU/SellNet"));
                System.out.println("FOUND THE FILE");
            } catch (FileNotFoundException e) {
                System.out.println("FILE NOT FOUND");
            }

            if (inputStream != null) {
                for (int i = 0; i < layer21.weights.length; ++i) {
                    for (int j = 0; j < layer21.weights[0].length; ++j) {
                        layer21.weights[i][j] = Double.parseDouble(inputStream.next());
                    }
                }

                for (int i = 0; i < layer22.weights.length; ++i) {
                    for (int j = 0; j < layer22.weights[0].length; ++j) {
                        layer22.weights[i][j] = Double.parseDouble(inputStream.next());
                    }
                }

                for (int i = 0; i < layer23.weights.length; ++i) {
                    for (int j = 0; j < layer23.weights[0].length; ++j) {
                        layer23.weights[i][j] = Double.parseDouble(inputStream.next());
                    }
                }

                for (int i = 0; i < layer24.weights.length; ++i) {
                    for (int j = 0; j < layer24.weights[0].length; ++j) {
                        layer24.weights[i][j] = Double.parseDouble(inputStream.next());
                    }
                }
            }
        }

        keepNet = new FourLayerNet(layer01, layer02, layer03, layer04, 0.0009);
        tradeNet = new FourLayerNet(layer11, layer12, layer13, layer14, 0.0009);
        sellNet = new FourLayerNet(layer21, layer22, layer23, layer24, 0.0009);
    }

    private static void init_neural_net() {
        init_neural_net(null);
    }

    private static void init_neural_net(String fileName) {
        int NEURONS = 100;
        NeuronLayer layer1 = new NeuronLayer(NEURONS, INPUT_SIZE);
        NeuronLayer layer2 = new NeuronLayer(NEURONS, NEURONS);
        NeuronLayer layer3 = new NeuronLayer(NEURONS, NEURONS);
        NeuronLayer layer4 = new NeuronLayer(1, NEURONS);


        if (fileName != null) {
            Scanner inputStream = null;
            try {
                inputStream = new Scanner(new FileReader(fileName));
                System.out.println("FOUND THE FILE");
            } catch (FileNotFoundException e) {
                System.out.println("FILE NOT FOUND");
            }

            if (inputStream != null) {
                for (int i = 0; i < layer1.weights.length; ++i) {
                    for (int j = 0; j < layer1.weights[0].length; ++j) {
                        layer1.weights[i][j] = Double.parseDouble(inputStream.next());
                    }
                }

                for (int i = 0; i < layer2.weights.length; ++i) {
                    for (int j = 0; j < layer2.weights[0].length; ++j) {
                        layer2.weights[i][j] = Double.parseDouble(inputStream.next());
                    }
                }

                for (int i = 0; i < layer3.weights.length; ++i) {
                    for (int j = 0; j < layer3.weights[0].length; ++j) {
                        layer3.weights[i][j] = Double.parseDouble(inputStream.next());
                    }
                }

                for (int i = 0; i < layer4.weights.length; ++i) {
                    for (int j = 0; j < layer4.weights[0].length; ++j) {
                        layer4.weights[i][j] = Double.parseDouble(inputStream.next());
                    }
                }
            }
        }

        stockNet = new FourLayerNet(layer1, layer2, layer3, layer4, 0.000001);
    }


    public static void main(String[] args) {

        init_neural_net("/Users/Ralph/IdeaProjects/AcquireCPU/StockNet");
        //init_neural_net();

        try {
            log = new PrintWriter("AcquireLog.txt");
        } catch (FileNotFoundException exception) {

        }

        //runningTest();


        HAL.c.set(new double[] {86.85,
                                4521.31,
                                3222.37,
                                1,
                                2767.46,
                                1209.53,
                                3.03,
                                0.23,
                                0.07,
                                1.76,
                                -2.61,
                                -3.79,
                                0.62,
                                0.10,
                                0.37,
                                3895.91});

        play_irl_game();

        index = 1;

        /*loadTests3();
        loadTests();

        while (index < 15) {

            Player winner = null;

            base_val = Player2.c.playerVals.get(index);

            while (Player2 != winner) {

                HAL.c.set(new double[] {86.85,
                        4521.31,
                        3222.37,
                        1,
                        2767.46,
                        1209.53,
                        3.03,
                        0.23,
                        0.07,
                        1.76,
                        -2.61,
                        -3.79,
                        0.62,
                        0.10,
                        0.37,
                        3895.91});
                Player2.c.set(new double[] {86.85,
                        4521.31,
                        3222.37,
                        1,
                        2767.46,
                        1209.53,
                        3.03,
                        0.23,
                        0.07,
                        1.76,
                        -2.61,
                        -3.79,
                        0.62,
                        0.10,
                        0.37,
                        3895.91});
                Player3.c.set(new double[] {86.85,
                        4521.31,
                        3222.37,
                        1,
                        2767.46,
                        1209.53,
                        3.03,
                        0.23,
                        0.07,
                        1.76,
                        -2.61,
                        -3.79,
                        0.62,
                        0.10,
                        0.37,
                        3895.91});

                //HAL.c.randomize();
                //Player2.c.randomize();
                //Player3.c.randomize();

                //play_many_games(1000);
                play_many_nn_games(200);
                //generate_nn3_tests();
                //generate_nn_tests();

                if (PLR2wins > PLR3wins + 50 && PLR2wins > HAL_wins + 50) {
                    winner = Player2;
                }

                if (PLR3wins > PLR2wins && PLR3wins > HAL_wins) {
                    base_val -= (base_val / 1.0);
                } else if (HAL_wins > PLR2wins && HAL_wins > PLR3wins) {
                    base_val += (base_val / 1.0);
                }

                HAL.c.refresh();
                Player2.c.refresh();
                Player3.c.refresh();


                System.out.println("merges" + frame.merges);
                System.out.println();
                System.out.println();
                System.out.println();
                System.out.println(HAL.name + " wins: " + HAL_wins);
                System.out.println(Player2.name + " wins: " + PLR2wins);
                System.out.println(Player3.name + " wins: " + PLR3wins);
                System.out.println();
                System.out.println(HAL.name + " avg: " + (HAL.totWinnings/1000));
                System.out.println(Player2.name + " avg: " + (Player2.totWinnings/1000));
                System.out.println(Player3.name + " avg: " + (Player3.totWinnings/1000));
                System.out.println();
                System.out.println();
                System.out.println("HAL VALUES");
                for (Double d : HAL.c.playerVals) {
                    System.out.println(d);
                }
                System.out.println();
                System.out.println("MAL VALUES");
                for (Double d : Player2.c.playerVals) {
                    System.out.println(d);
                }
                System.out.println();
                System.out.println("LAL VALUES");
                for (Double d : Player3.c.playerVals) {
                    System.out.println(d);
                }

                in.nextInt();

            }

            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println("Index " + index + "'s final value is " + base_val);

            HAL.c.set(index, base_val);
            Player2.c.set(index, base_val);
            Player3.c.set(index, base_val);

            in.nextInt();
            index += 1;
        }*/

        log.close();
    }

    public static void runningTest() {

        int ord = rand.nextInt(6);

        //if (ord == 0) {
            frame.setup(HAL, Player2, Player3);
        //} else if (ord == 1) {
        //    frame.setup(HAL, Player3, Player2);
        //} else if (ord == 2) {
        //    frame.setup(Player2, HAL, Player3);
        //} else if (ord == 3) {
        //    frame.setup(Player2, Player3, HAL);
        //} else if (ord == 4) {
        //    frame.setup(Player3, HAL, Player2);
        //} else {
        //    frame.setup(Player3, Player2, HAL);
        //}

        if (HAL.human) HAL.setup(); else HAL.setupIRL();
        if (Player2.human) Player2.setup(); else Player2.setupIRL();
        if (Player3.human) Player3.setup(); else Player3.setupIRL();

        util = frame.util;

        /////////// TEST CONTENT ///////////

        frame.turn++;

        for (int i = 0; i < 9; i++) {
            frame.takeAction(new BuyStock(HAL, frame.Fusion));
        }

        frame.play_tile(103, HAL);
        frame.play_tile(91, HAL);
        frame.play_tile(92, HAL);
        frame.play_tile(80, HAL);

        for (int i = 0; i < 3; i++) {
            frame.takeAction(new BuyStock(Player2, frame.Fusion));
        }

        for (int i = 0; i < 6; i++) {
            frame.takeAction(new BuyStock(Player3, frame.Fusion));
        }

        for (int i = 0; i < 18; i++) {
            frame.takeAction(new BuyStock(Player3, frame.America));
        }

        System.out.println(Arrays.toString(frame.newGameState(HAL, -1)));

        //frame.play_tile(4, Player3);
        //frame.play_tile(5, Player3);
        //frame.play_tile(6, Player3);

        //for (double i : frame.gameStateArray(HAL)) System.out.println(i);

        t.printBoard();
    }
}