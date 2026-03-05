package com.example.ecommercephone.service;

import com.example.ecommercephone.entity.AttributeValue;
import java.util.List;
import java.util.Optional;

public interface AttributeValueService {
    List<AttributeValue> findValuesByAttributeId(Integer attributeId);
    Optional<AttributeValue> findValueById(Integer id);
    AttributeValue createAttributeValue(AttributeValue attributeValue);
    AttributeValue updateAttributeValue(AttributeValue attributeValue);
    void deleteAttributeValue(Integer id);
}
