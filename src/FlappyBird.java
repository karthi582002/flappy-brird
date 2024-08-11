import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {

    //        panel width and height
    int boardWidth = 360;
    int boardHeight = 640;
    //        images
    ImageIcon backgroundImg;
    ImageIcon birdImg;
    ImageIcon topPipeImg;
    ImageIcon bottomPipeImg;

//    bird initial position
    int birdX = boardWidth/8;
    int birdY = boardHeight/2 - 50;
    int birdWidth = 34;
    int birdHeight = 24;

    class Bird{
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        ImageIcon img;
        Bird(ImageIcon img){
            this.img = img;
        }
    }


//    pipes
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64; // actual dimension  is *6 times bigger (384)
    int pipeHeight = 512; //

    class Pipe{
        int x= pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        ImageIcon img;
        boolean passed = false; // for passing the pipe of flappy bird
        Pipe(ImageIcon img){
            this.img = img;
        }
    }



//    game logic
    Bird bird;
    Timer gameLoop;
    Timer placePipesTimer;
    int velecityX = -4; //moves pipe to left speed (simulates bird moving right)
    int velocityY = 0; // bird movement
    int gravity = 1; // we need to add gravity for the bird

    ArrayList<Pipe> pipes; //Since we haveing many pipes we gona to make in array
    Random random = new Random();

    boolean gameOver = false;
    boolean start = false;
    double score = 0;
    SoundEffects bgTrack;
    SoundEffects collision;




    FlappyBird(){
    setPreferredSize(new Dimension(boardWidth,boardHeight));
    setBackground(Color.blue);
//    setFocusable(true); // this will make sure to work fine
        addKeyListener(this); // will accept the keylisiner for the panel
    // init-ing the variables
        backgroundImg = new ImageIcon("assets/flappybirdbg.png");
        birdImg = new ImageIcon("assets/flappybird.png");
        topPipeImg = new ImageIcon("assets/toppipe.png");
        bottomPipeImg = new ImageIcon("assets/bottompipe.png");
        //bird
        bird = new Bird(birdImg);
        pipes = new ArrayList<Pipe>();
        gameLoop = new Timer(1000/60,this); //60 Sec per frame 1000/60 = 16.6
        //sounds
        bgTrack = new SoundEffects("bg.wav");
        collision = new SoundEffects("collision.wav");

        placePipesTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        }); //every 1.5sec
        placePipesTimer.start();
        if(start) {
            gameLoop.start();
        }
        bgTrack.playSound();

    }


    public void placePipes(){
        // (0-1) * pipeHeight/2 -> (0-256)
        //128
        // 0- 128 - (0-256) --> pipeHeight/4 -> 3/4 pipe height

        int randomPipeY = (int) (pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2));
        int openingSpace = boardHeight/4;

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g); //refers a parent class
        draw(g);

    }


    public void draw(Graphics g){
        //background
        g.drawImage(backgroundImg.getImage(),0,0,boardWidth,boardHeight,this);
        g.drawImage(birdImg.getImage(),bird.x,bird.y,bird.width,bird.height,this);
//        draw pipes
        for (int i=0; i< pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img.getImage(),pipe.x,pipe.y,pipe.width,pipe.height,this);
        }

        // score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Verdana",Font.BOLD,32));
        if(gameOver){
            bgTrack.stopSound();
            collision.playSound();
            g.drawString("Game Over: "+String.valueOf((int) score),50,boardHeight/2);
            g.setFont(new Font("Verdana",Font.BOLD,16));
            g.drawString("Press Space To Restart the Game",30,(boardHeight/2)+20);

        } else if (!start) {
            g.setFont(new Font("Verdana",Font.BOLD,25));
            g.drawString("Press any key to Start",25,boardHeight/2);
        } else {

            g.drawString(String.valueOf((int)score),10,35);
        }
    }

    public void move(){
        //bird movement
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y,0);
//        move the pipes
        for(int i =0 ; i<pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            pipe.x += velecityX;
            if(collision(bird,pipe)){
                gameOver = true;
            }
            if(!pipe.passed && bird.x > pipe.x + pipe.width){
                pipe.passed = true;
                score += 0.5;
            }
        }
        if(bird.y > boardHeight){
            gameOver = true;
        }

    }
    public boolean collision(Bird a, Pipe b){
        return a.x < b.x + b.width && // a's top left corner doesn't reach b's top right corner
                a.x + a.width >b.x && // a's top right corner passes b's top left corner
                a.y < b.y + b.height && //  a's top left corner doesn't reach b's top left corner
                a.y + a.height > b.y; // a's bottom left corner doesn't reach b's top left corner
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint(); // will repaint for every 60sec
        if(gameOver){
            placePipesTimer.stop();
            gameLoop.stop();
        }
    }


    @Override
    public void keyPressed(KeyEvent e) {
        if (!start) {
            start = true;
            gameLoop.start();
            placePipesTimer.start();
            return; // Exit early to avoid unintended behavior
        }
        if(e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_UP){
            velocityY = -6;
            if(gameOver){
                // Starting the game again
                bird.y = birdY;
                velocityY = 0;
                pipes.clear();
                score = 0;
                gameOver = false;
                gameLoop.start();
                placePipesTimer.start();
                bgTrack.playSound();
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_P) {
            if (start) {
                start = false;
                gameLoop.stop();
                placePipesTimer.stop();
            } else {
                start = true;
                gameLoop.start();
                placePipesTimer.start();
                bgTrack.playSound();  // Resume background music when unpausing
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
    @Override
    public void keyTyped(KeyEvent e) {

    }

}
