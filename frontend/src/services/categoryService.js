import api from "./api";
import apiClient from "./apiClient";

const CATEGORY_URL = "/categories";

export const getCategories = async (
    page = 0,
    size = 10
) => {

    const response = await api.get(
        CATEGORY_URL,
        {
            params: {
                page,
                size
            }
        }
    );

    return response.data;
};

export const getCategoryById = async (id) => {

    const response = await api.get(
        `${CATEGORY_URL}/${id}`
    );

    return response.data;
};

export const createCategory = async (
    tenant,
    category
) => {

    const response = await apiClient.post(
        `/${tenant}/categories`,
        category
    );

    return response.data;
};

export const updateCategory = async (
    tenant,
    id,
    category
) => {

    const response = await apiClient.put(
        `/${tenant}/categories/${id}`,
        category
    );

    return response.data;
};

export const deleteCategory = async (
    tenant,
    id
) => {

    await apiClient.delete(
        `/${tenant}/categories/${id}`
    );
};