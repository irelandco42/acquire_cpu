package Version1_2_0;

public class Text {

    static Main main = new Main();

    public Text() {

    }

    public static String tile_name(int tile) {
        int tileNum = tile % 12;
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

        return tileNum + letter;
    }

    public static void print_hand(Player player) {
        System.out.print(player.handString());
    }

    public static void print_stocks(Player player) {
        System.out.print(player.name + " stocks: {");
        for (int i = 0; i < 6; i++) {
            System.out.print(((int)player.stocks[i]) + ", ");
        }
        System.out.println((int) player.stocks[6] + "}");
    }

    public static void stocks_of_corp(Corporation corp) {
        for (int i = 0; i < 3; i++) {
            System.out.print(main.frame.players[i].name + " has " + main.frame.players[i].stocks[corp.num] + " stocks in "
            + corp.name + ". ");
        }
        System.out.println();
    }

    public void printBoard() {
        int num = 1;
        for (int i = 0; i < 9; i++) {

            for (int d = 0; d < 11; d++) {
                System.out.print(main.frame.CorpOverlay.get(num) + " ");
                num++;
            }

            System.out.println(main.frame.CorpOverlay.get(num) + " ");
            num++;
        }
    }
}