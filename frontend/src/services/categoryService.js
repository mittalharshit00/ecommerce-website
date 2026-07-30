import api from "./api";
import apiClient from "./apiClient";
import { TENANT_NAME } from "../constants";

const CATEGORY_URL = "/categories";
const ADMIN_CATEGORY_URL = `/${TENANT_NAME}/categories`;

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


export const createCategory = async (category) => {

    const response = await apiClient.post(
        ADMIN_CATEGORY_URL,
        category
    );

    return response.data;
};


export const updateCategory = async (
    id,
    category
) => {

    const response = await apiClient.put(
        `${ADMIN_CATEGORY_URL}/${id}`,
        category
    );

    return response.data;
};


export const deleteCategory = async (id) => {

    await apiClient.delete(
        `${ADMIN_CATEGORY_URL}/${id}`
    );
};