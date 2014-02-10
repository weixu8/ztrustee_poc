package com.rivermeadow;

import java.io.File;
import java.util.HashMap;

import com.gazzang.ztrustee.ActivationRequest;
import com.gazzang.ztrustee.ClientConnection;
import com.gazzang.ztrustee.ClientFactory;
import com.gazzang.ztrustee.ClientInfo;
import com.gazzang.ztrustee.Deposit;
import com.gazzang.ztrustee.DepositInfo;
import com.gazzang.ztrustee.DepositRelease;
import com.gazzang.ztrustee.KeyLength;
import com.gazzang.ztrustee.Request;
import com.gazzang.ztrustee.ServerInfo;
import com.gazzang.ztrustee.ZtrusteeException;
import com.gazzang.ztrustee.impl.ClientFactoryImpl;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ZtrusteeService {
    private static final ClientFactory CLIENT_FACTORY = new ClientFactoryImpl();
    private static final ServerInfo SERVER_INFO = new ServerInfo.Builder()
            .hostname("ztdemo.gazzang.net")
            .build();
    private static final File ZTRUSTEE_KEY_DIR = new File("/Users/jinloes/.ztrustee");
    private static final ActivationRequest activationRequest = new ActivationRequest.Builder()
            .org("rivermeadow_poc")
            .auth("S00l31Q6ql3U/BKTpuE3qQ==")
            .build();
    // Can only have one connection per application
    private final ClientConnection conn;

    public ZtrusteeService() {
        try {
            ClientInfo clientInfo;
            if(!CLIENT_FACTORY.clientExists(ZTRUSTEE_KEY_DIR)) {
                clientInfo = CLIENT_FACTORY.createNewClient(KeyLength.L_2048, ZTRUSTEE_KEY_DIR);
            } else {
                clientInfo = CLIENT_FACTORY.openClient(ZTRUSTEE_KEY_DIR);
            }
            conn = CLIENT_FACTORY.connect(clientInfo, SERVER_INFO);
            if(!conn.isRegistered()) {
                conn.registerAndActivate(activationRequest);
                conn.getClientInfo().saveConfig(ZTRUSTEE_KEY_DIR);
            }
        } catch (ZtrusteeException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public String putData(String data) {
        try {
            DepositInfo deposit = conn.put(new Deposit.Builder()
                    .content(IOUtils.toInputStream(data))
                    .build());
            return deposit.getUuid();
        } catch (ZtrusteeException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public String getData(String uuid) {
        try {
            final DepositRelease release = conn.get(new Request.Builder()
                    .uuid(uuid)
                    .build());
            return IOUtils.toString(release.getContents());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
