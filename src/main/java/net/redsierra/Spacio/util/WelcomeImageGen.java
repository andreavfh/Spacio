package net.redsierra.Spacio.util;

import net.dv8tion.jda.api.entities.User;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

public class WelcomeImageGen {
    public InputStream getImage(User user) throws IOException, FontFormatException {

        Resources resources = new Resources();

        BufferedImage backgroundImage;
        URL url = new URL(Objects.requireNonNull(user.getAvatarUrl()));
        BufferedImage pfp = ImageIO.read(url);
        try {
            backgroundImage = ImageIO.read(resources.getResourceFile("img/background.png"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        InputStream fontStream = resources.getResourceFile("font/OpenSans-ExtraBold.ttf");

        BufferedImage image = new BufferedImage(2280, 1080, BufferedImage.TYPE_INT_RGB);
        Font test = Font.createFont(Font.TRUETYPE_FONT, fontStream);
        Font font = test.deriveFont(Font.PLAIN, 150);

        Graphics g = image.getGraphics();
        g.setColor(Color.decode("#9fb2c4"));
        g.fillRect(0, 0, 947, 418);
        g.setFont(font);


        g.drawImage(pfp, 368, 260, 488, 488, null);
        g.drawImage(backgroundImage, 0, 0, null);
        g.drawString(Objects.requireNonNull(user.getGlobalName()), 1035, 550);
        Font test2 = Font.createFont(Font.PLAIN, fontStream);
        Font font2 = test2.deriveFont(Font.PLAIN, 85);
        g.setFont(font2);
        g.setColor(Color.decode("#30516f"));
        g.drawString("Welcome to the server!", 950, 620);

        g.dispose();

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        try {
            ImageIO.write(image, "png", os);

            return new ByteArrayInputStream(os.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
