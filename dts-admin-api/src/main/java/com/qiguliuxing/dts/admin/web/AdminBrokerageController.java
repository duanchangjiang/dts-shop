package com.qiguliuxing.dts.admin.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageInfo;
import com.qiguliuxing.dts.admin.annotation.RequiresPermissionsDesc;
import com.qiguliuxing.dts.core.util.ResponseUtil;
import com.qiguliuxing.dts.core.validator.Order;
import com.qiguliuxing.dts.core.validator.Sort;
import com.qiguliuxing.dts.db.domain.DtsAccountTrace;
import com.qiguliuxing.dts.db.service.DtsAccountService;

/**
 * 佣金业务接口
 * 
 * @since 1.0.0
 * @author CHENBO
 * @QQ 623659388
 */
@RestController
@RequestMapping("/admin/brokerage")
@Validated
public class AdminBrokerageController {
	private static final Logger logger = LoggerFactory.getLogger(AdminBrokerageController.class);

	@Autowired
	private DtsAccountService accountService;

	@RequiresPermissions("admin:brokerage:list")
	@RequiresPermissionsDesc(menu = { "用户管理", "佣金管理" }, button = "查询")
	@GetMapping("/list")
	public Object list(String username, String mobile,String type,@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "10") Integer limit,
			@Sort @RequestParam(defaultValue = "trace_time") String sort,
			@Order @RequestParam(defaultValue = "desc") String order) {
		logger.info("【请求开始】用户管理->佣金管理->查询,请求参数,username:{},mobile:{},type:{},page:{}", username, mobile, type,page);

		List<DtsAccountTrace> traceList = accountService.querySelective(username, mobile, type, page, limit, sort, order);
		long total = PageInfo.of(traceList).getTotal();
		Map<String, Object> data = new HashMap<>();
		data.put("total", total);
		data.put("traceList", traceList);

		logger.info("【请求结束】用户管理->佣金管理->查询:记录数:{}", traceList == null ? 0 : traceList.size());
		return ResponseUtil.ok(data);
	}

}
