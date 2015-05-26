package org.bahmni.module;

import org.apache.commons.cli.ParseException;
import org.dcm4che.hl7.MLLPConnection;
import org.dcm4che.net.Connection;
import org.dcm4che.net.IncompatibleConnectionException;
import org.dcm4che.util.StringUtils;
import org.dcm4che2.net.Device;
import org.dcm4che2.net.NetworkApplicationEntity;
import org.dcm4che2.net.NetworkConnection;
import org.dcm4che2.net.NewThreadExecutor;
import org.dcm4che2.net.service.VerificationService;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

public class Modality {

    protected static NetworkConnection networkConnection;
    private static final String keyStoreURL = "pacskeystore";
    private static final char[] password = new char[0];

    //MLLP
    private final Connection conn = new Connection();
    private final Connection remote = new Connection();
    private Socket sock;
    private MLLPConnection mllp;

    public static void main(String args[]) throws IOException, GeneralSecurityException, ParseException {
        Modality modality = new Modality();
        //modality.connect("modality1");
        modality.connectThroughMLLPConnection();
        try {
            modality.open();
            //   main.sendFiles(cl.getArgList());
            modality.sendHL7Message("hls7blob message");
            //log.info("Order sent successfully.");
            //result=1;
        } catch (IncompatibleConnectionException e) {
            e.printStackTrace();
        } finally {
            modality.close();
        }
    }

    public void connect(String modalityName) throws GeneralSecurityException, IOException {
        Device device = new Device(modalityName);
        device.setNetworkApplicationEntity(getNetworkApplicationEntity());
        device.setNetworkConnection(getNetworkConnection());

        connectTo(device);

        NewThreadExecutor executor = new NewThreadExecutor(modalityName);
        device.startListening(executor);
        device.stopListening();
    }

    private static NetworkApplicationEntity getNetworkApplicationEntity() {
        NetworkApplicationEntity applicationEntity = new NetworkApplicationEntity();
        applicationEntity.setNetworkConnection(networkConnection);
        applicationEntity.setAssociationAcceptor(true);
        applicationEntity.register(new VerificationService());
        return applicationEntity;
    }

    private static NetworkConnection getNetworkConnection() {
        if (networkConnection == null)
            networkConnection = new NetworkConnection();
        return networkConnection;
    }

    public void connectTo(Device device) throws GeneralSecurityException, IOException {
        KeyStore keyStore = loadKeyStore(keyStoreURL, password);
        device.initTLS(keyStore, password);
    }

    private static KeyStore loadKeyStore(String url, char[] password) throws GeneralSecurityException, IOException {
        KeyStore key = KeyStore.getInstance("JKS");
        InputStream in = openFileOrURL(url);
        try {
            key.load(in, password);
        } finally {
            in.close();
        }
        return key;
    }

    private static InputStream openFileOrURL(String url) throws IOException {
        try {
            return new URL(url).openStream();
        } catch (MalformedURLException e) {
            return new FileInputStream(url);
        }
    }

    //MLLP
    public void connectThroughMLLPConnection() throws ParseException {
        configureConnect(remote, "10.0.0.25:8080");
        conn.setHostname("localhost");
        //CLIUtils.configure(main.conn, cl); //TODO
        remote.setTlsProtocols(conn.getTlsProtocols());
        remote.setTlsCipherSuites(conn.getTlsCipherSuites());
    }

    private static void configureConnect(Connection conn, String url)
            throws ParseException {
        String[] hostPort = StringUtils.split(url, ':');
        conn.setHostname(hostPort[0]);
        conn.setPort(Integer.parseInt(hostPort[1]));
    }

    public void open() throws IOException, IncompatibleConnectionException, GeneralSecurityException {
        sock = conn.connect(remote);
        sock.setSoTimeout(conn.getResponseTimeout());
        mllp = new MLLPConnection(sock);
    }

    public void sendHL7Message(String hl7blob) throws IOException {
        mllp.writeMessage(hl7blob.getBytes());
        if (mllp.readMessage() == null)
            throw new IOException("Connection closed by receiver");
    }

    public void close() {
        conn.close(sock);
    }

}
