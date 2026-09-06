package hostel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Comparator;

public class HostelManagementApp extends JFrame {
    private final HostelService service = new HostelService();
    private final Color ink = new Color(30, 42, 52);
    private final Color teal = new Color(0, 122, 116);
    private final Color paper = new Color(247, 249, 247);

    public HostelManagementApp() {
        setTitle("Harbor House | Hostel Management"); setDefaultCloseOperation(EXIT_ON_CLOSE); setSize(1120, 720); setLocationRelativeTo(null);
        getContentPane().setBackground(paper);
        JTabbedPane tabs = new JTabbedPane(); tabs.setFont(new Font("SansSerif", Font.BOLD, 14));
        tabs.addTab("Overview", overview()); tabs.addTab("Room allocation", rooms()); tabs.addTab("Complaints", complaints()); tabs.addTab("Fees", fees());
        add(tabs);
    }

    private JPanel base(String title, String subtitle) {
        JPanel panel = new JPanel(new BorderLayout(18, 18)); panel.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28)); panel.setBackground(paper);
        JLabel heading = new JLabel("<html><div style='font-size:24px; font-weight:700; color:#1e2a34'>" + title + "</div><div style='font-size:13px; color:#63727b'>" + subtitle + "</div></html>");
        panel.add(heading, BorderLayout.NORTH); return panel;
    }

    private JPanel overview() {
        JPanel panel = base("Good morning, warden", "A clear view of today's hostel operations.");
        JPanel cards = new JPanel(new GridLayout(1, 4, 14, 0)); cards.setOpaque(false);
        long occupied = service.getRooms().stream().mapToLong(room -> room.getOccupantIds().size()).sum();
        long capacity = service.getRooms().stream().mapToLong(Room::getCapacity).sum();
        cards.add(stat("OCCUPANCY", occupied + " / " + capacity, "beds in use")); cards.add(stat("VACANCIES", String.valueOf(capacity - occupied), "beds available"));
        cards.add(stat("OPEN ISSUES", String.valueOf(service.getAllComplaints().stream().filter(item -> item.getStatus() != Status.RESOLVED).count()), "need attention"));
        cards.add(stat("PENDING FEES", String.valueOf(service.getAllFees().stream().filter(item -> !item.isPaid()).count()), "records to follow up"));
        panel.add(cards, BorderLayout.CENTER); return panel;
    }

    private JPanel stat(String label, String value, String note) {
        JPanel card = new JPanel(new GridLayout(3, 1)); card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(218, 226, 222)), BorderFactory.createEmptyBorder(15, 17, 12, 17))); card.setBackground(Color.WHITE);
        JLabel top = new JLabel(label); top.setForeground(teal); top.setFont(new Font("SansSerif", Font.BOLD, 11)); JLabel number = new JLabel(value); number.setForeground(ink); number.setFont(new Font("SansSerif", Font.BOLD, 27)); JLabel small = new JLabel(note); small.setForeground(new Color(99, 114, 123)); return addLabels(card, top, number, small);
    }

    private JPanel addLabels(JPanel panel, JLabel... labels) { for (JLabel label : labels) panel.add(label); return panel; }
    private JButton button(String text) { JButton button = new JButton(text); button.setBackground(teal); button.setForeground(Color.WHITE); button.setFocusPainted(false); return button; }
    private JComboBox<String> students() { JComboBox<String> box = new JComboBox<>(); service.getStudents().stream().sorted(Comparator.comparing(Student::getId)).forEach(student -> box.addItem(student.getId())); return box; }

    private JPanel rooms() {
        JPanel panel = base("Room allocation", "Assign a resident to a room or release their bed at checkout.");
        DefaultTableModel model = new DefaultTableModel(new String[]{"Room", "Capacity", "Occupied", "Vacancy", "Residents"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(model); refreshRooms(model); panel.add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT)); controls.setOpaque(false); JComboBox<String> roomBox = new JComboBox<>(); service.getRooms().stream().sorted(Comparator.comparing(Room::getNumber)).forEach(room -> roomBox.addItem(room.getNumber())); JComboBox<String> studentBox = students(); JButton allocate = button("Allocate bed"); JButton checkout = new JButton("Checkout");
        allocate.addActionListener(event -> action(() -> { service.allocate((String) roomBox.getSelectedItem(), (String) studentBox.getSelectedItem()); refreshRooms(model); })); checkout.addActionListener(event -> action(() -> { service.checkout((String) roomBox.getSelectedItem(), (String) studentBox.getSelectedItem()); refreshRooms(model); }));
        controls.add(new JLabel("Room")); controls.add(roomBox); controls.add(new JLabel("Student ID")); controls.add(studentBox); controls.add(allocate); controls.add(checkout); panel.add(controls, BorderLayout.SOUTH); return panel;
    }

    private void refreshRooms(DefaultTableModel model) { model.setRowCount(0); service.getRooms().stream().sorted(Comparator.comparing(Room::getNumber)).forEach(room -> model.addRow(new Object[]{room.getNumber(), room.getCapacity(), room.getOccupantIds().size(), room.getVacancy(), room.getOccupantIds().stream().map(id -> service.getStudent(id).getName()).toList()})); }

    private JPanel complaints() {
        JPanel panel = base("Complaint desk", "Track maintenance and resident requests from open to resolved."); DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Student", "Category", "Description", "Raised", "Status"}, 0); JTable table = new JTable(model); refreshComplaints(model); panel.add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT)); controls.setOpaque(false); JComboBox<String> studentBox = students(); JTextField category = new JTextField("Maintenance", 12); JTextField description = new JTextField(24); JButton add = button("Log complaint"); JButton resolve = new JButton("Resolve selected");
        add.addActionListener(event -> action(() -> { service.addComplaint((String) studentBox.getSelectedItem(), category.getText(), description.getText()); refreshComplaints(model); description.setText(""); })); resolve.addActionListener(event -> action(() -> { int row = table.getSelectedRow(); if (row < 0) throw new IllegalArgumentException("Select a complaint first."); service.updateComplaint((String) model.getValueAt(row, 0), Status.RESOLVED); refreshComplaints(model); }));
        controls.add(new JLabel("Student")); controls.add(studentBox); controls.add(new JLabel("Category")); controls.add(category); controls.add(description); controls.add(add); controls.add(resolve); panel.add(controls, BorderLayout.SOUTH); return panel;
    }

    private void refreshComplaints(DefaultTableModel model) { model.setRowCount(0); service.getAllComplaints().forEach(item -> model.addRow(new Object[]{item.getId(), item.getStudentId(), item.getCategory(), item.getDescription(), item.getDateRaised(), item.getStatus()})); }

    private JPanel fees() {
        JPanel panel = base("Fee ledger", "Record monthly charges and keep payment status current."); DefaultTableModel model = new DefaultTableModel(new String[]{"Receipt", "Student", "Month", "Amount", "Recorded", "Status"}, 0); JTable table = new JTable(model); refreshFees(model); panel.add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT)); controls.setOpaque(false); JComboBox<String> studentBox = students(); JTextField amount = new JTextField(8); JTextField month = new JTextField("September 2024", 14); JButton add = button("Add fee"); JButton paid = new JButton("Mark paid");
        add.addActionListener(event -> action(() -> { service.addFee((String) studentBox.getSelectedItem(), Double.parseDouble(amount.getText()), month.getText(), false); refreshFees(model); amount.setText(""); })); paid.addActionListener(event -> action(() -> { int row = table.getSelectedRow(); if (row < 0) throw new IllegalArgumentException("Select a fee record first."); service.toggleFee((String) model.getValueAt(row, 0)); refreshFees(model); }));
        controls.add(new JLabel("Student")); controls.add(studentBox); controls.add(new JLabel("Amount")); controls.add(amount); controls.add(new JLabel("Month")); controls.add(month); controls.add(add); controls.add(paid); panel.add(controls, BorderLayout.SOUTH); return panel;
    }

    private void refreshFees(DefaultTableModel model) { model.setRowCount(0); service.getAllFees().forEach(item -> model.addRow(new Object[]{item.getId(), item.getStudentId(), item.getMonth(), String.format("%.2f", item.getAmount()), item.getRecordedOn(), item.isPaid() ? "PAID" : "PENDING"})); }
    private void action(Runnable command) { try { command.run(); } catch (Exception exception) { JOptionPane.showMessageDialog(this, exception.getMessage(), "Action could not be completed", JOptionPane.WARNING_MESSAGE); } }

    public static void main(String[] args) { SwingUtilities.invokeLater(() -> new LoginWindow().setVisible(true)); }
}