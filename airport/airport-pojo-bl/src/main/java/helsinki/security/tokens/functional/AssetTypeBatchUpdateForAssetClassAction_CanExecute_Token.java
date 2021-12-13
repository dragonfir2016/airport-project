package helsinki.security.tokens.functional;

import static java.lang.String.format;

import ua.com.fielden.platform.reflection.TitlesDescsGetter;
import ua.com.fielden.platform.security.tokens.Template;
import helsinki.assets.actions.AssetTypeBatchUpdateForAssetClassAction;
import helsinki.security.tokens.UsersAndPersonnelModuleToken;

/**
 * A security token for entity {@link AssetTypeBatchUpdateForAssetClassAction} to guard Execute.
 *
 * @author Developers
 *
 */
public class AssetTypeBatchUpdateForAssetClassAction_CanExecute_Token extends UsersAndPersonnelModuleToken {
    public final static String TITLE = format(Template.EXECUTE.forTitle(), AssetTypeBatchUpdateForAssetClassAction.ENTITY_TITLE);
    public final static String DESC = format(Template.EXECUTE.forDesc(), AssetTypeBatchUpdateForAssetClassAction.ENTITY_TITLE);
}