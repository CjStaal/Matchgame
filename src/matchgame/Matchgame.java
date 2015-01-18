/**
 * @author Charles Staal
 * @version 0.9.0 This program is created by Charles Staal.
 *
 * Matchgame is a simple matching game utilizing the Swing API.
 *
 * Last updated 1/17/2015 at 22:21.
 */

package Matchgame;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.SwingWorker;
import javax.swing.JOptionPane;
import java.util.StringTokenizer;

public class Matchgame extends JFrame implements ActionListener {

    private static String currentPlayer = "Player 1";
    private static char[] buttonValue;
    private static JButton[] button;
    private static boolean runCheck = false, finished = false;
    private static final long serialVersionUID = -1890926013993464332L;
    private static JLabel message = new JLabel(currentPlayer + ", choose a tile...");
    private static int currentTurnClicks = 0, player1Matches = 0, player2Matches = 0, clickedButton1 = -1, clickedButton2 = -2, numberOfMatchesFound = 0, totalMatches = 0;

    public static void main(String[] args) {
        Matchgame theGame = new Matchgame();
        theGame.setVisible(true);
        synchronized (theGame) {
            while (!finished) {
                if (runCheck) {
                    mySwingWorker(clickedButton1, clickedButton2);
                }
            }
        }
    }

    private Matchgame() {
        super("Staal's MatchGame");
        while (true) {
            StringTokenizer tk;
            try {
                tk = new StringTokenizer(JOptionPane.showInputDialog("Please enter enter x,y values\n"
                        + "{amount of buttons across comma amount of buttons down}\n must be divisible by 2 and <= 52. "), ",");
                if (tk.countTokens() == 2) {
                    int width = Integer.parseInt(tk.nextToken());
                    int height = Integer.parseInt(tk.nextToken());
                    int amount = width * height;
                    if ((amount) % 2 == 0 & amount <= 52) {
                        if (setBoard(width, height, amount)) {
                            return;
                        } else {
                            JOptionPane.showMessageDialog(null, "Error creating "
                                    + "Board, please try again.");
                        }
                    }
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            JOptionPane.showMessageDialog(null, "Incorrect paramaters");
        }
    }

    private final boolean setBoard(int width, int height, int amount) {
        totalMatches = amount / 2;
        setSize(height * 100, width * 125);
        buttonValue = new char[amount];
        button = new JButton[amount];
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        add(message, BorderLayout.NORTH);
        JPanel gameBoard = new JPanel();
        gameBoard.setLayout(new GridLayout(width, height));
        for (int i = 0; i < amount; ++i) {
            button[i] = new JButton(Integer.toString(i + 1));
            button[i].setBackground(Color.GRAY);
            button[i].addActionListener(this);
            gameBoard.add(button[i]);
        }
        add(gameBoard, BorderLayout.CENTER);
        for (int c = 0, b = 65; c < amount; ++c) {
            buttonValue[c] = (char) (b);
            if (c % 2 == 1) {
                ++b;
            }
        }
        char temp;
        for (int c = 5; c >= 0; --c) {
            for (int d = amount - 1, b; d >= 0; --d) {
                b = (int) Math.round(Math.random() * d);
                temp = buttonValue[d];
                buttonValue[d] = buttonValue[b];
                buttonValue[b] = temp;
            }
        }
        return true;
    }

    private final static void switchPlayer() {
        if (currentPlayer.equals("Player 1")) {
            currentPlayer = "Player 2";
        } else {
            currentPlayer = "Player 1";
        }
        message.setText(currentPlayer + ", choose your tile...");
        currentTurnClicks = 0;
    }

    public final void actionPerformed(ActionEvent e) {
        if (!runCheck) {
            mySwingWorker(Integer.parseInt(e.getActionCommand()));
        }
    }

    private final static SwingWorker<Integer, String> mySwingWorker(int tButtonNum) {
        Color playerColor = Color.RED;
        if (currentPlayer.equals("Player 2")) {
            playerColor = Color.GREEN;
        }
        if (++currentTurnClicks == 1) {
            clickedButton1 = tButtonNum--;
            message.setText(currentPlayer + ", choose your second tile...");
            setUpButton(clickedButton1, Character.toString(buttonValue[tButtonNum]), playerColor, false);
        } else if (currentTurnClicks == 2) {
            clickedButton2 = tButtonNum--;
            setUpButton(clickedButton2, Character.toString(buttonValue[tButtonNum]), playerColor, false);
            runCheck = true;
        }
        return null;
    }

    private final static SwingWorker<Integer, String> mySwingWorker(int buttonNumber, int buttonNumber2) {
        if (Character.toString(buttonValue[clickedButton1 - 1]).equals(Character.toString(buttonValue[clickedButton2 - 1]))) {
            if (currentPlayer.equals("Player 1")) {
                ++player1Matches;
            } else {
                ++player2Matches;
            }
            if (++numberOfMatchesFound == totalMatches) {
                winCheck();
                return null;
            }
        } else {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException i) {
                i.printStackTrace();
            }
            setUpButton(buttonNumber, Integer.toString(buttonNumber), Color.GRAY, true);
            setUpButton(buttonNumber2, Integer.toString(buttonNumber2), Color.GRAY, true);
        }
        switchPlayer();
        runCheck = false;
        return null;
    }

    private final static void setUpButton(int buttonNumberX, String text, Color background, boolean value) {
        button[--buttonNumberX].setBackground(background);
        button[buttonNumberX].setText(text);
        button[buttonNumberX].setEnabled(value);
    }

    private final static void winCheck() {
        if (player1Matches > player2Matches) {
            message.setText("Player 1 Wins with " + player1Matches + " matches.");
        } else if (player1Matches < player2Matches) {
            message.setText("Player 2 Wins with " + player2Matches + " matches.");
        } else if (player1Matches == player2Matches) {
            message.setText("Tie. Game over.");
        }
        finished = true;
    }
}