package dk.ek.studentmockdtoe24c.service;

import dk.ek.studentmockdtoe24c.model.Student;
import dk.ek.studentmockdtoe24c.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    // Constructor injection
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> getAllStudents() {
        List<Student> students = studentRepository.findAll();
        List<Student> studentResponses = new ArrayList<>();

        // Using a for-loop to convert each Student to a StudentResponseDTO
        for (Student student : students) {
            studentResponses.add(student);
        }

        return studentResponses;
    }

    public Student getStudentById(Long id) {
        Optional<Student> optionalStudent = studentRepository.findById(id);

        // Throw RuntimeException if student is not found
        if (optionalStudent.isEmpty()) {
            throw new RuntimeException("Student not found with id " + id);
        }

        Student studentResponse = optionalStudent.get();

        return studentResponse;

    }

    public Student createStudent(Student studentRequest) {
        Student studentResponse = studentRepository.save(studentRequest);

        return studentResponse;
    }

    public Student updateStudent(Long id, Student studentRequest) {
        Optional<Student> optionalStudent = studentRepository.findById(id);
        // Throw RuntimeException if student is not found
        if (optionalStudent.isEmpty()) {
            throw new RuntimeException("Student not found with id " + id);
        }

        Student student = optionalStudent.get();

        student.setName(studentRequest.getName());
        student.setPassword(studentRequest.getPassword());
        student.setBornDate(studentRequest.getBornDate());
        student.setBornTime(studentRequest.getBornTime());

        Student studentResponse = studentRepository.save(student);
        return studentResponse;
    }

    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            // Throw RuntimeException if student is not found
            throw new RuntimeException("Student not found with id " + id);
        }
        studentRepository.deleteById(id);
    }

}
