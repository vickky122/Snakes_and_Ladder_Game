public class Coordinate{
   private int x, y;
   private static final int DIM = 80;
   private static final int startingX = 100;
   private static final int startingY = 815;
   
   public Coordinate(int num){
      switch(num){
         case 0: x = 20;
            y = 815;
            break;
         default: 
            if ((num - 1) / 10 % 2 == 0){
               if (num % 10 == 0){
                  x = 820;
               }
               else{
                  x = startingX + DIM * ((num - 1) % 10);
               }
            }
            else{
               if (num % 10 == 0){
                  x = 100;
               }
               else{
                  x = (startingX + (DIM * 9)) - DIM * ((num - 1) % 10);
               }
            }
            y = startingY - DIM * ((num - 1) / 10);
      }
   }

   public int getX(){
      return x;
   }
   
   public int getY(){
      return y;
   }

}