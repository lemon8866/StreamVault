package com.flower.spirit.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.flower.spirit.config.Global;
import com.flower.spirit.service.ConfigService;

@Controller
@RequestMapping
public class FrontController {
	
	@Autowired
	private ConfigService configService;
	
	/**
	 * 引导页
	 * @return
	 */
	@RequestMapping(value = {"","/"})
	public String index(Model model) {
		if(Global.frontend.equals("blank")) {
			return "index";
		}
		if(Global.frontend.equals("video")) {
			// 传递隐藏平台配置到前端
			model.addAttribute("hiddenplatforms", Global.hiddenplatforms != null ? Global.hiddenplatforms : "");
			return "video";
		}
		if(Global.frontend.equals("admin")) {
			return "redirect:/admin/admin";
		}
		return "index";
	}
	

}
