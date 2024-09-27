package classes;

import javax.swing.*;
import java.awt.*;

import static classes.GamePanel.*;

public class ScoreBoard extends JPanel {
    static byte scoreBoardHeight = 30;
    String dis;
    int y = 2;

    public ScoreBoard() {
        setDoubleBuffered(true);
        setPreferredSize(new Dimension(Main.screenSize.width, scoreBoardHeight));
        setBackground(new Color(0xFF04563B, true));
        setFont(GamePanel.audioFont.deriveFont(16F));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2.setPaint(new GradientPaint((float) (circle += 10), (float) rand.nextInt(gameBoardHeight / 2),
                circle == Main.screenSize.width ? new Color(0xFF099B95, true) : new Color(0xFFFA6700, true),
                (float) Main.screenSize.width, 0.0F, circle < Main.screenSize.width ?
                new Color(0xFF40E8AA, true) : new Color(0xFF16B0E6, true), true));

        dis = "Score  =============  " + points + "              " + "Longest Word Typed  =============  " +
              longestWord + "              " + "Length  =============  " + longestWord.length() + " " +
              (longestWord.length() > 1 ? "characters" : "character") + "              " + "Time Taken To Type  =============  "
              + secUsed + (secUsed > 1 ? " seconds" : " second");
        g2.drawString(dis, y--, 15 + getFontMetrics(getFont()).getHeight() / 3);
        if (y + getFontMetrics(getFont()).stringWidth(dis) == 0) y = Main.screenSize.width - 5;
    }
}
