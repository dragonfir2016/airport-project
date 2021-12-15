package helsinki.assets.definers;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import helsinki.assets.AssetOwnership;
import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.meta.MetaProperty;
import ua.com.fielden.platform.entity.meta.impl.AbstractAfterChangeEventHandler;
import ua.com.fielden.platform.types.Money;
import ua.com.fielden.platform.utils.IUniversalConstants;

public class AssetOwnershipOrgDefiner extends AbstractAfterChangeEventHandler<String> {
    
    @Override
    public void handle(final MetaProperty<String> mpOrganisation, final String value) {
        final AssetOwnership entity = mpOrganisation.getEntity();
        if (!entity.isInitialising()) {
            if (!StringUtils.isEmpty(value)) {
                mpOrganisation.setRequired(true);
                entity.getProperty("role").setRequired(false);
                entity.getProperty("bu").setRequired(false);
                entity.setRole(null);
                entity.setBu(null);
            }
        } else {
            mpOrganisation.setRequired(!StringUtils.isEmpty(value));
        }
    }

}
