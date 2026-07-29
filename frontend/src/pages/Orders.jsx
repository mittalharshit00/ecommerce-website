import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

import { getOrders } from "../services/orderService";

function Orders() {

    const [orders, setOrders] = useState([]);

    const [page, setPage] = useState(0);

    const [totalPages, setTotalPages] = useState(0);

    const [loading, setLoading] = useState(true);

    const [error, setError] = useState("");

    useEffect(() => {
        loadOrders();
    }, [page]);

    const loadOrders = async () => {

        try {

            setLoading(true);

            setError("");

            const data = await getOrders(page, 10);

            setOrders(data.content);

            setTotalPages(data.totalPages);

        } catch (error) {

            console.error(error);

            setError("Unable to load orders.");

        } finally {

            setLoading(false);
        }
    };

    if (loading) {

        return (
            <div>
                <h1>My Orders</h1>
                <p>Loading...</p>
            </div>
        );
    }

    if (error) {

        return (
            <div>
                <h1>My Orders</h1>
                <p>{error}</p>
            </div>
        );
    }

    return (

        <div>

            <h1>My Orders</h1>

            {orders.length === 0 ? (

                <p>No orders found.</p>

            ) : (

                orders.map((order) => (

                    <div
                        key={order.id}
                        style={{
                            border: "1px solid #ccc",
                            padding: "15px",
                            marginBottom: "15px"
                        }}
                    >

                        <h3>
                            Order #{order.id}
                        </h3>

                        <p>
                            Status: {order.status}
                        </p>

                        <p>
                            Total Quantity: {order.totalQuantity}
                        </p>

                        <p>
                            Total Amount: ${order.totalAmount}
                        </p>

                        <Link
                            to={`/orders/${order.id}`}
                        >
                            View Details
                        </Link>

                    </div>

                ))

            )}

            {totalPages > 1 && (

                <div>

                    <button
                        disabled={page === 0}
                        onClick={() =>
                            setPage(page - 1)
                        }
                    >
                        Previous
                    </button>

                    {" "}

                    <span>
                        Page {page + 1} of {totalPages}
                    </span>

                    {" "}

                    <button
                        disabled={page >= totalPages - 1}
                        onClick={() =>
                            setPage(page + 1)
                        }
                    >
                        Next
                    </button>

                </div>

            )}

        </div>
    );
}

export default Orders;