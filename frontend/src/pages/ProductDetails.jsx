import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";

import { getProductById } from "../services/productService";
import { getImageUrl } from "../utils/imageUtils";


function ProductDetails() {

    const { id } = useParams();
    const navigate = useNavigate();

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

                    setError("Unable to load product.");
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

    const handleBuyNow = () => {

        if (!product?.id) {
            return;
        }

        if (product.quantity <= 0) {
            setError("This product is currently out of stock.");
            return;
        }

        setError("");
        navigate(`/orders/create?productId=${product.id}`);
    };

    const renderPage = (content) => (

        <div className="min-vh-100 bg-light">

            <header className="bg-white shadow-sm border-bottom">

                <div className="container py-3 d-flex flex-column flex-lg-row align-items-lg-center justify-content-between gap-3">

                    <div>

                        <h1 className="h3 mb-1">
                            Product Details
                        </h1>

                        <p className="text-muted mb-0">
                            See all the details for a selected product.
                        </p>

                    </div>


                    <nav className="d-flex flex-wrap align-items-center gap-2">

                        <Link
                            to="/dashboard"
                            className="nav-link px-3 py-2 rounded-pill"
                        >
                            Dashboard
                        </Link>


                        <Link
                            to="/products"
                            className="nav-link px-3 py-2 rounded-pill"
                        >
                            Products
                        </Link>

                    </nav>

                </div>

            </header>


            <main className="container py-4">

                {content}

            </main>

        </div>
    );


    if (loading) {

        return renderPage(

            <div className="card card-soft border-0 p-4 text-center">

                <div
                    className="spinner-border text-primary mx-auto mb-3"
                    role="status"
                />

                <h2 className="h5">
                    Loading product...
                </h2>

            </div>

        );

    }


    if (error) {

        return renderPage(

            <div className="card card-soft border-0 p-4">

                <h2 className="h5 mb-3">
                    {error}
                </h2>

                <Link
                    to="/products"
                    className="btn btn-outline-secondary"
                >
                    Back to Products
                </Link>

            </div>

        );

    }


    if (!product) {

        return renderPage(

            <div className="card card-soft border-0 p-4">

                <h2 className="h5 mb-3">
                    Product not found.
                </h2>

                <Link
                    to="/products"
                    className="btn btn-outline-secondary"
                >
                    Back to Products
                </Link>

            </div>

        );

    }


    return renderPage(

        <div className="row g-4">

            <div className="col-12 col-lg-8">

                <div className="card card-soft border-0 h-100">

                    <div className="card-body p-4 p-lg-5">


                        <div className="d-flex flex-wrap align-items-center justify-content-between gap-3 mb-4">


                            <div>

                                <h2 className="h3 mb-1">
                                    {product.name}
                                </h2>


                                <p className="text-muted mb-0">
                                    {product.categoryName || "Uncategorised"}
                                </p>


                            </div>


                            <span className="badge text-bg-primary fs-6">
                                ${product.price}
                            </span>


                        </div>




                        {
                            product.imageUrl && (

                                <div className="mb-4">

                                    <img

                                        src={
                                            getImageUrl(
                                                product.imageUrl
                                            )
                                        }

                                        alt={product.name}

                                        className="img-fluid rounded shadow-sm"

                                        style={{
                                            maxHeight: "320px",
                                            objectFit: "cover"
                                        }}

                                    />

                                </div>

                            )
                        }





                        <div className="row g-3">


                            <div className="col-12 col-md-6">

                                <div className="p-3 rounded bg-light">

                                    <div className="text-muted small">
                                        Description
                                    </div>

                                    <div>
                                        {product.description ||
                                            "No description available."}
                                    </div>

                                </div>

                            </div>




                            <div className="col-12 col-md-6">

                                <div className="p-3 rounded bg-light">

                                    <div className="text-muted small">
                                        Available Quantity
                                    </div>

                                    <div>
                                        {product.quantity}
                                    </div>

                                </div>

                            </div>




                            <div className="col-12 col-md-6">

                                <div className="p-3 rounded bg-light">

                                    <div className="text-muted small">
                                        Category
                                    </div>

                                    <div>
                                        {product.categoryName || "—"}
                                    </div>

                                </div>

                            </div>




                            <div className="col-12 col-md-6">

                                <div className="p-3 rounded bg-light">

                                    <div className="text-muted small">
                                        Product ID
                                    </div>

                                    <div>
                                        {product.id}
                                    </div>

                                </div>

                            </div>


                        </div>




                        <div className="mt-4 d-flex flex-wrap gap-2">

                            <button
                                type="button"
                                className="btn btn-primary"
                                onClick={handleBuyNow}
                                disabled={product.quantity <= 0}
                            >
                                Buy Now
                            </button>

                            <Link
                                to="/products"
                                className="btn btn-outline-secondary"
                            >
                                ← Back to Products
                            </Link>

                        </div>

                        {error && (
                            <div className="alert alert-danger mt-3 mb-0">
                                {error}
                            </div>
                        )}


                    </div>

                </div>

            </div>

        </div>

    );

}


export default ProductDetails;