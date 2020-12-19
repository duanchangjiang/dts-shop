package com.qiguliuxing.dts.admin.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qiguliuxing.dts.admin.util.CatVo;
import com.qiguliuxing.dts.core.util.ResponseUtil;
import com.qiguliuxing.dts.db.domain.DtsAdmin;
import com.qiguliuxing.dts.db.domain.DtsCategory;
import com.qiguliuxing.dts.db.service.DtsAdminService;
import com.qiguliuxing.dts.db.service.DtsCategoryService;

@Service
public class AdminBrandService {
	private static final Logger logger = LoggerFactory.getLogger(AdminBrandService.class);

	@Autowired
	private DtsCategoryService categoryService;
	
	@Autowired
	private DtsAdminService adminService;

	/**
	 * 获取分类和管理用户
	 * @return
	 */
	public Object catAndAdmin() {
		List<DtsCategory> l1CatList = categoryService.queryL1();
		List<CatVo> categoryList = new ArrayList<CatVo>(l1CatList == null ? 0 : l1CatList.size());

		for (DtsCategory l1 : l1CatList) {
			CatVo l1CatVo = new CatVo();
			l1CatVo.setValue(l1.getId());
			l1CatVo.setLabel(l1.getName());

			List<DtsCategory> l2CatList = categoryService.queryByPid(l1.getId());
			List<CatVo> children = new ArrayList<CatVo>(l2CatList == null ? 0 : l2CatList.size());
			for (DtsCategory l2 : l2CatList) {
				CatVo l2CatVo = new CatVo();
				l2CatVo.setValue(l2.getId());
				l2CatVo.setLabel(l2.getName());
				children.add(l2CatVo);
			}
			l1CatVo.setChildren(children);

			categoryList.add(l1CatVo);
		}
		
		//系统用户
		List<DtsAdmin> list = adminService.allAdmin();
		List<Map<String, Object>> adminList = new ArrayList<Map<String, Object>>(list == null ? 0 : list.size());
		for (DtsAdmin admin : list) {
			Map<String, Object> b = new HashMap<>(2);
			b.put("value", admin.getId());
			b.put("label", admin.getUsername());
			adminList.add(b);
		}

		Map<String, Object> data = new HashMap<>();
		data.put("categoryList", categoryList);
		data.put("adminList", adminList);
		logger.info("品牌入驻获取的总一级目录数：{},总会员数：{}",categoryList.size(),adminList.size());
		return ResponseUtil.ok(data);
	}

}
