package com.mmushtaq.bank.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Case implements Serializable {

    public int id;
    public String name;
    public String cnic;
    public String father_or_husband_name;
    public String gender;
    public String present_address;
    public String present_province;
    public String present_district;
    public String present_tehsil;
    public String business_name;
    public String business_address;
    public String loan_amount;
    public String loan_purpose;
    public String primary_mobile;
    public String secondary_mobile;
    public String business_detail;
    public Object is_partner_director;
    public Object is_existing_business;
    public Object is_mortage_property;
    public Object is_hire_purchase_of_vehicle;
    public Object is_pledge;
    public Object date_time_of_visit;
    public int agent_id;
    public String status;
    public List<Section> sections;
    private ArrayList<Header> header;
    private ArrayList<Documents> documents_business_attributes;
    private ArrayList<Documents> documents_residence_attributes;


    public ArrayList<Documents> getDocuments_business_attributes() {
        return documents_business_attributes;
    }

    public void setDocuments_business_attributes(ArrayList<Documents> documents_business_attributes) {
        this.documents_business_attributes = documents_business_attributes;
    }

    public ArrayList<Documents> getDocuments_residence_attributes() {
        return documents_residence_attributes;
    }

    public void setDocuments_residence_attributes(ArrayList<Documents> documents_residence_attributes) {
        this.documents_residence_attributes = documents_residence_attributes;
    }

    public ArrayList<Header> getHeader() {
        return header;
    }

    public void setHeader(ArrayList<Header> header) {
        this.header = header;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCnic() {
        return cnic;
    }

    public void setCnic(String cnic) {
        this.cnic = cnic;
    }

    public String getFather_or_husband_name() {
        return father_or_husband_name;
    }

    public void setFather_or_husband_name(String father_or_husband_name) {
        this.father_or_husband_name = father_or_husband_name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPresent_address() {
        return present_address;
    }

    public void setPresent_address(String present_address) {
        this.present_address = present_address;
    }

    public String getPresent_province() {
        return present_province;
    }

    public void setPresent_province(String present_province) {
        this.present_province = present_province;
    }

    public String getPresent_district() {
        return present_district;
    }

    public void setPresent_district(String present_district) {
        this.present_district = present_district;
    }

    public String getPresent_tehsil() {
        return present_tehsil;
    }

    public void setPresent_tehsil(String present_tehsil) {
        this.present_tehsil = present_tehsil;
    }

    public String getBusiness_name() {
        return business_name;
    }

    public void setBusiness_name(String business_name) {
        this.business_name = business_name;
    }

    public String getBusiness_address() {
        return business_address;
    }

    public void setBusiness_address(String business_address) {
        this.business_address = business_address;
    }

    public String getLoan_amount() {
        return loan_amount;
    }

    public void setLoan_amount(String loan_amount) {
        this.loan_amount = loan_amount;
    }

    public String getLoan_purpose() {
        return loan_purpose;
    }

    public void setLoan_purpose(String loan_purpose) {
        this.loan_purpose = loan_purpose;
    }

    public String getPrimary_mobile() {
        return primary_mobile;
    }

    public void setPrimary_mobile(String primary_mobile) {
        this.primary_mobile = primary_mobile;
    }

    public String getSecondary_mobile() {
        return secondary_mobile;
    }

    public void setSecondary_mobile(String secondary_mobile) {
        this.secondary_mobile = secondary_mobile;
    }

    public String getBusiness_detail() {
        return business_detail;
    }

    public void setBusiness_detail(String business_detail) {
        this.business_detail = business_detail;
    }

    public Object getIs_partner_director() {
        return is_partner_director;
    }

    public void setIs_partner_director(Object is_partner_director) {
        this.is_partner_director = is_partner_director;
    }

    public Object getIs_existing_business() {
        return is_existing_business;
    }

    public void setIs_existing_business(Object is_existing_business) {
        this.is_existing_business = is_existing_business;
    }

    public Object getIs_mortage_property() {
        return is_mortage_property;
    }

    public void setIs_mortage_property(Object is_mortage_property) {
        this.is_mortage_property = is_mortage_property;
    }

    public Object getIs_hire_purchase_of_vehicle() {
        return is_hire_purchase_of_vehicle;
    }

    public void setIs_hire_purchase_of_vehicle(Object is_hire_purchase_of_vehicle) {
        this.is_hire_purchase_of_vehicle = is_hire_purchase_of_vehicle;
    }

    public Object getIs_pledge() {
        return is_pledge;
    }

    public void setIs_pledge(Object is_pledge) {
        this.is_pledge = is_pledge;
    }

    public Object getDate_time_of_visit() {
        return date_time_of_visit;
    }

    public void setDate_time_of_visit(Object date_time_of_visit) {
        this.date_time_of_visit = date_time_of_visit;
    }

    public int getAgent_id() {
        return agent_id;
    }

    public void setAgent_id(int agent_id) {
        this.agent_id = agent_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    public Case copy() {
        Case obj=new Case();
        obj.setName(getName());
        obj.setPresent_address(getPresent_address());
        obj.setBusiness_address(getBusiness_address());
        obj.setPresent_tehsil(getPresent_tehsil());
        obj.setPresent_district(getPresent_district());
        obj.setPrimary_mobile(getPrimary_mobile());
        return obj;
    }
}
