import apiClient from "./apiClient";


export const syncUser = async () => {

    const response =
        await apiClient.post(
            "/users/sync"
        );

    return response.data;
};