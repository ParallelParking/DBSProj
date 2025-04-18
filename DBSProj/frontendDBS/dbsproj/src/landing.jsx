export default function landing(){

    const handleSubmit = (e) => {
        e.preventDefault();
        
        const form = e.target;
        
        const email = form.email.value;
        const name = form.name.value;
        const phone = form.phone.value;
        const password = form.password.value;
    
        console.log("Email:", email);
        console.log("Name:", name);
        console.log("Phone:", phone);
        console.log("Password:", password);
    
    };
    

    return(
        <div className="container">
            <div className="left">
                <p>Registration</p>
                <form onSubmit={handleSubmit}>
                    <input 
                        type="email"
                        placeholder="Email"
                        aria-label="mail"
                        name="email"
                        required
                    />
                    <input 
                        type="text"
                        placeholder="Name"
                        aria-label="name"
                        name="name"
                        required
                    />
                    <input 
                        type="tel"
                        placeholder="Phone"
                        aria-label="phone"
                        name="phone"
                        required
                    />
                    <input 
                        type="password"
                        placeholder="Password"
                        aria-label="password"
                        name="password"
                        required
                    />
                    <button type="submit">Register</button>
                </form>
            </div>
            <div className = "right">
                <p>Some placeholder text</p>
            </div>
        </div>
    )
}