import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class SpaceInvaders extends JPanel implements ActionListener, KeyListener {
    int tileSize = 32;
    int rows = 16;
    int columns = 16;
    int boardWidth = tileSize * columns;
    int boardHeight = tileSize * rows;
    
    Image shipImg;
    Image alienImg;
    Image alienCyanImg;
    Image alienMagentaImg;
    Image alienYellowImg;
    ArrayList<Image> alienImgArray;

    class Block {
        int x;
        int y;
        int width;
        int height;
        Image img;
        boolean alive = true;
        boolean used = false;
        int velocityX = 0;
        int velocityY = 0;

        
        Block(int x, int y, int width, int height, Image img) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.img = img;
        }
    }

    int shipWidth = tileSize*2;
    int shipHeight = tileSize;
    int shipX = tileSize * columns/2 - tileSize;
    int shipY = tileSize * rows - tileSize*2;
    int shipVelocityX = tileSize / 4;
    int shipVelocityY = tileSize / 4;
    Block ship;

    boolean moveLeft = false;
    boolean moveRight = false;
    boolean moveUp = false;
    boolean moveDown = false;

    ArrayList<Block> alienArray;
    int alienWidth = tileSize*2;
    int alienHeight = tileSize;
    int alienX = tileSize;
    int alienY = tileSize;
    int alienRows = 2;
    int alienColumns = 3;
    int alienCount = 0;
    int alienVelocityX = 1;

    ArrayList<Block> bulletArray;
    ArrayList<Block> alienBulletArray;
    int bulletWidth = tileSize/8;
    int bulletHeight = tileSize/2;
    int bulletVelocityY = -10;
    int alienBulletVelocityY = 10;

    Timer gameLoop;
    boolean gameOver = false;
    int score = 0;
    int maxBullets = 15;
    int bulletsShot = 0;
    boolean reloading = false;
    int powerShootBar = 0;
    int maxPowerShootBar = 45;

    SpaceInvaders() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.black);
        setFocusable(true);
        addKeyListener(this);

        shipImg = new ImageIcon(getClass().getResource("./ship.png")).getImage();
        alienImg = new ImageIcon(getClass().getResource("./alien.png")).getImage();
        alienCyanImg = new ImageIcon(getClass().getResource("./alien-cyan.png")).getImage();
        alienMagentaImg = new ImageIcon(getClass().getResource("./alien-magenta.png")).getImage();
        alienYellowImg = new ImageIcon(getClass().getResource("./alien-yellow.png")).getImage();

        alienImgArray = new ArrayList<Image>();
        alienImgArray.add(alienImg);
        alienImgArray.add(alienCyanImg);
        alienImgArray.add(alienMagentaImg);
        alienImgArray.add(alienYellowImg);

        ship = new Block(shipX, shipY, shipWidth, shipHeight, shipImg);
        alienArray = new ArrayList<Block>();
        bulletArray = new ArrayList<Block>();
        alienBulletArray = new ArrayList<Block>();

        gameLoop = new Timer(1000/60, this);
        createAliens();
        gameLoop.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(ship.img, ship.x, ship.y, ship.width, ship.height, null);

        for (int i = 0; i < alienArray.size(); i++) {
            Block alien = alienArray.get(i);
            if (alien.alive) {
                g.drawImage(alien.img, alien.x, alien.y, alien.width, alien.height, null);
            }
        }

        g.setColor(Color.white);
        for (int i = 0; i < bulletArray.size(); i++) {
            Block bullet = bulletArray.get(i);
            bullet.y += (bullet.velocityY != 0) ? bullet.velocityY : bulletVelocityY;
            bullet.x += bullet.velocityX;
            if (!bullet.used) {
                g.drawRect(bullet.x, bullet.y, bullet.width, bullet.height);
            }
        }

        g.setColor(Color.red);
        for (int i = 0; i < alienBulletArray.size(); i++) {
            Block bullet = alienBulletArray.get(i);
            if (!bullet.used) {
                g.drawRect(bullet.x, bullet.y, bullet.width, bullet.height);
            }
        }

        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver) {
            g.drawString("Game Over: " + String.valueOf((int) score), 10, 35);
        } else {
            g.drawString(String.valueOf((int) score), 10, 35);
        }

        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("Bullets: " + (maxBullets - bulletsShot), 10, 60);
        if (bulletsShot >= maxBullets) {
            g.drawString("Out of bullets! Press 'R' to reload.", 10, 80);
        }

        int powerBarWidth = 20;
        int powerBarX = boardWidth - powerBarWidth - 10;
        int powerBarHeight = boardHeight - 20;
        int powerBarY = 10;
        
        g.setColor(Color.darkGray);
        g.fillRect(powerBarX, powerBarY, powerBarWidth, powerBarHeight);
        
        g.setColor(Color.green);
        int filledHeight = (int)((double)powerShootBar / maxPowerShootBar * powerBarHeight);
        g.fillRect(powerBarX, powerBarY + powerBarHeight - filledHeight, powerBarWidth, filledHeight);
        
        g.setColor(Color.white);
        g.drawRect(powerBarX, powerBarY, powerBarWidth, powerBarHeight);

        if (powerShootBar >= maxPowerShootBar) {
            g.setColor(Color.yellow);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString("K", powerBarX + 5, powerBarY + powerBarHeight + 25);
        }
    }


    public void move() {
        if (moveLeft && ship.x - shipVelocityX >= 0) {
            ship.x -= shipVelocityX;
        }
        if (moveRight && ship.x + shipVelocityX + ship.width <= boardWidth) {
            ship.x += shipVelocityX;
        }
        if (moveUp && ship.y - shipVelocityY >= boardHeight / 2) {
            ship.y -= shipVelocityY;
        }
        if (moveDown && ship.y + shipVelocityY + ship.height <= boardHeight) {
            ship.y += shipVelocityY;
        }
    
        for (int i = 0; i < alienArray.size(); i++) {
            Block alien = alienArray.get(i);
            if (alien.alive) {
                alien.x += alienVelocityX;
    
                if (alien.x + alien.width >= boardWidth || alien.x <= 0) {
                    alienVelocityX *= -1;
                    alien.x += alienVelocityX*2;
    
                    for (int j = 0; j < alienArray.size(); j++) {
                        alienArray.get(j).y += alienHeight;
                    }
                }
    
                if (alien.y >= ship.y) {
                    gameOver = true;
                }
    
                if (new Random().nextInt(1000) < 5) {
                    alienShoot(alien);
                }
            }
        }
    
        for (int i = 0; i < bulletArray.size(); i++) {
            Block bullet = bulletArray.get(i);
            bullet.y += bulletVelocityY;
    
            for (int j = 0; j < alienArray.size(); j++) {
                Block alien = alienArray.get(j);
                if (!bullet.used && alien.alive && detectCollision(bullet, alien)) {
                    bullet.used = true;
                    alien.alive = false;
                    alienCount--;
                    score += 100;
                    powerShootBar = Math.min(powerShootBar + 1, maxPowerShootBar);
                }
            }
        }
    
        for (int i = 0; i < alienBulletArray.size(); i++) {
            Block bullet = alienBulletArray.get(i);
            bullet.y += alienBulletVelocityY;
    
            if (!bullet.used && detectCollision(bullet, ship)) {
                bullet.used = true;
                gameOver = true;
            }
        }
    
        while (bulletArray.size() > 0 && (bulletArray.get(0).used || bulletArray.get(0).y < 0)) {
            bulletArray.remove(0);
        }
    
        while (alienBulletArray.size() > 0 && (alienBulletArray.get(0).used || alienBulletArray.get(0).y > boardHeight)) {
            alienBulletArray.remove(0);
        }
    
        if (alienCount == 0) {
            score += alienColumns * alienRows * 100;
            alienColumns = Math.min(alienColumns + 1, columns/2 -2);
            alienRows = Math.min(alienRows + 1, rows-6);
            alienArray.clear();
            bulletArray.clear();
            alienBulletArray.clear();
            createAliens();
        }
    }

    public void createAliens() {
        Random random = new Random();
        for (int c = 0; c < alienColumns; c++) {
            for (int r = 0; r < alienRows; r++) {
                int randomImgIndex = random.nextInt(alienImgArray.size());
                Block alien = new Block(
                    alienX + c*alienWidth, 
                    alienY + r*alienHeight, 
                    alienWidth, 
                    alienHeight,
                    alienImgArray.get(randomImgIndex)
                );
                alienArray.add(alien);
            }
        }
        alienCount = alienArray.size();
    }

    public void alienShoot(Block alien) {
        Block bullet = new Block(alien.x + alien.width/2 - bulletWidth/2, alien.y + alien.height, bulletWidth, bulletHeight, null);
        alienBulletArray.add(bullet);
    }

    public boolean detectCollision(Block a, Block b) {
        return  a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            moveLeft = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            moveRight = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            moveUp = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            moveDown = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_SPACE && !reloading) {
            if (bulletsShot < maxBullets) {
                Block bullet = new Block(ship.x + shipWidth * 15 / 32, ship.y, bulletWidth, bulletHeight, null);
                bulletArray.add(bullet);
                bulletsShot++;
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_R) {
            reloading = true;
            bulletsShot = 0;
            reloading = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_K && powerShootBar >= maxPowerShootBar) {
            powerShoot();
            powerShootBar = 0;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (gameOver) {
            ship.x = shipX;
            ship.y = shipY;
            bulletArray.clear();
            alienArray.clear();
            alienBulletArray.clear();
            gameOver = false;
            score = 0;
            alienColumns = 3;
            alienRows = 2;
            alienVelocityX = 1;
            createAliens();
            gameLoop.start();
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            moveLeft = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            moveRight = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            moveUp = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            moveDown = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    public void powerShoot() {
        int numBullets = 15;
        double angleStep = 2 * Math.PI / numBullets;
        
        for (int i = 0; i < numBullets; i++) {
            double angle = i * angleStep;
            Block bullet = new Block(
                ship.x + shipWidth/2 - bulletWidth/2,
                ship.y + shipHeight/2 - bulletHeight/2, 
                bulletWidth, 
                bulletHeight, 
                null
            );
            bulletArray.add(bullet);
            bullet.velocityX = (int)(Math.cos(angle) * 10);
            bullet.velocityY = (int)(Math.sin(angle) * 10);
        }
    }
}