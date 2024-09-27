package classes;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Random;

public class GameWords extends RoundRectangle2D.Float {
    private final FontMetrics fm;
    private final int r, g, b;
    String string;
    float x_speed, y_speed;

    public GameWords(String s, GamePanel gp) {
        super(0, 0, 0, 0, 0, 0);
        Random rand = new Random();
        string = s;
        fm = gp.getFontMetrics(gp.getFont());
        super.x = rand.nextInt(1, Main.screenSize.width - fm.stringWidth(s)); // x position
        super.y = rand.nextInt(0, fm.getHeight() + 5); // y position
        super.width = fm.stringWidth(s) + 4;
        super.height = fm.getHeight() + 4;
        super.arcwidth = super.height / 2;
        super.archeight = super.height / 2;

        x_speed = (-rand.nextFloat(0.1f, 0.5f)) * rand.nextInt(-1, 2);
        y_speed = rand.nextFloat(0.0f, 0.5f);

        r = rand.nextInt(1, 256);
        g = rand.nextInt(1, 256);
        b = rand.nextInt(1, 256);
    }

    public void move(ArrayList<GameWords> gameWords) {
        for (GameWords w : gameWords) {
            //detect collision with other words
            if (w != this && w.intersects(this.getBounds())) {
                float tempX = x_speed;
                float tempY = y_speed;
                x_speed = w.x_speed;
                y_speed = w.y_speed;
                w.x_speed = tempX;
                w.y_speed = tempY;
                if (w.contains(super.x, super.y)) {
                    super.y = (float) w.getMaxY(); // y position
                }
                break;
            }
        }
        if (super.x < 0 || super.x > (Main.screenSize.width - getWidth() - 5))
            x_speed = -x_speed;
        if (super.y < 0 || super.y > GamePanel.gameBoardHeight - height)
            y_speed = -y_speed;
        super.x += x_speed;
        super.y += y_speed;
    }

    public void paint(Graphics2D g2) {
        g2.setColor(new Color(r, g, b));
        g2.draw(this);
        g2.setColor(new Color(g, b, r).brighter().brighter());
        g2.drawString(string, super.x + 2, super.y + fm.getHeight());
    }
}
