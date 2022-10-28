package com.healta.controller;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.healta.model.UserProfiles;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.json.JSONObject;

import com.healta.license.VerifyLicense;
import com.healta.model.Filter;
import com.healta.model.User;
import com.healta.service.DistributionService;
import com.healta.service.FrontCommonService;
import com.healta.service.WorkListService;
import com.healta.util.ExcelUtil;
import com.healta.util.ParaKit;
import com.healta.util.ResultUtil;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.i18n.Res;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

public class DistributionController extends Controller{
	private final static Logger log = Logger.getLogger(WorkListController.class);

	private static DistributionService sv = new DistributionService();
	private static FrontCommonService frontCommonsv = new FrontCommonService();
	
	public void index(){
		User user = (User) getSession().getAttribute("user");
		setAttr("up", UserProfiles.dao.findFirst("select * from userprofiles where userid = ?", user.getId()));
		renderJsp("/view/front/distribution/distribution.jsp");
	}
	public void west_search() {
		renderJsp("/view/front/distribution/west_search.jsp");
	}
	
	public void centerSearch() {
		setAttr("quicksearch",VerifyLicense.hasFunction("quicksearch"));
		renderJsp("/view/front/distribution/center_search.jsp");
	}
	
	public void getDataAll() {
		User user = (User) getSession().getAttribute("user");
		try {
			log.info("Quick Search Content:" + getPara("quicksearchcontent")+";Quick Search Name:"+getPara("quicksearchname"));
			Page<Record> page = sv.findStudyInfo(getParaMap(),null,getSessionAttr("syscode_lan"));
			Map<String, Object> jsonMap = new HashMap<String, Object>();//定义map  
			//total键 存放总记录数，必须的  
			jsonMap.put("total", page.getTotalRow());
            jsonMap.put("rows", page.getList());//rows键 存放每页记录 list  
			renderJson("data", jsonMap);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	
	
	/**
	 * 将查询出来的已审核报告，导出excel文件
	 */
	public void exportExcel() {
        HttpServletResponse response = getResponse();
        Page<Record> page = sv.findStudyInfo(getParaMap(),null,getSessionAttr("syscode_lan"));
        // excel的标题
        String titles = get("titles");
        String[] title = titles.split(",");
        // excel对应的列
        String fields = get("fields");
        String[] field = fields.split(",");
        // 下载后文件的名称
        String fileName = "查询报告记录表.xls";
        //sheet名
        String sheetName = "查询报告";
        // 建一个二维数组，前面放行 （每行数据），后面放列 （每列标题）
        log.info("报告数据量为：" + page.getTotalRow());
        String[][] content = sv.excelContent(page.getList(), title, field);
        // 建HSSFWorkbook
        HSSFWorkbook wb = ExcelUtil.getHSSFWorkbook(sheetName, title, content, get("widths").split(","));
        // 响应到客户端
        try {
            sv.setResponseHeader(response, fileName);
            OutputStream os = response.getOutputStream();
            wb.write(os);
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            renderNull();
        }
    }
    
	/**
	 * 加载列表管理界面
	 */
	public void openFilterManageDialog() {
		renderJsp("/view/front/distribution/filterManage.jsp");
	}
    
	/**
	 * 保存查询条件界面
	 */
	public void openFilterSaveDialog() {
		renderJsp("/view/front/distribution/filter.jsp");
	}
	/**
	  *  校验 我的条件 保存的命名是否已经存在
	 */
	public void checkMyConditionName() {
		User user = (User) getSession().getAttribute("user");
	    log.info("我的条件保存的模块为：" + get("filterType") + ",命名为：" + get("name"));
	    Boolean res = frontCommonsv.checkMyConditionName(user.getId(), get("filterType"), get("name"));
	    log.info("此名称是否已经存在：" + res);
	    renderJson(ResultUtil.success(res));
	}
	/**
	 * 保存查询条件
	 */
	public void saveFilter() {
		try {
			Filter record = getModel(Filter.class,"",true);
			User user = (User) getSession().getAttribute("user");
			if (frontCommonsv.saveFilter(record,user,getParaMap())) {
				renderJson(ResultUtil.success());
			} else {
				renderJson(ResultUtil.fail(-1, "保存失败"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(-1, e.getMessage()));
		}
	}
	
	/**
	 * 获取查询条件
	 */
	public void getFilters() {
		try {
			User user = (User) getSession().getAttribute("user");
			renderJson(sv.findFilters(getParaToInt("_id"),user.getId(),getPara("filterType"),getSessionAttr("language")));
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(1, e.getMessage()));
		}
	}
	/**
	 * 删除保存的查询条件
	 */
	public void deleteFilter() {
		try {
			if (frontCommonsv.deleteFilters(getPara("id"))) {
				renderJson(ResultUtil.success());
			} else {
				renderJson(ResultUtil.fail("删除失败！"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResultUtil.fail(e.getMessage()));
		}
	}

}
