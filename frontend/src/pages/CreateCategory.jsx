
import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";

import {
    createCategory
} from "../services/categoryService";

import { useAuth } from "../context/useAuth";


function CreateCategory() {

    const navigate = useNavigate();

    const { isAdmin } = useAuth();

    const [name, setName] = useState("");

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");


    if (!isAdmin) {

        return (
            <div>

                <h1>
                    Access Denied
                </h1>

                <p>
                    You do not have permission
                    to create categories.
                </p>

                <Link to="/categories">
                    Back to Categories
                </Link>

            </div>
        );
    }


    const handleSubmit = async (event) => {

        event.preventDefault();

        try {

            setLoading(true);
            setError("");

            await createCategory({
                name
            });

            navigate("/categories");

        } catch (error) {

            console.error(error);

            setError(
                error.response?.data?.message ||
                "Unable to create category."
            );

        } finally {

            setLoading(false);
        }
    };


    return (
        <div>

            <header>

                <h1>
                    Create Category
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
                        disabled={loading}
                    >
                        {loading
                            ? "Creating..."
                            : "Create Category"}
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

export default CreateCategory;
