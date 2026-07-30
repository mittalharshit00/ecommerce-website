import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";

import {
    getCategoryById,
    updateCategory
} from "../services/categoryService";

import { useAuth } from "../context/useAuth";

function EditCategory() {

    const { id } = useParams();

    const navigate = useNavigate();

    const { isAdmin, tenant } = useAuth();

    const [name, setName] = useState("");

    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);

    const [error, setError] = useState("");

    useEffect(() => {

        if (!isAdmin) {
            return;
        }

        loadCategory();

    }, [id, isAdmin]);

    const loadCategory = async () => {

        try {

            setLoading(true);
            setError("");

            const category = await getCategoryById(id);

            setName(category.name || "");

        } catch (error) {

            console.error(error);

            setError(
                error.response?.data?.message ||
                "Unable to load category."
            );

        } finally {

            setLoading(false);
        }
    };

    const handleSubmit = async (event) => {

        event.preventDefault();

        try {

            setSaving(true);
            setError("");

            await updateCategory(tenant, id, { name });

            navigate("/categories");

        } catch (error) {

            console.error(error);

            setError(
                error.response?.data?.message ||
                "Unable to update category."
            );

        } finally {

            setSaving(false);
        }
    };

    if (!isAdmin) {

        return (
            <div className="container py-5">
                <div className="card card-soft border-0 p-4">
                    <h1 className="h3 mb-2">Access Denied</h1>
                    <p className="text-muted">You do not have permission to edit categories.</p>
                    <Link to="/categories" className="btn btn-outline-secondary mt-3">Back to Categories</Link>
                </div>
            </div>
        );
    }

    if (loading) {

        return (
            <div className="container py-5">
                <div className="card card-soft border-0 p-4 text-center">
                    <div className="spinner-border text-primary mx-auto mb-3" role="status" />
                    <h1 className="h3">Edit Category</h1>
                    <p className="text-muted">Loading category...</p>
                </div>
            </div>
        );
    }

    return (
        <div className="min-vh-100 bg-light">
            <header className="bg-white shadow-sm border-bottom">
                <div className="container py-3 d-flex flex-column flex-lg-row align-items-lg-center justify-content-between gap-3">
                    <div>
                        <h1 className="h3 mb-1">Edit Category</h1>
                        <p className="text-muted mb-0">Update the category name.</p>
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
                            <button type="submit" className="btn btn-primary" disabled={saving}>
                                {saving ? "Updating..." : "Update Category"}
                            </button>
                            <Link to="/categories" className="btn btn-outline-secondary">Cancel</Link>
                        </div>
                    </form>
                </div>
            </main>
        </div>
    );
}

export default EditCategory;