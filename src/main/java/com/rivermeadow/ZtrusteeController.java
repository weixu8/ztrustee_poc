package com.rivermeadow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gazzang.ztrustee.Deposit;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by jinloes on 2/3/14.
 */
@RestController
@RequestMapping("/ztrustee")
public class ZtrusteeController {

    private final ZtrusteeService zTrusteeService;
    private final String encKey;

    @Autowired
    public ZtrusteeController(final ZtrusteeService ztrusteeService, final String encKey) {
        this.zTrusteeService = ztrusteeService;
        this.encKey = encKey;
    }

    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody ResponseEntity addObject(@RequestBody Map<String, Object> data) {
        String group = (String) data.get("group");
        String handle = (String) data.get("handle");
        List<String> trustees = (List<String>) data.get("trustees");
        Deposit.Builder deposit = new Deposit.Builder();
        if (StringUtils.isNotEmpty(group)) {
            deposit.group(group);
        }
        if(StringUtils.isNotEmpty(handle)) {
            deposit.handle(handle);
        }
        if(!CollectionUtils.isEmpty(trustees)) {
            deposit.trustees(trustees);
        }
        deposit.content(IOUtils.toInputStream((String) data.get("data")))
                .build();
        final String uuid = zTrusteeService.createDeposit(deposit.build());
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

    @RequestMapping(value = "/handles/{handle}", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity getHandleObject(@PathVariable("handle") final String handle) {
        return new ResponseEntity(new HashMap() {{
            put("data", zTrusteeService.getHandleData(handle));
        }}, HttpStatus.OK);
    }

    @RequestMapping(value = "/enc-key", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity getEncKey() {
        return new ResponseEntity(encKey, HttpStatus.OK);
    }
}
