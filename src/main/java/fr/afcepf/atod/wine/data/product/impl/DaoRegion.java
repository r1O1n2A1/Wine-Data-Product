package fr.afcepf.atod.wine.data.product.impl;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import fr.afcepf.atod.wine.data.impl.DaoGeneric;
import fr.afcepf.atod.wine.data.product.api.IDaoRegion;
import fr.afcepf.atod.wine.entity.Region;

@Service
@Transactional
public class DaoRegion extends DaoGeneric<Region, Integer> implements IDaoRegion {

}
