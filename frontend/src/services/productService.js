import api from "./api";
import apiClient from "./apiClient";
import { TENANT_NAME } from "../constants";

const PRODUCT_URL = "/products";
const ADMIN_PRODUCT_URL = `/${TENANT_NAME}/products`;

export const getProducts = async (
    page = 0,
    size = 10
) => {

    const response = await api.get(
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

    const response = await api.get(
        `${PRODUCT_URL}/${id}`
    );

    return response.data;
};


export const getProductsByCategory = async (
    categoryId,
    page = 0,
    size = 10
) => {

    const response = await api.get(
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
        ADMIN_PRODUCT_URL,
        product
    );

    return response.data;
};


export const updateProduct = async (
    id,
    product
) => {

    const response = await apiClient.put(
        `${ADMIN_PRODUCT_URL}/${id}`,
        product
    );

    return response.data;
};


export const deleteProduct = async (id) => {

    await apiClient.delete(
        `${ADMIN_PRODUCT_URL}/${id}`
    );
};