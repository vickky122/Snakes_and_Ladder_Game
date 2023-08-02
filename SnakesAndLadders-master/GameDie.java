import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import java.io.*;
import javax.imageio.*;
/* Represents a graphical standard 6-sided die*/

public class GameDie{
   private int value = 1;
   private final int X; //X and Y denote the locations of the die on the panel
   private final int Y;
   private Image image;
   
   public GameDie(int x, int y){
      X = x;
      Y = y;
      roll();
   }
   
   //Randomizes the value on the die and updates the image file to represent the number rolled
   public void roll(){
      value = (int)(Math.random() * 6) + 1;
      try{
         image = ImageIO.read(new File("" + value + ".png"));
      }
      catch(IOException e){
      
      }
   }
   
   public int getValue(){
      return value;
   }
   
   //draws the image of the die on the panel
   public void paint(Graphics g){
      g.drawImage(image, X, Y, null);
   }
}