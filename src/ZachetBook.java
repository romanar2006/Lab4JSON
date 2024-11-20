import java.io.*;
import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
class ZachetBook {
    @JsonProperty("surname")
    private String surname;

    @JsonProperty("name")
    private String name;

    @JsonProperty("patronymic")
    private String patronymic;

    @JsonProperty("gradebookNumber")
    private int gradebookNumber;

    @JsonProperty("course")
    private int course;

    @JsonProperty("groupNumber")
    private int groupNumber;

    @JsonProperty("sessions")
    private List<Session> sessions;

    public ZachetBook() {
        sessions = new ArrayList<>();
    }

    public ZachetBook(String surname, String name, String patronymic, int course, int groupNumber, int gradebookNumber) {
        this.surname = surname;
        this.name = name;
        this.patronymic = patronymic;
        this.course = course;
        this.groupNumber = groupNumber;
        this.gradebookNumber = gradebookNumber;
        this.sessions = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "ZachetBook {Full Name: " + surname + " " + name + " " + patronymic +
                ", Gradebook Number: " + gradebookNumber +
                ", Course: " + course +
                ", Group: " + groupNumber +
                ", Sessions: " + sessions + "}";
    }

    static class Session {
        @JsonProperty ("subject")
        private String subject;
        @JsonProperty ("sessionNumber")
        private int sessionNumber;
        @JsonProperty ("exams")
        private List<Exam> exams;

        public Session() {
        }

        public Session(String subject, int sessionNumber) {
            this.subject = subject;
            this.sessionNumber = sessionNumber;
            this.exams = new ArrayList<>();
        }

        public void addExam(int studentId, int grade) {
            exams.add(new Exam(studentId, grade));
        }

        public String getSubject() {
            return subject;
        }

        public int getSessionNumber() {
            return sessionNumber;
        }

        public List<Exam> getExams() {
            return exams;
        }

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder();
            result.append("Сессия ").append(sessionNumber).append(":\n");

            for (Exam exam : exams) {
                result.append(", Оценка: ").append(exam.grade).append("\n");
            }

            return result.toString();
        }

        static class Exam {
            @JsonProperty ("studentId")
            private int studentId;
            @JsonProperty ("grade")
            private int grade;

            public Exam() {
            }

            public Exam(int studentId, int grade) {
                this.studentId = studentId;
                this.grade = grade;
            }

            public int getStudentId() {
                return studentId;
            }

            public int getGrade() {
                return grade;
            }
            @Override
            public String toString() {
                return "Exam {Student ID: " + studentId + ", Grade: " + grade + "}";
            }
        }
    }

    public void addSession(Session session) {
        sessions.add(session);
    }

    public boolean isExcellentStudent() {
        for (Session session : sessions) {
            for (Session.Exam exam : session.getExams()) {
                if (exam.getStudentId() == gradebookNumber && (exam.getGrade() < 9)) {
                    return false;
                }
            }
        }
        return true;
    }

    public void outputStudent(PrintWriter writer) {
        writer.println("ФИО: " + surname + " " + name + " " + patronymic);
        writer.println("Курс: " + course);
        writer.println("Группа: " + groupNumber);
        writer.println("Зачётная книжка: " + gradebookNumber);
        for (Session session : sessions) {
            for (Session.Exam exam : session.getExams()) {
                if (exam.getStudentId() == gradebookNumber) {
                    writer.println("Предмет: " + session.getSubject());
                    writer.println("Сессия: " + session.getSessionNumber());
                    writer.println("Оценка: " + exam.getGrade());
                }
            }
        }
        writer.println();
    }

    public static List<ZachetBook> loadStudents(String studentsFile) {
        List<ZachetBook> zachetBooks = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(studentsFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length == 6) {
                    String surname = parts[0];
                    String name = parts[1];
                    String patronymic = parts[2];
                    int course = Integer.parseInt(parts[3]);
                    int groupNumber = Integer.parseInt(parts[4]);
                    int gradebookNumber = Integer.parseInt(parts[5]);
                    zachetBooks.add(new ZachetBook(surname, name, patronymic, course, groupNumber, gradebookNumber));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return zachetBooks;
    }

    public static void loadSessions(ZachetBook zachetBook, String[] sessionFiles) {
        for (String sessionFile : sessionFiles) {
            try (BufferedReader reader = new BufferedReader(new FileReader(sessionFile))) {
                String line = reader.readLine();
                if (line != null) {
                    String[] header = line.split(" ");
                    String subject = header[0];
                    int sessionNumber = Integer.parseInt(header[1]);

                    Session session = new Session(subject, sessionNumber);
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(" ");
                        int studentId = Integer.parseInt(parts[0]);
                        int grade = Integer.parseInt(parts[1]);
                        session.addExam(studentId, grade);
                    }
                    zachetBook.addSession(session);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void outputExcellentStudents(List<ZachetBook> zachetBooks, String outputFileName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFileName))) {
            for (ZachetBook zachetBook : zachetBooks) {
                if (zachetBook.isExcellentStudent()) {
                    zachetBook.outputStudent(writer);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void outputAllStudents(List<ZachetBook> zachetBooks, String outputFileName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFileName))) {
            for (ZachetBook zachetBook : zachetBooks) {
                zachetBook.outputStudent(writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Serialize all students to JSON
    public static void serializeAllStudents(List<ZachetBook> zachetBooks, String fileName) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.writeValue(new File(fileName), zachetBooks);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Serialize only excellent students to JSON
    public static void serializeExcellentStudents(List<ZachetBook> zachetBooks, String fileName) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            List<ZachetBook> excellentStudents = new ArrayList<>();
            for (ZachetBook zachetBook : zachetBooks) {
                if (zachetBook.isExcellentStudent()) {
                    excellentStudents.add(zachetBook);
                }
            }
            mapper.writeValue(new File(fileName), excellentStudents);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Deserialize all students from JSON
    public static List<ZachetBook> deserializeAllStudents(String fileName) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(new File(fileName), new TypeReference<List<ZachetBook>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Deserialize excellent students from JSON
    public static List<ZachetBook> deserializeExcellentStudents(String fileName) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(new File(fileName), new TypeReference<List<ZachetBook>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}