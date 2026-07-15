import { lazy, Suspense } from "react";
import { Navigate, Route, Routes } from "react-router-dom";
import { AppLayout } from "@/layouts/AppLayout";
import { ProtectedRoute } from "@/routes/ProtectedRoute";

const DashboardPage = lazy(async () => import("@/pages/DashboardPage").then((module) => ({ default: module.DashboardPage })));
const ServicesPage = lazy(async () => import("@/pages/ServicesPage").then((module) => ({ default: module.ServicesPage })));
const IncidentsPage = lazy(async () => import("@/pages/IncidentsPage").then((module) => ({ default: module.IncidentsPage })));
const SimulatorPage = lazy(async () => import("@/pages/SimulatorPage").then((module) => ({ default: module.SimulatorPage })));
const LoginPage = lazy(async () => import("@/pages/LoginPage").then((module) => ({ default: module.LoginPage })));
const LogsPage = lazy(async () => import("@/pages/LogsPage").then((module) => ({ default: module.LogsPage })));
const AnalyticsPage = lazy(async () => import("@/pages/AnalyticsPage").then((module) => ({ default: module.AnalyticsPage })));

function RouteFallback(): JSX.Element {
  return <div className="p-6 text-sm text-slate-300">Loading module...</div>;
}

export function AppRouter(): JSX.Element {
  return (
    <Suspense fallback={<RouteFallback />}>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route element={<ProtectedRoute />}>
          <Route element={<AppLayout />}>
            <Route path="/dashboard" element={<DashboardPage />} />
            <Route path="/services" element={<ServicesPage />} />
            <Route path="/incidents" element={<IncidentsPage />} />
            <Route path="/simulator" element={<SimulatorPage />} />
            <Route path="/logs" element={<LogsPage />} />
            <Route path="/analytics" element={<AnalyticsPage />} />
          </Route>
        </Route>
        <Route path="*" element={<Navigate to="/dashboard" replace />} />
      </Routes>
    </Suspense>
  );
}
