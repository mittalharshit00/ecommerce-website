import api from "./api";
import apiClient from "./apiClient";


const PRODUCT_URL = "/products";



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





export const getProductById = async (
    id
) => {

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








export const createProduct = async (
    tenant,
    product,
    image
) => {


    const formData =
        new FormData();



    formData.append(
        "product",
        new Blob(
            [
                JSON.stringify(product)
            ],
            {
                type:
                    "application/json"
            }
        )
    );



    if (image) {

        formData.append(
            "image",
            image
        );

    }





    const response =
        await apiClient.post(

            `/${tenant}/products`,

            formData,

            {
                headers: {

                    "Content-Type":
                        "multipart/form-data"

                }
            }

        );



    return response.data;

};








export const updateProduct = async (
    tenant,
    id,
    product
) => {


    const response =
        await apiClient.put(

            `/${tenant}/products/${id}`,

            product

        );


    return response.data;

};








export const deleteProduct = async (
    tenant,
    id
) => {


    await apiClient.delete(

        `/${tenant}/products/${id}`

    );

};