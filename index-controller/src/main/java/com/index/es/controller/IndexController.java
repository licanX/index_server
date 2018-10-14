package com.index.es.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.index.es.schedule.IndexSchedule;

/**
 * Http 调试入口
 * 
 * @author lic
 * @date 2018年8月26日
 * @since v1.0.0
 */
@RestController
@RequestMapping(value = "/index")
public class IndexController {

	@Autowired
	private IndexSchedule index;

	@RequestMapping(value = "/daily", method = RequestMethod.GET)
	@ResponseBody
	public Object daily() {
		index.daily();
		return "hello index daily";
	}
}
