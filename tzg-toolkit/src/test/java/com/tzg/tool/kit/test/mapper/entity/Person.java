package com.tzg.tool.kit.test.mapper.entity;

import com.tzg.tool.kit.mapper.XstreamMapper;

public class Person {
    private String firstname;

    private String lastname;

    public Person(String firstname, String lastname) {
        this.firstname=firstname;
        this.lastname=lastname;
    }

    public static void main(String[] args) {
        Person joe = new Person("Joe", "Walnes");
//        joe.setPhone(new PhoneNumber(123, "1234-456"));
//        joe.setFax(new PhoneNumber(123, "9999-999"));
        String xml = XstreamMapper.toXml(joe);
        
        System.out.println(xml);
        Object o = XstreamMapper.toBean(xml, Person.class);
        System.out.println(o);
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public PhoneNumber getPhone() {
        return phone;
    }

    public void setPhone(PhoneNumber phone) {
        this.phone = phone;
    }

    public PhoneNumber getFax() {
        return fax;
    }

    public void setFax(PhoneNumber fax) {
        this.fax = fax;
    }

    private PhoneNumber phone;

    private PhoneNumber fax;
}
