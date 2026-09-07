package com.rrparedes.neurosilogic.util;

import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class NetworkUtil {

    /**
     * Obtiene la dirección IP local de red (IPv4) del servidor para garantizar que los Códigos QR
     * generados sean escaneables por dispositivos móviles en la red Wi-Fi/LAN, incluso si la
     * interfaz web se navega desde 'localhost' en la PC.
     *
     * Método principal: abre un socket UDP "conectado" hacia una IP pública (no se envía ningún
     * paquete real — UDP "connect" solo resuelve la ruta localmente) y lee qué IP local eligió el
     * propio sistema operativo para esa ruta. Esto identifica de forma confiable el adaptador de
     * red REAL (el que realmente sale a Internet/LAN), sin importar cuántos adaptadores virtuales
     * tenga instalados la máquina (VirtualBox Host-Only, Hyper-V, WSL, Docker, VPN, Bluetooth PAN,
     * etc.) — antes, al recorrer todas las interfaces y devolver la primera con IP privada, era
     * cuestión de suerte cuál aparecía primero, y en máquinas de desarrollo con varias de esas
     * interfaces virtuales, frecuentemente devolvía una IP que no correspondía a la red real.
     */
    public static String getLanIP() {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            InetAddress local = socket.getLocalAddress();
            String ip = local != null ? local.getHostAddress() : null;
            if (ip != null && !ip.isBlank() && !ip.startsWith("0.") && !local.isAnyLocalAddress()) {
                return ip;
            }
        } catch (Exception ignored) {
            // Sin salida a Internet (ej. red completamente aislada / sin gateway) — se sigue
            // con el respaldo de abajo en vez de devolver directamente "localhost".
        }

        // Respaldo: recorre las interfaces evitando explícitamente los adaptadores virtuales
        // conocidos por nombre, para no repetir el problema original si el método de arriba falla.
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                if (iface.isLoopback() || !iface.isUp() || iface.isVirtual()) continue;

                String nombre = (safe(iface.getDisplayName()) + " " + safe(iface.getName())).toLowerCase();
                if (nombre.contains("virtualbox") || nombre.contains("vmware") || nombre.contains("hyper-v")
                        || nombre.contains("virtual") || nombre.contains("docker") || nombre.contains("wsl")
                        || nombre.contains("vethernet") || nombre.contains("bluetooth") || nombre.contains("tap")
                        || nombre.contains("npcap") || nombre.contains("vpn") || nombre.contains("loopback")) {
                    continue;
                }

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr.isLoopbackAddress() || !(addr instanceof Inet4Address)) continue;
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

    private static String safe(String s) {
        return s != null ? s : "";
    }
}
