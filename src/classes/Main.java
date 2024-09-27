package classes;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    public static final Cursor invisibleCursor = Toolkit.getDefaultToolkit().createCustomCursor(
            new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "invisible");
    public static final JFrame frame = new JFrame("Space Shooter");
    public static final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    public static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    public Main() {
        GamePanel gp = new GamePanel();
        ScoreBoard sb = new ScoreBoard();
        frame.setUndecorated(true);
        frame.setResizable(false);
        frame.setBackground(new Color(0x80000000, true));
        frame.setSize(screenSize);
        frame.setLocationRelativeTo(null);
        frame.setCursor(invisibleCursor);
        frame.add(sb, BorderLayout.NORTH);
        frame.add(gp, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.pack();
        gp.requestFocus();
        gp.requestFocusInWindow();


        try {
            service.scheduleAtFixedRate(() -> {
                gp.repaint();
                sb.repaint();
            }, 0, 16667, TimeUnit.MICROSECONDS);
            gp.requestFocus();
            gp.requestFocusInWindow();
        } catch (Exception ignored) {
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}