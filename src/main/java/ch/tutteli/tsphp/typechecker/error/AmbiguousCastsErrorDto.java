/*
 * Copyright 2012 Robert Stoll <rstoll@tutteli.ch>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package ch.tutteli.tsphp.typechecker.error;

import java.util.List;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class AmbiguousCastsErrorDto extends ReferenceErrorDto
{

    public String[] leftToRightCasts;
    public String[] rightToLeftCasts;
    public List<String[]> leftAmbiguouities;
    public List<String[]> rightAmbiguouities;

    public AmbiguousCastsErrorDto(String theIdentifier, int theLine, int thePosition,
            String[] theLeftToRightCasts, String[] theRightToLeftCasts,
            List<String[]> theLeftAmbiguouities, List<String[]> theRightAmbiguouities) {
        super(theIdentifier, theLine, thePosition);
        leftToRightCasts = theLeftToRightCasts;
        rightToLeftCasts = theRightToLeftCasts;
        leftAmbiguouities = theLeftAmbiguouities;
        rightAmbiguouities = theRightAmbiguouities;
    }
}