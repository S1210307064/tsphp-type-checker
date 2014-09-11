/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker;

import java.io.Serializable;

/**
 * Represents a tuple composed of the promotion level and the casting level.
 * <p/>
 * The casting level tells how many castings (implicit and explicit) have to be applied until one type is casted to
 * another.
 */
public class PromotionAndCastingLevelDto implements Serializable
{

    public int promotionLevel;
    public int castingLevel;

    public PromotionAndCastingLevelDto(int thePromotionLevel, int theExplicitCastingLevel) {
        promotionLevel = thePromotionLevel;
        castingLevel = theExplicitCastingLevel;
    }
}
