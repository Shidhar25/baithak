// src/components/Login.js
import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";

function Login() {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        username: "",
        password: ""
    });
    const [msg, setMsg] = useState("");
    const [isLoading, setIsLoading] = useState(false);
    const [showPassword, setShowPassword] = useState(false);
    const [loginAttempts, setLoginAttempts] = useState(0);
    const [isBlocked, setIsBlocked] = useState(false);
    const [blockTimer, setBlockTimer] = useState(0);

    // Block user after 5 failed attempts for 5 minutes
    useEffect(() => {
        if (loginAttempts >= 5) {
            setIsBlocked(true);
            setBlockTimer(300); // 5 minutes in seconds

            const timer = setInterval(() => {
                setBlockTimer(prev => {
                    if (prev <= 1) {
                        clearInterval(timer);
                        setIsBlocked(false);
                        setLoginAttempts(0);
                        return 0;
                    }
                    return prev - 1;
                });
            }, 1000);

            return () => clearInterval(timer);
        }
    }, [loginAttempts]);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
        // Clear error message when user starts typing
        if (msg) setMsg("");
    };

    const validateForm = () => {
        if (!formData.username.trim()) {
            setMsg("Username is required");
            return false;
        }
        if (!formData.password.trim()) {
            setMsg("Password is required");
            return false;
        }
        if (formData.username.length < 3) {
            setMsg("Username must be at least 3 characters long");
            return false;
        }
        if (formData.password.length < 6) {
            setMsg("Password must be at least 6 characters long");
            return false;
        }
        return true;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (isBlocked) {
            setMsg(`Too many failed attempts. Please wait ${Math.ceil(blockTimer / 60)} minutes.`);
            return;
        }

        if (!validateForm()) return;

        setIsLoading(true);
        setMsg("");

        try {
            const formDataToSend = new URLSearchParams();
            formDataToSend.append("username", formData.username.trim());
            formDataToSend.append("password", formData.password);

            const response = await fetch(`${process.env.REACT_APP_BACKEND_URL}/admin/login`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded",
                },
                body: formDataToSend.toString(),
            });

            const text = await response.text();

            if (response.ok && text === "Login successful") {
                // Store login state (consider using secure httpOnly cookies in production)
                sessionStorage.setItem("isLoggedIn", "true");
                sessionStorage.setItem("loginTime", Date.now().toString());

                setMsg("Login successful! Redirecting...");
                setTimeout(() => navigate("/grid"), 1000);
            } else {
                setLoginAttempts(prev => prev + 1);
                setMsg("Invalid credentials. Please try again.");
            }
        } catch (error) {
            console.error("Login error:", error);
            setMsg("Network error. Please check your connection and try again.");
        } finally {
            setIsLoading(false);
        }
    };

    const formatTime = (seconds) => {
        const mins = Math.floor(seconds / 60);
        const secs = seconds % 60;
        return `${mins}:${secs.toString().padStart(2, '0')}`;
    };

    return (
        <div style={styles.container}>
            <div style={styles.loginCard}>
                <div style={styles.header}>
                    <h2 style={styles.title}>॥ श्री राम समर्थ ॥</h2>
                    <div style={styles.blessing}>
                        <p>॥ जय जय रघुवीर समर्थ ॥</p>
                    </div>
                </div>

                <form onSubmit={handleSubmit} style={styles.form}>
                    <div style={styles.inputGroup}>
                        <input
                            type="text"
                            name="username"
                            placeholder="Username"
                            value={formData.username}
                            onChange={handleInputChange}
                            style={styles.input}
                            disabled={isLoading || isBlocked}
                            required
                        />
                    </div>

                    <div style={styles.inputGroup}>
                        <div style={styles.passwordContainer}>
                            <input
                                type={showPassword ? "text" : "password"}
                                name="password"
                                placeholder="Password"
                                value={formData.password}
                                onChange={handleInputChange}
                                style={styles.input}
                                disabled={isLoading || isBlocked}
                                required
                            />
                            <button
                                type="button"
                                onClick={() => setShowPassword(!showPassword)}
                                style={styles.eyeButton}
                                disabled={isLoading || isBlocked}
                            >
                                {showPassword ? "👁️" : "👁️‍🗨️"}
                            </button>
                        </div>
                    </div>

                    <button
                        type="submit"
                        style={{
                            ...styles.submitButton,
                            ...(isLoading || isBlocked ? styles.disabledButton : {})
                        }}
                        disabled={isLoading || isBlocked}
                    >
                        {isLoading ? "Logging in..." : "Login"}
                    </button>
                </form>

                {msg && (
                    <div style={{
                        ...styles.message,
                        color: msg.includes("successful") ? "#28a745" : "#dc3545"
                    }}>
                        {msg}
                    </div>
                )}

                {isBlocked && (
                    <div style={styles.blockMessage}>
                        Account temporarily blocked. Time remaining: {formatTime(blockTimer)}
                    </div>
                )}

                <div style={styles.footer}>
                    <p style={styles.attemptsInfo}>
                        {loginAttempts > 0 && loginAttempts < 5 &&
                            `Failed attempts: ${loginAttempts}/5`
                        }
                    </p>
                </div>
            </div>
        </div>
    );
}

const styles = {
    container: {
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        minHeight: "100vh",
        backgroundColor: "#f5f5f5",
        padding: "20px"
    },
    loginCard: {
        backgroundColor: "white",
        padding: "40px",
        borderRadius: "12px",
        boxShadow: "0 4px 6px rgba(0, 0, 0, 0.1)",
        width: "100%",
        maxWidth: "400px",
        border: "1px solid #e0e0e0"
    },
    header: {
        textAlign: "center",
        marginBottom: "30px"
    },
    title: {
        color: "#333",
        marginBottom: "20px",
        fontSize: "24px",
        fontWeight: "600"
    },
    blessing: {
        color: "#ff6b35",
        fontSize: "14px",
        fontWeight: "500",
        lineHeight: "1.4"
    },
    form: {
        display: "flex",
        flexDirection: "column",
        gap: "20px"
    },
    inputGroup: {
        display: "flex",
        flexDirection: "column"
    },
    input: {
        padding: "12px 16px",
        fontSize: "16px",
        border: "2px solid #e0e0e0",
        borderRadius: "8px",
        outline: "none",
        transition: "border-color 0.3s ease",
        backgroundColor: "white"
    },
    passwordContainer: {
        position: "relative",
        display: "flex",
        alignItems: "center"
    },
    eyeButton: {
        position: "absolute",
        right: "12px",
        background: "none",
        border: "none",
        cursor: "pointer",
        fontSize: "16px",
        padding: "4px"
    },
    submitButton: {
        padding: "12px 24px",
        fontSize: "16px",
        backgroundColor: "#007bff",
        color: "white",
        border: "none",
        borderRadius: "8px",
        cursor: "pointer",
        transition: "background-color 0.3s ease",
        fontWeight: "500"
    },
    disabledButton: {
        backgroundColor: "#6c757d",
        cursor: "not-allowed"
    },
    message: {
        textAlign: "center",
        marginTop: "20px",
        fontSize: "14px",
        fontWeight: "500"
    },
    blockMessage: {
        textAlign: "center",
        marginTop: "20px",
        color: "#dc3545",
        fontSize: "14px",
        fontWeight: "500",
        backgroundColor: "#f8d7da",
        padding: "10px",
        borderRadius: "6px",
        border: "1px solid #f5c6cb"
    },
    footer: {
        marginTop: "20px",
        textAlign: "center"
    },
    attemptsInfo: {
        color: "#6c757d",
        fontSize: "12px",
        margin: "0"
    }
};

export default Login;