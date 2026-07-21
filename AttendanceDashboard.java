import java.awt.*;
import java.awt.print.PrinterException;
import java.sql.*;
import java.time.LocalDate;
import javax.swing.*;
import javax.swing.table.*;

public class AttendanceDashboard extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JLabel lblPresent, lblAbsent;
    private JTextField txtFilterDate;
    
    // Theme Colors
    private final Color PRIMARY_BLUE = new Color(44, 62, 80);
    private final Color BG_LIGHT = new Color(245, 246, 250);
    private final Color HEADER_BG = new Color(230, 230, 230);
    private final Color BTN_FILTER = new Color(52, 152, 219); // Sky Blue
    private final Color BTN_PRINT = new Color(46, 204, 113);  // Emerald Green

    public AttendanceDashboard() {
        setTitle("Attendance Reporting & Printing System");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_LIGHT);
        setLayout(new BorderLayout(20, 20));

        // --- TOP HEADER ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_BLUE);
        header.setPreferredSize(new Dimension(0, 80));
        
        JLabel title = new JLabel("  ATTENDANCE SHEET MANAGER", JLabel.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);

        // --- FILTER & PRINT CONTROLS ---
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 20));
        controls.setOpaque(false);
        
        JLabel lblDate = new JLabel("Date (YYYY-MM-DD):");
        lblDate.setForeground(Color.WHITE);
        lblDate.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        
        txtFilterDate = new JTextField(LocalDate.now().toString(), 10);
        txtFilterDate.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtFilterDate.setHorizontalAlignment(JTextField.CENTER);
        
        // පැහැදිලිව පෙනෙන වර්ණ සහිත බොත්තම්
        JButton btnFilter = createColoredButton("FILTER DATA", BTN_FILTER);
        JButton btnPrint = createColoredButton("PRINT SHEET", BTN_PRINT);
        
        controls.add(lblDate);
        controls.add(txtFilterDate);
        controls.add(btnFilter);
        controls.add(btnPrint);
        header.add(controls, BorderLayout.EAST);
        
        add(header, BorderLayout.NORTH);

        // --- STATS CARDS (BOTTOM) ---
        JPanel statsPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 100, 20, 100));
        statsPanel.setOpaque(false);
        
        lblPresent = createCard(statsPanel, "TOTAL PRESENT", new Color(39, 174, 96));
        lblAbsent = createCard(statsPanel, "TOTAL ABSENT", new Color(192, 57, 43));
        add(statsPanel, BorderLayout.SOUTH);

        // --- TABLE AREA ---
        model = new DefaultTableModel(new String[]{"Student ID", "Full Name", "Date", "Attendance Status"}, 0);
        table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JTableHeader th = table.getTableHeader();
        th.setBackground(HEADER_BG);
        th.setForeground(Color.BLACK); 
        th.setFont(new Font("Segoe UI Bold", Font.PLAIN, 14));
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 30));
        centerPanel.setOpaque(false);
        centerPanel.add(new JScrollPane(table));
        add(centerPanel, BorderLayout.CENTER);

        // Logic
        btnFilter.addActionListener(e -> loadStats(txtFilterDate.getText()));
        btnPrint.addActionListener(e -> printSheet());

        loadStats(LocalDate.now().toString());
    }

    // බොත්තම් වලට වර්ණ සහ පෙනුම ලබා දෙන Method එක
    private JButton createColoredButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(130, 35));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE); // අකුරු සුදු පාටින්
        btn.setFont(new Font("Segoe UI Bold", Font.PLAIN, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false); // Transparancy නැති කිරීමට
        btn.setOpaque(true);         // වර්ණය ස්ථිරව පෙන්වීමට
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JLabel createCard(JPanel parent, String title, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setPreferredSize(new Dimension(250, 100));
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel t = new JLabel(title, JLabel.CENTER);
        t.setForeground(Color.WHITE);
        t.setFont(new Font("Segoe UI Bold", Font.PLAIN, 14));
        JLabel v = new JLabel("0", JLabel.CENTER);
        v.setForeground(Color.WHITE);
        v.setFont(new Font("Segoe UI Bold", Font.BOLD, 35));
        card.add(t, BorderLayout.NORTH);
        card.add(v, BorderLayout.CENTER);
        parent.add(card);
        return v;
    }

    private void printSheet() {
        try {
            table.print(JTable.PrintMode.FIT_WIDTH, 
                new java.text.MessageFormat("Attendance Report - " + txtFilterDate.getText()), 
                new java.text.MessageFormat("Page {0}"));
        } catch (PrinterException pe) {
            JOptionPane.showMessageDialog(this, "Error: " + pe.getMessage());
        }
    }

    private void loadStats(String date) {
        model.setRowCount(0);
        int p = 0, a = 0;
        try (Connection c = DB.connect()) {
            PreparedStatement ps = c.prepareStatement(
                "SELECT s.student_id, s.name, IFNULL(a.status, 0) FROM student s " +
                "LEFT JOIN attendance a ON s.student_id = a.student_id AND a.att_date = ?");
            ps.setString(1, date);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                int status = rs.getInt(3);
                String statText = (status == 1) ? "PRESENT" : "ABSENT";
                if(status == 1) p++; else a++;
                model.addRow(new Object[]{rs.getInt(1), rs.getString(2), date, statText});
            }
            lblPresent.setText(String.valueOf(p));
            lblAbsent.setText(String.valueOf(a));
        } catch (Exception e) { e.printStackTrace(); }
    }
}