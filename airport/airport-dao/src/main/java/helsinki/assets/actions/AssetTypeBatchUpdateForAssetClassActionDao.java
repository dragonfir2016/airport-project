package helsinki.assets.actions;

import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.cond;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.expr;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.fetchAggregates;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.fetchAll;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.fetchAllAndInstrument;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.fetchAllInclCalc;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.fetchAllInclCalcAndInstrument;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.fetchAndInstrument;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.fetchIdOnly;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.fetchKeyAndDescOnly;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.fetchKeyAndDescOnlyAndInstrument;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.fetchOnly;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.fetchOnlyAndInstrument;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.from;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.orderBy;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.select;
import static ua.com.fielden.platform.utils.EntityUtils.fetch;

import java.util.stream.Stream;

import com.google.inject.Inject;

import ua.com.fielden.platform.security.Authorise;
import ua.com.fielden.platform.dao.annotations.SessionRequired;
import helsinki.assets.AssetType;
import helsinki.assets.AssetTypeCo;
import helsinki.security.tokens.functional.AssetTypeBatchUpdateForAssetClassAction_CanExecute_Token;
import ua.com.fielden.platform.dao.CommonEntityDao;
import ua.com.fielden.platform.dao.QueryExecutionModel;
import ua.com.fielden.platform.entity.query.IFilter;
import ua.com.fielden.platform.entity.query.fluent.fetch;
import ua.com.fielden.platform.entity.query.model.EntityResultQueryModel;
import ua.com.fielden.platform.entity.query.model.OrderingModel;
import ua.com.fielden.platform.error.Result;
import ua.com.fielden.platform.entity.annotation.EntityType;
import ua.com.fielden.platform.entity.fetch.IFetchProvider;

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
        action.isValid().ifFailure(Result :: throwRuntime);
        
        final var query = select(AssetType.class).where().prop("id").in().values(action.getSelectedEntityIds().toArray()).model();
        final var qem = from(query).with(AssetTypeCo.FETCH_PROVIDER.fetchModel()).model();
        
        final AssetTypeCo coAssetType = co$(AssetType.class);
        try (final Stream<AssetType> st = coAssetType.stream(qem)) {
            st.forEach(at -> coAssetType.save(at.setAssetClass(action.getAssetClass())));
        }
        
        return super.save(action);
    }
    
    @Override
        protected IFetchProvider<AssetTypeBatchUpdateForAssetClassAction> createFetchProvider() {
            return FETCH_PROVIDER;
        }

}