package org.labmonkeys.cajun_navy.incident.api;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.labmonkeys.cajun_navy.incident.dto.IncidentDTO;
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
        //DeliveryOptions options = new DeliveryOptions().addHeader("action", "incidents");
        //return bus.<JsonObject>request("incident-service", new JsonObject(), options)
        //        .onItem().transform(msg -> Response.ok(msg.body().getJsonArray("incidents").encode()).build());
    }

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> createIncident(IncidentDTO incident) {
        return bus.request("createIncident", incident).onItem().transform(msg -> Response.ok().build());
        // DeliveryOptions options = new DeliveryOptions().addHeader("action", "createIncident");
        // return bus.<JsonObject>request("incident-service", new JsonObject(incident), options)
        //         .onItem().transform(msg -> Response.status(200).build());
    }

    @GET
    @Path("/{status}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> incidentsByStatus(@PathParam("status") IncidentStatus status) {
        return bus.<List<IncidentDTO>>request("incidentStatus", status).onItem().transform(msg -> Response.ok(msg.body()).build());
        // DeliveryOptions options = new DeliveryOptions().addHeader("action", "incidentsByStatus");
        // return bus.<JsonObject>request("incident-service", new JsonObject().put("status", status), options)
        //         .onItem().transform(msg -> Response.ok(msg.body().getJsonArray("incidents").encode()).build());
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
        // DeliveryOptions options = new DeliveryOptions().addHeader("action", "incidentById");
        // return bus.<JsonObject>request("incident-service", new JsonObject().put("incidentId", incidentId), options)
        //         .onItem().transform(msg -> {
        //             JsonObject incident = msg.body().getJsonObject("incident");
        //             if (incident == null) {
        //                 return Response.status(404).build();
        //             } else {
        //                 return Response.ok(incident.encode()).build();
        //             }
        //         });
    }

    @GET
    @Path("/byname/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> incidentsByName(@PathParam("name") String name) {
        return bus.<List<IncidentDTO>>request("incidentsByName", name).onItem().transform(msg -> Response.ok(msg.body()).build());
        // DeliveryOptions options = new DeliveryOptions().addHeader("action", "incidentsByName");
        // return bus.<JsonObject>request("incident-service", new JsonObject().put("name", name), options)
        //         .onItem().transform(msg -> Response.ok(msg.body().getJsonArray("incidents").encode()).build());
    }

    @POST
    @Path("/reset")
    public Uni<Response> reset() {
        return bus.request("reset", null).onItem().transform(msg -> Response.ok().build());
        // DeliveryOptions options = new DeliveryOptions().addHeader("action", "reset");
        // return bus.<JsonObject>request("incident-service", new JsonObject(), options)
        //         .onItem().transform(msg -> Response.ok().build());
    }

}
