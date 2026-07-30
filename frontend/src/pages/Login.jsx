import { useEffect } from "react";

import {
    loginWithKeycloak
} from "../services/keycloakService";


function Login() {


    useEffect(() => {

        loginWithKeycloak();

    }, []);


    return (
        <div className="min-vh-100 d-flex align-items-center justify-content-center bg-dark text-white px-3">
            <div className="card card-soft border-0 p-4" style={{ width: "100%", maxWidth: "480px", backgroundColor: "rgba(255,255,255,0.95)" }}>
                <div className="d-flex align-items-center gap-3 mb-4">
                    <div className="rounded-circle bg-primary text-white d-flex align-items-center justify-content-center" style={{ width: 48, height: 48, fontWeight: 700 }}>
                        E
                    </div>
                    <div>
                        <h2 className="h5 mb-1 text-dark">E-Commerce Platform</h2>
                        <p className="text-muted mb-0">Redirecting you to Keycloak</p>
                    </div>
                </div>

                <div className="d-flex align-items-center gap-2 text-secondary">
                    <div className="spinner-border spinner-border-sm text-primary" role="status" aria-hidden="true" />
                    <span>Signing you in securely...</span>
                </div>
            </div>
        </div>
    );
}


export default Login;