package org.bahmni.module.dicom;

import org.dcm4che2.net.Association;
import org.dcm4che2.net.ConfigurationException;
import org.dcm4che2.net.Device;
import org.dcm4che2.net.NetworkApplicationEntity;
import org.dcm4che2.net.NetworkConnection;
import org.dcm4che2.net.NewThreadExecutor;
import org.dcm4che2.net.UserIdentity;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.Executor;

public class DicomReciever {
    public static final String PASSWORD = "abc";
    public static final String USERNAME = "user";

    private final Executor executor;

    private final NetworkApplicationEntity remoteAE = new NetworkApplicationEntity();

    private final NetworkConnection remoteConn = new NetworkConnection();

    private final Device device;

    private final NetworkApplicationEntity ae = new NetworkApplicationEntity();

    private final NetworkConnection conn = new NetworkConnection();

    private Association assoc;

    public DicomReciever(String name) {
        device = new Device(name);
        executor = new NewThreadExecutor(name);
        remoteAE.setInstalled(true);
        remoteAE.setAssociationAcceptor(true);
        remoteAE.setNetworkConnection(new NetworkConnection[]{remoteConn});

        device.setNetworkApplicationEntity(ae);
        device.setNetworkConnection(conn);
        ae.setNetworkConnection(conn);
        ae.setAssociationInitiator(true);
        ae.setAssociationAcceptor(true);
        ae.setAETitle(name);
    }

    public static void main(String[] args) throws Exception {
        int port = 104;
        String host = "10.0.0.25";
        DicomReciever dicomReciever = new DicomReciever("DICOMRCV");
        dicomReciever.setRemoteHost(host);
        dicomReciever.setRemotePort(port);
        dicomReciever.setLocalPort(8080);
        dicomReciever.setLocalHost("localhost");

        UserIdentity userId = new UserIdentity.UsernamePasscode(USERNAME, PASSWORD.toCharArray());
        userId.setPositiveResponseRequested(true);
        dicomReciever.setUserIdentity(userId);

        dicomReciever.open();
        System.out.println("connection established");

        dicomReciever.close();
        System.out.println("connection closed");

    }

    public final void setRemoteHost(String hostname) {
        remoteConn.setHostname(hostname);
    }

    public final void setRemotePort(int port) {
        remoteConn.setPort(port);
    }

    public final void setLocalHost(String hostname) {
        conn.setHostname(hostname);
    }

    public final void setLocalPort(int port) {
        conn.setPort(port);
    }

    public final void setUserIdentity(UserIdentity userIdentity) {
        ae.setUserIdentity(userIdentity);
    }

    public void open() throws IOException, ConfigurationException, InterruptedException {
        assoc = ae.connect(remoteAE, executor);
    }

    public void close() throws InterruptedException {
        assoc.release(true);
    }

    public static void connectToPACS(int portNo, String hostAddress) {

        try {
            InetAddress addr;
            Socket sock = new Socket(hostAddress, portNo);
            addr = sock.getInetAddress();
            System.out.println("Connected to " + addr);
            sock.close();
        } catch (java.io.IOException e) {
            System.out.println("Can't connect to " + hostAddress);
            System.out.println(e);
        }
    }
}
