package Version1_2_0;

import java.util.Scanner;

class Terminal {

    Scanner in;

    public Terminal(Scanner in) {
        this.in = in;
    }

    public void engage() {
        String next = in.next();
        if (next.equals(Main.HAL.name)){
            modify(Main.HAL);
        } else if (next.equals(Main.Player2.name)){
            modify(Main.Player2);
        } else if (next.equals(Main.Player3.name)){
            modify(Main.Player3);
        } else if (next.equals("dismoney")) {
            displayMoney();
        } else {
            System.out.println("Command " + next + " not recognized!");
            in.nextLine();
        }
    }

    private void displayMoney() {
        System.out.println(Main.HAL.name + ": " + Main.HAL.Money);
        System.out.println(Main.Player2.name + ": " + Main.Player2.Money);
        System.out.println(Main.Player3.name + ": " + Main.Player3.Money);
    }

    private void modify(Player player) {
        String next = in.next();
        if (next.equals("money")) {
            addMoney(player);
        } else if (next.equals("stock")) {
            addStock(player);
        } else {
            System.out.println("Command " + next + " not recognized!");
            in.nextLine();
        }
    }

    private void addMoney(Player player) {
        String next = in.next();
        int modifier = Integer.parseInt(next);
        player.Money += modifier;
    }

    private void addStock(Player player) {
        String next = in.next();
        int corp = Integer.parseInt(next);
        next = in.next();

        int modifier = Integer.parseInt(next);
        player.stocks[corp] += modifier;
        Main.frame.Corps[corp].stocks_left -= modifier;
        Main.frame.Corps[corp].stocks_bought += modifier;

        System.out.println(player.stocks[corp]);
        System.out.println(Main.frame.Corps[corp].stocks_left);
        System.out.println(Main.frame.Corps[corp].stocks_bought);
    }
}