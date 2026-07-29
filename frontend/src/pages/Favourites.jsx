import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

import {
    getFavourites,
    removeFavourite
} from "../services/favouriteService";

function Favourites() {

    const [favourites, setFavourites] = useState([]);
    const [loading, setLoading] = useState(true);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    const loadFavourites = async () => {

        try {

            setLoading(true);

            const data = await getFavourites(
                page,
                10
            );

            setFavourites(data.content);
            setTotalPages(data.totalPages);

        } catch (error) {

            console.error(error);

        } finally {

            setLoading(false);
        }
    };

    useEffect(() => {

        loadFavourites();

    }, [page]);

    const handleRemove = async (
        productId
    ) => {

        try {

            await removeFavourite(
                productId
            );

            loadFavourites();

        } catch (error) {

            console.error(error);
        }
    };

    if (loading) {
        return <h2>Loading...</h2>;
    }

    return (

        <div>

            <h1>My Favourites</h1>

            <Link to="/products">
                Back to Products
            </Link>

            <hr/>

            {favourites.length === 0 && (
                <p>No favourite products.</p>
            )}

            {favourites.map(favourite => (

                <div
                    key={favourite.id}
                >

                    <h3>
                        {favourite.productName}
                    </h3>

                    <p>
                        Price: $
                        {favourite.price}
                    </p>

                    {favourite.imageUrl && (

                        <img
                            src={
                                favourite.imageUrl
                            }
                            alt={
                                favourite.productName
                            }
                            width="150"
                        />

                    )}

                    <br/><br/>

                    <Link
                        to={`/products/${favourite.productId}`}
                    >
                        View Product
                    </Link>

                    {" "}

                    <button
                        onClick={() =>
                            handleRemove(
                                favourite.productId
                            )
                        }
                    >
                        Remove Favourite
                    </button>

                    <hr/>

                </div>

            ))}

            {totalPages > 1 && (

                <div>

                    <button
                        disabled={page===0}
                        onClick={() =>
                            setPage(page-1)
                        }
                    >
                        Previous
                    </button>

                    {" "}

                    Page {page+1}
                    {" / "}
                    {totalPages}

                    {" "}

                    <button
                        disabled={
                            page===totalPages-1
                        }
                        onClick={() =>
                            setPage(page+1)
                        }
                    >
                        Next
                    </button>

                </div>

            )}

        </div>

    );
}

export default Favourites;