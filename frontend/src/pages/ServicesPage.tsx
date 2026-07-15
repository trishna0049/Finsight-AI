import { useQuery } from "@tanstack/react-query";
import { fetchServices } from "@/services/analystApi";

export function ServicesPage(): JSX.Element {
  const { data, isLoading, isError } = useQuery({
    queryKey: ["services"],
    queryFn: fetchServices,
    refetchInterval: 30_000
  });

  return (
    <section className="space-y-4">
      <h1 className="text-2xl font-semibold">Service Monitoring</h1>
      {isLoading ? <p className="text-slate-400">Loading monitored services...</p> : null}
      {isError ? <p className="text-red-300">Failed to load service telemetry.</p> : null}
      <div className="overflow-x-auto rounded-xl border border-white/10">
        <table className="min-w-full text-sm">
          <thead className="bg-white/5 text-slate-300">
            <tr>
              <th className="px-3 py-2 text-left">Service</th>
              <th className="px-3 py-2 text-left">Status</th>
              <th className="px-3 py-2 text-left">Latency</th>
              <th className="px-3 py-2 text-left">CPU</th>
              <th className="px-3 py-2 text-left">Memory</th>
              <th className="px-3 py-2 text-left">Req/s</th>
              <th className="px-3 py-2 text-left">Availability</th>
            </tr>
          </thead>
          <tbody>
            {(data ?? []).map((service) => (
              <tr key={service.id} className="border-t border-white/10">
                <td className="px-3 py-2">{service.name}</td>
                <td className="px-3 py-2">{service.status}</td>
                <td className="px-3 py-2">{service.latencyMs} ms</td>
                <td className="px-3 py-2">{service.cpuUsage}%</td>
                <td className="px-3 py-2">{service.memoryUsage}%</td>
                <td className="px-3 py-2">{service.requestsPerSec}</td>
                <td className="px-3 py-2">{service.availabilityPct}%</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </section>
  );
}
