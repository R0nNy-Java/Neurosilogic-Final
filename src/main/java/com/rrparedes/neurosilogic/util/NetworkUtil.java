package com.rrparedes.neurosilogic.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class NetworkUtil {

    /**
     * Obtiene la dirección IP local de red (IPv4) del servidor para garantizar
     * que los Códigos QR generados sean escaneables por dispositivos móviles en la red Wi-Fi/LAN,
     * incluso si la interfaz web se navega desde 'localhost' en la PC.
     */
    public static String getLanIP() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                if (iface.isLoopback() || !iface.isUp()) continue;

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr.isLoopbackAddress()) continue;
                    String hostAddress = addr.getHostAddress();
                    // Preferir direcciones IPv4 estándar de red privada (192.168.x.x, 10.x.x.x, 172.16-31.x.x)
                    if (hostAddress.matches("^(192\\.168\\.|10\\.|172\\.(1[6-9]|2[0-9]|3[0-1])\\.).*")) {
                        return hostAddress;
                    }
                }
            }
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "localhost";
        }
    }
}
