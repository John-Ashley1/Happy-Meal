import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class IntroScreen extends JFrame {

    private JPanel mainPanel;
    private JButton startButton;

    public IntroScreen() {
        setTitle("Happy Meal");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPanel = new JPanel() {
            private Image[] backgrounds;
            private int currentBg = 0;

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
                g.drawImage(backgrounds[currentBg], 0, 0, getWidth(), getHeight(), this);

                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(new Color(0, 0, 0, 120));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(100, 50, 100, 50));

        TitleText title = new TitleText();
        title.setPreferredSize(new Dimension(800, 200));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        startButton = new JButton("START");
        startButton.setFont(new Font("Arial", Font.BOLD, 24));
        startButton.setBackground(new Color(220, 20, 60));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        startButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 100, 100), 2),
                new EmptyBorder(15, 40, 15, 40)
        ));

        startButton.addActionListener(e -> {
            new HappyMealGame().setVisible(true);
            dispose();
        });

        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(title);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 40)));
        mainPanel.add(startButton);
        mainPanel.add(Box.createVerticalGlue());

        setContentPane(mainPanel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new IntroScreen().setVisible(true));
    }

    class TitleText extends JComponent {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            String text = "HAPPY MEAL";
            Font font = new Font("Impact", Font.BOLD, 100);
            g2d.setFont(font);

            FontMetrics fm = g2d.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(text)) / 2;
            int y = getHeight() / 2 + fm.getAscent() / 2;

            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.drawString(text, x + 6, y + 6);

            g2d.setPaint(new GradientPaint(
                    0, 0, Color.YELLOW,
                    0, getHeight(), new Color(255, 140, 0)
            ));
            g2d.drawString(text, x, y);

            g2d.setColor(new Color(150, 0, 0));
            g2d.setStroke(new BasicStroke(3f));

            Shape outline = font.createGlyphVector(
                    g2d.getFontRenderContext(), text
            ).getOutline(x, y);

            g2d.draw(outline);
        }
    }
}