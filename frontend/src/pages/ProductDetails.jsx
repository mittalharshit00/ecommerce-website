
import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { getProductById } from "../services/productService";

function ProductDetails() {

    const { id } = useParams();

    const [product, setProduct] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {

        let cancelled = false;

        const fetchProduct = async () => {

            try {

                setLoading(true);
                setError("");

                const data = await getProductById(id);

                if (!cancelled) {
                    setProduct(data);
                }

            } catch (error) {

                if (!cancelled) {

                    console.error(error);

                    setError(
                        "Unable to load product."
                    );
                }

            } finally {

                if (!cancelled) {
                    setLoading(false);
                }
            }
        };

        fetchProduct();

        return () => {
            cancelled = true;
        };

    }, [id]);

    if (loading) {
        return (
            <div>
                <h1>Product Details</h1>
                <p>Loading product...</p>
            </div>
        );
    }

    if (error) {
        return (
            <div>
                <h1>Product Details</h1>
                <p>{error}</p>

                <Link to="/products">
                    Back to Products
                </Link>
            </div>
        );
    }

    if (!product) {
        return (
            <div>
                <h1>Product Details</h1>
                <p>Product not found.</p>

                <Link to="/products">
                    Back to Products
                </Link>
            </div>
        );
    }

    return (
        <div>

            <header>

                <h1>Product Details</h1>

                <nav>

                    <Link to="/dashboard">
                        Dashboard
                    </Link>

                    {" | "}

                    <Link to="/products">
                        Products
                    </Link>

                </nav>

            </header>

            <hr />

            <main>

                <h2>{product.name}</h2>

                {product.imageUrl && (
                    <div>
                        <img
                            src={product.imageUrl}
                            alt={product.name}
                            width="300"
                        />
                    </div>
                )}

                <p>
                    <strong>Description:</strong>{" "}
                    {product.description || "No description available."}
                </p>

                <p>
                    <strong>Price:</strong>{" "}
                    ${product.price}
                </p>

                <p>
                    <strong>Available Quantity:</strong>{" "}
                    {product.quantity}
                </p>

                <p>
                    <strong>Category:</strong>{" "}
                    {product.categoryName}
                </p>

                <p>
                    <strong>Product ID:</strong>{" "}
                    {product.id}
                </p>

                <br />

                <Link to="/products">
                    ← Back to Products
                </Link>

            </main>

        </div>
    );
}

export default ProductDetails;

