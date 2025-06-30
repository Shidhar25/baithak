import React, { useState } from 'react';

function ExcelDownload() {
    const [weekNumber, setWeekNumber] = useState('');
    const [downloading, setDownloading] = useState(false);

    const handleDownload = async (type) => {
        if (!weekNumber) {
            alert("Please enter a week number.");
            return;
        }

        const endpoint =
            type === "personalized"
                ? `${process.env.REACT_APP_BACKEND_URL}/api/excel/download/personalized/week/${weekNumber}`
                : `${process.env.REACT_APP_BACKEND_URL}/api/excel/download/week/${weekNumber}`;

        const filename =
            type === "personalized"
                ? `personal_week_${weekNumber}.xlsx`
                : `week_${weekNumber}_assignments.xlsx`;

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
            <input
                type="number"
                placeholder="Enter Week Number"
                value={weekNumber}
                onChange={(e) => setWeekNumber(e.target.value)}
                style={{ padding: "8px", marginRight: "10px", width: "200px" }}
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
    }
};

export default ExcelDownload;
