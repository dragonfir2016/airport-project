package helsinki.assets;

import static org.junit.Assert.*;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.from;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.orderBy;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.select;

import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import ua.com.fielden.platform.keygen.KeyNumber;
import ua.com.fielden.platform.test.ioc.UniversalConstantsForTesting;
import ua.com.fielden.platform.utils.IUniversalConstants;
import helsinki.assets.actions.AssetTypeBatchUpdateForAssetClassAction;
import helsinki.test_config.AbstractDaoTestCase;


/**
 * Testing of the core {@link Asset} integrity constraints.
 * 
 * @author Helsinki Team
 *
 */
public class AssetTest extends AbstractDaoTestCase {

    @Test
    public void new_asset_get_their_number_generated() {
        final AssetType type = co(AssetType.class).findByKeyAndFetch(AssetCo.FETCH_PROVIDER.<AssetType>fetchFor("assetType").fetchModel(), "AT1");
        final AssetCo co = co(Asset.class);
        final Asset asset1 = co.new_();
        assertEquals(AssetCo.DEFAUL_KEY_VALUE, asset1.getNumber());
        asset1.setAssetType(type);
        asset1.setDesc("some desc");
        
        final Asset savedAsset1 = co.save(asset1);
        assertNotEquals(AssetCo.DEFAUL_KEY_VALUE, savedAsset1.getNumber());
        assertFalse(StringUtils.isEmpty(savedAsset1.getNumber()));
        assertEquals("000000001", savedAsset1.getNumber());
        
        final Asset asset2 = co.new_();
        assertEquals(AssetCo.DEFAUL_KEY_VALUE, asset2.getNumber());
        asset2.setAssetType(type);
        asset2.setDesc("Another desc.");
        
        final Asset savedAsset2 = co.save(asset2);
        assertNotEquals(AssetCo.DEFAUL_KEY_VALUE, savedAsset2.getNumber());
        assertFalse(StringUtils.isEmpty(savedAsset2.getNumber()));
        assertEquals("000000002", savedAsset2.getNumber());
        System.out.printf(savedAsset1.getNumber() + savedAsset2.getNumber());
    }
    
    
    @Test
    public void exsiting_assets_cannot_have_their_number_changed() {
        final AssetType type = co(AssetType.class).findByKeyAndFetch(AssetCo.FETCH_PROVIDER.<AssetType>fetchFor("assetType").fetchModel(), "AT1");
        final AssetCo co = co(Asset.class);
        final Asset asset = co.new_();
        assertEquals(AssetCo.DEFAUL_KEY_VALUE, asset.getNumber());
        asset.setAssetType(type);
        asset.setDesc("some desc");
        
        final Asset savedAsset = co.save(asset);
        
        savedAsset.setNumber("somenumber");
        assertEquals("000000001", savedAsset.getNumber());
    }
    
    
    @Test
    public void asset_financial_details_created_with_every_new_asset() {
        final AssetType type = co(AssetType.class).findByKeyAndFetch(AssetCo.FETCH_PROVIDER.<AssetType>fetchFor("assetType").fetchModel(), "AT1");
        final AssetCo co = co(Asset.class);
        final Asset asset = co.new_();
        assertEquals(AssetCo.DEFAUL_KEY_VALUE, asset.getNumber());
        asset.setAssetType(type);
        asset.setDesc("some desc");
        final Asset savedAsset = co.save(asset);
        
        final AssetFinDet assetFinDet = co(AssetFinDet.class).findByKey(savedAsset);
        assertNotNull(assetFinDet);

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
        
        if(useSavedDataPopulationScript()) {
            return;
        }
        
        save(new_(KeyNumber.class, AssetCo.ASSET_KEY_NAME).setValue("0"));
        
        final var ac1 = save(new_(AssetClass.class).setName("AC1").setDesc("Desc for AC1"));
        final var ac2 = save(new_(AssetClass.class).setName("AC2").setDesc("Desc for AC2"));
        
        save(new_(AssetType.class).setName("AT1").setDesc("Desc for AT1").setAssetClass(ac1));
        save(new_(AssetType.class).setName("AT2").setDesc("Desc for AT2").setAssetClass(ac2));
        save(new_(AssetType.class).setName("AT3").setDesc("Desc for AT3").setAssetClass(ac2));
        
    }

}