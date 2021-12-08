package helsinki.assets.definers;

import ua.com.fielden.platform.entity.meta.MetaProperty;
import ua.com.fielden.platform.entity.meta.impl.AbstractAfterChangeEventHandler;
import ua.com.fielden.platform.types.Money;

public class AssetFinDetInitCostDefiner extends AbstractAfterChangeEventHandler<Money> {

    @Override
    public void handle(final MetaProperty<Money> mpInitCost, final Money value) {
        mpInitCost.getEntity().getProperty("comisionDate").setRequired(value != null);
    }

}
