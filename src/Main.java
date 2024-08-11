import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
//        frame height and width
        int boardWidth = 360;
        int boardHeight = 640;


//        setting an icon for game
        ImageIcon icon = new ImageIcon("assets/flappybird.png");

//        creating a main frame
        JFrame frame = new JFrame("Flappy Bird");
        frame.setSize(boardWidth,boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(icon.getImage());
        FlappyBird flappyBird = new FlappyBird();
        frame.add(flappyBird);
        frame.pack(); // if this was not added means the height and width of title bar will also be taken
        flappyBird.requestFocus(); // will check for the event
        frame.setVisible(true);


    }
}
