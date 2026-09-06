package hostel;

import java.io.Serializable;

public class Student implements Serializable {
    private final String id;
    private final String name;
    private final String course;

    public Student(String id, String name, String course) {
        this.id = id;
        this.name = name;
        this.course = course;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getCourse() { return course; }

    @Override
    public String toString() {
        return id + " - " + name;
    }
}