import apiClient from "./apiClient";

export const getCurrentTenant = async () => {

    const response = await apiClient.get(
        "/tenants/me"
    );

    return response.data;
};