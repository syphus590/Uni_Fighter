package Main;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import structs.BattleState;
import structs.FightHistory;
import utils.ImageLoader;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.Stack;

public class FightHistoryManager {
    LinkedList<FightHistory> history;
    GamePanel gp;
    public int fightCount = 0;
    int offset = 100;
    public int fightIterator = 0;
    BufferedImage[] charSprites = new BufferedImage[Main.characters.size()];
    String exportPath = "res/data/fight_history.json";

    public FightHistoryManager(GamePanel gp){
        this.gp = gp;
        history = loadHistory();
        fightCount = history.stream().mapToInt(fight -> fight.fightNum).max().orElse(0);
        fightIterator = 0;
        for (int i = 0; i < Main.characters.size();i++){
            charSprites[i] = ImageLoader.loadImage(Main.characters.get(i).spritePath);
        }
    }

    private LinkedList<FightHistory> loadHistory(){
        Path path = Path.of(exportPath);
        if (!Files.exists(path)){
            return new LinkedList<>();
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            LinkedList<FightHistory> loaded = mapper.readValue(
                    path.toFile(),
                    new TypeReference<LinkedList<FightHistory>>() {}
            );
            return loaded == null ? new LinkedList<>() : loaded;
        } catch (IOException e) {
            System.err.println("Failed to load fight history: " + e.getMessage());
            return new LinkedList<>();
        }
    }

    public void addFight(int playerChar, int enemyChar, Stack<BattleState> roundHistory, int roundCount, FightManager.Outcome winner){
        fightCount++;
        history.add(new FightHistory(fightCount,playerChar,enemyChar,roundCount,winner));
        exportFightHistory();
    }

    public void draw(Graphics2D g2){
        g2.setFont(new Font("Arial",Font.BOLD,64));
        g2.setColor(Color.WHITE);
        int textLeftX = 0;
        int textRightX = (gp.getWidth() - (int)g2.getFontMetrics().getStringBounds(">",g2).getWidth());
        int textY = (gp.getHeight() - (int)g2.getFontMetrics().getStringBounds("<",g2).getHeight())/2;
        g2.drawString("<",textLeftX,textY);
        g2.drawString(">",textRightX,textY);
        g2.drawRect(offset,offset,gp.getWidth() - offset*2, gp.getHeight() - offset*2);

        if (history.isEmpty()){
            String text = "NO FIGHT HISTORY!";
            int x = ((gp.getWidth() - offset) - (int)g2.getFontMetrics().getStringBounds(text,g2).getWidth())/2 + offset;
            int y = ((gp.getHeight() - offset) + (int)g2.getFontMetrics().getStringBounds(text,g2).getHeight())/2;
            g2.drawString(text,x,y);
        }else {
            if (fightIterator < 0 || fightIterator >= history.size()){
                fightIterator = 0;
            }
            drawFightInfo(fightIterator,g2);
        }

    }

    private void drawFightInfo(int fightIterator, Graphics2D g2) {
        FightHistory fight = history.get(fightIterator);

        int x0 = offset;
        int y0 = offset;
        int width = gp.getWidth() - offset * 2;
        int height = gp.getHeight() - offset * 2;

        // HEADER
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 48));

        String text = "FIGHT No. " + fight.fightNum;

        FontMetrics fm = g2.getFontMetrics();

        int x = x0 + (width - fm.stringWidth(text)) / 2;
        int y = y0 + 60;

        g2.drawString(text, x, y);


        // PARTICIPANTS
        g2.setFont(new Font("Arial", Font.BOLD, 30));
        fm = g2.getFontMetrics();

        String player = "PLAYER NAME: " + gp.chars.get(fight.playerChar).name;
        String enemy = "ENEMY NAME: " + gp.chars.get(fight.enemyChar).name;

        int sectionY = y0 + height / 3;

        g2.drawString(player, x0 + 40, sectionY);
        g2.drawString(enemy, x0 + width - fm.stringWidth(enemy) - 40, sectionY);


        // WINNER
        g2.setFont(new Font("Arial", Font.BOLD, 36));
        fm = g2.getFontMetrics();

        text = "WINNER: " + fight.winner;

        x = x0 + (width - fm.stringWidth(text)) / 2;
        y = y0 + height * 2 / 3;

        g2.drawString(text, x, y);

        //WINNER IMAGE
        int scale = 10;
        if ("PLAYER".equals(fight.winner)){
            int imageW = charSprites[fight.playerChar].getWidth()*scale;
            int imageH = charSprites[fight.playerChar].getHeight()*scale;
            x = x0 + (width - imageW)/2;
            y = (y0 + height * 2 / 3) - imageH - 30;
            g2.drawImage(charSprites[fight.playerChar],x,y,imageW,imageH,null);

        }else {
            int imageW = charSprites[fight.enemyChar].getWidth()*scale;
            int imageH = charSprites[fight.enemyChar].getHeight()*scale;
            x = x0 + (width - imageW)/2;
            y = (y0 + height * 2 / 3) - imageH - 30;
            g2.drawImage(charSprites[fight.enemyChar],x,y,imageW,imageH,null);
        }


        // ROUND COUNT
        g2.setFont(new Font("Arial", Font.PLAIN, 24));
        fm = g2.getFontMetrics();

        text = "Rounds played: " + fight.roundCount;

        x = x0 + (width - fm.stringWidth(text)) / 2;
        y = y0 + height * 2 / 3 + 50;

        g2.drawString(text, x, y);

    }


    public void exportFightHistory() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);

            Path path = Path.of(exportPath);

            Files.createDirectories(path.toAbsolutePath().getParent());

            mapper.writeValue(path.toFile(), history);

            System.out.println("Fight history exported successfully!");
            System.out.println("Exported to: " + path.toAbsolutePath());

        } catch (IOException e) {
            System.err.println("Failed to export fight history: " + e.getMessage());
        }
    }



}
