package edu.cqupt.apiinterface.controller;

import cn.hutool.json.JSONUtil;
import edu.cqupt.apicommon.common.exception.BusinessException;
import edu.cqupt.apicommon.model.dto.interfaceinfo.MusicDTO;
import edu.cqupt.apiinterface.util.ResponseUtil;
import edu.cqupt.apisdk.model.params.HoroscopeParams;
import edu.cqupt.apisdk.model.params.IpInfoParams;
import edu.cqupt.apisdk.model.params.RandomWallpaperParams;
import edu.cqupt.apisdk.model.params.WeatherParams;
import edu.cqupt.apisdk.model.response.RandomWallpaperResponse;
import edu.cqupt.apisdk.model.response.ResultResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import static edu.cqupt.apiinterface.util.RequestUtil.buildUrl;
import static edu.cqupt.apiinterface.util.RequestUtil.get;
import static edu.cqupt.apiinterface.util.ResponseUtil.responseToMap;

@Slf4j
//@RestController
public class InterfaceControllerDev {

	/*
	 *幽默段子
	 */
	@GetMapping("/poisonousChickenSoup")
	public String getPoisonousChickenSoup() {
		return get("https://api.btstu.cn/yan/api.php?charset=utf-8&encode=json");
	}

	/*
	 *随机壁纸
	 */
	@GetMapping("/randomWallpaper")
	public RandomWallpaperResponse randomWallpaper(RandomWallpaperParams randomWallpaperParams) throws BusinessException {
		String baseUrl = "https://api.btstu.cn/sjbz/api.php";
		String url = buildUrl(baseUrl, randomWallpaperParams);
		if (StringUtils.isAllBlank(randomWallpaperParams.getLx(), randomWallpaperParams.getMethod())) {
			url = url + "?format=json&lx=fengjing";
		} else if (StringUtils.isBlank(randomWallpaperParams.getLx())) {
			url = url + "&format=json&lx=fengjing";
		} else {
			url = url + "&format=json";
		}
		return JSONUtil.toBean(get(url), RandomWallpaperResponse.class);
	}

	/*
	 *土味情话
	 */
	@GetMapping("/loveTalk")

	public String randomLoveTalk() {
		return get("https://39.107.79.226/api/interface/loveTalk");
	}

	/*
	 *星座运势
	 */
	@GetMapping("/horoscope")

	public ResultResponse getHoroscope(HoroscopeParams horoscopeParams) {
		String response = get("https://39.107.79.226/api/interface/horoscope", horoscopeParams);
		Map<String, Object> fromResponse = responseToMap(response);
		boolean success = (boolean) fromResponse.get("success");
		if (!success) {
			ResultResponse baseResponse = new ResultResponse();
			baseResponse.setData(fromResponse);
			return baseResponse;
		}
		return JSONUtil.toBean(response, ResultResponse.class);
	}

	/*
	 *
	 *IP归属地
	 */
	@GetMapping("/ipInfo")

	public ResultResponse getIpInfo(IpInfoParams ipInfoParams) {
		return ResponseUtil.baseResponse("https://39.107.79.226/api/interface/ipInfo", ipInfoParams);
	}

	/*
	 *
	 *天气查询
	 */
	@GetMapping("/weather")

	public ResultResponse getWeatherInfo(WeatherParams weatherParams) {
		return ResponseUtil.baseResponse("https://39.107.79.226/api/interface/weather", weatherParams);
	}

	/*
	 *
	 *网易云Hot
	 */
	@GetMapping("/wyHotMusic")
	public String getHotMusic() throws IOException {
		// 测试音乐是否可以播放
		String musicJson, location;
		do {
			musicJson = get("https://39.107.79.226/api/interface/wyHotMusic");
			MusicDTO musicDTO = JSONUtil.toBean(musicJson, MusicDTO.class);
			URL url = new URL(musicDTO.getInfo().getUrl());
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			location = con.getHeaderField("Location");
			log.info("location={}", location);
		} while (location.equals("http://music.163.com/404"));

		return musicJson;
	}

	/*
	 *
	 *动漫头像
	 */
	@GetMapping("/cartoonAvatar")
	public String getCartoonAvatar() {
		return get("https://39.107.79.226/api/interface/cartoonAvatar");
	}

	/**
	 * 职场人日历
	 */
	@GetMapping("/calendar")
	public String getCalendar() {
		return get("https://39.107.79.226/api/interface/calendar");
	}

	/**
	 * 手机号归属地
	 */
	@GetMapping("/phoneInfo")
	public String getPhoneInfo(String phone) {
		return get("https://39.107.79.226/api/interface/phoneInfo?phone=" + phone);
	}

}
