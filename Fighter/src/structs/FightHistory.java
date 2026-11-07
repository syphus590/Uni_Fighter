package structs;

import Main.FightManager;

public class FightHistory {
    public int playerChar;
    public int enemyChar;
    public int roundCount;
    public String winner;
    public int fightNum;

    public FightHistory(){};

    public FightHistory(int fightNum,int playerChar, int enemyChar, int roundCount, FightManager.Outcome winner){
        this.playerChar = playerChar;
        this.enemyChar = enemyChar;
        this.roundCount = roundCount;
        this.fightNum = fightNum;
        switch (winner){
            case P_WIN -> this.winner = "PLAYER";
            case E_WIN -> this.winner = "ENEMY";
        }

    }
}
