package helsinki.assets.definers;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import helsinki.assets.AssetOwnership;
import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.meta.MetaProperty;
import ua.com.fielden.platform.entity.meta.impl.AbstractAfterChangeEventHandler;
import ua.com.fielden.platform.types.Money;
import ua.com.fielden.platform.utils.IUniversalConstants;

public class AssetOwnershipRoleDefiner extends AbstractAfterChangeEventHandler<String> {
    
    @Override
    public void handle(final MetaProperty<String> mpRole, final String value) {
        final AssetOwnership entity = mpRole.getEntity();
        if (!entity.isInitialising()) {
            if (!StringUtils.isEmpty(value)) {
                mpRole.setRequired(true);
                entity.getProperty("organisation").setRequired(false);
                entity.getProperty("bu").setRequired(false);
                entity.setOrganisation(null);
                entity.setBu(null);
            }
        }
    }

}
