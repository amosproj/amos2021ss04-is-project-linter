import axios from "axios";

const ax = axios.create({
    baseURL: process.env.VUE_APP_API || "http://localhost:6969/api",
    // headers: {
    //     "Content-type": "application/json",
    // },
});

export default ax;
