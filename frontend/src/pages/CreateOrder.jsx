import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import { getProducts } from "../services/productService";
import { createOrder } from "../services/orderService";

function CreateOrder() {

    const navigate = useNavigate();

    const [products, setProducts] = useState([]);

    const [productId, setProductId] = useState("");

    const [quantity, setQuantity] = useState(1);

    const [loading, setLoading] = useState(true);

    const [submitting, setSubmitting] = useState(false);

    const [error, setError] = useState("");

    useEffect(() => {

        loadProducts();

    }, []);

    const loadProducts = async () => {

        try {

            const data = await getProducts(0, 100);

            setProducts(data.content);

        } catch (error) {

            console.error(error);

            setError("Unable to load products.");

        } finally {

            setLoading(false);
        }
    };

    const handleSubmit = async (event) => {

        event.preventDefault();

        if (!productId) {

            setError("Please select a product.");

            return;
        }

        try {

            setSubmitting(true);

            setError("");

            await createOrder({

                items: [
                    {
                        productId: Number(productId),
                        quantity: Number(quantity)
                    }
                ]
            });

            alert("Order placed successfully.");

            navigate("/orders");

        } catch (error) {

            console.error(error);

            setError(
                error.response?.data?.message ||
                "Unable to place order."
            );

        } finally {

            setSubmitting(false);
        }
    };

    if (loading) {

        return <p>Loading products...</p>;
    }

    return (

        <div>

            <h1>Create Order</h1>

            <form onSubmit={handleSubmit}>

                <div>

                    <label>

                        Product

                    </label>

                    <br />

                    <select
                        value={productId}
                        onChange={(e) =>
                            setProductId(e.target.value)
                        }
                    >

                        <option value="">
                            Select Product
                        </option>

                        {products.map((product) => (

                            <option
                                key={product.id}
                                value={product.id}
                            >

                                {product.name} (${product.price})

                            </option>

                        ))}

                    </select>

                </div>

                <br />

                <div>

                    <label>

                        Quantity

                    </label>

                    <br />

                    <input
                        type="number"
                        min="1"
                        value={quantity}
                        onChange={(e) =>
                            setQuantity(e.target.value)
                        }
                    />

                </div>

                <br />

                {error && (

                    <p style={{ color: "red" }}>
                        {error}
                    </p>

                )}

                <button
                    disabled={submitting}
                >
                    {submitting
                        ? "Placing..."
                        : "Place Order"}
                </button>

            </form>

        </div>
    );
}

export default CreateOrder;