package structs;

import Main.FightManager;

import java.util.ArrayDeque;

public class BattleState {

    public int playerHealth;
    public int enemyHealth;
    public int roundNum;

    public ArrayDeque<FightManager.Action> playerComboQueue;
    public ArrayDeque<FightManager.Action> enemyComboQueue;

    public FightManager.Action lastPlayerAction;
    public FightManager.Action lastEnemyAction;

    public BattleState(int playerHealth,int enemyHealth, int roundNum,
                       ArrayDeque<FightManager.Action> playerComboQueue,
                       ArrayDeque<FightManager.Action> enemyComboQueue,
                       FightManager.Action lastPlayerAction,
                       FightManager.Action lastEnemyAction){
        this.playerHealth = playerHealth;
        this.enemyHealth = enemyHealth;
        this.roundNum = roundNum;
        this.playerComboQueue = new ArrayDeque<>(playerComboQueue);
        this.enemyComboQueue = new ArrayDeque<>(enemyComboQueue);
        this.lastPlayerAction = lastPlayerAction;
        this.lastEnemyAction = lastEnemyAction;
    }



}
