import React, { useEffect, useState } from 'react';
import '../App.css';
import { useNavigate } from 'react-router-dom';
import { ChevronLeft, ChevronRight, Download, Users, Calendar, MapPin, Clock, XCircle, History, Search } from 'lucide-react';

const daysOfWeek = ['सोमवार', 'मंगळवार', 'बुधवार', 'गुरुवार', 'रविवार'];

const vaarList = [
    { name: 'सोमवार', code: 1 },
    { name: 'मंगळवार', code: 2 },
    { name: 'बुधवार', code: 3 },
    { name: 'गुरुवार', code: 4 },
    { name: 'रविवार', code: 7 }
];

function AssignmentGrid() {
    const navigate = useNavigate();
    const [members, setMembers] = useState([]);
    const [femaleMembers, setFemaleMembers] = useState([]);
    const [assignments, setAssignments] = useState([]);
    const [availablePlaces, setAvailablePlaces] = useState([]);
    const [assignedInfo, setAssignedInfo] = useState({});
    const [weekOffset, setWeekOffset] = useState(0);
    const [selectedVaarCode, setSelectedVaarCode] = useState(1);
    const [history, setHistory] = useState([]);
    const [searchQuery, setSearchQuery] = useState('');
    const [filterUnassignedOnly, setFilterUnassignedOnly] = useState(false);

    const weekNumber = weekOffset + 1;

    useEffect(() => {
        fetch(`${process.env.REACT_APP_BACKEND_URL}/api/members`)
            .then(res => res.json())
            .then(data => {
                setMembers(data.filter(m => m.gender === 'male'));
                setFemaleMembers(data.filter(m => m.gender === 'female'));
            })
            .catch(err => console.error("Error fetching members:", err));
    }, []);

    useEffect(() => {
        fetch(`${process.env.REACT_APP_BACKEND_URL}/api/assign/available-places?vaarCode=${selectedVaarCode}&week=${weekNumber}`)
            .then(res => res.json())
            .then(setAvailablePlaces)
            .catch(err => console.error("Error fetching places:", err));

        fetch(`${process.env.REACT_APP_BACKEND_URL}/api/assign/view?vaarCode=${selectedVaarCode}&week=${weekNumber}`)
            .then(res => res.json())
            .then(setAssignments)
            .catch(err => console.error("Error fetching assignments:", err));

        fetchAssignedPlaces();
    }, [selectedVaarCode, weekNumber, members.length, femaleMembers.length]);

    const fetchAssignedPlaces = async () => {
        const allMembers = [...members, ...femaleMembers];
        const responses = await Promise.all(
            allMembers.map(member =>
                fetch(`${process.env.REACT_APP_BACKEND_URL}/api/assign/assigned-place?memberId=${member.id}&vaarCode=${selectedVaarCode}&weekNumber=${weekNumber}`)
                    .then(res => res.json())
                    .then(data => ({ id: member.id, place: data?.name || null }))
                    .catch(() => ({ id: member.id, place: null }))
            )
        );
        const result = {};
        responses.forEach(r => {
            result[r.id] = r.place;
        });
        setAssignedInfo(result);
    };

    const updateAssignment = (member, placeName) => {
        fetch(`${process.env.REACT_APP_BACKEND_URL}/api/assign/manual`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                memberName: member.name,
                placeName,
                week: weekNumber,
                vaarCode: selectedVaarCode
            })
        })
            .then(async res => {
                if (!res.ok) {
                    const errText = await res.text();
                    throw new Error(errText || "Assignment failed");
                }
                return res.text();
            })
            .then(() => {
                setAssignedInfo(prev => ({ ...prev, [member.id]: placeName }));
                setAvailablePlaces(prev => prev.filter(p => p.name !== placeName));
                setAssignments(prev => [...prev, { memberName: member.name, placeName, vaarCode: selectedVaarCode, week: weekNumber }]);
                setHistory(prev => [...prev, `✅ ${member.name} assigned to ${placeName} (Week ${weekNumber})`]);
            })
            .catch(err => {
                alert(`❌ ${member.name} → ${placeName}\nError: ${err.message}`);
            });
    };

    const undoAssignment = (memberId, memberName, placeName) => {
        fetch(`${process.env.REACT_APP_BACKEND_URL}/api/assign/unassign`, {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ memberName, placeName, week: weekNumber, vaarCode: selectedVaarCode })
        })
            .then(res => {
                if (!res.ok) throw new Error("Unassignment failed");
                return res.text();
            })
            .then(() => {
                setAssignedInfo(prev => ({ ...prev, [memberId]: null }));
                setAvailablePlaces(prev => [...prev, { name: placeName, id: crypto.randomUUID(), femaleAllowed: true }]);
                setHistory(prev => [...prev, `❌ ${memberName} unassigned from ${placeName} (Week ${weekNumber})`]);
            })
            .catch(err => alert("Error: " + err.message));
    };

    const filterMembers = (memberList) => {
        return memberList.filter(member => {
            const isMatched = member.name.includes(searchQuery);
            const isUnassigned = !assignedInfo[member.id];
            return (!filterUnassignedOnly || isUnassigned) && isMatched;
        });
    };

    const renderGrid = (memberList, sectionTitle, icon) => {
        const filteredList = filterMembers(memberList);

        return (
            <div className="section-container">
                <div className="section-header">
                    <div className="section-title">
                        {icon}
                        <h2>{sectionTitle}</h2>
                    </div>
                    <div className="member-count">{filteredList.length} सदस्य</div>
                </div>

                <div className="grid-wrapper">
                    <div className="grid-container">
                        <div className="header-cell header-name">
                            <Users size={16} /> सदस्य
                        </div>
                        {daysOfWeek.map((day) => {
                            const vaarCode = vaarList.find(v => v.name === day)?.code;
                            return (
                                <div key={day} className={`header-cell day-header ${selectedVaarCode === vaarCode ? 'highlight-column' : ''}`}>
                                    <Calendar size={14} /> {day}
                                </div>
                            );
                        })}

                        {filteredList.map((member) => (
                            <React.Fragment key={member.id}>
                                <div className="member-cell">
                                    <div className="member-avatar">{member.name.charAt(0)}</div>
                                    <span className="member-name">{member.name}</span>
                                </div>
                                {daysOfWeek.map((day, colIndex) => {
                                    const vaarCode = vaarList.find(v => v.name === day)?.code;
                                    const isCurrentVaar = selectedVaarCode === vaarCode;
                                    const assignedPlace = isCurrentVaar ? assignedInfo[member.id] : "";

                                    return (
                                        <div key={`cell-${member.id}-${colIndex}`} className={`grid-cell ${isCurrentVaar ? 'highlight-column' : ''} ${assignedPlace ? 'has-assignment' : ''}`}>
                                            {isCurrentVaar ? (
                                                assignedPlace ? (
                                                    <div className="assigned-place">
                                                        <MapPin size={14} /> <span>{assignedPlace}</span>
                                                        <button className="unassign-btn" onClick={() => undoAssignment(member.id, member.name, assignedPlace)}>
                                                            <XCircle size={14} />
                                                        </button>
                                                    </div>
                                                ) : (
                                                    availablePlaces.length > 0 ? (
                                                        <select className="assignment-select" value="" onChange={(e) => updateAssignment(member, e.target.value)}>
                                                            <option value="">स्थान निवडा</option>
                                                            {availablePlaces.map((place) => {
                                                                const isAllowed = member.gender === "male" || place.femaleAllowed;
                                                                return (
                                                                    <option key={place.id} value={place.name} disabled={!isAllowed}>
                                                                        {place.name} {isAllowed ? '' : '🚫'}
                                                                    </option>
                                                                );
                                                            })}
                                                        </select>
                                                    ) : (
                                                        <small className="no-places">❌ उपलब्ध स्थान नाही</small>
                                                    )
                                                )
                                            ) : (
                                                <div className="empty-cell">
                                                    <div className="empty-indicator">-</div>
                                                </div>
                                            )}
                                        </div>
                                    );
                                })}
                            </React.Fragment>
                        ))}
                    </div>
                </div>
            </div>
        );
    };

    return (
        <div className="app-container">
            <div className="app-header">
                <div className="header-content">
                    <h1 className="app-title">
                        <Calendar className="title-icon" /> कार्य नियोजन व्यवस्था
                    </h1>
                    <button className="download-btn" onClick={() => navigate('/download')}>
                        <Download size={16} /> Excel डाउनलोड करा
                    </button>
                </div>
            </div>

            <div className="nav-container">
                <div className="week-nav">
                    <button className="nav-btn secondary" onClick={() => setWeekOffset(weekOffset - 1)}>
                        <ChevronLeft size={16} /> मागील आठवडा
                    </button>
                    <div className="week-indicator">
                        <Clock size={16} /> <span>आठवडा {weekNumber}</span>
                    </div>
                    <button className="nav-btn secondary" onClick={() => setWeekOffset(weekOffset + 1)}>
                        पुढील आठवडा <ChevronRight size={16} />
                    </button>
                </div>

                <div className="day-nav">
                    {vaarList.map(vaar => (
                        <button key={vaar.code} className={`day-btn ${selectedVaarCode === vaar.code ? 'active' : ''}`} onClick={() => setSelectedVaarCode(vaar.code)}>
                            {vaar.name}
                        </button>
                    ))}
                </div>

                <div className="filter-bar">
                    <div className="search-box">
                        <Search size={16} />
                        <input
                            type="text"
                            placeholder="सदस्य शोधा"
                            value={searchQuery}
                            onChange={e => setSearchQuery(e.target.value)}
                        />
                    </div>
                    <label className="checkbox">
                        <input type="checkbox" checked={filterUnassignedOnly} onChange={() => setFilterUnassignedOnly(prev => !prev)} /> फक्त नियुक्त नसलेले
                    </label>
                </div>
            </div>

            <div className="main-content">
                {renderGrid(members, 'पुरुष सदस्य', <Users size={20} className="section-icon male" />)}
                {renderGrid(femaleMembers, 'महिला सदस्य', <Users size={20} className="section-icon female" />)}
                <div className="history-box">
                    <h3><History size={16} /> अलीकडील बदल</h3>
                    <ul>
                        {history.slice().reverse().map((entry, i) => <li key={i}>{entry}</li>)}
                    </ul>
                </div>
            </div>
        </div>
    );
}

export default AssignmentGrid;
