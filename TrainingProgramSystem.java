import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.*;

public class TrainingProgramSystem {
    public static void main(String[] args) {
        // Setup
        Student s1 = new Student("S001", "Gaurav Mishra");
        Student s2 = new Student("S002", "Manish");

        Teacher t1 = new Teacher("T001", "Mr. Amol Sharma");

        FeedbackPhase midtermPhase = new FeedbackPhase(LocalDate.now(), "Mid-term");
        midtermPhase.loadQuestionsFromFile("feedback_questions.txt");

        TrainingProgram javaBootcamp = new TrainingProgram(
                "Wipro TalentNext",
                LocalDate.of(2025, 6, 23),
                LocalDate.of(2025, 8, 31),
                "Core Java, OOP, Collections",
                new ArrayList<>(List.of(s1, s2)),
                new ArrayList<>(List.of(t1)),
                new ArrayList<>(List.of(midtermPhase)));

        // Simulate feedback collection
        // javaBootcamp.collectFeedbackFromAllStudents("Mid-term");

        for (Student student : javaBootcamp.getStudents()) {
            if (student.getRollNo().equals(args[0])) {
                javaBootcamp.collectFeedbackFromStudent("Mid-term", student);
                break;
            }
        }

        // Display feedback results
        //System.out.println(javaBootcamp);
    }
}

// ------------------ Core Classes -----------------

class TrainingProgram {
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private String curriculum;
    private List<Student> students;
    private List<Teacher> teachers;
    private List<FeedbackPhase> feedbackPhases;

    public TrainingProgram(String name, LocalDate startDate, LocalDate endDate, String curriculum,
            List<Student> students, List<Teacher> teachers, List<FeedbackPhase> feedbackPhases) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.curriculum = curriculum;
        this.students = students;
        this.teachers = teachers;
        this.feedbackPhases = feedbackPhases;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    // Collect feedback for a specific phase
    public void collectFeedbackFromAllStudents(String phaseName) {
        FeedbackPhase phase = feedbackPhases.stream()
                .filter(p -> p.getPhase().equalsIgnoreCase(phaseName))
                .findFirst()
                .orElse(null);

        if (phase == null) {
            System.out.println("Feedback phase not found: " + phaseName);
            return;
        }

        for (Student student : students) {
            phase.takeFeedback(student);
        }
    }

    // Collect feedback for a specific phase from a specific student
    public void collectFeedbackFromStudent(String phaseName, Student student) {
        FeedbackPhase phase = feedbackPhases.stream()
                .filter(p -> p.getPhase().equalsIgnoreCase(phaseName))
                .findFirst()
                .orElse(null);

        if (phase == null) {
            System.out.println("Feedback phase not found: " + phaseName);
            return;
        }

        phase.takeFeedback(student);

    }

    @Override
    public String toString() {
        return "TrainingProgram{" +
                "name='" + name + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", curriculum='" + curriculum + '\'' +
                ", students=" + students +
                ", teachers=" + teachers +
                ", feedbackPhases=" + feedbackPhases +
                '}';
    }
}

class Student {
    private String rollNo;
    private String name;

    public Student(String rollNo, String name) {
        this.rollNo = rollNo;
        this.name = name;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Feedback giveFeedback(List<Question> questions) {
        List<Answer> answers = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Student: " + name + " is giving feedback...");

        for (Question q : questions) {
            System.out.print("Q: " + q.getText() + " -> ");
            String response = scanner.nextLine();
            answers.add(new Answer(q, response));
        }

        return new Feedback(this, answers);
    }

    @Override
    public String toString() {
        return "Student{rollNo='" + rollNo + "', name='" + name + "'}";
    }
}

class Teacher {
    private String id;
    private String name;

    public Teacher(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Teacher{id='" + id + "', name='" + name + "'}";
    }
}

class FeedbackPhase {
    private LocalDate sessionDate;
    private String phase;
    private List<Question> questions;
    private List<Feedback> feedbackList;

    public FeedbackPhase(LocalDate sessionDate, String phase) {
        this.sessionDate = sessionDate;
        this.phase = phase;
        this.questions = new ArrayList<>();
        this.feedbackList = new ArrayList<>();
    }

    public String getPhase() {
        return phase;
    }

    public void takeFeedback(Student student) {
        Feedback feedback = student.giveFeedback(this.questions);
        feedbackList.add(feedback);
        feedback.writeFeedbackToFile();
    }

    void loadQuestionsFromFile(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            int countQuestions = 0;
            while ((line = reader.readLine()) != null) {
                // Write the section headings and separators as-is

                if (line.trim().isEmpty() || line.startsWith("SECTION") || line.startsWith("TRAINING")
                        || line.startsWith("Trainer") || line.startsWith("Mode") || line.startsWith("FEEDBACK")
                        || line.startsWith("-") || line.contains("MEERUT")) {
                    continue;
                }

                questions.add(new Question(countQuestions + 1, line));
                countQuestions++;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println("Questions loaded successfully.");
    }

    @Override
    public String toString() {
        return "\nFeedbackPhase{" +
                "sessionDate=" + sessionDate +
                ", phase='" + phase + '\'' +
                ", questions=" + questions +
                ", feedbackList=" + feedbackList +
                '}';
    }
}

class Feedback {
    private Student student;
    private List<Answer> answers;

    public Feedback(Student student, List<Answer> answers) {
        this.student = student;
        this.answers = answers;
    }

    void writeFeedbackToFile()
    {
        String outputFile = "feedbacks//feedback_" + this.student.getName() +this.student.getRollNo()+ ".txt";
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            String line;
            int noAnswers = this.answers.size();
            int qNo = 0;
            while (noAnswers>0) {
                writer.write(this.answers.get(qNo).getQuestion().getText());
                writer.newLine();
                writer.write("Answer: " + this.answers.get(qNo).getResponse());
                writer.newLine();
                writer.newLine();

                noAnswers--;
                qNo++;
            }

            System.out.println("\n Thank you! Your feedback has been saved to: " + outputFile);

        } catch (IOException e) {
            System.err.println("Error reading or writing files: " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "\n  Feedback{" +
                "student=" + student +
                ", answers=" + answers +
                '}';
    }
}

class Question {
    private int id;
    private String text;

    public Question(int id, String text) {
        this.id = id;
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "Question{" + "id=" + id + ", text='" + text + '\'' + '}';
    }
}

class Answer {
    private Question question;
    private String response;

    public Answer(Question question, String response) {
        this.question = question;
        this.response = response;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "\n    Answer{" +
                "question=" + question +
                ", response='" + response + '\'' +
                '}';
    }
}
