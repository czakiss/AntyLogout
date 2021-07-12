package czakiss.antylogout.model;

import lombok.*;

import java.util.Random;


@ToString
public class RandomColor {
    private static final Random rand = new Random();

    private static final char[] colors = {
            '1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'
    };

    public static char getRandom(){
        return colors[rand.nextInt(colors.length)];
    }

}
