package fr.afcepf.atod.wine.data.product.impl;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import fr.afcepf.atod.wine.data.impl.DaoGeneric;
import fr.afcepf.atod.wine.data.product.api.IDaoProductType;
import fr.afcepf.atod.wine.entity.ProductType;


@Service
@Transactional
public class DaoProductType extends DaoGeneric<ProductType, 
        Integer> implements IDaoProductType {

	

}
