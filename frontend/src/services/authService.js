import axios from "axios";


const KEYCLOAK_URL =
    "http://localhost:8081";


const REALM =
    "ecommerce";


const KEYCLOAK_TOKEN_URL =
    `${KEYCLOAK_URL}/realms/${REALM}/protocol/openid-connect/token`;


const CLIENT_ID =
    "ecommerce-api";



/*
 * Get current access token
 */
export const getAccessToken = () => {

    return localStorage.getItem(
        "accessToken"
    );

};




/*
 * Get refresh token
 */
export const getRefreshToken = () => {

    return localStorage.getItem(
        "refreshToken"
    );

};


/*
 * Get ID token
 */
export const getIdToken = () => {

    return localStorage.getItem(
        "idToken"
    );

};




/*
 * Refresh access token
 */
export const refreshAccessToken = async () => {


    const refreshToken =
        getRefreshToken();



    if (!refreshToken) {

        throw new Error(
            "Refresh token is missing."
        );

    }



    const formData =
        new URLSearchParams();



    formData.append(
        "grant_type",
        "refresh_token"
    );



    formData.append(
        "client_id",
        CLIENT_ID
    );



    formData.append(
        "refresh_token",
        refreshToken
    );



    const response =
        await axios.post(

            KEYCLOAK_TOKEN_URL,

            formData,

            {
                headers: {

                    "Content-Type":
                        "application/x-www-form-urlencoded"

                }
            }

        );



    const data =
        response.data;



    localStorage.setItem(
        "accessToken",
        data.access_token
    );



    if (data.refresh_token) {


        localStorage.setItem(
            "refreshToken",
            data.refresh_token
        );


    }



    return data.access_token;

};




/*
 * Logout from application
 *
 * Clears React tokens
 * and redirects user to Keycloak logout.
 */
export const logout = () => {


    const idToken = getIdToken();


    localStorage.removeItem(
        "accessToken"
    );


    localStorage.removeItem(
        "refreshToken"
    );


    localStorage.removeItem(
        "idToken"
    );


    const params =
        new URLSearchParams({
            client_id: CLIENT_ID,
            post_logout_redirect_uri: `${window.location.origin}/`
        });


    if (idToken) {

        params.set(
            "id_token_hint",
            idToken
        );

    }


    const logoutUrl =
        `${KEYCLOAK_URL}/realms/${REALM}` +
        `/protocol/openid-connect/logout` +
        `?${params.toString()}`;


    window.location.replace(
        logoutUrl
    );

};