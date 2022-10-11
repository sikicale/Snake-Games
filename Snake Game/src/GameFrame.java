import javax.swing.*;
import java.awt.event.*;

public class GameFrame extends JFrame {
    GameFrame(){
        this.add(new GamePanel());
        this.setTitle("Snake Game");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.pack();     //umesto setSize() ili setBounds()
        this.setVisible(true);
        this.setLocationRelativeTo(null);//centrira Frame na ekranu
    }
}
