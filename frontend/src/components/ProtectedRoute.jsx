
import { Navigate } from "react-router-dom";
import { useAuth } from "../context/useAuth";


export default function ProtectedRoute({
    children,
    requiredRole
}) {


    const {
        isAuthenticated,
        loading,
        role
    } = useAuth();





    if (loading) {

        return (

            <div>
                Authenticating...
            </div>

        );

    }






    if (!isAuthenticated) {

        return (

            <Navigate
                to="/login"
                replace
            />

        );

    }







    if (
        requiredRole &&
        role !== requiredRole
    ) {

        return (

            <Navigate
                to="/dashboard"
                replace
            />

        );

    }






    return children;


}

