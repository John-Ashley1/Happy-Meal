package com.ror.gameutil;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import com.ror.engine.SoundManager;

public class HappyMealGame extends JFrame implements ActionListener {

    private JPanel mainPanel;
    private JTextField nameField, ageField;
    private JButton confirmButton;

    private Image[] backgrounds;
    private int currentBg = 0;

    private SoundManager sound;

    public HappyMealGame() {

        sound = new SoundManager();
        sound.setFile(SoundManager.BGM_MAIN);
        sound.loop();

        initUI();
    }

    public HappyMealGame(SoundManager sound) {
        this.sound = sound;
        initUI();
    }

    private void initUI() {

        setTitle("Player Registration");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        loadBackgrounds();

        mainPanel = new JPanel(new GridBagLayout()) {

            {
                new Timer(2000, e -> {
                    currentBg = (currentBg + 1) % backgrounds.length;
                    repaint();
                }).start();
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                g.drawImage(backgrounds[currentBg], 0, 0,
                        getWidth(), getHeight(), this);

                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(new Color(0, 0, 0, 120));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        setContentPane(mainPanel);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);

        Font titleFont = new Font("Monospaced", Font.BOLD, 28);
        Font labelFont = new Font("Monospaced", Font.BOLD, 18);
        Font inputFont = new Font("Monospaced", Font.BOLD, 20);

        Color arcadeGold = new Color(255, 215, 0);
        Color terminalGreen = new Color(50, 255, 50);

        JLabel title = new JLabel("Welcome to the Happy Meal Tournament");
        title.setFont(titleFont);
        title.setForeground(arcadeGold);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLabel = new JLabel("ENTER NAME:");
        nameLabel.setFont(labelFont);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        nameField = new JTextField(15);
        styleField(nameField, inputFont, arcadeGold, terminalGreen);

        JLabel ageLabel = new JLabel("ENTER AGE:");
        ageLabel.setFont(labelFont);
        ageLabel.setForeground(Color.WHITE);
        ageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        ageField = new JTextField(15);
        styleField(ageField, inputFont, arcadeGold, terminalGreen);

        confirmButton = new JButton("CONFIRM");
        confirmButton.setFont(new Font("Monospaced", Font.BOLD, 20));
        confirmButton.setBackground(new Color(220, 20, 60));
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setFocusPainted(false);
        confirmButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        confirmButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        confirmButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 100, 100), 2),
                new EmptyBorder(12, 30, 12, 30)
        ));

        confirmButton.addActionListener(this);

        formPanel.add(title);
        formPanel.add(Box.createRigidArea(new Dimension(0, 40)));
        formPanel.add(nameLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(nameField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        formPanel.add(ageLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(ageField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 50)));
        formPanel.add(confirmButton);

        mainPanel.add(formPanel);
    }

    private void loadBackgrounds() {
        backgrounds = new Image[]{
                new ImageIcon(getClass().getResource("/images/BG/bg_4.png")).getImage(),
                new ImageIcon(getClass().getResource("/images/BG/bg_f_15.png")).getImage(),
                new ImageIcon(getClass().getResource("/images/BG/bg_m_1.png")).getImage(),
                new ImageIcon(getClass().getResource("/images/BG/bg_r_15.png")).getImage(),
                new ImageIcon(getClass().getResource("/images/BG/bg_w_4.png")).getImage()
        };
    }

    private void styleField(JTextField field, Font font, Color borderColor, Color textColor) {
        field.setFont(font);
        field.setBackground(new Color(10, 10, 15));
        field.setForeground(textColor);
        field.setCaretColor(textColor);
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setMaximumSize(new Dimension(300, 45));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(borderColor, 2),
                new EmptyBorder(5, 10, 5, 10)
        ));
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String name = nameField.getText().trim();
        String age = ageField.getText().trim();

        if (name.isEmpty() || age.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both Name and Age.");
            return;
        }

        try {
            int ageNumber = Integer.parseInt(age);
            if (ageNumber <= 0) throw new NumberFormatException();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Enter a valid age.");
            return;
        }

        JOptionPane.showMessageDialog(this,
                "Welcome " + name + "!");

        new GameModeMenu(name, sound).setVisible(true);

        dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HappyMealGame().setVisible(true));
    }
}