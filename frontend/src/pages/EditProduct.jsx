import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";

import {
    getProductById,
    updateProduct
} from "../services/productService";

import { getCategories } from "../services/categoryService";
import { useAuth } from "../context/useAuth";

function EditProduct() {

    const { id } = useParams();
    const navigate = useNavigate();

    const { isAdmin, tenant } = useAuth();

    const [categories, setCategories] = useState([]);

    const [formData, setFormData] = useState({
        name: "",
        description: "",
        imageUrl: "",
        price: "",
        quantity: "",
        categoryId: ""
    });

    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState("");

    useEffect(() => {

        if (!isAdmin) {
            navigate("/products");
            return;
        }

        const loadData = async () => {

            try {

                setLoading(true);
                setError("");

                const [product, categoryData] =
                    await Promise.all([
                        getProductById(id),
                        getCategories()
                    ]);

                setFormData({
                    name: product.name || "",
                    description: product.description || "",
                    imageUrl: product.imageUrl || "",
                    price: product.price ?? "",
                    quantity: product.quantity ?? "",
                    categoryId: product.categoryId ?? ""
                });

                setCategories(
                    categoryData.content || []
                );

            } catch (error) {

                console.error(error);

                setError(
                    "Unable to load product."
                );

            } finally {

                setLoading(false);
            }
        };

        loadData();

    }, [id, isAdmin, navigate]);

    const handleChange = (event) => {

        const { name, value } = event.target;

        setFormData((previous) => ({
            ...previous,
            [name]: value
        }));
    };

    const handleSubmit = async (event) => {

        event.preventDefault();

        try {

            setSaving(true);
            setError("");

            await updateProduct(
                tenant,
                id,
                {
                    name: formData.name,
                    description: formData.description,
                    imageUrl: formData.imageUrl,
                    price: Number(formData.price),
                    quantity: Number(formData.quantity),
                    categoryId: Number(formData.categoryId)
                }
            );

            navigate(`/products/${id}`);

        } catch (error) {

            console.error(error);

            setError(
                error.response?.data?.message ||
                "Unable to update product."
            );

        } finally {

            setSaving(false);
        }
    };

    if (!isAdmin) {
        return null;
    }

    if (loading) {
        return (
            <div className="min-vh-100 bg-light d-flex align-items-center justify-content-center">
                <div className="text-center">
                    <h1 className="h3 mb-2">Edit Product</h1>
                    <p className="text-muted">Loading product...</p>
                </div>
            </div>
        );
    }

    return (
        <div className="min-vh-100 bg-light">
            <header className="bg-white shadow-sm border-bottom">
                <div className="container py-3 d-flex flex-column flex-lg-row align-items-lg-center justify-content-between gap-3">
                    <div>
                        <h1 className="h3 mb-1">Edit Product</h1>
                        <p className="text-muted mb-0">Update the selected item in your catalog.</p>
                    </div>
                    <nav className="d-flex flex-wrap align-items-center gap-2">
                        <Link to="/dashboard" className="nav-link px-3 py-2 rounded-pill">Dashboard</Link>
                        <Link to="/products" className="nav-link px-3 py-2 rounded-pill">Products</Link>
                    </nav>
                </div>
            </header>

            <main className="container py-4">
                <div className="row justify-content-center">
                    <div className="col-12 col-lg-8">
                        <div className="card border-0 shadow-sm p-4 p-lg-5">
                            <div className="d-flex flex-column flex-lg-row align-items-lg-center justify-content-between gap-3 mb-4">
                                <div>
                                    <h2 className="h4 mb-2">Product Details</h2>
                                    <p className="text-muted mb-0">Adjust the information below and save your changes.</p>
                                </div>
                            </div>

                            {error && (
                                <div className="alert alert-danger">{error}</div>
                            )}

                            <form onSubmit={handleSubmit}>
                                <div className="row g-3">
                                    <div className="col-12">
                                        <label htmlFor="name" className="form-label">Name</label>
                                        <input
                                            id="name"
                                            type="text"
                                            name="name"
                                            className="form-control"
                                            value={formData.name}
                                            onChange={handleChange}
                                            required
                                        />
                                    </div>

                                    <div className="col-12">
                                        <label htmlFor="description" className="form-label">Description</label>
                                        <textarea
                                            id="description"
                                            name="description"
                                            className="form-control"
                                            rows="4"
                                            value={formData.description}
                                            onChange={handleChange}
                                        />
                                    </div>

                                    <div className="col-12">
                                        <label htmlFor="imageUrl" className="form-label">Image URL</label>
                                        <input
                                            id="imageUrl"
                                            type="text"
                                            name="imageUrl"
                                            className="form-control"
                                            value={formData.imageUrl}
                                            onChange={handleChange}
                                        />
                                    </div>

                                    <div className="col-12 col-md-6">
                                        <label htmlFor="price" className="form-label">Price</label>
                                        <input
                                            id="price"
                                            type="number"
                                            name="price"
                                            min="0"
                                            step="0.01"
                                            className="form-control"
                                            value={formData.price}
                                            onChange={handleChange}
                                            required
                                        />
                                    </div>

                                    <div className="col-12 col-md-6">
                                        <label htmlFor="quantity" className="form-label">Quantity</label>
                                        <input
                                            id="quantity"
                                            type="number"
                                            name="quantity"
                                            min="0"
                                            step="1"
                                            className="form-control"
                                            value={formData.quantity}
                                            onChange={handleChange}
                                            required
                                        />
                                    </div>

                                    <div className="col-12">
                                        <label htmlFor="categoryId" className="form-label">Category</label>
                                        <select
                                            id="categoryId"
                                            name="categoryId"
                                            className="form-select"
                                            value={formData.categoryId}
                                            onChange={handleChange}
                                            required
                                        >
                                            <option value="">Select category</option>
                                            {categories.map((category) => (
                                                <option key={category.id} value={category.id}>
                                                    {category.name}
                                                </option>
                                            ))}
                                        </select>
                                    </div>
                                </div>

                                <div className="d-flex flex-column flex-sm-row gap-2 mt-4">
                                    <button type="submit" className="btn btn-primary" disabled={saving}>
                                        {saving ? "Updating..." : "Update Product"}
                                    </button>
                                    <button
                                        type="button"
                                        className="btn btn-outline-secondary"
                                        onClick={() => navigate(`/products/${id}`)}
                                    >
                                        Cancel
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </main>
        </div>
    );
}

export default EditProduct;