package com.flower.spirit.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.flower.spirit.config.Global;

@Controller
@RequestMapping
public class FrontController {
	
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
