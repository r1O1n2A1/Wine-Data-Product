package fr.afcepf.atod.wine.data.product.impl;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import fr.afcepf.atod.wine.data.impl.DaoGeneric;
import fr.afcepf.atod.wine.data.product.api.IDaoCountry;
import fr.afcepf.atod.wine.entity.Country;

@Service
@Transactional
public class DaoCountry extends DaoGeneric<Country, Integer> implements IDaoCountry {

}
