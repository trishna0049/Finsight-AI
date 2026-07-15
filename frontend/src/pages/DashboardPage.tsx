import { useQuery } from "@tanstack/react-query";
import { fetchDashboardSummary } from "@/services/analystApi";

export function DashboardPage(): JSX.Element {
  const { data, isLoading, isError } = useQuery({
    queryKey: ["dashboard-summary"],
    queryFn: fetchDashboardSummary,
    refetchInterval: 30_000
  });

  const metrics = [
    { label: "Active Incidents", value: data?.activeIncidents ?? 0 },
    { label: "Critical Incidents", value: data?.criticalIncidents ?? 0 },
    { label: "Open Tickets", value: data?.openTickets ?? 0 },
    { label: "Resolved Today", value: data?.resolvedToday ?? 0 },
    { label: "Average MTTR (min)", value: Number((data?.averageMttrMinutes ?? 0).toFixed(2)) },
    { label: "Average Response (ms)", value: Number((data?.averageResponseTimeMs ?? 0).toFixed(2)) }
  ];

  return (
    <section className="space-y-4">
      <h1 className="text-2xl font-semibold">Enterprise Dashboard</h1>
      {isLoading ? <p className="text-slate-400">Loading dashboard metrics...</p> : null}
      {isError ? <p className="text-red-300">Failed to load dashboard metrics.</p> : null}
      <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
        {metrics.map((metric) => (
          <article key={metric.label} className="rounded-xl border border-white/10 bg-white/5 p-4">
            <p className="text-xs uppercase tracking-wide text-slate-400">{metric.label}</p>
            <p className="mt-2 text-2xl font-semibold text-slate-50">{metric.value}</p>
          </article>
        ))}
      </div>
    </section>
  );
}
