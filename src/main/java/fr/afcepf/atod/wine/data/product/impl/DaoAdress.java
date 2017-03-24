package fr.afcepf.atod.wine.data.product.impl;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import fr.afcepf.atod.wine.data.impl.DaoGeneric;
import fr.afcepf.atod.wine.data.product.api.IDaoAdress;
import fr.afcepf.atod.wine.entity.Adress;

@Service
@Transactional
public class DaoAdress extends DaoGeneric<Adress, Integer> implements IDaoAdress {

	
}
