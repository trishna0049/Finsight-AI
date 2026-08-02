import { useMemo } from "react";
import { useQuery } from "@tanstack/react-query";
import {
  Bar,
  BarChart,
  CartesianGrid,
  Cell,
  Legend,
  Line,
  LineChart,
  Pie,
  PieChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis
} from "recharts";
import { fetchAnalyticsOverview } from "@/services/analystApi";

const severityColors: Record<string, string> = {
  CRITICAL: "#ff5f6d",
  HIGH: "#ffb020",
  MEDIUM: "#00d1b2",
  LOW: "#5dade2"
};

const dowMap = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];

export function AnalyticsPage(): JSX.Element {
  const { data, isLoading, isError } = useQuery({
    queryKey: ["analytics-overview"],
    queryFn: fetchAnalyticsOverview,
    refetchInterval: 60_000
  });

  const heatmapBars = useMemo(() => {
    const source = data?.incidentHeatmap ?? [];
    return source.map((point) => ({
      slot: `${dowMap[point.day]} ${String(point.hour).padStart(2, "0")}:00`,
      count: point.count
    }));
  }, [data?.incidentHeatmap]);

  return (
    <section className="space-y-6">
      <div>
        <h1 className="text-2xl font-semibold">Analytics</h1>
        <p className="text-slate-400">Operational trends over the last 14 days with severity and service concentration insights.</p>
      </div>

      {isLoading ? <p className="text-slate-400">Loading analytics...</p> : null}
      {isError ? <p className="text-red-300">Failed to load analytics overview.</p> : null}

      <div className="grid gap-4 md:grid-cols-2">
        <article className="rounded-xl border border-white/10 bg-white/5 p-4">
          <p className="text-xs uppercase tracking-wide text-slate-400">Average MTTR</p>
          <p className="mt-2 text-3xl font-semibold text-slate-50">{data?.averageMttrMinutes ?? 0} min</p>
        </article>
        <article className="rounded-xl border border-white/10 bg-white/5 p-4">
          <p className="text-xs uppercase tracking-wide text-slate-400">Average Response</p>
          <p className="mt-2 text-3xl font-semibold text-slate-50">{data?.averageResponseTimeMs ?? 0} ms</p>
        </article>
      </div>

      <div className="grid gap-4 xl:grid-cols-2">
        <article className="rounded-xl border border-white/10 bg-white/5 p-4">
          <h2 className="mb-3 text-lg font-medium">Incident Trend (14d)</h2>
          <div className="h-72">
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={data?.incidentTrend ?? []}>
                <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.08)" />
                <XAxis dataKey="label" stroke="#94a3b8" />
                <YAxis stroke="#94a3b8" />
                <Tooltip />
                <Line type="monotone" dataKey="value" stroke="#00d1b2" strokeWidth={3} dot={false} />
              </LineChart>
            </ResponsiveContainer>
          </div>
        </article>

        <article className="rounded-xl border border-white/10 bg-white/5 p-4">
          <h2 className="mb-3 text-lg font-medium">SLA Breach Trend (Response &gt; 1500ms)</h2>
          <div className="h-72">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={data?.slaBreachTrend ?? []}>
                <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.08)" />
                <XAxis dataKey="label" stroke="#94a3b8" />
                <YAxis stroke="#94a3b8" />
                <Tooltip />
                <Bar dataKey="value" fill="#ff7a59" radius={[6, 6, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </article>

        <article className="rounded-xl border border-white/10 bg-white/5 p-4">
          <h2 className="mb-3 text-lg font-medium">Service Availability Trend (14 points)</h2>
          <div className="h-72">
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={data?.serviceAvailabilityTrend ?? []}>
                <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.08)" />
                <XAxis dataKey="label" stroke="#94a3b8" />
                <YAxis stroke="#94a3b8" domain={[95, 100]} />
                <Tooltip />
                <Line type="monotone" dataKey="value" stroke="#58d68d" strokeWidth={3} dot={false} />
              </LineChart>
            </ResponsiveContainer>
          </div>
        </article>

        <article className="rounded-xl border border-white/10 bg-white/5 p-4">
          <h2 className="mb-3 text-lg font-medium">Severity Distribution</h2>
          <div className="h-72">
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie data={data?.severityDistribution ?? []} dataKey="count" nameKey="severity" outerRadius={110}>
                  {(data?.severityDistribution ?? []).map((entry) => (
                    <Cell key={entry.severity} fill={severityColors[entry.severity] ?? "#64748b"} />
                  ))}
                </Pie>
                <Tooltip />
                <Legend />
              </PieChart>
            </ResponsiveContainer>
          </div>
        </article>

        <article className="rounded-xl border border-white/10 bg-white/5 p-4">
          <h2 className="mb-3 text-lg font-medium">Top Failing Services</h2>
          <div className="h-72">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={data?.topFailingServices ?? []}>
                <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.08)" />
                <XAxis dataKey="service" stroke="#94a3b8" hide />
                <YAxis stroke="#94a3b8" />
                <Tooltip />
                <Bar dataKey="incidents" fill="#5dade2" radius={[6, 6, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
          <div className="mt-3 grid gap-1 text-xs text-slate-300">
            {(data?.topFailingServices ?? []).map((entry) => (
              <p key={entry.service}>
                {entry.service}: {entry.incidents}
              </p>
            ))}
          </div>
        </article>

        <article className="rounded-xl border border-white/10 bg-white/5 p-4">
          <h2 className="mb-3 text-lg font-medium">Incident Heatmap Density</h2>
          <div className="h-72">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={heatmapBars.slice(0, 40)}>
                <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.08)" />
                <XAxis dataKey="slot" stroke="#94a3b8" hide />
                <YAxis stroke="#94a3b8" />
                <Tooltip />
                <Bar dataKey="count" fill="#ffb020" radius={[4, 4, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
          <p className="mt-2 text-xs text-slate-400">Showing first 40 time buckets for readability.</p>
        </article>
      </div>
    </section>
  );
}
