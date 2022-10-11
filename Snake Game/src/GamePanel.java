import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.util.*;


public class GamePanel extends JPanel  /*implements ActionListener */{

    static final int SCREEN_WIDTH = 1300;
    static final int SCREEN_HEIGHT = 750;
    static final int UNIT_SIZE = 50;        // veličina jedne kockice, jednog pomeraja
    static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/(UNIT_SIZE*UNIT_SIZE);// koliko stane kockica na ekran
    static final int DELAY = 175;
    final int x[] = new int[GAME_UNITS];    //koordinate zmije
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 6;  //iz koliko se delova zmija sastoji
    int applesEaten;    // koliko je pojela kuglica
    int appleX; //koordinate kuglice
    int appleY;
    char direction = 'R';   // smer kretanja zmije
    boolean running = false;
   // Timer timer; // pokreće neki događaj u odgovarajućim intervalima(umesto niti)
    GameLoop loop;  // NIT
    Thread gameThread; // NIT
    Random random;

    GamePanel(){
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }
    public void startGame(){
        newApple(); // označava gde da bude kružić koji se jede
        running = true;
        //timer = new Timer(DELAY,this);
        //timer.start();
        loop = new GameLoop();
        gameThread = new Thread(loop);
        gameThread.start();

    }
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }
    public  void draw(Graphics g){
        if (running){
            for(int i=0;i<SCREEN_HEIGHT/UNIT_SIZE;i++) {        // crta mrežu kockica na ekranu
                g.drawLine(i*UNIT_SIZE, 0, i*UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i*UNIT_SIZE, SCREEN_WIDTH, i*UNIT_SIZE);
            }
            g.setColor(Color.red);
            g.fillOval(appleX,appleY,UNIT_SIZE,UNIT_SIZE);

            for(int i=0;i<bodyParts;i++){   // crtanje tela zmije
                if (i==0){  // glava zmije
                    g.setColor(Color.green);
                    g.fillRect(x[i],y[i],UNIT_SIZE,UNIT_SIZE);
                }else {
                    //g.setColor(new Color(45,180,0));
                    g.setColor(new Color(random.nextInt(255),random.nextInt(255),random.nextInt(255)));
                    g.fillRect(x[i],y[i],UNIT_SIZE,UNIT_SIZE);
                }
            }
            g.setColor(Color.red);
            g.setFont(new Font("Ink Free",Font.BOLD,40));
            FontMetrics metrics = getFontMetrics(g.getFont());  // određuje širinu datog Fonta odnosno stringa
            g.drawString("Score: " + applesEaten,(SCREEN_WIDTH-metrics.stringWidth("Score: "+applesEaten))/2,g.getFont().getSize());
        }else {
           gameOver(g);
        }
    }
    public void newApple(){ // označava gde da bude kružić koji se jede
        appleX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE))*UNIT_SIZE;
        appleY = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE))*UNIT_SIZE;
    }
    public void move(){
        for (int i=bodyParts;i>0;i--){  // pomera telo zmije x i y kad se zmija kreće
            x[i] = x[i-1];
            y[i] = y[i-1];
        }
        switch(direction){
            case 'U' -> {y[0]=y[0]-UNIT_SIZE; break;}
            case 'D' -> {y[0]=y[0]+UNIT_SIZE; break;}
            case 'L' -> {x[0]=x[0]-UNIT_SIZE; break;}
            case 'R' -> {x[0]=x[0]+UNIT_SIZE; break;}
        }
    }
    public void checkApple(){
        if ((x[0]==appleX)&&(y[0]==appleY)){
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }
    public void checkCollisions(){
        //provera ako glava udari u telo
        for(int i = bodyParts;i>0;i--) {
            if((x[0] == x[i])&& (y[0] == y[i])) {
                running = false;
            }
        }
        // provera ako glava dodirne levu ivicu
        if (x[0]<0) running = false;
        // provera ako glava dodirne desnu ivicu
        if (x[0]>SCREEN_WIDTH) running = false;
        // provera ako glava dodirne gornju ivicu
        if (y[0]<0) running = false;
        // provera ako glava dodirne donju ivicu
        if (y[0]>SCREEN_HEIGHT) running = false;
        //if (!running)
            //timer.stop();

    }
    public void gameOver(Graphics g){
        //Score - rezultat
        g.setColor(Color.red);
        g.setFont((new Font("Ink Free",Font.BOLD,40)));
        FontMetrics metrics1 = getFontMetrics(g.getFont());  // određuje širinu datog Fonta odnosno stringa
        g.drawString("Score: " + applesEaten,(SCREEN_WIDTH-metrics1.stringWidth("Score: "+applesEaten))/2,g.getFont().getSize());
        //Game over text
        g.setColor(Color.red);
        g.setFont((new Font("Ink Free",Font.BOLD,75)));
        FontMetrics metrics2 = getFontMetrics(g.getFont());  // određuje širinu datog Fonta odnosno stringa
        g.drawString("Game Over",(SCREEN_WIDTH-metrics2.stringWidth("Game Over"))/2,SCREEN_HEIGHT/2);
    }
   /* @Override
    public void actionPerformed(ActionEvent e){
        if (running){
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    } */
    public class GameLoop implements Runnable{
        public GameLoop(){}
       @Override
       public void run(){
           //game loop
           long lastTime = System.nanoTime();
           double amountOfTicks = 5.0;
           double ns = 1000000000/amountOfTicks;
           double delta = 0;
           while(gameThread != null){
               long now = System.nanoTime();
               delta += (now-lastTime)/ns;
               lastTime = now;
               if(delta>=1){
                   if (running){
                       move();
                       checkApple();
                       checkCollisions();
                   }
                   repaint();
                   delta--;
                  // System.out.println("Ada i Miki");
               }
           }
       }
   }
    public class MyKeyAdapter extends KeyAdapter{   // klasa koja odrađuje pritisak na tastere i kretanje Snake,
        @Override                                   // u slučaju da ide Desno ne može odmah Levo
        public void keyPressed(KeyEvent e){
            switch(e.getKeyCode()){
                case KeyEvent.VK_LEFT -> {if (direction != 'R') direction = 'L'; break;}
                case KeyEvent.VK_RIGHT -> {if (direction != 'L') direction = 'R'; break;}
                case KeyEvent.VK_UP -> {if (direction != 'D') direction = 'U'; break;}
                case KeyEvent.VK_DOWN -> {if (direction != 'U') direction = 'D'; break;}
            }
        }
    }
}
