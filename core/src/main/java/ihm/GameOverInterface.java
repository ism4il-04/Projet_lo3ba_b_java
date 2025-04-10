package ihm;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.prefs.Preferences;
import java.util.Random;

public class GameOverInterface {
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;
    private static int currentDisplayedScore = 0;

    public static void showGameOver(int finalScore) {
        JFrame frame = new JFrame("Game Over");
        JPanel mainPanel = createMainPanel(finalScore);

        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static JPanel createMainPanel(int finalScore) {
        JPanel panel = new JPanel() {
            private final Point[] debris = generateDebris();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(0, 0, 0, 200));
                g.fillRect(0, 0, getWidth(), getHeight());

                g.setColor(new Color(100, 100, 100, 150));
                for (Point p : debris) {
                    g.fillOval(p.x, p.y, 8, 8);
                }
            }
        };
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        JLabel gameOverLabel = createBloodyLabel("GAME OVER");
        panel.add(gameOverLabel, gbc);

        JLabel scoreLabel = new JLabel("Score: 0", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        scoreLabel.setForeground(Color.WHITE);
        panel.add(scoreLabel, gbc);

        JLabel highScoreLabel = new JLabel(getHighScoreText(finalScore), SwingConstants.CENTER);
        highScoreLabel.setFont(new Font("Arial", Font.BOLD, 20));
        highScoreLabel.setForeground(Color.YELLOW);
        panel.add(highScoreLabel, gbc);

        panel.add(createButtonsPanel(), gbc);

        startScoreAnimation(scoreLabel, finalScore);
        startButtonAnimation(panel);

        return panel;
    }

    private static String getHighScoreText(int score) {
        Preferences prefs = Preferences.userRoot().node(GameOverInterface.class.getName());
        int highScore = prefs.getInt("highScore", 0);

        if (score > highScore) {
            highScore = score;
            prefs.putInt("highScore", highScore);
            return "Nouveau record: " + highScore + "!";
        }
        return "Meilleur score: " + highScore;
    }

    private static void startScoreAnimation(JLabel scoreLabel, int finalScore) {
        Timer scoreTimer = new Timer(30, e -> {
            if (currentDisplayedScore < finalScore) {
                int increment = Math.max(1, (finalScore - currentDisplayedScore) / 5);
                currentDisplayedScore += increment;
                if (currentDisplayedScore > finalScore) {
                    currentDisplayedScore = finalScore;
                    ((Timer)e.getSource()).stop();
                }
                scoreLabel.setText("Score: " + currentDisplayedScore);
            }
        });
        scoreTimer.start();
    }

    private static void startButtonAnimation(JPanel panel) {
        new Timer(100, e -> {
            for (Component c : panel.getComponents()) {
                if (c instanceof JButton && ((JButton)c).getText().equals("Rejouer")) {
                    JButton btn = (JButton)c;
                    float pulse = 0.5f + 0.5f * (float)Math.sin(System.currentTimeMillis() / 300.0);
                    btn.setBackground(new Color(
                        (int)(0 * pulse),
                        (int)(150 * pulse),
                        (int)(0 * pulse)
                    ));
                }
            }
            panel.repaint();
        }).start();
    }

    private static JLabel createBloodyLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        String[] bloodFonts = {"Chiller", "Jokerman", "Algerian", "Arial Black"};
        Font font = null;

        for (String fontName : bloodFonts) {
            font = new Font(fontName, Font.BOLD, 48);
            if (font.getFamily().equals(fontName)) break;
        }

        label.setFont(font != null ? font : new Font("Arial", Font.BOLD, 48));
        label.setForeground(Color.RED);
        label.setBorder(new EmptyBorder(10, 30, 10, 30));
        return label;
    }

    private static Point[] generateDebris() {
        Random rand = new Random();
        Point[] debris = new Point[20];
        for (int i = 0; i < debris.length; i++) {
            debris[i] = new Point(rand.nextInt(WIDTH), rand.nextInt(HEIGHT));
        }
        return debris;
    }

    private static JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panel.setOpaque(false);

        JButton restartBtn = new JButton("Rejouer");
        styleButton(restartBtn, new Color(0, 150, 0), Color.WHITE);
        restartBtn.addActionListener(e -> JOptionPane.showMessageDialog(null, "Nouvelle partie !"));

        JButton quitBtn = new JButton("Quitter");
        styleButton(quitBtn, new Color(150, 0, 0), Color.WHITE);
        quitBtn.addActionListener(e -> System.exit(0));

        panel.add(restartBtn);
        panel.add(quitBtn);
        return panel;
    }

    private static void styleButton(JButton btn, Color bg, Color fg) {
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setBorder(new CompoundBorder(
            new LineBorder(bg.brighter()),
            new EmptyBorder(8, 20, 8, 20)
        ));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> showGameOver(1850));
    }
}
