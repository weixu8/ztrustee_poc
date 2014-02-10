package com.rivermeadow;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by jinloes on 2/3/14.
 */
@RestController
@RequestMapping("/ztrustee")
public class ZtrusteeController {

    private final ZtrusteeService zTrusteeService;

    @Autowired
    public ZtrusteeController(final ZtrusteeService ztrusteeService) {
        this.zTrusteeService = ztrusteeService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody ResponseEntity addObject(@RequestBody String data) {
        final String uuid = zTrusteeService.putData(data);
        return new ResponseEntity(
                new HashMap() {{
                    put("uuid", uuid);
                }},
                HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{objectId}", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity getObject(@PathVariable("objectId") final String id) {
        return new ResponseEntity(new HashMap() {{
            put("data", zTrusteeService.getData(id));
        }}, HttpStatus.OK);
    }
}
