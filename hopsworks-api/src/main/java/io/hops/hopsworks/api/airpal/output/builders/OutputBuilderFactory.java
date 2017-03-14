package io.hops.hopsworks.api.airpal.output.builders;

import io.hops.hopsworks.api.airpal.Job;
import io.hops.hopsworks.api.airpal.output.HiveTablePersistentOutput;
import io.hops.hopsworks.api.airpal.output.InvalidQueryException;
import io.hops.hopsworks.api.airpal.output.PersistentJobOutput;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.net.URI;

import static java.lang.String.format;

@RequiredArgsConstructor
public class OutputBuilderFactory
{
    private final long maxFileSizeBytes;
    private final boolean isCompressedOutput;

    public JobOutputBuilder forJob(Job job)
            throws IOException, InvalidQueryException
    {
        PersistentJobOutput output = job.getOutput();
        switch (output.getType()) {
            case "csv":
                return new CsvOutputBuilder(true, job.getUuid(), maxFileSizeBytes, isCompressedOutput);
            case "hive":
                HiveTablePersistentOutput hiveOutput = (HiveTablePersistentOutput) output;
                URI location = output.getLocation();
                if (location == null) {
                    throw new InvalidQueryException(format("Invalid table name '%s'", hiveOutput.getTmpTableName()));
                }
                return new HiveTableOutputBuilder(hiveOutput.getDestinationSchema(), hiveOutput.getTmpTableName());
            default:
                throw new IllegalArgumentException(format("OutputBuilder for type %s not found", output.getType()));
        }
    }
}
