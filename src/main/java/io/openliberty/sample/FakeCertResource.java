package io.openliberty.sample;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Path("/fake-cert")   // full path: /api/fake-cert
public class FakeCertResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getFakeCert() throws IOException {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(
                "/var/run/secrets/liberty/variables/ease-variables.properties")) {
            props.load(in);
        }

        // adjust key name if it's "multiline" instead of "FAKE_CERTIF"
        String value = props.getProperty("FAKE_CERTIF");

        if (value == null) {
            return "FAKE_CERTIF not found in ease-variables.properties";
        }

        return value;
    }
}
