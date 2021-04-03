package org.labmonkeys.cajun_navy.incident.api;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import org.eclipse.microprofile.health.Readiness;

@Liveness
@Readiness
@ApplicationScoped
public class HealthProbe implements HealthCheck {


    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.named("Health check").up().build();
    }
}
