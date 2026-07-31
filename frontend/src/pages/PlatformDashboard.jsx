import { Link } from "react-router-dom";

function PlatformDashboard() {

    const logoutUrl =
        `${window.location.origin}/logout`;

    return (

        <div className="container mt-5">

            <h1 className="mb-4">
                Platform Administration
            </h1>

            <div className="row g-4">

                <div className="col-md-6">

                    <div className="card h-100">

                        <div className="card-body">

                            <h5 className="card-title">
                                Create Tenant
                            </h5>

                            <p className="card-text">
                                Create a new tenant for the platform.
                            </p>

                            <Link
                                to="/platform/tenants/create"
                                className="btn btn-primary"
                            >
                                Create Tenant
                            </Link>

                        </div>

                    </div>

                </div>

                <div className="col-md-6">

                    <div className="card h-100">

                        <div className="card-body">

                            <h5 className="card-title">
                                Assign Tenant Admin
                            </h5>

                            <p className="card-text">
                                Promote a global user to a tenant administrator.
                            </p>

                            <Link
                                to="/platform/admins/assign"
                                className="btn btn-success"
                            >
                                Assign Admin
                            </Link>

                        </div>

                    </div>

                </div>

            </div>

            <div className="mt-5">

                <a
                    href={logoutUrl}
                    className="btn btn-outline-danger"
                    onClick={(event) => {

                        event.preventDefault();

                        window.location.replace(
                            logoutUrl
                        );

                    }}
                >
                    Logout
                </a>

            </div>

        </div>

    );

}

export default PlatformDashboard;