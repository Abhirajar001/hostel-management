package hostel;

import java.io.Serializable;
import java.time.LocalDate;

public class Complaint implements Serializable {
    private final String id;
    private final String studentId;
    private final String category;
    private final String description;
    private final LocalDate dateRaised;
    private Status status;

    public Complaint(String id, String studentId, String category, String description) {
        this.id = id;
        this.studentId = studentId;
        this.category = category;
        this.description = description;
        this.dateRaised = LocalDate.now();
        this.status = Status.OPEN;
    }

    public String getId() { return id; }
    public String getStudentId() { return studentId; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public LocalDate getDateRaised() { return dateRaised; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}