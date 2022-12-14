/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is part of dcm4che, an implementation of DICOM(TM) in
 * Java(TM), available at http://sourceforge.net/projects/dcm4che.
 *
 * The Initial Developer of the Original Code is
 * Agfa-Gevaert Group.
 * Portions created by the Initial Developer are Copyright (C) 2003-2008
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 * See @authors listed below.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */

package com.healta.plugin.dcm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.dcm4che3.data.Attributes;
//import org.dcm4che3.data.Dataset;
//import org.dcm4che3.data.DcmObjectFactory;
import org.dcm4che3.data.PersonName;

//import org.dcm4che.dict.Tags;
import com.healta.plugin.dcm.Match.Node;

/**
 * @author Gunter.Zeilinger <gunterze@gmail.com>
 * @author Franz Willer <franz.willer@agfa.com>
 * @author Harald.Metterlein@heicare.com
 * @author Jan Pechanec <jpechanec@orcz.cz>
 * @version $Revision: 16925 $ $Date: 2012-08-06 16:38:03 +0800 (Mon, 06 Aug 2012) $
 * @since 26.08.2003
 */
public class SqlBuilder {

    public static final boolean TYPE1 = false;
    public static final boolean TYPE2 = true;
    public static final String DESC = " DESC";
    public static final String ASC = " ASC";
    public static final String WHERE = " WHERE ";
    public static final String AND = " AND ";
    private static final String DATE_FORMAT = "''yyyy-MM-dd HH:mm:ss.SSS''";
    private static final String ORA_DATE_FORMAT = 
        "'TO_TIMESTAMP('''yyyy-MM-dd HH:mm:ss.SSS'','''YYYY-MM-DD HH24:MI:SS.FF''')";
    private static final String[] ONE = { "1" };
    private String[] select;
    private String[] from;
    private String[] aliases;
    private String[] leftJoin;
    private String[] relations;
    private ArrayList<Match> matches = new ArrayList<Match>();
    private ArrayList<String> orderby = new ArrayList<String>();
    private String groupBy;
    private int limit = 0;
    private int offset = 0;
    private String whereOrAnd = WHERE;
    private boolean distinct = false;
    private boolean subQueryMode = false;

    private boolean matchNotSupported = false;

    private static int getDatabase() {
        return JdbcProperties.getInstance().getDatabase();
    }

    public final void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }
    public final void setSubQueryMode(boolean subQuery) {
        this.subQueryMode = subQuery;
    }

    public void setSelect(String[] fields) {
        select = fields;//JdbcProperties.getInstance().getProperties(fields);
    }
    public void setFieldNamesForSelect(String[] fields) {
        select = fields;
    }

    public void setSelect1() {
        select = ONE;
    }

    public void setSelectCount( String[] fields, boolean distinct) {
        select = new String[]{ getCountOf(fields, distinct) };
    }

    public String getCountOf(String[] fields, boolean distinct) {
        StringBuffer sb = new StringBuffer();
        sb.append("count(");
        if ( distinct ) sb.append("DISTINCT ");
        JdbcProperties jdbcProps = JdbcProperties.getInstance();
        if ( fields == null || fields.length < 1 ) {
            sb.append('*');
        } else {
            sb.append( jdbcProps.getProperty(fields[0]) );
            for ( int i=1 ; i < fields.length ; i++) {
                sb.append(',').append( jdbcProps.getProperty(fields[i]) );
            }
        }
        sb.append(')');
        return sb.toString();
    }

    public void setFrom(String[] entities) {
        JdbcProperties jp = JdbcProperties.getInstance();
        from = jp.getProperties(entities);
    }

    public void setAliases(String[] aliases) {
        if (aliases != null) {
            if (aliases.length != from.length)
                throw new IllegalArgumentException("aliases.length != from.length");
            if (relations != null)
                throw new IllegalStateException("relations != null");
        }
        this.aliases = aliases;
    }

    public void setSubquery( SqlBuilder subQuery ) {
        from = new String[]{ "("+subQuery.getSql()+")" };
    }

    public void setLeftJoin(String[] leftJoin) {
        if (leftJoin == null) {
            this.leftJoin = null;
            return;
        }
        if (leftJoin.length % 4 != 0) {
            throw new IllegalArgumentException("" + Arrays.asList(leftJoin));
        }
        this.leftJoin = JdbcProperties.getInstance().getProperties(leftJoin);
        // replace table name by alias name
        int i4;
        String alias, col;
        for (int i = 0, n = leftJoin.length/4; i < n; ++i) {
            i4 = 4*i;
            alias = this.leftJoin[i4+1];
            if (alias != null) {
                col = this.leftJoin[i4+3];
                this.leftJoin[i4+3] = alias + col.substring(col.indexOf('.'));
            }
        }
    }

    public void addOrderBy(String field, String order) {
        orderby.add(JdbcProperties.getInstance().getProperty(field) + order);
    }
    
    public void setGroupBy(String field) {
        groupBy = field == null ? null : " GROUP BY "+JdbcProperties.getInstance().getProperty(field);
    }

    public final void setLimit(int limit) {
        this.limit = Math.max(0, limit);
    }

    public final void setOffset(int offset) {
        this.offset = Math.max(0, offset);
    }

    public void setRelations(String[] relations) {
        if (relations == null) {
            this.relations = null;
            return;
        }
        if (aliases != null)
            throw new IllegalArgumentException("aliases != null");
        if ((relations.length & 1) != 0) {
            throw new IllegalArgumentException(
                    "relations[" + relations.length + "]");
        }
        this.relations = JdbcProperties.getInstance().getProperties(relations);
    }

    /**
     * @return Returns the matches.
     */
    protected ArrayList<Match> getMatches() {
        return matches;
    }
    /**
     * Set the matches for where clause.
     * <p>
     * if <code>matches is null</code> the current matches are cleared.
     * @param matches The matches to set.
     */
    protected void setMatches(ArrayList<Match> matches) {
        if ( matches == null ) 
            this.matches.clear();
        else
            this.matches = matches;
    }

    private Match addMatch(Match match) {
        if (match.isUniveralMatch()) return null;
        matches.add(match);
        return match;
    }

    public Match addNULLValueMatch(String alias, String field, boolean inverter ) {
        return addMatch( new Match.NULLValue(alias, field, inverter ) );
    }

    public Match addIntValueMatch(String alias, String field, boolean type2,
            int value) {
        return addMatch(new Match.IntValue(alias, field, type2, value));
    }

    public Match addListOfIntMatch(String alias, String field, boolean type2,
            int[] values) {
        return addMatch(new Match.ListOfInt(alias, field, type2, values));
    }

    public Match addSingleValueMatch(String alias, String field, boolean type2,
            String value) {
        return addMatch(new Match.SingleValue(alias, field, type2, value));
    }

    public Match addFieldValueMatch(String alias1, String field1, boolean type2,
            String alias2, String field2) {
        return addMatch(new Match.FieldValue(alias1, field1, type2, alias2, field2));
    }

    public Match addLiteralMatch(String alias, String field, boolean type2,
            String literal) {
        return addMatch(new Match.AppendLiteral(alias, field, type2, literal));
    }

    public Match addBooleanMatch(String alias,  String field, boolean type2,
            boolean value) {
        return addMatch( getBooleanMatch(alias, field, type2, value) );
    }

    public Match getBooleanMatch(String alias,  String field, boolean type2,
            boolean value) {
        return new Match.AppendLiteral(alias, field, type2,
                toBooleanLiteral(value));
    }

    private String toBooleanLiteral(boolean value) {
        switch (getDatabase()) {
        case JdbcProperties.DB2 :
        case JdbcProperties.ORACLE :
        case JdbcProperties.MYSQL :
        case JdbcProperties.MSSQL :
        case JdbcProperties.FIREBIRD :
            return value ? " != 0" : " = 0";
        default:
            return value ? " = true" : " = false";
        }
    }

    public Match addListOfUidMatch(String alias, String field, boolean type2,
            String[] uids) {
        return addListOfStringMatch(alias, field, type2, uids);
    }

    public Match addListOfStringMatch(String alias, String field, boolean type2,
            String[] vals) {
        return addMatch(new Match.ListOfString(alias, field, type2, vals));
    }

    public Match addWildCardMatch(String alias, String field, boolean type2,
            String wc) {
        if (wc == null || wc.length() == 0 || wc.equals("*"))
            return null;
        return addMatch(new Match.WildCard(alias, field, type2, wc));    
    }

    public Match addWildCardMatch(String alias, String field, boolean type2,
            String[] vals) {
        if ( vals == null || vals.length == 0 ) return null;
        if ( vals.length > 1 ) {
            if ( containsWildCard(vals) ) {
                matchNotSupported = true;
                return null;
            }
            return addListOfStringMatch(alias, field, type2, vals);
        }
        return addWildCardMatch(alias, field, type2, vals[0]);  
    }

    /**
     * @param wc
     * @return
     */
    private boolean containsWildCard(String[] wc) {
        for ( int i = 0 ; i < wc.length ; i++ ) {
            if ( wc[i].indexOf('*') != -1 || wc[i].indexOf('?') != -1) 
                return true;
        }
        return false;
    }

    public void addPNMatch(String[] nameFields, boolean type2, boolean icase,
            String val) {
        if (val == null || val.length() == 0 || val.equals("*"))
            return;
        PersonName pn = new PersonName(val);//.getInstance().newPersonName(val);
        String pnalpha = pn.toString();//pn.toComponentGroupMatch();
        addWildCardMatch(null, nameFields[0], type2,
                icase ? pnalpha.toUpperCase() : pnalpha);
//        PersonName ipn = pn.getIdeographic();
//        if (ipn != null) {
//            addWildCardMatch(null, nameFields[1], type2,
//                    ipn.toComponentGroupMatch());
//        }
//        PersonName ppn = pn.getPhonetic();
//        if (ppn != null) {
//            addWildCardMatch(null, nameFields[2], type2,
//                    ppn.toComponentGroupMatch());
//        }
    }   


    public void addPNFuzzyMatch(String[] fields, boolean type2, String val) {
        if (val == null || val.length() == 0 || val.equals("*"))
            return;
        addMatch(new Match.PNFuzzy(fields, type2,new PersonName(val)));
    }

    /**
     * Returns true if at least one match is not supported. 
     * @return matchNotSupported flag.
     */
    public boolean isMatchNotSupported() {
        return matchNotSupported;
    }

    public Match addRangeMatch(String alias, String field, boolean type2,
            Date[] range) {
        return addMatch(getRangeMatch(alias, field, type2, range) );
    }

    public Match getRangeMatch(String alias,  String field, boolean type2,
            Date[] range) {
        return new Match.Range(alias, field, type2,
                range, getDatabase() == JdbcProperties.ORACLE ?
                        ORA_DATE_FORMAT : DATE_FORMAT);
    }

    public Match addRangeMatch(String alias, String field, boolean type2,
            String range) {
        if (range == null) {
            return null;
        }
        int hypen = range.indexOf('-');
        if (hypen == -1) {
            return addWildCardMatch(alias, field, type2, range);
        }
        return addMatch(new Match.StringRange(alias, field, type2, new String[]{
                hypen != 0 ? range.substring(0, hypen) : null,
                        hypen+1 < range.length() ? range.substring(hypen+1) : null
        }) );
    }

    public Match addModalitiesInStudyNestedMatch(String alias, String[] mds) {
        return ( mds != null && mds.length == 1 )  
        ? addMatch(new Match.ModalitiesInStudyNestedMatch(alias, mds[0]))
                : addMatch(new Match.ModalitiesInStudyMultiNestedMatch(alias, mds));
    }

    public Match addCallingAETsNestedMatch(boolean privateTables, String[] callingAETs) {
        return addMatch(new Match.CallingAETsNestedMatch( privateTables, callingAETs));
    }

    public Match addQueryPermissionNestedMatch(boolean patientLevel, 
            boolean checkForNoStudy, String[] roles) {
        return addMatch(new Match.QueryPermissionNestedMatch(
                patientLevel, checkForNoStudy, roles));
    }

    public Match.Node addNodeMatch(String orORand, boolean invert) {
        Match.Node m = new Match.Node(orORand, invert);
        addMatch( m );
        return m;
    }

    public Match addCorrelatedSubquery( SqlBuilder subQuery ) {
        return addMatch( new Match.Subquery(subQuery, null, null) );
    }

    public Match addUncorrelatedSubquery( SqlBuilder subQuery, String field, String alias ) {
        return addMatch( new Match.Subquery(subQuery, field, alias) );
    }

    public String getSql() {
        if (select == null)
            throw new IllegalStateException("select not initalized");
        if (from == null)
            throw new IllegalStateException("from not initalized");

        StringBuffer sb = new StringBuffer("SELECT ");
        if (limit > 0 || offset > 0) {
            appendLimitbeforeFrom(sb);
        } else {
            if (distinct) sb.append("DISTINCT ");
            appendTo(sb, select);            
        }
        sb.append(" FROM ");
        appendInnerJoinsToFrom(sb);
        appendLeftJoinToFrom(sb);
        whereOrAnd = WHERE;
        appendInnerJoinsToWhere(sb);
        appendLeftJoinToWhere(sb);
        appendMatchesTo(sb);
        if (!orderby.isEmpty()) {
            sb.append(" ORDER BY ");
            appendTo(sb, orderby.toArray(new String[orderby.size()]));
        }
        if (groupBy != null) {
            sb.append(groupBy);
        }
        if (limit > 0 || offset > 0) {
            appendLimitAtEnd(sb);
        }
        if (getDatabase() == JdbcProperties.DB2 && !subQueryMode)
            sb.append(" FOR READ ONLY");
        return sb.toString();
    }

    public StringBuffer appendLimitbeforeFrom(StringBuffer sb) {
        if (distinct && getDatabase()!= JdbcProperties.FIREBIRD 
                && getDatabase()!= JdbcProperties.HSQL) 
            sb.append("DISTINCT ");
        switch (getDatabase()) {
        case JdbcProperties.HSQL :
            sb.append("LIMIT ");
            sb.append(offset);
            sb.append(" ");
            sb.append(limit);
            sb.append(" ");
            if (distinct) sb.append("DISTINCT ");
            appendTo(sb, select);
            break;
        case JdbcProperties.DB2 :
            appendTo(sb, selectC1C2CN());
            sb.append(" FROM ( SELECT ");
            appendTo(sb, select);
            sb.append(", ROW_NUMBER() OVER (ORDER BY ");
            appendTo(sb, orderby.toArray(new String[orderby.size()]));
            sb.append(") AS rownum ");
            break;
        case JdbcProperties.ORACLE :
            appendTo(sb, selectC1C2CN());
            sb.append(" FROM ( SELECT ");
            appendTo(sb, selectC1C2CN());
            sb.append(", ROWNUM as r1 FROM ( SELECT ");
            appendTo(sb, selectAsC1C2CN());
            break;
        case JdbcProperties.MSSQL :
            if (!orderby.isEmpty()) {
                appendTo(sb, selectC1C2CN());
                sb.append(" FROM ( SELECT TOP ").append(limit).append(' ');
                appendTo(sb, selectC1C2CN());
                sb.append(',');
                appendTo(sb, selectSort());
                sb.append(" FROM ( SELECT TOP ").append(limit+offset).append(' ');
                appendTo(sb, selectAsC1C2CN());
                sb.append(',');
                appendTo(sb, selectOrderByAsSort());
            } else {
                throw new IllegalArgumentException("LIMIT OFFSET feature needs order by in MS SQL Server!!");
            }                    
            break;
        case JdbcProperties.FIREBIRD :
            sb.append("FIRST ");
            sb.append(limit);
            sb.append(" SKIP ");
            sb.append(offset);
            sb.append(" ");
            if (distinct) sb.append("DISTINCT ");
            appendTo(sb, select);
            break;
        default:
            appendTo(sb, select);
        break;
        }
        return sb;
    }

    public StringBuffer appendLimitAtEnd(StringBuffer sb) {
        switch (getDatabase()) {
        case JdbcProperties.PSQL :
        case JdbcProperties.MYSQL :
            sb.append(" LIMIT ");
            sb.append(limit);
            sb.append(" OFFSET ");
            sb.append(offset);
            break;
        case JdbcProperties.DB2 :
            sb.append(" ) AS foo WHERE rownum > ");
            sb.append(offset);
            sb.append(" AND rownum <= ");
            sb.append(offset + limit);
            break;
        case JdbcProperties.ORACLE :
            sb.append(" ) WHERE ROWNUM <= ");
            sb.append(offset + limit);
            sb.append(" ) WHERE r1 > ");
            sb.append(offset);
            break;
        case JdbcProperties.MSSQL:
            sb.append(") AS loTemp1 ORDER BY ");
            appendTo(sb,getOrderByWithSort(true));
            sb.append(") AS loTemp2 ORDER BY ");
            appendTo(sb,getOrderByWithSort(false));
            break;
        }
        return sb;
    }

    private String[] getOrderByWithSort(boolean invert) {
        String[] inverted = new String[ orderby.size() ];
        int pos;
        for ( int i=0 ; i < inverted.length ; i++) {
            pos = orderby.get(i).lastIndexOf(ASC);
            inverted[i] = "sort"+(i+1)+ ((pos==-1 ^ invert ) ? DESC : ASC);
        }
        return inverted;
    }


    private String[] selectC1C2CN() {
        String[] retval = new String[select.length]; 
        for (int i = 0; i < retval.length; i++)
            retval[i] = "c" + (i+1);
        return retval;
    }

    private String[] selectAsC1C2CN() {
        String[] retval = new String[select.length]; 
        for (int i = 0; i < retval.length; i++)
            retval[i] = select[i] + " AS c" + (i+1);
        return retval;
    }

    private String[] selectSort() {
        String[] retval = new String[orderby.size()]; 
        for (int i = 0; i < retval.length; i++)
            retval[i] = "sort" + (i+1);
        return retval;
    }
    private String[] selectOrderByAsSort() {
        String[] retval = new String[orderby.size()];
        String s;
        for (int i = 0; i < retval.length; i++) {
            s = orderby.get(i);
            retval[i] =  s.substring(0,s.lastIndexOf(' '))+ " AS sort" + (i+1);
        }
        return retval;
    }

    private StringBuffer appendTo(StringBuffer sb, String[] a) {
        for (int i = 0; i < a.length; i++) {
            if (i > 0)
                sb.append(", ");
            sb.append(a[i]);
        }
        return sb;
    }

    private void appendLeftJoinToFrom(StringBuffer sb) {
        if (leftJoin == null) return;
        for (int i = 0, n = leftJoin.length/4; i < n; ++i) {
            final int i4 = 4*i;
            if (getDatabase() == JdbcProperties.ORACLE) {
                sb.append(", ");
                sb.append(leftJoin[i4]);
                if (leftJoin[i4+1] != null) {
                    sb.append(" ");
                    sb.append(leftJoin[i4+1]);
                }
            } else {
                sb.append(" LEFT JOIN ");
                sb.append(leftJoin[i4]);
                if (leftJoin[i4+1] != null) {
                    sb.append(" AS ");
                    sb.append(leftJoin[i4+1]);
                }
                sb.append(" ON (");
                sb.append(leftJoin[i4+2]);
                sb.append(" = ");
                sb.append(leftJoin[i4+3]);
                sb.append(")");
            }
        }
    }

    private void appendLeftJoinToWhere(StringBuffer sb) {
        if (leftJoin == null || getDatabase() != JdbcProperties.ORACLE) return;
        for (int i = 0, n = leftJoin.length/4; i < n; ++i) {
            final int i4 = 4*i;
            sb.append(whereOrAnd);
            whereOrAnd = AND;
            sb.append(leftJoin[i4+2]);
            sb.append(" = ");
            sb.append(leftJoin[i4+3]);
            sb.append("(+)");
        }
    }

    private void appendInnerJoinsToFrom(StringBuffer sb) {
        if (relations == null || getDatabase() == JdbcProperties.ORACLE) {
            for (int i = 0; i < from.length; i++) {
                if (i > 0)
                    sb.append(", ");
                sb.append(from[i]);
                if (aliases != null && aliases[i] != null)
                    sb.append(' ').append(aliases[i]);
            }
        } else {
            sb.append(from[0]);
            for (int i = 0, n = relations.length/2; i < n; ++i) {
                final int i2 = 2*i;
                sb.append(" INNER JOIN ");
                sb.append(from[i+1]);
                sb.append(" ON (");
                sb.append(relations[i2]);
                sb.append(" = ");
                sb.append(relations[i2+1]);
                sb.append(")");
            }
        }
    }

    private void appendInnerJoinsToWhere(StringBuffer sb) {
        if (relations == null || getDatabase() != JdbcProperties.ORACLE) return;
        for (int i = 0, n = relations.length/2; i < n; ++i) {
            final int i2 = 2*i;
            sb.append(whereOrAnd);
            whereOrAnd = AND;
            sb.append(relations[i2]);
            sb.append(" = ");
            sb.append(relations[i2+1]);
        }
    }

    private void appendMatchesTo(StringBuffer sb) {
        if (matches == null) return;
        for (int i = 0; i < matches.size(); i++) {
            sb.append(whereOrAnd);
            whereOrAnd = AND;
            matches.get(i).appendTo(sb);
        }
    }

    public static boolean isCodeMatch(Attributes item) {
        return item != null && !item.isEmpty()
                && (item.containsValue(Tags.CodeValue)
                        || item.containsValue(Tags.CodingSchemeDesignator)
                        || item.containsValue(Tags.CodingSchemeVersion));
    }

    public boolean addCodeMatch(String alias, Attributes item) {
        if (item == null || item.isEmpty())
            return false;

        boolean universalMatch = addSingleValueMatch(alias,
                "Code.codeValue", SqlBuilder.TYPE1,
                item.getString(Tags.CodeValue)) == null;
        universalMatch = addSingleValueMatch(alias,
                "Code.codingSchemeDesignator", SqlBuilder.TYPE1,
                item.getString(Tags.CodingSchemeDesignator)) == null
                && universalMatch;
        universalMatch = addSingleValueMatch(alias,
                "Code.codingSchemeVersion", SqlBuilder.TYPE1,
                item.getString(Tags.CodingSchemeVersion)) == null
                && universalMatch;
        return !universalMatch;
    }

    public void addNestedCodeMatch(String[] parentRelations,
            String[] tables, String[] relations, Attributes item, boolean type2) {

        if (item == null || item.isEmpty())
            return;

        SqlBuilder subQuery = new SqlBuilder();
        subQuery.setSelect1();
        subQuery.setFrom(tables);
        subQuery.setRelations(relations);
        if (!subQuery.addCodeMatch(null, item))
            return;
        
        Match match = new Match.Subquery(subQuery, null, null);
        if (!type2) {
            addMatch(match);
        } else {
            SqlBuilder subQuery2 = new SqlBuilder();
            subQuery2.setSelect1();
            subQuery2.setFrom(new String[] { tables[0] });
            subQuery2.addFieldValueMatch(null, parentRelations[0], false, null, 
                    parentRelations[1]);
            Match match2 = new Match.Subquery(subQuery2, null, null);
            Node notNode = new  Match.Node(null, true);
            notNode.addMatch(match2);
            Node orMatch = addNodeMatch(" OR", false);
            orMatch.addMatch(match);
            orMatch.addMatch(notNode);
        }
    }

    public void addRefSOPMatch(String[] parentRelations, String table,
            String cuidField, String iuidField, Attributes item, boolean type2) {

        if (item == null || item.isEmpty())
            return;

        SqlBuilder subQuery = new SqlBuilder();
        subQuery.setSelect1();
        String[] tables = { table };
        subQuery.setFrom(tables);
        subQuery.addFieldValueMatch(null, parentRelations[0], false, null, 
                parentRelations[1]);
        boolean universalMatch = subQuery.addSingleValueMatch(null,
                cuidField, SqlBuilder.TYPE1,
                item.getString(Tags.RefSOPClassUID)) == null;
        universalMatch = subQuery.addSingleValueMatch(null,
                iuidField, SqlBuilder.TYPE1,
                item.getString(Tags.RefSOPInstanceUID)) == null
                && universalMatch;
        if (universalMatch)
            return;
        
        Match match = new Match.Subquery(subQuery, null, null);
        if (!type2) {
            addMatch(match);
        } else {
            SqlBuilder subQuery2 = new SqlBuilder();
            subQuery2.setSelect1();
            subQuery2.setFrom(tables);
            subQuery2.addFieldValueMatch(null, parentRelations[0], false, null, 
                    parentRelations[1]);
            Match match2 = new Match.Subquery(subQuery2, null, null);
            Node notNode = new  Match.Node(null, true);
            notNode.addMatch(match2);
            Node orMatch = addNodeMatch(" OR", false);
            orMatch.addMatch(match);
            orMatch.addMatch(notNode);
        }
    }
}
