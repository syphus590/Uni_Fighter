package Main;

import entity.Enemy;
import entity.Player;
import structs.BattleState;
import structs.Combo;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.Random;
import java.util.Stack;

public class FightManager {

    int playerChar;
    int enemyChar;
    Player player;
    Enemy enemy;
    boolean fightActive = false;
    boolean undoUsed;
    int roundNum;
    public String triggeredComboName;
    public String triggeredComboOwner;
    public int comboNotificationTimer;
    Random r = new Random(System.nanoTime());
    Stack<BattleState>roundHistory = new Stack<>();
    public Action lastPlayerAction = null;
    public Action lastEnemyAction = null;




    public enum Action{
        SHOOT,
        BLOCK,
        SLASH
    }
    public ArrayDeque<Action> playerComboBuffer = new ArrayDeque<>();
    public ArrayDeque<Action> enemyComboBuffer = new ArrayDeque<>();

    public enum Outcome{
        P_WIN,
        E_WIN,
        TIE
    }
    Outcome roundOutcome = null;
    public String winner;

    boolean correctCombo = false;



    GamePanel gp;

    public FightManager(GamePanel gp){
        this.gp = gp;
    }



    public void prepareFight(int playerChar){
        //Player
        createPlayer(playerChar);
        if (!playerComboBuffer.isEmpty()){
            playerComboBuffer.clear();
        }
        //Enemy
        createEnemy(playerChar);
        if (!enemyComboBuffer.isEmpty()){
            enemyComboBuffer.clear();
        }

        lastPlayerAction = null;
        lastEnemyAction = null;

        fightActive = true;
        roundNum = 1;
        undoUsed = false;
        roundHistory = new Stack<>();
        roundHistory.push(new BattleState(
                player.health, enemy.health, roundNum,
                playerComboBuffer, enemyComboBuffer,
                lastPlayerAction, lastEnemyAction
        ));

        triggeredComboName = null;
        triggeredComboOwner = null;
        comboNotificationTimer = 0;
    }





    public void fight(Action playerAction){

        if (playerAction != null){
            player.setAction(playerAction);
        }else {
            player.setAction(Action.SHOOT);
        }

        enemy.action = enemy.calculateAction();
        lastPlayerAction = player.action;
        lastEnemyAction = enemy.action;

        if (player.action == enemy.action){
            roundOutcome = Outcome.TIE;
            handleTie();

        } else if (beats(player.action, enemy.action)) {

            roundOutcome = Outcome.P_WIN;
            handlePlayerWin();

        } else {
            roundOutcome = Outcome.E_WIN;
            handleEnemyWin();
        }

        if (player.isDead || enemy.isDead){
            finishFight(roundOutcome);
            return;
        }

        roundNum++;
        BattleState round = new BattleState(
                player.health, enemy.health, roundNum,
                playerComboBuffer, enemyComboBuffer,
                lastPlayerAction, lastEnemyAction
        );

        roundHistory.push(round);




    }

    private boolean beats(Action attacker, Action defender){
        return (attacker == Action.SHOOT && defender == Action.BLOCK)
                || (attacker == Action.BLOCK && defender == Action.SLASH)
                || (attacker == Action.SLASH && defender == Action.SHOOT);
    }

    private void finishFight(Outcome roundOutcome) {

        switch (roundOutcome){
            case P_WIN -> winner = "PLAYER";
            case E_WIN -> winner = "ENEMY";
        }
        gp.fightHistoryManager.addFight(playerChar,enemyChar,roundHistory,roundNum,roundOutcome);
        gp.gameState = GamePanel.stage.RESULT;
    }

    public void useUndo(int amount){
        triggeredComboName = null;
        triggeredComboOwner = null;
        comboNotificationTimer = 0;

        int undoAmount = Math.min(amount, roundNum - 1);
        if (undoAmount <= 0){
            return;
        }

        for (int i = 0; i < undoAmount; i++){
            roundHistory.pop();
        }

        BattleState prevRound = roundHistory.peek();
        player.health = prevRound.playerHealth;
        enemy.health = prevRound.enemyHealth;
        roundNum = prevRound.roundNum;
        playerComboBuffer = new ArrayDeque<>(prevRound.playerComboQueue);
        enemyComboBuffer = new ArrayDeque<>(prevRound.enemyComboQueue);
        lastPlayerAction = prevRound.lastPlayerAction;
        lastEnemyAction = prevRound.lastEnemyAction;
        undoUsed = true;
    }


    void createPlayer(int playerChar){
        this.playerChar = playerChar;
        player = new Player(gp,playerChar);
    }

    void createEnemy(int playerChar){

        do {
            enemyChar = r.nextInt(gp.chars.size());
        }while (enemyChar ==playerChar);

        enemy = new Enemy(gp,enemyChar,this);

    }

    public void drawOponents(Graphics2D g2){
        player.draw(g2);
        enemy.draw(g2);
    }

    public Action[] getPlayerComboQueue(){
        Action[] queue = new Action[3];
        int i = 0;

        for (Action action : playerComboBuffer){
            queue[i++] = action;
        }
        return queue;
    }

    public Action[] getEnemyComboQueue() {
        Action[] queue = new Action[3];
        int i = 0;

        for (Action action : enemyComboBuffer){
            queue[i++] = action;
        }
        return queue;
    }


    private void handleTie(){

        System.out.println("This round was a tie!");

        playerComboBuffer.add(player.action);
        if (playerComboBuffer.size() > 3){
            playerComboBuffer.removeFirst();
        }

        enemyComboBuffer.add(enemy.action);
        if (enemyComboBuffer.size() > 3){
            enemyComboBuffer.removeFirst();
        }
    }

    private void handlePlayerWin(){

        System.out.println("Player won this round with: " + player.action);

        // Player combo
        playerComboBuffer.add(player.action);
        if (playerComboBuffer.size() > 3){
            playerComboBuffer.removeFirst();
        }

        for (Combo combo : player.combos){
            correctCombo = combo.sequence.size() == playerComboBuffer.size()
                    && combo.sequence.stream().toList().equals(playerComboBuffer.stream().toList());

            if (correctCombo){
                triggeredComboName = combo.name;
                triggeredComboOwner = "PLAYER";
                comboNotificationTimer = 120;
                if (combo.isHealing){
                    player.getHealed(combo.effect);
                }else {
                    enemy.getDamaged(combo.effect);
                }

                System.out.println("Player got combo " + combo.name);
                playerComboBuffer.clear();
                break;
            }
        }

        // Enemy combo
        enemyComboBuffer.clear();
        enemy.getDamaged(player.damage);
    }


    private void handleEnemyWin(){

        System.out.println("Enemy won this round with: " + enemy.action);

        // Player
        playerComboBuffer.clear();
        player.getDamaged(enemy.damage);

        // Enemy combo
        enemyComboBuffer.add(enemy.action);
        if (enemyComboBuffer.size() > 3){
            enemyComboBuffer.removeFirst();
        }

        for (Combo combo : enemy.combos){
            correctCombo = combo.sequence.size() == enemyComboBuffer.size() && combo.sequence.stream().toList().equals(enemyComboBuffer.stream().toList());

            if (correctCombo){
                triggeredComboName = combo.name;
                triggeredComboOwner = "ENEMY";
                comboNotificationTimer = 120;
                if (combo.isHealing){
                    enemy.getHealed(combo.effect);
                }else {
                    player.getDamaged(combo.effect);
                }

                System.out.println("Enemy got combo " + combo.name);
                enemyComboBuffer.clear();
                break;
            }
        }
    }

    public void updateComboNotification(){

        if (comboNotificationTimer > 0){
            comboNotificationTimer--;
        }
    }



}
