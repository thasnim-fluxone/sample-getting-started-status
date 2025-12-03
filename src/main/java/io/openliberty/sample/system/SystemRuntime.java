/*******************************************************************************
 * Copyright (c) 2022 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/

package io.openliberty.sample.system;

import java.lang.management.ManagementFactory;

import jakarta.enterprise.context.RequestScoped;

//import jakarta.ws.rs.GET;
import jakarta.ws.rs.core.Response;
//import jakarta.ws.rs.core.MediaType;
//import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
//import java.io.FileInputStream;
//import java.io.IOException;
import java.util.Properties;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
// import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.management.ObjectName;
import javax.management.MBeanInfo;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanServer;

@RequestScoped
@Path("/multiline")
public class SystemRuntime {
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getFakeCert() throws IOException {
        String varName = "multiline";

        try {
            String value = readMultilineFromVariablesFile(varName);
            return value; // already "FAKE_CERTIF=<full-multiline>"
        } catch (IOException e) {
            return varName + "=<error reading variables file: " + e.getMessage() + ">";
        }
    }

    /**
     * Reads FAKE_CERTIF as a full multiline value from ease-variables.properties.
     * We do NOT use java.util.Properties because it only returns the first line.
     */
    private String readMultilineFromVariablesFile(String varName) throws IOException {
        // Same directory Liberty uses to mount variables (you saw this earlier)
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
                // Look for the FAKE_CERTIF=... line
                if (line.startsWith(prefix)) {
                    inVar = true;
                    // Everything after '=' on this line is the first line of the value
                    String first = line.substring(prefix.length()).stripLeading();
                    value.append(first);
                    value.append('\n');
                }
            } else {
                // We’re already inside the value block.
                // Stop when we hit the next "KEY=" line or a blank line.
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
	// public Response getRuntime() {
	// 	String libertyVersion = getServerVersion();
	// 	return Response.ok(libertyVersion).build();
	// }

	// String getServerVersion() {
 //        String version = null;
 //        try {
 //            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
 //            ObjectName objName = new ObjectName("WebSphere:feature=kernel,name=ServerInfo");
	// 		MBeanInfo beanInfo = mbs.getMBeanInfo(objName);

	// 		for (MBeanAttributeInfo attr : beanInfo.getAttributes()) {
	// 			if (attr.getName().equals("LibertyVersion")) {
	// 				version = String.valueOf(mbs.getAttribute(objName, attr.getName()));
	// 				break;
	// 			}
	// 		}
 //        } catch (Exception ex) {
 //            System.out.println("Unable to retrieve server version.");
 //        }
 //        return version;
 //    }
	
}
