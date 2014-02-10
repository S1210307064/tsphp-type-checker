package ch.tsphp.typechecker;

import java.io.Serializable;

public class PromotionExplicitCastingLevelDto implements Serializable
{

    public int promotionLevel;
    public int explicitCastingLevel;

    public PromotionExplicitCastingLevelDto(int thePromotionLevel, int theExplicitCastingLevel) {
        promotionLevel = thePromotionLevel;
        explicitCastingLevel = theExplicitCastingLevel;
    }
}
