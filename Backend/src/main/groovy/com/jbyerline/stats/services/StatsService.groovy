package com.jbyerline.stats.services

import com.jbyerline.stats.domains.ConnectionDomain
import com.jbyerline.stats.dtos.CPUStatsDTO
import com.jbyerline.stats.dtos.ProcessDTO
import com.jbyerline.stats.dtos.StorageStatsDTO
import com.jbyerline.stats.utils.PerformCommand
import com.jbyerline.stats.utils.StringUtils
import groovy.util.logging.Slf4j
import org.json.JSONException
import org.json.JSONObject
import org.springframework.stereotype.Service
import org.json.XML


@Slf4j
@Service
class StatsService {

    PerformCommand performer = new PerformCommand()

    /**
     * Get CPU Statistics
     * @param connectionDomain
     * @return CPUStatsDTO
     */
    CPUStatsDTO getCpuStats(ConnectionDomain connectionDomain){

        // Define Commands
        List<String> commandList = ["cat /sys/class/thermal/thermal_zone*/temp", "mpstat"]

        // Perform Commands
        List<String> response = performer.executeCommands(connectionDomain, commandList)

        // Split response by whitespace
        String[] splitArr = response.get(1).split("\\s+")

        // Create response object
        CPUStatsDTO cpuStatsDTO = new CPUStatsDTO()

        // Create an empty list to store data
        List<Float> tempList = []

        // Fill response object
        String[] tempuratureList = response.get(0).split("\\n")
        for(String temp : tempuratureList){
            tempList.add((Float) (Float.parseFloat(temp) / 1000))
        }
        cpuStatsDTO.cpuTemp = tempList
        cpuStatsDTO.cpuArchitecture = splitArr[4]
        cpuStatsDTO.cpuCoreCount = Integer.parseInt(splitArr[5].substring(1))
        cpuStatsDTO.cpuUsagePercentage = Math.round((100.00f - Float.parseFloat(splitArr.last())) * 100.0) / 100.0

        return cpuStatsDTO
    }

    /**
     * Get Storage Information
     * @param connectionDomain
     * @return List<StorageStatsDTO>
     */
    List<StorageStatsDTO> getStorageStats(ConnectionDomain connectionDomain){

        // Define Commands
        List<String> commandList = ["df -h"]

        // Perform Commands
        List<String> response = performer.executeCommands(connectionDomain, commandList)

        // Split response by whitespace
        String[] splitArr = response.get(0).split("\\s+")

        // Create an empty list to store data
        List<StorageStatsDTO> storageList = []

        // Loop through response list and create objects
        for(int i = 7; i < splitArr.length; i=i+6){
            StorageStatsDTO storageStatsDTO = new StorageStatsDTO()
            storageStatsDTO.fileSystemName = splitArr[i]
            storageStatsDTO.totalSize = splitArr[i+1]
            storageStatsDTO.amountUsed = splitArr[i+2]
            storageStatsDTO.amountAvailable = splitArr[i+3]
            storageStatsDTO.percentageUsed = splitArr[i+4]
            storageStatsDTO.mountPath = splitArr[i+5]
            // Append each object to the list
            storageList.add(storageStatsDTO)
        }
        // Return the list
        return storageList
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
