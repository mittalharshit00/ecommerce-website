import { useEffect } from "react";
import { logout as logoutUser } from "../services/authService";

function Logout() {
    useEffect(() => {
        logoutUser();
    }, []);

    return null;
}

export default Logout;
