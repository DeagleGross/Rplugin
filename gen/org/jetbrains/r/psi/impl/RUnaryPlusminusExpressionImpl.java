// This is a generated file. Not intended for manual editing.
package org.jetbrains.r.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.jetbrains.r.parsing.RElementTypes.*;
import org.jetbrains.r.psi.api.*;

public class RUnaryPlusminusExpressionImpl extends ROperatorExpressionImpl implements RUnaryPlusminusExpression {

  public RUnaryPlusminusExpressionImpl(@NotNull ASTNode astNode) {
    super(astNode);
  }

  @Override
  public void accept(@NotNull RVisitor visitor) {
    visitor.visitUnaryPlusminusExpression(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RVisitor) accept((RVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public RExpression getExpression() {
    return PsiTreeUtil.getChildOfType(this, RExpression.class);
  }

  @Override
  @NotNull
  public RPlusminusOperator getPlusminusOperator() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, RPlusminusOperator.class));
  }

}
