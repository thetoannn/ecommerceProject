package com.example.ecommercephone.service.impl;

import com.example.ecommercephone.entity.AttributeValue;
import com.example.ecommercephone.repository.AttributeValueRepository;
import com.example.ecommercephone.service.AttributeValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AttributeValueServiceImpl implements AttributeValueService {

    @Autowired
    private AttributeValueRepository attributeValueRepository;

    @Override
    public List<AttributeValue> findValuesByAttributeId(Integer attributeId) {
        return attributeValueRepository.findByAttributeId(attributeId);
    }

    @Override
    public Optional<AttributeValue> findValueById(Integer id) {
        return attributeValueRepository.findById(id);
    }

    @Override
    @Transactional
    public AttributeValue createAttributeValue(AttributeValue attributeValue) {
        attributeValue.setId(null); // Ensure creation
        return attributeValueRepository.save(attributeValue);
    }

    @Override
    @Transactional
    public AttributeValue updateAttributeValue(AttributeValue attributeValue) {
        return attributeValueRepository.save(attributeValue);
    }

    @Override
    @Transactional
    public void deleteAttributeValue(Integer id) {
        attributeValueRepository.deleteById(id);
    }
}
