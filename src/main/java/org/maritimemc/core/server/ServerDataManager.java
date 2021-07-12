package org.maritimemc.core.server;

import lombok.SneakyThrows;
import org.maritimemc.core.Module;
import org.maritimemc.core.command.CommandCenter;
import org.maritimemc.core.perm.PermissionManager;
import org.maritimemc.core.service.Locator;
import org.maritimemc.data.perm.Permission;
import org.maritimemc.data.perm.PermissionGroup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class ServerDataManager implements Module {

    private String proxyId;
    private String serverName;

    @SneakyThrows
    public String getProxyId() {
        if (proxyId != null) {
            return proxyId;
        }

        File f = new File("maritime_proxy_data.dat");
        if (!f.exists()) {
            return "ProxyNameNotFound";
        }

        this.proxyId = new BufferedReader(new FileReader(f)).readLine();
        return proxyId;
    }

    @SneakyThrows
    public String getServerName() {
        if (serverName != null) {
            return serverName;
        }

        File f = new File("maritime_server_data.dat");
        if (!f.exists()) {
            return "ServerNameNotFound";
        }

        this.serverName = new BufferedReader(new FileReader(f)).readLine();
        return serverName;
    }

}
