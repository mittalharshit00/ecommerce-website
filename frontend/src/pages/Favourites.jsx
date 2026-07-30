import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

import {
    getFavourites,
    removeFavourite
} from "../services/favouriteService";

function Favourites() {

    const [favourites, setFavourites] = useState([]);

    const [loading, setLoading] = useState(true);

    const [page, setPage] = useState(0);

    const [totalPages, setTotalPages] = useState(0);

    const navItems = [
        { to: "/dashboard", label: "Dashboard" },
        { to: "/products", label: "Products" },
        { to: "/categories", label: "Categories" },
        { to: "/favourites", label: "Favourites" },
        { to: "/orders", label: "Orders" },
        { to: "/orders/create", label: "Create Order" }
    ];

    const loadFavourites = async () => {

        try {

            setLoading(true);

            const data = await getFavourites(
                page,
                10
            );

            setFavourites(
                data.content || []
            );

            setTotalPages(
                data.totalPages || 0
            );

        } catch (error) {

            console.error(error);

        } finally {

            setLoading(false);

        }

    };

    useEffect(() => {

        loadFavourites();

    }, [page]);

    const handleRemove = async (productId) => {

        try {

            await removeFavourite(productId);
            loadFavourites();

        } catch (error) {

            console.error(error);
        }

    };

    if (loading) {
        return (
            <div className="min-vh-100 bg-light">
                <header className="bg-white shadow-sm border-bottom">
                    <div className="container py-3 d-flex flex-column flex-lg-row align-items-lg-center justify-content-between gap-3">
                        <div>
                            <h1 className="h3 mb-1">Favourites</h1>
                            <p className="text-muted mb-0">Quick access to the products you care about.</p>
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
                        <h2 className="h5">Loading favourites...</h2>
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
                        <h1 className="h3 mb-1">Favourites</h1>
                        <p className="text-muted mb-0">Your saved products and preferred picks.</p>
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
                <div className="card card-soft border-0 p-4 mb-4">
                    <div className="d-flex flex-column flex-lg-row align-items-lg-center justify-content-between gap-3">
                        <div>
                            <h2 className="h4 mb-2">Saved Products</h2>
                            <p className="text-muted mb-0">Keep track of the items you want to revisit.</p>
                        </div>
                        <Link to="/products" className="btn btn-primary">Browse Products</Link>
                    </div>
                </div>

                {favourites.length === 0 ? (
                    <div className="card card-soft border-0 p-4 text-center">
                        <h2 className="h5 mb-2">No favourites yet</h2>
                        <p className="text-muted mb-0">Start exploring products and save your favourites.</p>
                    </div>
                ) : (
                    <div className="row g-4">
                        {favourites.map(favourite => (
                            <div key={favourite.productId} className="col-12 col-lg-6">
                                <div className="card card-soft border-0 h-100">
                                    <div className="card-body d-flex flex-column">
                                        <h3 className="h5 mb-2">{favourite.productName || favourite.product?.name || `Product ${favourite.productId}`}</h3>
                                        <p className="text-muted flex-grow-1">{favourite.productDescription || "Favourite item"}</p>
                                        <div className="d-flex justify-content-between align-items-center">
                                            <span className="badge text-bg-light">Saved</span>
                                            <button className="btn btn-outline-danger btn-sm" onClick={() => handleRemove(favourite.productId)}>
                                                Remove
                                            </button>
                                        </div>
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

export default Favourites;