package com.jbyerline.stats.services

import com.jbyerline.stats.domains.CommandDomain
import com.jbyerline.stats.domains.ConnectionDomain
import com.jbyerline.stats.dtos.CPUStatsDTO

import com.jbyerline.stats.dtos.TopProcess
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
        println(splitArr1)

        // Create response object
        ProcessDTO processDTO = new ProcessDTO()

        processDTO.numOfUsers = Integer.parseInt(splitArr1[5])
        println(processDTO.numOfUsers) //good to go

        processDTO.numOfTasks = Integer.parseInt(splitArr1[13])
        println(processDTO.numOfTasks) //good to go

        processDTO.cpuPercentageUsed = Float.parseFloat(splitArr1[24]) //user space time
        println(processDTO.cpuPercentageUsed) //good to go

        processDTO.cpuPercentageFree = Float.parseFloat(splitArr1[30]) //idle time
        println(processDTO.cpuPercentageFree) //good to go

        processDTO.memoryTotal = Float.parseFloat(splitArr1[43])
        println(processDTO.memoryTotal) //good to go

        processDTO.memoryFree = Float.parseFloat(splitArr1[45])
        println(processDTO.memoryFree) //good to go


        // Create an empty list to store process list
        List<TopProcess> processList = []

//        println(splitArr1[74])
//        println(splitArr1[75])
//        println(splitArr1[82])
//        println(splitArr1[83])
//        println(splitArr1[84])
//        println(splitArr1[85])

        // Loop through each process list
//        for(int i= 74; i<splitArr1.length; i+12){
//            // Create and append objects with each process info
//            TopProcess topProcess = new TopProcess()
//            topProcess.PID = Integer.parseInt(splitArr1[i+0])
//            topProcess.user = splitArr1[i+1]
//            topProcess.cpuUsagePercent = Float.parseFloat(splitArr1[i+8])
//            topProcess.memUsagePercent = Float.parseFloat(splitArr1[i+9])
//            topProcess.processUpTime = splitArr1[i+10]
//            topProcess.processCommandName = splitArr1[i+11]
//
//            // Append each object to the list
//            processList.add(topProcess)
//        }
        //processDTO.ProcessList = processList

        return processDTO
    }
}