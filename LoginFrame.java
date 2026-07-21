import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class LoginFrame extends JFrame {
    private JTextField txtUser;
    private JPasswordField txtPass;
    
    // Modern Web UI Colors
    private final Color COLOR_BG = new Color(245, 247, 251);
    private final Color COLOR_PRIMARY = new Color(67, 97, 238);
    private final Color COLOR_TEXT_MAIN = new Color(33, 37, 41);
    private final Color COLOR_TEXT_SUB = new Color(108, 117, 125);

    public LoginFrame() {
        setTitle("Education System - Portal Login");
        setSize(1000, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_BG);
        setLayout(new GridBagLayout());

        JPanel mainCard = new JPanel(new GridLayout(1, 2));
        mainCard.setPreferredSize(new Dimension(850, 500));
        mainCard.setBackground(Color.WHITE);
        mainCard.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));

        // LEFT SIDE: Branding
        JPanel leftSide = new JPanel(new GridBagLayout());
        leftSide.setBackground(COLOR_PRIMARY);
        JLabel lblWelcome = new JLabel("<html><div style='text-align: center;'>Welcome to<br>EduPortal</div></html>");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblWelcome.setForeground(Color.WHITE);
        leftSide.add(lblWelcome);
        mainCard.add(leftSide);

        // RIGHT SIDE: Login Form
        JPanel rightSide = new JPanel();
        rightSide.setLayout(new BoxLayout(rightSide, BoxLayout.Y_AXIS));
        rightSide.setBackground(Color.WHITE);
        rightSide.setBorder(new EmptyBorder(50, 60, 50, 60));

        JLabel lblLoginTitle = new JLabel("Login to Your Account");
        lblLoginTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblLoginTitle.setForeground(COLOR_TEXT_MAIN);
        lblLoginTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightSide.add(lblLoginTitle);

        rightSide.add(Box.createVerticalStrut(30));

        // Username
        rightSide.add(new JLabel("Username"));
        txtUser = createStyledField();
        rightSide.add(txtUser);
        rightSide.add(Box.createVerticalStrut(20));

        // Password
        rightSide.add(new JLabel("Password"));
        txtPass = new JPasswordField();
        styleField(txtPass);
        rightSide.add(txtPass);

        // Forgot Password Function
        JLabel lblForgot = new JLabel("Forgot Password?");
        lblForgot.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblForgot.setForeground(COLOR_PRIMARY);
        lblForgot.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblForgot.setAlignmentX(Component.RIGHT_ALIGNMENT);
        lblForgot.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                handleForgotPassword();
            }
        });
        rightSide.add(lblForgot);
        rightSide.add(Box.createVerticalStrut(30));

        // Login Button
        JButton btnLogin = new JButton("Login Now");
        styleButton(btnLogin);
        btnLogin.addActionListener(e -> performLogin());
        rightSide.add(btnLogin);
        rightSide.add(Box.createVerticalStrut(20));

        // Sign Up Function
        JPanel signupPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        signupPanel.setBackground(Color.WHITE);
        JLabel lblNoAcc = new JLabel("Don't have an account?");
        JLabel lblSignup = new JLabel("Sign Up");
        lblSignup.setForeground(COLOR_PRIMARY);
        lblSignup.setFont(new Font("Segoe UI Bold", Font.PLAIN, 13));
        lblSignup.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblSignup.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                handleSignUp();
            }
        });
        signupPanel.add(lblNoAcc);
        signupPanel.add(lblSignup);
        rightSide.add(signupPanel);

        mainCard.add(rightSide);
        add(mainCard);
    }

    private void styleField(JTextField tf) {
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 226, 230), 1),
            BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));
    }

    private JTextField createStyledField() {
        JTextField tf = new JTextField();
        styleField(tf);
        return tf;
    }

    private void styleButton(JButton btn) {
        btn.setBackground(COLOR_PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI Bold", Font.PLAIN, 15));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    // --- LOGIC FUNCTIONS ---

    private void performLogin() {
        String user = txtUser.getText();
        String pass = new String(txtPass.getPassword());

        try (Connection conn = DB.connect()) {
            String query = "SELECT * FROM users WHERE username=? AND password=?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, user);
            pst.setString(2, pass);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Welcome Back, " + user + "!");
                new StudentSystem(user).setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials!", "Login Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void handleSignUp() {
        JTextField userField = new JTextField();
        JPasswordField passField = new JPasswordField();
        Object[] message = { "New Username:", userField, "New Password:", passField };

        int option = JOptionPane.showConfirmDialog(this, message, "Sign Up New User", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String user = userField.getText();
            String pass = new String(passField.getPassword());

            if (!user.isEmpty() && !pass.isEmpty()) {
                try (Connection conn = DB.connect()) {
                    PreparedStatement pst = conn.prepareStatement("INSERT INTO users (username, password) VALUES (?,?)");
                    pst.setString(1, user);
                    pst.setString(2, pass);
                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Account Created Successfully! You can now login.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error: Username might already exist.");
                }
            }
        }
    }

    private void handleForgotPassword() {
        String user = JOptionPane.showInputDialog(this, "Enter your username to reset password:");
        if (user != null && !user.isEmpty()) {
            String newPass = JOptionPane.showInputDialog(this, "Enter New Password:");
            if (newPass != null && !newPass.isEmpty()) {
                try (Connection conn = DB.connect()) {
                    PreparedStatement pst = conn.prepareStatement("UPDATE users SET password=? WHERE username=?");
                    pst.setString(1, newPass);
                    pst.setString(2, user);
                    int updated = pst.executeUpdate();
                    if (updated > 0) JOptionPane.showMessageDialog(this, "Password Updated Successfully!");
                    else JOptionPane.showMessageDialog(this, "Username not found!");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}