/**
 * Copyright (c) 2004,2005 UCLA Compilers Group. All rights reserved. Redistribution and use in source and
 * binary forms, with or without modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the
 * following disclaimer. Redistributions in binary form must reproduce the above copyright notice, this list
 * of conditions and the following disclaimer in the documentation and/or other materials provided with the
 * distribution. Neither UCLA nor the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission. THIS SOFTWARE IS PROVIDED BY THE
 * COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/
/*
 * All files in the distribution of JTB, The Java Tree Builder are Copyright 1997, 1998, 1999 by the Purdue
 * Research Foundation of Purdue University. All rights reserved. Redistribution and use in source and binary
 * forms are permitted provided that this entire copyright notice is duplicated in all such copies, and that
 * any documentation, announcements, and other materials related to such distribution and use acknowledge that
 * the software was developed at Purdue University, West Lafayette, Indiana by Kevin Tao, Wanjun Wang and Jens
 * Palsberg. No charge may be made for copies, derivations, or distributions of this material without the
 * express written consent of the copyright holder. Neither the name of the University nor the name of the
 * author may be used to endorse or promote products derived from this material without specific prior written
 * permission. THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR ANY PARTICULAR PURPOSE.
 */
package EDU.purdue.jtb.common;

import java.util.ArrayList;
import java.util.List;
import EDU.purdue.jtb.analyse.GlobalDataBuilder;
import EDU.purdue.jtb.parser.syntaxtree.ExpansionChoices;
import EDU.purdue.jtb.parser.syntaxtree.INode;

/**
 * Class {@link UserClassInfo} is used by the JTB driver, the visitors and the files generator to store and
 * retrieve information about a (grammar) user (node) class including its name, the list of its fields, its
 * comments. It is also used to generate the user node class code.
 * <p>
 * Class and inner classes maintain state (for a user class), and are not supposed to be run in parallel
 * threads (on the same user class).
 * </p>
 *
 * @author Marc Mazas
 * @version 1.4.0 : 05-08/2009 : MMa : adapted to JavaCC v4.2 grammar and JDK 1.5
 * @version 1.4.6 : 01/2011 : FA/MMa : added -va and -npfx and -nsfx options
 * @version 1.4.7 : 09/2012 : MMa : refactored comment handling to add sub comments and optimization ; added
 *          the reference to the {@link GlobalDataBuilder}
 * @version 1.4.8 : 10/2012 : MMa : added JavaCodeProduction class generation ;<br>
 *          optimized common code 1.4.8 : 12/2014 : MMa : improved some debug printing
 * @version 1.5.0 : 01-03/2017 : MMa : refactored serialVersionUID generation ; added fields hash signature ;
 *          added children methods generation ; moved lists in new class FieldInfo ; enhanced to VisitorInfo
 *          based visitor generation ; renamed from UserInfo ; subject to global packages and classes
 *          refactoring ; removed NodeTCF related code 10-11/2022 : MMa : removed NodeTCF related code
 */
public class UserClassInfo {
  
  /** The corresponding ExpansionChoices node */
  public final INode           astEcNode;
  /** The class name (without optional prefix and suffix) */
  public final String          className;
  /** The class name (including optional prefix and suffix) */
  public final String          fixedClassName;
  /** The list of the class fields representing the node's children */
  public final List<FieldInfo> fields;
  /** The list of the field comments data */
  public List<CommentData>     fieldCmts        = null;
  /**
   * The list of the sub comments data (without field comments data).<br>
   * Built and used only when the "inline accept methods" option is on.
   */
  public List<CommentData>     fieldSubCmts     = null;
  /**
   * The fields hash signature (for helping controlling changes between generated classes versions). Null if
   * no fields.
   */
  public int                   fieldsHashSig    = 0;
  /**
   * The javadoc formatted field comments used by the visit methods (more than once, so that's why they are
   * stored as an optimization)
   */
  public StringBuilder         visitFieldCmtsSb = null;
  
  /**
   * Constructs an instance giving an ExpansionChoices node and a name.
   *
   * @param aEC - the ExpansionChoices node
   * @param aNbFields - the number of fields
   * @param aCN - the class name
   * @param aFCN - the fixed name of the class name
   */
  public UserClassInfo(final ExpansionChoices aEC, final int aNbFields, final String aCN, final String aFCN) {
    astEcNode = aEC;
    className = aCN;
    fixedClassName = aFCN;
    if (aEC != null) {
      fields = new ArrayList<>(aNbFields);
    } else {
      fields = null;
    }
  }
  
  /**
   * Adds a field type, name (with no initializer) to the internal lists.
   *
   * @param aFT - the field type
   * @param aFN - the field name
   */
  public void addField(final String aFT, final String aFN) {
    addField(aFT, aFT, aFN);
  }
  
  /**
   * Adds a field type, name, initializer, code and node to the internal lists.
   *
   * @param aFT - the field type
   * @param aFFT - the fixed name of the field type
   * @param aFN - the field name
   */
  public void addField(final String aFT, final String aFFT, final String aFN) {
    final FieldInfo fi = new FieldInfo();
    fields.add(fi);
    fi.type = aFT;
    fi.fixedType = aFFT;
    fi.name = aFN;
  }
  
  /**
   * Holds the information for a field (representing a node's child).
   *
   * @since 1.5.0
   */
  public class FieldInfo {
    
    /** The field type (without prefix / suffix) */
    public String type      = null;
    /** The field type (including prefix / suffix) */
    public String fixedType = null;
    /** The field name */
    public String name      = null;
  }
  
  /**
   * Holds the data of the lines of a comment or sub comment.
   */
  public class CommentData {
    
    /** The list of the lines */
    public List<CommentLineData> lines = null;
    
  }
  
  /**
   * Holds the data of a line of a comment or sub comment.
   */
  public class CommentLineData {
    
    /** The node's bare comment (should be never null after processing) */
    public String bare  = null;
    /** The node's debug comment (null if none, or starts with " //") */
    public String debug = null;
  }
  
}
