package ch.tutteli.tsphp.typechecker;

import ch.tutteli.tsphp.common.ITSPHPAst;
import java.util.List;

public class CastingDto extends PromotionExplicitCastingLevelDto
{

    public ITSPHPAst actualParameter;
    public List<ICastingMethod> castingMethods;
    public List<CastingDto> ambiguousCasts;

    public CastingDto(int thePromotionCount, int theExplicitCastingCount) {
        this(thePromotionCount, theExplicitCastingCount, null, null);
    }

    public CastingDto(int thePromotionCount, int theExplicitCastingCount,
            List<ICastingMethod> theCastingMethods) {
        this(thePromotionCount, theExplicitCastingCount, theCastingMethods, null);
    }

    public CastingDto(int thePromotionCount, int theExplicitCastingCount,
            List<ICastingMethod> theCastingMethods, ITSPHPAst theActualParameter) {
        super(thePromotionCount, theExplicitCastingCount);
        actualParameter = theActualParameter;
        castingMethods = theCastingMethods;

    }
}
