package com.jbyerline.stats.services

import com.google.gson.Gson
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ResourceLoader

import java.nio.charset.StandardCharsets;
import com.jbyerline.stats.domains.ConnectionDomain
import com.jbyerline.stats.domains.HostDomain
import com.jbyerline.stats.dtos.CPUStatsDTO
import com.jbyerline.stats.utils.PerformCommand
import com.jcraft.jsch.Session
import groovy.util.logging.Slf4j
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import org.springframework.util.FileCopyUtils


@Slf4j
@Service
class ConnectionService {

    @Autowired
    ResourceLoader resourceLoader

    CPUStatsDTO createConnection(ConnectionDomain connectionDetails){

        PerformCommand performer = new PerformCommand()

        String response = performer.executeCommands(connectionDetails.username, connectionDetails.password, connectionDetails.ipAddress, connectionDetails.port, connectionDetails.commands)

        String cpuTemp = ""

        for (int i = 0; i < response.length(); i++){
            if(response[i] == '+'){
                cpuTemp = response.substring(i, i+7)
            }
        }
        log.info(response)

        CPUStatsDTO cpuStatsDTO = new CPUStatsDTO()

        cpuStatsDTO.cpuTemp = cpuTemp

        return cpuStatsDTO
    }

    void addKnownHost(HostDomain hostDomain){

        // TODO: Save known hosts to persistent storage
    }

    boolean createSession(String ip){
        // TODO: Create session for given host
        // TODO: Somehow save session
    }

    String performCommand(Session session, List<String> Commands, String ip){
        // TODO: Perform commands and return String response
    }

    boolean terminateSession(Session session, String ip){
        // TODO: End session when desired
    }
}
