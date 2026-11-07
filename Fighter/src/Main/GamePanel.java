package Main;

import structs.Characters;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

public class GamePanel extends JPanel implements Runnable{

    //Screen resolution
    public final int screenWidth = 1920;
    public final int screenHeight = 1080;

    //FPS
    int FPS = 60;

    //Game stage
    enum stage{
        MENU,
        CHAR,
        FIGHT,
        PAUSE,
        HISTORY,
        UNDO,
        RESULT
    }
    stage gameState = stage.MENU;


    KeyHandler keyH = new KeyHandler(this);
    Thread gameThread;

    UI ui = new UI(this,keyH);
    public LinkedList<Characters> chars;
    int charCount;


    FightManager fightManager = new FightManager(this);
    FightHistoryManager fightHistoryManager = new FightHistoryManager(this);

    public GamePanel(LinkedList<Characters> chars) {

        //Main screen setup
        this.setPreferredSize(new Dimension(screenWidth,screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);

        //Character setup
        this.chars = chars;
        charCount = chars.size() - 1;
        for (Characters Char : chars) {
            Char.getCharImage();
        }



    }

    public void startGameThread() {

        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {

        double drawInterval = 1000000000 /FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        int drawCount = 0;

        while (gameThread != null){

            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;

            if (delta >= 1){
                    update();
                    repaint();
                    delta--;
                    drawCount++;
            }

            if (timer >= 1000000000) {
                System.out.println("FPS: " + drawCount);
                drawCount = 0;
                timer = 0;
            }



        }

    }

    public void update(){
        if (gameState == stage.FIGHT){
            fightManager.updateComboNotification();
        }

    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g;
        if (gameState == stage.MENU){
            ui.draw(g2);
        } else if (gameState == stage.HISTORY) {
            fightHistoryManager.draw(g2);
        } else if (gameState == stage.CHAR) {
            ui.draw(g2);
        } else if (gameState == stage.FIGHT){
            fightManager.drawOponents(g2);
            ui.draw(g2);
        } else if (gameState == stage.PAUSE) {
            fightManager.drawOponents(g2);
            ui.draw(g2);
        }else if (gameState == stage.UNDO){
            fightManager.drawOponents(g2);
            ui.draw(g2);
        } else if (gameState == stage.RESULT) {
            ui.draw(g2);
        }

        g2.dispose();

    }
}
