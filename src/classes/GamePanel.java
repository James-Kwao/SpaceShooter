package classes;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.List;
import java.util.*;

public class GamePanel extends JPanel {
    static final Random rand = new Random();
    static final int gameBoardHeight = Main.screenSize.height - ScoreBoard.scoreBoardHeight;
    static Font audioFont, waveFont;
    static int points, circle = 0, secUsed;
    static long l;
    static String longestWord = "";
    private final ArrayList<GameWords> gameWords = new ArrayList<>();
    private final ArrayList<String> totalGameWords = new ArrayList<>();
    private final SoundsManager sm = new SoundsManager();
    private final ArrayList<Float> scores = new ArrayList<>();
    boolean inDanger, isPause = true, isHelp, isSettings, isMusic, isSound = true, isHistory = true, isPointer = true, check;
    Image spaceShip, exp1, exp2, exp3, exp4, explosion;
    int addNewWordCounter = 0, msgXps = 190, gamePlayed = 1, expIndex, changeExp;
    char keyTyped = '1';
    private long startTime, pauseTime;
    private Line2D deadZone, pointer;
    private String wordSearch = "";
    private Rectangle2D spaceShipBound;

    public GamePanel() {
        setPreferredSize(new Dimension(Main.screenSize.width, gameBoardHeight));
        setBackground(new Color(0xFF000000, true));
        setCursor(Main.invisibleCursor);
        setFont(new Font("Arial", Font.PLAIN, 12));
        InputStream stream1 = ClassLoader.getSystemClassLoader().getResourceAsStream("res/fonts/Audiowide.ttf");
        InputStream stream2 = ClassLoader.getSystemClassLoader().getResourceAsStream("res/fonts/Wavefont-SemiBold.ttf");

        try {
            spaceShip = ImageIO.read(Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResourceAsStream(
                    "res/images/space_ship.png")));
            exp1 = ImageIO.read(Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResourceAsStream(
                    "res/images/explo1.png")));
            exp2 = ImageIO.read(Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResourceAsStream(
                    "res/images/explo2.png")));
            exp3 = ImageIO.read(Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResourceAsStream(
                    "res/images/explo3.png")));
            exp4 = ImageIO.read(Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResourceAsStream(
                    "res/images/explo4.png")));
            assert stream1 != null;
            assert stream2 != null;

            audioFont = Font.createFont(0, stream1).deriveFont(Font.PLAIN, 10.0F);
            waveFont = Font.createFont(0, stream2).deriveFont(Font.BOLD, 10.0F);
            InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("res/gameWords");

            assert is != null;
            BufferedReader bf = new BufferedReader(new InputStreamReader(is));
            totalGameWords.addAll(List.of(bf.readLine().split(" ")));
            totalGameWords.trimToSize();

            for (int i = 0; i < 8; ++i)
                gameWords.add(new GameWords(totalGameWords.get(rand.nextInt(0, totalGameWords.size())), this));

            expIndex = -1;
        } catch (Exception var6) {
            System.out.println(var6.getMessage());
        }

        if (isMusic) {
            sm.loadRand();
            sm.playMusic();
        }

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                super.keyTyped(e);
                if (!isPause) {
                    wordSearch = wordSearch.concat(String.valueOf(e.getKeyChar())).toLowerCase();
                    check = false;
                    for (int i = 0; i < gameWords.size(); ++i) {
                        GameWords gw = gameWords.get(i);
                        if (gw.string.equals(wordSearch)) {
                            if (isSound)
                                sm.playExplosion();
                            if (deadZone != null && gw.getMaxY() > deadZone.getY1())
                                inDanger = false;
                            points++;
                            if (wordSearch.length() >= longestWord.length()) {
                                longestWord = wordSearch;
                                secUsed = (int) ((System.currentTimeMillis() - l) / 1000);
                            }
                            wordSearch = "";
                            expIndex = gameWords.indexOf(gw);
                            explosion = exp1;
                            break;
                        }

                        //check if the right content is being typed
                        if (gw.string.substring(0, Math.min(wordSearch.length(), gw.string.length())).contentEquals(wordSearch)) {
                            check = true;
                            if (gw.string.length() >= longestWord.length()) {
                                if (l == 0L)
                                    l = System.currentTimeMillis();
                            }
                        }
                    }

                    if (!check) {
                        wordSearch = "";
                        pointer = null;
                        l = 0;
                    }
                }

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        if (isPause)
                            switch (keyTyped) {
                                case '1':
                                    if (startTime == 0L) {
                                        startTime = System.currentTimeMillis();
                                    }
                                    if (pauseTime != 0L) {
                                        startTime += (System.currentTimeMillis() - pauseTime);
                                    }
                                    pauseTime = 0;
                                    requestFocus();
                                    isPause = false;
                                    if (isMusic) {
                                        sm.playMusic();
                                    } else sm.stopMusic();
                                    break;
                                case '2':
                                    isSettings = true;
                                    keyTyped = '5';
                                    break;
                                case '3':
                                    isHelp = true;
                                    break;
                                case '4':
                                    Main.service.close();
                                    Main.frame.dispose();
                                    System.gc();
                                    System.exit(0);
                                    break;
                                case '5':
                                    keyTyped = '2';
                                    isSettings = false;
                                    break;
                                case '6':
                                    isSound = !isSound;
                                    break;
                                case '7':
                                    isMusic = !isMusic;
                                    if (isMusic) sm.loadRand();
                                    else sm.stopMusic();
                                    break;
                                case '8':
                                    isHistory = !isHistory;
                                    break;
                                case '9':
                                    isPointer = !isPointer;
                                    break;
                            }
                        break;
                    case KeyEvent.VK_SPACE, KeyEvent.VK_ESCAPE:
                        if (!isPause) {
                            pauseTime = System.currentTimeMillis();
                            if (isMusic) {
                                sm.pauseMusic();
                            }
                            isPause = true;
                        }
                        break;
                    case KeyEvent.VK_UP:
                        if (isPause)
                            switch (keyTyped) {
                                case '1':
                                    keyTyped = '4';
                                    break;
                                case '2':
                                    keyTyped = '1';
                                    break;
                                case '3':
                                    keyTyped = '2';
                                    break;
                                case '4':
                                    keyTyped = '3';
                                    break;
                                case '5':
                                    keyTyped = '9';
                                    break;
                                case '6':
                                    keyTyped = '5';
                                    break;
                                case '7':
                                    keyTyped = '6';
                                    break;
                                case '8':
                                    keyTyped = '7';
                                    break;
                                case '9':
                                    keyTyped = '8';
                                    break;
                            }
                        break;
                    case KeyEvent.VK_DOWN:
                        if (isPause)
                            switch (keyTyped) {
                                case '1':
                                    keyTyped = '2';
                                    break;
                                case '2':
                                    keyTyped = '3';
                                    break;
                                case '3':
                                    keyTyped = '4';
                                    break;
                                case '4':
                                    keyTyped = '1';
                                    break;
                                case '5':
                                    keyTyped = '6';
                                    break;
                                case '6':
                                    keyTyped = '7';
                                    break;
                                case '7':
                                    keyTyped = '8';
                                    break;
                                case '8':
                                    keyTyped = '9';
                                    break;
                                case '9':
                                    keyTyped = '5';
                                    break;
                            }
                        break;
                    case KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT:
                        if (isSettings)
                            switch (keyTyped) {
                                case '6':
                                    isSound = !isSound;
                                    break;
                                case '7':
                                    isMusic = !isMusic;
                                    if (isMusic) sm.loadRand();
                                    else sm.stopMusic();
                                    break;
                                case '8':
                                    isHistory = !isHistory;
                                    break;
                                case '9':
                                    isPointer = !isPointer;
                                    break;
                            }
                        break;
                }
            }
        });
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        float xPos = (float) (Main.screenSize.width - 65) / 2.0F;
        float yPos = (float) (gameBoardHeight - 65 - 20);
        if (circle > Main.screenSize.width * 2)
            circle = 0;

        drawStars(g2);
        if (isPause && !isSettings && !isHelp) {
            if (isHistory)
                drawHistory(g2);
            drawMainMenu(g2);
        } else if (isSettings) {
            drawSettingsMenu(g2);
        } else if (isHelp) {
//            drawMainMenu(g2);
        } else {
            if (gameWords.size() <= 30)
                addNewWord(g2);
            drawPlayer(g2, xPos, yPos);
            checkGameOver(yPos, g2);
            animateExplode(g2);

        }
        g.dispose();
    }

    private void checkGameOver(float yPos, Graphics2D g2) {
        deadZone = new Line2D.Float(0.0F, yPos - 120.0F, (float) Main.screenSize.width, yPos - 120.0F);

        for (GameWords gw : gameWords) {
            if (isPointer && !wordSearch.isEmpty())
                if (gw.string.substring(0, Math.min(wordSearch.length(), gw.string.length())).contentEquals(wordSearch)) {
                    pointer = new Line2D.Float((float) spaceShipBound.getCenterX(), (float) spaceShipBound.getCenterY(),
                            (float) gw.getCenterX(), (float) gw.getMaxY());
                    g2.draw(pointer);
                }
            if ((gw.string.substring(0, Math.min(wordSearch.length(), gw.string.length())).contentEquals(wordSearch)) &&
                !wordSearch.isEmpty())
                g2.fillRect((int) gw.x, (int) gw.y, g2.getFontMetrics().stringWidth(wordSearch) + 2, (int) gw.height);
            gw.paint(g2);
            gw.move(gameWords);

            if (gw.getMaxY() > deadZone.getY1()) inDanger = true;

            if (spaceShipBound.intersects(gw.getBounds())) {
                if (isMusic) sm.pauseMusic();
                restartGame();
                break;
            }
        }
        g2.setColor(inDanger ? (new Color(-2134105823, true)).darker() :
                (new Color(-2147418352, true)).brighter());
        g2.draw(deadZone);
        inDanger = false;
    }

    private void addNewWord(Graphics2D g2) {
        g2.setStroke(new BasicStroke(2.0F));
        if (addNewWordCounter >= 155 && gameWords.size() <= 30) {
            String s = totalGameWords.get(rand.nextInt(0, totalGameWords.size()));
            gameWords.add(new GameWords(s, this));
            gameWords.trimToSize();
            addNewWordCounter = 0;
        }
        addNewWordCounter++;
    }

    private void drawStars(Graphics2D g2) {
        for (int i = 0; i < 50; ++i) {
            g2.setColor(new Color(rand.nextInt(1, 256), rand.nextInt(1, 256), rand.nextInt(1, 256)));
            g2.fill(new Ellipse2D.Float((float) rand.nextInt(Main.screenSize.width),
                    (float) rand.nextInt(gameBoardHeight), (float) rand.nextInt(2, 6), (float) rand.nextInt(5, 6)));
        }
    }

    private void drawPlayer(Graphics2D g2, float xPos, float yPos) {
        try {
            int xp = (int) (xPos + rand.nextInt(-5, 6));
            int yp = (int) yPos + rand.nextInt(-5, 6);
            spaceShipBound = new Rectangle2D.Float(xp + 10, yp, spaceShip.getWidth(null) - 20, spaceShip.getHeight(null));
            g2.drawImage(spaceShip, xp, yp, null);
        } catch (Exception ignored) {
        }
    }

    private void drawSettingsMenu(Graphics2D g2) {
        int size = 350;
        float xPos = (float) (Main.screenSize.width - size) / 2.0F;
        float yPos = (float) (Main.screenSize.height - size) / 2.0F - 30.0F;
        g2.setStroke(new BasicStroke(3.0F));
        Shape con = new RoundRectangle2D.Float(xPos, yPos, (float) size, (float) (size + 15), 30.0F, 30.0F);
        g2.setColor(new Color(0xFF04563B, true));
        g2.fill(con);
        GradientPaint grad = new GradientPaint((float) (circle += 10), (float) rand.nextInt(gameBoardHeight / 2),
                circle < Main.screenSize.width ? new Color(4253866) : new Color(1487078),
                (float) Main.screenSize.width, 0.0F, circle == Main.screenSize.width ?
                new Color(629653) : new Color(16410368), true);
        g2.setPaint(grad);
        g2.draw(con);
        g2.setFont(audioFont.deriveFont(30.0F));
        FontMetrics fm = g2.getFontMetrics();
        String s = "SETTINGS";
        g2.drawString(s, xPos + (float) size / 2.0F - (float) fm.stringWidth(s) / 2.0F, yPos += (float) (fm.getHeight() + 5));
        con = new Rectangle2D.Float(xPos, yPos += 20.0F, (float) size, 10.0F);
        g2.setStroke(new BasicStroke(2.0F));
        g2.fill(con);
        g2.setFont(audioFont.deriveFont(Font.PLAIN, 25.0F));
        fm = g2.getFontMetrics();
        con = switch (keyTyped) {
            case '5' ->
                    new RoundRectangle2D.Float(xPos + 10.0F, yPos + 35.0F, (float) (size - 20), 40.0F, 10.0F, 10.0F);
            case '6' ->
                    new RoundRectangle2D.Float(xPos + 10.0F, yPos + 50.0F + (float) fm.getHeight(), (float) (size - 20), 40.0F, 10.0F, 10.0F);
            case '7' ->
                    new RoundRectangle2D.Float(xPos + 10.0F, yPos + 70.0F + (float) (fm.getHeight() * 2), (float) (size - 20), 40.0F, 10.0F, 10.0F);
            case '8' ->
                    new RoundRectangle2D.Float(xPos + 10.0F, yPos + 90.0F + (float) (fm.getHeight() * 3), (float) (size - 20), 40.0F, 10.0F, 10.0F);
            case '9' ->
                    new RoundRectangle2D.Float(xPos + 10.0F, yPos + 110.0F + (float) (fm.getHeight() * 4), (float) (size - 20), 40.0F, 10.0F, 10.0F);
            default -> con;
        };
        g2.setColor(new Color(-16711773, true));
        g2.fill(con);
        g2.setPaint(keyTyped == '5' ? grad : Color.white);
        s = "BACK";
        yPos += (float) (30 + fm.getHeight());
        g2.drawString(s, xPos + (float) size / 2.0F - (float) fm.stringWidth(s) / 2.0F, yPos);
        g2.setPaint(keyTyped == '6' ? grad : Color.white);
        s = "GAME SOUND";
        yPos += (float) (15 + fm.getHeight());
        g2.drawString(s, xPos + 15.0F, yPos);
        g2.setPaint(keyTyped == '7' ? grad : Color.white);
        s = "MUSIC";
        yPos += (float) (20 + fm.getHeight());
        g2.drawString(s, xPos + 15.0F, yPos);
        g2.setPaint(keyTyped == '8' ? grad : Color.white);
        s = "HISTORY";
        yPos += (float) (20 + fm.getHeight());
        g2.drawString(s, xPos + 15.0F, yPos);
        g2.setPaint(keyTyped == '9' ? grad : Color.white);
        s = "POINTER";
        yPos += (float) (20 + fm.getHeight());
        g2.drawString(s, xPos + 15.0F, yPos);
        Shape switchCon = new RoundRectangle2D.Float((float) (con.getBounds2D().getMaxX() - 70.0),
                (float) (con.getBounds2D().getCenterY() - 12.0), 60.0F, 25.0F, 22.0F, 22.0F);
        if (keyTyped != '5') {
            grad = new GradientPaint(grad.getPoint2(), grad.getColor2(), grad.getPoint1(), grad.getColor1(), true);
            switch (keyTyped) {
                case '6':
                    g2.setPaint(isSound ? Color.black : grad);
                    g2.fill(switchCon);
                    switchCon = new Ellipse2D.Float((float) (isSound ? switchCon.getBounds2D().getMaxX() - 25.0 :
                            switchCon.getBounds2D().getX() + 5.0), (float) (switchCon.getBounds2D().getCenterY() - 10.0),
                            20.0F, 20.0F);
                    g2.setPaint(!isSound ? Color.black : grad);
                    g2.fill(switchCon);
                    break;
                case '7':
                    g2.setPaint(isMusic ? Color.black : grad);
                    g2.fill(switchCon);
                    switchCon = new Ellipse2D.Float((float) (isMusic ? switchCon.getBounds2D().getMaxX() - 25.0 :
                            switchCon.getBounds2D().getX() + 5.0), (float) (switchCon.getBounds2D().getCenterY() - 10.0),
                            20.0F, 20.0F);
                    g2.setPaint(!isMusic ? Color.black : grad);
                    g2.fill(switchCon);
                    break;
                case '8':
                    g2.setPaint((isHistory ? Color.black : grad));
                    g2.fill(switchCon);
                    switchCon = new Ellipse2D.Float((float) (isHistory ? switchCon.getBounds2D().getMaxX() - 25.0 :
                            switchCon.getBounds2D().getX() + 5.0), (float) (switchCon.getBounds2D().getCenterY() - 10.0),
                            20.0F, 20.0F);
                    g2.setPaint(!isHistory ? Color.black : grad);
                    g2.fill(switchCon);
                    break;
                case '9':
                    g2.setPaint(isPointer ? Color.black : grad);
                    g2.fill(switchCon);
                    switchCon = new Ellipse2D.Float((float) (this.isPointer ? switchCon.getBounds2D().getMaxX() - 25.0 :
                            switchCon.getBounds2D().getX() + 5.0), (float) (switchCon.getBounds2D().getCenterY() - 10.0),
                            20.0F, 20.0F);
                    g2.setPaint(!isPointer ? Color.black : grad);
                    g2.fill(switchCon);
            }
        }
    }

    private void drawMainMenu(Graphics2D g2) {
        int size = 300;
        float xPos = (float) (Main.screenSize.width - size) / 2.0F;
        float yPos = (float) (Main.screenSize.height - size) / 2.0F - 30.0F;
        g2.setStroke(new BasicStroke(3.0F));
        Shape con = new RoundRectangle2D.Float(xPos, yPos, (float) size, (float) size, 30.0F, 30.0F);
        g2.setColor(new Color(0xFF04563B, true));
        g2.fill(con);
        g2.setPaint(new GradientPaint((float) (circle += 10), (float) rand.nextInt(gameBoardHeight / 2),
                circle == Main.screenSize.width ? new Color(0xFF099B95, true) : new Color(0xFFFA6700, true),
                (float) Main.screenSize.width, 0.0F, circle < Main.screenSize.width ?
                new Color(0xFF40E8AA, true) : new Color(0xFF16B0E6, true), true));
        g2.draw(con);
        g2.setFont(audioFont.deriveFont(30.0F));
        FontMetrics fm = g2.getFontMetrics();
        String s = "Space Shooter";
        g2.drawString(s, xPos + 150.0F - (float) fm.stringWidth(s) / 2.0F, yPos += (float) (fm.getHeight() + 5));
        con = new Rectangle2D.Float(xPos, yPos += 20.0F, (float) size, 10.0F);
        g2.setStroke(new BasicStroke(2.0F));
        g2.fill(con);
        g2.setFont(audioFont.deriveFont(Font.ITALIC, 22.0F));
        fm = g2.getFontMetrics();
        con = switch (this.keyTyped) {
            case '1' ->
                    new RoundRectangle2D.Float(xPos + 10.0F, yPos + 30.0F, (float) (size - 20), 40.0F, 10.0F, 10.0F);
            case '2' ->
                    new RoundRectangle2D.Float(xPos + 10.0F, yPos + 45.0F + (float) fm.getHeight(), (float) (size - 20), 40.0F, 10.0F, 10.0F);
            case '3' ->
                    new RoundRectangle2D.Float(xPos + 10.0F, yPos + 65.0F + (float) (fm.getHeight() * 2), (float) (size - 20), 40.0F, 10.0F, 10.0F);
            case '4' ->
                    new RoundRectangle2D.Float(xPos + 10.0F, yPos + 85.0F + (float) (fm.getHeight() * 3), (float) (size - 20), 40.0F, 10.0F, 10.0F);
            default -> con;
        };

        g2.setColor(new Color(-16711773, true));
        g2.fill(con);
        g2.setColor(keyTyped == '1' ? Color.black : Color.WHITE);
        s = startTime > 0L ? "RESUME" : "START";
        yPos += (float) (30 + fm.getHeight());
        g2.drawString(s, xPos + 150.0F - (float) fm.stringWidth(s) / 2.0F, yPos);
        g2.setColor(keyTyped == '2' ? Color.black : Color.WHITE);
        s = "SETTINGS";
        yPos += (float) (15 + fm.getHeight());
        g2.drawString(s, xPos + 150.0F - (float) fm.stringWidth(s) / 2.0F, yPos);
        g2.setColor(keyTyped == '3' ? Color.black : Color.WHITE);
        s = "HELP";
        yPos += (float) (20 + fm.getHeight());
        g2.drawString(s, xPos + 150.0F - (float) fm.stringWidth(s) / 2.0F, yPos);
        g2.setColor(keyTyped == '4' ? Color.black : Color.WHITE);
        s = "EXIT";
        yPos += (float) (20 + fm.getHeight());
        g2.drawString(s, xPos + 150.0F - (float) fm.stringWidth(s) / 2.0F, yPos);
        pauseMsgDisplay(g2);
    }

    private void pauseMsgDisplay(Graphics2D g2) {
        g2.setStroke(new BasicStroke(0.0F));
        g2.setColor(new Color(0xFF598E95, true));
        g2.setFont(audioFont);
        FontMetrics fm = g2.getFontMetrics();
        String s = "CURRENT  LOCATION  :::::::::::::::::::  SOMEWHERE  IN  DEEP  SPACE";
        g2.drawString(s, 11, 30);
        s = "DIRECTION  :::  LATITUDE  ::  " + rand.nextFloat(1.0F, 10.2F);
        g2.drawString(s, 11, 30 + fm.getHeight() + 10);
        s = " LONGITUDE  ::  " + rand.nextFloat(1.0F, 10.2F);
        g2.drawString(s, 11 + fm.stringWidth("WWWWWWWWWWWWWWWWWWWWW"), 30 + fm.getHeight() + 10);
        s = "OXYGEN  LEVEL:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::VERY  CRITICAL";
        g2.drawString(s, 11, 30 + fm.getHeight() * 2 + 20);
        s = "SPEED  :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: "
            + rand.nextInt(10000, 90000);
        g2.drawString(s, 11, 30 + fm.getHeight() * 3 + 30);
        s = "SIGNAL ::::";
        g2.drawString(s, 11, 30 + fm.getHeight() * 4 + 45);
        g2.setPaint(new GradientPaint((float) (circle += 10), (float) rand.nextInt(gameBoardHeight / 2),
                circle == Main.screenSize.width ? new Color(0xFF2C120F, true) : new Color(0xFF284138, true),
                (float) Main.screenSize.width, 0.0F, circle < Main.screenSize.width ?
                new Color(0xFF161853, true) : new Color(0xFF281443, true), true));
        g2.setFont(waveFont);
        fm = g2.getFontMetrics();
        s = "";

        for (int i = 0; i < 250; ++i) {
            s = s.concat(String.valueOf((char) rand.nextInt(1, 128)));
            g2.drawString(s, 70, 30 + fm.getHeight() * 4 + 40);
        }

        Rectangle2D msgCOn = new Rectangle2D.Float(0.0F, (float) (gameBoardHeight - 30), 180.0F, 30.0F);
        g2.setFont(audioFont);
        fm = g2.getFontMetrics();
        g2.setColor(Color.WHITE);
        String msg = "AIR  SPACE  A741B!!!,  THIS  IS  HOUSTON!!,  I REPEAT,  THIS  IS HOUSTON!!." +
                     "  THIS  IS  NOT  A  TEST  TRANSMISSION,  WE  ARE CURRENTLY  FACING  A  HEAT " +
                     " WAVE  IMPACT,  MAKING  IT IMPOSSIBLE  TO  REACH  YOUR  CONNECTION. " +
                     " IF  YOU  CAN HEAR  ME,  ABORT  MI.............,  I  REPEAT  A.............  " +
                     "SION, AIR ................  A741B  I  REPEAT  ABOR......";
        g2.drawString(msg, msgXps, (int) (msgCOn.getCenterY() + (fm.getHeight() / 2)));
        msgXps--;
        if (msgXps + fm.stringWidth(msg) <= 180) {
            msgXps = Main.screenSize.width - 5;
        }

        g2.setColor(new Color(0xFF04563B, true));
        g2.fill(msgCOn);
        g2.setFont(audioFont.deriveFont(Font.BOLD, 20.0F));
        fm = g2.getFontMetrics();
        s = "M E S S A G E";
        g2.setColor(Color.WHITE);
        g2.drawString(s, (int) (msgCOn.getCenterX() - (double) (fm.stringWidth(s) / 2)),
                (int) (msgCOn.getCenterY() + (double) (fm.getHeight() / 3)));
    }

    private void restartGame() {
        isPause = true;
        gameWords.removeAll(Collections.unmodifiableList(gameWords));
        float secSpent = (float) (System.currentTimeMillis() - startTime - pauseTime) / 1000.0F;
        System.out.println("time spent on game " + secSpent + "sec");
        scores.add((float) points / secSpent);
        scores.trimToSize();
        startTime = 0L;
        pauseTime = 0L;
        expIndex = -1;
        changeExp = 0;
        pointer = null;
        points = 0;
        wordSearch = "";
        inDanger = false;
        gamePlayed++;
        keyTyped = '1';

        for (int i = 0; i < 8; ++i) {
            gameWords.add(new GameWords(totalGameWords.get(rand.nextInt(0, totalGameWords.size())), this));
        }
        repaint();
    }

    private void drawHistory(Graphics2D g2) {
        g2.setStroke(new BasicStroke(5.0F));
        g2.setFont(audioFont.deriveFont(14.0F));
        FontMetrics fm = g2.getFontMetrics();
        g2.setColor(new Color(0xFF04563B, true));
        Line2D bar = new Line2D.Float(40.0F, (float) (gameBoardHeight - 100), 40.0F, 200.0F);
        g2.draw(bar);
        g2.setColor(Color.WHITE);
        String s = "Word/Sec";
        g2.drawString(s, 21, 190);
        g2.setColor(new Color(0xFF04563B, true));
        int line = 50 * gamePlayed;
        bar.setLine(bar.getP1(), new Point(Math.min(line + 10, Main.screenSize.width - 40), (int) bar.getY1()));
        g2.draw(bar);
        g2.setColor(Color.WHITE);
        s = "Game Played";
        line += 20;
        g2.drawString(s, bar.getP2().getX() + (double) fm.stringWidth(s) + 10 > (double) Main.screenSize.width ?
                        (float) Main.screenSize.width / 2.0F - (float) fm.stringWidth(s) / 2.0F : (float) line,
                bar.getP2().getX() + (double) fm.stringWidth(s) + 10 > (double) Main.screenSize.width ?
                        (float) ((int) bar.getY1()) + (float) fm.getHeight() / 3.0F - 2.5F + fm.getHeight() + 10 :
                        (float) ((int) bar.getY1()) + (float) fm.getHeight() / 3.0F - 2.5F);
        Line2D path = new Line2D.Float(bar.getP1(), bar.getP1());
        if (!scores.isEmpty()) {
            g2.setFont(audioFont.deriveFont(10.0F));
            for (float l : this.scores) {
                path = new Line2D.Float((float) path.getX2(), (float) path.getY2(),
                        (float) path.getX2() + (float) (line / this.gamePlayed) - 20.0F, (float) bar.getY1() - l * 300.0F);
                g2.setColor(Color.black);
                g2.setStroke(new BasicStroke(4.0F));
                g2.draw(path);
                Ellipse2D ell = new Ellipse2D.Float((float) path.getX2() - 10.0F, (float) path.getY2() - 10.0F, 20.0F, 20.0F);
                g2.setColor(new Color(rand.nextInt(1, 256), rand.nextInt(1, 256), rand.nextInt(1, 256)));
                g2.fill(ell);
                g2.setColor(Color.WHITE);
                DecimalFormat df = new DecimalFormat();
                df.setMaximumFractionDigits(2);
                s = String.valueOf(df.format(l));
                g2.drawString(s, (int) (ell.getCenterX() - fm.stringWidth(s) / 2) + 4, (int) (ell.getY() - 5));
            }
        }
    }

    private void animateExplode(Graphics2D g2) {
        if (explosion != null) {
            changeExp++;
            GameWords gw = gameWords.get(expIndex);
            gw.string = "";
            gw.width = 1;
            int x = (int) (gw.getCenterX() - explosion.getWidth(null) / 2);
            int y = (int) (gw.getCenterY() - explosion.getHeight(null) / 2);
            if (changeExp >= 4 && changeExp < 7) explosion = exp2;
            if (changeExp >= 7 && changeExp < 10) explosion = exp3;
            if (changeExp >= 10) explosion = exp4;
            g2.drawImage(explosion, x, y, null);
            if (changeExp == 13) {
                gameWords.remove(expIndex);
                explosion = null;
                changeExp = 0;
            }
        }
    }
}
