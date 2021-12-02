package helsinki.assets.actions;

import com.google.inject.Inject;

import ua.com.fielden.platform.security.Authorise;
import ua.com.fielden.platform.dao.annotations.SessionRequired;
import helsinki.security.tokens.functional.AssetTypeBatchUpdateForAssetClassAction_CanExecute_Token;
import ua.com.fielden.platform.dao.CommonEntityDao;
import ua.com.fielden.platform.entity.query.IFilter;
import ua.com.fielden.platform.entity.annotation.EntityType;

/**
 * DAO implementation for companion object {@link AssetTypeBatchUpdateForAssetClassActionCo}.
 *
 * @author Developers
 *
 */
@EntityType(AssetTypeBatchUpdateForAssetClassAction.class)
public class AssetTypeBatchUpdateForAssetClassActionDao extends CommonEntityDao<AssetTypeBatchUpdateForAssetClassAction> implements AssetTypeBatchUpdateForAssetClassActionCo {

    @Inject
    public AssetTypeBatchUpdateForAssetClassActionDao(final IFilter filter) {
        super(filter);
    }

    @Override
    @SessionRequired
    @Authorise(AssetTypeBatchUpdateForAssetClassAction_CanExecute_Token.class)
    public AssetTypeBatchUpdateForAssetClassAction save(final AssetTypeBatchUpdateForAssetClassAction action) {
        return super.save(action);
    }

}