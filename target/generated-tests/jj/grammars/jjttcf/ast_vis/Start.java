/* Generated By:JJTree: Do not edit this line. Start.java Version 7.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package grammars.jjttcf.ast_vis;

import grammars.jjttcf.*;

public
class Start extends SimpleNode {
  public Start(int id) {
    super(id);
  }

  public Start(TcfGrammar p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public void jjtAccept(TcfGrammarVisitor visitor, Object data) {

    visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=0d41f11fbfb7dab87f16e6379fbcec0b (do not edit this line) */