package hostel;

import java.io.*;
import java.util.*;

public class HostelService implements Serializable {
    private static final String DATA_FILE = "hostel-data.ser";
    private final Map<String, Student> students = new HashMap<>();
    private final Map<String, Room> rooms = new HashMap<>();
    private final Map<String, List<Complaint>> complaints = new HashMap<>();
    private final Map<String, List<FeeRecord>> fees = new HashMap<>();
    private int nextComplaintId = 1001;
    private int nextFeeId = 2001;

    public HostelService() {
        if (!load()) seedData();
    }

    private void seedData() {
        addStudent(new Student("STU-101", "Aarav Sharma", "Computer Science"));
        addStudent(new Student("STU-102", "Maya Patel", "Information Systems"));
        addStudent(new Student("STU-103", "Rohan Mehta", "Business Administration"));
        addRoom(new Room("A-101", 2));
        addRoom(new Room("A-102", 2));
        addRoom(new Room("B-201", 3));
        allocate("A-101", "STU-101");
        allocate("A-101", "STU-102");
        addComplaint("STU-101", "Maintenance", "Bathroom tap is leaking.");
        addFee("STU-101", 8500, "September 2024", true);
        save();
    }

    public Collection<Student> getStudents() { return students.values(); }
    public Collection<Room> getRooms() { return rooms.values(); }
    public List<Complaint> getAllComplaints() { return complaints.values().stream().flatMap(Collection::stream).toList(); }
    public List<FeeRecord> getAllFees() { return fees.values().stream().flatMap(Collection::stream).toList(); }
    public Student getStudent(String id) { return students.get(id); }
    public Room getRoom(String number) { return rooms.get(number); }

    public void addStudent(Student student) { students.put(student.getId(), student); }
    public void addRoom(Room room) { rooms.put(room.getNumber(), room); }

    public void allocate(String roomNumber, String studentId) {
        Room room = rooms.get(roomNumber);
        if (room == null || !students.containsKey(studentId)) throw new IllegalArgumentException("Select a valid room and student.");
        if (!room.hasOccupant(studentId) && room.getVacancy() <= 0) throw new IllegalStateException("Room " + roomNumber + " is full.");
        rooms.values().forEach(existing -> existing.getOccupantIds().remove(studentId));
        room.addOccupant(studentId);
        save();
    }

    public void checkout(String roomNumber, String studentId) {
        Room room = rooms.get(roomNumber);
        if (room == null) throw new IllegalArgumentException("Room not found.");
        room.removeOccupant(studentId);
        save();
    }

    public Complaint addComplaint(String studentId, String category, String description) {
        if (!students.containsKey(studentId) || description.isBlank()) throw new IllegalArgumentException("Choose a student and enter a description.");
        Complaint complaint = new Complaint("CMP-" + nextComplaintId++, studentId, category, description);
        complaints.computeIfAbsent(studentId, key -> new ArrayList<>()).add(complaint);
        save();
        return complaint;
    }

    public void updateComplaint(String complaintId, Status status) {
        getAllComplaints().stream().filter(item -> item.getId().equals(complaintId)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Complaint not found.")).setStatus(status);
        save();
    }

    public FeeRecord addFee(String studentId, double amount, String month, boolean paid) {
        if (!students.containsKey(studentId) || amount <= 0 || month.isBlank()) throw new IllegalArgumentException("Enter valid fee details.");
        FeeRecord fee = new FeeRecord("FEE-" + nextFeeId++, studentId, amount, month, paid);
        fees.computeIfAbsent(studentId, key -> new ArrayList<>()).add(fee);
        save();
        return fee;
    }

    public void toggleFee(String feeId) {
        getAllFees().stream().filter(item -> item.getId().equals(feeId)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Fee record not found.")).setPaid(true);
        save();
    }

    private boolean load() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return false;
        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(file))) {
            HostelService saved = (HostelService) input.readObject();
            students.putAll(saved.students); rooms.putAll(saved.rooms); complaints.putAll(saved.complaints); fees.putAll(saved.fees);
            nextComplaintId = saved.nextComplaintId; nextFeeId = saved.nextFeeId;
            return true;
        } catch (IOException | ClassNotFoundException exception) { return false; }
    }

    private void save() {
        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) { output.writeObject(this); }
        catch (IOException exception) { System.err.println("Could not save data: " + exception.getMessage()); }
    }
}