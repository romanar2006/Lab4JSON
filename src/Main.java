import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<ZachetBook> zachetBooks = ZachetBook.loadStudents("students.txt");

        String[] sessionFiles = {"exam1.txt", "exam2.txt", "exam3.txt",
                "exam4.txt", "exam5.txt", "exam6.txt"};
        for (ZachetBook zachetBook : zachetBooks) {
            ZachetBook.loadSessions(zachetBook, sessionFiles);
        }

        ZachetBook.outputExcellentStudents(zachetBooks, "output.txt");
        ZachetBook.outputAllStudents(zachetBooks, "output1.txt");

        // Serialize all students and excellent students
        ZachetBook.serializeAllStudents(zachetBooks, "all_students.json");
        ZachetBook.serializeExcellentStudents(zachetBooks, "excellent_students.json");

        // Deserialize all students and excellent students
        List<ZachetBook> allStudentsFromJson = ZachetBook.deserializeAllStudents("all_students.json");
        List<ZachetBook> excellentStudentsFromJson = ZachetBook.deserializeExcellentStudents("excellent_students.json");

        // Optional: Output deserialized data to verify
        System.out.println("Deserialized all students:");
        for (ZachetBook student : allStudentsFromJson) {
            System.out.println(student);
        }

        System.out.println("\nDeserialized excellent students:");
        for (ZachetBook student : excellentStudentsFromJson) {
            System.out.println(student);
        }
    }
}
