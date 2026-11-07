package entity;

import Main.FightManager;
import Main.UI;
import Main.GamePanel;
import structs.Combo;
import utils.ImageLoader;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

public class Player {

    GamePanel gp;

     int posX;
     int posY;
     int size;

    public int health;
    public int damage;
    public String name;
    public LinkedList<Combo>combos;
    public FightManager.Action action;
    public boolean isDead;
    //Sprite
    String spritePath;
    public BufferedImage sprite;

    //Health bar
    BufferedImage playerBottom;
    BufferedImage playerTop;


    public Player(GamePanel gp, int charNum){
        this.gp = gp;
        name = gp.chars.get(charNum).name;
        combos = gp.chars.get(charNum).combos;
        spritePath = gp.chars.get(charNum).spritePath;
        sprite = ImageLoader.loadImage(spritePath);
        playerBottom = ImageLoader.loadImage("healthbars/bottom.png");
        playerTop = ImageLoader.loadImage("healthbars/top.png");
        setDefaultValues();
    }

    public void setDefaultValues(){
        size = UI.realSize;
        posX = 200;
        posY = (gp.screenHeight*9/10) - size;
        health = 100;
        damage = 10;
        action = null;
        isDead = false;
    }

    public void setAction(FightManager.Action action){
        this.action = action;
    }

    public void draw(Graphics2D g2){
        g2.drawImage(sprite,posX,posY, size,size,null);
        g2.setFont(new Font("Arial",Font.ITALIC,30));
        g2.setColor(Color.WHITE);

        //Health bar
        int boundOffset = 20;
        int centerSpace = 100;
        int scaledX = (gp.getWidth() - (centerSpace + boundOffset*2))/2;
        int scaledY = UI.realSize;

        //Total health
        g2.drawImage(playerBottom,boundOffset,boundOffset,scaledX,scaledY,null);

        //Current health
        int healthScale = (int)(scaledX * ((double) health /100));
        g2.drawImage(playerTop,boundOffset,boundOffset,healthScale,scaledY,null);



        int textX = boundOffset;
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
