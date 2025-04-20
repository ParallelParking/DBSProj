import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Home from './home';
import Register from './register';
import Login from './login';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/register" element={<Register />} />
        <Route path="/login" element={<Login />} />
      </Routes>
    </Router>
  );
}

export default App;