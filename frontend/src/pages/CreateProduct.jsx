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
        <div>

            <header>

                <h1>Create Product</h1>

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

                <h2>New Product</h2>

                {error && (
                    <p>
                        {error}
                    </p>
                )}

                <form onSubmit={handleSubmit}>

                    <div>

                        <label htmlFor="name">
                            Name
                        </label>

                        <br />

                        <input
                            id="name"
                            name="name"
                            type="text"
                            value={formData.name}
                            onChange={handleChange}
                            maxLength={200}
                            required
                        />

                    </div>

                    <br />

                    <div>

                        <label htmlFor="description">
                            Description
                        </label>

                        <br />

                        <textarea
                            id="description"
                            name="description"
                            value={formData.description}
                            onChange={handleChange}
                        />

                    </div>

                    <br />

                    <div>

                        <label htmlFor="imageUrl">
                            Image URL
                        </label>

                        <br />

                        <input
                            id="imageUrl"
                            name="imageUrl"
                            type="text"
                            value={formData.imageUrl}
                            onChange={handleChange}
                        />

                    </div>

                    <br />

                    <div>

                        <label htmlFor="price">
                            Price
                        </label>

                        <br />

                        <input
                            id="price"
                            name="price"
                            type="number"
                            min="0"
                            step="0.01"
                            value={formData.price}
                            onChange={handleChange}
                            required
                        />

                    </div>

                    <br />

                    <div>

                        <label htmlFor="quantity">
                            Quantity
                        </label>

                        <br />

                        <input
                            id="quantity"
                            name="quantity"
                            type="number"
                            min="0"
                            step="1"
                            value={formData.quantity}
                            onChange={handleChange}
                            required
                        />

                    </div>

                    <br />

                    <div>

                        <label htmlFor="categoryId">
                            Category
                        </label>

                        <br />

                        {categoriesLoading ? (

                            <p>
                                Loading categories...
                            </p>

                        ) : (

                            <select
                                id="categoryId"
                                name="categoryId"
                                value={formData.categoryId}
                                onChange={handleChange}
                                required
                            >

                                <option value="">
                                    Select a category
                                </option>

                                {categories.map((category) => (

                                    <option
                                        key={category.id}
                                        value={category.id}
                                    >
                                        {category.name}
                                    </option>

                                ))}

                            </select>
                        )}

                    </div>

                    <br />

                    <button
                        type="submit"
                        disabled={loading || categoriesLoading}
                    >
                        {loading
                            ? "Creating..."
                            : "Create Product"}
                    </button>

                    {" "}

                    <Link to="/products">
                        Cancel
                    </Link>

                </form>

            </main>

        </div>
    );
}

export default CreateProduct;