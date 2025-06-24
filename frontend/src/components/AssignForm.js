import React, { useState, useEffect } from "react";
import axios from "axios";

const AssignPage = () => {
    const [week, setWeek] = useState(0);
    const [vaarCode, setVaarCode] = useState(1);
    const [availablePlaces, setAvailablePlaces] = useState([]);
    const [members, setMembers] = useState([]);
    const [selectedMember, setSelectedMember] = useState("");
    const [selectedPlace, setSelectedPlace] = useState("");

    useEffect(() => {
        fetchAvailablePlaces();
        fetchMembers(); // optional if you want dropdown
    }, [week, vaarCode]);

    const fetchAvailablePlaces = async () => {
        const res = await axios.get(`http://localhost:8081/api/assign/available-places?vaarCode=${vaarCode}&week=${week}`);
        setAvailablePlaces(res.data);
    };

    const fetchMembers = async () => {
        const res = await axios.get("http://localhost:8081/api/members"); // adjust if needed
        setMembers(res.data);
    };

    const handleAssign = async () => {
        if (!selectedMember || !selectedPlace) {
            alert("Please select both member and place");
            return;
        }

        await axios.post("http://localhost:8081/api/assign/manual", {
            memberName: selectedMember,
            placeName: selectedPlace,
            week: week
        });

        alert("Assignment successful!");
        setSelectedMember("");
        setSelectedPlace("");
        fetchAvailablePlaces(); // refresh dropdown
    };

    return (
        <div style={{ padding: "20px" }}>
            <h2>Manual Assignment</h2>

            <div style={{ marginBottom: "10px" }}>
                <label>Week: </label>
                <button onClick={() => setWeek((w) => w - 1)}> ⏪ मागील आठवडा </button>
                <span style={{ margin: "0 10px" }}>आठवडा {week}</span>
                <button onClick={() => setWeek((w) => w + 1)}> पुढील आठवडा ⏩ </button>
            </div>

            <div style={{ marginBottom: "10px" }}>
                <label>सदस्य:</label>
                <select value={selectedMember} onChange={(e) => setSelectedMember(e.target.value)}>
                    <option value="">सदस्य निवडा</option>
                    {members.map((m, i) => (
                        <option key={i} value={m.name}>{m.name}</option>
                    ))}
                </select>
            </div>

            <div style={{ marginBottom: "10px" }}>
                <label>ठिकाण:</label>
                <select value={selectedPlace} onChange={(e) => setSelectedPlace(e.target.value)}>
                    <option value="">ठिकाण निवडा</option>
                    {availablePlaces.map((p, i) => (
                        <option key={i} value={p.name}>{p.name}</option>
                    ))}
                </select>
            </div>

            <button onClick={handleAssign}>Assign</button>
        </div>
    );
};

export default AssignPage;
