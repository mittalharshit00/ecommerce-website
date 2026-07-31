import { useState } from "react";
import { useNavigate } from "react-router-dom";

import { createTenant } from "../services/platformService";

function CreateTenant() {

    const navigate = useNavigate();

    const [formData, setFormData] = useState({
        name: "",
        domain: ""
    });

    const [loading, setLoading] = useState(false);

    const [error, setError] = useState("");

    const [success, setSuccess] = useState("");

    const handleChange = (event) => {

        const { name, value } = event.target;

        setFormData(previous => ({
            ...previous,
            [name]: value
        }));

    };

    const handleSubmit = async (event) => {

        event.preventDefault();

        setLoading(true);
        setError("");
        setSuccess("");

        try {

            await createTenant(formData);

            setSuccess(
                "Tenant created successfully."
            );

            setTimeout(() => {

                navigate("/platform");

            }, 1500);

        } catch (error) {

            setError(

                error.response?.data?.message ||

                "Unable to create tenant."

            );

        } finally {

            setLoading(false);

        }

    };

    return (

        <div className="container mt-5">

            <div className="row justify-content-center">

                <div className="col-lg-6">

                    <div className="card">

                        <div className="card-header">

                            <h3 className="mb-0">
                                Create Tenant
                            </h3>

                        </div>

                        <div className="card-body">

                            {

                                success &&

                                <div className="alert alert-success">

                                    {success}

                                </div>

                            }

                            {

                                error &&

                                <div className="alert alert-danger">

                                    {error}

                                </div>

                            }

                            <form
                                onSubmit={handleSubmit}
                            >

                                <div className="mb-3">

                                    <label className="form-label">

                                        Tenant Name

                                    </label>

                                    <input
                                        type="text"
                                        name="name"
                                        className="form-control"
                                        value={formData.name}
                                        onChange={handleChange}
                                        required
                                    />

                                </div>

                                <div className="mb-4">

                                    <label className="form-label">

                                        Tenant Domain

                                    </label>

                                    <input
                                        type="text"
                                        name="domain"
                                        className="form-control"
                                        value={formData.domain}
                                        onChange={handleChange}
                                        required
                                    />

                                </div>

                                <button
                                    type="submit"
                                    className="btn btn-primary"
                                    disabled={loading}
                                >

                                    {

                                        loading

                                            ? "Creating..."

                                            : "Create Tenant"

                                    }

                                </button>

                            </form>

                        </div>

                    </div>

                </div>

            </div>

        </div>

    );

}

export default CreateTenant;