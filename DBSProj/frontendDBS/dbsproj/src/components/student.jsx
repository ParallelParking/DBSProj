export default function student({form ,setForm, handleChange}){
    return(
        <>
      <form>
        <input
          type="text"
          placeholder='Reg. No.'
          aria-label='regno'
          name='regno'
          value={form.regno || ''}
          onChange={handleChange}
          required
        />

        <p>SC Role (If any)</p>
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

        <p>POC (Person of Contact) for</p>
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

        <p>Clubs you're a member of</p>
        <div className="checkbox-container">
            {['Astronomy Club', 'LDQ', 'DebSoc', 'Tech Council', 'Dance Club', 'Music Club', 'Drama Club'].map((club) => (
                <label key={club} className="checkbox-item">
                <input
                    type="checkbox"
                    name="memberClubs"
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
                {club}
                </label>
            ))}
            </div>
      </form>
    </>
    );
}