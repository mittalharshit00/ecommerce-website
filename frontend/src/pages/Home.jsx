import { Navigate } from "react-router-dom";
import { useAuth } from "../context/useAuth";


function Home() {

    const {
        isAuthenticated
    } = useAuth();


    if (isAuthenticated) {

        return (
            <Navigate
                to="/dashboard"
            />
        );

    }


    return (
        <Navigate
            to="/login"
        />
    );
}


export default Home;