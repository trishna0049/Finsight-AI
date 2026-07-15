import { Link, NavLink, Outlet } from "react-router-dom";
import { useAuth } from "@/store/authStore";

const navItems = [
  { to: "/dashboard", label: "Dashboard" },
  { to: "/services", label: "Service Monitoring" },
  { to: "/incidents", label: "Incidents" },
  { to: "/simulator", label: "Incident Simulator" },
  { to: "/logs", label: "Log Explorer" },
  { to: "/analytics", label: "Analytics" }
];

export function AppLayout(): JSX.Element {
  const { user, logout } = useAuth();

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100">
      <div className="mx-auto grid max-w-7xl grid-cols-1 gap-4 p-4 lg:grid-cols-[260px_1fr]">
        <aside className="rounded-2xl border border-white/10 bg-panel/80 p-4 shadow-panel backdrop-blur-xl">
          <Link to="/dashboard" className="mb-8 block text-xl font-semibold tracking-wide text-accent">
            FinSight AI
          </Link>
          <nav className="space-y-2">
            {navItems.map((item) => (
              <NavLink
                key={item.to}
                to={item.to}
                className={({ isActive }) =>
                  [
                    "block rounded-lg px-3 py-2 text-sm font-medium transition",
                    isActive ? "bg-accent/20 text-accent" : "text-slate-300 hover:bg-white/5"
                  ].join(" ")
                }
              >
                {item.label}
              </NavLink>
            ))}
          </nav>
          <div className="mt-8 rounded-lg border border-white/10 bg-white/5 p-3 text-xs text-slate-300">
            <p className="font-medium text-slate-100">{user?.fullName ?? "User"}</p>
            <p className="mt-1">{user?.roles.join(", ")}</p>
            <button
              type="button"
              className="mt-3 rounded-md bg-slate-800 px-3 py-1 text-xs hover:bg-slate-700"
              onClick={() => {
                void logout();
              }}
            >
              Logout
            </button>
          </div>
        </aside>
        <main className="rounded-2xl border border-white/10 bg-surface/70 p-6 shadow-panel backdrop-blur-xl">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
