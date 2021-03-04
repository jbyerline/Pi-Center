package com.jbyerline.stats.controllers

import com.jbyerline.stats.domains.ConnectionDomain
import com.jbyerline.stats.domains.HostDomain
import com.jbyerline.stats.dtos.CPUStatsDTO
import com.jbyerline.stats.services.ConnectionService
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
    ConnectionService connectionService

    /**
     * POST - create SSH connection with given credentials
     * @requestBody JSON SSH Credentials
     * @param ConnectionDomain
     * @return CPUStatsDTO
     */
    @PostMapping(value = '/connect', produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    CPUStatsDTO connect(@RequestBody ConnectionDomain connectionDomain) {
        log.info "Attempting to connect to $connectionDomain.ipAddress"
        connectionService.createConnection(connectionDomain)
    }

    /**
     * POST - adds host to file of known hosts
     * @requestBody JSON Host Credentials
     * @param HostDomain
     * @return String host IP
     */
    @PostMapping(value = '/addKnownHost', produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    void addKnownHost(@RequestBody HostDomain hostDomain) {
        log.info "Saving Host: $hostDomain.ipAddress"
        connectionService.addKnownHost(hostDomain)
    }

    /**
     * POST - Starts session with IP
     * @requestBody JSON String IP (host)
     * @param String IP
     * @return boolean
     */
    @PostMapping(value = '/createSession', produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    boolean createSession(@RequestBody String ip) {
        log.info "Creating session for: $ip"
        connectionService.createSession(ip)
    }



}
