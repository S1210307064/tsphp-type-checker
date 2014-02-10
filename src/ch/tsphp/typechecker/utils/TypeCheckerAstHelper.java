package ch.tsphp.typechecker.utils;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.typechecker.CastingDto;
import ch.tsphp.typechecker.ICastingMethod;

public class TypeCheckerAstHelper implements ITypeCheckerAstHelper
{

    @Override
    public ITSPHPAst prependCasting(CastingDto dto) {
        ITSPHPAst parent = (ITSPHPAst) dto.actualParameter.getParent();

        //save child index, since it is going to change during rewrite
        int childIndex = dto.actualParameter.getChildIndex();
        ITSPHPAst actualParameter = dto.actualParameter;
        for (ICastingMethod castingMethod : dto.castingMethods) {
            ITSPHPAst cast = castingMethod.createCastAst(actualParameter);
            cast.setEvalType(castingMethod.getType());
            actualParameter = cast;
        }
        actualParameter.setParent(parent);
        parent.replaceChildren(childIndex, childIndex, actualParameter);

        return actualParameter;
    }
}
