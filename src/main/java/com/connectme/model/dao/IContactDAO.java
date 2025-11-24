package com.connectme.model.dao;

import com.connectme.model.entities.Contact;
import com.connectme.model.enums.ContactType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface IContactDAO {
    boolean create(Contact contact);

    boolean update(Contact contact);

    boolean delete(int contactId);

    Contact findById(int id);

    List<Contact> findAll();

    List<Contact> findByType(ContactType type);

    List<Contact> search(String query);

    int countAll();

    List<Contact> findPaginated(int page, int pageSize);

    Contact mapResultSetToContact(ResultSet rs) throws SQLException;

    boolean isValidContact(Contact contact);
}
