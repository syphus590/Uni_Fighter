package Main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    GamePanel gp;

    public KeyHandler(GamePanel gp){
        this.gp = gp;
    }



    @Override
    public void keyTyped(KeyEvent keyEvent) {

    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        int code = keyEvent.getKeyCode();
        boolean leftButton = (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT);
        boolean rightButton = (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT);
        boolean upButton = (code == KeyEvent.VK_W || code == KeyEvent.VK_UP);
        boolean downButton = (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN);
        boolean enterButton = (code == KeyEvent.VK_ENTER);
        boolean escapeButton = (code == KeyEvent.VK_ESCAPE);
        if (gp.gameState == GamePanel.stage.MENU){
            if (upButton){
                gp.ui.commandNum--;
                if (gp.ui.commandNum < 0){
                    gp.ui.commandNum = 3;
                }
            }
            if (downButton){
                gp.ui.commandNum++;
                if (gp.ui.commandNum > 3){
                    gp.ui.commandNum = 0;
                }
            }
            if (enterButton){
                if (gp.ui.commandNum == 0){// NEW GAME
                    gp.gameState = GamePanel.stage.CHAR;
                } else if (gp.ui.commandNum == 1) {//FIGHT HISTORY
                    gp.gameState = GamePanel.stage.HISTORY;
                } else if (gp.ui.commandNum == 2) {
                    gp.fightHistoryManager.exportFightHistory();
                } else if (gp.ui.commandNum == 3) {
                    System.exit(0);
                }
            }
        } else if (gp.gameState == GamePanel.stage.CHAR) {
            if (escapeButton){
                gp.gameState = GamePanel.stage.MENU;
            } else if (rightButton){
                gp.ui.charNum++;
                if (gp.ui.charNum > gp.charCount){
                    gp.ui.charNum = 0;
                }
                System.out.println(gp.ui.charNum);
            } else if (leftButton) {
                gp.ui.charNum--;
                if (gp.ui.charNum < 0){
                    gp.ui.charNum = gp.charCount;
                }
                System.out.println(gp.ui.charNum);
            } else if (enterButton) {
                gp.fightManager.prepareFight(gp.ui.charNum);
                gp.gameState = GamePanel.stage.FIGHT;
            }

        } else if (gp.gameState == GamePanel.stage.FIGHT) {
            if (leftButton) {
                gp.ui.selectedAction = FightManager.Action.SHOOT;
            } else if (downButton) {
                gp.ui.selectedAction = FightManager.Action.BLOCK;
            } else if (rightButton) {
                gp.ui.selectedAction = FightManager.Action.SLASH;
            } else if (escapeButton) {
                gp.gameState = GamePanel.stage.PAUSE;
            } else if (enterButton) {
                gp.fightManager.fight(gp.ui.selectedAction);
            }

        } else if (gp.gameState == GamePanel.stage.PAUSE) {
            if (escapeButton){
                gp.gameState = GamePanel.stage.FIGHT;
            } else if (downButton) {
                gp.ui.pauseNum++;
                if (gp.ui.pauseNum > 1){
                    gp.ui.pauseNum = 0;
                }
            } else if (upButton) {
                gp.ui.pauseNum--;
                if (gp.ui.pauseNum < 0){
                    gp.ui.pauseNum = 1;
                }
            } else if (enterButton) {
                if (gp.ui.pauseNum == 0){
                    gp.gameState = GamePanel.stage.MENU;
                } else if ((gp.ui.pauseNum == 1) && !(gp.fightManager.undoUsed)
                        && gp.fightManager.roundNum > 1) {
                    gp.ui.undoNum = 0;
                    gp.gameState = GamePanel.stage.UNDO;
                }
            }
        } else if (gp.gameState == GamePanel.stage.UNDO) {
            int maxUndoNum = Math.max(0, Math.min(2, gp.fightManager.roundNum - 2));

            if (downButton) {
                gp.ui.undoNum++;
                if (gp.ui.undoNum > maxUndoNum){
                    gp.ui.undoNum = 0;
                }
            } else if (upButton) {
                gp.ui.undoNum--;
                if (gp.ui.undoNum < 0){
                    gp.ui.undoNum = maxUndoNum;
                }
            } else if (enterButton) {
                gp.fightManager.useUndo(gp.ui.undoNum + 1);
                gp.ui.undoNum = 0;
                gp.gameState = GamePanel.stage.FIGHT;
            } else if (escapeButton) {
                gp.gameState = GamePanel.stage.PAUSE;
            }
        } else if (gp.gameState == GamePanel.stage.RESULT) {
            if (enterButton){
                gp.gameState = GamePanel.stage.MENU;
            }
        } else if (gp.gameState == GamePanel.stage.HISTORY) {
            if (escapeButton){
                gp.gameState = GamePanel.stage.MENU;
            } else if (!gp.fightHistoryManager.history.isEmpty()){
                if (leftButton){
                    gp.fightHistoryManager.fightIterator--;
                    if (gp.fightHistoryManager.fightIterator < 0){
                        gp.fightHistoryManager.fightIterator = gp.fightHistoryManager.history.size() - 1;
                    }
                } else if (rightButton) {
                    gp.fightHistoryManager.fightIterator++;
                    if (gp.fightHistoryManager.fightIterator >= gp.fightHistoryManager.history.size()){
                        gp.fightHistoryManager.fightIterator = 0;
                    }
                }

            }
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {


    }
}
