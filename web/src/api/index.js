import axios from "axios";

const ax = axios.create({
    baseURL: process.env.VUE_APP_API || "http://localhost:8080",
    headers: {
        "Content-type": "application/json",
    },
});

export default ax;
