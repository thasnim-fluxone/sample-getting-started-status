package io.openliberty.sample.config;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Logs a safe preview of the FAKE_CERTIF value at application startup.
 *
 * This helps verify that the multiline value from environment.yaml
 * has flowed through Helm → Secret → mounted variables file and is
 * readable by the application.
 */
@ApplicationScoped
public class FakeCertifLogger {

    private static final Logger LOG = Logger.getLogger(FakeCertifLogger.class.getName());

    @PostConstruct
    void logFakeCertifPreview() {
        try {
            String value = LibertyVariables.getFakeCertif();

            LOG.info("==== FAKE_CERTIF debug preview ====");

            if (value == null) {
                LOG.info("FAKE_CERTIF is null (not found in ease-variables.properties)");
            } else {
                // Avoid flooding logs: show only first 120 chars
                String preview = value.length() > 120
                        ? value.substring(0, 120) + "..."
                        : value;

                LOG.info("FAKE_CERTIF (first 120 chars):");
                LOG.info(preview);
            }

            LOG.info("==== end FAKE_CERTIF debug preview ====");

        } catch (IOException e) {
            LOG.log(
                    Level.WARNING,
                    "Failed to read FAKE_CERTIF from /var/run/secrets/liberty/variables/ease-variables.properties",
                    e
            );
        }
    }
}
