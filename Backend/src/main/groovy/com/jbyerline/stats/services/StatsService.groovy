package com.jbyerline.stats.services

import com.jbyerline.stats.domains.ConnectionDomain
import com.jbyerline.stats.dtos.CPUStatsDTO

import com.jbyerline.stats.dtos.ProcessDTO
import com.jbyerline.stats.dtos.StorageStatsDTO
import com.jbyerline.stats.utils.PerformCommand
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

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

    /**
     * Get HW Information
     * @param connectionDomain
     * @return String (Pre-formatted JSON_
     */
    String getHardwareInfo(ConnectionDomain connectionDomain){

        // Define Command
        List<String> commandList = ["sudo lshw -json"]

        // Execute Command
        List<String> response = performer.executeCommands(connectionDomain, commandList)

        // Return Response
        return response.get(0)
    }

    ProcessDTO getProcessInfo(ConnectionDomain connectionDomain){

        List<String> commandList = ["top -b -n 1 > top.txt", "cat top.txt"]

        List<String> response = performer.executeCommands(connectionDomain, commandList)

        log.info(response.get(0))

        ProcessDTO processDTO = new ProcessDTO()

        processDTO.cpuPercentageFree = 17.65

        return processDTO
    }
}
