package io.openliberty.sample;


import jakarta.ws.rs.core.Response;

import jakarta.ws.rs.Produces;

import java.util.Properties;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import java.nio.file.Paths;
import java.util.List;

import javax.management.ObjectName;
import javax.management.MBeanInfo;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanServer;

@Path("/fake-cert")  
public class FakeCertResource {

    @GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getFakeCert() throws IOException {
        String varName = "multiline";

        try {
            String value = readMultilineFromVariablesFile(varName);
            return value; 
        } catch (IOException e) {
            return varName + "=<error reading variables file: " + e.getMessage() + ">";
        }
    }

    /**
     * Reads FAKE_CERTIF as a full multiline value from ease-variables.properties.
     * We do NOT use java.util.Properties because it only returns the first line.
     */
    private String readMultilineFromVariablesFile(String varName) throws IOException {
        String variablesDir = System.getenv("VARIABLE_SOURCE_DIR");
        if (variablesDir == null || variablesDir.isBlank()) {
            variablesDir = "/var/run/secrets/liberty/variables";
        }

        java.nio.file.Path propsPath = Paths.get(variablesDir, "ease-variables.properties");
        if (!Files.exists(propsPath)) {
            return varName + "=<variables file not found at " + propsPath + ">";
        }

        List<String> lines = Files.readAllLines(propsPath, StandardCharsets.UTF_8);
        String prefix = varName + "=";

        StringBuilder value = new StringBuilder();
        boolean inVar = false;

        for (String line : lines) {
            if (!inVar) {
                
                if (line.startsWith(prefix)) {
                    inVar = true;
                    String first = line.substring(prefix.length()).stripLeading();
                    value.append(first);
                    value.append('\n');
                }
            } else {
                // Stop when hit the next "KEY=" line or a blank line.
                if (line.isEmpty() || line.matches("^[A-Za-z0-9_.-]+=.*")) {
                    break;
                }
                value.append(line);
                value.append('\n');
            }
        }

        if (!inVar) {
            return varName + "=<not found>";
        }

        // Trim trailing newline
        if (value.length() > 0 && value.charAt(value.length() - 1) == '\n') {
            value.setLength(value.length() - 1);
        }

        return varName + "=" + value;
    }

}
