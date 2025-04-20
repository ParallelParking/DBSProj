export default function Student({ form, setForm, handleChange }) {
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
          <option value="Astronomy Club">Astronomy Club</option>
          <option value="LDQ">LDQ</option>
          <option value="DebSoc">Debating Society</option>
          <option value="Tech Council">Tech Council</option>
          <option value="Dance Club">Dance Club</option>
        </select>
      </label>

      <label>Clubs you're a member of</label>
      <div className="checkbox-container">
        {[
          'Astronomy Club',
          'LDQ',
          'DebSoc',
          'Tech Council',
          'Dance Club',
          'Music Club',
          'Drama Club',
        ].map((club) => (
          <div className="checkbox-item-cont">
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
                    memberClubs: updatedClubs,
                  };
                });
              }}
            />
            <label key={club} className="checkbox-item" htmlFor={club}>
              {club}
            </label>
          </div>
        ))}
      </div>
    </div>
  );
}
