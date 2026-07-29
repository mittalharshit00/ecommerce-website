import { useState } from "react";
import { jwtDecode } from "jwt-decode";

import { AuthContext } from "./AuthContext";

import {
    getAccessToken,
    logout as logoutUser
} from "../services/authService";

function getUserRole(token) {

    if (!token) {
        return null;
    }

    try {

        const decodedToken = jwtDecode(token);

        const roles =
            decodedToken?.realm_access?.roles || [];

        if (roles.includes("ADMIN")) {
            return "ADMIN";
        }

        if (roles.includes("USER")) {
            return "USER";
        }

        return null;

    } catch (error) {

        console.error(
            "Unable to decode access token:",
            error
        );

        return null;
    }
}

export function AuthProvider({ children }) {

    const [token, setToken] = useState(
        getAccessToken()
    );

    const login = (accessToken) => {

        // authService.login() is responsible
        // for storing the tokens.

        setToken(accessToken);
    };

    const logout = () => {

        logoutUser();

        setToken(null);
    };

    const isAuthenticated = Boolean(token);

    const role = getUserRole(token);

    const isAdmin = role === "ADMIN";

    return (
        <AuthContext.Provider
            value={{
                token,
                isAuthenticated,
                role,
                isAdmin,
                login,
                logout
            }}
        >
            {children}
        </AuthContext.Provider>
    );
}

