import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";

import {
    createCategory
} from "../services/categoryService";

import { useAuth } from "../context/useAuth";

function CreateCategory() {

    const navigate = useNavigate();

    const { isAdmin, tenant } = useAuth();

    const [name, setName] = useState("");

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    if (!isAdmin) {

        return (
            <div className="container py-5">
                <div className="card card-soft border-0 p-4">
                    <h1 className="h3 mb-2">Access Denied</h1>
                    <p className="text-muted">You do not have permission to create categories.</p>
                    <Link to="/categories" className="btn btn-outline-secondary mt-3">Back to Categories</Link>
                </div>
            </div>
        );
    }

    const handleSubmit = async (event) => {

        event.preventDefault();

        try {

            setLoading(true);
            setError("");

            await createCategory(tenant, { name });

            navigate("/categories");

        } catch (error) {

            console.error(error);

            setError(
                error.response?.data?.message ||
                "Unable to create category."
            );

        } finally {

            setLoading(false);
        }
    };

    return (
        <div className="min-vh-100 bg-light">
            <header className="bg-white shadow-sm border-bottom">
                <div className="container py-3 d-flex flex-column flex-lg-row align-items-lg-center justify-content-between gap-3">
                    <div>
                        <h1 className="h3 mb-1">Create Category</h1>
                        <p className="text-muted mb-0">Add a new category to the store.</p>
                    </div>
                    <nav className="d-flex flex-wrap align-items-center gap-2">
                        <Link to="/dashboard" className="nav-link px-3 py-2 rounded-pill">Dashboard</Link>
                        <Link to="/categories" className="nav-link px-3 py-2 rounded-pill">Categories</Link>
                    </nav>
                </div>
            </header>

            <main className="container py-4">
                <div className="card card-soft border-0 p-4 p-lg-5">
                    {error && <div className="alert alert-danger">{error}</div>}
                    <form onSubmit={handleSubmit}>
                        <div className="mb-3">
                            <label className="form-label">Category Name</label>
                            <input
                                type="text"
                                className="form-control"
                                value={name}
                                onChange={(event) => setName(event.target.value)}
                                maxLength={100}
                                required
                            />
                        </div>
                        <div className="d-flex gap-2">
                            <button type="submit" className="btn btn-primary" disabled={loading}>
                                {loading ? "Creating..." : "Create Category"}
                            </button>
                            <Link to="/categories" className="btn btn-outline-secondary">Cancel</Link>
                        </div>
                    </form>
                </div>
            </main>
        </div>
    );
}

export default CreateCategory;