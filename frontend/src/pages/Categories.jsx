import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

import {
    getCategories,
    deleteCategory
} from "../services/categoryService";

import { useAuth } from "../context/useAuth";

function Categories() {

    const { isAdmin, tenant } = useAuth();

    const [categories, setCategories] = useState([]);

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

    const loadCategories = async () => {

        try {

            setLoading(true);
            setError("");

            const data = await getCategories(page, 10);

            setCategories(data.content || []);
            setTotalPages(data.totalPages || 0);

        } catch (error) {

            console.error(error);

            setError(
                error.response?.data?.message ||
                "Unable to load categories."
            );

        } finally {

            setLoading(false);
        }
    };

    useEffect(() => {

        loadCategories();

    }, [page]);

    const handleDelete = async (id) => {

        const confirmed = window.confirm(
            "Are you sure you want to delete this category?"
        );

        if (!confirmed) {
            return;
        }

        try {

            setError("");

            await deleteCategory(tenant, id);
            await loadCategories();

        } catch (error) {

            console.error(error);

            setError(
                error.response?.data?.message ||
                "Unable to delete category."
            );
        }
    };

    if (loading) {

        return (
            <div className="min-vh-100 bg-light">
                <header className="bg-white shadow-sm border-bottom">
                    <div className="container py-3 d-flex flex-column flex-lg-row align-items-lg-center justify-content-between gap-3">
                        <div>
                            <h1 className="h3 mb-1">Categories</h1>
                            <p className="text-muted mb-0">Group products into logical collections.</p>
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
                        <h2 className="h5">Loading categories...</h2>
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
                        <h1 className="h3 mb-1">Categories</h1>
                        <p className="text-muted mb-0">Group products into logical collections.</p>
                    </div>
                    <nav className="d-flex flex-wrap align-items-center gap-2">
                        {navItems.map(item => (
                            <Link key={item.to} to={item.to} className="nav-link px-3 py-2 rounded-pill">
                                {item.label}
                            </Link>
                        ))}
                        {isAdmin && (
                            <Link to="/categories/create" className="btn btn-primary btn-sm">
                                Create Category
                            </Link>
                        )}
                    </nav>
                </div>
            </header>

            <main className="container py-4">
                <div className="card card-soft border-0 p-4 mb-4">
                    <div className="d-flex flex-column flex-lg-row align-items-lg-center justify-content-between gap-3">
                        <div>
                            <h2 className="h4 mb-2">Category Library</h2>
                            <p className="text-muted mb-0">Create and maintain the categories that power your storefront.</p>
                        </div>
                        {isAdmin && (
                            <Link to="/categories/create" className="btn btn-primary">
                                Create Category
                            </Link>
                        )}
                    </div>
                </div>

                {error && <div className="alert alert-danger">{error}</div>}

                {categories.length === 0 ? (
                    <div className="card card-soft border-0 p-4 text-center">
                        <h2 className="h5 mb-2">No categories found</h2>
                        <p className="text-muted mb-0">Create a category to organise products.</p>
                    </div>
                ) : (
                    <div className="row g-4">
                        {categories.map(category => (
                            <div key={category.id} className="col-12 col-md-6 col-xl-4">
                                <div className="card card-soft border-0 h-100">
                                    <div className="card-body d-flex flex-column">
                                        <div className="d-flex justify-content-between align-items-start gap-2 mb-3">
                                            <div>
                                                <h3 className="h5 mb-1">{category.name}</h3>
                                                <p className="text-muted mb-0">Category ID: {category.id}</p>
                                            </div>
                                            <span className="badge text-bg-light">Active</span>
                                        </div>
                                        {isAdmin && (
                                            <div className="d-flex gap-2 mt-auto">
                                                <Link to={`/categories/${category.id}/edit`} className="btn btn-outline-secondary btn-sm">
                                                    Edit
                                                </Link>
                                                <button className="btn btn-danger btn-sm" onClick={() => handleDelete(category.id)}>
                                                    Delete
                                                </button>
                                            </div>
                                        )}
                                    </div>
                                </div>
                            </div>
                        ))}
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

export default Categories;