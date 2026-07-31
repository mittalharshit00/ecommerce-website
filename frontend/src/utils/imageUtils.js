const API_URL = "http://localhost:8080";


export const getImageUrl = (imageUrl) => {

    if (!imageUrl) {

        return "/no-image.png";

    }


    if (
        imageUrl.startsWith("http")
    ) {

        return imageUrl;

    }


    return `${API_URL}${imageUrl}`;

};