package org.bahmni.module.dicom;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.io.DicomInputStream;

import java.io.File;
import java.io.IOException;

public class Instance {

    private File file;

    public Instance(File file) {
        this.file = file;
    }

    public DicomObject getDICOM() {
        DicomObject dcmObj;
        DicomInputStream din = null;
        try {
            din = new DicomInputStream(new File("image.dcm"));
            return din.readDicomObject();
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        finally {
            try {
                din.close();
            }
            catch (IOException ignore) {
            }
        }

    }
}
