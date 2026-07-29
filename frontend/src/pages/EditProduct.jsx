import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import {
    getProductById,
    updateProduct
} from "../services/productService";

import { getCategories } from "../services/categoryService";
import { useAuth } from "../context/useAuth";

function EditProduct() {

    const { id } = useParams();
    const navigate = useNavigate();

    const { isAdmin } = useAuth();

    const [categories, setCategories] = useState([]);

    const [formData, setFormData] = useState({
        name: "",
        description: "",
        imageUrl: "",
        price: "",
        quantity: "",
        categoryId: ""
    });

    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState("");

    useEffect(() => {

        if (!isAdmin) {
            navigate("/products");
            return;
        }

        const loadData = async () => {

            try {

                setLoading(true);
                setError("");

                const [product, categoryData] =
                    await Promise.all([
                        getProductById(id),
                        getCategories()
                    ]);

                setFormData({
                    name: product.name || "",
                    description: product.description || "",
                    imageUrl: product.imageUrl || "",
                    price: product.price ?? "",
                    quantity: product.quantity ?? "",
                    categoryId: product.categoryId ?? ""
                });

                setCategories(
                    categoryData.content || []
                );

            } catch (error) {

                console.error(error);

                setError(
                    "Unable to load product."
                );

            } finally {

                setLoading(false);
            }
        };

        loadData();

    }, [id, isAdmin, navigate]);

    const handleChange = (event) => {

        const { name, value } = event.target;

        setFormData((previous) => ({
            ...previous,
            [name]: value
        }));
    };

    const handleSubmit = async (event) => {

        event.preventDefault();

        try {

            setSaving(true);
            setError("");

            await updateProduct(id, {
                name: formData.name,
                description: formData.description,
                imageUrl: formData.imageUrl,
                price: Number(formData.price),
                quantity: Number(formData.quantity),
                categoryId: Number(formData.categoryId)
            });

            navigate(`/products/${id}`);

        } catch (error) {

            console.error(error);

            setError(
                error.response?.data?.message ||
                "Unable to update product."
            );

        } finally {

            setSaving(false);
        }
    };

    if (!isAdmin) {
        return null;
    }

    if (loading) {
        return (
            <div>
                <h1>Edit Product</h1>
                <p>Loading product...</p>
            </div>
        );
    }

    return (
        <div>

            <h1>Edit Product</h1>

            {error && (
                <p>{error}</p>
            )}

            <form onSubmit={handleSubmit}>

                <div>
                    <label>
                        Name
                    </label>

                    <input
                        type="text"
                        name="name"
                        value={formData.name}
                        onChange={handleChange}
                        required
                    />
                </div>

                <br />

                <div>
                    <label>
                        Description
                    </label>

                    <textarea
                        name="description"
                        value={formData.description}
                        onChange={handleChange}
                    />
                </div>

                <br />

                <div>
                    <label>
                        Image URL
                    </label>

                    <input
                        type="text"
                        name="imageUrl"
                        value={formData.imageUrl}
                        onChange={handleChange}
                    />
                </div>

                <br />

                <div>
                    <label>
                        Price
                    </label>

                    <input
                        type="number"
                        name="price"
                        min="0"
                        step="0.01"
                        value={formData.price}
                        onChange={handleChange}
                        required
                    />
                </div>

                <br />

                <div>
                    <label>
                        Quantity
                    </label>

                    <input
                        type="number"
                        name="quantity"
                        min="0"
                        value={formData.quantity}
                        onChange={handleChange}
                        required
                    />
                </div>

                <br />

                <div>
                    <label>
                        Category
                    </label>

                    <select
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
                    disabled={saving}
                >
                    {saving
                        ? "Updating..."
                        : "Update Product"}
                </button>

                {" "}

                <button
                    type="button"
                    onClick={() =>
                        navigate(`/products/${id}`)
                    }
                >
                    Cancel
                </button>

            </form>

        </div>
    );
}

export default EditProduct;
