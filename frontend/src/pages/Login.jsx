import { useEffect } from "react";

import {
    loginWithKeycloak
} from "../services/keycloakService";


function Login() {


    useEffect(() => {

        loginWithKeycloak();

    }, []);


    return (

        <div>

            <p>
                Redirecting to login...
            </p>

        </div>

    );
}


export default Login;