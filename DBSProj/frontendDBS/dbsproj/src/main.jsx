import { createRoot } from 'react-dom/client'
import './index.css'
import Landing from "./landing"

createRoot(document.getElementById('root')).render(
  
    <>
      <header>
        <img src = "src/assets/mit.png" />
        <h1>Student Club Room Booking</h1>
      </header>
      <Landing />
    </>
  
)
