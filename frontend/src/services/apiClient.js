import axios from "axios";

import {
    getAccessToken,
    refreshAccessToken,
    logout
} from "./authService";


const apiClient = axios.create({
    baseURL: "http://localhost:8080/api/local"
});


/*
 * Add access token to every request.
 */
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


/*
 * Handle expired access token.
 */
apiClient.interceptors.response.use(

    (response) => {

        return response;
    },

    async (error) => {

        const originalRequest =
            error.config;


        /*
         * Only try refresh when:
         *
         * 1. Backend returned 401
         * 2. We haven't already retried this request
         */
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


                /*
                 * Put new token on the
                 * original request.
                 */
                originalRequest.headers.Authorization =
                    `Bearer ${newAccessToken}`;


                /*
                 * Retry original request.
                 */
                return apiClient(
                    originalRequest
                );

            } catch (refreshError) {

                console.error(
                    "Refresh token failed:",
                    refreshError
                );


                /*
                 * Refresh token itself is no
                 * longer valid.
                 *
                 * NOW we require login.
                 */
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
