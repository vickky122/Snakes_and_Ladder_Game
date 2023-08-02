import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import java.io.*;
import javax.imageio.*;

public class Player{
   private Image image;
   private int spaceNum;
   private String name;
   
   public Player(String color, String name){
      try{
         image = ImageIO.read(new File(color + ".png"));
      }
      catch(IOException e){
      
      }
      this.name = name;
      spaceNum = 0;
   }
   
   public int getSpaceNum(){
      return spaceNum;
   }
   
   public void setSpaceNum(int newNum){
      spaceNum = newNum;
   }
   
   public String getName(){
      return name;
   }
   
   public void paint(Graphics g, int x, int y){
      g.drawImage(image, x, y, null);
   }
}