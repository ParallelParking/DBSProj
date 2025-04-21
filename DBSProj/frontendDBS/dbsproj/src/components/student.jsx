import { useEffect, useState } from "react";
import axios from "axios";

export default function Student({ form, setForm, handleChange }) {
  const [availableClubs, setAvailableClubs] = useState([]);

  useEffect(() => {
    const fetchClubs = async () => {
      try {
        const response = await axios.get("http://localhost:8080/api/clubs");
        setAvailableClubs(response.data.map((club) => club.name));
      } catch (error) {
        console.error("Error fetching clubs:", error);
      }
    };
    fetchClubs();
  }, []);

  return (
    <div className="student-form">
      <label>
        <input
          type="text"
          placeholder="Reg. No."
          aria-label="regno"
          name="regno"
          value={form.regno || ''}
          onChange={handleChange}
          required
        />
      </label>

      <label>
        SC Role (If any)
        <select
          name="SCrole"
          value={form.SCrole || 'none'}
          onChange={handleChange}
          required
        >
          <option value="none">None</option>
          <option value="president">President</option>
          <option value="vicePresident">Vice President</option>
          <option value="treasurer">Treasurer</option>
          <option value="secretary">Secretary</option>
        </select>
      </label>

      <label>
        POC (Person of Contact) for
        <select
          name="pocClub"
          value={form.pocClub || ''}
          onChange={handleChange}
          required
        >
          <option value="">-- Select a club --</option>
          {availableClubs.map((club) => (
            <option key={club} value={club}>
              {club}
            </option>
          ))}
        </select>
      </label>

      <label>Clubs you're a member of</label>
      <div className="checkbox-container">
        {availableClubs.map((club) => (
          <div key={club} className="checkbox-item-cont">
            <input
              style={{
                height: "auto",
                marginBottom: "0"
              }}
              className="club-checkbox"
              type="checkbox"
              name="memberClubs"
              id={club}
              value={club}
              checked={form.memberClubs?.includes(club) || false}
              onChange={(e) => {
                const value = e.target.value;
                setForm((prev) => {
                  const isChecked = e.target.checked;
                  const updatedClubs = isChecked
                    ? [...(prev.memberClubs || []), value]
                    : (prev.memberClubs || []).filter((c) => c !== value);
                  return {
                    ...prev,
                    memberClubs: updatedClubs
                  };
                });
              }}
            />
            <label className="checkbox-item" htmlFor={club}>
              {club}
            </label>
          </div>
        ))}
      </div>
    </div>
  );
}
