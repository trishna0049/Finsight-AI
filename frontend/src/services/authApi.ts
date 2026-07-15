import { httpClient } from "@/services/httpClient";
import type { ApiResponse, Tokens, UserProfile } from "@/types/api";

export async function login(username: string, password: string): Promise<Tokens> {
  const response = await httpClient.post<ApiResponse<Tokens>>("/auth/login", { username, password });
  return response.data.data;
}

export async function profile(): Promise<UserProfile> {
  const response = await httpClient.get<ApiResponse<UserProfile>>("/auth/profile");
  return response.data.data;
}

export async function logout(refreshToken: string): Promise<void> {
  await httpClient.post<ApiResponse<null>>("/auth/logout", { refreshToken });
}
