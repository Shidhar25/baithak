import React, { useState } from 'react';

function ExcelDownload() {
    const [weekNumber, setWeekNumber] = useState('');
    const [startWeek, setStartWeek] = useState('');
    const [endWeek, setEndWeek] = useState('');
    const [memberName, setMemberName] = useState('');
    const [downloading, setDownloading] = useState(false);

    const handleDownload = async (type) => {
        let endpoint = '';
        let filename = '';

        if (type === "normal" || type === "personalized") {
            if (!weekNumber) {
                alert("Please enter a week number.");
                return;
            }
            if (type === "personalized") {
                endpoint = `${process.env.REACT_APP_BACKEND_URL}/api/excel/download/personalized/week/${weekNumber}`;
                filename = `personal_week_${weekNumber}.xlsx`;
            } else {
                endpoint = `${process.env.REACT_APP_BACKEND_URL}/api/excel/download/week/${weekNumber}`;
                filename = `week_${weekNumber}_assignments.xlsx`;
            }
        }

        if (type === "range") {
            if (!startWeek || !endWeek) {
                alert("Please enter both start and end week.");
                return;
            }
            endpoint = `${process.env.REACT_APP_BACKEND_URL}/api/excel/download/range?start=${startWeek}&end=${endWeek}`;
            filename = `week_${startWeek}_to_${endWeek}_assignments.xlsx`;
        }

        if (type === "history") {
            if (!memberName) {
                alert("Please enter member name.");
                return;
            }
            endpoint = `${process.env.REACT_APP_BACKEND_URL}/api/excel/history?memberName=${encodeURIComponent(memberName)}`;
            filename = `${memberName}_history.xlsx`;
        }

        setDownloading(true);
        try {
            const response = await fetch(endpoint);
            if (!response.ok) throw new Error("Download failed");
            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);
            const link = document.createElement("a");
            link.href = url;
            link.setAttribute("download", filename);
            document.body.appendChild(link);
            link.click();
            link.remove();
        } catch (error) {
            alert("Error downloading: " + error.message);
        } finally {
            setDownloading(false);
        }
    };

    return (
        <div style={{ padding: "20px", fontFamily: "Arial" }}>
            <h2>📥 Download Assignment Excel</h2>

            <div style={{ marginBottom: "20px" }}>
                <input
                    type="number"
                    placeholder="Enter Week Number"
                    value={weekNumber}
                    onChange={(e) => setWeekNumber(e.target.value)}
                    style={{ ...styles.input }}
                />
                <div style={{ marginTop: "10px" }}>
                    <button
                        onClick={() => handleDownload("normal")}
                        disabled={downloading}
                        style={styles.button}
                    >
                        📄 Regular Weekly Excel
                    </button>

                    <button
                        onClick={() => handleDownload("personalized")}
                        disabled={downloading}
                        style={{ ...styles.button, backgroundColor: "#28a745" }}
                    >
                        🧍 Personalized Excel
                    </button>
                </div>
            </div>

            <h3>📆 Week Range Download</h3>
            <div>
                <input
                    type="number"
                    placeholder="Start Week"
                    value={startWeek}
                    onChange={(e) => setStartWeek(e.target.value)}
                    style={{ ...styles.input }}
                />
                <input
                    type="number"
                    placeholder="End Week"
                    value={endWeek}
                    onChange={(e) => setEndWeek(e.target.value)}
                    style={{ ...styles.input }}
                />
            </div>
            <button
                onClick={() => handleDownload("range")}
                disabled={downloading}
                style={{ ...styles.button, marginTop: "10px", backgroundColor: "#6f42c1" }}
            >
                📑 Week Range Excel
            </button>

            <h3 style={{ marginTop: "30px" }}>🧾 Member History</h3>
            <div>
                <input
                    type="text"
                    placeholder="Enter Member Name"
                    value={memberName}
                    onChange={(e) => setMemberName(e.target.value)}
                    style={{ ...styles.input }}
                />
            </div>
            <button
                onClick={() => handleDownload("history")}
                disabled={downloading}
                style={{ ...styles.button, marginTop: "10px", backgroundColor: "#ffc107", color: "#000" }}
            >
                📜 Download Member History
            </button>

            {downloading && <p style={{ marginTop: "15px" }}>🔄 Downloading...</p>}
        </div>
    );
}

const styles = {
    button: {
        padding: "10px 20px",
        fontSize: "14px",
        marginRight: "10px",
        cursor: "pointer",
        borderRadius: "4px",
        backgroundColor: "#007bff",
        color: "#fff",
        border: "none",
    },
    input: {
        padding: "8px",
        marginRight: "10px",
        marginBottom: "10px",
        width: "200px",
    }
};

export default ExcelDownload;
