  import React, { useState } from 'react';
  import Student from './components/student.jsx'
  import Professor from './components/professor.jsx';
  import FloorManager from './components/floorManager.jsx';

  export default function Landing() {
    // State to hold form values
    const [form, setForm] = useState({
      email: '',
      name: '',
      phone: '',
      password: '',
      role: 'student',  // Default selected role
    });

    // Role-specific states
    const [studentData, setStudentData] = useState({
      regno: '',
      SCrole: 'none',
      pocClub: '',
      memberClubs: [],
    });

    const [professorData, setProfessorData] = useState({
      inCul: 'no',
      clubHead: 'none'
    });


    const [floorManagerData, setFloorManagerData] = useState({
        block: ''
    });

    // Handler for common fields
    const handleChange = (e) => {
      const { name, value } = e.target;
      setForm((prev) => ({
        ...prev,
        [name]: value,
      }));
    };

    // Handler for student-specific fields
    const handleStudentChange = (e) => {
      const { name, value } = e.target;
      setStudentData((prev) => ({
        ...prev,
        [name]: value,
      }));
    };

    // Handler for professor-specific fields
    const handleProfessorChange = (e) => {
      const { name, value } = e.target;
      setProfessorData((prev) => ({
        ...prev,
        [name]: value,
      }));
    };


    // Handler for floorManager-specific fields
    const handleFloorManagerChange = (e) => {
      const { name, value } = e.target;
      setFloorManagerData((prev) => ({
        ...prev,
        [name]: value,
      }));
    };

    const handleSubmit = async (e) => {
      e.preventDefault();
    
      let requestBody = {
        email: form.email,
        password: form.password,
        name: form.name,
        phone: parseInt(form.phone),
        userType: form.role.toUpperCase(), // converting role to uppercase (e.g., "PROFESSOR")
      };
    
      // Add role-specific fields
      switch (form.role) {
        case 'student':
          requestBody = {
            ...requestBody,
            regno: studentData.regno,
            SCrole: studentData.SCrole,
            pocClub: studentData.pocClub,
            memberClubs: studentData.memberClubs,
          };
          break;
        case 'professor':
          requestBody = {
            ...requestBody,
            isCultural: professorData.inCul === 'yes', // assuming 'yes'/'no' in your UI
            clubHead: professorData.clubHead,
          };
          break;
        case 'floorManager':
          requestBody = {
            ...requestBody,
            block: floorManagerData.block,
          };
          break;
        default:
          break;
      }
    
      try {
        const response = await fetch('http://localhost:8080/api/auth/register', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(requestBody),
        });
    
        const data = await response.json();
    
        if (!response.ok) {
          throw new Error(data.message || 'Registration failed');
        }
    
        console.log('✅ Registration successful:', data);
        // Optionally reset the form or show a success message
      } catch (error) {
        console.error('❌ Registration error:', error.message);
        // Show error to user
      }
    };
    
   

    // Conditional content based on selected role
    const renderRightDivContent = () => {
      switch (form.role) {
        case 'security':
          return;
        case 'student':
          return <Student form={studentData} setForm={setStudentData} handleChange={handleStudentChange}/>;
        case 'professor':
          return <Professor form={professorData} setForm={setProfessorData} handleChange={handleProfessorChange}/>
        case 'floorManager':
          return <FloorManager form={floorManagerData} setForm={setFloorManagerData} handleChange={handleFloorManagerChange}/>
        default:
          return <p>Some placeholder text.</p>;
      }
    };

    return (
      <div className="container">
        <div className="left">
          <p>Registration</p>
          <form onSubmit={handleSubmit}>
            <input 
              type="email"
              placeholder="Email"
              aria-label="mail"
              name="email"
              value={form.email}
              onChange={handleChange}
              required
            />
            <input 
              type="text"
              placeholder="Name"
              aria-label="name"
              name="name"
              value={form.name}
              onChange={handleChange}
              required
            />
            <input 
              type="tel"
              placeholder="Phone"
              aria-label="phone"
              name="phone"
              value={form.phone}
              onChange={handleChange}
              required
            />
            <input 
              type="password"
              placeholder="Password"
              aria-label="password"
              name="password"
              value={form.password}
              onChange={handleChange}
              required
            />

            {/* Dropdown for Role Selection */}
            <select
              name="role"
              value={form.role}
              onChange={handleChange}
              required
            >
              <option value="security">Security</option>
              <option value="student">Student</option>
              <option value="professor">Professor</option>
              <option value="floorManager">Floor Manager</option>
            </select>

            <button type="submit">Register</button>
          </form>
        </div>
        <div className="right">
          {/* Conditionally render content based on selected role */}
          {renderRightDivContent()}
        </div>
      </div>
    );
  }
