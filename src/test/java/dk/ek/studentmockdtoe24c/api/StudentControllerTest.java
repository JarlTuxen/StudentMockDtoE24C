package dk.ek.studentmockdtoe24c.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.ek.studentmockdtoe24c.model.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; //for serializing/deserializing Java <-> JSON

    @Test
    void getAllStudents() throws Exception {
        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getStudentById() throws Exception {

        //lav testobjekt
        Student student = new Student(
                "John",
                "1234",
                LocalDate.of(2025,11,18),
                LocalTime.of(11,30,0)
        );

        //serialiser testobjekt
        String jsonRequest = objectMapper.writeValueAsString(student);

        //kald post endpoint og put svar i result
        MvcResult result = mockMvc.perform(post("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isCreated())
                .andReturn();

        //pak responsebody ud af responseentity
        String content = result.getResponse().getContentAsString();

        //deserialiser response body til student objekt
        Student actualStudent = objectMapper.readValue(content, Student.class);

        Long id = actualStudent.getId();
        //kald getbyid endpoint og sammenlign om navn svarer til den netop oprettede student
        mockMvc.perform(get("/api/students/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(actualStudent.getName()));

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