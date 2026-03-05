package com.example.ecommercephone.service.impl;

import com.example.ecommercephone.dto.request.AttributeSelection;
import com.example.ecommercephone.dto.request.ProductAttributeRequest;
import com.example.ecommercephone.entity.Attribute;
import com.example.ecommercephone.entity.AttributeValue;
import com.example.ecommercephone.entity.Product;
import com.example.ecommercephone.entity.ProductAttribute;
import com.example.ecommercephone.repository.AttributeRepository;
import com.example.ecommercephone.repository.AttributeValueRepository;
import com.example.ecommercephone.repository.ProductAttributeRepository;
import com.example.ecommercephone.service.ProductAttributeService;
import com.example.ecommercephone.service.AttributeService;
import com.example.ecommercephone.service.AttributeValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductAttributeServiceImpl implements ProductAttributeService {

    @Autowired
    private ProductAttributeRepository productAttributeRepository;

    @Autowired
    private AttributeValueService attributeValueService;

    @Autowired
    private AttributeRepository attributeRepository;

    @Autowired
    private AttributeValueRepository attributeValueRepository;


    @Override
    @Transactional
    public void updateProductAttributes(Product product, ProductAttributeRequest request) {
        List<AttributeSelection> selections = request.getAttributes();
        List<ProductAttribute> currentAttributes = productAttributeRepository.findByProductId(product.getId());

        Map<Integer, Integer> attributesMap = (selections == null) ? Map.of() : selections.stream()
                .collect(Collectors.toMap(
                        AttributeSelection::getAttributeId,
                        AttributeSelection::getValueId,
                        (v1, v2) -> v1
                ));

        // Cách tiếp cận tối ưu: lấy hết các giá trị mới nếu chúng khác giá trị cũ trong một lần truy vấn
        List<Integer> idsToFetch = new ArrayList<>();
        for (ProductAttribute existing : currentAttributes) {
            Integer attrId = existing.getAttribute().getId();
            if (attributesMap.containsKey(attrId)) {
                Integer newValueId = attributesMap.get(attrId);
                if (!newValueId.equals(existing.getAttributeValue().getId())) {
                    idsToFetch.add(newValueId);
                }
            }
        }

        Map<Integer, AttributeValue> newValueMap = idsToFetch.isEmpty() ? Map.of() :
                attributeValueRepository.findAllById(idsToFetch).stream()
                        .collect(Collectors.toMap(AttributeValue::getId, av -> av));

        List<ProductAttribute> toUpdate = new ArrayList<>();
        for (ProductAttribute existing : currentAttributes) {
            Integer attrId = existing.getAttribute().getId();
            if (attributesMap.containsKey(attrId)) {
                Integer newValueId = attributesMap.get(attrId);
                if (!newValueId.equals(existing.getAttributeValue().getId())) {
                    AttributeValue newValue = newValueMap.get(newValueId);
                    if (newValue == null) {
                        throw new IllegalArgumentException("Không tìm thấy giá trị thuộc tính: " + newValueId);
                    }
                    existing.setAttributeValue(newValue);
                    toUpdate.add(existing);
                }
            }
        }

        if (!toUpdate.isEmpty()) {
            productAttributeRepository.saveAll(toUpdate);
        }
    }

    @Override
    @Transactional
    public void addNewAttributes(Product product, ProductAttributeRequest request) {
        List<AttributeSelection> selections = request.getAttributes();
        if (selections == null) return;


        List<ProductAttribute> toInsert = new ArrayList<>();
        for (AttributeSelection selection : selections) {
                Attribute attribute = attributeRepository.getReferenceById(selection.getAttributeId());
                AttributeValue attributeValue = attributeValueRepository.getReferenceById(selection.getValueId());

                ProductAttribute pa = ProductAttribute.builder()
                        .product(product)
                        .attribute(attribute)
                        .attributeValue(attributeValue)
                        .build();
                toInsert.add(pa);

        }
        productAttributeRepository.saveAll(toInsert);
    }
    
}
