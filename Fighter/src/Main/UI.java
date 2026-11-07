package Main;

import structs.Characters;
import structs.Combo;
import utils.ImageLoader;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.AttributedString;

public class UI {

    GamePanel gp;
    KeyHandler keyH;
    Graphics2D g2;
    Font arial_40 = new Font("Arial",Font.PLAIN,40);
    int radius = 960;


    //UI Images
    BufferedImage shootButton;
    BufferedImage blockButton;
    BufferedImage slashButton;
    BufferedImage selector;
    BufferedImage actionQueue;
    BufferedImage emptyImg;







    public int commandNum = 0;
    public int pauseNum = 0;
    public int charNum = 0;
    public int undoNum = 0;
    public FightManager.Action selectedAction = null;
    //IMAGE CONFIG
    public static int baseSize = 32;
    public static int scale = 4;
    public static int realSize = baseSize * scale;


    public UI(GamePanel gp, KeyHandler keyH){
        this.gp = gp;
        this.keyH = keyH;

        //Load UI images
        shootButton = ImageLoader.loadImage("shoot_button.png");
        blockButton = ImageLoader.loadImage("block_button.png");
        slashButton = ImageLoader.loadImage("slash_button.png");
        selector = ImageLoader.loadImage("selector.png");
        actionQueue = ImageLoader.loadImage("action_queue.png");
        emptyImg = ImageLoader.loadImage("empty.png");




    }

    public void draw(Graphics2D g2){
        this.g2 = g2;

        g2.setFont(arial_40);
        g2.setColor(Color.WHITE);

        if (gp.gameState == GamePanel.stage.MENU){
            drawTitleScreen();
        } else if (gp.gameState == GamePanel.stage.CHAR) {
            drawCharSelScreen();
        } else if (gp.gameState == GamePanel.stage.FIGHT) {
            drawFightScreen();
        } else if (gp.gameState == GamePanel.stage.PAUSE) {
            drawPauseScreen();
        } else if (gp.gameState == GamePanel.stage.UNDO) {
            drawUndoScreen();
        } else if (gp.gameState == GamePanel.stage.RESULT) {
            drawResultScreen();
        }
    }

    private void drawResultScreen() {
       if (gp.fightManager.winner.equals("PLAYER")){
           BufferedImage image = gp.fightManager.player.sprite;
           int imageX = (gp.getWidth() - image.getWidth()*scale * 5)/2;
           int imageY = (gp.getHeight() - image.getHeight()*scale * 5)/2;

           g2.drawImage(image,imageX,imageY,image.getWidth()*scale * 5, image.getHeight()*scale * 5, null);

           g2.setColor(Color.YELLOW);
           String text = "PLAYER WINS!!!";
           g2.drawString(text,getXforCenteredText(text),gp.getHeight()/4);
       }else {
           BufferedImage image = gp.fightManager.enemy.sprite;
           int imageX = (gp.getWidth() - image.getWidth()*scale * 5)/2;
           int imageY = (gp.getHeight() - image.getHeight()*scale * 5)/2;

           g2.drawImage(image,imageX,imageY,image.getWidth()*scale * 5, image.getHeight()*scale * 5, null);

           g2.setColor(Color.YELLOW);
           String text = "ENEMY WINS!!!";
           g2.drawString(text,getXforCenteredText(text),gp.getHeight()/4);
       }

    }

    private void drawFightScreen() {
        g2.setColor(Color.GRAY);
        int uiMinY = gp.screenHeight*9/10;
        g2.fillRect(0, uiMinY, gp.screenWidth, gp.screenHeight);

        //Buttons
        int offset = 50;
        int buttonY = uiMinY - realSize/2;
        int centerX = (gp.screenWidth - realSize)/2;
        g2.drawImage(shootButton,centerX - offset - realSize,buttonY,realSize,realSize,null);
        if (selectedAction == FightManager.Action.SHOOT){
            g2.drawImage(selector,centerX - offset - realSize,buttonY,realSize,realSize,null);
        }
        g2.drawImage(blockButton,centerX,buttonY,realSize,realSize,null);
        if (selectedAction == FightManager.Action.BLOCK){
            g2.drawImage(selector,centerX,buttonY,realSize,realSize,null);
        }
        g2.drawImage(slashButton,centerX + realSize + offset,buttonY,realSize,realSize,null);
        if (selectedAction == FightManager.Action.SLASH){
            g2.drawImage(selector,centerX + realSize + offset,buttonY,realSize,realSize,null);
        }
        //Action Queue
        int boundOffset = 20;
        int imageX = boundOffset;
        int imageY = boundOffset + realSize - 30;
        g2.drawImage(actionQueue,imageX, imageY,actionQueue.getWidth()*scale,actionQueue.getHeight()*scale,null);

        int slotY = imageY + 10*scale;
        int lastSlotX = imageX +  3*scale;
        int midSlotX = lastSlotX + 34*scale;
        int firstSlotX = midSlotX + 34*scale;

        FightManager.Action[] queue = gp.fightManager.getPlayerComboQueue();
        g2.drawImage(enumToImage(queue[2]),firstSlotX,slotY,realSize,realSize,null);
        g2.drawImage(enumToImage(queue[1]),midSlotX,slotY,realSize,realSize,null);
        g2.drawImage(enumToImage(queue[0]),lastSlotX,slotY,realSize,realSize,null);


        int enemyImageX = gp.screenWidth - boundOffset - actionQueue.getWidth() * scale;
        int enemyImageY = imageY;

        g2.drawImage(actionQueue, enemyImageX, enemyImageY,
                actionQueue.getWidth() * scale,
                actionQueue.getHeight() * scale, null);

        // Slots
        int enemyLastSlotX = enemyImageX + 3 * scale;
        int enemyMidSlotX = enemyLastSlotX + 34 * scale;
        int enemyFirstSlotX = enemyMidSlotX + 34 * scale;

        FightManager.Action[] enemyQueue = gp.fightManager.getEnemyComboQueue();

        g2.drawImage(enumToImage(enemyQueue[2]), enemyFirstSlotX, slotY, realSize, realSize, null);
        g2.drawImage(enumToImage(enemyQueue[1]), enemyMidSlotX, slotY, realSize, realSize, null);
        g2.drawImage(enumToImage(enemyQueue[0]), enemyLastSlotX, slotY, realSize, realSize, null);



        String text  = String.valueOf(gp.fightManager.roundNum);
        int diameter = 50;
        int counterX = (gp.getWidth()/2) - diameter/2;
        int counterY = (boundOffset*3);
        g2.setColor(Color.WHITE);
        g2.fillOval(counterX,counterY,diameter,diameter);
        g2.setColor(Color.RED);
        FontMetrics fm = g2.getFontMetrics();

        int textX = counterX + (diameter - fm.stringWidth(text)) / 2;
        int textY = counterY + (diameter - fm.getHeight()) / 2 + fm.getAscent();

        g2.drawString(text, textX, textY);
        drawComboNotification();












    }

    private void drawComboNotification(){

        FightManager fm = gp.fightManager;

        if (fm.comboNotificationTimer <= 0 || fm.triggeredComboName == null){
            return;
        }

        // Dimensions
        int width = 400;
        int height = 110;

        int x = (gp.screenWidth - width) / 2;
        int y = 220;

        Color comboColor;

        if (fm.triggeredComboOwner.equals("PLAYER")){
            comboColor = new Color(40, 200, 80);
        }else {
            comboColor = new Color(220, 50, 50);
        }

        // Background
        g2.setColor(new Color(0, 0, 0, 210));
        g2.fillRoundRect(x, y, width, height, 20, 20);

        // Border
        g2.setColor(comboColor);
        g2.setStroke(new BasicStroke(4));
        g2.drawRoundRect(x, y, width, height, 20, 20);

        g2.setFont(new Font("Arial", Font.BOLD, 22));
        g2.setColor(comboColor);

        String ownerText = fm.triggeredComboOwner + " COMBO!";
        int ownerX = getXforCenteredText(ownerText);

        g2.drawString(ownerText, ownerX, y + 35);

        g2.setFont(new Font("Arial", Font.BOLD, 30));
        g2.setColor(Color.WHITE);

        String comboName = fm.triggeredComboName;
        int nameX = getXforCenteredText(comboName);

        g2.drawString(comboName, nameX, y + 78);
    }

    private void drawCharSelScreen() {


        String caption = "Choose your hero!";

        BufferedImage image = gp.chars.get(charNum).sprite;
        int charScale = 10;
        int width = image.getWidth() * charScale;
        int height = image.getHeight() * charScale;

        int imageX = (gp.screenWidth - width) / 2;
        int imageY = (gp.screenHeight - height) / 2;

        g2.setColor(Color.WHITE);
        g2.fillRect(0, gp.getHeight() * 2 / 3, gp.screenWidth, gp.screenHeight);

        FontMetrics fm = g2.getFontMetrics();

        int offset = 30;

        Rectangle2D leftBounds = fm.getStringBounds("<", g2);
        Rectangle2D rightBounds = fm.getStringBounds(">", g2);

        int leftX = imageX - offset - (int) leftBounds.getMaxX();
        int rightX = imageX + width + offset - (int) rightBounds.getMinX();

        int arrowY = imageY + (height - fm.getHeight()) / 2 + fm.getAscent();

        g2.drawString("<", leftX, arrowY);
        g2.drawString(">", rightX, arrowY);

        g2.setColor(Color.RED);
        g2.drawString(caption, getXforCenteredText(caption), 50);

        g2.drawImage(image, imageX, imageY, width, height, null);

        Characters character = gp.chars.get(charNum);

        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 30));

        g2.drawString("Name: " + character.name, 50, gp.screenHeight * 2 / 3 + 45);
        g2.drawString("Description: " + character.description, 50, gp.screenHeight * 2 / 3 + 85);

        g2.setFont(new Font("Arial", Font.PLAIN, 22));

        int comboY = gp.screenHeight * 2 / 3 + 130;

        for (Combo combo : character.combos) {
            String sequence = " -> " + combo.sequence;

            g2.drawString(combo.name + ": " + sequence, 50, comboY);
            g2.drawString(combo.description, 50, comboY + 25);

            comboY += 65;
        }

    }

    private void drawPauseScreen() {

        drawFightScreen();
        g2.setColor(new Color(0,0,0,200));
        g2.fillRect(0,0,gp.screenWidth,gp.screenHeight);
        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD,48F));
        String text = "PAUSED";
        int x = getXforCenteredText(text);
        int y = gp.screenHeight/5;

        g2.drawString(text, x, y);

        g2.setFont(g2.getFont().deriveFont(Font.BOLD,48F));
        text = "BACK TO MAIN MENU";
        x = getXforCenteredText(text);
        y = gp.screenHeight/2;
        g2.drawString(text,x,y);

        if (pauseNum == 0){
            g2.drawString(">",x - (int)g2.getFontMetrics().getStringBounds(text,g2).getWidth()/4, y);
        }

        text = "UNDO";
        x = getXforCenteredText(text);
        y += 70;

        if (gp.fightManager.undoUsed){
            Font font = g2.getFont().deriveFont(Font.BOLD,48F);
            g2.setFont(font);
            g2.setColor(Color.RED);
            AttributedString str = new AttributedString(text);
            str.addAttribute(TextAttribute.FONT, font);
            str.addAttribute(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);

            g2.drawString(str.getIterator(), x, y);
        }else {
            g2.drawString(text,x,y);
        }

        if (pauseNum == 1){
            g2.drawString(">",x - (int)g2.getFontMetrics().getStringBounds(text,g2).getWidth()/4, y);
        }



    }

    private void drawUndoScreen(){
        drawFightScreen();
        g2.setColor(new Color(0,0,0,200));
        g2.fillRect(0,0,gp.screenWidth,gp.screenHeight);
        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD,48F));
        String text = "UNDO";
        int x = getXforCenteredText(text);
        int y = gp.screenHeight/5;

        g2.drawString(text, x, y);


        if (gp.fightManager.roundNum > 1){
            g2.setFont(g2.getFont().deriveFont(Font.BOLD,48F));
            text = "1 ROUND";
            x = getXforCenteredText(text);
            y = gp.screenHeight/2;
            g2.drawString(text,x,y);

            if (undoNum == 0){
                g2.drawString(">",x - (int)g2.getFontMetrics().getStringBounds(text,g2).getWidth()/4, y);
            }
        }else {
            g2.setFont(g2.getFont().deriveFont(Font.BOLD,48F));
            text = "TOO EARLY FOR UNDO!";
            x = getXforCenteredText(text);
            y = gp.screenHeight/2;
            g2.drawString(text,x,y);
        }

        if (gp.fightManager.roundNum > 2){
            text = "2 ROUNDS";
            x = getXforCenteredText(text);
            y += 70;

            g2.drawString(text,x,y);

            if (undoNum == 1){
                g2.drawString(">",x - (int)g2.getFontMetrics().getStringBounds(text,g2).getWidth()/4, y);
            }
        }

        if (gp.fightManager.roundNum > 3){
            g2.setFont(g2.getFont().deriveFont(Font.BOLD,48F));
            text = "3 ROUNDS";
            x = getXforCenteredText(text);
            y += 70;
            g2.drawString(text,x,y);

            if (undoNum == 2){
                g2.drawString(">",x - (int)g2.getFontMetrics().getStringBounds(text,g2).getWidth()/4, y);
            }
        }


    }

    private void drawTitleScreen() {

        g2.setColor(new Color(0,0,0));
        g2.fillRect(0,0,gp.screenWidth,gp.screenHeight);
        //TITLE NAME
        g2.setFont(g2.getFont().deriveFont(Font.BOLD,96F));
        String text = "Fighter Game";
        int x = getXforCenteredText(text);
        int y = gp.screenHeight*1/3;

        //SHADOW
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawString(text, x+5, y+5);
        //MAIN COLOR
        g2.setColor(Color.WHITE);
        g2.drawString(text, x, y);

        //MENU
        g2.setFont(g2.getFont().deriveFont(Font.BOLD,48F));
        text = "NEW GAME";
        x = getXforCenteredText(text);
        y = gp.screenHeight*2/3;
        g2.drawString(text,x,y);

        if (commandNum == 0){
            g2.drawString(">",x - (int)g2.getFontMetrics().getStringBounds(text,g2).getWidth()/4, y);
        }

        text = "FIGHT HISTORY";
        x = getXforCenteredText(text);
        y += 70;
        g2.drawString(text,x,y);

        if (commandNum == 1){
            g2.drawString(">",x - (int)g2.getFontMetrics().getStringBounds(text,g2).getWidth()/4, y);
        }

        text = "EXPORT FIGHT HISTORY";
        x = getXforCenteredText(text);
        y += 70;
        g2.drawString(text,x,y);

        if (commandNum == 2){
            g2.drawString(">",x - (int)g2.getFontMetrics().getStringBounds(text,g2).getWidth()/4, y);
        }

        text = "QUIT";
        x = getXforCenteredText(text);
        y += 70;
        g2.drawString(text,x,y);

        if (commandNum == 3){
            g2.drawString(">",x - (int)g2.getFontMetrics().getStringBounds(text,g2).getWidth()/2, y);
        }

    }

    private BufferedImage enumToImage(FightManager.Action action){
        if (action == FightManager.Action.SHOOT){
            return shootButton;
        } else if (action == FightManager.Action.BLOCK) {
            return blockButton;
        } else if (action == FightManager.Action.SLASH) {
            return slashButton;
        }else {
            return emptyImg;
        }
    }


    public int getXforCenteredText(String text){
        int length = (int)g2.getFontMetrics().getStringBounds(text,g2).getWidth();
        int x = gp.screenWidth/2 - length/2;
        return x;
    }

    public void drawCenteredCircle(Graphics2D g2, int radius) {
        int x = (gp.screenWidth - radius * 2) / 2;
        int y = (gp.screenHeight - radius * 2) / 2;

        g2.fillOval(x, y, radius * 2, radius * 2);
    }





}
