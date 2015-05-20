package org.bahmni.module;

import ca.uhn.hl7v2.model.v251.message.ORM_O01;

public class Main {
    public static void main(String args[]){
        ORM_O01 message = new ORM_O01();
        message.initQuickstart();

    }
}
