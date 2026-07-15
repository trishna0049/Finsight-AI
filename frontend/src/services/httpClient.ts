import axios from "axios";
import { authStorage } from "@/services/authStorage";

export const httpClient = axios.create({
  baseURL: import.meta.env.VITE_BACKEND_API_URL ?? "http://localhost:8080/api/v1",
  timeout: 15_000
});

httpClient.interceptors.request.use((config) => {
  const token = authStorage.getAccessToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

