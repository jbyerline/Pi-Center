package com.jbyerline.stats.controllers

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
class StatsController {

    @Autowired
    StatsService statsService

    /**
     * POST - Starts session with IP
     * @requestBody JSON String IP (host)
     * @param String IP
     * @return boolean
     */
    @PostMapping(value = '/CPU', produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    boolean getCpuStats(@RequestBody String ip) {
        log.info "Creating session for: $ip"
        statsService.getCpuStats(ip)
    }

}
