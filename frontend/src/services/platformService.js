
import apiClient from "./apiClient";


const PLATFORM_URL = "/platform";



/*
 * Create Tenant
 */
export const createTenant = async (data) => {

    const response =
        await apiClient.post(
            `${PLATFORM_URL}/tenants`,
            data
        );

    return response.data;

};





/*
 * Get users available for tenant admin assignment
 */
export const getPlatformUsers = async () => {

    const response =
        await apiClient.get(
            `${PLATFORM_URL}/users`
        );

    return response.data;

};





/*
 * Get all tenants
 */
export const getPlatformTenants = async () => {

    const response =
        await apiClient.get(
            `${PLATFORM_URL}/tenants`
        );

    return response.data;

};





/*
 * Assign tenant admin
 */
export const assignTenantAdmin = async (
    userId,
    tenantId
) => {


    await apiClient.put(

        `${PLATFORM_URL}/users/${userId}/admin`,

        {
            tenantId
        }

    );

};

