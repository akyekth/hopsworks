package io.hops.hopsworks.api.airpal.output;

public class InvalidQueryException
        extends Exception
{
    public InvalidQueryException(String message)
    {
        super(message);
    }
}
