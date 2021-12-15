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
import ua.com.fielden.platform.error.Result;
import ua.com.fielden.platform.keygen.KeyNumber;
import ua.com.fielden.platform.test.ioc.UniversalConstantsForTesting;
import ua.com.fielden.platform.types.Money;
import ua.com.fielden.platform.utils.IUniversalConstants;
import helsinki.assets.actions.AssetTypeBatchUpdateForAssetClassAction;
import helsinki.test_config.AbstractDaoTestCase;


/**
 * Testing of the core {@link AssetOwnership} integrity constraints.
 * 
 * @author Helsinki Team
 *
 */
public class AssetOwnershipTest extends AbstractDaoTestCase {
    private final DateTime now = dateTime("2019-10-01 11:30:00");
    
    @Before
    public void setUp() {
        final UniversalConstantsForTesting constants = (UniversalConstantsForTesting) getInstance(IUniversalConstants.class);
        constants.setNow(now);
    }
    
    @Test
    public void if_no_owners_are_specified_then_all_are_required() {
        final Asset asset = co(Asset.class).findByKeyAndFetch(AssetOwnershipCo.FETCH_PROVIDER.<Asset>fetchFor("asset").fetchModel(), "000000001");
        assertNotNull(asset);
        
        final AssetOwnershipCo co = co(AssetOwnership.class);
        final AssetOwnership ownership = co.new_();
        ownership.setAsset(asset).setStartDate(date("2021-12-10 00:00:00"));
        
        assertTrue(ownership.getProperty("role").isRequired());
        assertTrue(ownership.getProperty("organisation").isRequired());
        assertTrue(ownership.getProperty("bu").isRequired());
        final Result res = ownership.isValid();
        assertFalse(res.isSuccessful());
        System.out.println(res.getMessage());

    }
    
    @Test
    public void if_one_of_meps_is_entered_than_the_other_two_are_cleared_and_nor_required() {
        final Asset asset = co(Asset.class).findByKeyAndFetch(AssetOwnershipCo.FETCH_PROVIDER.<Asset>fetchFor("asset").fetchModel(), "000000001");
        assertNotNull(asset);
        
        final AssetOwnershipCo co = co(AssetOwnership.class);
        final AssetOwnership ownership = co.new_();
        ownership.setAsset(asset).setStartDate(date("2021-12-10 00:00:00"));
        
        final var mpRole = ownership.getProperty("role");
        final var mpOrganisation = ownership.getProperty("organisation");
        final var mpBu = ownership.getProperty("bu");
        
        ownership.setRole("some role");
        
        assertTrue(mpRole.isRequired());
        assertFalse(mpOrganisation.isRequired());
        assertFalse(mpBu.isRequired());
        
        assertEquals(ownership.getRole(), "some role");
        assertNull(ownership.getOrganisation());
        assertNull(ownership.getBu());
        
        ownership.setOrganisation("some org");
        
        assertTrue(mpOrganisation.isRequired());
        assertFalse(mpRole.isRequired());
        assertFalse(mpBu.isRequired());
        
        assertEquals(ownership.getOrganisation(), "some org");
        assertNull(ownership.getRole());
        assertNull(ownership.getBu());
        
        ownership.setBu("some bu");
        
        assertTrue(mpBu.isRequired());
        assertFalse(mpRole.isRequired());
        assertFalse(mpOrganisation.isRequired());
        
        assertEquals(ownership.getBu(), "some bu");
        assertNull(ownership.getRole());
        assertNull(ownership.getOrganisation());
        
        ownership.setBu(null);
        assertFalse(mpBu.isValid());
        
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
        
        final var at1 = save(new_(AssetType.class).setName("AT1").setDesc("Desc for AT1").setAssetClass(ac1));
        save(new_(AssetType.class).setName("AT2").setDesc("Desc for AT2").setAssetClass(ac2));
        save(new_(AssetType.class).setName("AT3").setDesc("Desc for AT3").setAssetClass(ac2));
       
        save(new_(Asset.class).setAssetType(at1).setDesc("test asset"));        
    }

}