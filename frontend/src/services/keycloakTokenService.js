import {
    CLIENT_ID,
    getRedirectUri
} from "./keycloakService";


export const exchangeCodeForToken = async (code) => {


    const params =
        new URLSearchParams();



    params.append(
        "grant_type",
        "authorization_code"
    );



    params.append(
        "client_id",
        CLIENT_ID
    );



    params.append(
        "code",
        code
    );



    params.append(
        "redirect_uri",
        getRedirectUri()
    );



    console.log(
        "Sending code exchange"
    );



    const response = await fetch(

        "http://localhost:8081/realms/ecommerce/protocol/openid-connect/token",

        {
            method: "POST",


            headers: {

                "Content-Type":
                    "application/x-www-form-urlencoded"

            },


            body:
                params.toString()

        }

    );



    console.log(
        "TOKEN STATUS",
        response.status
    );

    const data = await response.json();

    console.log(
        "TOKEN RESPONSE",
        data
    );



    if (!response.ok) {


        const error =
            await response.text();



        console.error(
            "Keycloak token exchange failed:",
            error
        );



        throw new Error(
            "Token exchange failed"
        );


    }



    return data;

};