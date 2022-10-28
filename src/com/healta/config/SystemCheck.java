package com.healta.config;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import com.healta.model.ChatGroup;
import com.healta.model.ChatGroupUser;
import com.healta.model.Reporttemplatenode;
import com.healta.model.StudyMethodTemplate;
import com.healta.model.Syscode;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class SystemCheck {
	private static final Logger log = Logger.getLogger(SystemCheck.class);
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static void check(){
		
		log.info("starting system check......");
		SystemCheck check=new SystemCheck();
		check.check_TemplateNode();
		//if(VerifyLicense.hasModule("chat")){
			//check.checkChatGroup();
		//}
		check.unlockAllReport();
		check.checkStudyMethodNode();
		check.checkModalityTypeTable();
		log.info("end system check......");
	}

	public boolean check_TemplateNode(){
		
//		List<Syscode> codes=SyscodeKit.getInstance().getCodes("0004");
		
		List<Record> list=Db.find("select syscode.code,reporttemplatenode.* from syscode "
				+ "left join reporttemplatenode on syscode.code=reporttemplatenode.modality and reporttemplatenode.parent=0 "
				+ "where syscode.type='0004' and syscode.parent!=0;");
		
		for(Record re:list){
			log.info(re.getStr("nodename"));
			
			if(StrKit.isBlank(re.getStr("nodename"))){
				Reporttemplatenode node_pub=new Reporttemplatenode();
				node_pub.setCreator(0);
				node_pub.setIspublic("1");
				node_pub.setModality(re.getStr("code"));
				node_pub.setNodename(re.getStr("code"));
				node_pub.setParent(0);
				node_pub.save();
				log.info("add node:"+node_pub.toString());
				Reporttemplatenode node_pri=new Reporttemplatenode();
				node_pri.setCreator(0);
				node_pri.setIspublic("0");
				node_pri.setModality(re.getStr("code"));
				node_pri.setNodename("个人模板");
				node_pri.setParent(node_pub.getId());
				node_pri.save();
				log.info("add node:"+node_pri.toString());
			}
			else{
				Record prinode=Db.findFirst("select * from reporttemplatenode "
						+ "where parent=? and ispublic='0' and creator=0",re.getInt("id"));
				if(prinode==null){
					Reporttemplatenode node_pri=new Reporttemplatenode();
					node_pri.setCreator(0);
					node_pri.setIspublic("0");
					node_pri.setModality(re.getStr("code"));
					node_pri.setNodename("个人模板");
					node_pri.setParent(re.getInt("id"));
					node_pri.save();
					log.info("add node:"+node_pri.toString());
				}
			}
		}
		
		return true;
	}
	
	public void unlockAllReport() {
		Db.update("update report set islocking=0 , lockingpeople =null where createtime >? and islocking=1",LocalDate.now().minusDays(3).format(DateTimeFormatter.ISO_LOCAL_DATE));
	}
	
	/**
	 *  校验是否可以聊天
	 * @return
	 */
	public void checkChatGroup() {
		log.info("进入检测可用账号是否进入群聊. . . . . .");
	    // 1、校验是否chat_group表里是否有默认群，没有则将之添加进去
		ChatGroup cg=ChatGroup.dao.findFirst("select top 1 * from chat_group where groupname = '默认群'");
		if(cg==null){
			cg=new ChatGroup();
			cg.set("groupname", "默认群").set("avatar", "themes/head.ico").save();
		}
	    // 2、获取users表里 用户处于激活状态的，
	    // 未过期的用户是否进群（在chat_group_user表里是否进行数据绑定）
 	    Db.find("select users.id,users.name,"+cg.getId()+" as group_id from users left join chat_group_user on users.id=chat_group_user.userid and chat_group_user.groupid=? "
 	    		+ "where users.active=1 and chat_group_user.id is null and (expiredate is null or expiredate>?)",cg.getId(),LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)).forEach(x->{
 	    	log.info("Add user :"+x.getStr("name")+" to chat group "+x.get("group_id")+".");
 	    	new ChatGroupUser().set("userid", x.get("id")).set("groupid", x.get("group_id")).save();		
 	    });
	} 
	
	/**
	 *  校验数据库是否有检查方法模板的根节点（与设备相关联）
	 */
	public void checkStudyMethodNode() {
	    log.info("进入检测是否有未添加检查方法模板的设备类型. . . . . .");
	    List<Record> records = Db.find("select syscode.id,syscode.code from syscode LEFT JOIN study_method_template on syscode.code = study_method_template.modalitytype "
	          + " and study_method_template.parentid = '0' where syscode.type = '0004' and study_method_template.id is null and syscode.code is not null");
	    if (CollectionUtils.isNotEmpty(records) && records.size() > 0) {
            log.info("有" + records.size() + "个未添加检查方法的根节点");
            for (int i = 0; i < records.size(); i++) {
                Record record = records.get(i);
                StudyMethodTemplate node = new StudyMethodTemplate();
                node.setParentid(0);
                node.setModalitytype(record.get("code"));
                node.setNodename(record.get("code"));
                node.setType(0);
                node.setCreatetime(new Date());
                node.setCreator("系统自动创建");
                node.remove("id").save();
            }
        }
	    log.info("检测完成. . . . . .");
	}
	/**
	 *  校验此设备类型的表是否存在
	 */
	public void checkModalityTypeTable() {
		log.info("进入校验此设备类型的表是否存在...........");
		List<Syscode> list = Syscode.dao.find("select * from syscode where type = '0004'");
		for (Syscode syscode:list) {
			if (StringUtils.isNotBlank(syscode.getCode())) {
				String tablename = "id_" + syscode.getCode();
				try {
					Record r = Db.queryFirst("SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[" + tablename + "]') AND type IN ('U')");
					if (r == null) {
						log.info("数据库表" + tablename + "不存在，将创建。");
						// 创建这张表
						Db.query("CREATE TABLE [dbo].[" + tablename + "] ([id] int  IDENTITY(1,1) NOT NULL, [a] nvarchar(255) COLLATE Chinese_PRC_CI_AS NULL)");
					}
				} catch (Exception e) {
					log.info(e.getMessage());
				}
			}
		}
		log.info("检测完成. . . . . .");
	}
}
