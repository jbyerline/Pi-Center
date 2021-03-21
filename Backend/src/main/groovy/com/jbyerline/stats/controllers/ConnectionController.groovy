package com.jbyerline.stats.controllers

import com.jbyerline.stats.domains.ConnectionDomain

import com.jbyerline.stats.dtos.CPUStatsDTO
import com.jbyerline.stats.dtos.ProcessDTO

import com.jbyerline.stats.dtos.StorageStatsDTO
import com.jbyerline.stats.services.StatsService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@Slf4j
class ConnectionController {

    @Autowired
    StatsService statsService

    /**
     * POST - Get CPU stats for given host
     * @requestBody JSON SSH Credentials
     * @param ConnectionDomain
     * @return CPUStatsDTO
     */
    @PostMapping(value = '/stats/cpu', produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    CPUStatsDTO cpu(@RequestBody ConnectionDomain connectionDomain) {
        log.info "Getting CPU Stats from $connectionDomain.ipAddress"
        statsService.getCpuStats(connectionDomain)
    }

    /**
     * POST - Get Storage stats for given host
     * @requestBody JSON SSH Credentials
     * @param ConnectionDomain
     * @return StorageStatsDTO
     */
    @PostMapping(value = '/stats/storage', produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    List<StorageStatsDTO> storage(@RequestBody ConnectionDomain connectionDomain) {
        log.info "Getting Storage Stats from $connectionDomain.ipAddress"
        statsService.getStorageStats(connectionDomain)
    }

    /**
     * POST - Get Hw info for given host
     * @requestBody JSON SSH Credentials
     * @param ConnectionDomain
     * @return String
     */
    @PostMapping(value = '/stats/hardwareInfo', produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    String hardware(@RequestBody ConnectionDomain connectionDomain) {
        log.info "Getting HW info from $connectionDomain.ipAddress"
        statsService.getHardwareInfo(connectionDomain)
    }

    /**
     * POST - Get Process info for given host
     * @requestBody JSON SSH Credentials
     * @param ConnectionDomain
     * @return RAMStatsDTO
     */
    @PostMapping(value = '/stats/process', produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    ProcessDTO process(@RequestBody ConnectionDomain connectionDomain) {
        log.info "Getting Process info from $connectionDomain.ipAddress"
        statsService.getProcessInfo(connectionDomain)
    }

    // TODO: Create call to execute any command and return unparsed response. (No Sudo Commands)

}
