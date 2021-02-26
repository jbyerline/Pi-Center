package com.jbyerline.stats.services

import com.jbyerline.stats.domains.ConnectionDomain
import com.jbyerline.stats.dtos.CPUStatsDTO
import com.jbyerline.stats.utils.PerformCommand
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

@Slf4j
@Service
class ConnectionService {

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
}
