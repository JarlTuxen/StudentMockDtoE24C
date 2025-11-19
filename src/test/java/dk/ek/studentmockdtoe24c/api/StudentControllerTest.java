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
        //hent alle students
        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getStudentById() throws Exception {
        //opret først ny student og hent derefter ById
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
    void shouldReturnNotFoundForNonExistentStudent() throws Exception {
        mockMvc.perform(get("/api/students/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void createStudent() throws Exception {
        // Given: A new student request
        Student request = new Student(
                "Jane Smith",
                "securePass",
                LocalDate.of(1995, 5, 20),
                LocalTime.of(14, 45)
        );
        String jsonRequest = objectMapper.writeValueAsString(request);

        // When: We send a POST request
        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Jane Smith"))
                .andExpect(jsonPath("$.bornDate").value("1995-05-20"))
                .andExpect(jsonPath("$.bornTime").value("14:45:00"));
    }

    @Test
    void updateStudent() throws Exception {
        //create student, then update the student
        // Given: A student exists in the database
        Student createRequest = new Student(
                "John Doe",
                "password123",
                LocalDate.of(2001, 3, 10),
                LocalTime.of(9, 15)
        );
        String createJson = objectMapper.writeValueAsString(createRequest);

        MvcResult createResult = mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated())
                .andReturn();

        Long studentId = objectMapper.readValue(createResult.getResponse().getContentAsString(), Student.class).getId();

        // When: We send an update request
        Student updateRequest = new Student(
                "John Updated",
                "newPassword",
                LocalDate.of(2001, 3, 10),
                LocalTime.of(9, 30)
        );
        String updateJson = objectMapper.writeValueAsString(updateRequest);

        mockMvc.perform(put("/api/students/{id}", studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Updated"))
                .andExpect(jsonPath("$.bornDate").value("2001-03-10"))
                .andExpect(jsonPath("$.bornTime").value("09:30:00"));

    }

    @Test
    void deleteStudent() throws Exception {
        // Given: A student exists in the database
        Student request = new Student(
                "Jane Doe",
                "password123",
                LocalDate.of(1999, 8, 25),
                LocalTime.of(16, 0)
        );
        String jsonRequest = objectMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andReturn();

        Long studentId = objectMapper.readValue(result.getResponse().getContentAsString(), Student.class).getId();

        // When: We delete the student
        mockMvc.perform(delete("/api/students/{id}", studentId))
                .andExpect(status().isNoContent());

        // Then: The student should not be found
        mockMvc.perform(get("/api/students/{id}", studentId))
                .andExpect(status().isNotFound());
    }
}