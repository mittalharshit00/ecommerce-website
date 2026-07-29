
import apiClient from "./apiClient";

const CATEGORY_URL = "/categories";


export const getCategories = async (
    page = 0,
    size = 10
) => {

    const response = await apiClient.get(
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

    const response = await apiClient.get(
        `${CATEGORY_URL}/${id}`
    );

    return response.data;
};


export const createCategory = async (category) => {

    const response = await apiClient.post(
        CATEGORY_URL,
        category
    );

    return response.data;
};


export const updateCategory = async (
    id,
    category
) => {

    const response = await apiClient.put(
        `${CATEGORY_URL}/${id}`,
        category
    );

    return response.data;
};


export const deleteCategory = async (id) => {

    await apiClient.delete(
        `${CATEGORY_URL}/${id}`
    );
};

