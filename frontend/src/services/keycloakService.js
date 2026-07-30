export const KEYCLOAK_URL =
    "http://localhost:8081";


export const REALM =
    "ecommerce";


export const CLIENT_ID =
    "ecommerce-api";


export const getRedirectUri = () =>
    `${window.location.origin}/dashboard`;


export const loginWithKeycloak = () => {


    const redirectUri =
        encodeURIComponent(
            getRedirectUri()
        );


    const params =
        new URLSearchParams({
            client_id: CLIENT_ID,
            redirect_uri: getRedirectUri(),
            response_type: "code",
            scope: "openid"
        });


    const url =
        `${KEYCLOAK_URL}/realms/${REALM}` +
        `/protocol/openid-connect/auth` +
        `?${params.toString()}`;


    window.location.href = url;

};