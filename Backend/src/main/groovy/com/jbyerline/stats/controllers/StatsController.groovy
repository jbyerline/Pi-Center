package com.jbyerline.stats.controllers

import com.jbyerline.stats.services.StatsService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RestController

@RestController
@Slf4j
class StatsController {

    @Autowired
    StatsService statsService

}
