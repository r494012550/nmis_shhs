<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<title>Insert title here</title>
</head>
<body>
<div class="easyui-tabs" id="tabs_div_reg" data-options="plain:true,narrow:true,
		tabHeight:30,fit:true,border:false,tabWidth:140,
		tabPosition:'top',onSelect:selectimport">
	<%-- <div title="提取病人信息" data-options="href:'${ctx}/register/importPatient',onLoad:initimport,border:false">
	</div> --%>
	<div title="登记录入" id="register" data-options="selected:true,border:false">
		<jsp:include page="/view/front/register/registerContent.jsp" />	
	</div>

	<div title="已登记" data-options="href:'${ctx}/register/registerSearch',onLoad:initSearch,border:false">
	</div>
	
	<div title="待匹配" data-options="href:'${ctx}/register/unmatchstudy_view',onLoad:initUnmatchstudy,border:false">
	</div>
		
</div>
	

<script type="text/javascript">
/* $.extend($.fn.tree.methods, {
	highlight: function(jq, target){
		return jq.each(function(){
			$(this).find('.tree-node-hover').removeClass('tree-node-hover');
	        $(target).addClass('tree-node-hover');
		});
    },
    nav: function(jq){
        return jq.each(function(){
           var t = $(this);
			t.attr('tabindex',1);
            t.unbind('.tree').bind('keydown.tree', function(e){
              var curr = getCurr();
              if (!curr.length){return}
              if (e.keyCode == 40){    // down
                  var li = getNext(curr);
                  if (li.length){
                      t.tree('highlight', li[0]);                        
                  }
              } else if (e.keyCode == 38){    // up
                   var li = getPrev(curr);
                   if (li.length){
                       t.tree('highlight', li[0]);                        
                   }
               } else if (e.keyCode == 13){
                   t.tree('select', curr[0]);
               } else if (e.keyCode == 39){    // right
                   if (!t.tree('isLeaf', curr[0])){
                       t.tree('expand', curr[0]);
                   }
              } else if (e.keyCode == 37){    // left
                   if (!t.tree('isLeaf', curr[0])){
                       t.tree('collapse', curr[0]);
                  }
               }
               e.preventDefault();
           }).bind('mouseover.tree', function(e){
              var node = $(e.target).closest('div.tree-node');
               if (node.length){
                  t.find('.tree-node-hover').each(function(){
						if (this != node[0]){
							$(this).removeClass('tree-node-hover');
                       }
                   })
               }
           }); 
           function getCurr(){
               var n = t.find('.tree-node-hover');
               if (!n.length){
                   n = t.find('.tree-node-selected');
               }
               return n;
           }
           function getNext(curr){
               var n = $();
               var node = t.tree('getNode', curr[0]);
               if (t.tree('isLeaf', node.target)){
                   n = curr.parent().next().children('div.tree-node');
                   if (!n.length){
                       var p = t.tree('getParent', curr[0]);
                       if (p){
                           n = $(p.target).parent().next().children('div.tree-node');
                       }
                   }
               } else {
                   if (node.state == 'closed'){
                      n = curr.parent().next().children('div.tree-node');
                   } else {
                       var cc = t.tree('getChildren', curr[0]);
                      if (cc.length){
                           n = $(cc[0].target);
                       }
                   }

               }
               return n;
           }
           function getPrev(curr){
               var n = curr.parent().prev().children('div.tree-node');
               if (n.length){
                   var node = t.tree('getNode', n[0]);
                   if (node.state == 'open'){
                       var cc = t.tree('getChildren', node.target);
                       if (cc.length){
                          n = $(cc[cc.length-1].target);
                       }
                   }
               } else {
                   var p = t.tree('getParent', curr[0]);
                   if (p){
                       n = $(p.target);
                   }
               }
              return n;
           }
       });
   }
}); */
/* $('#itemtree_reg').tree('nav'); */

</script>
</body>
</html>