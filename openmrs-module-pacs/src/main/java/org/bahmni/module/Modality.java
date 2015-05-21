package org.bahmni.module;

import org.dcm4che2.net.Device;
import org.dcm4che2.net.NetworkApplicationEntity;
import org.dcm4che2.net.NetworkConnection;
import org.dcm4che2.net.NewThreadExecutor;
import org.dcm4che2.net.service.VerificationService;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

public class Modality {

    protected static NetworkConnection networkConnection;
    private static final String keyStoreURL = "pacskeystore";
    private static final char[] password = new char[0];

    public static void main(String args[]) throws IOException, GeneralSecurityException {
        Modality modality = new Modality();
        modality.connect("modality1");
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
}
