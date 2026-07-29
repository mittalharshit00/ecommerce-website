
import { useEffect, useState } from "react";
import { createProduct } from "../services/productService";
import { getCategories } from "../services/categoryService";

function CreateProduct({ onProductCreated, onCancel }) {

    const [categories, setCategories] = useState([]);

    const [formData, setFormData] = useState({
        name: "",
        description: "",
        imageUrl: "",
        price: "",
        quantity: "",
        categoryId: ""
    });

    const [loadingCategories, setLoadingCategories] =
        useState(true);

    const [submitting, setSubmitting] =
        useState(false);

    const [error, setError] = useState("");

    useEffect(() => {

        const loadCategories = async () => {

            try {

                setLoadingCategories(true);
                setError("");

                const data = await getCategories();

                setCategories(data.content || []);

            } catch (error) {

                console.error(error);

                setError(
                    error.response?.data?.message ||
                    "Unable to load categories."
                );

            } finally {

                setLoadingCategories(false);
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

        if (formData.name.trim().length > 200) {
            setError(
                "Product name cannot exceed 200 characters."
            );
            return;
        }

        if (formData.price === "") {
            setError("Price is required.");
            return;
        }

        if (Number(formData.price) < 0) {
            setError("Price cannot be negative.");
            return;
        }

        if (formData.quantity === "") {
            setError("Quantity is required.");
            return;
        }

        if (
            !Number.isInteger(
                Number(formData.quantity)
            )
        ) {
            setError("Quantity must be a whole number.");
            return;
        }

        if (Number(formData.quantity) < 0) {
            setError("Quantity cannot be negative.");
            return;
        }

        if (!formData.categoryId) {
            setError("Category is required.");
            return;
        }

        try {

            setSubmitting(true);

            const product = {
                name: formData.name.trim(),
                description: formData.description.trim(),
                imageUrl: formData.imageUrl.trim(),
                price: Number(formData.price),
                quantity: Number(formData.quantity),
                categoryId: Number(formData.categoryId)
            };

            const createdProduct =
                await createProduct(product);

            setFormData({
                name: "",
                description: "",
                imageUrl: "",
                price: "",
                quantity: "",
                categoryId: ""
            });

            onProductCreated(createdProduct);

        } catch (error) {

            console.error(error);

            setError(
                error.response?.data?.message ||
                "Unable to create product."
            );

        } finally {

            setSubmitting(false);
        }
    };

    if (loadingCategories) {

        return (
            <div>
                <h2>Create Product</h2>
                <p>Loading categories...</p>
            </div>
        );
    }

    return (
        <div>

            <h2>Create Product</h2>

            {error && (
                <p>{error}</p>
            )}

            <form onSubmit={handleSubmit}>

                <div>
                    <label htmlFor="name">
                        Name
                    </label>

                    <br />

                    <input
                        id="name"
                        type="text"
                        name="name"
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
                        type="url"
                        name="imageUrl"
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
                        type="number"
                        name="price"
                        value={formData.price}
                        onChange={handleChange}
                        min="0"
                        step="0.01"
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
                        type="number"
                        name="quantity"
                        value={formData.quantity}
                        onChange={handleChange}
                        min="0"
                        step="1"
                        required
                    />
                </div>

                <br />

                <div>
                    <label htmlFor="categoryId">
                        Category
                    </label>

                    <br />

                    <select
                        id="categoryId"
                        name="categoryId"
                        value={formData.categoryId}
                        onChange={handleChange}
                        required
                    >
                        <option value="">
                            Select category
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
                </div>

                <br />

                <button
                    type="submit"
                    disabled={submitting}
                >
                    {submitting
                        ? "Creating..."
                        : "Create Product"}
                </button>

                {" "}

                <button
                    type="button"
                    onClick={onCancel}
                    disabled={submitting}
                >
                    Cancel
                </button>

            </form>

        </div>
    );
}

export default CreateProduct;
