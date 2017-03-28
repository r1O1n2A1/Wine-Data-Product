package fr.afcepf.atod.wine.data.product.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fr.afcepf.atod.vin.data.exception.WineErrorCode;
import fr.afcepf.atod.vin.data.exception.WineException;
import fr.afcepf.atod.wine.data.product.api.IDaoProduct;
import fr.afcepf.atod.wine.data.util.DaoUtil;
import fr.afcepf.atod.wine.entity.Product;

/**
 * Test class for the database access of a {@link Product} 
 * @author ronan
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:/springDataTest.xml")
public class TestDaoProduct {
	private static Logger logger = Logger.getLogger(TestDaoProduct.class);
	@Autowired
	private IDaoProduct daoProduct;
	private String nominal = "pre";
	private Product expectedProduct;
	private String exception = "un parpin";
	private WineException expectedException;
	public TestDaoProduct() {
		expectedException = new WineException(WineErrorCode.RECHERCHE_NON_PRESENTE_EN_BASE,
				"the product named un parpin has not been" + " found in the database.");
		try {
			expectedProduct = new Product(1,
					"pre",
					(double)500, 
					"un produit", 
					new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2016-09-20 13:07:50"), 
					null, 
					null, 
					null);
		} catch (ParseException e) {
			logger.info(e.getMessage());
		}
	}
	@Test
	public void testNominalProductByName() throws WineException {
		List<Product> retour = daoProduct.findByName(nominal);
		Assert.assertNotNull(retour);
		if (retour.get(0) != null && 
				!retour.get(0).getDescription().equalsIgnoreCase(DaoUtil.EMPTY_STR)) {
			Assert.assertEquals(retour.get(0).getName(), expectedProduct.getName());
			Assert.assertEquals(retour.get(0).getCreatedAt().getTime(), 
					expectedProduct.getCreatedAt().getTime());
		}
	}
	@Test
	public void testErrorProductByName() {
		try {
			daoProduct.findByName(exception);
		} catch(WineException e) {
			Assert.assertEquals(e.getErreurVin(),
					expectedException.getErreurVin());
		}
	}
}
