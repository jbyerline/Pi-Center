package com.jbyerline.stats.services

import com.jbyerline.stats.domains.ConnectionDomain
import com.jbyerline.stats.dtos.CPUStatsDTO
import com.jbyerline.stats.dtos.ProcessDTO
import com.jbyerline.stats.dtos.StorageStatsDTO
import com.jbyerline.stats.utils.PerformCommand
import groovy.util.logging.Slf4j
import org.json.JSONException
import org.json.JSONObject
import org.springframework.stereotype.Service
import org.json.XML;


@Slf4j
@Service
class StatsService {

    PerformCommand performer = new PerformCommand()

    CPUStatsDTO getCpuStats(ConnectionDomain connectionDomain){

        List<String> commandList = ["sensors", "mpstat"]

        String response = performer.executeCommands(connectionDomain, commandList)

        // TODO: Parse and return response.

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

    StorageStatsDTO getStorageStats(ConnectionDomain connectionDomain){

        List<String> commandList = ["df", "lshw", "lshw -class cpu -short"]

        String response = performer.executeCommands(connectionDomain, commandList)

        // TODO: Parse and return response.

        log.info(response)

        StorageStatsDTO storageStatsDTO = new StorageStatsDTO()

        storageStatsDTO.numberOfDrives = 5

        return storageStatsDTO
    }

    String getHardwareInfo(ConnectionDomain connectionDomain){

        List<String> commandList = ["sudo lshw -xml -class memory"]

        // TODO: Figure out best combo of lshw command to return

        String response = performer.executeCommands(connectionDomain, commandList)

        // TODO: Parse and return response.
        String jsonPrettyPrintString = ""

        try {
            JSONObject xmlJSONObj = XML.toJSONObject(response)
            jsonPrettyPrintString = xmlJSONObj.toString(4)
        } catch (JSONException je) {
            log.error("Could not parse XML")
        }

        log.info(jsonPrettyPrintString)

        return jsonPrettyPrintString
    }

    ProcessDTO getProcessInfo(ConnectionDomain connectionDomain){

        List<String> commandList = ["top -b -n 1 > top.txt", "cat top.txt"]

        String response = performer.executeCommands(connectionDomain, commandList)

        log.info(response)

        ProcessDTO processDTO = new ProcessDTO()

        processDTO.cpuPercentageFree = 17.65

        return processDTO
    }
}
