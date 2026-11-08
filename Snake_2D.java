package projects;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
public class Snake_2D {
   
    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        frame.setUndecorated(true);
        frame.setLayout(new GridBagLayout());
        
        frame.getContentPane().setBackground(Color.BLACK);
        
        Board board = new Board();
        frame.add(board);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.setVisible(true);
        
        GraphicsDevice device = 
GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        device.setFullScreenWindow(frame);
        
        board.requestFocusInWindow();
    }
}

class Board extends JPanel implements ActionListener, KeyListener {
   
    static final int WIDTH = 1536;          
    static final int HEIGHT = 768;          
    static final int UNIT = 24;             
    static final int MAX_CELLS = (WIDTH * HEIGHT) / (UNIT * UNIT);   
    static final int DELAY = 200;           
    static final int WIN = 5;               
    
    int[] x = new int[MAX_CELLS];
    int[] y = new int[MAX_CELLS];
    int bodyparts = 6;
    int applesEaten = 0;
    
    int appleX;
    int appleY;
    char direction = 'R';                   
    char nextDirection = 'R';
    boolean running = false;
    boolean paused = false;
    boolean won = false;
    
    Timer timer;
    Random random = new Random();
    
    public Board() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));  
        setBackground(Color.BLACK);                     
        setFocusable(true);                             
        addKeyListener(this);                          
        startGame();                                     
    }
  
    public void startGame() {
        for (int i = 0; i < bodyparts; i++) {
            x[i] = (5 - i) * UNIT;
            y[i] = 12 * UNIT;
        }
        newApple();                    
        running = true;              
        if (timer != null) {
            timer.stop();
        }
        timer = new Timer(DELAY, this);
        timer.start();
    }
   
    public void restartGame() {
        bodyparts = 6;                
        applesEaten = 0;               
        direction = 'R';               
        nextDirection = 'R';
        paused = false;
        won = false;
        startGame();
    }
    
    public void newApple() {
        boolean onSnake;
        do {
           
            appleX = random.nextInt(WIDTH / UNIT) * UNIT;
            appleY = random.nextInt(HEIGHT / UNIT) * UNIT;
           
            onSnake = false;
            for (int i = 0; i < bodyparts; i++) {
                if (x[i] == appleX && y[i] == appleY) {
                    onSnake = true;
                }
            }
        } while (onSnake);            
    }
    
    public void move() {
        direction = nextDirection;    
        for (int i = bodyparts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        switch (direction) {
            case 'U': y[0] -= UNIT; break;     
            case 'D': y[0] += UNIT; break;     
            case 'L': x[0] -= UNIT; break;    
            case 'R': x[0] += UNIT; break;     
        }
    }
    
    public void checkApple() {
        if (x[0] == appleX && y[0] == appleY) {
            bodyparts++;
            applesEaten++;
            if (applesEaten == WIN) {
                won = true;               
            }
            newApple();
        }
    }
   
    public void checkCollisions() {
        for (int i = bodyparts - 1; i > 0; i--) {
            if (x[0] == x[i] && y[0] == y[i]) {
                running = false;
            }
        }
        if (x[0] < 0 || x[0] >= WIDTH || y[0] < 0 || y[0] >= HEIGHT) {
            running = false;
        }
        if (!running) {
            timer.stop();
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (running && !paused) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();                     
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                if (direction != 'R') nextDirection = 'L';     
                break;
            case KeyEvent.VK_RIGHT:
                if (direction != 'L') nextDirection = 'R';
                break;
            case KeyEvent.VK_UP:
                if (direction != 'D') nextDirection = 'U';
                break;
            case KeyEvent.VK_DOWN:
                if (direction != 'U') nextDirection = 'D';
                break;
            case KeyEvent.VK_P:
                if (running) {
                    paused = !paused;          
                }
                break;
            case KeyEvent.VK_R:
                if (!running) {
                    restartGame();             
                }
                break;
            case KeyEvent.VK_ESCAPE:
                System.exit(0);               
                break;
        }
    }
    @Override
    public void keyReleased(KeyEvent e) { }
    @Override
    public void keyTyped(KeyEvent e) { }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }
    
    public void draw(Graphics g) {
        if (running) {
            drawApple(g);
            drawSnake(g);
            drawScore(g);
            if (won) {
                drawStudyMessage(g);  
            }
            if (paused) {
                drawPaused(g);
            }
        } else {
            gameOver(g);
        }
    }
    
    public void drawApple(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval(appleX, appleY, UNIT, UNIT);
    }
    
    public void drawSnake(Graphics g) {
        for (int i = 0; i < bodyparts; i++) {
            if (i == 0) {
                g.setColor(new Color(0, 255, 0));     
            } else {
                g.setColor(new Color(0, 180, 0));     
            }
            g.fillRect(x[i], y[i], UNIT, UNIT);
        }
    }
   
    public void drawScore(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Score: " + applesEaten, 10, 20);
    }
   
    public void drawStudyMessage(Graphics g) {
        drawCentered(g, "Go and study!", 60, new Font("Arial", Font.BOLD, 40), new Color(255, 215, 0));
    }
   
    public void drawPaused(Graphics g) {
        drawCentered(g, "PAUSED", HEIGHT / 2 - 20, new Font("Arial", Font.BOLD, 50), Color.YELLOW);
        drawCentered(g, "Press P to continue", HEIGHT / 2 + 30, new Font("Arial", Font.PLAIN, 24), 
Color.WHITE);
    }
   
    public void gameOver(Graphics g) {
drawCentered(g, "Game Over", HEIGHT / 2 - 40, new Font("Arial", Font.BOLD, 70), Color.RED);
drawCentered(g, "Score: " + applesEaten, HEIGHT / 2 + 10, new Font("Arial", Font.BOLD, 30), 
Color.WHITE);
drawCentered(g, "Press R to play again", HEIGHT / 2 + 60, new Font("Arial", Font.PLAIN, 24), 
Color.WHITE);
}

public void drawCentered(Graphics g, String text, int y, Font font, Color color) {
g.setFont(font);
g.setColor(color);
int textWidth = g.getFontMetrics().stringWidth(text);
g.drawString(text, (WIDTH - textWidth) / 2, y);
}

}
