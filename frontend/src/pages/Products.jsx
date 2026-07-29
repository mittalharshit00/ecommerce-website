import { useEffect, useState, useCallback } from "react";
import { Link } from "react-router-dom";

import {
    getProducts,
    deleteProduct
} from "../services/productService";

import {
    getFavourites,
    addFavourite,
    removeFavourite
} from "../services/favouriteService";

import { useAuth } from "../context/useAuth";

function Products() {

    const { isAdmin } = useAuth();

    const [products, setProducts] = useState([]);
    const [favouriteIds, setFavouriteIds] = useState([]);

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    const loadFavourites = useCallback(async () => {

        try {

            const data = await getFavourites(0, 100);

            setFavouriteIds(
                data.content.map(
                    favourite => favourite.productId
                )
            );

        } catch (error) {

            console.error(error);

        }

    }, []);

    const loadProducts = useCallback(async () => {

        try {

            setLoading(true);
            setError("");

            const data = await getProducts(page, 10);

            setProducts(data.content || []);
            setTotalPages(data.totalPages || 0);

        } catch (error) {

            console.error(error);

            setError(
                error.response?.data?.message ||
                "Unable to load products."
            );

        } finally {

            setLoading(false);

        }

    }, [page]);

    useEffect(() => {

        loadProducts();
        loadFavourites();

    }, [loadProducts, loadFavourites]);

    const handleAddFavourite = async (productId) => {

        try {

            await addFavourite(productId);

            setFavouriteIds(prev => [
                ...prev,
                productId
            ]);

        } catch (error) {

            console.error(error);

            setError(
                error.response?.data?.message ||
                "Unable to add favourite."
            );

        }
    };

    const handleRemoveFavourite = async (productId) => {

        try {

            await removeFavourite(productId);

            setFavouriteIds(prev =>
                prev.filter(
                    id => id !== productId
                )
            );

        } catch (error) {

            console.error(error);

            setError(
                error.response?.data?.message ||
                "Unable to remove favourite."
            );

        }
    };

    const handleDelete = async (id) => {

        const confirmed = window.confirm(
            "Are you sure you want to delete this product?"
        );

        if (!confirmed) {
            return;
        }

        try {

            await deleteProduct(id);

            setProducts(currentProducts =>
                currentProducts.filter(
                    product => product.id !== id
                )
            );

        } catch (error) {

            console.error(error);

            setError(
                error.response?.data?.message ||
                "Unable to delete product."
            );

        }
    };

    if (loading) {

        return (
            <div>

                <h1>Products</h1>

                <p>
                    Loading products...
                </p>

            </div>
        );
    }

    if (error) {

        return (
            <div>

                <h1>Products</h1>

                <p>
                    {error}
                </p>

            </div>
        );
    }

    return (
        <div>

            <header>

                <h1>Products</h1>

                {isAdmin && (
                    <p>
                        <Link to="/products/create">
                            Create Product
                        </Link>
                    </p>
                )}

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

                    {" | "}

                    <Link to="/favourites">
                        My Favourites
                    </Link>

                    {" | "}

                    <Link to="/orders">
                        Orders
                    </Link>

                </nav>

            </header>

            <hr />

            <main>

                <h2>
                    Product List
                </h2>

                {products.length === 0 ? (

                    <p>
                        No products found.
                    </p>

                ) : (

                    <div>

                        {products.map(product => (

                            <div key={product.id}>

                                <h3>
                                    {product.name}
                                </h3>

                                <p>
                                    {product.description}
                                </p>

                                <p>
                                    Price: ${product.price}
                                </p>

                                <p>
                                    Quantity: {product.quantity}
                                </p>

                                <p>
                                    Category: {product.categoryName}
                                </p>

                                <Link
                                    to={`/products/${product.id}`}
                                >
                                    View Details
                                </Link>

                                {" "}

                                {favouriteIds.includes(product.id) ? (

                                    <button
                                        onClick={() =>
                                            handleRemoveFavourite(product.id)
                                        }
                                    >
                                        ♥ Remove Favourite
                                    </button>

                                ) : (

                                    <button
                                        onClick={() =>
                                            handleAddFavourite(product.id)
                                        }
                                    >
                                        ♡ Add Favourite
                                    </button>

                                )}

                                {isAdmin && (

                                    <>

                                        {" "}

                                        <Link
                                            to={`/products/${product.id}/edit`}
                                        >
                                            Edit
                                        </Link>

                                        {" "}

                                        <button
                                            type="button"
                                            onClick={() =>
                                                handleDelete(product.id)
                                            }
                                        >
                                            Delete
                                        </button>

                                    </>

                                )}

                                <hr />

                            </div>

                        ))}

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
                                page >= totalPages - 1
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

export default Products;