<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="shiro"  uri="http://shiro.apache.org/tags"%>
<%@page import="com.healta.constant.Version"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="vs" value="${Version.VERSION}"/>