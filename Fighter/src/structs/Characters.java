package structs;

import com.fasterxml.jackson.annotation.JsonIgnore;
import utils.ImageLoader;

import java.awt.image.BufferedImage;
import java.util.LinkedList;

public class Characters {

    public String name;
    public String description;
    public String spritePath;
    public LinkedList<Combo>combos = new LinkedList<>();

    @JsonIgnore
    public BufferedImage sprite;

    Characters(){};

    @JsonIgnore
    public void getCharImage() {
        sprite = ImageLoader.loadImage(spritePath);
    }





}
