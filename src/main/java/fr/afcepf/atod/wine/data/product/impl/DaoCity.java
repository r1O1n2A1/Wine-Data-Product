package fr.afcepf.atod.wine.data.product.impl;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import fr.afcepf.atod.wine.data.impl.DaoGeneric;
import fr.afcepf.atod.wine.data.product.api.IDaoCity;
import fr.afcepf.atod.wine.entity.City;

@Service
@Transactional
public class DaoCity extends DaoGeneric<City, Integer> implements IDaoCity {

}
