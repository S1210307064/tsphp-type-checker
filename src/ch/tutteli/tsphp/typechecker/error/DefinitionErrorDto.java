package ch.tutteli.tsphp.typechecker.error;

public class DefinitionErrorDto extends ReferenceErrorDto
{

    public String identifierNewDefinition;
    public int lineNewDefinition;
    public int positionNewDefinition;

    public DefinitionErrorDto(
            String theExistingIdentifier, int theLineExistingDefinition, int thePositionExistingDefinition,
            String theNewIdentifier, int theLineNewDefinition, int thePositionNewDefinition) {
        super(theNewIdentifier, theLineExistingDefinition, thePositionExistingDefinition);
        identifier = theExistingIdentifier;
        line = theLineExistingDefinition;
        position = thePositionExistingDefinition;
        identifierNewDefinition = theNewIdentifier;
        lineNewDefinition = theLineNewDefinition;
        positionNewDefinition = thePositionNewDefinition;
    }

    @Override
    public String toString() {
        return identifier + " " + line + "|" + position + " "
                + lineNewDefinition + "|" + positionNewDefinition;
    }
}
