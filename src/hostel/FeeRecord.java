package hostel;

import java.io.Serializable;
import java.time.LocalDate;

public class FeeRecord implements Serializable {
    private final String id;
    private final String studentId;
    private final double amount;
    private final String month;
    private final LocalDate recordedOn;
    private boolean paid;

    public FeeRecord(String id, String studentId, double amount, String month, boolean paid) {
        this.id = id;
        this.studentId = studentId;
        this.amount = amount;
        this.month = month;
        this.paid = paid;
        this.recordedOn = LocalDate.now();
    }

    public String getId() { return id; }
    public String getStudentId() { return studentId; }
    public double getAmount() { return amount; }
    public String getMonth() { return month; }
    public LocalDate getRecordedOn() { return recordedOn; }
    public boolean isPaid() { return paid; }
    public void setPaid(boolean paid) { this.paid = paid; }
}