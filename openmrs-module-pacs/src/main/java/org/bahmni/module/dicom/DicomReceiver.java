package org.bahmni.module.dicom;

import org.dcm4che2.data.BasicDicomObject;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.UID;
import org.dcm4che2.data.UIDDictionary;
import org.dcm4che2.data.VR;
import org.dcm4che2.io.DicomOutputStream;
import org.dcm4che2.net.Association;
import org.dcm4che2.net.CommandUtils;
import org.dcm4che2.net.ConfigurationException;
import org.dcm4che2.net.Device;
import org.dcm4che2.net.DimseRSP;
import org.dcm4che2.net.ExtQueryTransferCapability;
import org.dcm4che2.net.NetworkApplicationEntity;
import org.dcm4che2.net.NetworkConnection;
import org.dcm4che2.net.NewThreadExecutor;
import org.dcm4che2.net.NoPresentationContextException;
import org.dcm4che2.net.TransferCapability;
import org.dcm4che2.net.UserIdentity;
import org.dcm4che2.tool.dcmqr.DcmQR;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

public class DicomReceiver {
    private static DcmQR dcmqr;

    private final Executor executor;
    private final Device device;

    private final NetworkApplicationEntity remoteAE = new NetworkApplicationEntity();
    private final NetworkConnection remoteConn = new NetworkConnection();
    private final NetworkApplicationEntity ae = new NetworkApplicationEntity();
    private final NetworkConnection conn = new NetworkConnection();

    private QueryRetrieveLevel qrlevel = QueryRetrieveLevel.STUDY;
    private DicomObject keys = new BasicDicomObject();

    private Association assoc;
    private int priority = 0;
    private int cancelAfter = Integer.MAX_VALUE;
    private List<String> privateFind = new ArrayList<String>();

    public DicomReceiver(String hostAETitle, String remoteAETitle) {
        device = new Device(hostAETitle);
        executor = new NewThreadExecutor(hostAETitle);
        remoteAE.setInstalled(true);
        remoteAE.setAssociationAcceptor(true);
        remoteAE.setNetworkConnection(new NetworkConnection[]{remoteConn});
        remoteAE.setAETitle(remoteAETitle);

        device.setNetworkApplicationEntity(ae);
        device.setNetworkConnection(conn);
        ae.setNetworkConnection(conn);
        ae.setAssociationInitiator(true);
        ae.setAssociationAcceptor(true);
        ae.setAETitle(hostAETitle);
    }

    public static void main(String[] args) throws Exception {
        String remoteHost = "10.0.0.25";
        int remotePort = 104;
        String username = "cshsvc";
        String password = "Service1";
        String remoteAETitle = "ImageSuite";
        UserIdentity userId = new UserIdentity.UsernamePasscode(username, password.toCharArray());
        userId.setPositiveResponseRequested(true);


//
//        String remoteHost = "192.168.33.10";
//        int remotePort = 11112;
//        String username = "admin";
//        String password = "1234";
//        String remoteAETitle = "DCM4CHEE";
//

        dcmqr = new DcmQR("Bahmni");
        dcmqr.setUserIdentity(userId);
        dcmqr.setCalledAET(remoteAETitle, true);
        dcmqr.setRemoteHost(remoteHost);
        dcmqr.setRemotePort(remotePort);
        dcmqr.getKeys();
        dcmqr.setDateTimeMatching(true);
        dcmqr.setCFind(true);
        dcmqr.setCGet(true);
        dcmqr.setAssociationReaperPeriod(30000);


        dcmqr.setQueryLevel(DcmQR.QueryRetrieveLevel.IMAGE);
        dcmqr.addMatchingKey(Tag.toTagPath("PatientName"), "Vicens^DICOM");
        dcmqr.configureTransferCapability(true);

        dcmqr.start();
        System.out.println("Started connection");

        dcmqr.open();
        System.out.println("Opened connection");

        List<DicomObject> result = dcmqr.query();
        System.out.println("Query done");

        dcmqr.get(result);
        System.out.println("List Size : " + result.size());
        displayObjectDetails(result);
        for (DicomObject dco : result) {
            byte[] data = toByteArray(dco);

            // here how can get dicom image ??


        }

        try {
            if (dcmqr != null) {
                dcmqr.stop();
                dcmqr.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // toByteArray Method here
    private static byte[] toByteArray(DicomObject obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream(baos);
        DicomOutputStream dos = new DicomOutputStream(bos);
        dos.writeDicomFile(obj);
        dos.close();
        byte[] data = baos.toByteArray();
        return data;
    }

    // display object details
    private static void displayObjectDetails(List resultimg) {

        DicomObject obj = new BasicDicomObject();
        List<String> listimg = new ArrayList<String>();
        for (int i = 0; i < resultimg.size(); i++) {
            obj = (DicomObject) resultimg.get(i);

            System.out.println("InstanceNumber : " + obj.getString(Tag.InstanceNumber, VR.IS));
            System.out.println("ImageType : " + obj.getString(Tag.ImageType, VR.CS));
            System.out.println("ImageID : " + obj.getString(Tag.ImageID, VR.SH));
            System.out.println("RetrieveAETitle : " + obj.getString(Tag.RetrieveAETitle, VR.AE));
            System.out.println("SOPInstanceUID : " + obj.getString(Tag.SOPInstanceUID, VR.UI));

            System.out.println("PatientName : " + obj.getString(Tag.PatientName, VR.PN));
            System.out.println("PatientBirthDate : " + obj.getString(Tag.PatientBirthDate, VR.DA));
            System.out.println("PatientSex : " + obj.getString(Tag.PatientSex, VR.CS));
            System.out.println("ReferringPhysicianName : " + obj.getString(Tag.ReferringPhysicianName, VR.PN));
            System.out.println("StudyDescription : " + obj.getString(Tag.StudyDescription, VR.LO));
            System.out.println("SeriesDescription : " + obj.getString(Tag.SeriesDescription, VR.LO));

        }
    }

    public void otherMain() throws InterruptedException, ConfigurationException, IOException {
        String remoteHost = "10.0.0.25";
        int remotePort = 104;
        String username = "admin";
        String password = "123456";
        String remoteAETitle = "ImageSuite";

//        if (args.length > 0) {
//            remoteHost = args[0];
//            if (args.length >= 2)
//                remotePort = Integer.parseInt(args[1]);
//            if (args.length >= 3)
//                username = args[2];
//            if (args.length >= 4)
//                password = args[3];
//            if (args.length >= 5)
//                remoteAETitle = args[4];
//        }
        System.out.println(remoteHost + ":" + remotePort + ":" + username + ":" + password);

        DicomReceiver dicomReceiver = new DicomReceiver("someAETitle", remoteAETitle);
        dicomReceiver.setRemoteHost(remoteHost);
        dicomReceiver.setRemotePort(remotePort);
        dicomReceiver.setLocalPort(8086);
        dicomReceiver.setLocalHost("localhost");

        UserIdentity userId = new UserIdentity.UsernamePasscode(username, password.toCharArray());
        userId.setPositiveResponseRequested(true);
        dicomReceiver.setUserIdentity(userId);

        // Start & Open connection
        dicomReceiver.open();
        System.out.println("connection established");

        dicomReceiver.query();


        // END
        dicomReceiver.close();
        System.out.println("connection closed");
    }

    public List<DicomObject> query() throws IOException, InterruptedException {
        privateFind.add(UID.PrivateStudyRootQueryRetrieveInformationModelFIND);
        privateFind.add(UID.PrivateBlockedStudyRootQueryRetrieveInformationModelFIND);
        privateFind.add(UID.PrivateVirtualMultiframeStudyRootQueryRetrieveInformationModelFIND);

        privateFind.add("1.2.840.10008.5.1.4.1.1.1");


        List<DicomObject> result = new ArrayList<DicomObject>();
        TransferCapability tc = selectFindTransferCapability();
        String cuid = tc.getSopClass();
        String tsuid = selectTransferSyntax(tc);
        if (tc.getExtInfoBoolean(ExtQueryTransferCapability.RELATIONAL_QUERIES) || containsUpperLevelUIDs(cuid)) {
            System.out.printf(String.format("Send Query Request using {}:\n{}", UIDDictionary.getDictionary().prompt(cuid), keys));
            DimseRSP rsp = assoc.cfind(cuid, priority, keys, tsuid, cancelAfter);
            while (rsp.next()) {
                DicomObject cmd = rsp.getCommand();
                if (CommandUtils.isPending(cmd)) {
                    DicomObject data = rsp.getDataset();
                    result.add(data);
                    System.out.println(String.format("Query Response #{}:\n{}", Integer.valueOf(result.size()), data));
                }
            }
        } else {
            List<DicomObject> upperLevelUIDs = queryUpperLevelUIDs(cuid, tsuid);
            List<DimseRSP> rspList = new ArrayList<DimseRSP>(upperLevelUIDs.size());
            for (int i = 0, n = upperLevelUIDs.size(); i < n; i++) {
                upperLevelUIDs.get(i).copyTo(keys);
                System.out.println(String.format("Send Query Request #{}/{} using {}:\n{}",
                        new Object[]{
                                Integer.valueOf(i + 1),
                                Integer.valueOf(n),
                                UIDDictionary.getDictionary().prompt(cuid),
                                keys
                        }));
                rspList.add(assoc.cfind(cuid, priority, keys, tsuid, cancelAfter));
            }
            for (int i = 0, n = rspList.size(); i < n; i++) {
                DimseRSP rsp = rspList.get(i);
                for (int j = 0; rsp.next(); ++j) {
                    DicomObject cmd = rsp.getCommand();
                    if (CommandUtils.isPending(cmd)) {
                        DicomObject data = rsp.getDataset();
                        result.add(data);
                        System.out.println(String.format("Query Response #{} for Query Request #{}/{}:\n{}",
                                new Object[]{Integer.valueOf(j + 1), Integer.valueOf(i + 1), Integer.valueOf(n), data}));
                    }
                }
            }
        }
        return result;
    }

    private List<DicomObject> queryUpperLevelUIDs(String cuid, String tsuid)
            throws IOException, InterruptedException {
        List<DicomObject> keylist = new ArrayList<DicomObject>();
        if (Arrays.asList(PATIENT_LEVEL_FIND_CUID).contains(cuid)) {
            queryPatientIDs(cuid, tsuid, keylist);
            if (qrlevel == QueryRetrieveLevel.STUDY) {
                return keylist;
            }
            keylist = queryStudyOrSeriesIUIDs(cuid, tsuid, keylist,
                    Tag.StudyInstanceUID, STUDY_MATCHING_KEYS, QueryRetrieveLevel.STUDY);
        } else {
            keylist.add(new BasicDicomObject());
            keylist = queryStudyOrSeriesIUIDs(cuid, tsuid, keylist,
                    Tag.StudyInstanceUID, PATIENT_STUDY_MATCHING_KEYS, QueryRetrieveLevel.STUDY);
        }
        if (qrlevel == QueryRetrieveLevel.IMAGE) {
            keylist = queryStudyOrSeriesIUIDs(cuid, tsuid, keylist,
                    Tag.SeriesInstanceUID, SERIES_MATCHING_KEYS, QueryRetrieveLevel.SERIES);
        }
        return keylist;
    }

    private List<DicomObject> queryStudyOrSeriesIUIDs(String cuid, String tsuid,
                                                      List<DicomObject> upperLevelIDs, int uidTag, int[] matchingKeys,
                                                      QueryRetrieveLevel qrLevel) throws IOException,
            InterruptedException {
        List<DicomObject> keylist = new ArrayList<DicomObject>();
        String uid = keys.getString(uidTag);
        for (DicomObject upperLevelID : upperLevelIDs) {
            if (uid != null) {
                DicomObject suidKey = new BasicDicomObject();
                upperLevelID.copyTo(suidKey);
                suidKey.putString(uidTag, VR.UI, uid);
                keylist.add(suidKey);
            } else {
                DicomObject keys2 = new BasicDicomObject();
                keys.subSet(matchingKeys).copyTo(keys2);
                upperLevelID.copyTo(keys2);
                keys2.putNull(uidTag, VR.UI);
                keys2.putString(Tag.QueryRetrieveLevel, VR.CS, qrLevel.getCode());
                System.out.println(String.format("Send Query Request using {}:\n{}",
                        UIDDictionary.getDictionary().prompt(cuid), keys2));
                DimseRSP rsp = assoc.cfind(cuid, priority, keys2,
                        tsuid, Integer.MAX_VALUE);
                for (int i = 0; rsp.next(); ++i) {
                    DicomObject cmd = rsp.getCommand();
                    if (CommandUtils.isPending(cmd)) {
                        DicomObject data = rsp.getDataset();
                        System.out.println(String.format("Query Response #{}:\n{}", Integer.valueOf(i + 1), data));
                        DicomObject suidKey = new BasicDicomObject();
                        upperLevelID.copyTo(suidKey);
                        suidKey.putString(uidTag, VR.UI, data.getString(uidTag));
                        keylist.add(suidKey);
                    }
                }
            }
        }
        return keylist;
    }

    private void queryPatientIDs(String cuid, String tsuid,
                                 List<DicomObject> keylist) throws IOException, InterruptedException {
        String patID = keys.getString(Tag.PatientID);
        String issuer = keys.getString(Tag.IssuerOfPatientID);
        if (patID != null) {
            DicomObject patIdKeys = new BasicDicomObject();
            patIdKeys.putString(Tag.PatientID, VR.LO, patID);
            if (issuer != null) {
                patIdKeys.putString(Tag.IssuerOfPatientID, VR.LO, issuer);
            }
            keylist.add(patIdKeys);
        } else {
            DicomObject patLevelQuery = new BasicDicomObject();
            keys.subSet(PATIENT_MATCHING_KEYS).copyTo(patLevelQuery);
            patLevelQuery.putNull(Tag.PatientID, VR.LO);
            patLevelQuery.putNull(Tag.IssuerOfPatientID, VR.LO);
            patLevelQuery.putString(Tag.QueryRetrieveLevel, VR.CS, "PATIENT");
            System.out.println(String.format("Send Query Request using {}:\n{}", UIDDictionary.getDictionary().prompt(cuid), patLevelQuery));
            DimseRSP rsp = assoc.cfind(cuid, priority, patLevelQuery, tsuid,
                    Integer.MAX_VALUE);
            for (int i = 0; rsp.next(); ++i) {
                DicomObject cmd = rsp.getCommand();
                if (CommandUtils.isPending(cmd)) {
                    DicomObject data = rsp.getDataset();
                    System.out.println(String.format("Query Response #{}:\n{}", Integer.valueOf(i + 1), data));
                    DicomObject patIdKeys = new BasicDicomObject();
                    patIdKeys.putString(Tag.PatientID, VR.LO,
                            data.getString(Tag.PatientID));
                    issuer = keys.getString(Tag.IssuerOfPatientID);
                    if (issuer != null) {
                        patIdKeys.putString(Tag.IssuerOfPatientID, VR.LO,
                                issuer);
                    }
                    keylist.add(patIdKeys);
                }
            }
        }
    }

    @SuppressWarnings("fallthrough")
    private boolean containsUpperLevelUIDs(String cuid) {
        switch (qrlevel) {
            case IMAGE:
                if (!keys.containsValue(Tag.SeriesInstanceUID)) {
                    return false;
                }
                // fall through
            case SERIES:
                if (!keys.containsValue(Tag.StudyInstanceUID)) {
                    return false;
                }
                // fall through
            case STUDY:
                if (Arrays.asList(PATIENT_LEVEL_FIND_CUID).contains(cuid)
                        && !keys.containsValue(Tag.PatientID)) {
                    return false;
                }
                // fall through
            case PATIENT:
                // fall through
        }
        return true;
    }

    public TransferCapability selectFindTransferCapability()
            throws NoPresentationContextException {
        TransferCapability tc;
        if ((tc = selectTransferCapability(privateFind)) != null)
            return tc;
        if ((tc = selectTransferCapability(qrlevel.getFindClassUids())) != null)
            return tc;
        throw new NoPresentationContextException(UIDDictionary.getDictionary()
                .prompt(qrlevel.getFindClassUids()[0])
                + " not supported by " + remoteAE.getAETitle());
    }

    public TransferCapability selectTransferCapability(List<String> cuid) {
        TransferCapability tc;
        for (int i = 0, n = cuid.size(); i < n; i++) {
            tc = assoc.getTransferCapabilityAsSCU(cuid.get(i));
            if (tc != null)
                return tc;
        }
        return null;
    }

    public TransferCapability selectTransferCapability(String[] cuid) {
        TransferCapability tc;
        for (int i = 0; i < cuid.length; i++) {
            tc = assoc.getTransferCapabilityAsSCU(cuid[i]);
            if (tc != null)
                return tc;
        }
        return null;
    }

    public String selectTransferSyntax(TransferCapability tc) {
        String[] tcuids = tc.getTransferSyntax();
        if (Arrays.asList(tcuids).indexOf(UID.DeflatedExplicitVRLittleEndian) != -1)
            return UID.DeflatedExplicitVRLittleEndian;
        return tcuids[0];
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
        if (conn.isListening()) {
            conn.bind(executor);
            System.out.println("Start Server listening on port " + conn.getPort());
        }
        assoc = ae.connect(remoteAE, executor);
    }

    public void close() throws InterruptedException {
        assoc.release(true);
    }

    public static enum QueryRetrieveLevel {
        PATIENT("PATIENT", PATIENT_RETURN_KEYS, PATIENT_LEVEL_FIND_CUID,
                PATIENT_LEVEL_GET_CUID, PATIENT_LEVEL_MOVE_CUID),
        STUDY("STUDY", STUDY_RETURN_KEYS, STUDY_LEVEL_FIND_CUID,
                STUDY_LEVEL_GET_CUID, STUDY_LEVEL_MOVE_CUID),
        SERIES("SERIES", SERIES_RETURN_KEYS, SERIES_LEVEL_FIND_CUID,
                SERIES_LEVEL_GET_CUID, SERIES_LEVEL_MOVE_CUID),
        IMAGE("IMAGE", INSTANCE_RETURN_KEYS, SERIES_LEVEL_FIND_CUID,
                SERIES_LEVEL_GET_CUID, SERIES_LEVEL_MOVE_CUID);

        private final String code;
        private final int[] returnKeys;
        private final String[] findClassUids;
        private final String[] getClassUids;
        private final String[] moveClassUids;

        private QueryRetrieveLevel(String code, int[] returnKeys,
                                   String[] findClassUids, String[] getClassUids,
                                   String[] moveClassUids) {
            this.code = code;
            this.returnKeys = returnKeys;
            this.findClassUids = findClassUids;
            this.getClassUids = getClassUids;
            this.moveClassUids = moveClassUids;
        }

        public String getCode() {
            return code;
        }

        public int[] getReturnKeys() {
            return returnKeys;
        }

        public String[] getFindClassUids() {
            return findClassUids;
        }

        public String[] getGetClassUids() {
            return getClassUids;
        }

        public String[] getMoveClassUids() {
            return moveClassUids;
        }
    }


    private static final String[] PATIENT_LEVEL_FIND_CUID = {
            UID.PatientRootQueryRetrieveInformationModelFIND,
            UID.PatientStudyOnlyQueryRetrieveInformationModelFINDRetired};

    private static final String[] STUDY_LEVEL_FIND_CUID = {
            UID.StudyRootQueryRetrieveInformationModelFIND,
            UID.PatientRootQueryRetrieveInformationModelFIND,
            UID.PatientStudyOnlyQueryRetrieveInformationModelFINDRetired};

    private static final String[] SERIES_LEVEL_FIND_CUID = {
            UID.StudyRootQueryRetrieveInformationModelFIND,
            UID.PatientRootQueryRetrieveInformationModelFIND,};

    private static final String[] PATIENT_LEVEL_GET_CUID = {
            UID.PatientRootQueryRetrieveInformationModelGET,
            UID.PatientStudyOnlyQueryRetrieveInformationModelGETRetired};

    private static final String[] STUDY_LEVEL_GET_CUID = {
            UID.StudyRootQueryRetrieveInformationModelGET,
            UID.PatientRootQueryRetrieveInformationModelGET,
            UID.PatientStudyOnlyQueryRetrieveInformationModelGETRetired};

    private static final String[] SERIES_LEVEL_GET_CUID = {
            UID.StudyRootQueryRetrieveInformationModelGET,
            UID.PatientRootQueryRetrieveInformationModelGET};

    private static final String[] PATIENT_LEVEL_MOVE_CUID = {
            UID.PatientRootQueryRetrieveInformationModelMOVE,
            UID.PatientStudyOnlyQueryRetrieveInformationModelMOVERetired};

    private static final String[] STUDY_LEVEL_MOVE_CUID = {
            UID.StudyRootQueryRetrieveInformationModelMOVE,
            UID.PatientRootQueryRetrieveInformationModelMOVE,
            UID.PatientStudyOnlyQueryRetrieveInformationModelMOVERetired};

    private static final String[] SERIES_LEVEL_MOVE_CUID = {
            UID.StudyRootQueryRetrieveInformationModelMOVE,
            UID.PatientRootQueryRetrieveInformationModelMOVE};

    private static final int[] PATIENT_RETURN_KEYS = {
            Tag.PatientName,
            Tag.PatientID,
            Tag.PatientBirthDate,
            Tag.PatientSex,
            Tag.NumberOfPatientRelatedStudies,
            Tag.NumberOfPatientRelatedSeries,
            Tag.NumberOfPatientRelatedInstances};

    private static final int[] PATIENT_MATCHING_KEYS = {
            Tag.PatientName,
            Tag.PatientID,
            Tag.IssuerOfPatientID,
            Tag.PatientBirthDate,
            Tag.PatientSex};

    private static final int[] STUDY_RETURN_KEYS = {
            Tag.StudyDate,
            Tag.StudyTime,
            Tag.AccessionNumber,
            Tag.StudyID,
            Tag.StudyInstanceUID,
            Tag.NumberOfStudyRelatedSeries,
            Tag.NumberOfStudyRelatedInstances};

    private static final int[] STUDY_MATCHING_KEYS = {
            Tag.StudyDate,
            Tag.StudyTime,
            Tag.AccessionNumber,
            Tag.ModalitiesInStudy,
            Tag.ReferringPhysicianName,
            Tag.StudyID,
            Tag.StudyInstanceUID};

    private static final int[] PATIENT_STUDY_MATCHING_KEYS = {
            Tag.StudyDate,
            Tag.StudyTime,
            Tag.AccessionNumber,
            Tag.ModalitiesInStudy,
            Tag.ReferringPhysicianName,
            Tag.PatientName,
            Tag.PatientID,
            Tag.IssuerOfPatientID,
            Tag.PatientBirthDate,
            Tag.PatientSex,
            Tag.StudyID,
            Tag.StudyInstanceUID};

    private static final int[] SERIES_RETURN_KEYS = {
            Tag.Modality,
            Tag.SeriesNumber,
            Tag.SeriesInstanceUID,
            Tag.NumberOfSeriesRelatedInstances};

    private static final int[] SERIES_MATCHING_KEYS = {
            Tag.Modality,
            Tag.SeriesNumber,
            Tag.SeriesInstanceUID,
            Tag.RequestAttributesSequence
    };

    private static final int[] INSTANCE_RETURN_KEYS = {
            Tag.InstanceNumber,
            Tag.SOPClassUID,
            Tag.SOPInstanceUID,};

    private static final int[] MOVE_KEYS = {
            Tag.QueryRetrieveLevel,
            Tag.PatientID,
            Tag.StudyInstanceUID,
            Tag.SeriesInstanceUID,
            Tag.SOPInstanceUID,};

    private static final String[] IVRLE_TS = {
            UID.ImplicitVRLittleEndian};

    private static final String[] NATIVE_LE_TS = {
            UID.ExplicitVRLittleEndian,
            UID.ImplicitVRLittleEndian};

    private static final String[] NATIVE_BE_TS = {
            UID.ExplicitVRBigEndian,
            UID.ImplicitVRLittleEndian};

    private static final String[] DEFLATED_TS = {
            UID.DeflatedExplicitVRLittleEndian,
            UID.ExplicitVRLittleEndian,
            UID.ImplicitVRLittleEndian};

    private static final String[] NOPX_TS = {
            UID.NoPixelData,
            UID.ExplicitVRLittleEndian,
            UID.ImplicitVRLittleEndian};

    private static final String[] NOPXDEFL_TS = {
            UID.NoPixelDataDeflate,
            UID.NoPixelData,
            UID.ExplicitVRLittleEndian,
            UID.ImplicitVRLittleEndian};

    private static final String[] JPLL_TS = {
            UID.JPEGLossless,
            UID.JPEGLosslessNonHierarchical14,
            UID.JPEGLSLossless,
            UID.JPEG2000LosslessOnly,
            UID.ExplicitVRLittleEndian,
            UID.ImplicitVRLittleEndian};

    private static final String[] JPLY_TS = {
            UID.JPEGBaseline1,
            UID.JPEGExtended24,
            UID.JPEGLSLossyNearLossless,
            UID.JPEG2000,
            UID.ExplicitVRLittleEndian,
            UID.ImplicitVRLittleEndian};

    private static final String[] MPEG2_TS = {UID.MPEG2};

    private static final String[] DEF_TS = {
            UID.JPEGLossless,
            UID.JPEGLosslessNonHierarchical14,
            UID.JPEGLSLossless,
            UID.JPEGLSLossyNearLossless,
            UID.JPEG2000LosslessOnly,
            UID.JPEG2000,
            UID.JPEGBaseline1,
            UID.JPEGExtended24,
            UID.MPEG2,
            UID.DeflatedExplicitVRLittleEndian,
            UID.ExplicitVRBigEndian,
            UID.ExplicitVRLittleEndian,
            UID.ImplicitVRLittleEndian};

    private static enum TS {
        IVLE(IVRLE_TS),
        LE(NATIVE_LE_TS),
        BE(NATIVE_BE_TS),
        DEFL(DEFLATED_TS),
        JPLL(JPLL_TS),
        JPLY(JPLY_TS),
        MPEG2(MPEG2_TS),
        NOPX(NOPX_TS),
        NOPXD(NOPXDEFL_TS);

        final String[] uids;

        TS(String[] uids) {
            this.uids = uids;
        }
    }

    private static enum CUID {
        CR(UID.ComputedRadiographyImageStorage),
        CT(UID.CTImageStorage),
        MR(UID.MRImageStorage),
        US(UID.UltrasoundImageStorage),
        NM(UID.NuclearMedicineImageStorage),
        PET(UID.PositronEmissionTomographyImageStorage),
        SC(UID.SecondaryCaptureImageStorage),
        XA(UID.XRayAngiographicImageStorage),
        XRF(UID.XRayRadiofluoroscopicImageStorage),
        DX(UID.DigitalXRayImageStorageForPresentation),
        MG(UID.DigitalMammographyXRayImageStorageForPresentation),
        PR(UID.GrayscaleSoftcopyPresentationStateStorageSOPClass),
        KO(UID.KeyObjectSelectionDocumentStorage),
        SR(UID.BasicTextSRStorage);

        final String uid;

        CUID(String uid) {
            this.uid = uid;
        }

    }

}
