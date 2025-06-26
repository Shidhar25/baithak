import React from "react";
import './App.css';
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Login from "./components/Login";
import AssignmentGrid from "./components/AssignmentGrid";
import ExcelDownload from "./components/ExcelDownload"; // ✅ Import the new component

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<Login />} />
                <Route path="/grid" element={<AssignmentGrid />} />
                <Route path="/download" element={<ExcelDownload />} /> {/* ✅ New route added */}
            </Routes>
        </Router>
    );
}

export default App;
