package dk.ek.studentmockdtoe24c.service;

import dk.ek.studentmockdtoe24c.model.Student;
import dk.ek.studentmockdtoe24c.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;

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

        //findById giver studerende på id=1 og empty optional på id=42
        Mockito.when(mockedStudentRepository.findById(1L)).thenReturn(Optional.of(s1));
        Mockito.when(mockedStudentRepository.findById(42L)).thenReturn(Optional.empty());

        // Define the behavior af save using thenAnswer that uses parameters to the mock in the answer
        // The student passed in save, can be read from arguments in the InvocationOnMock object
        Mockito.when(mockedStudentRepository.save(ArgumentMatchers.any(Student.class))).thenAnswer(new Answer<Student>() {
            @Override
            public Student answer(InvocationOnMock invocation) throws Throwable {
                // Extract the student object passed as an argument to the save method
                Object[] arguments = invocation.getArguments();
                if (arguments.length > 0 && arguments[0] instanceof Student) {
                    Student studentToSave = (Student) arguments[0];
                    //er id = 0, så simuler create - er id sat så simuler opdater
                    if (studentToSave.getId()==null) {
                        //create - repository skal returnere studentobject med næste ledige id = 3
                        studentToSave.setId(3L);
                    }
                    return studentToSave;
                } else {
                    // Handle the case where the argument is not a Student (optional)
                    throw new IllegalArgumentException("Invalid argument type");
                }
            }
        });

        //deleteById(42L) giver fejl vha. doThrow
        doThrow(new RuntimeException("Student not found with id: 42")).when(mockedStudentRepository).deleteById(42L);

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

        Student student = studentService.getStudentById(1L);
        assertEquals("Anders", student.getName());

        assertThrows(RuntimeException.class, () -> studentService.getStudentById(42L));
    }

    @Test
    void createStudent() {
        //Arrange & Act
        Student resultStudent = studentService.createStudent(
                new Student(
                        "Hugo",
                        "Secret",
                        LocalDate.of(2000,1,1),
                        LocalTime.of(0, 0, 1)
                ));
        //Assert
        assertEquals("Hugo", resultStudent.getName());
        assertEquals(3L, resultStudent.getId());

    }

    @Test
    void updateStudent() {
        //Arrange
        Student studentRequest = new Student(
                "Hugo",
                "Secret",
                LocalDate.of(2000,1,1),
                LocalTime.of(0, 0, 1));
        //Act
        Student resultStudent = studentService.updateStudent(1L,  studentRequest);
        //Assert
        assertEquals(1, resultStudent.getId());
        assertEquals("Hugo", resultStudent.getName());
        //Act & Assert for non-existing student
        assertThrows(RuntimeException.class, () -> studentService.updateStudent(42L, studentRequest));

    }

    @Test
    void deleteStudent() {
        assertThrows(RuntimeException.class, () -> studentService.deleteStudent(42L));
    }
}