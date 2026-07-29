import apiClient from "./apiClient";

const FAVOURITE_URL = "/favourites";

export const getFavourites = async (
    page = 0,
    size = 20
) => {

    const response = await apiClient.get(
        FAVOURITE_URL,
        {
            params: {
                page,
                size
            }
        }
    );

    return response.data;
};

export const addFavourite = async (
    productId
) => {

    const response = await apiClient.post(
        `${FAVOURITE_URL}/${productId}`
    );

    return response.data;
};

export const removeFavourite = async (
    productId
) => {

    await apiClient.delete(
        `${FAVOURITE_URL}/${productId}`
    );
};