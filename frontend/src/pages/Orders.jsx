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

    const navItems = [
        { to: "/dashboard", label: "Dashboard" },
        { to: "/products", label: "Products" },
        { to: "/categories", label: "Categories" },
        { to: "/favourites", label: "Favourites" },
        { to: "/orders", label: "Orders" },
        { to: "/orders/create", label: "Create Order" }
    ];

    useEffect(() => {

        loadOrders();

    }, [page]);

    const loadOrders = async () => {

        try {

            setLoading(true);

            setError("");

            const data = isAdmin
                ? await getAdminOrders(tenant, page, 10)
                : await getOrders(page, 10);

            setOrders(data.content || []);
            setTotalPages(data.totalPages || 0);

        } catch (error) {

            console.error(error);
            setError("Unable to load orders.");

        } finally {

            setLoading(false);

        }

    };

    const statusClass = (status) => {
        switch (status?.toUpperCase()) {
            case "CONFIRMED":
                return "bg-success";
            case "DELIVERED":
                return "bg-primary";
            case "CANCELLED":
                return "bg-danger";
            default:
                return "bg-secondary";
        }
    };

    if (loading) {

        return (
            <div className="min-vh-100 bg-light">
                <header className="bg-white shadow-sm border-bottom">
                    <div className="container py-3 d-flex flex-column flex-lg-row align-items-lg-center justify-content-between gap-3">
                        <div>
                            <h1 className="h3 mb-1">Orders</h1>
                            <p className="text-muted mb-0">Track and review your order history.</p>
                        </div>
                        <nav className="d-flex flex-wrap align-items-center gap-2">
                            {navItems.map(item => (
                                <Link key={item.to} to={item.to} className="nav-link px-3 py-2 rounded-pill">
                                    {item.label}
                                </Link>
                            ))}
                        </nav>
                    </div>
                </header>
                <main className="container py-4">
                    <div className="card card-soft border-0 p-4 text-center">
                        <div className="spinner-border text-primary mx-auto mb-3" role="status" />
                        <h2 className="h5">Loading orders...</h2>
                    </div>
                </main>
            </div>
        );

    }

    return (

        <div className="min-vh-100 bg-light">
            <header className="bg-white shadow-sm border-bottom">
                <div className="container py-3 d-flex flex-column flex-lg-row align-items-lg-center justify-content-between gap-3">
                    <div>
                        <h1 className="h3 mb-1">Orders</h1>
                        <p className="text-muted mb-0">Review current and past orders in one place.</p>
                    </div>
                    <nav className="d-flex flex-wrap align-items-center gap-2">
                        {navItems.map(item => (
                            <Link key={item.to} to={item.to} className="nav-link px-3 py-2 rounded-pill">
                                {item.label}
                            </Link>
                        ))}
                        <Link to="/orders/create" className="btn btn-primary btn-sm">Create Order</Link>
                    </nav>
                </div>
            </header>

            <main className="container py-4">
                <div className="card card-soft border-0 p-4 mb-4">
                    <div className="d-flex flex-column flex-lg-row align-items-lg-center justify-content-between gap-3">
                        <div>
                            <h2 className="h4 mb-2">{isAdmin ? "Tenant Orders" : "My Orders"}</h2>
                            <p className="text-muted mb-0">Track status, quantities and totals from one dashboard.</p>
                        </div>
                        <Link to="/orders/create" className="btn btn-primary">Create Order</Link>
                    </div>
                </div>

                {error && <div className="alert alert-danger">{error}</div>}

                {orders.length === 0 ? (
                    <div className="card card-soft border-0 p-4 text-center">
                        <h2 className="h5 mb-2">No orders found</h2>
                        <p className="text-muted mb-0">Place your first order to see it here.</p>
                    </div>
                ) : (
                    <div className="card card-soft border-0 overflow-hidden">
                        <div className="table-responsive">
                            <table className="table table-hover align-middle mb-0">
                                <thead className="table-light">
                                    <tr>
                                        <th>Order</th>
                                        <th>Status</th>
                                        <th>Qty</th>
                                        <th>Amount</th>
                                        <th></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {orders.map((order) => (
                                        <tr key={order.id}>
                                            <td>
                                                <div className="fw-semibold">#{order.id}</div>
                                                {isAdmin && order.user && (
                                                    <div className="text-muted small">{order.user.fullName}</div>
                                                )}
                                            </td>
                                            <td><span className={`badge ${statusClass(order.status)}`}>{order.status}</span></td>
                                            <td>{order.totalQuantity}</td>
                                            <td>${order.totalAmount}</td>
                                            <td>
                                                <Link to={`/orders/${order.id}`} className="btn btn-outline-primary btn-sm">
                                                    View Details
                                                </Link>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    </div>
                )}

                {totalPages > 1 && (
                    <div className="d-flex justify-content-between align-items-center mt-4">
                        <button className="btn btn-outline-secondary btn-sm" disabled={page === 0} onClick={() => setPage(page - 1)}>
                            Previous
                        </button>
                        <span className="text-muted">Page {page + 1} of {totalPages}</span>
                        <button className="btn btn-outline-secondary btn-sm" disabled={page >= totalPages - 1} onClick={() => setPage(page + 1)}>
                            Next
                        </button>
                    </div>
                )}
            </main>
        </div>
    );
}

export default Orders;