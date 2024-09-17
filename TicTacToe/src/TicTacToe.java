import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TicTacToe implements ActionListener {

    Random random = new Random();
    JFrame frame = new JFrame();
    JPanel title_panel = new JPanel();
    JPanel button_panel = new JPanel();
    JLabel textfield = new JLabel();
    JButton[] buttons = new JButton[9];
    boolean player1_turn;

    TicTacToe() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        frame.getContentPane().setBackground(new Color(50, 50, 50));
        frame.setLayout(new BorderLayout());
        frame.setVisible(true);

        textfield.setBackground(new Color(25, 25, 25));
        textfield.setForeground(new Color(25, 255, 0));
        textfield.setFont(new Font("Ink Free", Font.BOLD, 75));
        textfield.setHorizontalAlignment(JLabel.CENTER);
        textfield.setText("Tic-Tac-Toe");
        textfield.setOpaque(true);

        title_panel.setLayout(new BorderLayout());
        title_panel.setBounds(0, 0, 800, 100);

        button_panel.setLayout(new GridLayout(3, 3));
        button_panel.setBackground(new Color(150, 150, 150));

        for (int i = 0; i < 9; i++) {
            buttons[i] = new JButton();
            button_panel.add(buttons[i]);
            buttons[i].setFont(new Font("MV Boli", Font.BOLD, 120));
            buttons[i].setFocusable(false);
            buttons[i].addActionListener(this);
        }

        title_panel.add(textfield);
        frame.add(title_panel, BorderLayout.NORTH);
        frame.add(button_panel);

        firstTurn();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (int i = 0; i < 9; i++) {
            if (e.getSource() == buttons[i]) {
                if (player1_turn) {
                    if (buttons[i].getText() == "") {
                        buttons[i].setForeground(new Color(255, 0, 0));
                        buttons[i].setText("X");
                        player1_turn = false;
                        textfield.setText("O turn");
                        check();
                    }
                } else {
                    if (buttons[i].getText() == "") {
                        buttons[i].setForeground(new Color(0, 0, 255));
                        buttons[i].setText("O");
                        player1_turn = true;
                        textfield.setText("X turn");
                        check();
                    }
                }
            }
        }
    }

    public void firstTurn() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (random.nextInt(2) == 0) {
            player1_turn = true;
            textfield.setText("X turn");
        } else {
            player1_turn = false;
            textfield.setText("O turn");
        }
    }

    public void check() {
        // Check X win conditions
        if (
            (buttons[0].getText() == "X" && buttons[1].getText() == "X" && buttons[2].getText() == "X") ||
            (buttons[3].getText() == "X" && buttons[4].getText() == "X" && buttons[5].getText() == "X") ||
            (buttons[6].getText() == "X" && buttons[7].getText() == "X" && buttons[8].getText() == "X") ||
            (buttons[0].getText() == "X" && buttons[3].getText() == "X" && buttons[6].getText() == "X") ||
            (buttons[1].getText() == "X" && buttons[4].getText() == "X" && buttons[7].getText() == "X") ||
            (buttons[2].getText() == "X" && buttons[5].getText() == "X" && buttons[8].getText() == "X") ||
            (buttons[0].getText() == "X" && buttons[4].getText() == "X" && buttons[8].getText() == "X") ||
            (buttons[2].getText() == "X" && buttons[4].getText() == "X" && buttons[6].getText() == "X")
        ) {
            xWins();
        }
        // Check O win conditions
        else if (
            (buttons[0].getText() == "O" && buttons[1].getText() == "O" && buttons[2].getText() == "O") ||
            (buttons[3].getText() == "O" && buttons[4].getText() == "O" && buttons[5].getText() == "O") ||
            (buttons[6].getText() == "O" && buttons[7].getText() == "O" && buttons[8].getText() == "O") ||
            (buttons[0].getText() == "O" && buttons[3].getText() == "O" && buttons[6].getText() == "O") ||
            (buttons[1].getText() == "O" && buttons[4].getText() == "O" && buttons[7].getText() == "O") ||
            (buttons[2].getText() == "O" && buttons[5].getText() == "O" && buttons[8].getText() == "O") ||
            (buttons[0].getText() == "O" && buttons[4].getText() == "O" && buttons[8].getText() == "O") ||
            (buttons[2].getText() == "O" && buttons[4].getText() == "O" && buttons[6].getText() == "O")
        ) {
            oWins();
        }
        // Check draw condition
        else if (isBoardFull()) {
            draw();
        }
    }

    // Check if all buttons are filled
    public boolean isBoardFull() {
        for (JButton button : buttons) {
            if (button.getText().equals("")) {
                return false;
            }
        }
        return true;
    }

    // When X wins
    public void xWins() {
        textfield.setText("X wins");
        disableButtons();
        // Delay the reset so the message can be seen
        new java.util.Timer().schedule(
            new java.util.TimerTask() {
                @Override
                public void run() {
                    resetGame();
                }
            }, 2000 // Delay of 2 seconds before resetting
        );
    }

    // When O wins
    public void oWins() {
        textfield.setText("O wins");
        disableButtons();
        // Delay the reset so the message can be seen
        new java.util.Timer().schedule(
            new java.util.TimerTask() {
                @Override
                public void run() {
                    resetGame();
                }
            }, 2000 // Delay of 2 seconds before resetting
        );
    }

    // When it's a draw
    public void draw() {
        textfield.setText("Stalemate");
        disableButtons();
        // Delay the reset so the message can be seen
        new java.util.Timer().schedule(
            new java.util.TimerTask() {
                @Override
                public void run() {
                    resetGame();
                }
            }, 2000 // Delay of 2 seconds before resetting
        );
    }

    // Disable all buttons
    public void disableButtons() {
        for (int i = 0; i < 9; i++) {
            buttons[i].setEnabled(false);
        }
    }

 // Reset the game board
    public void resetGame() {
        for (int i = 0; i < 9; i++) {
            buttons[i].setText("");
            buttons[i].setEnabled(true);
            buttons[i].setBackground(new Color(238, 238, 238)); // Default button background color in Swing
        }

        firstTurn(); // Randomly decide the next starting player
    }
}