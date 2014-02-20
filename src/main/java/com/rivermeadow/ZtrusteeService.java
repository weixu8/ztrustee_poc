package com.rivermeadow;

import java.io.File;
import java.util.List;

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
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class ZtrusteeService {
    private static final String ZTRUSTEE_SERVER = "ztdemo.gazzang.net";
    private static final ClientFactory CLIENT_FACTORY = new ClientFactoryImpl();
    private static final File ZTRUSTEE_KEY_DIR = new File("/Users/jinloes/.ztrustee");
    private static final ActivationRequest ACTIVATION_REQUEST = new ActivationRequest.Builder()
            .org("rivermeadow_poc")
            .auth("S00l31Q6ql3U/BKTpuE3qQ==")
            .build();
    private static final ActivationRequest GROUP_ACTIVATION_REQUEST = new ActivationRequest.Builder()
            .org("rivermeadow_poc")
            .auth("S00l31Q6ql3U/BKTpuE3qQ==")
            .group("rm-group")
            .justification("Testing out justifications")
            .contact("jonathan@rivermeadow.com")
            .notified("jonathan@rivermeadow.com")
            .build();
    // Can only have one connection per application
    private final ClientConnection conn;
    private final ServerInfo serverInfo;

    public ZtrusteeService() {
        try {
            ClientInfo clientInfo;
            if(!CLIENT_FACTORY.clientExists(ZTRUSTEE_KEY_DIR)) {
                clientInfo = CLIENT_FACTORY.createNewClient(KeyLength.L_2048, ZTRUSTEE_KEY_DIR);
            } else {
                clientInfo = CLIENT_FACTORY.openClient(ZTRUSTEE_KEY_DIR);
            }
            if (clientInfo.hasServerInfo(ZTRUSTEE_SERVER)) {
                // Load the saved server info
                serverInfo = clientInfo.getServerInfo(ZTRUSTEE_SERVER);
            } else {
                // Create server info
                serverInfo = new ServerInfo.Builder()
                        .hostname(ZTRUSTEE_SERVER)
                        .build();
            }
            conn = CLIENT_FACTORY.connect(clientInfo, serverInfo);
            if(!conn.isRegistered()) {
                conn.registerAndActivate(GROUP_ACTIVATION_REQUEST);
                conn.getClientInfo().saveConfig(ZTRUSTEE_KEY_DIR);
            }

        } catch (ZtrusteeException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public String createDeposit(Deposit deposit) {
        try {
            DepositInfo info = conn.put(deposit);
            return info.getUuid();
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

    public String getHandleData(String handle) {
        try {
            final DepositRelease release = conn.get(new Request.Builder()
                    .handle(handle)
                    .build());
            return IOUtils.toString(release.getContents());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public String getData(String uuid, String group) {
        try {
            final DepositRelease release = conn.get(new Request.Builder()
                    .uuid(uuid)
                    .group(group)
                    .build());
            return IOUtils.toString(release.getContents());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
