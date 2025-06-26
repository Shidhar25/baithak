import React, { useEffect, useState } from 'react';
import '../App.css';
import { useNavigate } from 'react-router-dom';

const daysOfWeek = ['रविवार', 'सोमवार', 'मंगळवार', 'बुधवार', 'गुरुवार', 'शुक्रवार', 'शनिवार'];

const vaarList = [
    { name: 'रविवार', code: 7 },
    { name: 'सोमवार', code: 1 },
    { name: 'मंगळवार', code: 2 },
    { name: 'बुधवार', code: 3 },
    { name: 'गुरुवार', code: 4 },
    { name: 'शुक्रवार', code: 5 },
    { name: 'शनिवार', code: 6 },
];

function getWeekDates(startDate) {
    const dates = [];
    for (let i = 0; i < 7; i++) {
        const d = new Date(startDate);
        d.setDate(startDate.getDate() + i);
        const dd = String(d.getDate()).padStart(2, '0');
        const mm = String(d.getMonth() + 1).padStart(2, '0');
        const yy = String(d.getFullYear()).toString().slice(-2);
        dates.push(`${dd}/${mm}/${yy}`);
    }
    return dates;
}

function AssignmentGrid() {
    const navigate = useNavigate(); // ✅ useNavigate inside the component

    const [members, setMembers] = useState([]);
    const [assignments, setAssignments] = useState([]);
    const [availablePlaces, setAvailablePlaces] = useState([]);
    const [weekOffset, setWeekOffset] = useState(0);
    const [selectedVaarCode, setSelectedVaarCode] = useState(1); // Default to Monday

    const today = new Date();
    const sunday = new Date(today);
    sunday.setDate(today.getDate() - today.getDay() + (weekOffset * 7));
    const weekDates = getWeekDates(sunday);
    const weekNumber = weekOffset + 1;

    useEffect(() => {
        fetch("http://localhost:8081/api/members")
            .then(res => res.json())
            .then(data => setMembers(data))
            .catch(err => console.error("Error fetching members:", err));
    }, []);

    useEffect(() => {
        fetch(`http://localhost:8081/api/assign/available-places?vaarCode=${selectedVaarCode}&week=${weekNumber}`)
            .then(res => res.json())
            .then(data => setAvailablePlaces(data))
            .catch(err => console.error("Error fetching places:", err));

        fetch(`http://localhost:8081/api/assign/view?vaarCode=${selectedVaarCode}&week=${weekNumber}`)
            .then(res => res.json())
            .then(data => setAssignments(data))
            .catch(err => console.error("Error fetching assignments:", err));
    }, [selectedVaarCode, weekNumber]);

    const getAssignedPlace = (memberName, day) => {
        const match = assignments.find(
            (a) => a.memberName === memberName && a.dayOfWeek === day
        );
        return match ? match.placeName : "";
    };

    const updateAssignment = (memberName, dayOfWeek, placeName) => {
        fetch("http://localhost:8081/api/assign/manual", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                memberName,
                placeName,
                week: weekNumber
            })
        })
            .then(res => {
                if (!res.ok) throw new Error("Assignment failed");
                return res.text();
            })
            .then(() => {
                alert("Assigned!");

                // Refresh assignments and available places
                fetch(`http://localhost:8081/api/assign/view?vaarCode=${selectedVaarCode}&week=${weekNumber}`)
                    .then(res => res.json())
                    .then(data => setAssignments(data));

                fetch(`http://localhost:8081/api/assign/available-places?vaarCode=${selectedVaarCode}&week=${weekNumber}`)
                    .then(res => res.json())
                    .then(data => setAvailablePlaces(data));
            })
            .catch(err => {
                alert("Error: " + err.message);
            });
    };

    return (
        <div className="wrapper">

            {/* 🔽 Excel Download Button */}
            <div className="excel-button-wrapper">
                <button className="excel-download" onClick={() => navigate('/download')}>
                    Download Excel 📥
                </button>
            </div>

            {/* Vaar Code Selector */}
            <div className="vaar-nav">
                {vaarList.map(vaar => (
                    <button
                        key={vaar.code}
                        className={`vaar-button ${selectedVaarCode === vaar.code ? 'active' : ''}`}
                        onClick={() => setSelectedVaarCode(vaar.code)}
                    >
                        {vaar.name}
                    </button>
                ))}
            </div>

            {/* Week Navigation */}
            <div className="week-nav">
                <button onClick={() => setWeekOffset(weekOffset - 1)}>⏮ मागील आठवडा</button>
                <span>आठवडा {weekNumber}</span>
                <button onClick={() => setWeekOffset(weekOffset + 1)}>पुढील आठवडा ⏭</button>
            </div>

            {/* Grid Table */}
            <div className="scrollable-grid">
                <div className="grid-container">
                    <div className="header-cell week-cell">तारीख</div>
                    {weekDates.map((date, i) => (
                        <div className="header-cell date-cell" key={i}>{date}</div>
                    ))}

                    <div className="header-cell empty-cell"></div>
                    {daysOfWeek.map((day, i) => (
                        <div className="header-cell" key={i}>{day}</div>
                    ))}

                    {members.map((member, rowIndex) => (
                        <React.Fragment key={rowIndex}>
                            <div className="header-cell header-name">{member.name}</div>
                            {daysOfWeek.map((day, colIndex) => {
                                const assignedPlace = getAssignedPlace(member.name, day);
                                return (
                                    <div className="grid-box" key={`cell-${rowIndex}-${colIndex}`}>
                                        <select
                                            className="cell-dropdown"
                                            value={assignedPlace}
                                            onChange={(e) =>
                                                updateAssignment(member.name, day, e.target.value)
                                            }
                                            disabled={!!assignedPlace}
                                        >
                                            <option value="">निवडा</option>
                                            {availablePlaces.map(place => (
                                                <option key={place.id} value={place.name}>
                                                    {place.name}
                                                </option>
                                            ))}
                                        </select>
                                    </div>
                                );
                            })}
                        </React.Fragment>
                    ))}
                </div>
            </div>
        </div>
    );
}

export default AssignmentGrid;
