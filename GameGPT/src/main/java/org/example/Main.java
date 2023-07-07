package org.example;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Main {
    private static final int ROWS = 21;
    private static final int COLS = 21;
    private static final int CELL_SIZE = 45;
    private static final int WINDOW_WIDTH = COLS * CELL_SIZE;
    private static final int WINDOW_HEIGHT = ROWS * CELL_SIZE;

    private static JFrame frame;
    private static MainPanel mainPanel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Random World Explorer");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);

            mainPanel = new MainPanel();
            frame.getContentPane().add(mainPanel);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            mainPanel.startGame();
        });
    }

    private static class MainPanel extends JPanel implements ActionListener {
        private int playerRow;
        private int playerCol;
        private int goalRow;
        private int goalCol;
        private boolean[][] field;
        private boolean gameFinished;
        private Color emptySpaceColor;
        private String obstacleEmoji;
        private int worldsTraveled;

        private MainPanel() {
            setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
            setFocusable(true);
            addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    if (!gameFinished) {
                        movePlayer(e.getKeyCode());
                    }
                }
            });
        }

        public void startGame() {
            playerRow = ROWS / 2;
            playerCol = COLS / 2;
            field = generateField();
            emptySpaceColor = getRandomColor();
            obstacleEmoji = getRandomObstacleEmoji();
            generateGoal();
            gameFinished = false;
            worldsTraveled = 0;

            Timer timer = new Timer(100, this);
            timer.start();
            requestFocusInWindow();
        }

        private boolean[][] generateField() {
            boolean[][] field = new boolean[ROWS][COLS];
            for (int i = 0; i < ROWS; i++) {
                for (int j = 0; j < COLS; j++) {
                    field[i][j] = Math.random() < 0.2; // 20% chance of obstacle
                }
            }
            return field;
        }

        private void generateGoal() {
            while (true) {
                goalRow = (int) (Math.random() * ROWS);
                goalCol = (int) (Math.random() * COLS);
                if (!field[goalRow][goalCol]) {
                    break;
                }
            }
        }

        private void movePlayer(int keyCode) {
            int newRow = playerRow;
            int newCol = playerCol;

            switch (keyCode) {
                case KeyEvent.VK_UP:
                    newRow--;
                    break;
                case KeyEvent.VK_DOWN:
                    newRow++;
                    break;
                case KeyEvent.VK_LEFT:
                    newCol--;
                    break;
                case KeyEvent.VK_RIGHT:
                    newCol++;
                    break;
            }

            if (newRow >= 0 && newRow < ROWS && newCol >= 0 && newCol < COLS && !field[newRow][newCol]) {
                playerRow = newRow;
                playerCol = newCol;

                if (playerRow == goalRow && playerCol == goalCol) {
                    worldsTraveled++;
                    generateNewLevel();
                }

                repaint();
            }
        }

        private void generateNewLevel() {
            field = generateField();
            emptySpaceColor = getRandomColor();
            obstacleEmoji = getRandomObstacleEmoji();
            generateGoal();
            gameFinished = false;
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.setColor(emptySpaceColor);
            g.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

            for (int i = 0; i < ROWS; i++) {
                for (int j = 0; j < COLS; j++) {
                    int x = j * CELL_SIZE;
                    int y = i * CELL_SIZE;

                    if (field[i][j]) {
                        g.setColor(emptySpaceColor);
                        g.fillRect(x, y, CELL_SIZE, CELL_SIZE);

                        g.setColor(Color.BLACK);
                        g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
                        g.drawString(obstacleEmoji, x + 10, y + 28);
                    } else {
                        g.setColor(emptySpaceColor);
                        g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                    }
                    if (i == playerRow && j == playerCol) {
                        g.setColor(Color.RED);
                        g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 25));
                        g.drawString("🧍‍♂️", x + 10, y + 30);
                    }
                    if (i == goalRow && j == goalCol) {
                        g.setColor(Color.BLUE);
                        g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 25));
                        g.drawString("🚪", x + 10, y + 30);
                    }
                }
            }

            g.setColor(getOppositeColor(emptySpaceColor));
            g.setFont(new Font("Segoe UI", Font.BOLD, 20));
            g.drawString("Worlds Traveled: " + worldsTraveled, 10, 25);
        }

        private Color getRandomColor() {
            int r = (int) (Math.random() * 256);
            int g = (int) (Math.random() * 256);
            int b = (int) (Math.random() * 256);
            return new Color(r, g, b);
        }

        private Color getOppositeColor(Color color) {
            int r = 255 - color.getRed();
            int g = 255 - color.getGreen();
            int b = 255 - color.getBlue();
            return new Color(r, g, b);
        }

        private String getRandomObstacleEmoji() {
            String[] obstacleEmojis = {
                    "🌳", "🌴", "🌵", "🍄", "🌸", "🌀", "🌙", "🌈", "🔮", "🕷️", "🔥", "💀", "\uD83D\uDC40", "⚡", "\uD83E\uDEA8", "🌊", "🔪", "🌚", "♟", "👾", "\uD83E\uDDE9", "🌓", "🦴", "\uD83D\uDCBF", "✡", "\uD83D\uDED6"
            };
            int randomIndex = (int) (Math.random() * obstacleEmojis.length);
            return obstacleEmojis[randomIndex];
        }

        public void actionPerformed(ActionEvent e) {
            if (!gameFinished) {
                repaint();
            }
        }
    }
}
