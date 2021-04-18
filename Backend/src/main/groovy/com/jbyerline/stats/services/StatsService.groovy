package com.jbyerline.stats.services

import com.jbyerline.stats.domains.CommandDomain
import com.jbyerline.stats.domains.ConnectionDomain
import com.jbyerline.stats.dtos.CPUStatsDTO

import com.jbyerline.stats.dtos.TopProcess
import com.jbyerline.stats.dtos.ProcessDTO
import com.jbyerline.stats.dtos.StorageStatsDTO
import com.jbyerline.stats.dtos.TopProcess
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
     * @return String (Pre-formatted JSON)
     */
    String getHardwareInfo(ConnectionDomain connectionDomain){

        // Define Command
        List<String> commandList = ["sudo lshw -json"]

        // Execute Command
        List<String> response = performer.executeCommands(connectionDomain, commandList)

        // Return Response
        return response.get(0)
    }

    /**
     *Execute any command
     * @param connectionDomain
     * @return Unparsed String
     */
    List<String> executeCommand(CommandDomain commandDomain){
        ConnectionDomain cd = new ConnectionDomain()
        cd.username = commandDomain.username
        cd.ipAddress = commandDomain.ipAddress
        cd.password = commandDomain.password
        cd.port = commandDomain.port

        List<String> response = performer.executeCommands(cd, commandDomain.commands)

        return response
    }

    /**
     * Get Process Information
     * @param connectionDomain
     * @return ProcessDTO
     */
    ProcessDTO getProcessInfo(ConnectionDomain connectionDomain){

        List<String> commandList = ["top -b -n 1 > top.txt", "cat top.txt"]

        List<String> response = performer.executeCommands(connectionDomain, commandList)

        // Split response lines by whitespace
        String[] splitArr1 = response.get(1).split("\\s+")

        log.info(response.get(1))

        // Create response object
        ProcessDTO processDTO = new ProcessDTO()

        processDTO.numOfUsers = Integer.parseInt(splitArr1[4])
        processDTO.numOfTasks = Integer.parseInt(splitArr1[15])
        processDTO.cpuPercentageUsed = Float.parseFloat(splitArr1[26]) //user space time
        processDTO.cpuPercentageFree = Float.parseFloat(splitArr1[32]) //idle time
        processDTO.memoryTotal = Float.parseFloat(splitArr1[45])
        processDTO.memoryFree = Float.parseFloat(splitArr1[47])
        processDTO.memoryUsed = Float.parseFloat(splitArr1[49])

        // Create an empty list to store process list
        List<TopProcess> processList = []

        // Loop through each process list
        for(int i= 76; i<splitArr1.length; i=i+12){
            //special case: command is 2 words (usually just 1)
            if(splitArr1[i] == "Notif+"){
                i+=1
            }
            // Create and append objects with each process info
            TopProcess topProcess = new TopProcess()
            topProcess.PID = Integer.parseInt(splitArr1[i])
            topProcess.user = splitArr1[i+1]
            topProcess.cpuUsagePercent = Float.parseFloat(splitArr1[i+8])
            topProcess.memUsagePercent = Float.parseFloat(splitArr1[i+9])
            topProcess.processUpTime = splitArr1[i+10]
            topProcess.processCommandName = splitArr1[i+11]

            // Append each object to the list
            processList.add(topProcess)
        }
        processDTO.ProcessList = processList

        return processDTO
    }
}