package org.labmonkeys.cajun_navy.incident.api;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.labmonkeys.cajun_navy.incident.dto.IncidentDTO;
import org.labmonkeys.cajun_navy.incident.dto.PriorityZoneDTO;
import org.labmonkeys.cajun_navy.incident.dto.IncidentDTO.IncidentStatus;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.eventbus.EventBus;

@Path("/incidents")
public class IncidentApi {

    @Inject
    EventBus bus;

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> incidents() {
        return bus.<List<IncidentDTO>>request("incidents", null).onItem().transform(msg -> Response.ok(msg.body()).build());
    }

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> createIncident(IncidentDTO incident) {
        return bus.request("createIncident", incident).onItem().transform(msg -> Response.ok().build());
    }

    @GET
    @Path("/{status}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> incidentsByStatus(@PathParam("status") IncidentStatus status) {
        return bus.<List<IncidentDTO>>request("incidentStatus", status).onItem().transform(msg -> Response.ok(msg.body()).build());
    }

    @GET
    @Path("/incident/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> incidentById(@PathParam("id") String incidentId) {
        return bus.<IncidentDTO>request("incidentById", incidentId).onItem().transform(msg -> {
            IncidentDTO incident = msg.body(); 
            if (incident == null) {
                return Response.status(Status.NOT_FOUND).build();
            } else {
                return Response.ok(incident).build();
            }
        });
    }

    @GET
    @Path("/byname/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> incidentsByName(@PathParam("name") String name) {
        return bus.<List<IncidentDTO>>request("incidentsByName", name).onItem().transform(msg -> Response.ok(msg.body()).build());
    }

    @POST
    @Path("/reset")
    public Uni<Response> reset() {
        return bus.request("reset", null).onItem().transform(msg -> Response.ok().build());
    }

    @GET
    @Path("/priority/zones")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> incidentPriorityZones() {
        return bus.<List<PriorityZoneDTO>>request("priority-zones", null).onItem().transform(msg -> Response.ok(msg.body()).build());
    }

    @POST
    @Path("/priority/reset")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> resetPriority() {
        return bus.request("priority-reset", null).onItem().transform(msg -> Response.ok().build());
    }

    @POST
    @Path("/priority/zone")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> applyPriorityZone(PriorityZoneDTO zone) {
        return bus.<List<PriorityZoneDTO>>request("apply-priority-zone", zone).onItem().transform(msg -> Response.ok(msg.body()).build());
    }

    @DELETE
    @Path("/priority/zones")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> clearPriorityZones() {
        return bus.request("clear-priority-zones", null).onItem().transform(msg -> Response.ok().build());
    }
}
