package com.example.dbs.config;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.dbs.model.Club;
import com.example.dbs.model.ClubMembership;
import com.example.dbs.model.FloorManager;
import com.example.dbs.model.Professor;
import com.example.dbs.model.Room;
import com.example.dbs.model.Security;
import com.example.dbs.model.Student;
import com.example.dbs.model.StudentCouncil;
import com.example.dbs.repository.ClubMembershipRepository;
import com.example.dbs.repository.ClubRepository;
import com.example.dbs.repository.FloorManagerRepository;
import com.example.dbs.repository.ProfessorRepository;
import com.example.dbs.repository.RoomRepository;
import com.example.dbs.repository.SecurityRepository;
import com.example.dbs.repository.StudentCouncilRepository;
import com.example.dbs.repository.StudentRepository;
import com.example.dbs.repository.UserRepository;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired private UserRepository userRepository;
    @Autowired private StudentRepository studentRepository;
    @Autowired private ProfessorRepository professorRepository;
    @Autowired private ClubRepository clubRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private ClubMembershipRepository clubMembershipRepository;
    @Autowired private FloorManagerRepository floorManagerRepository;
    @Autowired private RoomRepository roomRepository;
    @Autowired private StudentCouncilRepository studentCouncilRepository;
    @Autowired private SecurityRepository securityRepository;
    
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("Loading initial data...");

        // Use checks to avoid recreating data if it already exists
        if (userRepository.count() == 0) { // Example check: only load if users table is empty
            loadUsersAndRoles();
            loadRooms();
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
        prof2.setIsCultural(false); // Example value
        professorRepository.save(prof2);

        // cultural department
        Professor prof3 = new Professor();
        prof3.setEmail("cultural.prof@example.com");
        prof3.setPassword(passwordEncoder.encode("profpass3"));
        prof3.setName("Cultural Prof");
        prof3.setPhone(8887776661L);
        prof3.setIsCultural(true); // Cultural professor
        professorRepository.save(prof3);

        // --- create  floor managers ---
        FloorManager manager1 = new FloorManager();
        manager1.setEmail("floor.manager1@example.com");
        manager1.setPassword(passwordEncoder.encode("managerpass1"));
        manager1.setName("Floor Manager One");
        manager1.setPhone(4445556661L);
        floorManagerRepository.save(manager1);

        FloorManager manager2 = new FloorManager();
        manager2.setEmail("floor.manager2@example.com");
        manager2.setPassword(passwordEncoder.encode("managerpass2"));
        manager2.setName("Floor Manager Two");
        manager2.setPhone(4445556662L);
        floorManagerRepository.save(manager2);

        // --- create SC member ---
        StudentCouncil member = new StudentCouncil();
        member.setEmail("sc.member1@example.com");
        member.setPassword(passwordEncoder.encode("scpass1"));
        member.setName("Student President");
        member.setPhone(1234567890L);
        member.setPosition("PRESIDENT");
        member.setRegno(Long.valueOf(23100103));
        studentCouncilRepository.save(member);

        // --- create security dude ---
        Security security = new Security();
        security.setEmail("security1@example.com");
        security.setPassword(passwordEncoder.encode("securitypass1"));
        security.setName("Security One");
        security.setPhone(0000000000L);
        securityRepository.save(security);

        System.out.println("Loaded sample users.");
    }

    private void loadRooms() {
        Room room1_1 = new Room();
        room1_1.setBlock("AB1");
        room1_1.setRoom("101");
        Optional<FloorManager> manager1Optional = floorManagerRepository.findByEmail("floor.manager1@example.com");
        if (!manager1Optional.isEmpty()) {
            FloorManager manager1 = manager1Optional.get();
            room1_1.setManager(manager1);
            roomRepository.save(room1_1);
        }

        Room room2_1 = new Room();
        room2_1.setBlock("AB2");
        room2_1.setRoom("101");
        Optional<FloorManager> manager2Optional = floorManagerRepository.findByEmail("floor.manager2@example.com");
        if (!manager2Optional.isEmpty()) {
            FloorManager manager2 = manager2Optional.get();
            room2_1.setManager(manager2);
            roomRepository.save(room2_1);
        }
    }

    private void loadClubs() {
        // Ensure the POC student and Faculty Head emails exist from the previous step
        if (studentRepository.existsById("poc.student1@example.com") && professorRepository.existsById("faculty.head1@example.com")) {
            Club club1 = new Club();
            club1.setName("Tech Club");
            club1.setPocStudentEmail("poc.student1@example.com");
            club1.setFacultyHeadEmail("faculty.head1@example.com");
            clubRepository.save(club1);

            ClubMembership membership1 = new ClubMembership();
            membership1.setStuEmail("poc.student1@example.com");
            membership1.setClubName(club1.getName());
            clubMembershipRepository.save(membership1);
        } else {
             System.err.println("Could not create 'Tech Club' because prerequisite users do not exist.");
        }


        if (studentRepository.existsById("poc.student2@example.com") && professorRepository.existsById("faculty.head2@example.com")) {
            Club club2 = new Club();
            club2.setName("Music Club");
            club2.setPocStudentEmail("poc.student2@example.com");
            club2.setFacultyHeadEmail("faculty.head2@example.com");
            clubRepository.save(club2);

            ClubMembership membership2 = new ClubMembership();
            membership2.setStuEmail("poc.student2@example.com");
            membership2.setClubName(club2.getName());
            clubMembershipRepository.save(membership2);
        } else {
            System.err.println("Could not create 'Music Club' because prerequisite users do not exist.");
        }

         System.out.println("Loaded sample clubs.");
    }
}
