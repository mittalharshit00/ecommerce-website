import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";

import { useAuth } from "../context/useAuth";

import {
    getOrderById,
    getAdminOrderById,
    updateOrderStatus
} from "../services/orderService";

function OrderDetails() {

    const { id } = useParams();

    const { isAdmin, tenant } = useAuth();

    const [order, setOrder] = useState(null);

    const [status, setStatus] = useState("");

    const [loading, setLoading] = useState(true);

    const [updating, setUpdating] = useState(false);

    const [error, setError] = useState("");

    useEffect(() => {
        loadOrder();
    }, [id]);

    const loadOrder = async () => {

        try {

            setLoading(true);

            setError("");

            const data = isAdmin
                ? await getAdminOrderById(
                    tenant,
                    id
                )
                : await getOrderById(id);

            setOrder(data);

            setStatus(data.status);

        } catch (error) {

            console.error(error);

            setError("Unable to load order.");

        } finally {

            setLoading(false);
        }
    };

    const handleUpdateStatus = async () => {

        try {

            setUpdating(true);

            const updated =
                await updateOrderStatus(
                    tenant,
                    id,
                    status
                );

            setOrder(updated);

        } catch (error) {

            console.error(error);

            alert(
                error.response?.data?.message ??
                "Unable to update status."
            );

        } finally {

            setUpdating(false);

        }
    };

    if (loading) {

        return <p>Loading order...</p>;
    }

    if (error) {

        return <p>{error}</p>;
    }

    return (

        <div>

            <h1>
                Order #{order.id}
            </h1>

            <p>

                <strong>Status:</strong>{" "}

                {order.status}

            </p>

            {isAdmin && (

                <>

                    <hr />

                    <h3>
                        Update Status
                    </h3>

                    <select
                        value={status}
                        onChange={(e) =>
                            setStatus(e.target.value)
                        }
                    >

                        <option value="PENDING">
                            PENDING
                        </option>

                        <option value="CONFIRMED">
                            CONFIRMED
                        </option>

                        <option value="DELIVERED">
                            DELIVERED
                        </option>

                        <option value="CANCELLED">
                            CANCELLED
                        </option>

                    </select>

                    {" "}

                    <button
                        onClick={handleUpdateStatus}
                        disabled={updating}
                    >

                        {updating
                            ? "Updating..."
                            : "Update Status"}

                    </button>

                </>

            )}

            <p>

                <strong>Total Quantity:</strong>{" "}

                {order.totalQuantity}

            </p>

            <p>

                <strong>Total Amount:</strong>{" "}

                ${order.totalAmount}

            </p>

            <hr />

            <h2>
                Order Items
            </h2>

            {order.items.map(item => (

                <div
                    key={item.productId}
                    style={{
                        border: "1px solid #ccc",
                        padding: "15px",
                        marginBottom: "10px"
                    }}
                >

                    <h3>

                        {item.productName}

                    </h3>

                    <p>

                        <strong>Quantity:</strong>{" "}

                        {item.quantity}

                    </p>

                    <p>

                        <strong>Price:</strong>{" "}

                        ${item.price}

                    </p>

                </div>

            ))}

            <br />

            <Link to="/orders">

                Back to Orders

            </Link>

        </div>

    );
}

export default OrderDetails;