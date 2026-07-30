import { useEffect, useState } from "react";
import { jwtDecode } from "jwt-decode";

import { AuthContext } from "./AuthContext";

import {
    getAccessToken,
    logout as logoutUser
} from "../services/authService";

import { getCurrentTenant } from "../services/tenantService";


function getUserRole(token) {

    if (!token) {
        return null;
    }

    try {

        const decoded = jwtDecode(token);

        const roles =
            decoded?.realm_access?.roles || [];

        if (roles.includes("ADMIN")) {
            return "ADMIN";
        }

        if (roles.includes("USER")) {
            return "USER";
        }

        return null;

    } catch (error) {

        console.error(error);

        return null;
    }
}


export function AuthProvider({ children }) {

    const [token, setToken] = useState(
        getAccessToken()
    );

    const [tenant, setTenant] =
        useState(null);


    const role =
        getUserRole(token);


    const isAdmin =
        role === "ADMIN";


    useEffect(() => {

        if (isAdmin) {

            loadTenant();

        } else {

            setTenant(null);

        }

    }, [token, isAdmin]);


    const loadTenant = async () => {

        if (!token) {

            setTenant(null);

            return;
        }

        try {

            const response =
                await getCurrentTenant();


            /*
             * Adjust this if your backend
             * returns a different property.
             */

            setTenant(
                response.domain
            );

        } catch (error) {

            console.error(
                "Unable to load tenant",
                error
            );

            setTenant(null);
        }
    };


    const login = (accessToken) => {

        setToken(accessToken);

    };


    const logout = () => {

        logoutUser();

        setTenant(null);

        setToken(null);

    };


    const isAuthenticated =
        Boolean(token);


    return (

        <AuthContext.Provider
            value={{
                token,
                role,
                tenant,
                isAuthenticated,
                isAdmin,
                login,
                logout
            }}
        >

            {children}

        </AuthContext.Provider>

    );
}