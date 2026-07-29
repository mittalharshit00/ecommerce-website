import { Link } from "react-router-dom";
import { useAuth } from "../context/useAuth";

function Dashboard() {

    const { logout } = useAuth();

    return (
        <div>

            {/* Header */}

            <header>
                <h1>E-Commerce Platform</h1>

                <nav>
                    <Link to="/dashboard">
                        Dashboard
                    </Link>

                    {" | "}

                    <Link to="/products">
                        Products
                    </Link>

                    {" | "}

                    <Link to="/categories">
                        Categories
                    </Link>

                    {" | "}

                    <Link to="/favourites">
                        Favourites
                    </Link>

                    {" | "}

                    <Link to="/orders">
                        Orders
                    </Link>

                    {" | "}

                    <Link to="/orders/create">
                        Create Order
                    </Link>

                    {" | "}

                    <button onClick={logout}>
                        Logout
                    </button>
                </nav>
            </header>

            <hr />

            {/* Main Dashboard */}

            <main>

                <h2>Dashboard</h2>

                <p>
                    Welcome to your e-commerce dashboard.
                </p>

                {/* Summary Cards */}

                <section>

                    <div>
                        <h3>Products</h3>

                        <p>
                            Manage your products
                        </p>

                        <Link to="/products">
                            View Products
                        </Link>
                    </div>

                    <div>
                        <h3>Categories</h3>

                        <p>
                            Manage product categories
                        </p>

                        <Link to="/categories">
                            View Categories
                        </Link>
                    </div>

                    <div>
                        <h3>Favourites</h3>

                        <p>
                            View your favourite products
                        </p>

                        <Link to="/favourites">
                            View Favourites
                        </Link>
                    </div>

                    <div>
                        <h3>Orders</h3>

                        <p>
                            View your orders
                        </p>

                        <Link to="/orders">
                            View Orders
                        </Link>
                    </div>

                </section>

                <hr />

                {/* Quick Actions */}

                <section>

                    <h2>Quick Actions</h2>

                    <p>
                        Quickly access the main features of
                        the platform.
                    </p>

                    <Link to="/products">
                        <button>
                            Browse Products
                        </button>
                    </Link>

                    {" "}

                    <Link to="/categories">
                        <button>
                            Manage Categories
                        </button>
                    </Link>

                    {" "}

                    <Link to="/favourites">
                        <button>
                            My Favourites
                        </button>
                    </Link>

                    {" "}

                    <Link to="/orders">
                        <button>
                            My Orders
                        </button>
                    </Link>

                    {" "}

                    <Link to="/orders/create">
                        <button>
                            Create Order
                        </button>
                    </Link>

                </section>

            </main>

        </div>
    );
}

export default Dashboard;