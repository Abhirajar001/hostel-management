package hostel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Room implements Serializable {
    private final String number;
    private final int capacity;
    private final List<String> occupantIds = new ArrayList<>();

    public Room(String number, int capacity) {
        this.number = number;
        this.capacity = capacity;
    }

    public String getNumber() { return number; }
    public int getCapacity() { return capacity; }
    public List<String> getOccupantIds() { return occupantIds; }
    public int getVacancy() { return capacity - occupantIds.size(); }
    public boolean hasOccupant(String studentId) { return occupantIds.contains(studentId); }

    public void addOccupant(String studentId) {
        if (getVacancy() <= 0) throw new IllegalStateException("Room " + number + " is full.");
        if (hasOccupant(studentId)) throw new IllegalStateException("Student is already in this room.");
        occupantIds.add(studentId);
    }

    public void removeOccupant(String studentId) {
        if (!occupantIds.remove(studentId)) throw new IllegalStateException("Student is not allocated to this room.");
    }
}