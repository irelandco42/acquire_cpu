import java.util.ArrayList;
import java.util.Random;

public class test {

    static Random rand  = new Random();

    public static void main(String[] args) {
        ArrayList<Integer> array = new ArrayList();

        for (int i = 1; i < 109; i++) {
            array.add(8);
        }

        ArrayList<Integer> temp = new ArrayList<>();

        double startTime = System.nanoTime();

        for (int i = 1; i < 109; i++) {
            temp = (ArrayList<Integer>) array.clone();
            //temp.add(8);
        }

        System.out.println(System.nanoTime() - startTime);
    }
}
