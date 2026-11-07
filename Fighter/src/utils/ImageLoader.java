package utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ImageLoader {

    public static BufferedImage loadImage(String path){


        File file = new File("res", path);

        if (file.exists()) {
            try {
                BufferedImage image = ImageIO.read(file);

                if (image != null) {
                    return image;
                }
            } catch (IOException e) {
                System.err.println("Failed to read external image: " + file);
            }
        }





        String resourcePath = "/res/" + path;

        try {
            InputStream stream = ImageLoader.class.getResourceAsStream(resourcePath);
            if (stream != null) {
                BufferedImage image = ImageIO.read(stream);

                if (image != null) {
                    return image;
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to read included image: " + resourcePath);
        }
        System.err.println("Image not found: " + path);
        return createErrorImage();
    }

    private static BufferedImage createErrorImage() {

        int witdh = 32;
        int height = 32;

        BufferedImage image = new BufferedImage(witdh,height,BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = image.createGraphics();

        g2.setColor(Color.RED);
        g2.fillRect(0,0,witdh,height);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD,14));

        String text = "BAD";

        int x = (witdh - g2.getFontMetrics().stringWidth(text))/2;
        int y = height /2;

        g2.drawString(text,x,y);

        g2.dispose();

        return image;

    }


}
