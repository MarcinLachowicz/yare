/*
 * MIT License
 *
 * Copyright 2018 Sabre GLBL Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.sabre.oss.yare.engine.executor.runtime.operator;

import com.sabre.oss.yare.engine.executor.runtime.predicate.Predicate;
import com.sabre.oss.yare.engine.executor.runtime.predicate.PredicateContext;
import com.sabre.oss.yare.engine.executor.runtime.value.ValueProvider;

import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.Validate.noNullElements;

public class BiArgsChainedPredicate extends Predicate {

    private final ValueProvider lOperandProvider;
    private final ValueProvider rOperandProvider;
    private final BiArgsPredicate[] predicates;

    public BiArgsChainedPredicate(ValueProvider lOperandProvider, ValueProvider rOperandProvider, List<BiArgsPredicate> predicates) {
        this.lOperandProvider = requireNonNull(lOperandProvider);
        this.rOperandProvider = requireNonNull(rOperandProvider);
        this.predicates = noNullElements(predicates).toArray(new BiArgsPredicate[predicates.size()]);
    }

    @Override
    public final Boolean evaluate(PredicateContext context) {
        Object left = lOperandProvider.get(context);
        if (left == null) {
            return null;
        }
        Object right = rOperandProvider.get(context);
        if (right == null) {
            return null;
        }
        for (BiArgsPredicate predicate : predicates) {
            if (predicate.applicable(left, right)) {
                return predicate.evaluate(left, right);
            }
        }
        throw new IllegalArgumentException("Operator doesn't support current arguments");
    }
}
