package edu.cqupt.apiinterface.controller;

import cn.hutool.json.JSONUtil;
import edu.cqupt.apicommon.common.exception.BusinessException;
import edu.cqupt.apicommon.model.dto.interfaceinfo.MusicDTO;
import edu.cqupt.apiinterface.util.RequestUtil;
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
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import static edu.cqupt.apiinterface.util.RequestUtil.buildUrl;
import static edu.cqupt.apiinterface.util.RequestUtil.get;
import static edu.cqupt.apiinterface.util.ResponseUtil.responseToMap;

@Slf4j
@RestController
public class InterfaceController {

	/**
	 * 随机土味情话
	 */
	@GetMapping("/loveTalk")
	public String randomLoveTalk() {
		return RequestUtil.get("https://api.vvhan.com/api/text/love");
	}

	/**
	 * 随机毒鸡汤
	 */
	@GetMapping("/poisonousChickenSoup")
	public String getPoisonousChickenSoup() {
		return RequestUtil.get("https://api.btstu.cn/yan/api.php?charset=utf-8&encode=json");
	}

	/**
	 * 随机壁纸
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

	/**
	 * 每日星座运势
	 */
	@GetMapping("/horoscope")
	public ResultResponse getHoroscope(HoroscopeParams horoscopeParams) throws BusinessException {
		String response = get("https://api.vvhan.com/api/horoscope", horoscopeParams);
		Map<String, Object> fromResponse = responseToMap(response);
		boolean success = (boolean) fromResponse.get("success");
		if (!success) {
			ResultResponse baseResponse = new ResultResponse();
			baseResponse.setData(fromResponse);
			return baseResponse;
		}
		return JSONUtil.toBean(response, ResultResponse.class);
	}

	/**
	 * 获取IP信息归属地
	 */
	@GetMapping("/ipInfo")
	public ResultResponse getIpInfo(IpInfoParams ipInfoParams) {
		return ResponseUtil.baseResponse("https://api.vvhan.com/api/ipInfo", ipInfoParams);
	}

	/**
	 * 获取天气信息
	 */
	@GetMapping("/weather")
	public ResultResponse getWeatherInfo(WeatherParams weatherParams) {
		if (StringUtils.isAllBlank(weatherParams.getIp(), weatherParams.getCity(), weatherParams.getType())) {
			return ResponseUtil.baseResponse("https://api.vvhan.com/api/weather?ip=183.230.12.201", weatherParams);
		} else {
			return ResponseUtil.baseResponse("https://api.vvhan.com/api/weather", weatherParams);
		}
	}

	/**
	 * 网易云Hot榜歌曲
	 */
	@GetMapping("/wyHotMusic")
	public String getHotMusic() throws IOException {
		// 测试音乐是否可以播放
		String musicJson, location;
		do {
			musicJson = get("https://api.vvhan.com/api/wyMusic/热歌榜?type=json");
			MusicDTO musicDTO = JSONUtil.toBean(musicJson, MusicDTO.class);
			URL url = new URL(musicDTO.getInfo().getUrl());
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			location = con.getHeaderField("Location");
			log.info("location={}", location);
		} while (location.equals("http://music.163.com/404"));

		return musicJson;
	}

	/**
	 * 获取动漫头像
	 */
	@GetMapping("/cartoonAvatar")
	public String getCartoonAvatar() {
		return RequestUtil.get("https://api.vvhan.com/api/avatar/dm?type=json");
	}

	/**
	 * 职场人日历
	 */
	@GetMapping("/calendar")
	public String getCalendar() {
		return RequestUtil.get("https://api.vvhan.com/api/zhichang?type=json");
	}

	@GetMapping("/phoneInfo")
	public String getPhoneInfo(String phone) {
		return RequestUtil.get("https://api.vvhan.com/api/phone/" + phone);
	}

}
