package com.jbyerline.stats.utils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.jbyerline.stats.domains.ConnectionDomain;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class PerformCommand {

    public ArrayList<String> executeCommands(ConnectionDomain connectionDomain, List<String> command) throws Exception {

        String username = connectionDomain.getUsername();
        String password = connectionDomain.getPassword();
        String host = connectionDomain.getIpAddress();
        int port = connectionDomain.getPort();

        // Declare local variables
        Session session = null;
        ChannelExec channel = null;
        ArrayList<String>  response = new ArrayList<>();

        // Try to perform command
        try {
            // Create new SSH session
            session = new JSch().getSession(username, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            // Iterate over String Command List
            for (int i = 0; i < command.size(); i++) {
                // Execute each command
                channel = (ChannelExec) session.openChannel("exec");
                // Enable for SUDO
                //channel.setPty(true);
                channel.setCommand(command.get(i));
                // Set up ByteArray to collect results
                ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
                channel.setOutputStream(responseStream);
                channel.connect();
                // Wait 100mills for command to be performed
                while (channel.isConnected()) {
                    Thread.sleep(100);
                }
                // Append command output to string
                response.add(responseStream.toString());
            }
            // Return response data
            return response;
        }
        // Otherwise disconnect
        finally {
            if (session != null) {
                session.disconnect();
            }
            if (channel != null) {
                channel.disconnect();
            }
        }
    }
}
