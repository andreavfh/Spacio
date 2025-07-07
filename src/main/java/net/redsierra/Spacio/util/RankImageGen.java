package net.redsierra.Spacio.util;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;

public class RankImageGen {

    private static final int IMAGE_WIDTH = 736;
    private static final int IMAGE_HEIGHT = 414;
    private static final int PFP_WIDTH = 221;
    private static final int PFP_HEIGHT = 207;
    private static final int PFP_X = 91;
    private static final int PFP_Y = 150;
    private static final int USER_NAME_X = PFP_X - 1;
    private static final int USER_NAME_Y = PFP_Y - 63;
    private static final int LEVEL_X = IMAGE_WIDTH / 2 + 67;
    private static final int LEVEL_Y = 250;
    private static final int XP_X = IMAGE_WIDTH / 2 + 60;
    private static final int XP_Y = 310;

    private static final BufferedImage BACKGROUND_IMAGE;
    private static final Font FONT_BOLD_46;
    private static final Font FONT_BOLD_38;

    static {
        try (InputStream imageStream = new URL("https://i.pinimg.com/736x/35/6a/19/356a19c7d607c289b6256e903c7b42ba.jpg").openStream()) {
            BACKGROUND_IMAGE = ImageIO.read(imageStream);

            FONT_BOLD_46 = new Font("Monospaced", Font.BOLD, 46);
            FONT_BOLD_38 = new Font("Monospaced", Font.BOLD, 38);

            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(FONT_BOLD_46);
            ge.registerFont(FONT_BOLD_38);

        } catch (Exception e) {
            throw new RuntimeException("Failed to load background image or fonts", e);
        }
    }


    public InputStream getImage(String name, String avatarUrl, int xp, int level) throws IOException {
        BufferedImage image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();

        try {
            BufferedImage avatarImage;
            try (InputStream avatarStream = new URL(avatarUrl).openStream()) {
                avatarImage = ImageIO.read(avatarStream);
            }

            // Fondo sólido primero
            g.setColor(Color.BLACK);  // Cambia por el color que te guste
            g.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);

            // Imagen PNG encima (con transparencia)
            g.drawImage(BACKGROUND_IMAGE, 0, 0, null);

            // Recorte circular para avatar
            Shape clip = new java.awt.geom.Ellipse2D.Float(PFP_X, PFP_Y, PFP_WIDTH, PFP_HEIGHT);
            g.setClip(clip);
            g.drawImage(avatarImage, PFP_X, PFP_Y, PFP_WIDTH, PFP_HEIGHT, null);
            g.setClip(null);

            // Texto
            g.setColor(Color.BLACK);
            g.setFont(FONT_BOLD_46);
            g.drawString(name, USER_NAME_X, USER_NAME_Y);

            g.setColor(Color.decode("#c9daec"));
            g.setFont(FONT_BOLD_38);
            int requiredXP = (level + 1) * 350;
            g.drawString("Level " + level, LEVEL_X, LEVEL_Y);
            g.drawString(xp + "/" + requiredXP, XP_X, XP_Y);

        } finally {
            g.dispose();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return new ByteArrayInputStream(baos.toByteArray());
    }
}