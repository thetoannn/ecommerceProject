package com.example.ecommercephone.service;

import com.example.ecommercephone.entity.Attribute;
import com.example.ecommercephone.entity.AttributeValue;

import java.util.List;
import java.util.Optional;

public interface AttributeService {
    List<Attribute> findAllAttributes();
    Optional<Attribute> findAttributeById(Integer id);
    Attribute createAttribute(Attribute attribute);
    Attribute updateAttribute(Attribute attribute);
    void deleteAttribute(Integer id);
}

