package com.example.gurpreetsingh.project.framework.data;

import com.example.gurpreetsingh.project.domain.Contact;
import com.lacronicus.easydatastorelib.BooleanEntry;
import com.lacronicus.easydatastorelib.FloatEntry;
import com.lacronicus.easydatastorelib.IntEntry;
import com.lacronicus.easydatastorelib.ObjectEntry;
import com.lacronicus.easydatastorelib.Preference;
import com.lacronicus.easydatastorelib.StringEntry;

import java.util.List;

/**
 * Created by Gurpreet on 15-01-2017.
 */

public interface DataStore {

    @Preference("string")
    StringEntry dataString();

    @Preference("float")
    FloatEntry dataFloat();

    @Preference("int")
    IntEntry dataInt();

    @Preference("boolean")
    BooleanEntry dataBoolean();

    @Preference("object")
    ObjectEntry<Contact> emergencyContact();

    @Preference("list")
    ObjectEntry<List<Contact>> emergencyContactList();
}
