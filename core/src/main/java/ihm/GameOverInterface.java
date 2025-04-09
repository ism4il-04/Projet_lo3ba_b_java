package ihm;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.prefs.Preferences;

public class GameOverInterface {
    private JPanel mainPanel;
    private JLabel gameOverLabel;
    private JLabel scoreLabel;
    private JLabel highScoreLabel;
    private JButton restartButton;
    private JButton quitButton;
    private int currentDisplayedScore = 0;

    // Constructeur pour le GUI Designer
    public GameOverInterface() {
        // L'initialisation des composants est gérée automatiquement par IntelliJ
    }

    // Votre constructeur personnalisé
    public GameOverInterface(int finalScore) {
        this(); // Appelle l'initialisation du GUI Designer

        // Sauvegarde les composants existants
        Component[] originalComponents = mainPanel.getComponents();
        LayoutManager originalLayout = mainPanel.getLayout();

        // Recrée le mainPanel avec le fond personnalisé
        mainPanel = new JPanel(originalLayout) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Fond semi-transparent
                g.setColor(new Color(0, 0, 0, 200));
                g.fillRect(0, 0, getWidth(), getHeight());

                // Dessin des débris
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(new Color(100, 100, 100, 100));
                drawDebris(g2d);
            }

            private void drawDebris(Graphics2D g) {
                int[] xPoints = {50, 70, 90, 80, 60};
                int[] yPoints = {100, 90, 100, 120, 110};
                g.fillPolygon(xPoints, yPoints, 5);

                g.fillRect(200, 150, 40, 10);
                g.fillRect(205, 140, 10, 20);
                g.fillOval(300, 200, 30, 30);
            }
        };

        // Réajoute tous les composants d'origine
        for (Component comp : originalComponents) {
            mainPanel.add(comp);
        }

        updateScores(finalScore);
        setupActions();
        applyStyles();
    }

    private void setupActions() {
        restartButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(mainPanel, "Le jeu redémarre !");
            // Ajoutez ici la logique de redémarrage
        });

        quitButton.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(mainPanel,
                "Quitter le jeu ?", "Confirmation",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
    }

    private void applyStyles() {
        // Style GAME OVER
        gameOverLabel.setFont(new Font("Arial", Font.BOLD, 48));
        gameOverLabel.setForeground(Color.RED);
        gameOverLabel.setBorder(new EmptyBorder(10, 0, 20, 0));

        // Style des scores
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        scoreLabel.setForeground(Color.WHITE);

        highScoreLabel.setFont(new Font("Arial", Font.ITALIC, 18));
        highScoreLabel.setForeground(new Color(180, 180, 180));

        // Style des boutons
        styleButton(restartButton, new Color(70, 130, 80));
        styleButton(quitButton, new Color(130, 70, 80));
    }

    private void styleButton(JButton button, Color baseColor) {
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setFocusPainted(false);
        button.setBorder(new CompoundBorder(
            new LineBorder(baseColor.brighter(), 1),
            new EmptyBorder(8, 25, 8, 25)
        ));
        button.setBackground(baseColor.darker());
        button.setForeground(Color.WHITE);
        button.setOpaque(true);

        // Effet hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(baseColor);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(baseColor.darker());
            }
        });
    }

    private void updateScores(int score) {
        Preferences prefs = Preferences.userRoot().node(this.getClass().getName());
        int highScore = prefs.getInt("highScore", 0);

        if (score > highScore) {
            highScore = score;
            prefs.putInt("highScore", highScore);
            highScoreLabel.setText("Nouveau record: " + highScore + "!");
            highScoreLabel.setForeground(new Color(255, 215, 0));
        } else {
            highScoreLabel.setText("Meilleur score: " + highScore);
        }
        scoreLabel.setText("Votre score: " + score);
    }

    public static void showGameOver(int score) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Game Over");
            GameOverInterface gameOver = new GameOverInterface(score);

            frame.setContentPane(gameOver.mainPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    public static void main(String[] args) {
        showGameOver(1850);
    }
}
