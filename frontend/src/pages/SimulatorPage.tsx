import { useMutation } from "@tanstack/react-query";
import { simulateIncident } from "@/services/analystApi";

export function SimulatorPage(): JSX.Element {
  const mutation = useMutation({
    mutationFn: (scenario: string) => simulateIncident(scenario)
  });

  const actions = [
    { scenario: "payment-failure", label: "Simulate Payment Failure" },
    { scenario: "database-failure", label: "Simulate Database Failure" },
    { scenario: "api-timeout", label: "Simulate API Timeout" },
    { scenario: "authentication-failure", label: "Simulate Authentication Failure" },
    { scenario: "memory-leak", label: "Simulate Memory Leak" }
  ];

  return (
    <section className="space-y-4">
      <h1 className="text-2xl font-semibold">Incident Simulator</h1>
      <p className="text-slate-400">Trigger enterprise incident scenarios and push events through Kafka processing pipeline.</p>
      <div className="grid gap-3 md:grid-cols-2">
        {actions.map((action) => (
          <button
            key={action.scenario}
            type="button"
            className="rounded-lg border border-white/10 bg-white/5 px-4 py-3 text-left transition hover:bg-white/10"
            onClick={() => mutation.mutate(action.scenario)}
          >
            {action.label}
          </button>
        ))}
      </div>
      {mutation.isPending ? <p className="text-slate-300">Simulation in progress...</p> : null}
      {mutation.isSuccess ? <p className="text-emerald-300">Scenario submitted successfully.</p> : null}
      {mutation.isError ? <p className="text-red-300">Simulation failed. Verify backend and Kafka availability.</p> : null}
    </section>
  );
}
