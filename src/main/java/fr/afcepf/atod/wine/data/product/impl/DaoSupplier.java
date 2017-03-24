package fr.afcepf.atod.wine.data.product.impl;


import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import fr.afcepf.atod.vin.data.exception.WineException;
import fr.afcepf.atod.wine.data.impl.DaoGeneric;
import fr.afcepf.atod.wine.data.product.api.IDaoSupplier;
import fr.afcepf.atod.wine.entity.Supplier;

@Service
@Transactional
public class DaoSupplier extends DaoGeneric<Supplier, Integer> implements IDaoSupplier {
	
}
