import axios from "axios";

import {
    getAccessToken,
    refreshAccessToken,
    logout
} from "./authService";


const apiClient = axios.create({
    baseURL: "http://localhost:8080/api"
});


apiClient.interceptors.request.use(
    (config) => {

        const token = getAccessToken();

        if (token) {

            config.headers.Authorization =
                `Bearer ${token}`;
        }

        return config;
    },

    (error) => {

        return Promise.reject(error);
    }
);


apiClient.interceptors.response.use(

    (response) => {

        return response;
    },

    async (error) => {

        const originalRequest =
            error.config;


        if (
            error.response?.status === 401 &&
            !originalRequest._retry
        ) {

            originalRequest._retry = true;

            try {

                console.log(
                    "Access token expired. Refreshing..."
                );

                const newAccessToken =
                    await refreshAccessToken();


                originalRequest.headers.Authorization =
                    `Bearer ${newAccessToken}`;


                return apiClient(
                    originalRequest
                );

            } catch (refreshError) {

                console.error(
                    "Refresh token failed:",
                    refreshError
                );

                logout();

                window.location.href =
                    "/login";

                return Promise.reject(
                    refreshError
                );
            }
        }

        return Promise.reject(error);
    }
);

export default apiClient;