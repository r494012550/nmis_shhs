package com.healta.model;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dialect.OracleDialect;
import com.jfinal.plugin.activerecord.dialect.SqlServerDialect;
import com.jfinal.plugin.activerecord.generator.MetaBuilder;
import com.jfinal.plugin.activerecord.generator.TableMeta;
 
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
 
/**
 * Created by wendell on 2017/1/25.
 */
public class _SqlMetaBuilder extends MetaBuilder {
    protected Set<String> containTables = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
    public _SqlMetaBuilder(DataSource dataSource) {
        super(dataSource);
    }
 
    @Override
    protected ResultSet getTablesResultSet() throws SQLException {
        setDialect(new SqlServerDialect());
        String schemaPattern = dialect instanceof OracleDialect ? dbMeta.getUserName() : null;
        ResultSet rs = dbMeta.getTables(conn.getCatalog(), schemaPattern, null, new String[]{"TABLE"});
 
        return rs;
    }
    @Override
    protected void buildTableNames(List<TableMeta> ret) throws SQLException {
        ResultSet rs = getTablesResultSet();
        while (rs.next()) {
            String schem = rs.getString("TABLE_SCHEM");
            String tableName = rs.getString("TABLE_Name");
 
//            if (schem.equals("sys")) {
//                System.out.println("Skip table :" + tableName + "，这是Sqlserver生成的表");
//                continue;
//            }
//            if (excludedTables.contains(tableName)) {
//                System.out.println("Skip table :" + tableName);
//                continue;
//            }
//            if (isSkipTable(tableName)) {
//                System.out.println("Skip table :" + tableName);
//                continue;
//            }
            if("users".equals(tableName)) { //修改表名
	            TableMeta tableMeta = new TableMeta();
	            tableMeta.name = tableName;
	            tableMeta.remarks = rs.getString("REMARKS");
	 
	            tableMeta.modelName = buildModelName(tableName);
	            tableMeta.baseModelName = buildBaseModelName(tableMeta.modelName);
	            ret.add(tableMeta);
            }
        }
        rs.close();
    }
 
    protected void buildPrimaryKey(TableMeta tableMeta) throws SQLException {
        ResultSet rs = dbMeta.getPrimaryKeys(conn.getCatalog(), null, tableMeta.name);
 
        String primaryKey = "";
        int index = 0;
        while (rs.next()) {
            if (index++ > 0) {
                primaryKey += ",";
            }
            primaryKey += rs.getString("COLUMN_NAME");
        }
        if (StrKit.isBlank(primaryKey)) {
            throw new RuntimeException("primaryKey required by active record pattern，Table Name:" + tableMeta.name);
        }
        tableMeta.primaryKey = primaryKey;
        rs.close();
    }
}