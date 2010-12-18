/**
 * Copyright (c) 2004,2005 UCLA Compilers Group. All rights reserved. Redistribution and use in
 * source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met: Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary form must reproduce
 * the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution. Neither UCLA nor the names
 * of its contributors may be used to endorse or promote products derived from this software without
 * specific prior written permission. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 **/
/*
 * All files in the distribution of JTB, The Java Tree Builder are Copyright 1997, 1998, 1999 by the
 * Purdue Research Foundation of Purdue University. All rights reserved. Redistribution and use in
 * source and binary forms are permitted provided that this entire copyright notice is duplicated in
 * all such copies, and that any documentation, announcements, and other materials related to such
 * distribution and use acknowledge that the software was developed at Purdue University, West
 * Lafayette, Indiana by Kevin Tao, Wanjun Wang and Jens Palsberg. No charge may be made for copies,
 * derivations, or distributions of this material without the express written consent of the
 * copyright holder. Neither the name of the University nor the name of the author may be used to
 * endorse or promote products derived from this material without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR ANY PARTICULAR
 * PURPOSE.
 */
package EDU.purdue.jtb.misc;

import java.util.ArrayList;
import java.util.Iterator;

import EDU.purdue.jtb.syntaxtree.ExpansionChoices;
import EDU.purdue.jtb.syntaxtree.INode;
import EDU.purdue.jtb.syntaxtree.NodeToken;
import EDU.purdue.jtb.visitor.CommentsPrinter;

/**
 * Class ClassInfo is used by the visitors to store and ask for information about a class including
 * its name, the list of field types, names and initializers.
 *<p>
 * Uses class {@link CommentsPrinter} to find field javadoc comments and format them.<br>
 *
 * @author Marc Mazas, mmazas@sopragroup.com
 * @version 1.4.0 : 05-08/2009 : MMa : adapted to JavaCC v4.2 grammar and JDK 1.5
 */
public class ClassInfo {

  /** The node */
  final INode                astNode;
  /** The class name */
  final String               className;
  /** The list of the types of the class fields representing the node's children */
  final ArrayList<String>    fieldTypes;
  /** The list of the names of the class fields representing the node's children */
  final ArrayList<String>    fieldNames;
  /** The list of the initializers of the class fields representing the node's children */
  final ArrayList<String>    fieldInitializers;
  /** True if the class allows specific initializing constructor(s) (without {@link NodeToken} nodes */
  boolean                    needInitializingConstructor = false;
  /**
   * The list of the javadoc comments of the class fields representing the node's children<br>
   * Start with " * " but without indentation, and may be on multiple lines
   */
  ArrayList<String>          fieldComments               = null;
  /** The comments printer visitor */
  static CommentsPrinter     cpv                         = new CommentsPrinter();
  /** The OS line separator */
  public static final String LS                          = System.getProperty("line.separator");

  /**
   * Constructs an instance giving an ExpansionChoices node and a name
   *
   * @param ec the ExpansionChoices node
   * @param cn the class name
   */
  public ClassInfo(final ExpansionChoices ec, final String cn) {
    astNode = ec;
    className = cn;
    final int nb = (ec.f1.present() ? ec.f1.size() + 1 : 1);
    fieldTypes = new ArrayList<String>(nb);
    fieldNames = new ArrayList<String>(nb);
    fieldInitializers = new ArrayList<String>(nb);
  }

  /**
   * Getter for the node.
   *
   * @return the node
   */
  public INode getAstNode() {
    return astNode;
  }

  /**
   * Getter for the class name.
   *
   * @return the class name
   */
  public String getClassName() {
    return className;
  }

  /**
   * Getter for the fields types.
   *
   * @return the fields types
   */
  public ArrayList<String> getFieldTypes() {
    return fieldTypes;
  }

  /**
   * Getter for the fields names.
   *
   * @return the fields names
   */
  public ArrayList<String> getFieldNames() {
    return fieldNames;
  }

  /**
   * Getter for the fields comments (which may be null).
   *
   * @return the fields comments
   */
  public ArrayList<String> getFieldComments() {
    return fieldComments;
  }

  /**
   * Setter for the fields comments.
   *
   * @param fc the fields comments
   */
  public void setFieldComments(final ArrayList<String> fc) {
    fieldComments = fc;
  }

  /**
   * Adds a field type, name (with no initializer) to the internal lists.
   *
   * @param fieldType the field type
   * @param fieldName the field name
   */
  public void addField(final String fieldType, final String fieldName) {
    addField(fieldType, fieldName, null);
  }

  /**
   * Adds a field type, name and initializer to the internal lists.
   *
   * @param fieldType the field type
   * @param fieldName the field name
   * @param fieldInitializer the field initializer
   */
  public void addField(final String fieldType, final String fieldName, final String fieldInitializer) {
    fieldTypes.add(fieldType);
    fieldNames.add(fieldName);

    if (fieldInitializer == null || fieldInitializer.equals(""))
      fieldInitializers.add(null);
    else {
      fieldInitializers.add(fieldInitializer);
      needInitializingConstructor = true;
    }
  }

  /**
   * Generates the BNF description of the current class as a bunch of comments showing which field
   * names belong to which parts of the production.<br>
   * Does not add the javadoc opening and closing delimiters.
   *
   * @param spc the indentation
   * @return the BNF description
   */
  public String genAllFieldsComment(final Spacing spc) {
    if (fieldComments == null)
      fieldComments = new ArrayList<String>(fieldNames.size());
    return cpv.formatAllFieldsComment(spc, this);
  }

  /**
   * Generates the node class code into a newly allocated buffer.
   *
   * @param spc the current indentation
   * @return the buffer with the node class code
   */
  public StringBuilder genClassString(final Spacing spc) {
    Iterator<String> types = fieldTypes.iterator();
    Iterator<String> names = fieldNames.iterator();
    Iterator<String> inits;
    final StringBuilder sb = new StringBuilder(2048);

    /*
     * class declaration
     */

    sb.append(spc.spc).append("public class " + className);

    if (Globals.nodesSuperclass != null)
      sb.append(" extends ").append(Globals.nodesSuperclass);
    sb.append(" implements ").append(Globals.iNodeName).append(" {").append(LS).append(LS);
    spc.updateSpc(+1);

    /*
     * data fields declarations
     */

    for (; types.hasNext();) {
      if (Globals.javaDocComments)
        sb.append(spc.spc).append("/** A child node */").append(LS);
      sb.append(spc.spc).append("public ").append(types.next()).append(" ").append(names.next())
        .append(";").append(LS).append(LS);
    }

    if (Globals.parentPointer) {
      if (Globals.javaDocComments)
        sb.append(spc.spc).append("/** The parent pointer */").append(LS);
      sb.append(spc.spc).append("private ").append(Globals.iNodeName).append(" parent;").append(LS)
        .append(LS);
    }

    if (Globals.javaDocComments)
      sb.append(spc.spc).append("/** The serial version uid */").append(LS);
    sb.append(spc.spc).append("private static final long serialVersionUID = ")
      .append(Globals.SERIAL_UID + "L;").append(LS).append(LS);

    /*
     * standard constructor header
     */

    if (Globals.javaDocComments) {
      sb.append(spc.spc).append("/**").append(LS);
      sb.append(spc.spc).append(" * Constructs the node with ");
      sb.append(fieldTypes.size() > 1 ? "all its children nodes." : "its child node.").append(LS);
      sb.append(spc.spc).append(" *").append(LS);
      types = fieldTypes.iterator();
      sb.append(spc.spc).append(" * @param n0 ").append(fieldTypes.size() > 1 ? "first" : "the")
        .append(" child node").append(LS);
      for (int i = 1; i < fieldTypes.size(); i++)
        sb.append(spc.spc).append(" * @param n").append(i).append(" next child node").append(LS);
      sb.append(spc.spc).append(" */").append(LS);
    }
    sb.append(spc.spc).append("public ").append(className).append("(");
    types = fieldTypes.iterator();
    if (types.hasNext())
      sb.append("final ").append(types.next()).append(" n0");
    for (int i = 1; types.hasNext(); i++)
      sb.append(", final ").append(types.next()).append(" n").append(i);
    sb.append(") {").append(LS);

    /*
     * standard constructor body
     */

    names = fieldNames.iterator();
    spc.updateSpc(+1);
    for (int count = 0; names.hasNext(); ++count) {
      final String nm = names.next();
      sb.append(spc.spc).append(nm).append(" = n").append(count).append(";").append(LS);
      if (Globals.parentPointer) {
        sb.append(spc.spc).append("if (").append(nm).append(" != null)").append(LS);
        spc.updateSpc(+1);
        sb.append(spc.spc).append(nm).append(".setParent(this);").append(LS);
        spc.updateSpc(-1);
      }
    }

    spc.updateSpc(-1);
    sb.append(spc.spc).append("}").append(LS);

    /*
     * specific initializing constructor header if necessary
     */

    if (needInitializingConstructor) {
      int count = 0;
      boolean firstTime = true;
      sb.append(LS);
      if (Globals.javaDocComments) {
        sb.append(spc.spc).append("/**").append(LS);
        sb.append(spc.spc).append(" * Constructs the node with only its non ")
          .append(Globals.nodeTokenName);
        sb.append(" child node").append(fieldTypes.size() > 1 ? "(s)." : ".").append(LS);
        sb.append(spc.spc).append(" *").append(LS);
        types = fieldTypes.iterator();
        inits = fieldInitializers.iterator();
        while (types.hasNext()) {
          types.next();
          if (inits.next() == null) {
            if (!firstTime)
              sb.append(spc.spc).append(" * @param n").append(count).append(" next child node")
                .append(LS);
            else
              sb.append(spc.spc).append(" * @param n").append(count).append(" first child node")
                .append(LS);
            ++count;
            firstTime = false;
          }
        }
        sb.append(spc.spc).append(" */").append(LS);
      }
      sb.append(spc.spc).append("public ").append(className).append("(");
      count = 0;
      firstTime = true;
      types = fieldTypes.iterator();
      inits = fieldInitializers.iterator();
      while (types.hasNext()) {
        final String type = types.next();
        if (inits.next() == null) {
          if (!firstTime)
            sb.append(", final ");
          else
            sb.append("final ");
          sb.append(type).append(" n").append(count);
          ++count;
          firstTime = false;
        }
      }

      sb.append(") {").append(LS);
    }

    /*
     * specific initializing constructor body if necessary
     */

    if (needInitializingConstructor) {
      int count = 0;
      names = fieldNames.iterator();
      inits = fieldInitializers.iterator();
      spc.updateSpc(+1);
      while (names.hasNext()) {
        final String nm = names.next();
        final String init = inits.next();
        if (init != null)
          sb.append(spc.spc).append(nm).append(" = ").append(init).append(";").append(LS);
        else {
          sb.append(spc.spc).append(nm).append(" = n").append(count).append(";").append(LS);
          ++count;
        }
        if (Globals.parentPointer) {
          sb.append(spc.spc).append("if (").append(nm).append(" != null)").append(LS);
          spc.updateSpc(+1);
          sb.append(spc.spc).append("  ").append(nm).append(".setParent(this);").append(LS);
          spc.updateSpc(-1);
        }
      }
      spc.updateSpc(-1);
      sb.append(spc.spc).append("}").append(LS);
    }

    /*
     * visit methods
     */

    sb.append(LS);
    if (Globals.javaDocComments) {
      sb.append(spc.spc).append("/**").append(LS);
      sb.append(spc.spc).append(" * Accepts the ").append(Globals.iRetArguVisitorName)
        .append(" visitor.").append(LS);
      sb.append(spc.spc).append(" *").append(LS);
      sb.append(spc.spc).append(" * @param <").append(Globals.genRetType)
        .append("> the user return type").append(LS);
      sb.append(spc.spc).append(" * @param <").append(Globals.genArguType)
        .append("> the user argument type").append(LS);
      sb.append(spc.spc).append(" * @param vis the visitor").append(LS);
      sb.append(spc.spc).append(" * @param argu a user chosen argument").append(LS);
      sb.append(spc.spc).append(" * @return a user chosen return information").append(LS);
      sb.append(spc.spc).append(" */").append(LS);
    }
    sb.append(spc.spc).append("public <").append(Globals.genRetType).append(", ")
      .append(Globals.genArguType).append("> " + Globals.genRetType).append(" accept(final ")
      .append(Globals.iRetArguVisitorName).append("<" + Globals.genRetType).append(", ")
      .append(Globals.genArguType).append("> vis, final " + Globals.genArguType).append(" argu) {")
      .append(LS);
    spc.updateSpc(+1);
    sb.append(spc.spc).append("return vis.visit(this, argu);").append(LS);
    spc.updateSpc(-1);
    sb.append(spc.spc).append("}").append(LS);

    sb.append(LS);
    if (Globals.javaDocComments) {
      sb.append(spc.spc).append("/**").append(LS);
      sb.append(spc.spc).append(" * Accepts the ").append(Globals.iRetVisitorName)
        .append(" visitor.").append(LS);
      sb.append(spc.spc).append(" *").append(LS);
      sb.append(spc.spc).append(" * @param <").append(Globals.genRetType)
        .append("> the user return type").append(LS);
      sb.append(spc.spc).append(" * @param vis the visitor").append(LS);
      sb.append(spc.spc).append(" * @return a user chosen return information").append(LS);
      sb.append(spc.spc).append(" */").append(LS);
    }
    sb.append(spc.spc).append("public <").append(Globals.genRetType).append("> ")
      .append(Globals.genRetType + " accept(final ").append(Globals.iRetVisitorName).append("<")
      .append(Globals.genRetType).append("> vis) {").append(LS);
    spc.updateSpc(+1);
    sb.append(spc.spc).append("return vis.visit(this);").append(LS);
    spc.updateSpc(-1);
    sb.append(spc.spc).append("}").append(LS);

    sb.append(LS);
    if (Globals.javaDocComments) {
      sb.append(spc.spc).append("/**").append(LS);
      sb.append(spc.spc).append(" * Accepts the ").append(Globals.iVoidArguVisitorName)
        .append(" visitor.").append(LS);
      sb.append(spc.spc).append(" *").append(LS);
      sb.append(spc.spc).append(" * @param <").append(Globals.genArguType)
        .append("> the user argument type").append(LS);
      sb.append(spc.spc).append(" * @param vis the visitor").append(LS);
      sb.append(spc.spc).append(" * @param argu a user chosen argument").append(LS);
      sb.append(spc.spc).append(" */").append(LS);
    }
    sb.append(spc.spc).append("public <").append(Globals.genArguType)
      .append("> void accept(final " + Globals.iVoidArguVisitorName).append("<")
      .append(Globals.genArguType).append("> vis, final " + Globals.genArguType).append(" argu) {")
      .append(LS);
    spc.updateSpc(+1);
    sb.append(spc.spc).append("vis.visit(this, argu);").append(LS);
    spc.updateSpc(-1);
    sb.append(spc.spc).append("}").append(LS);

    sb.append(LS);
    if (Globals.javaDocComments) {
      sb.append(spc.spc).append("/**").append(LS);
      sb.append(spc.spc).append(" * Accepts the ").append(Globals.iVoidVisitorName)
        .append(" visitor.").append(LS);
      sb.append(spc.spc).append(" *").append(LS);
      sb.append(spc.spc).append(" * @param vis the visitor").append(LS);
      sb.append(spc.spc).append(" */").append(LS);
    }
    sb.append(spc.spc).append("public void accept(final ").append(Globals.iVoidVisitorName)
      .append(" vis) {").append(LS);
    spc.updateSpc(+1);
    sb.append(spc.spc).append("vis.visit(this);").append(LS);
    spc.updateSpc(-1);
    sb.append(spc.spc).append("}").append(LS);

    /*
     * parent getter & setter methods
     */

    if (Globals.parentPointer) {
      sb.append(LS);
      if (Globals.javaDocComments) {
        sb.append(spc.spc).append("/**").append(LS);
        sb.append(spc.spc).append(" * Setter for the parent node.").append(LS);
        sb.append(spc.spc).append(" *").append(LS);
        sb.append(spc.spc).append(" * @param n the parent node").append(LS);
        sb.append(spc.spc).append(" */").append(LS);
      }
      sb.append(spc.spc).append("public void setParent(final ").append(Globals.iNodeName)
        .append(" n) {").append(LS);
      spc.updateSpc(+1);
      sb.append(spc.spc).append("parent = n;").append(LS);
      spc.updateSpc(-1);
      sb.append(spc.spc).append("}").append(LS);
      sb.append(LS);
      if (Globals.javaDocComments) {
        sb.append(spc.spc).append("/**").append(LS);
        sb.append(spc.spc).append(" * Getter for the parent node.").append(LS);
        sb.append(spc.spc).append(" *").append(LS);
        sb.append(spc.spc).append(" * @return the parent node").append(LS);
        sb.append(spc.spc).append(" */").append(LS);
      }
      sb.append(spc.spc).append("public ").append(Globals.iNodeName).append(" getParent() {")
        .append(LS);
      spc.updateSpc(+1);
      sb.append(spc.spc).append("return parent;").append(LS);
      spc.updateSpc(-1);
      sb.append(spc.spc).append("}").append(LS);
    }

    /*
     * end
     */

    spc.updateSpc(-1);
    sb.append(spc.spc).append(LS);
    sb.append(spc.spc).append("}").append(LS);

    return sb;
  }

}
