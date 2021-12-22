package helsinki.assets;

import static org.junit.Assert.*;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.from;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.orderBy;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.select;

import java.util.Date;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import ua.com.fielden.platform.entity.meta.MetaProperty;
import ua.com.fielden.platform.keygen.KeyNumber;
import ua.com.fielden.platform.test.ioc.UniversalConstantsForTesting;
import ua.com.fielden.platform.types.Money;
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
    private final DateTime now = dateTime("2019-10-01 11:30:00");
    
    @Before
    public void setUp() {
        final UniversalConstantsForTesting constants = (UniversalConstantsForTesting) getInstance(IUniversalConstants.class);
        constants.setNow(now);
    }

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
    
    
    @Test
    public void asset_financial_details_are_not_created_again_for_existing_asset() {
        final AssetType type = co(AssetType.class).findByKeyAndFetch(AssetCo.FETCH_PROVIDER.<AssetType>fetchFor("assetType").fetchModel(), "AT1");
        final AssetCo co = co(Asset.class);
        final Asset asset = co.new_();
        assertEquals(AssetCo.DEFAUL_KEY_VALUE, asset.getNumber());
        asset.setAssetType(type);
        asset.setDesc("some desc");
        final Asset savedAsset = co.save(asset);
        
        final var co$AssetFinDet = co$(AssetFinDet.class);
        final AssetFinDet assetFinDet = co$AssetFinDet.findByKeyAndFetch(AssetFinDetCo.FETCH_PROVIDER.fetchModel(), savedAsset);
        assertNotNull(assetFinDet);
        assetFinDet.setInitCost(Money.of("100.00"));
        assetFinDet.setComisionDate(date("2021-11-06 10:00:00"));
        final var savedAssetFinDet = co$AssetFinDet.save(assetFinDet);
        assertEquals(Money.of("100.00"), savedAssetFinDet.getInitCost());

        final Asset savedAgain = co.save(savedAsset.setDesc("some other desc."));
        final AssetFinDet assetFinDetAfterAssetWasSaved = co$AssetFinDet.findByKeyAndFetch(AssetFinDetCo.FETCH_PROVIDER.fetchModel(), savedAsset);
        assertEquals(Money.of("100.00"), assetFinDetAfterAssetWasSaved.getInitCost());
    }
    
    @Test
    public void dependent_date_props_resolve_invalid_values_by_means_of_dependency() {
        final AssetType type = co(AssetType.class).findByKeyAndFetch(AssetCo.FETCH_PROVIDER.<AssetType>fetchFor("assetType").fetchModel(), "AT1");
        final AssetCo co = co(Asset.class);
        final Asset asset = co.new_();
        assertEquals(AssetCo.DEFAUL_KEY_VALUE, asset.getNumber());
        asset.setAssetType(type);
        asset.setDesc("some desc");
        final Asset savedAsset = co.save(asset);
        
        final var co$AssetFinDet = co$(AssetFinDet.class);
        final AssetFinDet assetFinDet = co$AssetFinDet.findByKeyAndFetch(AssetFinDetCo.FETCH_PROVIDER.fetchModel(), savedAsset);
        assetFinDet.setComisionDate(date("2021-12-06 10:00:00"));
        final MetaProperty<Date> mpComisionDate = assetFinDet.getProperty("comisionDate");
        assertTrue(mpComisionDate.isValid());
        
        assertNull(assetFinDet.getDisposalDate());
        assetFinDet.setDisposalDate(date("2021-12-06 08:00:00"));
        final MetaProperty<Date> mpDisposalDate = assetFinDet.getProperty("disposalDate");
        assertFalse(mpDisposalDate.isValid());
        assertNull(assetFinDet.getDisposalDate());
        
        assetFinDet.setComisionDate(date("2021-12-06 06:00:00"));
        assertTrue(mpComisionDate.isValid());
        assertTrue(mpDisposalDate.isValid());

    }
    
    @Test
    public void comissionDate_is_required_where_initCost_is_entered() {
        final AssetType type = co(AssetType.class).findByKeyAndFetch(AssetCo.FETCH_PROVIDER.<AssetType>fetchFor("assetType").fetchModel(), "AT1");
        final AssetCo co = co(Asset.class);
        final Asset savedAsset = co.save(co.new_().setAssetType(type).setDesc("some desc"));
        
        final var co$AssetFinDet = co$(AssetFinDet.class);
        final AssetFinDet assetFinDet = co$AssetFinDet.findByKeyAndFetch(AssetFinDetCo.FETCH_PROVIDER.fetchModel(), savedAsset);
        
        final MetaProperty<Date> mpComisionDate = assetFinDet.getProperty("comisionDate");
        final MetaProperty<Money> mpInitCost = assetFinDet.getProperty("initCost");
        
        assertFalse(mpComisionDate.isRequired());
        assertFalse(mpInitCost.isRequired());
        
        assetFinDet.setInitCost(Money.of("100.00"));
        
        assertTrue(mpComisionDate.isRequired());


        assetFinDet.setComisionDate(date("2021-12-06 10:00:00"));
        assertTrue(mpComisionDate.isRequired());

        assertEquals(date("2021-12-06 10:00:00"), assetFinDet.getComisionDate());
    }
    
    @Test
    public void comisionDate_is_assigned_now_upon_initCost_change_if_empty() {
        final AssetType type = co(AssetType.class).findByKeyAndFetch(AssetCo.FETCH_PROVIDER.<AssetType>fetchFor("assetType").fetchModel(), "AT1");
        final AssetCo co = co(Asset.class);
        final Asset savedAsset = co.save(co.new_().setAssetType(type).setDesc("some desc"));
        
        final var co$AssetFinDet = co$(AssetFinDet.class);
        final AssetFinDet assetFinDet = co$AssetFinDet.findByKeyAndFetch(AssetFinDetCo.FETCH_PROVIDER.fetchModel(), savedAsset);
        
        final MetaProperty<Date> mpComisionDate = assetFinDet.getProperty("comisionDate");
        final MetaProperty<Money> mpInitCost = assetFinDet.getProperty("initCost");
        
        assertFalse(mpComisionDate.isRequired());
        assertFalse(mpInitCost.isRequired());
        assertNull(assetFinDet.getComisionDate());
        
        assetFinDet.setInitCost(Money.of("100.00"));
        
        assertTrue(mpComisionDate.isRequired());
        assertTrue(mpComisionDate.isRequired());
        assertEquals(now.toDate(), assetFinDet.getComisionDate());
        
        final UniversalConstantsForTesting constants = (UniversalConstantsForTesting) getInstance(IUniversalConstants.class);
        constants.setNow(now.plusMinutes(1));

        
        assetFinDet.setInitCost(Money.of("101.00"));
        assertEquals(now.toDate(), assetFinDet.getComisionDate());
    }
    
    @Test
    public void comisionDate_does_not_change_upon_retrieval() {
        final AssetType type = co(AssetType.class).findByKeyAndFetch(AssetCo.FETCH_PROVIDER.<AssetType>fetchFor("assetType").fetchModel(), "AT1");
        final AssetCo co = co(Asset.class);
        final Asset savedAsset = co.save(co.new_().setAssetType(type).setDesc("some desc"));
        
        final var co$AssetFinDet = co$(AssetFinDet.class);
        final AssetFinDet assetFinDet = save(co$AssetFinDet.findByKeyAndFetch(AssetFinDetCo.FETCH_PROVIDER.fetchModel(), savedAsset).setInitCost(Money.of("100.00")));        
        assertEquals(now.toDate(), assetFinDet.getComisionDate());
        
        final UniversalConstantsForTesting constants = (UniversalConstantsForTesting) getInstance(IUniversalConstants.class);
        constants.setNow(now.plusMinutes(1));
        
        final AssetFinDet assetFinDet2 = co$AssetFinDet.findByKeyAndFetch(AssetFinDetCo.FETCH_PROVIDER.fetchModel(), savedAsset);        
        assertEquals(assetFinDet.getComisionDate(), assetFinDet2.getComisionDate());
    }
    
    
    
    @Override
    public boolean saveDataPopulationScriptToFile() {
        return false;
    }

    @Override
    public boolean useSavedDataPopulationScript() {
        return false;
    }

    @Override
    protected void populateDomain() {
        super.populateDomain();

        final UniversalConstantsForTesting constants = (UniversalConstantsForTesting) getInstance(IUniversalConstants.class);
        constants.setNow(now);
        
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