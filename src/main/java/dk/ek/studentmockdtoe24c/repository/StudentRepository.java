package dk.ek.studentmockdtoe24c.repository;

import dk.ek.studentmockdtoe24c.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
}
