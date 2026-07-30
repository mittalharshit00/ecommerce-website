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

    const loadCategories = async () => {

        try {

            setLoading(true);
            setError("");

            const data =
                await getCategories(page, 10);

            setCategories(
                data.content || []
            );

            setTotalPages(
                data.totalPages || 0
            );

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

            await deleteCategory(
                tenant,
                id
            );

            /*
             * Reload current page after deletion.
             */
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
            <div>

                <h1>Categories</h1>

                <p>
                    Loading categories...
                </p>

            </div>
        );
    }

    return (
        <div>

            <header>

                <h1>
                    Categories
                </h1>

                <nav>

                    <Link to="/dashboard">
                        Dashboard
                    </Link>

                    {" | "}

                    <Link to="/products">
                        Products
                    </Link>

                    {" | "}

                    <Link to="/categories">
                        Categories
                    </Link>

                </nav>

            </header>

            <hr />

            <main>

                {/*
                 * ADMIN ONLY
                 */}
                {isAdmin && (

                    <div>

                        <Link to="/categories/create">

                            <button>
                                Create Category
                            </button>

                        </Link>

                    </div>
                )}

                <br />

                {error && (
                    <p>
                        {error}
                    </p>
                )}

                {categories.length === 0 ? (

                    <p>
                        No categories found.
                    </p>

                ) : (

                    <div>

                        {categories.map(
                            (category) => (

                                <div
                                    key={category.id}
                                >

                                    <h3>
                                        {category.name}
                                    </h3>

                                    <p>
                                        ID: {category.id}
                                    </p>

                                    {/*
                                     * ADMIN ONLY
                                     */}
                                    {isAdmin && (

                                        <div>

                                            <Link
                                                to={`/categories/${category.id}/edit`}
                                            >
                                                <button>
                                                    Edit
                                                </button>
                                            </Link>

                                            {" "}

                                            <button
                                                onClick={() =>
                                                    handleDelete(
                                                        category.id
                                                    )
                                                }
                                            >
                                                Delete
                                            </button>

                                        </div>
                                    )}

                                    <hr />

                                </div>
                            )
                        )}

                    </div>
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
                            disabled={
                                page >=
                                totalPages - 1
                            }
                            onClick={() =>
                                setPage(page + 1)
                            }
                        >
                            Next
                        </button>

                    </div>
                )}

            </main>

        </div>
    );
}

export default Categories;