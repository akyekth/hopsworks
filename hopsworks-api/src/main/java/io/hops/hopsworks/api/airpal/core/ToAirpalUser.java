package io.hops.hopsworks.api.airpal.core;

import org.apache.shiro.subject.Subject;

public interface ToAirpalUser
{
    public AirpalUser toAirpalUser(Subject subject);
}
