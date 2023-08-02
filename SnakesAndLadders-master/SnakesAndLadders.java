import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import java.io.*;
import javax.imageio.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.awt.event.*;
import javax.sound.sampled.*;

public class SnakesAndLadders extends JPanel implements MouseListener {
   private Image board;
   private GameDie die;
   private static HashMap<Integer, Integer> warps = new HashMap<Integer, Integer>(); // stores the locations and
                                                                                     // endpoints for snakes and ladders
   private static JFrame mainFrame; // the main window for the game. Later subwindows will be included to indicate
                                    // player info, winner, and snakes and ladder locations
   private static ArrayList<Player> players = new ArrayList<Player>(); // collection of players within the game, ranges
                                                                       // in size from 2 to 6
   private static HashMap<Integer, Coordinate> locations = new HashMap<Integer, Coordinate>(); // maps a space number to
                                                                                               // a location on the
                                                                                               // panel
   private Timer rollTimer = new Timer(60, new RollTimer()); // simulates the "animated rolling" of the die
   private int rollShows; // how many times the die "flips" before landing
   private int movesLeft; // counts down based on the die roll
   private Timer moveTimer = new Timer(500, new MoveTimer()); // controls the animation of the player advancing along
                                                              // board
   private Timer warpTimer = new Timer(1000, new WarpTimer()); // controls the pause before a player is moved through a
                                                               // warp
   private int currentPlayer;
   private File yourFile; // location of the sound file to be used
   private AudioInputStream stream;
   private AudioFormat format;
   private DataLine.Info info;
   private Clip clip;
   private boolean moveBackwards; // used in the case where the player's move exceeds 100

   public SnakesAndLadders() {
      try {
         board = ImageIO.read(new File("gameboard.jpg"));
      } catch (IOException e) {

      }
      addMouseListener(this); // allows game to respond to button clicks
      die = new GameDie(1000, 150);
      currentPlayer = 0; // player 1 will begin the game
      moveBackwards = false;
      repaint(); // updates the state of the board
   }

   // Hard codes the locations of the snakes and ladders in the following format
   // (start, end)
   public static void fillWarps() {
      warps.put(1, 38);
      warps.put(4, 14);
      warps.put(9, 31);
      warps.put(17, 7);
      warps.put(21, 42);
      warps.put(28, 84);
      warps.put(51, 67);
      warps.put(54, 34);
      warps.put(62, 19);
      warps.put(64, 60);
      warps.put(71, 91);
      warps.put(80, 100);
      warps.put(87, 24);
      warps.put(93, 73);
      warps.put(95, 75);
      warps.put(98, 79);
   }

   // invoked when the repaint() method is called
   public void paintComponent(Graphics g) {
      g.setColor(Color.WHITE); // white background
      g.fillRect(0, 0, getWidth(), getHeight());
      g.setFont(new Font("Arial Black", Font.BOLD, 40));
      g.setColor(Color.BLACK);
      g.drawString("Snakes and Ladders!", 100, 50);
      g.setFont(new Font("Arial Black", Font.BOLD, 20));
      g.drawString("by Vikrant Kumar Yadav", 100, 90);
      g.setFont(new Font("Arial Black", Font.BOLD, 40));
      g.drawString(players.get(currentPlayer).getName() + "'s Turn!", 1000, 90); // displays the name of the current
                                                                                 // player
      g.drawImage(board, 100, 100, null);

      // paint each player's location
      for (Player player : players) {
         int x = locations.get(player.getSpaceNum()).getX();
         int y = locations.get(player.getSpaceNum()).getY();
         player.paint(g, x, y);
      }

      // rolling is only enabled when an animation isn't taking place
      if (!rollTimer.isRunning() && !moveTimer.isRunning() && !warpTimer.isRunning()) {
         g.setColor(Color.BLACK);
         g.fillRect(975, 300, 150, 100);
         g.setColor(Color.WHITE);
         g.setFont(new Font("Arial Black", Font.PLAIN, 40));
         g.drawString("Roll!", 1000, 370);
      }
      die.paint(g);
   }

   // maps spaces "0" through 100 with coordinates on the board
   public static void fillCoordinates() {
      for (int i = 0; i <= 100; i++) {
         locations.put(i, new Coordinate(i));
      }
   }

   // method required for mouse listener interface
   public void mousePressed(MouseEvent e) {

   }

   // method required for mouse listener interface
   public void mouseReleased(MouseEvent e) {

   }

   // method required for mouse listener interface
   public void mouseEntered(MouseEvent e) {

   }

   // method required for mouse listener interface
   public void mouseExited(MouseEvent e) {

   }

   // method required for mouse listener interface, handles the clicking of the
   // roll button
   public void mouseClicked(MouseEvent e) {
      int x = e.getX(); // get location of the mouse click
      int y = e.getY();
      if (!rollTimer.isRunning() && !moveTimer.isRunning() && !warpTimer.isRunning()) { // make sure no animation is
                                                                                        // occurring
         if (x >= 975 && x <= 1125 && y >= 300 && y <= 400) { // mouse click was within limits of the button
            try { // play fanfare music
                  // https://stackoverflow.com/questions/2416935/how-to-play-wav-files-with-java
               try {
                  clip.stop();
               } catch (Exception ex) {
               }
               yourFile = new File("dice.wav");
               stream = AudioSystem.getAudioInputStream(yourFile);
               format = stream.getFormat();
               info = new DataLine.Info(Clip.class, format);
               clip = (Clip) AudioSystem.getLine(info);
               clip.open(stream);
               clip.start();
            } catch (Exception exception) {
               System.out.println(exception);
               // whatevers
            } // end play fanfare music
            rollShows = (int) (Math.random() * 6) + 5; // randomize how many dice faces will show in animation
            rollTimer.start();
         }
      }
   }

   // handles the popup windows at the start of the game to get player info
   public static void getPlayerInfo() {
      Object[] possibilities = { "2", "3", "4", "5", "6" }; // numbers of possible players
      ArrayList<String> colors = new ArrayList<String>(); // pawn colors
      colors.add("blue");
      colors.add("brown");
      colors.add("green");
      colors.add("orange");
      colors.add("pink");
      colors.add("purple");
      colors.add("red");
      colors.add("yellow");
      int numPlayers = Integer.parseInt((String) JOptionPane.showInputDialog(null, // popup window for number of players
            "Number of Players",
            "Welcome",
            JOptionPane.PLAIN_MESSAGE,
            null,
            possibilities,
            "1"));
      for (int i = 1; i <= numPlayers; i++) {// grab info on each player
         String name = (String) JOptionPane.showInputDialog(null,
               "Name of Player #" + i,
               "Welcome",
               JOptionPane.PLAIN_MESSAGE,
               null,
               null,
               null);
         String color = (String) JOptionPane.showInputDialog(null,
               name + ", choose your color!",
               "Welcome",
               JOptionPane.PLAIN_MESSAGE,
               null,
               colors.toArray(),
               null);
         players.add(new Player(color, name));
         colors.remove(color); // no color can be used by two players
      }
   }

   // handle the rolling of the die
   private class RollTimer implements ActionListener {
      public void actionPerformed(ActionEvent e) {
         // count down the roll shows and re-randomize the die
         if (rollShows > 0) {
            rollShows--;
            die.roll();
         } else { // end the animation and start moving the pawn
            rollTimer.stop();
            movesLeft = die.getValue();
            moveTimer.start();
         }
         repaint();
      }
   }

   // handle advancing along the board
   private class MoveTimer implements ActionListener {
      public void actionPerformed(ActionEvent e) {
         if (movesLeft > 0) { // player has moves left
            movesLeft--;
            try { // play fanfare music
                  // https://stackoverflow.com/questions/2416935/how-to-play-wav-files-with-java
               try {
                  clip.stop();
               } catch (Exception ex) {
               }
               yourFile = new File("gamepiece.wav");
               stream = AudioSystem.getAudioInputStream(yourFile);
               format = stream.getFormat();
               info = new DataLine.Info(Clip.class, format);
               clip = (Clip) AudioSystem.getLine(info);
               clip.open(stream);
               clip.start();
            } catch (Exception exception) {
               System.out.println(exception);
               // whatevers
            } // end play fanfare music

            // player is moving forwards
            if (!moveBackwards) {
               players.get(currentPlayer).setSpaceNum(players.get(currentPlayer).getSpaceNum() + 1); // advance player
               if (players.get(currentPlayer).getSpaceNum() == 100 && movesLeft > 0) { // player exceeds 100
                  moveBackwards = true;
               } else if (players.get(currentPlayer).getSpaceNum() == 100 && movesLeft == 0) { // player lands on 100
                                                                                               // exactly
                  repaint();
                  endGame();
               }
            } else { // player is moving backwards
               players.get(currentPlayer).setSpaceNum(players.get(currentPlayer).getSpaceNum() - 1);
               if (movesLeft == 0) {
                  moveBackwards = false;
               }
            }
         }
         // player is done moving
         else {
            moveTimer.stop();
            if (warps.get(players.get(currentPlayer).getSpaceNum()) != null) { // player landed on a warp
               if (warps.get(players.get(currentPlayer).getSpaceNum()) > players.get(currentPlayer).getSpaceNum()) { // player
                                                                                                                     // landed
                                                                                                                     // on
                                                                                                                     // ladder
                  try { // play fanfare music
                        // https://stackoverflow.com/questions/2416935/how-to-play-wav-files-with-java
                     try {
                        clip.stop();
                     } catch (Exception ex) {
                     }
                     yourFile = new File("ladder.wav");
                     stream = AudioSystem.getAudioInputStream(yourFile);
                     format = stream.getFormat();
                     info = new DataLine.Info(Clip.class, format);
                     clip = (Clip) AudioSystem.getLine(info);
                     clip.open(stream);
                     clip.start();
                  } catch (Exception exception) {
                     System.out.println(exception);
                     // whatevers
                  } // end play fanfare music
               } else { // player landed on snake
                  try { // play fanfare music
                        // https://stackoverflow.com/questions/2416935/how-to-play-wav-files-with-java
                     try {
                        clip.stop();
                     } catch (Exception ex) {
                     }
                     yourFile = new File("snake.wav");
                     stream = AudioSystem.getAudioInputStream(yourFile);
                     format = stream.getFormat();
                     info = new DataLine.Info(Clip.class, format);
                     clip = (Clip) AudioSystem.getLine(info);
                     clip.open(stream);
                     clip.start();
                  } catch (Exception exception) {
                     System.out.println(exception);
                     // whatevers
                  } // end play fanfare music
               }
               warpTimer.start();
            } else { // player goes again if they rolled a 6; otherwise change player
               if (die.getValue() != 6) {
                  currentPlayer++;
                  if (currentPlayer == players.size()) {
                     currentPlayer = 0;
                  }
               }
            }
         }
         repaint();
      }
   }

   // game ends when a player lands on 100 exactly
   public void endGame() {
      try { // play fanfare music
            // https://stackoverflow.com/questions/2416935/how-to-play-wav-files-with-java
         try {
            clip.stop();
         } catch (Exception ex) {
         }
         yourFile = new File("victory.wav");
         stream = AudioSystem.getAudioInputStream(yourFile);
         format = stream.getFormat();
         info = new DataLine.Info(Clip.class, format);
         clip = (Clip) AudioSystem.getLine(info);
         clip.open(stream);
         clip.start();
      } catch (Exception exception) {
         System.out.println(exception);
         // whatevers
      } // end play fanfare music

      JOptionPane.showMessageDialog(new JFrame("Congratulations"), players.get(currentPlayer).getName() + " wins!"); // show
                                                                                                                     // popup
                                                                                                                     // window
                                                                                                                     // and
                                                                                                                     // exit
                                                                                                                     // game
      System.exit(0);

   }

   // handles the delay for a warp
   private class WarpTimer implements ActionListener {
      public void actionPerformed(ActionEvent e) {

         players.get(currentPlayer).setSpaceNum(warps.get(players.get(currentPlayer).getSpaceNum())); // send player to
                                                                                                      // the mapped warp
                                                                                                      // space
         warpTimer.stop();
         repaint();
         if (players.get(currentPlayer).getSpaceNum() == 100) {
            endGame();
         }

         // if player rolled a 6, they go again; otherwise go to next player
         if (die.getValue() != 6) {
            currentPlayer++;
            if (currentPlayer == players.size()) {
               currentPlayer = 0;
            }
         }

      }
   }

   // Builds the main frame and sets the panel contents. Will be modified to ask
   // for player info first.
   public static void main(String[] args) {
      fillCoordinates();
      fillWarps();
      getPlayerInfo();

      mainFrame = new JFrame("Snakes and Ladders!");
      mainFrame.setSize(1500, 1000);
      mainFrame.setLocation(0, 0);
      mainFrame.setContentPane(new SnakesAndLadders());
      mainFrame.setVisible(true);
      mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      mainFrame.setResizable(false);
   }
}