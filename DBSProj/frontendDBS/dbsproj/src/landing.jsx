import React, { useState } from 'react';
import Student from './components/student.jsx'

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
    department: '',
    courses: [],
  });

  const [securityData, setSecurityData] = useState({
    shiftTiming: '',
    assignedArea: '',
  });

  const [floorManagerData, setFloorManagerData] = useState({
    floorAssigned: '',
    roomAvailability: [],
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

  // Handler for security-specific fields
  const handleSecurityChange = (e) => {
    const { name, value } = e.target;
    setSecurityData((prev) => ({
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

  const handleSubmit = (e) => {
    e.preventDefault();
    console.log("Form Submitted with values: ", form);
  };

  // Conditional content based on selected role
  const renderRightDivContent = () => {
    switch (form.role) {
      case 'security':
        return;
      case 'student':
        return <Student form={form} setForm={setForm} handleChange={handleChange} />;
        
      case 'professor':
        return <p>Professor-related content goes here. E.g., Course assignments.</p>;
      case 'floorManager':
        return <p>Floor Manager-related content goes here. E.g., Room management.</p>;
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
