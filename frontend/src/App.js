import React, { useEffect, useState } from 'react';
import './App.css';

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

function App() {
    const [members, setMembers] = useState([]);
    const [assignments, setAssignments] = useState([]);
    const [availablePlaces, setAvailablePlaces] = useState([]);
    const [weekOffset, setWeekOffset] = useState(0);
    const [selectedVaarCode, setSelectedVaarCode] = useState(1);

    const today = new Date();
    const sunday = new Date(today);
    sunday.setDate(today.getDate() - today.getDay() + (weekOffset * 7));
    const weekDates = getWeekDates(sunday);
    const weekNumber = weekOffset + 1;

    const fetchData = () => {
        fetch("http://localhost:8081/api/members")
            .then(res => res.json())
            .then(data => setMembers(data))
            .catch(err => console.error("Error fetching members:", err));

        fetch(`http://localhost:8081/api/assign/available-places?vaarCode=${selectedVaarCode}&week=${weekNumber}`)
            .then(res => res.json())
            .then(data => setAvailablePlaces(data))
            .catch(err => console.error("Error fetching available places:", err));

        fetch(`http://localhost:8081/api/assign/view?vaarCode=${selectedVaarCode}&week=${weekNumber}`)
            .then(res => res.json())
            .then(data => setAssignments(data))
            .catch(err => console.error("Error fetching assignments:", err));
    };

    useEffect(() => {
        fetchData();
    }, [selectedVaarCode, weekNumber]);

    const getAssignedPlace = (memberName, day) => {
        const match = assignments.find(
            (a) => a.memberName === memberName && a.dayOfWeek === day
        );
        return match ? match.placeName : "";
    };

    const renderMemberGrid = (filteredMembers) => (
        filteredMembers.map((member, rowIndex) => (
            <React.Fragment key={rowIndex}>
                <div className="header-cell header-name">{member.name}</div>
                {daysOfWeek.map((day, colIndex) => {
                    const assignedPlace = getAssignedPlace(member.name, day);
                    return (
                        <div className="grid-box" key={`cell-${rowIndex}-${colIndex}`}>
                            <select
                                className="cell-dropdown"
                                value={assignedPlace || ""}
                                disabled={!!assignedPlace} // Disable if already assigned
                            >
                                <option value="">{assignedPlace ? assignedPlace : "निवडा"}</option>
                                {!assignedPlace && availablePlaces.map(place => (
                                    <option key={place.id} value={place.name}>{place.name}</option>
                                ))}
                            </select>
                        </div>
                    );
                })}
            </React.Fragment>
        ))
    );

    const maleMembers = members.filter(m => m.gender?.toLowerCase() === 'male');
    const femaleMembers = members.filter(m => m.gender?.toLowerCase() === 'female');

    return (
        <div className="wrapper">
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

            <div className="week-nav">
                <button onClick={() => setWeekOffset(weekOffset - 1)}>⏮ मागील आठवडा</button>
                <span>आठवडा {weekNumber}</span>
                <button onClick={() => setWeekOffset(weekOffset + 1)}>पुढील आठवडा ⏭</button>
            </div>

            <h2>👨 पुरुष विभाग</h2>
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
                    {renderMemberGrid(maleMembers)}
                </div>
            </div>

            <h2>👩 महिला विभाग</h2>
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
                    {renderMemberGrid(femaleMembers)}
                </div>
            </div>
        </div>
    );
}

export default App;