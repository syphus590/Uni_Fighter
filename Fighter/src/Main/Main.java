package Main;

import structs.Characters;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {

            public static LinkedList<Characters> characters;



        public static void main(String[] args) {

            ObjectMapper mapper = new ObjectMapper();

            try {
                characters = mapper.readValue(
                        new File("res/data/characters.json"),
                        new TypeReference<LinkedList<Characters>>() {}
                );
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(
                        null,
                        "Could not read res/data/characters.json:\n" + e.getMessage(),
                        "Fighter - load error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            if (characters == null || characters.size() < 2) {
                JOptionPane.showMessageDialog(
                        null,
                        "characters.json must contain at least 2 characters.",
                        "Fighter - data error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            System.out.println("Loaded " + characters.size() + " characters!");


            System.setProperty("sun.java2d.opengl", "true");
            JFrame window = new JFrame();
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setResizable(false);
            window.setTitle("Fighter");

            GamePanel gamePanel = new GamePanel(characters);
            window.add(gamePanel);

            window.pack();


            window.setLocationRelativeTo(null);
            window.setVisible(true);
            System.out.println("Panel size: " + gamePanel.getSize());

            gamePanel.startGameThread();
        }
}
