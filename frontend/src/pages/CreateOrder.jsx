import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";

import { getProducts } from "../services/productService";
import { createOrder } from "../services/orderService";

function CreateOrder() {

    const navigate = useNavigate();

    const [products, setProducts] = useState([]);

    const [productId, setProductId] = useState("");

    const [quantity, setQuantity] = useState(1);

    const [loading, setLoading] = useState(true);

    const [submitting, setSubmitting] = useState(false);

    const [error, setError] = useState("");

    useEffect(() => {

        loadProducts();

    }, []);

    const loadProducts = async () => {

        try {

            const data = await getProducts(0, 100);

            setProducts(data.content || []);

        } catch (error) {

            console.error(error);

            setError("Unable to load products.");

        } finally {

            setLoading(false);
        }
    };

    const handleSubmit = async (event) => {

        event.preventDefault();

        if (!productId) {

            setError("Please select a product.");

            return;
        }

        try {

            setSubmitting(true);

            setError("");

            await createOrder({
                items: [
                    {
                        productId: Number(productId),
                        quantity: Number(quantity)
                    }
                ]
            });

            alert("Order placed successfully.");

            navigate("/orders");

        } catch (error) {

            console.error(error);

            setError(
                error.response?.data?.message ||
                "Unable to place order."
            );

        } finally {

            setSubmitting(false);
        }
    };

    if (loading) {

        return (
            <div className="min-vh-100 bg-light">
                <header className="bg-white shadow-sm border-bottom">
                    <div className="container py-3 d-flex flex-column flex-lg-row align-items-lg-center justify-content-between gap-3">
                        <div>
                            <h1 className="h3 mb-1">Create Order</h1>
                            <p className="text-muted mb-0">Place a new order from the available products.</p>
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
                        <h2 className="h5">Loading products...</h2>
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
                        <h1 className="h3 mb-1">Create Order</h1>
                        <p className="text-muted mb-0">Select a product and quantity to place an order.</p>
                    </div>
                    <nav className="d-flex flex-wrap align-items-center gap-2">
                        <Link to="/dashboard" className="nav-link px-3 py-2 rounded-pill">Dashboard</Link>
                        <Link to="/orders" className="nav-link px-3 py-2 rounded-pill">Orders</Link>
                    </nav>
                </div>
            </header>

            <main className="container py-4">
                <div className="card card-soft border-0 p-4 p-lg-5">
                    <h2 className="h4 mb-3">New Order</h2>
                    {error && <div className="alert alert-danger">{error}</div>}
                    <form onSubmit={handleSubmit}>
                        <div className="mb-3">
                            <label className="form-label">Product</label>
                            <select className="form-select" value={productId} onChange={(e) => setProductId(e.target.value)}>
                                <option value="">Select Product</option>
                                {products.map((product) => (
                                    <option key={product.id} value={product.id}>
                                        {product.name} (${product.price})
                                    </option>
                                ))}
                            </select>
                        </div>

                        <div className="mb-3">
                            <label className="form-label">Quantity</label>
                            <input className="form-control" type="number" min="1" value={quantity} onChange={(e) => setQuantity(e.target.value)} />
                        </div>

                        <div className="d-flex gap-2">
                            <button className="btn btn-primary" disabled={submitting}>
                                {submitting ? "Placing..." : "Place Order"}
                            </button>
                            <Link to="/orders" className="btn btn-outline-secondary">Cancel</Link>
                        </div>
                    </form>
                </div>
            </main>
        </div>
    );
}

export default CreateOrder;