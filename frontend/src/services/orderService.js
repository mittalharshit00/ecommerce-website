import apiClient from "./apiClient";

const ORDER_URL = "/orders";

export const createOrder = async (order) => {

    const response = await apiClient.post(
        ORDER_URL,
        order
    );

    return response.data;
};

export const getOrders = async (
    page = 0,
    size = 10
) => {

    const response = await apiClient.get(
        ORDER_URL,
        {
            params: {
                page,
                size
            }
        }
    );

    return response.data;
};

export const getOrderById = async (id) => {

    const response = await apiClient.get(
        `${ORDER_URL}/${id}`
    );

    return response.data;
};

/**
 * Admin APIs
 */

export const getAdminOrders = async (
    tenant,
    page = 0,
    size = 10
) => {

    const response = await apiClient.get(
        `/${tenant}/orders`,
        {
            params: {
                page,
                size
            }
        }
    );

    return response.data;
};

export const getAdminOrderById = async (
    tenant,
    id
) => {

    const response = await apiClient.get(
        `/${tenant}/orders/${id}`
    );

    return response.data;
};

export const updateOrderStatus = async (
    tenant,
    id,
    status
) => {

    const response = await apiClient.patch(
        `/${tenant}/orders/${id}/status`,
        null,
        {
            params: {
                status
            }
        }
    );

    return response.data;
};