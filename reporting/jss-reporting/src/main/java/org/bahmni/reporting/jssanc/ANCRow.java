package org.bahmni.reporting.jssanc;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.bahmni.csv.CSVEntity;
import org.bahmni.csv.CSVHeader;

public class ANCRow extends CSVEntity {
    @CSVHeader(name = "culuster")
    public String cluster;

    @CSVHeader(name = "villagename")
    public String villagename;
    @CSVHeader(name = "postoffice")
    public String postoffice;
    @CSVHeader(name = "personid")
    public String personid;
    @CSVHeader(name = "tehsilid")
    public String tehsilid;
    @CSVHeader(name = "villageid")
    public String villageid;
    @CSVHeader(name = "familynum")
    public String familynum;
    @CSVHeader(name = "membernum")
    public String membernum;
    @CSVHeader(name = "dolcb")
    public String dolcb;
    @CSVHeader(name = "mem. Name")
    public String memName;
    @CSVHeader(name = "husbandname")
    public String husbandname;
    @CSVHeader(name = "preg_age")
    public String preg_age;
    @CSVHeader(name = "height")
    public String height;
    @CSVHeader(name = "gravida")
    public String gravida;
    @CSVHeader(name = "parity")
    public String parity;
    @CSVHeader(name = "abortion")
    public String abortion;
    @CSVHeader(name = "livebirth")
    public String livebirth;
    @CSVHeader(name = "lmp")
    public String lmp;
    @CSVHeader(name = "edd")
    public String edd;
    @CSVHeader(name = "TT1")
    public String TT1;
    @CSVHeader(name = "TT2")
    public String TT2;
    @CSVHeader(name = "prevpreg_specialcare1")
    public String prevpreg_specialcare1;
    @CSVHeader(name = "prevpreg_specialcare2")
    public String prevpreg_specialcare2;
    @CSVHeader(name = "prevpreg_specialcare3")
    public String prevpreg_specialcare3;
    @CSVHeader(name = "curpreg_specialcare1")
    public String curpreg_specialcare1;
    @CSVHeader(name = "curpreg_specialcare2")
    public String curpreg_specialcare2;
    @CSVHeader(name = "curpreg_specialcare3")
    public String curpreg_specialcare3;
    @CSVHeader(name = "ultrasound")
    public String ultrasound;
    @CSVHeader(name = "ultrasound_report")
    public String ultrasound_report;
    @CSVHeader(name = "checkup_date")
    public String checkup_date;
    @CSVHeader(name = "weight")
    public String weight;
    @CSVHeader(name = "urine_alb")
    public String urine_alb;
    @CSVHeader(name = "oedema")
    public String oedema;
    @CSVHeader(name = "hb")
    public String hb;
    @CSVHeader(name = "H.B. gram")
    public String hbGram;
    @CSVHeader(name = "bp")
    public String bp;
    @CSVHeader(name = "bp_syst")
    public String bp_syst;
    @CSVHeader(name = "bp_diast")
    public String bp_diast;
    @CSVHeader(name = "gestation")
    public String gestation;
    @CSVHeader(name = "fundal_ht")
    public String fundal_ht;
    @CSVHeader(name = "position_child")
    public String position_child;
    @CSVHeader(name = "heartbeats_child")
    public String heartbeats_child;
    @CSVHeader(name = "chloroquin")
    public String chloroquin;
    @CSVHeader(name = "delivery_date")
    public String delivery_date;
    @CSVHeader(name = "del_type")
    public String del_type;
    @CSVHeader(name = "del_kit")
    public String del_kit;
    @CSVHeader(name = "del_place")
    public String del_place;
    @CSVHeader(name = "conduct_by")
    public String conduct_by;
    @CSVHeader(name = "sex")
    public String sex;
    @CSVHeader(name = "birth_status")
    public String birth_status;
    @CSVHeader(name = "child_wt")
    public String child_wt;
    @CSVHeader(name = "pnc")
    public String pnc;
    @CSVHeader(name = "pnc_date")
    public String pnc_date;
    @CSVHeader(name = "family_plan")
    public String family_plan;
    @CSVHeader(name = "bednet")
    public String bednet;
    @CSVHeader(name = "del done")
    public String delDone;
    @CSVHeader(name = "sickle sample")
    public String sickleSample;
    @CSVHeader(name = "sickle report")
    public String sickleReport;
    @CSVHeader(name = "hiv sample")
    public String hivSample;
    @CSVHeader(name = "hiv report")
    public String hivReport;
    @CSVHeader(name = "hep b sample")
    public String hepBSample;
    @CSVHeader(name = "Hep b report")
    public String HepBReport;
    @CSVHeader(name = "garbhpat")
    public String garbhpat;
    @CSVHeader(name = "Iron")
    public String Iron;
    @CSVHeader(name = "Opd/anc ")
    public String opdAnc;
    @CSVHeader(name = "Smart card")
    public String smartCard;
    @CSVHeader(name = "upyog ho raha hai /nahi")
    public String isUsed;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
