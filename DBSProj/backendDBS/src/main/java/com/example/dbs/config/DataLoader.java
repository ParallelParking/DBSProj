package com.example.dbs.config; // Or another appropriate package

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.dbs.model.Club;
import com.example.dbs.model.Professor;
import com.example.dbs.model.Student;
import com.example.dbs.repository.ClubRepository;
import com.example.dbs.repository.ProfessorRepository;
import com.example.dbs.repository.StudentRepository;
import com.example.dbs.repository.UserRepository;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired private UserRepository userRepository;
    @Autowired private StudentRepository studentRepository;
    @Autowired private ProfessorRepository professorRepository;
    @Autowired private ClubRepository clubRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("Loading initial data...");

        // Use checks to avoid recreating data if it already exists
        if (userRepository.count() == 0) { // Example check: only load if users table is empty
            loadUsersAndRoles();
            loadClubs();
            // Add calls to load other data as needed
            System.out.println("Initial data loaded.");
        } else {
            System.out.println("Data already exists. Skipping data loading.");
        }
    }

    private void loadUsersAndRoles() {
        // --- Create Students ---
        Student student1 = new Student();
        student1.setEmail("poc.student1@example.com");
        student1.setPassword(passwordEncoder.encode("studentpass1"));
        student1.setName("PoC Student One");
        student1.setPhone(1112223330L);
        student1.setRegno(Long.valueOf(23100101));
        studentRepository.save(student1);

        Student student2 = new Student();
        student2.setEmail("poc.student2@example.com");
        student2.setPassword(passwordEncoder.encode("studentpass2"));
        student2.setName("PoC Student Two");
        student2.setPhone(1112223331L);
        student2.setRegno(Long.valueOf(23100102));
        studentRepository.save(student2);

        // --- Create Professors ---
        Professor prof1 = new Professor();
        prof1.setEmail("faculty.head1@example.com");
        prof1.setPassword(passwordEncoder.encode("profpass1"));
        prof1.setName("Faculty Head One");
        prof1.setPhone(9998887770L);
        prof1.setIsCultural(false); // Example value
        professorRepository.save(prof1);

        Professor prof2 = new Professor();
        prof2.setEmail("faculty.head2@example.com");
        prof2.setPassword(passwordEncoder.encode("profpass2"));
        prof2.setName("Faculty Head Two");
        prof2.setPhone(9998887771L);
        prof2.setIsCultural(true); // Example value
        professorRepository.save(prof2);

        // Add more users (FloorManager, Security, StudentCouncil) as needed...
         System.out.println("Loaded sample students and professors.");
    }

    private void loadClubs() {
        // Ensure the POC student and Faculty Head emails exist from the previous step
        if (studentRepository.existsById("poc.student1@example.com") && professorRepository.existsById("faculty.head1@example.com")) {
            Club club1 = new Club();
            club1.setName("Tech Club");
            club1.setPocStudentEmail("poc.student1@example.com");
            club1.setFacultyHeadEmail("faculty.head1@example.com");
            clubRepository.save(club1);
        } else {
             System.err.println("Could not create 'Tech Club' because prerequisite users do not exist.");
        }


        if (studentRepository.existsById("poc.student2@example.com") && professorRepository.existsById("faculty.head2@example.com")) {
            Club club2 = new Club();
            club2.setName("Music Club");
            club2.setPocStudentEmail("poc.student2@example.com");
            club2.setFacultyHeadEmail("faculty.head2@example.com");
            clubRepository.save(club2);
        } else {
            System.err.println("Could not create 'Music Club' because prerequisite users do not exist.");
        }

         System.out.println("Loaded sample clubs.");
    }
}
