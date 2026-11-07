package structs;

import Main.FightManager;

import java.util.Queue;

public class Combo {
    public String name;
    public Queue<FightManager.Action> sequence;
    public String description;
    public int effect;
    public boolean isHealing;


    Combo(){}



}
