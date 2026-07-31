
import { useCallback, useEffect, useState, useRef } from "react";
import { jwtDecode } from "jwt-decode";

import { AuthContext } from "./AuthContext";

import {
    getAccessToken,
    logout as logoutUser
} from "../services/authService";

import {
    exchangeCodeForToken
} from "../services/keycloakTokenService";

import {
    syncUser
} from "../services/userService";

import { getCurrentTenant } from "../services/tenantService";


const clearAuthCallback = () => {

    const nextUrl =
        new URL(
            window.location.href
        );


    nextUrl.search = "";


    window.history.replaceState(
        {},
        document.title,
        `${nextUrl.pathname}${nextUrl.hash}`
    );

};




function getUserRole(token) {

    if (!token) {
        return null;
    }


    try {

        const decoded =
            jwtDecode(token);


        const roles =
            decoded?.realm_access?.roles || [];



        if (roles.includes("PLATFORM_ADMIN")) {

            return "PLATFORM_ADMIN";

        }



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


    const processedCode =
        useRef(false);



    const [token, setToken] =
        useState(
            getAccessToken()
        );


    const [loading, setLoading] =
        useState(true);



    const [tenant, setTenant] =
        useState(null);




    const role =
        getUserRole(token);



    const isAdmin =
        role === "ADMIN";


    const isPlatformAdmin =
        role === "PLATFORM_ADMIN";





    const loadTenant =
        useCallback(async () => {


            if (!token) {

                setTenant(null);

                return;

            }



            try {


                const response =
                    await getCurrentTenant();



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


        }, [token]);








    /*
     * Handle Keycloak authorization code
     *
     * Example:
     * http://localhost:5173/login?code=xxxxx
     */
    useEffect(() => {


        const params =
            new URLSearchParams(
                window.location.search
            );



        const code =
            params.get("code");



        if (code && !processedCode.current) {


            processedCode.current = true;


            setLoading(true);



            exchangeCodeForToken(code)

                .then(async tokens => {



                    if (!tokens?.access_token) {


                        throw new Error(
                            "No access token returned by Keycloak"
                        );

                    }




                    localStorage.setItem(
                        "accessToken",
                        tokens.access_token
                    );




                    if (tokens.refresh_token) {


                        localStorage.setItem(
                            "refreshToken",
                            tokens.refresh_token
                        );


                    }


                    if (tokens.id_token) {


                        localStorage.setItem(
                            "idToken",
                            tokens.id_token
                        );


                    }



                    setToken(
                        tokens.access_token
                    );




                    await syncUser();




                    clearAuthCallback();



                })


                .catch(error => {


                    console.error(
                        "Unable to exchange Keycloak code",
                        error
                    );



                    clearAuthCallback();


                    processedCode.current = false;


                })


                .finally(() => {


                    setLoading(false);


                });



            return;

        }



        setLoading(false);



    }, []);








    useEffect(() => {


        if (isAdmin) {


            loadTenant();


        } else {


            setTenant(null);


        }


    }, [isAdmin, loadTenant]);









    const login = (accessToken) => {


        setToken(
            accessToken
        );


    };









    const logout = () => {


        setTenant(null);


        setToken(null);


        setLoading(false);


        processedCode.current = false;


        clearAuthCallback();


        localStorage.removeItem("accessToken");
        localStorage.removeItem("refreshToken");
        localStorage.removeItem("idToken");


        sessionStorage.clear();


        logoutUser();


    };









    const isAuthenticated =
        Boolean(token);








    return (

        <AuthContext.Provider

            value={{
                token,
                role,
                tenant,
                loading,
                isAuthenticated,

                isAdmin,
                isPlatformAdmin,

                login,
                logout
            }}

        >

            {children}

        </AuthContext.Provider>

    );

}

