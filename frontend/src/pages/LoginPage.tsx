import { FormEvent, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "@/store/authStore";

export function LoginPage(): JSX.Element {
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [username, setUsername] = useState("incident.analyst");
  const [password, setPassword] = useState("password");
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  const from = (location.state as { from?: { pathname?: string } } | null)?.from?.pathname ?? "/dashboard";

  async function onSubmit(event: FormEvent<HTMLFormElement>): Promise<void> {
    event.preventDefault();
    setError(null);
    setSubmitting(true);
    try {
      await login(username, password);
      navigate(from, { replace: true });
    } catch {
      setError("Unable to authenticate. Verify credentials and backend availability.");
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <section className="mx-auto mt-16 max-w-md rounded-xl border border-white/10 bg-panel/80 p-6 shadow-panel backdrop-blur-xl">
      <h1 className="text-xl font-semibold">Sign in to FinSight AI</h1>
      <p className="mt-2 text-sm text-slate-400">Use seeded users: incident.analyst / password or platform.admin / password</p>
      <form className="mt-6 space-y-4" onSubmit={(event) => void onSubmit(event)}>
        <div>
          <label className="mb-1 block text-sm text-slate-300" htmlFor="username">
            Username
          </label>
          <input
            id="username"
            className="w-full rounded-lg border border-white/15 bg-slate-900 px-3 py-2 text-sm outline-none ring-accent focus:ring"
            value={username}
            onChange={(event) => setUsername(event.target.value)}
          />
        </div>
        <div>
          <label className="mb-1 block text-sm text-slate-300" htmlFor="password">
            Password
          </label>
          <input
            id="password"
            type="password"
            className="w-full rounded-lg border border-white/15 bg-slate-900 px-3 py-2 text-sm outline-none ring-accent focus:ring"
            value={password}
            onChange={(event) => setPassword(event.target.value)}
          />
        </div>
        {error ? <p className="text-sm text-red-300">{error}</p> : null}
        <button
          type="submit"
          disabled={submitting}
          className="w-full rounded-lg bg-accent px-4 py-2 text-sm font-semibold text-slate-900 transition hover:opacity-90 disabled:opacity-50"
        >
          {submitting ? "Signing in..." : "Sign in"}
        </button>
      </form>
    </section>
  );
}
