package com.qiguliuxing.dts.db.service;

import java.time.LocalDateTime;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.github.pagehelper.PageHelper;
import com.qiguliuxing.dts.db.dao.DtsBrandMapper;
import com.qiguliuxing.dts.db.dao.DtsCategoryMapper;
import com.qiguliuxing.dts.db.domain.DtsBrand;
import com.qiguliuxing.dts.db.domain.DtsBrand.Column;
import com.qiguliuxing.dts.db.domain.DtsBrandExample;
import com.qiguliuxing.dts.db.domain.DtsCategory;

@Service
public class DtsBrandService {
	@Resource
	private DtsCategoryMapper categoryMapper;
	
	@Resource
	private DtsBrandMapper brandMapper;
	private Column[] columns = new Column[] { Column.id, Column.name, Column.desc, Column.picUrl, Column.floorPrice };

	public List<DtsBrand> queryVO(int offset, int limit) {
		DtsBrandExample example = new DtsBrandExample();
		example.or().andDeletedEqualTo(false);
		example.setOrderByClause("add_time desc");
		PageHelper.startPage(offset, limit);
		return brandMapper.selectByExampleSelective(example, columns);
	}

	public int queryTotalCount() {
		DtsBrandExample example = new DtsBrandExample();
		example.or().andDeletedEqualTo(false);
		return (int) brandMapper.countByExample(example);
	}

	public DtsBrand findById(Integer id) {
		return brandMapper.selectByPrimaryKey(id);
	}

	public List<DtsBrand> querySelective(String id, String name, Integer page, Integer size, String sort,
			String order) {
		DtsBrandExample example = new DtsBrandExample();
		DtsBrandExample.Criteria criteria = example.createCriteria();

		if (!StringUtils.isEmpty(id)) {
			criteria.andIdEqualTo(Integer.valueOf(id));
		}
		if (!StringUtils.isEmpty(name)) {
			criteria.andNameLike("%" + name + "%");
		}
		criteria.andDeletedEqualTo(false);

		if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
			example.setOrderByClause(sort + " " + order);
		}

		PageHelper.startPage(page, size);
		return brandMapper.selectByExample(example);
	}

	public int updateById(DtsBrand brand) {
		brand.setUpdateTime(LocalDateTime.now());
		return brandMapper.updateByPrimaryKeySelective(brand);
	}

	public void deleteById(Integer id) {
		brandMapper.logicalDeleteByPrimaryKey(id);
	}

	public void add(DtsBrand brand) {
		brand.setAddTime(LocalDateTime.now());
		brand.setUpdateTime(LocalDateTime.now());
		brandMapper.insertSelective(brand);
	}

	public List<DtsBrand> all() {
		DtsBrandExample example = new DtsBrandExample();
		example.or().andDeletedEqualTo(false);
		return brandMapper.selectByExample(example);
	}

	/**
	 * 根据分类id获取分类名
	 * @param categoryId
	 * @return
	 */
	public String getBrandCategory(Integer categoryId) {
		DtsCategory dtsCategory = categoryMapper.selectByPrimaryKey(categoryId);
		return dtsCategory == null ? "综合" : dtsCategory.getName();
	}
}
