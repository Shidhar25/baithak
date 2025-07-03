import React, { useState } from 'react';
import { Download, FileText, Calendar, User, FileSpreadsheet, Clock, AlertCircle, CheckCircle } from 'lucide-react';

function ExcelDownload() {
    const [weekNumber, setWeekNumber] = useState('');
    const [startWeek, setStartWeek] = useState('');
    const [endWeek, setEndWeek] = useState('');
    const [memberName, setMemberName] = useState('');
    const [downloading, setDownloading] = useState(false);
    const [downloadStatus, setDownloadStatus] = useState('');

    const handleDownload = async (type) => {
        let endpoint = '';
        let filename = '';

        if (type === "normal" || type === "personalized") {
            if (!weekNumber) {
                setDownloadStatus('error');
                setTimeout(() => setDownloadStatus(''), 3000);
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
                setDownloadStatus('error');
                setTimeout(() => setDownloadStatus(''), 3000);
                return;
            }
            endpoint = `${process.env.REACT_APP_BACKEND_URL}/api/excel/matrix?start=${startWeek}&end=${endWeek}`;
            filename = `week_${startWeek}_to_${endWeek}_assignments.xlsx`;
        }

        if (type === "history") {
            if (!memberName) {
                setDownloadStatus('error');
                setTimeout(() => setDownloadStatus(''), 3000);
                return;
            }
            endpoint = `${process.env.REACT_APP_BACKEND_URL}/api/excel/history?memberName=${encodeURIComponent(memberName)}`;
            filename = `${memberName}_history.xlsx`;
        }

        setDownloading(true);
        setDownloadStatus('');

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
            setDownloadStatus('success');
            setTimeout(() => setDownloadStatus(''), 3000);
        } catch (error) {
            setDownloadStatus('error');
            setTimeout(() => setDownloadStatus(''), 3000);
        } finally {
            setDownloading(false);
        }
    };

    return (
        <div style={styles.container}>
            <div style={styles.maxWidth}>
                <div style={styles.mainCard}>
                    {/* Header */}
                    <div style={styles.header}>
                        <div style={styles.iconContainer}>
                            <FileSpreadsheet style={styles.headerIcon} />
                        </div>
                        <h1 style={styles.title}>Assignment Excel Downloads</h1>
                        <p style={styles.subtitle}>Generate and download assignment reports in Excel format</p>
                    </div>

                    {/* Status Messages */}
                    {downloadStatus === 'success' && (
                        <div style={styles.successMessage}>
                            <CheckCircle style={styles.messageIcon} />
                            <span style={styles.successText}>Download completed successfully!</span>
                        </div>
                    )}

                    {downloadStatus === 'error' && (
                        <div style={styles.errorMessage}>
                            <AlertCircle style={styles.messageIcon} />
                            <span style={styles.errorText}>Please fill in all required fields or check your connection.</span>
                        </div>
                    )}

                    <div style={styles.gridContainer}>
                        {/* Single Week Downloads */}
                        <div style={styles.card}>
                            <div style={styles.cardHeader}>
                                <Calendar style={styles.cardIcon} />
                                <h2 style={styles.cardTitle}>Weekly Reports</h2>
                            </div>

                            <div style={styles.inputContainer}>
                                <label style={styles.label}>
                                    Week Number
                                </label>
                                <input
                                    type="number"
                                    placeholder="Enter week number"
                                    value={weekNumber}
                                    onChange={(e) => setWeekNumber(e.target.value)}
                                    style={styles.input}
                                />
                            </div>

                            <div style={styles.buttonContainer}>
                                <button
                                    onClick={() => handleDownload("normal")}
                                    disabled={downloading}
                                    style={{...styles.button, ...styles.blueButton}}
                                >
                                    <FileText style={styles.buttonIcon} />
                                    {downloading ? 'Downloading...' : 'Regular Weekly Excel'}
                                </button>

                                <button
                                    onClick={() => handleDownload("personalized")}
                                    disabled={downloading}
                                    style={{...styles.button, ...styles.greenButton}}
                                >
                                    <User style={styles.buttonIcon} />
                                    {downloading ? 'Downloading...' : 'Personalized Excel'}
                                </button>
                            </div>
                        </div>

                        {/* Week Range Downloads */}
                        <div style={styles.card}>
                            <div style={styles.cardHeader}>
                                <Calendar style={styles.purpleIcon} />
                                <h2 style={styles.cardTitle}>Date Range Report</h2>
                            </div>

                            <div style={styles.rangeInputContainer}>
                                <div style={styles.rangeInputHalf}>
                                    <label style={styles.label}>
                                        Start Week
                                    </label>
                                    <input
                                        type="number"
                                        placeholder="Start"
                                        value={startWeek}
                                        onChange={(e) => setStartWeek(e.target.value)}
                                        style={styles.input}
                                    />
                                </div>
                                <div style={styles.rangeInputHalf}>
                                    <label style={styles.label}>
                                        End Week
                                    </label>
                                    <input
                                        type="number"
                                        placeholder="End"
                                        value={endWeek}
                                        onChange={(e) => setEndWeek(e.target.value)}
                                        style={styles.input}
                                    />
                                </div>
                            </div>

                            <button
                                onClick={() => handleDownload("range")}
                                disabled={downloading}
                                style={{...styles.button, ...styles.purpleButton}}
                            >
                                <Calendar style={styles.buttonIcon} />
                                {downloading ? 'Downloading...' : 'Download Range Report'}
                            </button>
                        </div>

                        {/* Member History */}
                        <div style={styles.fullWidthCard}>
                            <div style={styles.cardHeader}>
                                <Clock style={styles.amberIcon} />
                                <h2 style={styles.cardTitle}>Member History</h2>
                            </div>

                            <div style={styles.memberInputContainer}>
                                <label style={styles.label}>
                                    Member Name
                                </label>
                                <input
                                    type="text"
                                    placeholder="Enter member name"
                                    value={memberName}
                                    onChange={(e) => setMemberName(e.target.value)}
                                    style={styles.input}
                                />
                            </div>

                            <button
                                onClick={() => handleDownload("history")}
                                disabled={downloading}
                                style={{...styles.button, ...styles.amberButton}}
                            >
                                <User style={styles.buttonIcon} />
                                {downloading ? 'Downloading...' : 'Download Member History'}
                            </button>
                        </div>
                    </div>

                    {/* Loading Indicator */}
                    {downloading && (
                        <div style={styles.loadingContainer}>
                            <div style={styles.loadingBox}>
                                <div style={styles.spinner}></div>
                                <span style={styles.loadingText}>Preparing your download...</span>
                            </div>
                        </div>
                    )}

                    {/* Footer */}
                    <div style={styles.footer}>
                        <p style={styles.footerText}>
                            Downloads are generated in Excel format (.xlsx) and will start automatically
                        </p>
                    </div>
                </div>
            </div>
        </div>
    );
}

const styles = {
    container: {
        minHeight: '100vh',
        background: 'linear-gradient(135deg, #EBF8FF 0%, #E0E7FF 100%)',
        padding: '24px',
        fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif'
    },
    maxWidth: {
        maxWidth: '1024px',
        margin: '0 auto'
    },
    mainCard: {
        backgroundColor: '#ffffff',
        borderRadius: '16px',
        boxShadow: '0 25px 50px -12px rgba(0, 0, 0, 0.25)',
        padding: '32px'
    },
    header: {
        textAlign: 'center',
        marginBottom: '32px'
    },
    iconContainer: {
        display: 'inline-flex',
        alignItems: 'center',
        justifyContent: 'center',
        width: '64px',
        height: '64px',
        background: 'linear-gradient(135deg, #3B82F6 0%, #4F46E5 100%)',
        borderRadius: '50%',
        marginBottom: '16px'
    },
    headerIcon: {
        width: '32px',
        height: '32px',
        color: '#ffffff'
    },
    title: {
        fontSize: '30px',
        fontWeight: 'bold',
        color: '#111827',
        margin: '0 0 8px 0'
    },
    subtitle: {
        color: '#6B7280',
        margin: '0'
    },
    successMessage: {
        marginBottom: '24px',
        padding: '16px',
        backgroundColor: '#F0FDF4',
        border: '1px solid #BBF7D0',
        borderRadius: '8px',
        display: 'flex',
        alignItems: 'center'
    },
    errorMessage: {
        marginBottom: '24px',
        padding: '16px',
        backgroundColor: '#FEF2F2',
        border: '1px solid #FECACA',
        borderRadius: '8px',
        display: 'flex',
        alignItems: 'center'
    },
    messageIcon: {
        width: '20px',
        height: '20px',
        marginRight: '8px'
    },
    successText: {
        color: '#166534'
    },
    errorText: {
        color: '#991B1B'
    },
    gridContainer: {
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fit, minmax(400px, 1fr))',
        gap: '32px',
        '@media (max-width: 768px)': {
            gridTemplateColumns: '1fr'
        }
    },
    card: {
        backgroundColor: '#F9FAFB',
        borderRadius: '12px',
        padding: '24px',
        border: '1px solid #E5E7EB'
    },
    fullWidthCard: {
        backgroundColor: '#F9FAFB',
        borderRadius: '12px',
        padding: '24px',
        border: '1px solid #E5E7EB',
        gridColumn: '1 / -1'
    },
    cardHeader: {
        display: 'flex',
        alignItems: 'center',
        marginBottom: '16px'
    },
    cardIcon: {
        width: '24px',
        height: '24px',
        color: '#2563EB',
        marginRight: '12px'
    },
    purpleIcon: {
        width: '24px',
        height: '24px',
        color: '#7C3AED',
        marginRight: '12px'
    },
    amberIcon: {
        width: '24px',
        height: '24px',
        color: '#D97706',
        marginRight: '12px'
    },
    cardTitle: {
        fontSize: '20px',
        fontWeight: '600',
        color: '#111827',
        margin: '0'
    },
    inputContainer: {
        marginBottom: '16px'
    },
    memberInputContainer: {
        maxWidth: '400px',
        marginBottom: '16px'
    },
    rangeInputContainer: {
        display: 'grid',
        gridTemplateColumns: '1fr 1fr',
        gap: '12px',
        marginBottom: '16px'
    },
    rangeInputHalf: {
        display: 'flex',
        flexDirection: 'column'
    },
    label: {
        display: 'block',
        fontSize: '14px',
        fontWeight: '500',
        color: '#374151',
        marginBottom: '8px'
    },
    input: {
        width: '100%',
        padding: '12px 16px',
        border: '1px solid #D1D5DB',
        borderRadius: '8px',
        fontSize: '16px',
        transition: 'all 0.2s ease',
        boxSizing: 'border-box',
        ':focus': {
            outline: 'none',
            borderColor: '#3B82F6',
            boxShadow: '0 0 0 3px rgba(59, 130, 246, 0.1)'
        }
    },
    buttonContainer: {
        display: 'flex',
        flexDirection: 'column',
        gap: '12px'
    },
    button: {
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        padding: '12px 16px',
        borderRadius: '8px',
        border: 'none',
        fontSize: '16px',
        fontWeight: '500',
        cursor: 'pointer',
        transition: 'all 0.2s ease',
        transform: 'scale(1)',
        ':hover': {
            transform: 'scale(1.05)'
        },
        ':disabled': {
            opacity: '0.5',
            cursor: 'not-allowed',
            transform: 'scale(1)'
        }
    },
    blueButton: {
        backgroundColor: '#2563EB',
        color: '#ffffff',
        ':hover': {
            backgroundColor: '#1D4ED8'
        }
    },
    greenButton: {
        backgroundColor: '#16A34A',
        color: '#ffffff',
        ':hover': {
            backgroundColor: '#15803D'
        }
    },
    purpleButton: {
        backgroundColor: '#7C3AED',
        color: '#ffffff',
        ':hover': {
            backgroundColor: '#6D28D9'
        }
    },
    amberButton: {
        backgroundColor: '#D97706',
        color: '#ffffff',
        ':hover': {
            backgroundColor: '#B45309'
        }
    },
    buttonIcon: {
        width: '20px',
        height: '20px',
        marginRight: '8px'
    },
    loadingContainer: {
        marginTop: '32px',
        textAlign: 'center'
    },
    loadingBox: {
        display: 'inline-flex',
        alignItems: 'center',
        padding: '12px 24px',
        backgroundColor: '#EBF8FF',
        borderRadius: '8px',
        border: '1px solid #BAE6FD'
    },
    spinner: {
        width: '20px',
        height: '20px',
        border: '2px solid #E5E7EB',
        borderTop: '2px solid #2563EB',
        borderRadius: '50%',
        animation: 'spin 1s linear infinite',
        marginRight: '12px'
    },
    loadingText: {
        color: '#1E40AF',
        fontWeight: '500'
    },
    footer: {
        marginTop: '32px',
        paddingTop: '24px',
        borderTop: '1px solid #E5E7EB'
    },
    footerText: {
        textAlign: 'center',
        color: '#6B7280',
        fontSize: '14px',
        margin: '0'
    }
};

// Add CSS animation for spinner
const styleSheet = document.createElement('style');
styleSheet.textContent = `
    @keyframes spin {
        0% { transform: rotate(0deg); }
        100% { transform: rotate(360deg); }
    }
`;
document.head.appendChild(styleSheet);

export default ExcelDownload;