package com.qiguliuxing.dts.admin.web;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.qiguliuxing.dts.admin.annotation.RequiresPermissionsDesc;
import com.qiguliuxing.dts.admin.service.AdminBrandService;
import com.qiguliuxing.dts.admin.util.DtsBrandVo;
import com.qiguliuxing.dts.core.qcode.QCodeService;
import com.qiguliuxing.dts.core.util.ResponseUtil;
import com.qiguliuxing.dts.core.validator.Order;
import com.qiguliuxing.dts.core.validator.Sort;
import com.qiguliuxing.dts.db.domain.DtsBrand;
import com.qiguliuxing.dts.db.domain.DtsCategory;
import com.qiguliuxing.dts.db.service.DtsBrandService;
import com.qiguliuxing.dts.db.service.DtsCategoryService;

@RestController
@RequestMapping("/admin/brand")
@Validated
public class AdminBrandController {
	private static final Logger logger = LoggerFactory.getLogger(AdminBrandController.class);

	@Autowired
	private DtsBrandService brandService;
	
	@Autowired
	private DtsCategoryService categoryService;
	
	@Autowired
	private AdminBrandService adminBrandService;
	
	@Autowired
	private QCodeService qCodeService;

	@RequiresPermissions("admin:brand:list")
	@RequiresPermissionsDesc(menu = { "商场管理", "品牌管理" }, button = "查询")
	@GetMapping("/list")
	public Object list(String id, String name, @RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "10") Integer limit,
			@Sort @RequestParam(defaultValue = "add_time") String sort,
			@Order @RequestParam(defaultValue = "desc") String order) {
		logger.info("【请求开始】商场管理->品牌管理->查询,请求参数:name:{},page:{}", name, page);

		List<DtsBrand> brandList = brandService.querySelective(id, name, page, limit, sort, order);
		long total = PageInfo.of(brandList).getTotal();
		Map<String, Object> data = new HashMap<>();
		
		List<DtsBrandVo> brandVoList = new ArrayList<DtsBrandVo> (brandList == null ? 0 : brandList.size());
		for(DtsBrand brand : brandList) {
			DtsBrandVo brandVo = new DtsBrandVo();
			BeanUtils.copyProperties(brand, brandVo);//对象属性的复制
			// 用于展示商品归属的类目（页面级联下拉控件数据展示）
			Integer categoryId = brand.getDefaultCategoryId();
			DtsCategory category = categoryService.findById(categoryId);
			Integer[] categoryIds = new Integer[] {};
			if (category != null) {
				Integer parentCategoryId = category.getPid();
				categoryIds = new Integer[] { parentCategoryId, categoryId };
				brandVo.setCategoryIds(categoryIds);
			}
			brandVoList.add(brandVo);
		}
		
		data.put("total", total);
		data.put("items", brandVoList);

		logger.info("【请求结束】商场管理->品牌管理->查询:total:{}", total);
		return ResponseUtil.ok(data);
	}

	private Object validate(DtsBrand brand) {
		String name = brand.getName();
		if (StringUtils.isEmpty(name)) {
			return ResponseUtil.badArgument();
		}

		String desc = brand.getDesc();
		if (StringUtils.isEmpty(desc)) {
			return ResponseUtil.badArgument();
		}

		BigDecimal price = brand.getFloorPrice();
		if (price == null) {
			return ResponseUtil.badArgument();
		}
		return null;
	}

	@RequiresPermissions("admin:brand:create")
	@RequiresPermissionsDesc(menu = { "商场管理", "品牌管理" }, button = "添加")
	@PostMapping("/create")
	public Object create(@RequestBody DtsBrand brand) {
		logger.info("【请求开始】商场管理->品牌管理->添加,请求参数:{}", JSONObject.toJSONString(brand));
		Object error = validate(brand);
		if (error != null) {
			return error;
		}
		
		try {
			//生成店铺的分享URL
			String defaultCategory = brandService.getBrandCategory(brand.getDefaultCategoryId());
			String shareUrl = qCodeService.createBrandImage(brand.getId(), brand.getPicUrl(), brand.getName(),defaultCategory);
			brand.setShareUrl(shareUrl);
		} catch (Exception e) {
			logger.error("生成品牌商铺分享图URL出错：{}",e.getMessage());
			e.printStackTrace();
		}
		
		brandService.add(brand);
		logger.info("【请求结束】商场管理->品牌管理->添加:响应结果:{}", JSONObject.toJSONString(brand));
		return ResponseUtil.ok(brand);
	}

	@RequiresPermissions("admin:brand:read")
	@RequiresPermissionsDesc(menu = { "商场管理", "品牌管理" }, button = "详情")
	@GetMapping("/read")
	public Object read(@NotNull Integer id) {
		logger.info("【请求开始】商场管理->品牌管理->详情,请求参数, id:{}", id);

		DtsBrand brand = brandService.findById(id);

		logger.info("【请求结束】商场管理->品牌管理->详情:响应结果:{}", JSONObject.toJSONString(brand));
		return ResponseUtil.ok(brand);
	}

	@RequiresPermissions("admin:brand:update")
	@RequiresPermissionsDesc(menu = { "商场管理", "品牌管理" }, button = "编辑")
	@PostMapping("/update")
	public Object update(@RequestBody DtsBrand brand) {
		logger.info("【请求开始】商场管理->品牌管理->编辑,请求参数, id:{}", JSONObject.toJSONString(brand));

		Object error = validate(brand);
		if (error != null) {
			return error;
		}
		try {
			//生成店铺的分享URL
			String defaultCategory = brandService.getBrandCategory(brand.getDefaultCategoryId());
			String shareUrl = qCodeService.createBrandImage(brand.getId(), brand.getPicUrl(), brand.getName(),defaultCategory);
			brand.setShareUrl(shareUrl);
		} catch (Exception e) {
			logger.error("生成品牌商铺分享图URL出错：{}",e.getMessage());
			e.printStackTrace();
		}
		if (brandService.updateById(brand) == 0) {
			logger.error("商场管理->品牌管理->编辑 失败，更新数据失败！");
			return ResponseUtil.updatedDataFailed();
		}

		logger.info("【请求结束】商场管理->品牌管理->编辑:响应结果:{}", JSONObject.toJSONString(brand));
		return ResponseUtil.ok(brand);
	}

	@RequiresPermissions("admin:brand:delete")
	@RequiresPermissionsDesc(menu = { "商场管理", "品牌管理" }, button = "删除")
	@PostMapping("/delete")
	public Object delete(@RequestBody DtsBrand brand) {
		logger.info("【请求开始】商场管理->品牌管理->删除,请求参数:{}", JSONObject.toJSONString(brand));

		Integer id = brand.getId();
		if (id == null) {
			return ResponseUtil.badArgument();
		}
		brandService.deleteById(id);

		logger.info("【请求结束】商场管理->品牌管理->删除,响应结果:{}", "成功！");
		return ResponseUtil.ok();
	}
	
	
	@GetMapping("/catAndAdmin")
	public Object catAndAdmin() {
		logger.info("【请求开始】商场管理->品牌管理->获取目录与管理用户");
		return adminBrandService.catAndAdmin();
	}

}
