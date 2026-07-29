
import axios from "axios";

const KEYCLOAK_TOKEN_URL =
    "http://localhost:8081/realms/ecommerce/protocol/openid-connect/token";

const CLIENT_ID = "ecommerce-api";


/*
 * Login
 *
 * Keycloak returns:
 * - access_token
 * - refresh_token
 * - expires_in
 * - refresh_expires_in
 */
export const login = async (username, password) => {

    const formData = new URLSearchParams();

    formData.append("grant_type", "password");
    formData.append("client_id", CLIENT_ID);
    formData.append("username", username);
    formData.append("password", password);

    const response = await axios.post(
        KEYCLOAK_TOKEN_URL,
        formData,
        {
            headers: {
                "Content-Type":
                    "application/x-www-form-urlencoded"
            }
        }
    );

    const data = response.data;

    /*
     * Store BOTH tokens.
     */
    localStorage.setItem(
        "accessToken",
        data.access_token
    );

    localStorage.setItem(
        "refreshToken",
        data.refresh_token
    );

    return data;
};


/*
 * Get current access token.
 */
export const getAccessToken = () => {

    return localStorage.getItem(
        "accessToken"
    );
};


/*
 * Get refresh token.
 */
export const getRefreshToken = () => {

    return localStorage.getItem(
        "refreshToken"
    );
};


/*
 * Use refresh token to obtain
 * a new access token.
 */
export const refreshAccessToken = async () => {

    const refreshToken =
        getRefreshToken();

    if (!refreshToken) {
        throw new Error(
            "Refresh token is missing."
        );
    }

    const formData = new URLSearchParams();

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

    const response = await axios.post(
        KEYCLOAK_TOKEN_URL,
        formData,
        {
            headers: {
                "Content-Type":
                    "application/x-www-form-urlencoded"
            }
        }
    );

    const data = response.data;

    /*
     * Keycloak normally returns
     * a new access token.
     */
    localStorage.setItem(
        "accessToken",
        data.access_token
    );

    /*
     * If Keycloak rotates the refresh token,
     * save the new one.
     */
    if (data.refresh_token) {

        localStorage.setItem(
            "refreshToken",
            data.refresh_token
        );
    }

    return data.access_token;
};


/*
 * Logout
 */
export const logout = () => {

    localStorage.removeItem(
        "accessToken"
    );

    localStorage.removeItem(
        "refreshToken"
    );
};

