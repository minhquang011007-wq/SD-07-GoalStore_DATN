import axios from "axios";

console.log("api.ts loaded");

const api = axios.create({
  baseURL: "http://localhost:8080",
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  config.headers = config.headers || {};
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  console.log("REQUEST =>", config.baseURL, config.url, config.params);
  return config;
});
   
export default api;