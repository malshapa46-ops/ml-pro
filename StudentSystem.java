import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;

public class StudentSystem extends JFrame {
    private JTextField txtID, txtName, txtEmail, txtPhone, txtAddress, txtDOB;
    private JComboBox<String> cmbGender;
    private JTable table;
    private DefaultTableModel model;

    private final Color PRIMARY_BLUE = new Color(44, 62, 80);
    private final Color ACCENT_BLUE = new Color(52, 152, 219);
    private final Color SUCCESS_GREEN = new Color(39, 174, 96);
    private final Color DANGER_RED = new Color(192, 57, 43);
    private final Color WARNING_ORANGE = new Color(230, 126, 34);
    private final Color BG_LIGHT = new Color(245, 246, 250);

    public StudentSystem(String user) {
        setTitle("Student Management System - Professional Dashboard");
        setSize(1280, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_LIGHT);
        setLayout(new BorderLayout(0, 0));

        // --- TOP NAVIGATION ---
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(PRIMARY_BLUE);
        topBar.setPreferredSize(new Dimension(0, 70));
        
        JLabel title = new JLabel("  STUDENT INFORMATION SYSTEM", JLabel.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        topBar.add(title, BorderLayout.WEST);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        userPanel.setOpaque(false);
        JLabel lblUser = new JLabel("User: " + user);
        lblUser.setForeground(Color.WHITE);
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        
        JButton btnLogout = createStyledButton("LOGOUT", DANGER_RED, 120, 35);
        btnLogout.addActionListener(e -> { new LoginFrame().setVisible(true); this.dispose(); });
        
        userPanel.add(lblUser);
        userPanel.add(btnLogout);
        topBar.add(userPanel, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // --- SIDEBAR FORM ---
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Color.WHITE);
        sidebar.setPreferredSize(new Dimension(380, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel formTitle = new JLabel("Manage Student Details");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        sidebar.add(formTitle);
        sidebar.add(Box.createVerticalStrut(20));

        txtID = createInputGroup(sidebar, "Student ID (Auto)");
        txtID.setEditable(false);
        txtName = createInputGroup(sidebar, "Full Name");
        txtDOB = createInputGroup(sidebar, "Date of Birth (YYYY-MM-DD)");
        
        sidebar.add(new JLabel("Gender"));
        cmbGender = new JComboBox<>(new String[]{"Male", "Female"});
        cmbGender.setMaximumSize(new Dimension(400, 35));
        sidebar.add(cmbGender);
        sidebar.add(Box.createVerticalStrut(15));

        txtAddress = createInputGroup(sidebar, "Residential Address");
        txtPhone = createInputGroup(sidebar, "Phone Number");
        txtEmail = createInputGroup(sidebar, "Email Address");

        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        btnPanel.setOpaque(false);
        btnPanel.setMaximumSize(new Dimension(400, 100));

        JButton btnSave = createStyledButton("SAVE", SUCCESS_GREEN, 150, 40);
        JButton btnUpdate = createStyledButton("UPDATE", ACCENT_BLUE, 150, 40);
        JButton btnDelete = createStyledButton("DELETE", DANGER_RED, 150, 40);
        JButton btnClear = createStyledButton("CLEAR", Color.GRAY, 150, 40);

        btnPanel.add(btnSave); btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete); btnPanel.add(btnClear);
        sidebar.add(btnPanel);

        sidebar.add(Box.createVerticalStrut(20));
        JButton btnReport = createStyledButton("VIEW ATTENDANCE REPORT", WARNING_ORANGE, 320, 45);
        btnReport.addActionListener(e -> new AttendanceDashboard().setVisible(true));
        sidebar.add(btnReport);

        add(sidebar, BorderLayout.WEST);

        // --- TABLE AREA ---
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        tablePanel.setOpaque(false);

        String[] cols = {"ID", "Name", "D.O.B", "Gender", "Address", "Phone", "Email", "Present?"};
        model = new DefaultTableModel(cols, 0) {
            @Override public Class<?> getColumnClass(int c) { return c == 7 ? Boolean.class : Object.class; }
            @Override public boolean isCellEditable(int r, int c) { return c == 7; }
        };
        table = new JTable(model);
        
        // TABLE HEADER STYLING (Set to Black Text)
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(230, 230, 230)); // Light Gray Header Background
        header.setForeground(Color.BLACK);             // BLACK TEXT FOR HEADER
        header.setFont(new Font("Segoe UI Bold", Font.PLAIN, 14));
        
        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);
        add(tablePanel, BorderLayout.CENTER);

        btnSave.addActionListener(e -> save());
        btnUpdate.addActionListener(e -> update());
        btnDelete.addActionListener(e -> delete());
        btnClear.addActionListener(e -> clear());

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int r = table.getSelectedRow();
                txtID.setText(model.getValueAt(r, 0).toString());
                txtName.setText(model.getValueAt(r, 1).toString());
                txtDOB.setText(model.getValueAt(r, 2).toString());
                cmbGender.setSelectedItem(model.getValueAt(r, 3).toString());
                txtAddress.setText(model.getValueAt(r, 4).toString());
                txtPhone.setText(model.getValueAt(r, 5).toString());
                txtEmail.setText(model.getValueAt(r, 6).toString());
                if (table.getSelectedColumn() == 7) {
                    updateAttStatus((int)model.getValueAt(r, 0), (boolean)model.getValueAt(r, 7));
                }
            }
        });

        loadData();
    }

    private JTextField createInputGroup(JPanel p, String label) {
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
        p.add(l);
        JTextField tf = new JTextField();
        tf.setMaximumSize(new Dimension(400, 35));
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY), 
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        p.add(tf);
        p.add(Box.createVerticalStrut(10));
        return tf;
    }

    private JButton createStyledButton(String text, Color bg, int width, int height) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(null);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(width, height));
        btn.setMaximumSize(new Dimension(width, height));
        return btn;
    }

    private void loadData() {
        model.setRowCount(0);
        try (Connection c = DB.connect()) {
            ResultSet rs = c.createStatement().executeQuery("SELECT s.*, IFNULL(a.status, 0) FROM student s LEFT JOIN attendance a ON s.student_id = a.student_id AND a.att_date = CURDATE()");
            while(rs.next()) model.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getInt(8)==1});
        } catch (Exception e) {}
    }

    private void save() {
        try (Connection c = DB.connect()) {
            PreparedStatement ps = c.prepareStatement("INSERT INTO student (name, dob, gender, address, phone, email) VALUES (?,?,?,?,?,?)");
            ps.setString(1, txtName.getText()); ps.setString(2, txtDOB.getText()); ps.setString(3, cmbGender.getSelectedItem().toString());
            ps.setString(4, txtAddress.getText()); ps.setString(5, txtPhone.getText()); ps.setString(6, txtEmail.getText());
            ps.executeUpdate(); loadData(); clear();
            JOptionPane.showMessageDialog(this, "Saved Successfully!");
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void update() {
        if(txtID.getText().isEmpty()) return;
        try (Connection c = DB.connect()) {
            PreparedStatement ps = c.prepareStatement("UPDATE student SET name=?, dob=?, gender=?, address=?, phone=?, email=? WHERE student_id=?");
            ps.setString(1, txtName.getText()); ps.setString(2, txtDOB.getText()); ps.setString(3, cmbGender.getSelectedItem().toString());
            ps.setString(4, txtAddress.getText()); ps.setString(5, txtPhone.getText()); ps.setString(6, txtEmail.getText());
            ps.setInt(7, Integer.parseInt(txtID.getText()));
            ps.executeUpdate(); loadData();
            JOptionPane.showMessageDialog(this, "Updated!");
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void delete() {
        if(txtID.getText().isEmpty()) return;
        int opt = JOptionPane.showConfirmDialog(this, "Are you sure?", "Confirm", JOptionPane.YES_NO_OPTION);
        if(opt == 0) {
            try (Connection c = DB.connect()) {
                c.createStatement().execute("DELETE FROM attendance WHERE student_id=" + txtID.getText());
                c.createStatement().execute("DELETE FROM student WHERE student_id=" + txtID.getText());
                loadData(); clear();
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    private void updateAttStatus(int id, boolean s) {
        try (Connection c = DB.connect()) {
            PreparedStatement ps = c.prepareStatement("INSERT INTO attendance (student_id, att_date, status) VALUES (?, CURDATE(), ?) ON DUPLICATE KEY UPDATE status=?");
            int v = s ? 1 : 0; ps.setInt(1, id); ps.setInt(2, v); ps.setInt(3, v);
            ps.executeUpdate();
        } catch (Exception e) {}
    }

    private void clear() {
        txtID.setText(""); txtName.setText(""); txtDOB.setText(""); txtAddress.setText(""); txtPhone.setText(""); txtEmail.setText("");
    }
}