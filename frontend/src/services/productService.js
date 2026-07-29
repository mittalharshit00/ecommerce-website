
import apiClient from "./apiClient";

const PRODUCT_URL = "/products";

export const getProducts = async (
    page = 0,
    size = 10
) => {

    const response = await apiClient.get(
        PRODUCT_URL,
        {
            params: {
                page,
                size
            }
        }
    );

    return response.data;
};

export const getProductById = async (id) => {

    const response = await apiClient.get(
        `${PRODUCT_URL}/${id}`
    );

    return response.data;
};

export const getProductsByCategory = async (
    categoryId,
    page = 0,
    size = 10
) => {

    const response = await apiClient.get(
        `${PRODUCT_URL}/category/${categoryId}`,
        {
            params: {
                page,
                size
            }
        }
    );

    return response.data;
};

export const createProduct = async (product) => {

    const response = await apiClient.post(
        PRODUCT_URL,
        product
    );

    return response.data;
};

export const updateProduct = async (
    id,
    product
) => {

    const response = await apiClient.put(
        `${PRODUCT_URL}/${id}`,
        product
    );

    return response.data;
};

export const deleteProduct = async (id) => {

    await apiClient.delete(
        `${PRODUCT_URL}/${id}`
    );
};

