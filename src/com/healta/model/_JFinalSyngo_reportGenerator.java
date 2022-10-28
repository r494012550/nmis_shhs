package com.healta.model;

import javax.sql.DataSource;


import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.CaseInsensitiveContainerFactory;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.activerecord.dialect.SqlServerDialect;
import com.jfinal.plugin.activerecord.generator.Generator;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import com.jfinal.plugin.druid.DruidPlugin;

/**
 * 在数据库表有任何变动时，运行一下 main 方法，极速响应变化进行代码重构
 */
public class _JFinalSyngo_reportGenerator {
	
	public static DataSource getDataSource() {
		PropKit.use("config.properties");
//		C3p0Plugin c3p0Plugin = new C3p0Plugin(PropKit.get("jdbcUrl"), PropKit.get("user"), PropKit.get("password"));
//		c3p0Plugin.setDriverClass("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//		c3p0Plugin.start();

		DruidPlugin dbPlugin=new DruidPlugin(PropKit.get("jdbcUrl").trim(), PropKit.get("user").trim(), PropKit.get("password").trim());
		dbPlugin.setDriverClass("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		dbPlugin.start();
		return dbPlugin.getDataSource();
	}
	
	public static void main(String[] args) {
		// base model 所使用的包名
		String baseModelPackageName = "com.healta.model.base";
		// base model 文件保存路径
		String baseModelOutputDir = PathKit.getWebRootPath() + "/../src/com/healta/model/base";
		
		// model 所使用的包名 (MappingKit 默认使用的包名)
		String modelPackageName = "com.healta.model";
		// model 文件保存路径 (MappingKit 与 DataDictionary 文件默认保存路径)
		String modelOutputDir = baseModelOutputDir + "/..";
		
		// 创建生成器
		Generator gernerator = new Generator(getDataSource(), baseModelPackageName, baseModelOutputDir, modelPackageName, modelOutputDir);
		// 添加不需要生成的表名
		gernerator.addExcludedTable("adv","examitemdic","department","employee","equip_group","id1","id2","id3","modalitydic","patient","report","reporttemplate","reporttemplatenode","role",
				"studyitem","studyorder","syscode","userrole","users","resource","authority","authority_resource","role_authority","clinicalcode","favorites",
				"filter","organdic","requestimage","srcomponent","srtemplate","structclassify","structsection","structsectionitem","structtemplateitem",
				"structtemplatereport","study_image","xslttemplate","syngoviareport","syngoviaimage","reportrace","structreport","regex","syngoviasrdata",
				"syngoviafindingfile","synctime","favoritesreport","srcomponentoption","modules","role_module","userprofiles","statistics","ae","mwlitem",
				"admission","eorder","eorderitem","organdic","exam_equip","label"
				);
		
		// 设置是否在 Model 中生成 dao 对象
		gernerator.setGenerateDaoInModel(true);
		// 设置是否生成字典文件
		gernerator.setGenerateDataDictionary(false);
		// 设置需要被移除的表名前缀用于生成modelName。例如表名 "osc_user"，移除前缀 "osc_"后生成的model名为 "User"而非 OscUser
		gernerator.setRemovedTableNamePrefixes("t_");
		
		gernerator.setDialect(new SqlServerDialect());
//		gernerator.setDialect(new MysqlDialect());
//		gernerator.setDialect(new MySqlServerDialect());
		gernerator.setMetaBuilder(new _SqlMetaBuilder(getDataSource()));
		// 生成
		gernerator.generate();
	}
}




