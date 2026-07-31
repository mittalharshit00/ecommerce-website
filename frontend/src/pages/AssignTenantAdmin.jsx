import {
    useEffect,
    useState
} from "react";

import {
    useNavigate
} from "react-router-dom";


import {
    assignTenantAdmin,
    getPlatformUsers,
    getPlatformTenants
} from "../services/platformService";



function AssignTenantAdmin() {


    const navigate =
        useNavigate();



    const [users, setUsers] =
        useState([]);



    const [tenants, setTenants] =
        useState([]);



    const [formData, setFormData] =
        useState({

            userId: "",
            tenantId: ""

        });



    const [loading, setLoading] =
        useState(false);



    const [pageLoading, setPageLoading] =
        useState(true);



    const [success, setSuccess] =
        useState("");



    const [error, setError] =
        useState("");





    useEffect(() => {


        const loadData = async () => {


            try {


                const [
                    usersResponse,
                    tenantsResponse
                ] = await Promise.all([

                    getPlatformUsers(),

                    getPlatformTenants()

                ]);



                setUsers(
                    usersResponse
                );



                setTenants(
                    tenantsResponse
                );


            } catch (error) {


                console.error(error);


                setError(
                    "Unable to load users or tenants."
                );


            }
            finally {


                setPageLoading(false);

            }

        };



        loadData();



    }, []);






    const handleChange = (event) => {


        const {
            name,
            value
        } = event.target;



        setFormData(previous => ({

            ...previous,

            [name]: value

        }));

    };







    const handleSubmit = async (event) => {


        event.preventDefault();


        setLoading(true);

        setSuccess("");

        setError("");



        try {


            await assignTenantAdmin(

                Number(formData.userId),

                Number(formData.tenantId)

            );



            setSuccess(
                "Tenant admin assigned successfully."
            );



            setTimeout(() => {


                navigate("/platform");


            },1500);



        }
        catch(error) {


            setError(

                error.response?.data?.message ||

                "Unable to assign tenant admin."

            );

        }
        finally {


            setLoading(false);

        }


    };







    if(pageLoading) {


        return (

            <div className="container mt-5">

                Loading...

            </div>

        );

    }






    return (


        <div className="container mt-5">


            <div className="row justify-content-center">


                <div className="col-lg-6">


                    <div className="card">


                        <div className="card-header">

                            <h3>
                                Assign Tenant Admin
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




                            <form onSubmit={handleSubmit}>



                                <div className="mb-3">


                                    <label className="form-label">

                                        Select User

                                    </label>



                                    <select

                                        className="form-select"

                                        name="userId"

                                        value={formData.userId}

                                        onChange={handleChange}

                                        required

                                    >


                                        <option value="">

                                            Choose User

                                        </option>


                                        {
                                            users.map(user => (

                                                <option

                                                    key={user.id}

                                                    value={user.id}

                                                >

                                                    {user.username}
                                                    {" "}
                                                    -
                                                    {" "}
                                                    {user.email}

                                                </option>

                                            ))
                                        }


                                    </select>


                                </div>






                                <div className="mb-4">


                                    <label className="form-label">

                                        Select Tenant

                                    </label>



                                    <select

                                        className="form-select"

                                        name="tenantId"

                                        value={formData.tenantId}

                                        onChange={handleChange}

                                        required

                                    >


                                        <option value="">

                                            Choose Tenant

                                        </option>



                                        {
                                            tenants.map(tenant => (

                                                <option

                                                    key={tenant.id}

                                                    value={tenant.id}

                                                >

                                                    {tenant.name}
                                                    {" "}
                                                    (
                                                    {tenant.domain}
                                                    )

                                                </option>


                                            ))
                                        }


                                    </select>



                                </div>






                                <button

                                    className="btn btn-success"

                                    type="submit"

                                    disabled={loading}

                                >

                                    {

                                        loading

                                        ?

                                        "Assigning..."

                                        :

                                        "Assign Tenant Admin"

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



export default AssignTenantAdmin;