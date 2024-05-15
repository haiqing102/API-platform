package vin.suki.apiinterface.controller;

import cn.hutool.json.JSONUtil;
import com.google.gson.Gson;
import vin.suki.apisdk.model.params.IpInfoParams;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import vin.suki.apicommon.common.exception.BusinessException;
import vin.suki.apicommon.model.dto.interfaceinfo.MusicInfo;
import vin.suki.apiinterface.util.RequestUtil;
import vin.suki.apiinterface.util.ResponseUtil;
import vin.suki.apisdk.model.params.HoroscopeParams;
import vin.suki.apisdk.model.params.RandomWallpaperParams;
import vin.suki.apisdk.model.params.WeatherParams;
import vin.suki.apisdk.model.response.RandomWallpaperResponse;
import vin.suki.apisdk.model.response.ResultResponse;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

@Slf4j
@RestController
public class InterfaceController {

	/**
	 * 土味情话
	 */
	@GetMapping("/loveTalk")
	public String randomLoveTalk() {
		return RequestUtil.get("https://api.vvhan.com/api/text/love");
	}

	/**
	 * 幽默段子
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
		String url = RequestUtil.buildUrl(baseUrl, randomWallpaperParams);
		if (StringUtils.isAllBlank(randomWallpaperParams.getLx(), randomWallpaperParams.getMethod())) {
			url = url + "?format=json&lx=fengjing";
		} else if (StringUtils.isBlank(randomWallpaperParams.getLx())) {
			url = url + "&format=json&lx=fengjing";
		} else {
			url = url + "&format=json";
		}
		return JSONUtil.toBean(RequestUtil.get(url), RandomWallpaperResponse.class);
	}

	/**
	 * 星座运势
	 */
	@GetMapping("/horoscope")
	public ResultResponse getHoroscope(HoroscopeParams horoscopeParams) throws BusinessException {
		String response = RequestUtil.get("https://api.vvhan.com/api/horoscope", horoscopeParams);
		Map<String, Object> fromResponse = ResponseUtil.responseToMap(response);
		boolean success = (boolean) fromResponse.get("success");
		if (!success) {
			ResultResponse baseResponse = new ResultResponse();
			baseResponse.setData(fromResponse);
			return baseResponse;
		}
		return JSONUtil.toBean(response, ResultResponse.class);
	}

	/*
	 *IP归属地
	 */
	@GetMapping("/ipInfo")
	public ResultResponse getIpInfo(IpInfoParams ipInfoParams) {
		return ResponseUtil.baseResponse("https://api.vvhan.com/api/ipInfo", ipInfoParams);
	}

	/**
	 * 天气查询
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
	 * 网易云Hot
	 */
	@GetMapping("/wyHotMusic")
	public ResultResponse getHotMusic() throws IOException {
		// 测试音乐是否可以播放
		String location;
		ResultResponse response;
		do {
			response = ResponseUtil.baseResponse("https://api.vvhan.com/api/wyMusic/热歌榜?type=json", null);
			String musicInfoJson = new Gson().toJson(response.getData().get("info"));
			MusicInfo musicInfo = new Gson().fromJson(musicInfoJson, MusicInfo.class);
			URL url = new URL(musicInfo.getUrl());
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			location = con.getHeaderField("Location");
		} while (location.equals("http://music.163.com/404"));

		return response;
	}

	/**
	 * 动漫头像
	 */
	@GetMapping("/cartoonAvatar")
	public ResultResponse getCartoonAvatar() {
		return ResponseUtil.baseResponse("https://api.vvhan.com/api/avatar/dm?type=json", null);
	}

	/**
	 * 职场人日历
	 */
	@GetMapping("/calendar")
	public ResultResponse getCalendar() {
		return ResponseUtil.baseResponse("https://api.vvhan.com/api/zhichang?type=json", null);
	}

	/**
	 * 手机号归属地
	 */
	@GetMapping("/phoneInfo")
	public ResultResponse getPhoneInfo(String phone) {
		return ResponseUtil.baseResponse("https://api.vvhan.com/api/phone/" + phone, null);
	}

}
