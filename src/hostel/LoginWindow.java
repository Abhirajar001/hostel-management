package hostel;

import javax.swing.*;
import java.awt.*;

public class LoginWindow extends JFrame {
    private final AuthDatabase database = new AuthDatabase();
    private final Color teal = new Color(0, 122, 116);
    private final Color ink = new Color(30, 42, 52);

    public LoginWindow() {
        setTitle("Harbor House | Sign in"); setDefaultCloseOperation(EXIT_ON_CLOSE); setSize(380, 400); setLocationRelativeTo(null); getContentPane().setBackground(new Color(247, 249, 247));
        showLogin();
    }

    private void showLogin() {
        resizeAuthWindow(380, 400);
        JPanel panel = form("Welcome back", "Sign in to manage your hostel records.");
        JTextField email = new JTextField(); JPasswordField password = new JPasswordField(); JButton signIn = primary("Sign in"); JButton signUp = new JButton("Create an account"); JButton forgot = new JButton("Forgot password?");
        panel.add(field("Email address", email)); panel.add(passwordField("Password", password)); panel.add(signIn); panel.add(forgot); panel.add(signUp);
        signIn.addActionListener(event -> run(() -> { database.login(email.getText(), new String(password.getPassword())); new HostelManagementApp().setVisible(true); dispose(); }));
        signUp.addActionListener(event -> showSignUp()); forgot.addActionListener(event -> showReset()); setContentPane(panel); revalidate(); repaint();
    }

    private void showSignUp() {
        resizeAuthWindow(420, 540);
        JPanel panel = form("Create your account", "Use your hostel email and a secure password."); JTextField name = new JTextField(); JTextField email = new JTextField(); JTextField google = new JTextField(); JPasswordField password = new JPasswordField(); JPasswordField confirmation = new JPasswordField(); JButton create = primary("Create account"); JButton back = new JButton("Back to sign in");
        panel.add(field("Full name", name)); panel.add(field("Email address", email)); panel.add(field("Google ID or email (optional)", google)); panel.add(passwordField("Password", password)); panel.add(passwordField("Confirm password", confirmation)); panel.add(create); panel.add(back);
        create.addActionListener(event -> run(() -> { database.register(name.getText(), email.getText(), new String(password.getPassword()), new String(confirmation.getPassword()), google.getText()); JOptionPane.showMessageDialog(this, "Account created. You can now sign in."); showLogin(); })); back.addActionListener(event -> showLogin()); setContentPane(panel); revalidate(); repaint();
    }

    private void showReset() {
        resizeAuthWindow(400, 460);
        JPanel panel = form("Reset your password", "Enter your registered email and choose a new password."); JTextField email = new JTextField(); JPasswordField password = new JPasswordField(); JPasswordField confirmation = new JPasswordField(); JButton reset = primary("Reset password"); JButton back = new JButton("Back to sign in");
        panel.add(field("Email address", email)); panel.add(passwordField("New password", password)); panel.add(passwordField("Confirm new password", confirmation)); panel.add(reset); panel.add(back);
        reset.addActionListener(event -> run(() -> { database.resetPassword(email.getText(), new String(password.getPassword()), new String(confirmation.getPassword())); JOptionPane.showMessageDialog(this, "Password reset successfully."); showLogin(); })); back.addActionListener(event -> showLogin()); setContentPane(panel); revalidate(); repaint();
    }

    private JPanel form(String title, String subtitle) { JPanel panel = new JPanel(); panel.setBorder(BorderFactory.createEmptyBorder(22, 30, 20, 30)); panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); panel.setBackground(new Color(247, 249, 247)); JLabel heading = new JLabel("<html><div style='font-size:22px; font-weight:700; color:#1e2a34'>" + title + "</div><div style='font-size:12px; color:#63727b'>" + subtitle + "</div></html>"); heading.setAlignmentX(Component.LEFT_ALIGNMENT); panel.add(heading); panel.add(Box.createVerticalStrut(14)); return panel; }
    private void resizeAuthWindow(int width, int height) { setSize(width, height); setLocationRelativeTo(null); }
    private JPanel field(String label, JTextField input) { return labeled(label, input); }
    private JPanel passwordField(String label, JPasswordField input) { JPanel row = labeled(label, input); JButton eye = new JButton("Show"); eye.addActionListener(event -> { boolean visible = input.getEchoChar() == 0; input.setEchoChar(visible ? (char) 0 : '\u2022'); eye.setText(visible ? "Show" : "Hide"); }); row.add(eye, BorderLayout.EAST); return row; }
    private JPanel labeled(String label, JComponent input) { JPanel row = new JPanel(new BorderLayout(6, 3)); row.setOpaque(false); row.setBorder(BorderFactory.createEmptyBorder(0, 0, 7, 0)); JLabel caption = new JLabel(label); caption.setForeground(ink); row.add(caption, BorderLayout.NORTH); row.add(input, BorderLayout.CENTER); row.setAlignmentX(Component.LEFT_ALIGNMENT); return row; }
    private JButton primary(String text) { JButton button = new JButton(text); button.setBackground(teal); button.setForeground(Color.WHITE); button.setFocusPainted(false); button.setAlignmentX(Component.LEFT_ALIGNMENT); return button; }
    private void run(Runnable action) { try { action.run(); } catch (Exception exception) { JOptionPane.showMessageDialog(this, exception.getMessage(), "Could not continue", JOptionPane.WARNING_MESSAGE); } }
    public static void main(String[] args) { SwingUtilities.invokeLater(() -> new LoginWindow().setVisible(true)); }
}