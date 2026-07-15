import { createContext, useContext, useEffect, useMemo, useState } from "react";
import { authStorage } from "@/services/authStorage";
import * as authApi from "@/services/authApi";
import type { UserProfile } from "@/types/api";

interface AuthContextValue {
  user: UserProfile | null;
  loading: boolean;
  isAuthenticated: boolean;
  login: (username: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: { children: React.ReactNode }): JSX.Element {
  const [user, setUser] = useState<UserProfile | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = authStorage.getAccessToken();
    if (!token) {
      setLoading(false);
      return;
    }

    authApi
      .profile()
      .then(setUser)
      .catch(() => {
        authStorage.clear();
        setUser(null);
      })
      .finally(() => setLoading(false));
  }, []);

  const value = useMemo<AuthContextValue>(
    () => ({
      user,
      loading,
      isAuthenticated: Boolean(user),
      async login(username: string, password: string) {
        const tokens = await authApi.login(username, password);
        authStorage.setTokens(tokens.accessToken, tokens.refreshToken);
        const profile = await authApi.profile();
        setUser(profile);
      },
      async logout() {
        const refreshToken = authStorage.getRefreshToken();
        if (refreshToken) {
          await authApi.logout(refreshToken).catch(() => undefined);
        }
        authStorage.clear();
        setUser(null);
      }
    }),
    [user, loading]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthContextValue {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within AuthProvider");
  }
  return context;
}
