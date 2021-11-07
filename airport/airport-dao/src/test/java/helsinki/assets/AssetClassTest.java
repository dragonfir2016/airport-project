package helsinki.assets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import helsinki.test_config.AbstractDaoTestCase;
import ua.com.fielden.platform.entity.meta.MetaProperty;
import ua.com.fielden.platform.error.Result;
import ua.com.fielden.platform.test.ioc.UniversalConstantsForTesting;
import ua.com.fielden.platform.utils.IUniversalConstants;


/**
 * Basic testing of {@link AssetClass}.
 * 
 * @author Generated
 *
 */
public class AssetClassTest extends AbstractDaoTestCase {

    @Test
    public void a_simple_asset_class_can_be_created_and_saved() {
    	final var assetClass = co(AssetClass.class).new_();
    	assetClass.setName("Building");
    	assetClass.setDesc("Property, builidngs and carparks");
    	final var savedAssetClass = co(AssetClass.class).save(assetClass);
    	assertNotNull(savedAssetClass);
    	assertTrue(savedAssetClass.isActive());
    	assertEquals("Building", savedAssetClass.getName());
    }

    @Test
    public void can_access_prop_active_with_default_fetch_model() {
        co(AssetClass.class).save(co(AssetClass.class).new_().setName("Building").setDesc("Property, builidngs and carparks"));
        final var assetClass = co(AssetClass.class).findByKeyAndFetch(AssetClassCo.FETCH_PROVIDER.fetchModel(), "Building");
        assertTrue(assetClass.isActive());
    }

    @Test
    public void name_can_not_be_longer_than_50_characters() {
        final var longName = "Building".repeat(50);
        final var assetClass = co(AssetClass.class).new_().setName(longName).setDesc("Property, builidngs and carparks");
        final MetaProperty<String> mpName = assetClass.getProperty("name");
        assertNull(mpName.getValue());
        assertNull(assetClass.getName());
        assertFalse(mpName.isValid());
        final Result validationResult = mpName.getFirstFailure();
        assertNotNull(validationResult);
        assertFalse(validationResult.isSuccessful());
        System.out.println(validationResult.getMessage());
    }
    

    @Override
    public boolean saveDataPopulationScriptToFile() {
        return false;
    }

    @Override
    public boolean useSavedDataPopulationScript() {
        return true;
    }

    @Override
    protected void populateDomain() {
    	super.populateDomain();

    	final UniversalConstantsForTesting constants = (UniversalConstantsForTesting) getInstance(IUniversalConstants.class);
    	constants.setNow(dateTime("2019-10-01 11:30:00"));

    	// If the use of saved data population script is indicated then there is no need to proceed with any further data population logic.
        if (useSavedDataPopulationScript()) {
            return;
        }

    }

}
