package com.healta.config;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import com.healta.constant.DbConfigName;
import com.healta.constant.Version;
import com.healta.controller.AdminController;
import com.healta.controller.ApiController;
import com.healta.controller.CallingController;
import com.healta.controller.ChatController;
import com.healta.controller.ColorController;
import com.healta.controller.ComGoodsController;
import com.healta.controller.ComGoodsVarietyController;
import com.healta.controller.ProfileController;
import com.healta.controller.PurchaseConrtoller;
import com.healta.controller.DicController;
import com.healta.controller.DistributionController;
import com.healta.controller.ExamineController;
import com.healta.controller.FollowupController;
import com.healta.controller.FrontCacheController;
import com.healta.controller.FrontCommonController;
import com.healta.controller.ImageController;
import com.healta.controller.ImportTSRDataController;
import com.healta.controller.InjectionController;
import com.healta.controller.ModalitydicController;
import com.healta.controller.NoticeController;
import com.healta.controller.OpenActionController;
import com.healta.controller.PatientController;
import com.healta.controller.PrintController;
import com.healta.controller.RegisterController;
import com.healta.controller.ReportController;
import com.healta.controller.ReportTemplateController;
import com.healta.controller.ResearchController;
import com.healta.controller.SRReportController;
import com.healta.controller.SRTemplateController;
import com.healta.controller.SaleController;
import com.healta.controller.ScheduleController;
import com.healta.controller.StatisticsController;
import com.healta.controller.StockController;
import com.healta.controller.SysCodeController;
import com.healta.controller.SystemConfigController;
import com.healta.controller.UserController;
import com.healta.controller.VerifyLicenseController;
import com.healta.controller.InquiringController;
import com.healta.controller.WebViewController;
import com.healta.controller.WorkListController;
import com.healta.controller.WorkforceController;
import com.healta.handler.WebSocketHandler;
import com.healta.license.VerifyLicense;
import com.healta.model._MappingKit;
import com.healta.plugin.activemq.ActiveMQPlugin;
import com.healta.plugin.cron4j.MyCron4jPlugin;
import com.healta.plugin.dcm.DcmQRPlugin;
import com.healta.plugin.dcm.DcmStoreScpPlugin;
import com.healta.plugin.hl7.HL7SendPlugin;
import com.healta.plugin.hl7.HL7ServerPlugin;
import com.healta.plugin.queueup.QueuingUpPlugin;
import com.healta.plugin.shiro.ShiroPlugin;
import com.healta.plugin.workforce.WorkforcePlugin;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.Const;
import com.jfinal.core.JFinal;
import com.jfinal.ext.handler.ContextPathHandler;
import com.jfinal.json.MixedJsonFactory;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.CaseInsensitiveContainerFactory;
import com.jfinal.plugin.activerecord.OrderedFieldContainerFactory;
import com.jfinal.plugin.activerecord.dialect.OracleDialect;
import com.jfinal.plugin.activerecord.dialect.SqlServerDialect;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import com.jfinal.plugin.cron4j.Cron4jPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.render.ViewType;
import com.jfinal.template.Engine;
import com.jfinal.template.source.ClassPathSourceFactory;


public class MainConfig extends JFinalConfig {
	
	public static C3p0Plugin c3p0Plugin;
	public static DruidPlugin mainDbPlugin;
	public static MyCron4jPlugin cron4jPlugin;
	/**
	 * 全局配置
	 */
	@Override
	public void configConstant(Constants me) {
		// 读取数据库配置文件
		PropKit.use("config.properties");
		// 设置当前是否为开发模式
		me.setDevMode(PropKit.getBoolean("devMode"));
		// 设置默认上传文件保存路径 getFile等使用
		me.setBaseUploadPath(PropKit.get("base_upload_path"));
		// 设置上传最大限制尺寸
		// me.setMaxPostSize(1024*1024*10);
		// 设置默认下载文件路径 renderFile使用
		me.setBaseDownloadPath("download");
		// 设置默认视图类型
		me.setViewType(ViewType.JFINAL_TEMPLATE);
		// 设置404渲染视图
		// me.setError404View("404.html");
		// 设置json工厂
		me.setJsonFactory(MixedJsonFactory.me());
		//me.setJsonDatePattern("yyyy-MM-dd HH:mm:ss");
		me.setMaxPostSize(10 * Const.DEFAULT_MAX_POST_SIZE);
		
		me.setDenyAccessJsp(false);
		
	}

	/**
	 * 路由配置
	 */
	@Override
	public void configRoute(Routes me) {
		VerifyLicense vl=new VerifyLicense("verifyLicParam.properties","rebound_nmis");
//		vl.setParam("verifyLicParam.properties");
		if(vl.verify()){
			VerifyLicense.checkFunction("user","/", UserController.class,me,null);
			VerifyLicense.checkFunction("patient","/patient", PatientController.class,me,null);
			VerifyLicense.checkFunction("report","/report", ReportController.class,me,null);
			VerifyLicense.checkFunction("template","/template", ReportTemplateController.class,me,null);
			VerifyLicense.checkFunction("modalitydic","/modalitydic", ModalitydicController.class,me,null);
			VerifyLicense.checkFunction("worklist","/worklist", WorkListController.class,me,null);
			VerifyLicense.checkFunction("srtemplate","/srtemplate", SRTemplateController.class,me,null);
			VerifyLicense.checkFunction("register","/register", RegisterController.class,me,null);
			VerifyLicense.checkFunction("syscode","/syscode", SysCodeController.class,me,null);
			VerifyLicense.checkFunction("admin","/admin", AdminController.class,me,"/view/admin");
			VerifyLicense.checkFunction("image","/image", ImageController.class,me,null);
			VerifyLicense.checkFunction("print","/print", PrintController.class,me,null);
			VerifyLicense.checkFunction("system","/system", SystemConfigController.class,me,null);
			VerifyLicense.checkFunction("profile","/profile", ProfileController.class,me,null);
			VerifyLicense.checkFunction("statistics","/statistics", StatisticsController.class,me,null);
			VerifyLicense.checkFunction("examine","/examine", ExamineController.class,me,null);
			VerifyLicense.checkFunction("chat","/chat", ChatController.class,me,null);
			VerifyLicense.checkFunction("schedule","/schedule", ScheduleController.class,me,null);
			VerifyLicense.checkFunction("color","/color", ColorController.class,me,null);
			VerifyLicense.checkFunction("dic","/dic", DicController.class,me,null);
			VerifyLicense.checkFunction("frontcommon","/frontcommon", FrontCommonController.class,me,null);
			VerifyLicense.checkFunction("cache","/cache", FrontCacheController.class,me,null);
			VerifyLicense.checkFunction("srreport","/srreport", SRReportController.class,me,null);
			VerifyLicense.checkFunction("webview","/webview", WebViewController.class,me,null);
			VerifyLicense.checkFunction("distribution","/distribution", DistributionController.class,me,null);
			VerifyLicense.checkFunction("workforce","/workforce", WorkforceController.class,me,null);
			VerifyLicense.checkFunction("inquiring","/inquiring", InquiringController.class,me,null);//问诊
			VerifyLicense.checkFunction("injection","/injection", InjectionController.class,me,null);//注射
			VerifyLicense.checkFunction("research","/research", ResearchController.class,me,null);//课程
			//VerifyLicense.checkFunction("comGoods","/comGoods", ComGoodsController.class,me,null);
			me.add("notice", NoticeController.class);
			me.add("openaction",OpenActionController.class);
			me.add("comGoods", ComGoodsController.class);
			me.add("goodsVariety", ComGoodsVarietyController.class);
			me.add("calling",CallingController.class);
			me.add("purchase", PurchaseConrtoller.class);
			me.add("stock", StockController.class);
			me.add("sale", SaleController.class);
			me.add("importsrdata", ImportTSRDataController.class);
			me.add("api", ApiController.class);
			me.add("research", ResearchController.class);
		}
		else{
			me.add("/", VerifyLicenseController.class);
		}
		
	}

	/**
	 * 模板引擎配置
	 */
	@Override
	public void configEngine(Engine me) {
		me.setDevMode(true);
	}

	/**
	 * 数据库连接配置
	 */
	@Override
	public void configPlugin(Plugins me) {
		// 配置数据库连接池插件
//		C3p0Plugin dbPlugin = new C3p0Plugin(PropKit.get("jdbcUrl"), PropKit.get("user"), PropKit.get("password"));
//		this.c3p0Plugin = dbPlugin;
		DruidPlugin dbPlugin=new DruidPlugin(PropKit.get("jdbcUrl"), PropKit.get("user"), PropKit.get("password"));
		dbPlugin.set(PropKit.getInt("initialSize",10), PropKit.getInt("minIdle",20), PropKit.getInt("maxActive",200));
		mainDbPlugin=dbPlugin;
		me.add(dbPlugin);
		// orm映射 配置ActiveRecord插件
		ActiveRecordPlugin arp = new ActiveRecordPlugin(DbConfigName.MSSQL,dbPlugin);
		arp.getEngine().setSourceFactory(new ClassPathSourceFactory());
		arp.addSqlTemplate("all.sql");
		arp.setShowSql(PropKit.getBoolean("devMode"));
//				arp.setDialect(new MysqlDialect());
		arp.setContainerFactory(new CaseInsensitiveContainerFactory(true));
//				arp.setTransactionLevel(Connection.TRANSACTION_READ_COMMITTED);
		/******** 在此添加数据库 表-Model 映射 *********/
		_MappingKit.mapping(arp);
		arp.setDialect(new SqlServerDialect());
		// 添加到插件列表中
		me.add(arp);
		
		ActiveRecordPlugin arp1 = new ActiveRecordPlugin(DbConfigName.MSSQL_ST,dbPlugin);
		arp1.getEngine().setSourceFactory(new ClassPathSourceFactory());
		arp1.addSqlTemplate("all.sql");
		arp1.setShowSql(PropKit.getBoolean("devMode"));
		arp1.setContainerFactory(new OrderedFieldContainerFactory());
		//arp.setTransactionLevel(Connection.TRANSACTION_SERIALIZABLE);
		/******** 在此添加数据库 表-Model 映射 *********/
		_MappingKit.mapping(arp1);
		arp1.setDialect(new SqlServerDialect());
		// 添加到插件列表中
		me.add(arp1);
		
		if(PropKit.getBoolean("enable_matrix_dicom_db", false)) {
			DruidPlugin matrixDBPlugin=new DruidPlugin(PropKit.get("matrixJdbcUrl"), PropKit.get("matrixUser"), PropKit.get("matrixPassword"));
			me.add(matrixDBPlugin);
			ActiveRecordPlugin arp_matrix = new ActiveRecordPlugin(DbConfigName.MSSQL_MATRIX_DICOM,matrixDBPlugin);
			arp_matrix.getEngine().setSourceFactory(new ClassPathSourceFactory());
//			arp_matrix.addSqlTemplate("all.sql");
			arp_matrix.setShowSql(PropKit.getBoolean("devMode"));
			arp_matrix.setContainerFactory(new OrderedFieldContainerFactory());
			//arp.setTransactionLevel(Connection.TRANSACTION_SERIALIZABLE);
			/******** 在此添加数据库 表-Model 映射 *********/
			_MappingKit.mappingMatrix(arp_matrix);
			arp_matrix.setDialect(new SqlServerDialect());
			me.add(arp_matrix);
		}
		
		//EhCachePlugin
		me.add(new EhCachePlugin(PathKit.getRootClassPath()+"\\ehcache.xml"));
		
		// 配置shiro插件
		me.add(new ShiroPlugin());
		//HL7ServerPlugin
		if(StrKit.equals("1", PropKit.use("system.properties").get("enable_dcmqr"))) {
			me.add(new DcmQRPlugin(PropKit.use("system.properties").get("dcmqr_command")));
		}
		
		if(StrKit.equals("1", PropKit.use("system.properties").get("enable_storescp"))) {
			me.add(new DcmStoreScpPlugin(PropKit.use("system.properties").get("storescp_command")+" "+PropKit.use("system.properties").get("online_path")));
		}
		if(StrKit.equals("1", PropKit.use("system.properties").get("enable_hl7_server","0"))) {
			me.add(new HL7ServerPlugin(PropKit.use("system.properties").get("hl7_command")));
		}
		
		me.add(new HL7SendPlugin());
		cron4jPlugin=new MyCron4jPlugin("task.properties");
		me.add(cron4jPlugin);
		
		if("1".equals(PropKit.use("system.properties").get("enable_activemq"))){
			me.add(new ActiveMQPlugin(PropKit.use("system.properties").get("activemq_brokerurl")));
		}
		if(StrKit.equals("1", PropKit.use("system.properties").get("enable_call_plugin","0"))) {
			me.add(new QueuingUpPlugin());
		}
		//me.add(new SequencePlugin());
		if(VerifyLicense.hasFunction("workforce")) {
			me.add(new WorkforcePlugin());
		}
	}

	/**
	 * 拦截器配置
	 */
	@Override   
	public void configInterceptor(Interceptors me) {
		//me.addGlobalActionInterceptor(new AuthInterceptor());
		
	}

	/**
	 * 处理器配置
	 */
	@Override
	public void configHandler(Handlers me) {
		me.add(new ContextPathHandler("ctx"));
		me.add(new WebSocketHandler("^/websocket"));
	}
	
	
	/**
	 * Call back after JFinal start
	 */
	@Override
	public void afterJFinalStart(){
		//生成版本号
		//Version.SUBVERSION=LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"))+"";
		Version.VERSION=PropKit.use("system.properties").get("version")+(PropKit.getBoolean("devMode")?LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8")):"");
//		 org.apache.log4j.LogManager.resetConfiguration();
//	     org.apache.log4j.PropertyConfigurator.configure("log4j.properties");
		//初始化缓存
		InitializeCache.init();
		//系统初始化时新建报告医生、审核医生、技师等角色
		SystemCheck.check();
	}
	

}
