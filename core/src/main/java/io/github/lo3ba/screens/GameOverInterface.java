package io.github.lo3ba.screens;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.prefs.Preferences;
import java.util.Random;

public class GameOverInterface {
    // Dimensions de la fenêtre
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;

    // Variable utilisée pour l'animation du score qui s'incrémente progressivement
    private static int currentDisplayedScore = 0;

    // Méthode principale pour afficher l'écran Game Over avec le score final
    public static void showGameOver(int finalScore) {
        JFrame frame = new JFrame("Game Over"); // Création de la fenêtre principale
        JPanel mainPanel = createMainPanel(finalScore); // Création du contenu principal

        frame.setContentPane(mainPanel); // Intègre le panneau dans la fenêtre
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT); // Applique les dimensions
        frame.setLocationRelativeTo(null); // Centre la fenêtre à l'écran
        frame.setVisible(true);
    }

    // Méthode qui crée le panneau principal avec les composants et animations
    private static JPanel createMainPanel(int finalScore) {
        // Panneau personnalisé pour dessiner le fond sombre + débris
        JPanel panel = new JPanel() {
            private final Point[] debris = generateDebris(); // generer des débris aléatoires

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Dessin d’un fond noir semi-transparent
                g.setColor(new Color(0, 0, 0, 200));
                g.fillRect(0, 0, getWidth(), getHeight());

                // Dessin des petits cercles gris (débris)
                g.setColor(new Color(100, 100, 100, 150));
                for (Point p : debris) {
                    g.fillOval(p.x, p.y, 8, 8);
                }
            }
        };

        //  layout flexible
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Marge autour des composants
        gbc.gridwidth = GridBagConstraints.REMAINDER; // Chaque composant prend toute la largeur

        // Titre "GAME OVER" stylisé
        JLabel gameOverLabel = createBloodyLabel("GAME OVER");
        panel.add(gameOverLabel, gbc);

        // Affichage du score (initialement à 0)
        JLabel scoreLabel = new JLabel("Score: 0", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        scoreLabel.setForeground(Color.WHITE);
        panel.add(scoreLabel, gbc);

        // Affichage du meilleur score ou du nouveau record
        JLabel highScoreLabel = new JLabel(getHighScoreText(finalScore), SwingConstants.CENTER);
        highScoreLabel.setFont(new Font("Arial", Font.BOLD, 20));
        highScoreLabel.setForeground(Color.YELLOW);
        panel.add(highScoreLabel, gbc);


        panel.add(createButtonsPanel(), gbc);

        // Lancement de l'animation d'incrémentation du score
        startScoreAnimation(scoreLabel, finalScore);

        // Lancement de l’animation du bouton "Rejouer"
        startButtonAnimation(panel);

        return panel;
    }

    // Méthode qui récupère et met à jour le meilleur score enregistré
    private static String getHighScoreText(int score) {
        Preferences prefs = Preferences.userRoot().node(GameOverInterface.class.getName());
        int highScore = prefs.getInt("highScore", 0); // Lecture du meilleur score

        // Si le nouveau score est plus grand, on le met à jour
        if (score > highScore) {
            highScore = score;
            prefs.putInt("highScore", highScore); // Sauvegarde dans les préférences
            return "Nouveau record: " + highScore + "!";
        }
        return "Meilleur score: " + highScore;
    }

    // Méthode qui anime l'affichage du score progressivement
    private static void startScoreAnimation(JLabel scoreLabel, int finalScore) {
        Timer scoreTimer = new Timer(30, e -> {
            if (currentDisplayedScore < finalScore) {
                // Calcul d’un incrément dynamique
                int increment = Math.max(1, (finalScore - currentDisplayedScore) / 5);
                currentDisplayedScore += increment;
                if (currentDisplayedScore > finalScore) {
                    currentDisplayedScore = finalScore;
                    ((Timer)e.getSource()).stop(); // Stoppe le timer une fois le score atteint
                }
                scoreLabel.setText("Score: " + currentDisplayedScore);
            }
        });
        scoreTimer.start(); // démarre le timer
    }

    // méthode qui anime visuellement le bouton "Rejouer" (effet pulsé)
    private static void startButtonAnimation(JPanel panel) {
        new Timer(100, e -> {
            for (Component c : panel.getComponents()) {
                if (c instanceof JButton && ((JButton)c).getText().equals("Rejouer")) {
                    JButton btn = (JButton)c;
                    // Calcul d’un effet de pulsation avec sinusoïde
                    float pulse = 0.5f + 0.5f * (float)Math.sin(System.currentTimeMillis() / 300.0);
                    btn.setBackground(new Color(
                        (int)(0 * pulse),
                        (int)(150 * pulse),
                        (int)(0 * pulse)
                    ));
                }
            }
            panel.repaint(); // Redessine le panneau
        }).start();
    }

    // Crée un JLabel stylisé en rouge avec une police originale
    private static JLabel createBloodyLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        String[] bloodFonts = {"Chiller", "Jokerman", "Algerian", "Arial Black"};
        Font font = null;

        for (String fontName : bloodFonts) {
            font = new Font(fontName, Font.BOLD, 48);
            if (font.getFamily().equals(fontName)) break; // Utilise la première dispo
        }

        label.setFont(font != null ? font : new Font("Arial", Font.BOLD, 48)); // Police de secours
        label.setForeground(Color.RED); // Couleur rouge sang
        label.setBorder(new EmptyBorder(10, 30, 10, 30)); // Marges autour du texte
        return label;
    }

    // Génère un tableau de 50 points aléatoires représentant les "débris"
    private static Point[] generateDebris() {
        Random rand = new Random();
        Point[] debris = new Point[50];
        for (int i = 0; i < debris.length; i++) {
            debris[i] = new Point(rand.nextInt(WIDTH), rand.nextInt(HEIGHT));
        }
        return debris;
    }

    // crée un panneau contenant les boutons "Rejouer" et "Quitter"
    private static JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panel.setOpaque(false); // Fond transparent

        JButton restartBtn = new JButton("Rejouer");
        styleButton(restartBtn, new Color(0, 150, 0), Color.WHITE);
        restartBtn.addActionListener(e -> JOptionPane.showMessageDialog(null, "Nouvelle partie !")); // Action fictive

        JButton quitBtn = new JButton("Quitter");
        styleButton(quitBtn, new Color(150, 0, 0), Color.WHITE);
        quitBtn.addActionListener(e -> System.exit(0)); // Quitte le programme

        panel.add(restartBtn);
        panel.add(quitBtn);
        return panel;
    }

    //  styler bouton
    private static void styleButton(JButton btn, Color bg, Color fg) {
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setBorder(new CompoundBorder(
            new LineBorder(bg.brighter()), // Bord clair
            new EmptyBorder(8, 20, 8, 20) // Marges internes
        ));
    }

    // Point d’entrée du programme pour tester l’interface
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> showGameOver(1850)); // Affiche l’interface avec un score fictif
    }
}
