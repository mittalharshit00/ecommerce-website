import { useEffect, useState, useCallback } from "react";
import { Link } from "react-router-dom";

import {
    getProducts,
    deleteProduct
} from "../services/productService";

import {
    getFavourites,
    addFavourite,
    removeFavourite
} from "../services/favouriteService";

import { useAuth } from "../context/useAuth";

import { getImageUrl } from "../utils/imageUtils";


function Products() {


    const {
        isAdmin,
        tenant
    } = useAuth();



    const [products, setProducts] =
        useState([]);



    const [favouriteIds, setFavouriteIds] =
        useState([]);



    const [loading, setLoading] =
        useState(true);



    const [error, setError] =
        useState("");



    const [page, setPage] =
        useState(0);



    const [totalPages, setTotalPages] =
        useState(0);





    const navItems = [

        {
            to: "/dashboard",
            label: "Dashboard"
        },

        {
            to: "/products",
            label: "Products"
        },

        {
            to: "/categories",
            label: "Categories"
        },

        {
            to: "/favourites",
            label: "Favourites"
        },

        {
            to: "/orders",
            label: "Orders"
        }

    ];







    const loadFavourites = useCallback(async () => {


        try {


            const data =
                await getFavourites(0, 100);



            setFavouriteIds(

                data.content.map(

                    favourite =>
                        favourite.productId

                )

            );


        } catch (error) {


            console.error(error);


        }


    }, []);








    const loadProducts = useCallback(async () => {


        try {


            setLoading(true);

            setError("");



            const data =
                await getProducts(
                    page,
                    10
                );



            setProducts(
                data.content || []
            );



            setTotalPages(
                data.totalPages || 0
            );


        } catch (error) {


            console.error(error);



            setError(

                error.response?.data?.message ||

                "Unable to load products."

            );


        } finally {


            setLoading(false);

        }


    }, [page]);








    useEffect(() => {


        loadProducts();

        loadFavourites();


    }, [
        loadProducts,
        loadFavourites
    ]);









    const handleAddFavourite = async (
        productId
    ) => {


        try {


            await addFavourite(
                productId
            );



            setFavouriteIds(
                previous => [

                    ...previous,

                    productId

                ]
            );


        } catch (error) {


            console.error(error);



            setError(

                error.response?.data?.message ||

                "Unable to add favourite."

            );

        }

    };









    const handleRemoveFavourite = async (
        productId
    ) => {


        try {


            await removeFavourite(
                productId
            );



            setFavouriteIds(

                previous =>

                    previous.filter(

                        id =>
                            id !== productId

                    )

            );


        } catch (error) {


            console.error(error);



            setError(

                error.response?.data?.message ||

                "Unable to remove favourite."

            );

        }

    };









    const handleDelete = async (
        id
    ) => {


        const confirmed =
            window.confirm(
                "Are you sure you want to delete this product?"
            );



        if (!confirmed) {

            return;

        }





        try {


            await deleteProduct(
                tenant,
                id
            );



            setProducts(

                currentProducts =>

                    currentProducts.filter(

                        product =>
                            product.id !== id

                    )

            );


        } catch (error) {


            console.error(error);



            setError(

                error.response?.data?.message ||

                "Unable to delete product."

            );

        }

    };









    const renderContent = () => {


        if (loading) {


            return (

                <div className="card card-soft border-0 p-4 text-center">

                    <div
                        className="spinner-border text-primary mx-auto mb-3"
                        role="status"
                    />

                    <h2 className="h5">
                        Loading products...
                    </h2>

                </div>

            );

        }






        if (error) {


            return (

                <div className="alert alert-danger">

                    {error}

                </div>

            );

        }







        if (products.length === 0) {


            return (

                <div className="card card-soft border-0 p-4 text-center">

                    <h2 className="h5">
                        No products found
                    </h2>


                    <p className="text-muted mb-0">

                        Try again later or create a new product.

                    </p>


                </div>

            );

        }







        return (

            <div className="row g-4">


                {
                    products.map(product => (


                        <div
                            key={product.id}
                            className="col-12 col-lg-6"
                        >



                            <div className="card card-soft h-100 border-0">


                                {
                                    product.imageUrl && (

                                        <img

                                            src={
                                                getImageUrl(
                                                    product.imageUrl
                                                )
                                            }

                                            alt={product.name}

                                            style={{

                                                width: "100%",

                                                height: "250px",

                                                objectFit: "cover",

                                                borderRadius:
                                                    "10px 10px 0 0"

                                            }}

                                        />

                                    )

                                }





                                <div className="card-body d-flex flex-column">



                                    <div className="d-flex justify-content-between gap-3 mb-3">


                                        <div>

                                            <h3 className="h5 mb-1">

                                                {product.name}

                                            </h3>


                                            <p className="text-muted mb-0">

                                                {
                                                    product.categoryName ||
                                                    "Uncategorised"
                                                }

                                            </p>


                                        </div>




                                        <span className="badge text-bg-primary">

                                            ${product.price}

                                        </span>


                                    </div>





                                    <p className="text-muted flex-grow-1">

                                        {
                                            product.description ||
                                            "No description available."
                                        }

                                    </p>





                                    <div className="d-flex flex-wrap gap-2 mb-3">


                                        <span className="badge text-bg-light">

                                            Qty:
                                            {" "}
                                            {product.quantity}

                                        </span>



                                        <span className="badge text-bg-light">

                                            Category:
                                            {" "}
                                            {
                                                product.categoryName ||
                                                "—"
                                            }

                                        </span>


                                    </div>







                                    <div className="d-flex flex-wrap gap-2">


                                        <Link

                                            to={`/products/${product.id}`}

                                            className="btn btn-outline-primary btn-sm"

                                        >

                                            View Details

                                        </Link>





                                        {
                                            favouriteIds.includes(
                                                product.id
                                            )

                                                ?

                                                (

                                                    <button

                                                        className="btn btn-outline-danger btn-sm"

                                                        onClick={() =>
                                                            handleRemoveFavourite(
                                                                product.id
                                                            )
                                                        }

                                                    >

                                                        ♥ Remove Favourite

                                                    </button>

                                                )

                                                :

                                                (

                                                    <button

                                                        className="btn btn-outline-warning btn-sm"

                                                        onClick={() =>
                                                            handleAddFavourite(
                                                                product.id
                                                            )
                                                        }

                                                    >

                                                        ♡ Add Favourite

                                                    </button>

                                                )

                                        }







                                        {
                                            isAdmin && (

                                                <>

                                                    <Link

                                                        to={`/products/${product.id}/edit`}

                                                        className="btn btn-outline-secondary btn-sm"

                                                    >

                                                        Edit

                                                    </Link>





                                                    <button

                                                        className="btn btn-danger btn-sm"

                                                        onClick={() =>
                                                            handleDelete(
                                                                product.id
                                                            )
                                                        }

                                                    >

                                                        Delete

                                                    </button>


                                                </>

                                            )

                                        }



                                    </div>



                                </div>


                            </div>



                        </div>


                    ))

                }


            </div>

        );


    };









    return (

        <div className="min-vh-100 bg-light">



            <header className="bg-white shadow-sm border-bottom">


                <div className="container py-3 d-flex flex-column flex-lg-row justify-content-between gap-3">


                    <div>

                        <h1 className="h3 mb-1">
                            Products
                        </h1>

                        <p className="text-muted mb-0">

                            Browse the catalogue and manage availability.

                        </p>

                    </div>





                    <nav className="d-flex flex-wrap gap-2">


                        {
                            navItems.map(item => (


                                <Link

                                    key={item.to}

                                    to={item.to}

                                    className="nav-link px-3 py-2 rounded-pill"

                                >

                                    {item.label}

                                </Link>


                            ))

                        }




                        {
                            isAdmin && (

                                <Link

                                    to="/products/create"

                                    className="btn btn-primary btn-sm"

                                >

                                    Create Product

                                </Link>

                            )

                        }


                    </nav>


                </div>


            </header>








            <main className="container py-4">


                <div className="card card-soft border-0 p-4 mb-4">


                    <h2 className="h4">
                        Product List
                    </h2>


                </div>





                {renderContent()}






                {
                    totalPages > 1 && (


                        <div className="d-flex justify-content-between mt-4">


                            <button

                                className="btn btn-outline-secondary btn-sm"

                                disabled={
                                    page === 0
                                }

                                onClick={() =>
                                    setPage(
                                        page - 1
                                    )
                                }

                            >

                                Previous

                            </button>




                            <span>

                                Page {page + 1}
                                {" "}
                                of
                                {" "}
                                {totalPages}

                            </span>





                            <button

                                className="btn btn-outline-secondary btn-sm"

                                disabled={
                                    page >= totalPages - 1
                                }

                                onClick={() =>
                                    setPage(
                                        page + 1
                                    )
                                }

                            >

                                Next

                            </button>



                        </div>


                    )

                }


            </main>


        </div>

    );


}


export default Products;