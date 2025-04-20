package com.example.dbs.controller.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dbs.model.FloorManager;
import com.example.dbs.model.Professor;
import com.example.dbs.model.Security;
import com.example.dbs.model.Student;
import com.example.dbs.model.StudentCouncil;
import com.example.dbs.security.JwtUtil;
import com.example.dbs.service.FloorManagerService;
import com.example.dbs.service.ProfessorService;
import com.example.dbs.service.SecurityService;
import com.example.dbs.service.StudentCouncilService;
import com.example.dbs.service.StudentService;
import com.example.dbs.service.UserService;
import com.example.dbs.types.AuthenticationRequest;
import com.example.dbs.types.AuthenticationResponse;
import com.example.dbs.types.RegistrationRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Validated // Enable validation for request bodies annotated with @Valid
public class AuthController {

    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private UserDetailsService userDetailsService; // UserDetailsServiceImpl
    @Autowired private JwtUtil jwtUtil;
    @Autowired private PasswordEncoder passwordEncoder;

    // --- Inject ALL necessary services ---
    @Autowired private UserService userService;
    @Autowired private StudentService studentService;
    @Autowired private ProfessorService professorService;
    @Autowired private FloorManagerService floorManagerService;
    @Autowired private SecurityService securityService;
    @Autowired private StudentCouncilService studentCouncilService;

    @PostMapping("/register")
    // Use @Valid to trigger DTO validation
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegistrationRequest request) {

        // 1. Check if email already exists in the base Users table
        if (userService.existsByEmail(request.email())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT) // 409 Conflict
                    .body("{\"error\": \"Email is already taken!\"}");
        }

        // 2. Hash the password
        String encodedPassword = passwordEncoder.encode(request.password());

        // 3. Delegate creation based on userType
        try {
            Object createdUserSpecific; // To hold the result from specific services

            switch (request.userType().toUpperCase()) {
                case "STUDENT" -> {
                    if (request.regno() == null) {
                        return ResponseEntity.badRequest().body("{\"error\": \"Registration number (regno) is required for students.\"}");
                    }
                    Student student = new Student();
                    // Set common fields (assuming Student inherits or shares these conceptually)
                    student.setEmail(request.email());
                    student.setPassword(encodedPassword); // Set hashed password
                    student.setName(request.name());
                    student.setPhone(request.phone());
                    // Set specific fields
                    student.setRegno(request.regno());
                    createdUserSpecific = studentService.createStudent(student); // Service handles saving Users + Student
                }

                case "PROFESSOR" -> {
                    if (request.isCultural() == null) {
                        return ResponseEntity.badRequest().body("{\"error\": \"'isCultural' field is required for professors.\"}");
                    }
                    Professor professor = new Professor();
                    professor.setEmail(request.email());
                    professor.setPassword(encodedPassword);
                    professor.setName(request.name());
                    professor.setPhone(request.phone());
                    professor.setIsCultural(request.isCultural());
                    createdUserSpecific = professorService.createProfessor(professor); // Service handles saving Users + Professor
                }

                case "FLOOR_MANAGER" -> {
                    FloorManager floorManager = new FloorManager();
                    floorManager.setEmail(request.email());
                    floorManager.setPassword(encodedPassword);
                    floorManager.setName(request.name());
                    floorManager.setPhone(request.phone());
                    createdUserSpecific = floorManagerService.createFloorManager(floorManager); // Service handles saving Users + FloorManager
                }

                case "SECURITY" -> {
                    Security security = new Security();
                    security.setEmail(request.email());
                    security.setPassword(encodedPassword);
                    security.setName(request.name());
                    security.setPhone(request.phone());
                    createdUserSpecific = securityService.createSecurity(security); // Service handles saving Users + Security
                }

                case "STUDENT_COUNCIL" -> {
                    if (request.regno() == null) {
                        return ResponseEntity.badRequest().body("{\"error\": \"Registration number (regno) is required for Student Council members.\"}");
                    }
                    if (request.position() == null || request.position().isBlank()) {
                        return ResponseEntity.badRequest().body("{\"error\": \"Position is required for Student Council members.\"}");
                    }
                    StudentCouncil councilMember = new StudentCouncil();
                    councilMember.setEmail(request.email());
                    councilMember.setPassword(encodedPassword);
                    councilMember.setName(request.name());
                    councilMember.setPhone(request.phone());
                    councilMember.setRegno(request.regno());
                    councilMember.setPosition(request.position());
                    createdUserSpecific = studentCouncilService.saveCouncilMember(councilMember); // Service handles saving Users + Student + StudentCouncil
                }

                default -> {
                    return ResponseEntity.badRequest().body("{\"error\": \"Invalid userType specified.\"}");
                }
            }

            // 4. Return success response (including the created specific user data)
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUserSpecific);

        } catch (IllegalArgumentException e) {
            // Catch specific validation errors from services if they throw them
             return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            // Catch broader errors during the creation process in services
            System.err.println("Error during user registration: " + e.getMessage()); // Log the error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("{\"error\": \"An internal error occurred during registration.\"}");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.email(), authenticationRequest.password())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"Incorrect email or password\"}"); // Return 401
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.email());
        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }
}
