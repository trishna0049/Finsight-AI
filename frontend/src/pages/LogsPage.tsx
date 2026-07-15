import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { fetchLogs } from "@/services/analystApi";

export function LogsPage(): JSX.Element {
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(50);
  const [service, setService] = useState("");
  const [level, setLevel] = useState("");

  const { data, isLoading, isError } = useQuery({
    queryKey: ["logs", page, size, service, level],
    queryFn: () =>
      fetchLogs({
        page,
        size,
        service: service || undefined,
        level: level || undefined
      })
  });

  return (
    <section className="space-y-4">
      <h1 className="text-2xl font-semibold">Log Explorer</h1>
      <div className="grid gap-3 rounded-xl border border-white/10 bg-white/5 p-3 lg:grid-cols-4">
        <input
          className="rounded-md border border-white/10 bg-slate-900 px-2 py-2 text-sm"
          placeholder="Filter by service"
          value={service}
          onChange={(event) => {
            setService(event.target.value);
            setPage(0);
          }}
        />
        <select
          className="rounded-md border border-white/10 bg-slate-900 px-2 py-2 text-sm"
          value={level}
          onChange={(event) => {
            setLevel(event.target.value);
            setPage(0);
          }}
        >
          <option value="">All Levels</option>
          <option value="INFO">INFO</option>
          <option value="WARN">WARN</option>
          <option value="ERROR">ERROR</option>
        </select>
        <select
          className="rounded-md border border-white/10 bg-slate-900 px-2 py-2 text-sm"
          value={size}
          onChange={(event) => {
            setSize(Number(event.target.value));
            setPage(0);
          }}
        >
          <option value={25}>25 / page</option>
          <option value={50}>50 / page</option>
          <option value={100}>100 / page</option>
        </select>
        <button
          type="button"
          className="rounded-md border border-white/10 bg-slate-900 px-2 py-2 text-sm"
          onClick={() => {
            setService("");
            setLevel("");
            setPage(0);
          }}
        >
          Reset Filters
        </button>
      </div>
      <div className="overflow-x-auto rounded-xl border border-white/10">
        <table className="min-w-full text-sm">
          <thead className="bg-white/5 text-slate-300">
            <tr>
              <th className="px-3 py-2 text-left">Timestamp</th>
              <th className="px-3 py-2 text-left">Service</th>
              <th className="px-3 py-2 text-left">Level</th>
              <th className="px-3 py-2 text-left">Error</th>
              <th className="px-3 py-2 text-left">Message</th>
            </tr>
          </thead>
          <tbody>
            {(data?.content ?? []).map((entry) => (
              <tr key={`${entry.timestamp}-${entry.correlationId}`} className="border-t border-white/10">
                <td className="px-3 py-2">{new Date(entry.timestamp).toLocaleString()}</td>
                <td className="px-3 py-2">{entry.service}</td>
                <td className="px-3 py-2">{entry.logLevel}</td>
                <td className="px-3 py-2">{entry.errorCode ?? "-"}</td>
                <td className="px-3 py-2">{entry.message}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      <div className="flex items-center justify-between rounded-xl border border-white/10 bg-white/5 p-3 text-xs text-slate-300">
        <p>
          Page {(data?.number ?? 0) + 1} of {Math.max(data?.totalPages ?? 1, 1)}
        </p>
        <div className="space-x-2">
          <button
            type="button"
            className="rounded-md border border-white/15 px-2 py-1 disabled:opacity-40"
            disabled={page <= 0}
            onClick={() => setPage((prev) => Math.max(prev - 1, 0))}
          >
            Previous
          </button>
          <button
            type="button"
            className="rounded-md border border-white/15 px-2 py-1 disabled:opacity-40"
            disabled={page >= Math.max((data?.totalPages ?? 1) - 1, 0)}
            onClick={() => setPage((prev) => prev + 1)}
          >
            Next
          </button>
        </div>
      </div>
      {isLoading ? <p className="text-slate-400">Loading logs...</p> : null}
      {isError ? <p className="text-red-300">Failed to load logs.</p> : null}
    </section>
  );
}
