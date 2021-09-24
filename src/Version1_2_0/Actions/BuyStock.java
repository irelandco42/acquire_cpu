package Version1_2_0.Actions;

import Version1_2_0.Player;
import Version1_2_0.Corporation;

public class BuyStock extends Action {

    Corporation corp;

    public BuyStock(Player _player, Corporation corp) {
        super(_player);
        this.corp = corp;
    }

    @Override
    public void takeAction() {
        u.print(player.name + " bought stock in " + corp.name);

        player.stocks[corp.num] += 1;
        corp.stocks_left--;
        corp.stocks_bought++;
        player.Money -= corp.stock_value();
        //System.out.println("Post stocks left: " + corp.stocks_left);
    }

    @Override
    public void undoAction() {
        u.print(player.name + " unbought stock in " + corp.name);

        player.stocks[corp.num] -= 1;
        corp.stocks_left++;
        corp.stocks_bought--;
        player.Money += corp.stock_value();
    }
}
