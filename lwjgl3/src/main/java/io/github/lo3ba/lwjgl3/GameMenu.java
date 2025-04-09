package io.github.lo3ba.lwjgl3;

import javax.swing.*;
import java.awt.*;

public class GameMenu extends JFrame {

    public GameMenu() {
        System.out.println("Creating GameMenu...");
        // Configuration de la fenêtre
        setTitle("Mon Jeu Vidéo");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrer la fenêtre

        // Création d'un panel personnalisé avec image de fond
        JPanel backgroundPanel = new JPanel() {
            private Image backgroundImage;

            {
                // Charger l'image de fond
                try {
                    backgroundImage = new ImageIcon("/Users/mac/IdeaProjects/Projet_lo3ba_b_java/UI/src/img.png").getImage();
                } catch (Exception e) {
                    System.err.println("Erreur de chargement de l'image de fond");
                    e.printStackTrace();
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };

        backgroundPanel.setLayout(new GridBagLayout());

        // Création du bouton Start
        JButton startButton = new JButton("Play");
        startButton.setFont(new Font("", Font.BOLD, 36));
        startButton.setBackground(new Color(50, 150, 50));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        // Action du bouton : lancer le jeu LibGDX
        startButton.addActionListener(e -> {
            dispose(); // Fermer le menu
            System.out.println("Bouton cliqué ! Lancement du jeu...");
            new Thread(() -> {
                Lwjgl3Launcher.main(new String[]{});
            }).start();
        });

        // Ajout du bouton au panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(0, 0, 400, 0); // Marge en bas
        backgroundPanel.add(startButton, gbc);

        // Ajout du panel à la fenêtre
        add(backgroundPanel);
        setVisible(true);
        System.out.println("Window should be visible now");
    }

    public static void main(String[] args) {
        // Toujours utiliser l'EDT pour Swing
        SwingUtilities.invokeLater(GameMenu::new);
    }
}
