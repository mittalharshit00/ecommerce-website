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
                ? await getAdminOrderById(tenant, id)
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

            const updated = await updateOrderStatus(tenant, id, status);
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
        return (
            <div className="min-vh-100 bg-light">
                <header className="bg-white shadow-sm border-bottom">
                    <div className="container py-3 d-flex flex-column flex-lg-row align-items-lg-center justify-content-between gap-3">
                        <div>
                            <h1 className="h3 mb-1">Order Details</h1>
                            <p className="text-muted mb-0">Inspect the selected order and its contents.</p>
                        </div>
                        <nav className="d-flex flex-wrap align-items-center gap-2">
                            <Link to="/dashboard" className="nav-link px-3 py-2 rounded-pill">Dashboard</Link>
                            <Link to="/orders" className="nav-link px-3 py-2 rounded-pill">Orders</Link>
                        </nav>
                    </div>
                </header>
                <main className="container py-4">
                    <div className="card card-soft border-0 p-4 text-center">
                        <div className="spinner-border text-primary mx-auto mb-3" role="status" />
                        <h2 className="h5">Loading order...</h2>
                    </div>
                </main>
            </div>
        );
    }

    if (error) {
        return (
            <div className="container py-5">
                <div className="card card-soft border-0 p-4">
                    <h2 className="h5 mb-3">{error}</h2>
                    <Link to="/orders" className="btn btn-outline-secondary">Back to Orders</Link>
                </div>
            </div>
        );
    }

    return (
        <div className="min-vh-100 bg-light">
            <header className="bg-white shadow-sm border-bottom">
                <div className="container py-3 d-flex flex-column flex-lg-row align-items-lg-center justify-content-between gap-3">
                    <div>
                        <h1 className="h3 mb-1">Order #{order.id}</h1>
                        <p className="text-muted mb-0">Review the current order summary and status.</p>
                    </div>
                    <nav className="d-flex flex-wrap align-items-center gap-2">
                        <Link to="/dashboard" className="nav-link px-3 py-2 rounded-pill">Dashboard</Link>
                        <Link to="/orders" className="nav-link px-3 py-2 rounded-pill">Orders</Link>
                    </nav>
                </div>
            </header>

            <main className="container py-4">
                <div className="card card-soft border-0 p-4 p-lg-5">
                    <div className="d-flex flex-wrap align-items-center justify-content-between gap-3 mb-4">
                        <div>
                            <h2 className="h4 mb-2">Order Overview</h2>
                            <p className="text-muted mb-0">Status, amounts, and items for this transaction.</p>
                        </div>
                        <span className="badge text-bg-primary fs-6">{order.status}</span>
                    </div>

                    {isAdmin && (
                        <div className="row g-3 mb-4">
                            <div className="col-12 col-lg-6">
                                <label className="form-label">Update Status</label>
                                <div className="d-flex gap-2">
                                    <select className="form-select" value={status} onChange={(e) => setStatus(e.target.value)}>
                                        <option value="PENDING">PENDING</option>
                                        <option value="CONFIRMED">CONFIRMED</option>
                                        <option value="DELIVERED">DELIVERED</option>
                                        <option value="CANCELLED">CANCELLED</option>
                                    </select>
                                    <button className="btn btn-primary" onClick={handleUpdateStatus} disabled={updating}>
                                        {updating ? "Updating..." : "Update"}
                                    </button>
                                </div>
                            </div>
                        </div>
                    )}

                    <div className="row g-3 mb-4">
                        <div className="col-12 col-md-6">
                            <div className="p-3 rounded bg-light">
                                <div className="text-muted small">Total Quantity</div>
                                <div>{order.totalQuantity}</div>
                            </div>
                        </div>
                        <div className="col-12 col-md-6">
                            <div className="p-3 rounded bg-light">
                                <div className="text-muted small">Total Amount</div>
                                <div>${order.totalAmount}</div>
                            </div>
                        </div>
                    </div>

                    <h3 className="h5 mb-3">Order Items</h3>
                    <div className="row g-3">
                        {order.items.map(item => (
                            <div key={item.productId} className="col-12 col-lg-6">
                                <div className="card border-0 bg-light h-100">
                                    <div className="card-body">
                                        <h4 className="h6">{item.productName}</h4>
                                        <p className="text-muted mb-2">Quantity: {item.quantity}</p>
                                        <p className="text-muted mb-0">Price: ${item.price}</p>
                                        <Link to={`/products/${item.productId}`} className="btn btn-outline-primary btn-sm mt-3">
                                            View Product Details
                                        </Link>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>

                    <div className="mt-4">
                        <Link to="/orders" className="btn btn-outline-secondary">Back to Orders</Link>
                    </div>
                </div>
            </main>
        </div>
    );
}

export default OrderDetails;