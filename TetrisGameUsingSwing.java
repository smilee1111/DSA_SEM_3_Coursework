package DSA_SEM_3_Coursework;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

public class TetrisGameUsingSwing extends JPanel implements ActionListener, KeyListener {
    private static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 20;
    private static final int CELL_SIZE = 30;
    private static final int PREVIEW_SIZE = 4;
    private static final int DELAY = 500;

    private Timer timer;
    private boolean[][] gameBoard;
    private Queue<Block> blockQueue;
    private Stack<int[]> boardStack;
    private Block currentBlock;
    private Block nextBlock;
    private Random random;
    private int score;

    public TetrisGameUsingSwing() {
        setPreferredSize(new Dimension(BOARD_WIDTH * CELL_SIZE, BOARD_HEIGHT * CELL_SIZE));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        gameBoard = new boolean[BOARD_HEIGHT][BOARD_WIDTH];
        blockQueue = new LinkedList<>();
        boardStack = new Stack<>();
        random = new Random();
        score = 0;

        initializeGame();
        timer = new Timer(DELAY, this);
        timer.start();
    }

    private void initializeGame() {
        for (int i = 0; i < 3; i++) {
            blockQueue.add(generateRandomBlock());
        }
        spawnNextBlock();
    }

    private Block generateRandomBlock() {
        int[][] shape = Block.SHAPES[random.nextInt(Block.SHAPES.length)];
        return new Block(shape, BOARD_WIDTH / 2 - shape[0].length / 2, 0);
    }

    private void spawnNextBlock() {
        currentBlock = blockQueue.poll();
        nextBlock = generateRandomBlock();
        blockQueue.add(nextBlock);
    }

    private boolean isValidMove(Block block, int newX, int newY) {
        for (int row = 0; row < block.getHeight(); row++) {
            for (int col = 0; col < block.getWidth(); col++) {
                if (block.getShape()[row][col] != 0) {
                    int x = newX + col;
                    int y = newY + row;
                    if (x < 0 || x >= BOARD_WIDTH || y >= BOARD_HEIGHT || (y >= 0 && gameBoard[y][x])) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void placeBlock() {
        for (int row = 0; row < currentBlock.getHeight(); row++) {
            for (int col = 0; col < currentBlock.getWidth(); col++) {
                if (currentBlock.getShape()[row][col] != 0) {
                    int x = currentBlock.getX() + col;
                    int y = currentBlock.getY() + row;
                    if (y >= 0) {
                        gameBoard[y][x] = true;
                    }
                }
            }
        }
        checkCompletedRows();
        spawnNextBlock();
    }

    private void checkCompletedRows() {
        ArrayList<Integer> completedRows = new ArrayList<>();
        for (int row = 0; row < BOARD_HEIGHT; row++) {
            boolean isComplete = true;
            for (int col = 0; col < BOARD_WIDTH; col++) {
                if (!gameBoard[row][col]) {
                    isComplete = false;
                    break;
                }
            }
            if (isComplete) {
                completedRows.add(row);
            }
        }

        for (int row : completedRows) {
            for (int r = row; r > 0; r--) {
                System.arraycopy(gameBoard[r - 1], 0, gameBoard[r], 0, BOARD_WIDTH);
            }
            gameBoard[0] = new boolean[BOARD_WIDTH];
            score += 100;
        }
    }

    private void moveDown() {
        if (isValidMove(currentBlock, currentBlock.getX(), currentBlock.getY() + 1)) {
            currentBlock.moveDown();
        } else {
            placeBlock();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        moveDown();
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGameBoard(g);
        drawCurrentBlock(g);
        drawNextBlock(g);
        drawScore(g);
    }

    private void drawGameBoard(Graphics g) {
        for (int row = 0; row < BOARD_HEIGHT; row++) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                if (gameBoard[row][col]) {
                    g.setColor(Color.GREEN);
                    g.fillRect(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }
        }
    }

    private void drawCurrentBlock(Graphics g) {
        drawBlock(g, currentBlock, currentBlock.getX(), currentBlock.getY());
    }

    private void drawNextBlock(Graphics g) {
        g.setColor(Color.black);
        g.drawString("Next Block:", BOARD_WIDTH * CELL_SIZE + 20, 20);
        drawBlock(g, nextBlock, BOARD_WIDTH + 2, 2);
    }

    private void drawBlock(Graphics g, Block block, int x, int y) {
        g.setColor(Color.yellow);
        for (int row = 0; row < block.getHeight(); row++) {
            for (int col = 0; col < block.getWidth(); col++) {
                if (block.getShape()[row][col] != 0) {
                    g.fillRect((x + col) * CELL_SIZE, (y + row) * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }
        }
    }

    private void drawScore(Graphics g) {
        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, BOARD_WIDTH * CELL_SIZE + 20, 60);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_LEFT:
                if (isValidMove(currentBlock, currentBlock.getX() - 1, currentBlock.getY())) {
                    currentBlock.moveLeft();
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (isValidMove(currentBlock, currentBlock.getX() + 1, currentBlock.getY())) {
                    currentBlock.moveRight();
                }
                break;
            case KeyEvent.VK_DOWN:
                moveDown();
                break;
            case KeyEvent.VK_UP:
                Block rotated = currentBlock.rotate();
                if (isValidMove(rotated, rotated.getX(), rotated.getY())) {
                    currentBlock = rotated;
                }
                break;
        }
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Tetris Game");
        TetrisGameUsingSwing game = new TetrisGameUsingSwing();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

class Block {
    public static final int[][][] SHAPES = {
        {{1, 1, 1, 1}}, // I-shape
        {{1, 1}, {1, 1}}, // O-shape
        {{0, 1, 0}, {1, 1, 1}}, // T-shape
        {{1, 0, 0}, {1, 1, 1}}, // L-shape
        {{0, 0, 1}, {1, 1, 1}}, // J-shape
        {{0, 1, 1}, {1, 1, 0}}, // S-shape
        {{1, 1, 0}, {0, 1, 1}}  // Z-shape
    };

    private int[][] shape;
    private int x, y;

    public Block(int[][] shape, int x, int y) {
        this.shape = shape;
        this.x = x;
        this.y = y;
    }

    public int[][] getShape() {
        return shape;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return shape[0].length;
    }

    public int getHeight() {
        return shape.length;
    }

    public void moveDown() {
        y++;
    }

    public void moveLeft() {
        x--;
    }

    public void moveRight() {
        x++;
    }

    public Block rotate() {
        int[][] rotated = new int[shape[0].length][shape.length];
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[0].length; col++) {
                rotated[col][shape.length - 1 - row] = shape[row][col];
            }
        }
        return new Block(rotated, x, y);
    }
}