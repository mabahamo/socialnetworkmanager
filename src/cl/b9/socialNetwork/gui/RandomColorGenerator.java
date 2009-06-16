/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.b9.socialNetwork.gui;

import java.awt.Color;
import java.util.Random;

/**
 * Retorna un color random, se garantiza que siempre color diferirá del siguiente
 * @author manuel
 */
public class RandomColorGenerator {

    static float hue,saturation, brightness;
    static Random r = new Random();
    static float[] bv = {1f,1f,0.6f};
    static float[] sv = {1f,0.75f,0.6f};
    static Color[] colors = {Color.BLUE,Color.CYAN,Color.GREEN,Color.MAGENTA,Color.ORANGE,Color.PINK,Color.RED,Color.YELLOW};
    static int counter = -1;
     
    public static Color nextColor() {
        counter++;
        if (counter<colors.length){
            return colors[counter];
        }
        saturation = sv[r.nextInt(sv.length)];
        brightness = bv[r.nextInt(bv.length)];
        hue = getAwayRandom(hue);
        return Color.getHSBColor(hue, saturation, brightness);
    }

  

    private static float getAwayRandom(float hue) {
        float f = r.nextFloat();
        while(Math.abs(f-hue)<0.2){
            f = r.nextFloat();
        }
        return f;
    }
    
}
