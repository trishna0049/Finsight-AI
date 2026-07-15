import { Navigate, Outlet, useLocation } from "react-router-dom";
import { useAuth } from "@/store/authStore";

export function ProtectedRoute(): JSX.Element {
  const { isAuthenticated, loading } = useAuth();
  const location = useLocation();

  if (loading) {
    return <div className="p-8 text-slate-300">Authenticating session...</div>;
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  return <Outlet />;
}
