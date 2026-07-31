import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

import { useAuth } from "../context/useAuth";

import {
    getAdminOrders,
    updateOrderStatus
} from "../services/orderService";

function AdminOrders() {

    const { tenant } = useAuth();

    const [orders, setOrders] = useState([]);

    const [page, setPage] = useState(0);

    const [totalPages, setTotalPages] = useState(0);

    const [loading, setLoading] = useState(true);

    const [error, setError] = useState("");

    useEffect(() => {
        if (!tenant) {
            setOrders([]);
            setTotalPages(0);
            setLoading(false);
            return;
        }

        loadOrders();
    }, [page, tenant]);

    const loadOrders = async () => {

        try {

            setLoading(true);

            setError("");

            if (!tenant) {
                setOrders([]);
                setTotalPages(0);
                return;
            }

            const data = await getAdminOrders(
                tenant,
                page,
                10
            );

            setOrders(data.content);

            setTotalPages(data.totalPages);

        } catch (error) {

            console.error(error);

            setError("Unable to load orders.");

        } finally {

            setLoading(false);
        }
    };

    const handleStatusUpdate = async (
        orderId,
        status
    ) => {

        try {

            if (!tenant) {
                throw new Error("No tenant context available.");
            }

            await updateOrderStatus(
                tenant,
                orderId,
                status
            );

            alert("Order status updated.");

            loadOrders();

        } catch (error) {

            console.error(error);

            alert(
                error.response?.data?.message ||
                "Unable to update status."
            );
        }
    };

    if (loading) {

        return <p>Loading orders...</p>;
    }

    if (error) {

        return <p>{error}</p>;
    }

    return (

        <div>

            <h1>Tenant Orders</h1>

            {orders.length === 0 ? (

                <p>No orders found.</p>

            ) : (

                orders.map(order => (

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

                        {order.user && (

                            <>

                                <p>

                                    <strong>Customer:</strong>{" "}

                                    {order.user.fullName}

                                </p>

                                <p>

                                    <strong>Email:</strong>{" "}

                                    {order.user.email}

                                </p>

                            </>

                        )}

                        <p>

                            <strong>Status:</strong>{" "}

                            {order.status}

                        </p>

                        <p>

                            <strong>Total Quantity:</strong>{" "}

                            {order.totalQuantity}

                        </p>

                        <p>

                            <strong>Total Amount:</strong>{" "}

                            ${order.totalAmount}

                        </p>

                        <select
                            defaultValue={order.status}
                            onChange={(e) =>
                                handleStatusUpdate(
                                    order.id,
                                    e.target.value
                                )
                            }
                        >

                            <option value="PENDING">
                                PENDING
                            </option>

                            <option value="CONFIRMED">
                                CONFIRMED
                            </option>

                            <option value="SHIPPED">
                                SHIPPED
                            </option>

                            <option value="DELIVERED">
                                DELIVERED
                            </option>

                            <option value="CANCELLED">
                                CANCELLED
                            </option>

                        </select>

                        <br />
                        <br />

                        <Link
                            to={`/admin/orders/${order.id}`}
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

export default AdminOrders;