import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

import { useAuth } from "../context/useAuth";

import {
    getOrders,
    getAdminOrders
} from "../services/orderService";


function Orders() {

    const { isAdmin, tenant } = useAuth();

    const [orders, setOrders] = useState([]);

    const [page, setPage] = useState(0);

    const [totalPages, setTotalPages] = useState(0);

    const [loading, setLoading] = useState(true);

    const [error, setError] = useState("");


    const logoutUrl =
        `${window.location.origin}/logout`;


    useEffect(() => {

        loadOrders();

    }, [page]);


    const loadOrders = async () => {

        try {

            setLoading(true);

            setError("");

            const data = isAdmin
                ? await getAdminOrders(
                    tenant,
                    page,
                    10
                )
                : await getOrders(
                    page,
                    10
                );


            setOrders(data.content);

            setTotalPages(data.totalPages);


        } catch (error) {

            console.error(error);

            setError(
                "Unable to load orders."
            );

        } finally {

            setLoading(false);

        }

    };


    if (loading) {

        return (

            <div>

                <header>

                    <h1>
                        Orders
                    </h1>

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

                        <a
                            href={logoutUrl}
                            onClick={(event) => {

                                event.preventDefault();

                                window.location.replace(
                                    `${window.location.origin}/logout`
                                );

                            }}
                        >
                            Logout
                        </a>

                    </nav>

                </header>


                <p>Loading...</p>

            </div>

        );

    }


    if (error) {

        return (

            <div>

                <header>

                    <h1>
                        Orders
                    </h1>

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

                        <a
                            href={logoutUrl}
                            onClick={(event) => {

                                event.preventDefault();

                                window.location.replace(
                                    `${window.location.origin}/logout`
                                );

                            }}
                        >
                            Logout
                        </a>

                    </nav>

                </header>


                <p>{error}</p>

            </div>

        );

    }


    return (

        <div>

            <header>

                <h1>
                    Orders
                </h1>

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

                    <a
                        href={logoutUrl}
                        onClick={(event) => {

                            event.preventDefault();

                            window.location.replace(
                                `${window.location.origin}/logout`
                            );

                        }}
                    >
                        Logout
                    </a>

                </nav>

            </header>


            <h1>
                {isAdmin ? "Tenant Orders" : "My Orders"}
            </h1>


            {
                orders.length === 0 ? (

                    <p>
                        No orders found.
                    </p>

                ) : (

                    orders.map((order) => (

                        <div
                            key={order.id}
                            style={{
                                border:"1px solid #ccc",
                                padding:"15px",
                                marginBottom:"15px"
                            }}
                        >

                            <h3>
                                Order #{order.id}
                            </h3>


                            {
                                isAdmin &&
                                order.user && (

                                    <>
                                        <p>
                                            Customer:
                                            {" "}
                                            {order.user.fullName}
                                        </p>

                                        <p>
                                            Email:
                                            {" "}
                                            {order.user.email}
                                        </p>
                                    </>

                                )
                            }


                            <p>
                                Status:
                                {" "}
                                {order.status}
                            </p>


                            <p>
                                Total Quantity:
                                {" "}
                                {order.totalQuantity}
                            </p>


                            <p>
                                Total Amount:
                                {" "}
                                ${order.totalAmount}
                            </p>


                            <Link
                                to={`/orders/${order.id}`}
                            >
                                View Details
                            </Link>

                        </div>

                    ))

                )
            }


            {
                totalPages > 1 && (

                    <div>

                        <button
                            disabled={page===0}
                            onClick={() =>
                                setPage(page-1)
                            }
                        >
                            Previous
                        </button>


                        {" "}

                        Page {page+1} of {totalPages}

                        {" "}


                        <button
                            disabled={
                                page>=totalPages-1
                            }
                            onClick={() =>
                                setPage(page+1)
                            }
                        >
                            Next
                        </button>

                    </div>

                )
            }


        </div>

    );

}


export default Orders;