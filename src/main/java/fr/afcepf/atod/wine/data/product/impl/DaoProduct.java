package fr.afcepf.atod.wine.data.product.impl;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import fr.afcepf.atod.vin.data.exception.WineErrorCode;
import fr.afcepf.atod.vin.data.exception.WineException;
import fr.afcepf.atod.wine.data.impl.DaoGeneric;
import fr.afcepf.atod.wine.data.product.api.IDaoProduct;
import fr.afcepf.atod.wine.data.util.DaoUtil;
import fr.afcepf.atod.wine.entity.Product;
import fr.afcepf.atod.wine.entity.ProductType;
import fr.afcepf.atod.wine.entity.ProductVarietal;
import fr.afcepf.atod.wine.entity.ProductVintage;
import fr.afcepf.atod.wine.entity.ProductWine;

@Service
@Transactional
public class DaoProduct extends DaoGeneric<Product, Integer>
        implements IDaoProduct{

    /*****************************************************
     * Requetes HQL 
     ****************************************************/
    private static final String REQFINDBYNAME = "SELECT p FROM Product p " + "WHERE p.name like :name";

    private static final String REQEXPPROD = "SELECT distinct(p) FROM Product p LEFT JOIN FETCH p.productSuppliers WHERE 0 < ANY"
            + "(SELECT ps.quantity from ProductSupplier ps WHERE ps.pk.product=p)" 
    		+ " AND p.price > :paramMin";

    private static final String REQGETPROMOTEDPRODUCTSSORTEDBYENDDATE = "SELECT"
            + " p FROM Product p LEFT JOIN FETCH p.productSuppliers WHERE 0 < ANY"
            + "(SELECT ps.quantity from ProductSupplier ps WHERE ps.pk.product=p)" 
            + " AND  p.speEvent IS NOT NULL";

    private static final String REQAPPELLATIONSBYWINETYPE = "SELECT DISTINCT p.appellation "
            + "FROM Product p WHERE p.productType = :type";

    private static final String REQVARIETALSBYWINETYPE = "SELECT  DISTINCT(pv) "
            + "FROM ProductVarietal pv LEFT JOIN pv.productsWine pw "
            + "LEFT JOIN pw.productType pt WHERE pt = :type";

    private static final String REQFINDBYAPPELATION = "SELECT p FROM Product "
            + "p WHERE p.appellation like :paramApp";

    private static final String REQFINDBYTYPE = "SELECT distinct(pt) FROM ProductType pt "
            + "left join fetch pt.productsWine where pt.type like :paramType";
    private static final String REQFINDBYVINTAGE = "SELECT distinct(pv) FROM ProductVintage pv "
            + "left join fetch pv.productsWine"
            + "  where pv.year =:paramVintage";

    private static final String REQFINDBYVARIETAL = "SELECT distinct(pv) FROM ProductVarietal pv "
            + "left join fetch pv.productsWine pw"
            + "  where pw.description like :paramVarietal";

    private static final String REQTYPEVARITAL = "SELECT distinct(pv) FROM ProductVarietal pv "
            + "left join fetch pv.productsWine as pw WHERE pv.description = :paramVarietal "
            + "AND pw.productType.type = :paramType";
    
    private static final String PRODUCT_WINE = "Select p FROM ProductWine p ";
    private static final String REQTYPEAPPELLATION = PRODUCT_WINE
    		+ "WHERE p.productType.type = :paramType AND p.appellation = :appellation";

    private static final String REQTYPEVINTAGE = "SELECT distinct(pv) FROM ProductVintage pv "
            + "left join fetch pv.productsWine as pw WHERE pv.year = :paramVintage "
            + "AND pw.productType.type = :paramType";
    
    private static final String REQTYPEMAXMONEY = PRODUCT_WINE
            + "WHERE p.productType.type = :paramType AND p.price > :paramMin";
    
    private static final String REQTYPEMONEY = PRODUCT_WINE
            + "WHERE p.productType.type = :paramType AND p.price between :start "
            + " and :end";
    
    private static final String REQTYPE = PRODUCT_WINE
            + "WHERE p.productType.type = :paramType";
    

    /*********************************************
     * Requetes HQL
     *********************************************/
    @SuppressWarnings("unchecked")
	@Override
    public List<Product> findByName(String name) throws WineException {
        List<Product> p = null;
        if (!name.equals(DaoUtil.EMPTY_STR)) {
            p =  (getSf().getCurrentSession().createQuery(REQFINDBYNAME).setParameter("name", "%" + name + "%")
                    .list());
            if (p == null) {
                throw new WineException(WineErrorCode.RECHERCHE_NON_PRESENTE_EN_BASE,
                        "the product named " + name + " has not been" + " found in the database.");
            }
        } else {
            throw new WineException(WineErrorCode.RECHERCHE_NON_PRESENTE_EN_BASE,
                    "the product named " + name + " has not been" + " found in the database.");
        }
        return p;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Product> getPromotedProductsSortedByEndDate(Integer max) {
        List<Product> l = null;
        l = getSf().getCurrentSession().createQuery(REQGETPROMOTEDPRODUCTSSORTEDBYENDDATE).setMaxResults(max).list();
        return l;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Product> findExpensiveProducts(double min) throws WineException {
        List<Product> expensiveProds = null;
        if (min >= 0) {
            expensiveProds = getSf().getCurrentSession().createQuery(REQEXPPROD)
                    .setParameter("paramMin", min).list();
            if (expensiveProds.isEmpty()) {
                throw new WineException(WineErrorCode.RECHERCHE_NON_PRESENTE_EN_BASE,
                        "expensive products not found in the database");
            }
        } else {
            throw new WineException(WineErrorCode.RECHERCHE_NON_PRESENTE_EN_BASE, "criteria has to be defined...");
        }

        return expensiveProds;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ProductVarietal> getVarietalsByWineType(ProductType type) throws WineException {
        List<ProductVarietal> l = null;
        l = getSf().getCurrentSession()
                .createQuery(REQVARIETALSBYWINETYPE)
                .setParameter("type", type)
                .list();
        return l;
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public List<Product> findByAppelation(String appelation) throws WineException {
        List<Product> list = null;
        if (!appelation.equals(DaoUtil.EMPTY_STR)) {
            list = getSf().getCurrentSession().createQuery(REQFINDBYAPPELATION)
                    .setParameter("paramApp", "%" + appelation + "%").list();
            if (list == null) {
                throw new WineException(WineErrorCode.RECHERCHE_NON_PRESENTE_EN_BASE,
                        "the product list named " + list + " has not been"
                        + " found in the database.");
            }
        } else {
            throw new WineException(WineErrorCode.RECHERCHE_NON_PRESENTE_EN_BASE,
                    "the product list named " + list + " has not been"
                    + " found in the database.");
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ProductVintage> findByVintage(Integer vintage) throws WineException {
        List<ProductVintage> list = null;
        list = getSf().getCurrentSession().createQuery(REQFINDBYVINTAGE).setParameter("paramVintage", vintage).list();
        return list;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ProductVarietal> findByVarietal(String variatal) throws WineException {
        List<ProductVarietal> list = null;
        if (!variatal.equals(DaoUtil.EMPTY_STR)) {
            list = getSf().getCurrentSession().createQuery(REQFINDBYVARIETAL)
                    .setParameter("paramVarietal", "%" + variatal + "%").list();
            if (list == null) {
                throw new WineException(WineErrorCode.RECHERCHE_NON_PRESENTE_EN_BASE,
                        "the productVarietal list named " + list + " has not been"
                        + " found in the database.");
            }
        } else {
            throw new WineException(WineErrorCode.RECHERCHE_NON_PRESENTE_EN_BASE,
                    "the productVarietal list named " + list + " has not been"
                    + " found in the database.");
        }

        return list;

    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ProductType> findByType(String wineType) throws WineException {
        List<ProductType> list = null;
        if (!wineType.equals(DaoUtil.EMPTY_STR)) {
            list = getSf().getCurrentSession().createQuery(REQFINDBYTYPE)
                    .setParameter("paramType", "%" + wineType + "%").list();
            if (list == null) {
                throw new WineException(WineErrorCode.RECHERCHE_NON_PRESENTE_EN_BASE,
                        "the productVarietal list named " + list + " has not been"
                        + " found in the database.");
            }
        } else {
            throw new WineException(WineErrorCode.RECHERCHE_NON_PRESENTE_EN_BASE,
                    "the productVarietal list named " + list + " has not been"
                    + " found in the database.");
        }

        return list;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Product> findByNotCompleteName(String name) throws WineException {
        List<Product> list = null;
        if (!name.equals(DaoUtil.EMPTY_STR)) {
            list = getSf().getCurrentSession().createQuery(REQFINDBYNAME)
                    .setParameter("name", "%" + name + "%").list();
            if (list == null) {
                throw new WineException(WineErrorCode.RECHERCHE_NON_PRESENTE_EN_BASE,
                        "the product list named " + list + " has not been"
                        + " found in the database.");
            }
        } else {
            throw new WineException(WineErrorCode.RECHERCHE_NON_PRESENTE_EN_BASE,
                    "the product list named " + list + " has not been"
                    + " found in the database.");
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> getAppellationsByWineType(ProductType type) throws WineException {
        List<String> l = null;
        l = getSf().getCurrentSession()
                .createQuery(REQAPPELLATIONSBYWINETYPE)
                .setParameter("type", type)
                .list();
        return l;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ProductWine> findByVarietalAndType(ProductType wineType, ProductVarietal varietal, Integer firstRow, Integer rowsPerPage)
            throws WineException {
        List<ProductWine> listWine = null;
        List<ProductVarietal> list = null;
        if (!wineType.getType().equalsIgnoreCase(DaoUtil.EMPTY_STR)
                && !varietal.getDescription().equalsIgnoreCase(DaoUtil.EMPTY_STR)) {
            list = getSf().getCurrentSession().createQuery(REQTYPEVARITAL)
                    .setParameter("paramType", wineType.getType())
                    .setParameter("paramVarietal", varietal.getDescription())
                    .list();
            if (!list.isEmpty()) {
            	ArrayList<ProductWine> listTemp = new ArrayList(list.get(0).getProductsWine());
                listWine = listTemp.subList(firstRow, (firstRow+rowsPerPage<listTemp.size()-1 ? firstRow+rowsPerPage : listTemp.size()-1 ));
            } else {
                throw new WineException(WineErrorCode.RECHERCHE_NON_PRESENTE_EN_BASE,
                    "the products according to the type "
                    + wineType + " and " + varietal.getDescription()
                    + " has not been"
                    + " found in the database.");
            }
        } else {
            throw new WineException(WineErrorCode.RECHERCHE_NON_PRESENTE_EN_BASE,
                    "the products according to the type "
                    + wineType + " and " + varietal.getDescription()
                    + " has not been"
                    + " found in the database.");
        }
        return listWine;
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<ProductWine> findByVintageAndType(ProductType type, ProductVintage vintage,
    		Integer firstRow, Integer rowsPerPage) throws WineException {
        List<ProductWine> listWine = null;
        List<ProductVintage> list = null;
        if (!type.getType().equalsIgnoreCase(DaoUtil.EMPTY_STR)
                && vintage.getYear() != null) {
            list = getSf().getCurrentSession().createQuery(REQTYPEVINTAGE)
                    .setParameter("paramType", type.getType())
                    .setParameter("paramVintage",vintage.getYear())
                    .setFirstResult(firstRow)
                    .setMaxResults(rowsPerPage)
                    .list();
            if (!list.isEmpty()) {
            	ArrayList<ProductWine> listTemp = new ArrayList(list.get(0).getProductsWine());
                listWine = listTemp.subList(firstRow, (firstRow+rowsPerPage<listTemp.size()-1 ? firstRow+rowsPerPage : listTemp.size()-1 ));
            } else {
                throw new WineException(WineErrorCode.RECHERCHE_NON_PRESENTE_EN_BASE,
                    "the products according to the type "
                    + type + " and " + vintage.getYear().toString()
                    + " has not been"
                    + " found in the database.");
            }
        } else {
            throw new WineException(WineErrorCode.RECHERCHE_NON_PRESENTE_EN_BASE,
                    "the products according to the type "
                    + type + " and " + vintage.getYear().toString()
                    + " has not been"
                    + " found in the database.");
        }
        return listWine;
    }    

    @SuppressWarnings("unchecked")
	@Override
    public List<ProductWine> findByMoneyAndType(ProductType type, Integer integ, Integer firstRow, Integer rowsPerPage) throws WineException {
        List<ProductWine> listWine = null;
        if (!type.getType().equalsIgnoreCase(DaoUtil.EMPTY_STR)) {
            listWine = getSf().getCurrentSession()
                    .createQuery(REQTYPEMAXMONEY)
                    .setParameter("paramType", type.getType())
                    .setParameter("paramMin", integ.doubleValue())
                    .setFirstResult(firstRow)
                    .setMaxResults(rowsPerPage)
                    .list();
        } else {
            throw new WineException(WineErrorCode.RECHERCHE_NON_PRESENTE_EN_BASE,
                    "Pas de produit avec un prix aussi eleve.");
        }
        return listWine;
    }


	@SuppressWarnings("unchecked")
	@Override
	public List<ProductWine> findByMoneyAndType(ProductType type, Integer integ, Integer maxInt, Integer firstRow,
			Integer rowsPerPage) throws WineException {
		List<ProductWine> listWine = null;
        if (!type.getType().equalsIgnoreCase(DaoUtil.EMPTY_STR)) {
            listWine = getSf().getCurrentSession()
                    .createQuery(REQTYPEMONEY)
                    .setParameter("paramType", type.getType())
                    .setParameter("start", integ.doubleValue())
                    .setParameter("end", maxInt.doubleValue())
                    .setFirstResult(firstRow)
                    .setMaxResults(rowsPerPage)
                    .list();
        } else {
            throw new WineException(WineErrorCode.RECHERCHE_NON_PRESENTE_EN_BASE,
                    "Pas de produit avec un prix aussi eleve.");
        }
        return listWine; 
	}

	@SuppressWarnings("unchecked")
	@Override
	public Integer countByVarietalAndType(ProductType wineType, ProductVarietal varietal) {
		 	Integer count =0;
	        List<ProductVarietal> list = null;
	        if (!wineType.getType().equalsIgnoreCase(DaoUtil.EMPTY_STR)
	                && !varietal.getDescription().equalsIgnoreCase(DaoUtil.EMPTY_STR)) {
	            list = getSf().getCurrentSession().createQuery(REQTYPEVARITAL)
	                    .setParameter("paramType", wineType.getType())
	                    .setParameter("paramVarietal", varietal.getDescription())
	                    .list();
	            if (!list.isEmpty()) {
	            	count = new ArrayList(list.get(0).getProductsWine()).size();
	            } 
	        } 
	        return count;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Integer countByVintageAndType(ProductType type, ProductVintage vintage) {
		Integer count =0;
        List<ProductVintage> list = null;
        if (!type.getType().equalsIgnoreCase(DaoUtil.EMPTY_STR)
                && vintage.getYear() != null) {
            list = getSf().getCurrentSession().createQuery(REQTYPEVINTAGE)
                    .setParameter("paramType", type.getType())
                    .setParameter("paramVintage",vintage.getYear())
                    .list();
            if (!list.isEmpty()) {
                count = new ArrayList(list.get(0).getProductsWine()).size();
            }
        }
		return count;
	}

	@Override
	public Integer countByMoneyAndType(ProductType type, Integer integ) {
		Integer count = 0;
        if (!type.getType().equalsIgnoreCase(DaoUtil.EMPTY_STR)) {
            count = getSf().getCurrentSession()
                    .createQuery(REQTYPEMAXMONEY)
                    .setParameter("paramType", type.getType())
                    .setParameter("paramMin", integ.doubleValue())
                    .list().size();
        } 
        return count;
	}

	@Override
	public Integer countByMoneyAndType(ProductType type, Integer integ, Integer maxInt) {
		Integer count = 0;
        if (!type.getType().equalsIgnoreCase(DaoUtil.EMPTY_STR)) {
            count = getSf().getCurrentSession()
                    .createQuery(REQTYPEMONEY)
                    .setParameter("paramType", type.getType())
                    .setParameter("start", integ.doubleValue())
                    .setParameter("end", maxInt.doubleValue())
                    .list().size();
        }
        return count; 
	}

	@Override
	public Integer countByAppellationAndType(ProductType type, String appellation) {
		Integer count = 0;
        if (!type.getType().equalsIgnoreCase(DaoUtil.EMPTY_STR)) {
            count = getSf().getCurrentSession()
                    .createQuery(REQTYPEAPPELLATION)
                    .setParameter("paramType", type.getType())
                    .setParameter("appellation", appellation)
                    .list().size();
        }
        return count; 
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProductWine> findByAppelationAndType(ProductType type, String appellation, Integer firstRow,
			Integer rowsPerPage) throws WineException {
		List<ProductWine> listWine = null;
        if (!type.getType().equalsIgnoreCase(DaoUtil.EMPTY_STR)) {
            listWine = getSf().getCurrentSession()
                    .createQuery(REQTYPEAPPELLATION)
                    .setParameter("paramType", type.getType())
                    .setParameter("appellation", appellation)
                    .setFirstResult(firstRow)
                    .setMaxResults(rowsPerPage)
                    .list();
        } else {
            throw new WineException(WineErrorCode.RECHERCHE_NON_PRESENTE_EN_BASE,
                    "Pas de produit de ce type avec cette appellation");
        }
        return listWine; 
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProductWine> findByType(ProductType type, Integer firstRow, Integer rowsPerPage) throws WineException {
		List<ProductWine> listWine = null;
        if (!type.getType().equalsIgnoreCase(DaoUtil.EMPTY_STR)) {
            listWine = getSf().getCurrentSession()
                    .createQuery(REQTYPE)
                    .setParameter("paramType", type.getType())
                    .setFirstResult(firstRow)
                    .setMaxResults(rowsPerPage)
                    .list();
        } else {
            throw new WineException(WineErrorCode.RECHERCHE_NON_PRESENTE_EN_BASE,
                    "Pas de produit de ce type");
        }
        return listWine;
	}

	@Override
	public Integer countByType(ProductType type) {
		Integer count = 0;
        if (!type.getType().equalsIgnoreCase(DaoUtil.EMPTY_STR)) {
            count = getSf().getCurrentSession()
                    .createQuery(REQTYPE)
                    .setParameter("paramType", type.getType())
                    .list().size();
        }
        return count; 
	}

	public Integer countByAppellation(ProductType type, Object o) {
		return null;
	}
}
