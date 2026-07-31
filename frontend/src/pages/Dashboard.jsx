
import { Link } from "react-router-dom";
import { useAuth } from "../context/useAuth";

function Dashboard() {


    const {
        isPlatformAdmin
    } = useAuth();



    const logoutUrl =
        `${window.location.origin}/logout`;



    const navItems = [
        { to: "/dashboard", label: "Dashboard" },
        { to: "/products", label: "Products" },
        { to: "/categories", label: "Categories" },
        { to: "/favourites", label: "Favourites" },
        { to: "/orders", label: "Orders" },
        { to: "/orders/create", label: "Create Order" }
    ];



    return (

        <div className="min-vh-100 bg-light">


            <header className="bg-white shadow-sm border-bottom">


                <div className="container py-3 d-flex flex-column flex-lg-row align-items-lg-center justify-content-between gap-3">


                    <div>

                        <h1 className="h3 mb-1">
                            E-Commerce Platform
                        </h1>


                        <p className="text-muted mb-0">
                            Manage products, orders and customer favourites
                        </p>

                    </div>




                    <nav className="d-flex flex-wrap align-items-center gap-2">


                        {
                            navItems.map(item => (

                                <Link
                                    key={item.to}
                                    to={item.to}
                                    className="nav-link px-3 py-2 rounded-pill"
                                >
                                    {item.label}
                                </Link>

                            ))
                        }



                        {
                            isPlatformAdmin && (

                                <Link
                                    to="/platform"
                                    className="btn btn-warning btn-sm"
                                >
                                    Platform Administration
                                </Link>

                            )
                        }





                        <a
                            href={logoutUrl}
                            onClick={(event) => {

                                event.preventDefault();

                                window.location.replace(logoutUrl);

                            }}
                            className="btn btn-dark btn-sm"
                        >
                            Logout
                        </a>



                    </nav>


                </div>


            </header>





            <main className="container py-4">


                <div 
                    className="card card-soft border-0 p-4 p-lg-5 mb-4 text-white"
                    style={{
                        background:
                            "linear-gradient(90deg, #4f46e5 0%, #7c3aed 100%)"
                    }}
                >


                    <h2 className="h3 mb-2">
                        Dashboard
                    </h2>


                    <p 
                        className="mb-0"
                        style={{
                            maxWidth: 700
                        }}
                    >
                        Welcome back. Keep your storefront up-to-date,
                        monitor orders and manage customer favourites
                        from one place.
                    </p>


                </div>






                <div className="row g-4">


                    {
                        [
                            {
                                title: "Products",
                                description:
                                    "Manage your product catalogue",
                                link: "/products",
                                label: "View Products"
                            },


                            {
                                title: "Categories",
                                description:
                                    "Organise your inventory structure",
                                link: "/categories",
                                label: "View Categories"
                            },


                            {
                                title: "Favourites",
                                description:
                                    "Explore customer favourites",
                                link: "/favourites",
                                label: "View Favourites"
                            },


                            {
                                title: "Orders",
                                description:
                                    "Track current customer orders",
                                link: "/orders",
                                label: "View Orders"
                            }


                        ].map(card => (


                            <div
                                key={card.title}
                                className="col-12 col-md-6 col-xl-3"
                            >


                                <div className="card card-soft h-100 border-0">


                                    <div className="card-body">


                                        <h3 className="h5">
                                            {card.title}
                                        </h3>


                                        <p className="text-muted">
                                            {card.description}
                                        </p>



                                        <Link
                                            to={card.link}
                                            className="btn btn-outline-primary btn-sm"
                                        >
                                            {card.label}
                                        </Link>



                                    </div>


                                </div>


                            </div>


                        ))
                    }


                </div>






                <div className="card card-soft border-0 mt-4">


                    <div className="card-body">


                        <h3 className="h5">
                            Quick Actions
                        </h3>


                        <p className="text-muted">
                            Jump into the most common workflows.
                        </p>



                        <div className="d-flex flex-wrap gap-2">


                            <Link
                                to="/products"
                                className="btn btn-primary"
                            >
                                Browse Products
                            </Link>



                            <Link
                                to="/categories"
                                className="btn btn-outline-secondary"
                            >
                                Manage Categories
                            </Link>



                            <Link
                                to="/favourites"
                                className="btn btn-outline-secondary"
                            >
                                My Favourites
                            </Link>



                            <Link
                                to="/orders"
                                className="btn btn-outline-secondary"
                            >
                                My Orders
                            </Link>



                            <Link
                                to="/orders/create"
                                className="btn btn-outline-secondary"
                            >
                                Create Order
                            </Link>



                        </div>


                    </div>


                </div>




            </main>


        </div>

    );

}


export default Dashboard;

