
import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";

import {
    getCategoryById,
    updateCategory
} from "../services/categoryService";

import { useAuth } from "../context/useAuth";


function EditCategory() {

    const { id } = useParams();

    const navigate = useNavigate();

    const { isAdmin } = useAuth();

    const [name, setName] = useState("");

    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);

    const [error, setError] = useState("");


    useEffect(() => {

        if (!isAdmin) {
            return;
        }

        loadCategory();

    }, [id, isAdmin]);


    const loadCategory = async () => {

        try {

            setLoading(true);
            setError("");

            const category =
                await getCategoryById(id);

            setName(
                category.name || ""
            );

        } catch (error) {

            console.error(error);

            setError(
                error.response?.data?.message ||
                "Unable to load category."
            );

        } finally {

            setLoading(false);
        }
    };


    const handleSubmit = async (event) => {

        event.preventDefault();

        try {

            setSaving(true);
            setError("");

            await updateCategory(
                id,
                {
                    name
                }
            );

            navigate("/categories");

        } catch (error) {

            console.error(error);

            setError(
                error.response?.data?.message ||
                "Unable to update category."
            );

        } finally {

            setSaving(false);
        }
    };


    if (!isAdmin) {

        return (
            <div>

                <h1>
                    Access Denied
                </h1>

                <p>
                    You do not have permission
                    to edit categories.
                </p>

                <Link to="/categories">
                    Back to Categories
                </Link>

            </div>
        );
    }


    if (loading) {

        return (
            <div>

                <h1>
                    Edit Category
                </h1>

                <p>
                    Loading category...
                </p>

            </div>
        );
    }


    return (
        <div>

            <header>

                <h1>
                    Edit Category
                </h1>

                <nav>

                    <Link to="/dashboard">
                        Dashboard
                    </Link>

                    {" | "}

                    <Link to="/categories">
                        Categories
                    </Link>

                </nav>

            </header>

            <hr />

            <main>

                {error && (
                    <p>
                        {error}
                    </p>
                )}


                <form
                    onSubmit={handleSubmit}
                >

                    <div>

                        <label>
                            Category Name
                        </label>

                        <br />

                        <input
                            type="text"
                            value={name}
                            onChange={(event) =>
                                setName(
                                    event.target.value
                                )
                            }
                            maxLength={100}
                            required
                        />

                    </div>

                    <br />

                    <button
                        type="submit"
                        disabled={saving}
                    >
                        {saving
                            ? "Updating..."
                            : "Update Category"}
                    </button>

                    {" "}

                    <Link to="/categories">
                        Cancel
                    </Link>

                </form>

            </main>

        </div>
    );
}

export default EditCategory;
