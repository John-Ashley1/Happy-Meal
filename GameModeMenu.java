import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import com.ror.engine.SoundManager;

public class GameModeMenu extends JFrame implements ActionListener {

    private JPanel mainPanel;
    private JButton pvp, ai, arcade, backButton;
    private String playerName;

    private Image[] backgrounds;
    private int currentBg = 0;

    private SoundManager sound;

    public GameModeMenu(String name) {
        this.playerName = name;
        this.sound = null;
        initUI();
    }

    public GameModeMenu(String name, SoundManager sound) {
        this.playerName = name;
        this.sound = sound;
        initUI();
    }

    private void initUI() {

        setTitle("Happy Meal Tournament - Select Game Mode");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPanel = new JPanel(new GridBagLayout()) {

            {
                backgrounds = new Image[]{
                        new ImageIcon(getClass().getResource("/images/BG/bg_4.png")).getImage(),
                        new ImageIcon(getClass().getResource("/images/BG/bg_f_15.png")).getImage(),
                        new ImageIcon(getClass().getResource("/images/BG/bg_m_1.png")).getImage(),
                        new ImageIcon(getClass().getResource("/images/BG/bg_r_15.png")).getImage(),
                        new ImageIcon(getClass().getResource("/images/BG/bg_w_4.png")).getImage()
                };

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
                g2d.setColor(new Color(0, 0, 0, 140));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        setContentPane(mainPanel);

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false);

        JLabel title = new JLabel("CHOOSE GAME MODE");
        title.setFont(new Font("Monospaced", Font.BOLD, 36));
        title.setForeground(new Color(255, 215, 0));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        pvp = new JButton("PLAYER VS PLAYER");
        ai = new JButton("PLAYER VS AI");
        arcade = new JButton("ARCADE MODE");
        backButton = new JButton("BACK");

        Color actionColor = new Color(220, 20, 60);

        styleButton(pvp, actionColor);
        styleButton(ai, actionColor);
        styleButton(arcade, actionColor);
        styleButton(backButton, new Color(70, 70, 80));

        pvp.addActionListener(this);
        ai.addActionListener(this);
        arcade.addActionListener(this);

        backButton.addActionListener(e -> {
            new HappyMealGame(sound).setVisible(true);
            dispose();
        });

        menuPanel.add(title);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 50)));
        menuPanel.add(pvp);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        menuPanel.add(ai);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        menuPanel.add(arcade);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 60)));
        menuPanel.add(backButton);

        mainPanel.add(menuPanel);
    }

    private void styleButton(JButton btn, Color bgColor) {
        btn.setFont(new Font("Monospaced", Font.BOLD, 20));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);

        Dimension size = new Dimension(350, 50);
        btn.setPreferredSize(size);
        btn.setMaximumSize(size);
        btn.setMinimumSize(size);

        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.brighter(), 2),
                new EmptyBorder(10, 20, 10, 20)
        ));
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String selectedMode = "";
        String difficulty = "Medium";

        if (e.getSource() == pvp) {
            selectedMode = "PvP";

        } else if (e.getSource() == ai) {
            selectedMode = "PvAI";

            String[] options = {"Easy", "Medium", "Hard"};
            difficulty = (String) JOptionPane.showInputDialog(
                    this,
                    "Select AI Difficulty:",
                    "Difficulty",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    "Medium"
            );

            if (difficulty == null) difficulty = "Medium";

        } else if (e.getSource() == arcade) {
            selectedMode = "Arcade";
        }

        new HeroSelection(playerName, selectedMode, difficulty, sound).setVisible(true);
        dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new GameModeMenu("TestPlayer").setVisible(true)
        );
    }
}