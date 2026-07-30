import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";

import { createProduct } from "../services/productService";
import { getCategories } from "../services/categoryService";
import { useAuth } from "../context/useAuth";

function CreateProduct() {

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

    const [loading, setLoading] = useState(false);
    const [categoriesLoading, setCategoriesLoading] = useState(true);
    const [error, setError] = useState("");

    // Only ADMIN should access this page
    useEffect(() => {

        if (!isAdmin) {
            navigate("/products");
        }

    }, [isAdmin, navigate]);

    // Load categories
    useEffect(() => {

        const loadCategories = async () => {

            try {

                setCategoriesLoading(true);

                const data = await getCategories(0, 100);

                setCategories(data.content || []);

            } catch (error) {

                console.error(error);

                setError(
                    error.response?.data?.message ||
                    "Unable to load categories."
                );

            } finally {

                setCategoriesLoading(false);
            }
        };

        loadCategories();

    }, []);

    const handleChange = (event) => {

        const { name, value } = event.target;

        setFormData((previous) => ({
            ...previous,
            [name]: value
        }));
    };

    const handleSubmit = async (event) => {

        event.preventDefault();

        setError("");

        if (!formData.name.trim()) {
            setError("Product name is required.");
            return;
        }

        if (formData.price === "") {
            setError("Price is required.");
            return;
        }

        if (formData.quantity === "") {
            setError("Quantity is required.");
            return;
        }

        if (!formData.categoryId) {
            setError("Please select a category.");
            return;
        }

        try {

            setLoading(true);

            const product = {
                name: formData.name.trim(),
                description: formData.description.trim(),
                imageUrl: formData.imageUrl.trim(),
                price: Number(formData.price),
                quantity: Number(formData.quantity),
                categoryId: Number(formData.categoryId)
            };

            await createProduct(
                tenant,
                product
            );

            navigate("/products");

        } catch (error) {

            console.error(error);

            setError(
                error.response?.data?.message ||
                "Unable to create product."
            );

        } finally {

            setLoading(false);
        }
    };

    if (!isAdmin) {
        return null;
    }

    return (
        <div className="min-vh-100 bg-light">
            <header className="bg-white shadow-sm border-bottom">
                <div className="container py-3 d-flex flex-column flex-lg-row align-items-lg-center justify-content-between gap-3">
                    <div>
                        <h1 className="h3 mb-1">Create Product</h1>
                        <p className="text-muted mb-0">Add a new item to your store catalog.</p>
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
                        <div className="card card-soft border-0 p-4 p-lg-5">
                            <div className="d-flex flex-column flex-lg-row align-items-lg-center justify-content-between gap-3 mb-4">
                                <div>
                                    <h2 className="h4 mb-2">New Product</h2>
                                    <p className="text-muted mb-0">Fill in the core details to publish a product.</p>
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
                                            name="name"
                                            type="text"
                                            className="form-control"
                                            value={formData.name}
                                            onChange={handleChange}
                                            maxLength={200}
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
                                            name="imageUrl"
                                            type="text"
                                            className="form-control"
                                            value={formData.imageUrl}
                                            onChange={handleChange}
                                        />
                                    </div>

                                    <div className="col-12 col-md-6">
                                        <label htmlFor="price" className="form-label">Price</label>
                                        <input
                                            id="price"
                                            name="price"
                                            type="number"
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
                                            name="quantity"
                                            type="number"
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
                                        {categoriesLoading ? (
                                            <div className="form-text">Loading categories...</div>
                                        ) : (
                                            <select
                                                id="categoryId"
                                                name="categoryId"
                                                className="form-select"
                                                value={formData.categoryId}
                                                onChange={handleChange}
                                                required
                                            >
                                                <option value="">Select a category</option>
                                                {categories.map((category) => (
                                                    <option key={category.id} value={category.id}>
                                                        {category.name}
                                                    </option>
                                                ))}
                                            </select>
                                        )}
                                    </div>
                                </div>

                                <div className="d-flex flex-column flex-sm-row gap-2 mt-4">
                                    <button type="submit" className="btn btn-primary" disabled={loading || categoriesLoading}>
                                        {loading ? "Creating..." : "Create Product"}
                                    </button>
                                    <Link to="/products" className="btn btn-outline-secondary">
                                        Cancel
                                    </Link>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </main>
        </div>
    );
}

export default CreateProduct;