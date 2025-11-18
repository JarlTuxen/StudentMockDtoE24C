package dk.ek.studentmockdtoe24c.service;

import dk.ek.studentmockdtoe24c.model.Student;
import dk.ek.studentmockdtoe24c.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StudentServiceTest {

    @Mock
    private StudentRepository mockedStudentRepository;

    private StudentService studentService;

    @BeforeEach
    void setUp(
    ) {
        Student s1 = new Student(1L, "Anders", "123", LocalDate.of(2008, 5, 22), LocalTime.of(8, 30, 45));
        Student s2 = new Student(2L, "Lina","Hemmeligt", LocalDate.of(2012, 7, 9), LocalTime.of(15, 20, 30));
        List<Student> students = new ArrayList<>();
        students.add(s1);
        students.add(s2);

        //findAll på mockedRepository skal give en liste af students
        Mockito.when(mockedStudentRepository.findAll()).thenReturn(students);

        //findById(1) på mockedRepository skal returnere student Anders
        //findById(42 på mockedRepository skal returnere en tom optionalStudent

        //inject mockedRepository in studentService
        studentService = new StudentService(mockedStudentRepository);

    }

    @Test
    void getAllStudents() {
        //Arrange
        //data.sql
        //Act
        List<Student> students = studentService.getAllStudents();
        //Assert
        assertNotNull(students);
        assertEquals(2, students.size());
        assertEquals("Anders", students.get(0).getName());
    }

    @Test
    void getStudentById() {
    }

    @Test
    void createStudent() {
    }

    @Test
    void updateStudent() {
    }

    @Test
    void deleteStudent() {
    }
}