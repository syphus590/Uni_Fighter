package entity;

import Main.FightManager;
import Main.UI;
import Main.GamePanel;
import structs.Combo;
import utils.ImageLoader;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Random;

public class Enemy {

    GamePanel gp;

    int posX;
    int posY;
    int size;

    public int health;
    public int damage;
    public String name;
    public LinkedList<Combo>combos;
    public FightManager.Action action = null;
    public boolean isDead;
    //Sprite
    String spritePath;
    public BufferedImage sprite;

    //Health bar
    BufferedImage enemyBottom;
    BufferedImage enemyTop;

    FightManager fightManager;
    Random random = new Random();


    public Enemy(GamePanel gp, int charNum, FightManager fightManager){
        this.gp = gp;
        this.fightManager = fightManager;
        name = gp.chars.get(charNum).name;
        combos = gp.chars.get(charNum).combos;
        spritePath = gp.chars.get(charNum).spritePath;
        sprite = ImageLoader.loadImage(spritePath);
        enemyBottom = ImageLoader.loadImage("healthbars/bottom.png");
        enemyTop = ImageLoader.loadImage("healthbars/top.png");
        setDefaultValues();
    }

    public void setDefaultValues(){
        size = UI.realSize;
        posX = 1720;
        posY = (gp.screenHeight*9/10) - size;
        health = 100;
        damage = 10;
        isDead = false;
    }


    public FightManager.Action calculateAction(){


        FightManager.Action lastPlayerAction = fightManager.lastPlayerAction;
        FightManager.Action lastEnemyAction = fightManager.lastEnemyAction;

        // RULE 1
        if (lastPlayerAction == FightManager.Action.SHOOT && random.nextInt(100) < 70) {
            return FightManager.Action.SLASH;
        }

        // RULE 2
        if (lastPlayerAction == FightManager.Action.BLOCK && random.nextInt(100) < 70) {
            return FightManager.Action.SHOOT;
        }

        // RULE 3
        if (lastPlayerAction == FightManager.Action.SLASH && random.nextInt(100) < 70) {
            return  FightManager.Action.BLOCK;
        }

        // RULE 4
        if (lastEnemyAction != null && random.nextInt(100) < 20) {
            return lastEnemyAction;
        }

        // RULE 5
        return FightManager.Action.values()[random.nextInt(3)];
    }



    public void draw(Graphics2D g2){
        AffineTransform transform = new AffineTransform();
        transform.translate(posX + size, posY);
        transform.scale(-1, 1);
        transform.scale((double) size / sprite.getWidth(),(double) size / sprite.getHeight());
        g2.drawImage(sprite,transform,null);
        g2.setFont(new Font("Arial",Font.ITALIC,30));
        g2.setColor(Color.WHITE);

        //Health bar
        int boundOffset = 20;
        int centerSpace = 100;
        int scaledX = (gp.getWidth() - (centerSpace + boundOffset*2))/2;
        int scaledY = UI.realSize;

        //Total health
        g2.drawImage(enemyBottom,scaledX + boundOffset + centerSpace,boundOffset,scaledX,scaledY,null);

        //Current health
        int healthScale = (int)(scaledX * ((double)health/100));

        g2.drawImage(enemyTop,scaledX + boundOffset + centerSpace + (scaledX - healthScale),boundOffset,healthScale,scaledY,null);

        //Name
        int length = (int)g2.getFontMetrics().getStringBounds(name,g2).getWidth();
        int textX = (scaledX + boundOffset + centerSpace + scaledX) - length;
        int textY = boundOffset + 25;
        g2.drawString(name,textX,textY);


    }

    public void getDamaged(int damage){
        health = Math.max(0, health - damage);
        if (health == 0){
            isDead = true;
        }
    }

    public void getHealed(int heal){
        health += heal;
        if (health > 100){
            health = 100;
        }
    }

}
