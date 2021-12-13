package helsinki.assets;

import static org.junit.Assert.*;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.from;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.orderBy;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.select;

import java.util.stream.Collectors;
import org.junit.Test;

import ua.com.fielden.platform.test.ioc.UniversalConstantsForTesting;
import ua.com.fielden.platform.utils.IUniversalConstants;
import helsinki.assets.actions.AssetTypeBatchUpdateForAssetClassAction;
import helsinki.test_config.AbstractDaoTestCase;


/**
 * Testing of the batch changes to {@link AssetType}'s {@code assetClass} property.
 * 
 * @author Helsinki Team
 *
 */
public class BatchUpdateForAssetTypesTest extends AbstractDaoTestCase {

    @Test
    public void we_can_update_assetClass_for_selected_assetTypes() {
        final AssetTypeCo coAssetType = co(AssetType.class);
        assertEquals(1, coAssetType.count(select(AssetType.class).where().prop("assetClass.name").eq().val("AC1").model() ));
        
        final AssetClassCo coAssetClass = co(AssetClass.class);
        final var query = select(AssetType.class).where().prop("name").in().values("AT1", "AT2", "AT3", "AT4").model();
        final var qem = from(query).with(AssetTypeCo.FETCH_PROVIDER.fetchModel()).with(orderBy().prop("name").asc().model()).model();
        final var ac1 = coAssetClass.findByKeyAndFetch(AssetTypeCo.FETCH_PROVIDER.<AssetClass>fetchFor("assetClass").fetchModel(), "AC1");
        
        final var coForAction = co(AssetTypeBatchUpdateForAssetClassAction.class);
        final AssetTypeBatchUpdateForAssetClassAction action = coForAction.new_();
        action.setAssetClass(ac1);
        action.setSelectedEntityIds(coAssetType.getAllEntities(qem).stream().map(at -> at.getId()).collect(Collectors.toSet()));
        
        coForAction.save(action);
        
        assertEquals(3 , coAssetType.count(select(AssetType.class).where().prop("assetClass.name").eq().val("AC1").model()));        

    }
    
    @Override
    public boolean saveDataPopulationScriptToFile() {
        return true;
    }

    @Override
    public boolean useSavedDataPopulationScript() {
        return false;
    }

    @Override
    protected void populateDomain() {
        super.populateDomain();

        final UniversalConstantsForTesting constants = (UniversalConstantsForTesting) getInstance(IUniversalConstants.class);
        constants.setNow(dateTime("2019-10-01 11:30:00"));
        
        final var ac1 = save(new_(AssetClass.class).setName("AC1").setDesc("Desc for AC1"));
        final var ac2 = save(new_(AssetClass.class).setName("AC2").setDesc("Desc for AC2"));
        
        save(new_(AssetType.class).setName("AT1").setDesc("Desc for AT1").setAssetClass(ac1));
        save(new_(AssetType.class).setName("AT2").setDesc("Desc for AT2").setAssetClass(ac2));
        save(new_(AssetType.class).setName("AT3").setDesc("Desc for AT3").setAssetClass(ac2));
        
    }

}