package io.openliberty.sample.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Utility to read Liberty variables from the mounted variables file:
 * /var/run/secrets/liberty/variables/ease-variables.properties
 *
 * This is how FAKE_CERTIF (multiline) is provided to the container.
 */
public final class LibertyVariables {

    // Path to the variables file mounted by Kubernetes / Liberty operator
    private static final Path VARS_FILE =
            Paths.get("/var/run/secrets/liberty/variables/ease-variables.properties");

    private static final Properties PROPS = new Properties();
    private static volatile boolean loaded = false;

    private LibertyVariables() {
        // utility class – no instances
    }

    private static synchronized void loadIfNeeded() throws IOException {
        if (loaded) {
            return;
        }

        if (!Files.exists(VARS_FILE)) {
            throw new IOException("Variables file not found: " + VARS_FILE);
        }

        try (FileInputStream in = new FileInputStream(VARS_FILE.toFile())) {
            PROPS.load(in);
        }

        loaded = true;
    }

    /**
     * Generic accessor for any key in ease-variables.properties.
     */
    public static String getVariable(String key) throws IOException {
        loadIfNeeded();
        return PROPS.getProperty(key);
    }

    /**
     * Convenience accessor specifically for the multiline FAKE_CERTIF variable.
     */
    public static String getFakeCertif() throws IOException {
        return getVariable("FAKE_CERTIF");
    }
}
