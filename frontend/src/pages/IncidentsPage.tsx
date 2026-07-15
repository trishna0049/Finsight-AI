import { useMemo, useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  addIncidentComment,
  assignIncident,
  fetchAssignees,
  fetchIncidentDetails,
  fetchIncidents,
  resolveIncident
} from "@/services/analystApi";

export function IncidentsPage(): JSX.Element {
  const [selectedId, setSelectedId] = useState<number | null>(null);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(25);
  const [statusFilter, setStatusFilter] = useState("");
  const [severityFilter, setSeverityFilter] = useState("");
  const [serviceFilter, setServiceFilter] = useState("");
  const [sortBy, setSortBy] = useState("updatedAt");
  const [direction, setDirection] = useState<"asc" | "desc">("desc");
  const [resolution, setResolution] = useState("");
  const [commentDraft, setCommentDraft] = useState("");
  const [assigneeId, setAssigneeId] = useState<number | "">("");
  const queryClient = useQueryClient();

  const incidentsQuery = useQuery({
    queryKey: ["incidents", page, size, statusFilter, severityFilter, serviceFilter, sortBy, direction],
    queryFn: () =>
      fetchIncidents({
        page,
        size,
        status: statusFilter || undefined,
        severity: severityFilter || undefined,
        service: serviceFilter || undefined,
        sortBy,
        direction
      })
  });

  const selectedIncident = useQuery({
    queryKey: ["incident", selectedId],
    queryFn: () => fetchIncidentDetails(selectedId as number),
    enabled: selectedId !== null
  });

  const assigneesQuery = useQuery({
    queryKey: ["assignees"],
    queryFn: fetchAssignees
  });

  const resolveMutation = useMutation({
    mutationFn: ({ id, resolutionText }: { id: number; resolutionText: string }) => resolveIncident(id, resolutionText),
    onSuccess: async () => {
      await Promise.all([
        queryClient.invalidateQueries({ queryKey: ["incidents"] }),
        queryClient.invalidateQueries({ queryKey: ["incident", selectedId] }),
        queryClient.invalidateQueries({ queryKey: ["dashboard-summary"] })
      ]);
      setResolution("");
    }
  });

  const assignMutation = useMutation({
    mutationFn: ({ id, userId }: { id: number; userId: number }) => assignIncident(id, userId),
    onSuccess: async () => {
      await Promise.all([
        queryClient.invalidateQueries({ queryKey: ["incidents"] }),
        queryClient.invalidateQueries({ queryKey: ["incident", selectedId] })
      ]);
    }
  });

  const commentMutation = useMutation({
    mutationFn: ({ id, content }: { id: number; content: string }) => addIncidentComment(id, content),
    onSuccess: async () => {
      setCommentDraft("");
      await queryClient.invalidateQueries({ queryKey: ["incident", selectedId] });
    }
  });

  const incidents = incidentsQuery.data?.content ?? [];

  const unresolvedCount = useMemo(
    () => incidents.filter((incident) => incident.status !== "RESOLVED" && incident.status !== "CLOSED").length,
    [incidents]
  );

  return (
    <section className="space-y-4">
      <h1 className="text-2xl font-semibold">Incident Management</h1>
      <p className="text-slate-400">Active incidents on this page: {unresolvedCount}</p>
      <div className="grid gap-3 rounded-xl border border-white/10 bg-white/5 p-3 lg:grid-cols-6">
        <select
          className="rounded-md border border-white/10 bg-slate-900 px-2 py-2 text-sm"
          value={statusFilter}
          onChange={(event) => {
            setStatusFilter(event.target.value);
            setPage(0);
          }}
        >
          <option value="">All Statuses</option>
          <option value="OPEN">Open</option>
          <option value="INVESTIGATING">Investigating</option>
          <option value="RESOLVED">Resolved</option>
          <option value="CLOSED">Closed</option>
        </select>

        <select
          className="rounded-md border border-white/10 bg-slate-900 px-2 py-2 text-sm"
          value={severityFilter}
          onChange={(event) => {
            setSeverityFilter(event.target.value);
            setPage(0);
          }}
        >
          <option value="">All Severities</option>
          <option value="LOW">Low</option>
          <option value="MEDIUM">Medium</option>
          <option value="HIGH">High</option>
          <option value="CRITICAL">Critical</option>
        </select>

        <input
          className="rounded-md border border-white/10 bg-slate-900 px-2 py-2 text-sm"
          placeholder="Filter by service"
          value={serviceFilter}
          onChange={(event) => {
            setServiceFilter(event.target.value);
            setPage(0);
          }}
        />

        <select
          className="rounded-md border border-white/10 bg-slate-900 px-2 py-2 text-sm"
          value={sortBy}
          onChange={(event) => {
            setSortBy(event.target.value);
            setPage(0);
          }}
        >
          <option value="updatedAt">Sort by Updated Time</option>
          <option value="createdAt">Sort by Created Time</option>
          <option value="severity">Sort by Severity</option>
          <option value="status">Sort by Status</option>
        </select>

        <select
          className="rounded-md border border-white/10 bg-slate-900 px-2 py-2 text-sm"
          value={direction}
          onChange={(event) => {
            setDirection(event.target.value as "asc" | "desc");
            setPage(0);
          }}
        >
          <option value="desc">Descending</option>
          <option value="asc">Ascending</option>
        </select>

        <select
          className="rounded-md border border-white/10 bg-slate-900 px-2 py-2 text-sm"
          value={size}
          onChange={(event) => {
            setSize(Number(event.target.value));
            setPage(0);
          }}
        >
          <option value={10}>10 / page</option>
          <option value={25}>25 / page</option>
          <option value={50}>50 / page</option>
        </select>
      </div>

      <div className="grid gap-4 lg:grid-cols-[1.2fr_1fr]">
        <div className="overflow-x-auto rounded-xl border border-white/10">
          <table className="min-w-full text-sm">
            <thead className="bg-white/5 text-slate-300">
              <tr>
                <th className="px-3 py-2 text-left">Incident</th>
                <th className="px-3 py-2 text-left">Service</th>
                <th className="px-3 py-2 text-left">Severity</th>
                <th className="px-3 py-2 text-left">Status</th>
              </tr>
            </thead>
            <tbody>
              {incidents.map((incident) => (
                <tr
                  key={incident.id}
                  className="cursor-pointer border-t border-white/10 hover:bg-white/5"
                  onClick={() => setSelectedId(incident.id)}
                >
                  <td className="px-3 py-2">
                    <p className="font-medium">{incident.incidentKey}</p>
                    <p className="text-xs text-slate-400">{incident.title}</p>
                  </td>
                  <td className="px-3 py-2">{incident.service}</td>
                  <td className="px-3 py-2">{incident.severity}</td>
                  <td className="px-3 py-2">{incident.status}</td>
                </tr>
              ))}
            </tbody>
          </table>
          {incidentsQuery.isLoading ? <p className="p-3 text-slate-400">Loading incidents...</p> : null}
          {incidentsQuery.isError ? <p className="p-3 text-red-300">Failed to load incidents.</p> : null}
          <div className="flex items-center justify-between border-t border-white/10 p-3 text-xs text-slate-300">
            <p>
              Page {(incidentsQuery.data?.number ?? 0) + 1} of {Math.max(incidentsQuery.data?.totalPages ?? 1, 1)}
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
                disabled={page >= Math.max((incidentsQuery.data?.totalPages ?? 1) - 1, 0)}
                onClick={() => setPage((prev) => prev + 1)}
              >
                Next
              </button>
            </div>
          </div>
        </div>

        <div className="rounded-xl border border-white/10 bg-white/5 p-4">
          {!selectedId ? <p className="text-slate-400">Select an incident to inspect details.</p> : null}
          {selectedIncident.isLoading ? <p className="text-slate-400">Loading incident details...</p> : null}
          {selectedIncident.data ? (
            <div className="space-y-3">
              <h2 className="text-lg font-semibold">{selectedIncident.data.incidentKey}</h2>
              <p className="text-sm text-slate-300">{selectedIncident.data.description}</p>
              <p className="text-xs text-slate-400">Status: {selectedIncident.data.status}</p>
              <p className="text-xs text-slate-400">Assigned To: {selectedIncident.data.assignedTo ?? "Unassigned"}</p>
              <p className="text-xs text-slate-400">Root Cause: {selectedIncident.data.rootCause ?? "N/A"}</p>
              <p className="text-xs text-slate-400">AI Summary: {selectedIncident.data.aiSummary ?? "N/A"}</p>

              <div className="space-y-2 rounded-lg border border-white/10 bg-slate-900/60 p-3">
                <p className="text-sm font-medium">Assignment</p>
                <select
                  className="w-full rounded-md border border-white/10 bg-slate-900 px-2 py-2 text-sm"
                  value={assigneeId}
                  onChange={(event) => setAssigneeId(event.target.value ? Number(event.target.value) : "")}
                >
                  <option value="">Select assignee</option>
                  {(assigneesQuery.data ?? []).map((assignee) => (
                    <option key={assignee.id} value={assignee.id}>
                      {assignee.username} ({assignee.role})
                    </option>
                  ))}
                </select>
                <button
                  type="button"
                  className="rounded-lg bg-slate-700 px-3 py-2 text-sm font-semibold disabled:opacity-50"
                  disabled={assigneeId === "" || assignMutation.isPending}
                  onClick={() => {
                    if (selectedIncident.data && assigneeId !== "") {
                      assignMutation.mutate({ id: selectedIncident.data.id, userId: assigneeId });
                    }
                  }}
                >
                  {assignMutation.isPending ? "Assigning..." : "Assign Incident"}
                </button>
              </div>

              <textarea
                className="h-24 w-full rounded-lg border border-white/10 bg-slate-900 p-2 text-sm"
                placeholder="Add resolution note"
                value={resolution}
                onChange={(event) => setResolution(event.target.value)}
              />
              <button
                type="button"
                className="rounded-lg bg-accent px-3 py-2 text-sm font-semibold text-slate-900 disabled:opacity-50"
                disabled={!resolution || resolveMutation.isPending}
                onClick={() => {
                  if (!selectedIncident.data) {
                    return;
                  }
                  resolveMutation.mutate({ id: selectedIncident.data.id, resolutionText: resolution });
                }}
              >
                {resolveMutation.isPending ? "Resolving..." : "Resolve Incident"}
              </button>
              {resolveMutation.isError ? <p className="text-sm text-red-300">Failed to resolve incident.</p> : null}

              <div className="space-y-2 rounded-lg border border-white/10 bg-slate-900/60 p-3">
                <p className="text-sm font-medium">Add Timeline Comment</p>
                <textarea
                  className="h-20 w-full rounded-lg border border-white/10 bg-slate-900 p-2 text-sm"
                  placeholder="Share investigation notes"
                  value={commentDraft}
                  onChange={(event) => setCommentDraft(event.target.value)}
                />
                <button
                  type="button"
                  className="rounded-lg bg-slate-700 px-3 py-2 text-sm font-semibold disabled:opacity-50"
                  disabled={!commentDraft.trim() || commentMutation.isPending}
                  onClick={() => {
                    if (!selectedIncident.data) {
                      return;
                    }
                    commentMutation.mutate({ id: selectedIncident.data.id, content: commentDraft.trim() });
                  }}
                >
                  {commentMutation.isPending ? "Posting..." : "Add Comment"}
                </button>
              </div>

              {selectedIncident.data.comments.length > 0 ? (
                <div className="space-y-2">
                  <p className="text-sm font-medium">Timeline Comments</p>
                  {selectedIncident.data.comments.slice(-5).map((comment) => (
                    <article key={comment.id} className="rounded-md border border-white/10 bg-slate-900 p-2 text-xs">
                      <p className="text-slate-200">{comment.content}</p>
                      <p className="mt-1 text-slate-500">{comment.author}</p>
                    </article>
                  ))}
                </div>
              ) : null}
            </div>
          ) : null}
        </div>
      </div>
    </section>
  );
}
