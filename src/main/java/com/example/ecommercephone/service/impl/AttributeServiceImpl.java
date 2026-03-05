package com.example.ecommercephone.service.impl;

import com.example.ecommercephone.entity.Attribute;
import com.example.ecommercephone.repository.AttributeRepository;
import com.example.ecommercephone.service.AttributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AttributeServiceImpl implements AttributeService {
    @Autowired
    private AttributeRepository attributeRepository;

    @Override
    public List<Attribute> findAllAttributes() {
        return attributeRepository.findAll();
    }

    @Override
    public Optional<Attribute> findAttributeById(Integer id) {
        return attributeRepository.findById(id);
    }

    @Override
    @Transactional
    public Attribute createAttribute(Attribute attribute) {
        attribute.setId(null); // Ensure creation
        return attributeRepository.save(attribute);
    }

    @Override
    @Transactional
    public Attribute updateAttribute(Attribute attribute) {
        return attributeRepository.save(attribute);
    }

    @Override
    @Transactional
    public void deleteAttribute(Integer id) {
        attributeRepository.deleteById(id);
    }
}

