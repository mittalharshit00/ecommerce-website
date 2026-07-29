import { useState } from "react";
import { useNavigate } from "react-router-dom";

import { login as loginRequest } from "../services/authService";
import { useAuth } from "../context/useAuth";

function Login() {

    const navigate = useNavigate();
    const { login } = useAuth();

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");

    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (event) => {

        event.preventDefault();

        setError("");
        setLoading(true);

        try {

            const response = await loginRequest(
                username,
                password
            );

            login(response.access_token);

            navigate("/dashboard");

        } catch (error) {

            console.error(error);

            setError(
                "Invalid username or password."
            );

        } finally {

            setLoading(false);

        }
    };

    return (
        <div>

            <h1>Login</h1>

            <form onSubmit={handleSubmit}>

                <div>
                    <label>
                        Username
                    </label>

                    <br />

                    <input
                        type="text"
                        value={username}
                        onChange={(event) =>
                            setUsername(event.target.value)
                        }
                        required
                    />
                </div>

                <br />

                <div>
                    <label>
                        Password
                    </label>

                    <br />

                    <input
                        type="password"
                        value={password}
                        onChange={(event) =>
                            setPassword(event.target.value)
                        }
                        required
                    />
                </div>

                <br />

                {error && (
                    <p>{error}</p>
                )}

                <button
                    type="submit"
                    disabled={loading}
                >
                    {loading
                        ? "Logging in..."
                        : "Login"}
                </button>

            </form>

        </div>
    );
}

export default Login;